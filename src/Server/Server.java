package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;

import Data.Time;
import Data.Lock;

/**
 * Class manages incoming requests from potential Clients. Listens to a port a then after received request delegates
 * service to ClientHandlerThread. Server works until it is shutdown, new object is not assigned to a reference after final calendar has been sent to clients.
 */
public class Server extends Thread{

    private ServerController serverController;

    private ServerSocket socketHearing = null;
    private Socket connectionSocket = null;
    private ArrayList<Time> finalCalendar = null;
    private final Lock CommonLock = new Lock(0);
    private final Lock ServerLock = new Lock(1);
    private double MeetingLength;
    private ArrayList<ClientHandler> allConnectedClients = null;
    private int numberOfClients = 0;
    private int connectedClients;

    //this function run everything
    /**Starts thread main thread of server
     */
    @Override
    public void run(){
        serverRunningForMultipleRequests();
    }

    /**
     * Creates server that listens to a port. Sets clientsController to new ServerController())
     * @param port port that is listened to by server
     */
    public Server(int port) {

        finalCalendar = new ArrayList<>();

        setServerController(new ServerController(this, new ServerGUI()));

        //actually connected  out of all that declared participation
        connectedClients = 0;
    }

    /**
     * Prepares server for a new session with new input parameters (number of clients, meeting length).
     */
    public void restart(){
        allConnectedClients = null;
        connectedClients = 0;
        connectionSocket = null;
        ClientHandler.finishedOperations = 0;
        finalCalendar.clear();

        serverController.restartServerView();
    }

    //server management

    /**
     * set server to Listen to port.
     * @param port port to listen to
     */
    private void setServerToListenToPort(int port){
        try {
            socketHearing = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     Server listen to port and after connection is established, it assigns recently acquired connection to new ClientHandlerThread and stores it.
     After the last expected client connects to server, server starts all ClientThreads one after another.
     */

    private void assignClientHandlersToClients(int minNumberOfCommonPartOperations){

        allConnectedClients = new ArrayList<>();

        //create Client Handler threads until all clients are connected
        while (connectedClients != numberOfClients) {
            System.out.println("Waiting  for Clients to connect. Currently connected : " + connectedClients + " out of " + numberOfClients);

            try {
                connectionSocket = socketHearing.accept();
                connectedClients++;
                System.out.println("Client connected to the server application :" + connectedClients + " out of " + numberOfClients);
                DataInputStream dis = new DataInputStream(connectionSocket.getInputStream());
                DataOutputStream dos = new DataOutputStream(connectionSocket.getOutputStream());


                serverController.modelRequestsRefreshingConnectedClientsList("Client nr " + connectedClients);

                allConnectedClients.add(new ClientHandler(
                        dis, dos, connectionSocket, finalCalendar, minNumberOfCommonPartOperations,
                        CommonLock, MeetingLength, serverController));


            } catch (IOException e) {
                try {
                    if (connectionSocket!= null)
                        connectionSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

    /**
     * Starts all ClientHandlerThreads.
     * @param CHandlerList list of handlers deployed by server
     */
    public void StartAllClientHandlers(ArrayList<ClientHandler> CHandlerList){

        for(ClientHandler cH : CHandlerList)
            cH.start();
    }

    public static int setPort(){
        Scanner sc = new Scanner(System.in);
        int portNumb;

        while(true) {
            System.out.println("Set port to listen to.");

            portNumb = sc.nextInt();

            if(portNumb < 65536 && portNumb > 1023)return portNumb;
            else System.out.println("Incorrect port number, should be from range (1024, 65535)");
        }
    }

    public static int setNumberOfClients(){
        int result;
        Scanner sc = new Scanner(System.in);
        while(true){


            System.out.println("Set number of expected clients in the meeting [1,9], {-1} stops the server from executing");
            result = sc.nextInt();

            if(result > 0 && result < 10){
                System.out.println("Correct number of clients");
                break;
            }
            else if(result >= 10) System.out.println("Too many clients");
            else if(result <= 0 && result != -1)System.out.println("Number of clients must be positive number");
            else break;
        }
        return result;

    }

    /**
     * Server thread stops until its is notified that request has been processed and Calendar has been sent to clients.
     */
    private void ServerWaitForClientHandlerThreads(){
        synchronized (CommonLock){
            try {
                CommonLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMeetingLength(Double period){
        this.MeetingLength = period;
    }

    public void setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    /**
     * "Work loop" for a server. Thanks to this function we do not have to create new Server object after every time session terminates.
     */
    public void serverRunningForMultipleRequests(){
        while (true){
            setServerToListenToPort(5055);
            serverIsActiveAndResponding();
            stopListeningToPort();

            synchronized (ServerLock){
                try {
                    ServerLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Closes serverSocket that listens to given port.
     */
    public void stopListeningToPort(){
        try {
            socketHearing.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function that provides entire lifecycle of started session. Is used as a help function for serverRunningMultipleRequests().
     */

    public void serverIsActiveAndResponding(){

        if (numberOfClients < 1) {
            System.out.println("Server terminated, wrong number of Clients");
            return;
        }

        assignClientHandlersToClients(numberOfClients);

        System.out.println("Starting all ClientHandler threads");

        //start all ClientHandler threads
        this.StartAllClientHandlers(allConnectedClients);

        ServerWaitForClientHandlerThreads();

        System.out.println("Calculation of the common calendar is completed");

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restart();
    }

    /**
     * Closes all resources of server. Called before the termination of serverThread.
     */
    public void terminateServer() {
        try {
            if (allConnectedClients!= null)
                 for (ClientHandler clients : allConnectedClients){
                    clients.endConnection();
                 }

            if (socketHearing != null)
                socketHearing.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Lock getServerLock() {
        return ServerLock;
    }

    public ServerController getServerController() {
        return serverController;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public ServerSocket getSocketHearing() {
        return socketHearing;
    }

    public void setSocketHearing(ServerSocket socketHearing) {
        this.socketHearing = socketHearing;
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    public void setConnectionSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public ArrayList<Time> getFinalCalendar() {
        return finalCalendar;
    }

    public void setFinalCalendar(ArrayList<Time> finalCalendar) {
        this.finalCalendar = finalCalendar;
    }

    public Lock getCommonLock() {
        return CommonLock;
    }

    public double getMeetingLength() {
        return MeetingLength;
    }

    public void setMeetingLength(double meetingLength) {
        MeetingLength = meetingLength;
    }

    public ArrayList<ClientHandler> getAllConnectedClients() {
        return allConnectedClients;
    }

    public int getNumberOfClients() {
        return numberOfClients;
    }

    public int getConnectedClients() {
        return connectedClients;
    }

    public void setConnectedClients(int connectedClients) {
        this.connectedClients = connectedClients;
    }

    public static void main(String[] args) {
        Server server = new Server(5055);
    }

}
