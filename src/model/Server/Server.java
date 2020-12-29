package model.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.*;

import controller.Server.ServerController;
import view.Server.ServerGUI;
import model.Data.Time;
import model.Data.Lock;


public class Server extends Thread{

    public ServerController serverController;


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

    @Override
    public void run(){
        serverIsActiveAndResponding();
    }

    public Server(int port) {

        finalCalendar = new ArrayList<>();

        setServerController(new ServerController(this, new ServerGUI()));

        //actually connected  out of all that declared participation
        connectedClients = 0;
        setServerToListenToPort(port);
    }


    //TODO : socket closing
    public void restart(){
        allConnectedClients = null;
        connectedClients = 0;
        connectionSocket = null;
        ClientHandler.finishedOperations = 0;
        finalCalendar.clear();

        serverController.restartServerView();
    }

    //server management
    public void setServerToListenToPort(int port){
        try {
            socketHearing = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void assignClientHandlersToClients(int connectedClients, int numberOfClients, int minNumberOfCommonPartOperations){

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
                    connectionSocket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                e.printStackTrace();
            }
        }
    }

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

    public void ServerWaitForClientHandlerThreads(){
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

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public void serverRunningForMultipleRequests(){
        while (true){
            serverIsActiveAndResponding();

            synchronized (ServerLock){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void serverIsActiveAndResponding(){

        if (numberOfClients < 1) {
            System.out.println("Server terminated, wrong number of Clients");
            return;
        }

        assignClientHandlersToClients(connectedClients, numberOfClients, numberOfClients);

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

    public static void main(String[] args) {
        Server server = new Server(5055);
    }

    //TODO socket closing
    public void cleanUpResources() {
    }
}
