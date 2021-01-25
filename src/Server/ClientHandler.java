package Server;
import java.io.*;
import java.net.*;

import Data.Lock;
import Data.Calendars;
import Data.Time;

import java.util.ArrayList;

/**
 *  ClientHandler is created and deployed by Server to manage and calculate Calendar that has been sent by Client application.
 *  ClientHandler threads wait for the last One of them to finish exectue as they have static variable finishedOperations that counts
 *  how many of the pool has already finished and created their partial result calendar in finalCalendar.
 */

public class ClientHandler extends Thread {



    static int finishedOperations = 0;

    private DataInputStream dataInputS;
    private DataOutputStream dataOutputS;
    private Socket connectionSocket;
    private ArrayList<Time> finalCalendar;
    private ArrayList<Time> clientsCalendar;
    private int minNumbOfCommonPartOperations;
    private final Lock commonLock;
    private double MeetingLength;

    private ServerController serverController;

    /**
     * Starts threads execution, main function that gathers all tasks that must be performed to calculate partial final calender
     */
    @Override    //this function run everything
    public void run() {

        sleepFor2000ms();

        synchronized (commonLock) {

            this.getThisClientsCalendar();

            //final calendar reference is being updated according to number of finished operations of getting common part
            updateCommonResource();

            CommonPartOperationFinished();

            if (finishedOperations == minNumbOfCommonPartOperations)
                commonLock.notifyAll();
            else {
                try {
                    commonLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        ArrayList<String> finalCalendarString = Calendars.changeFormatToString(finalCalendar);

        //sending calculated common calendar to all  participants of meeting
        sendResultToClient(finalCalendarString);

        endConnection();
    }

    public ClientHandler(DataInputStream dis, DataOutputStream dos, Socket connectionSocketPara, ArrayList<Time> finalCalendarPara,
                         int minNumbOfCommonPartOperations, Lock cLock, double MeetingLength, ServerController controller) {

        dataInputS = dis;
        dataOutputS = dos;
        connectionSocket = connectionSocketPara;
        finalCalendar = finalCalendarPara;
        this.minNumbOfCommonPartOperations = minNumbOfCommonPartOperations;
        commonLock = cLock;
        this.MeetingLength = MeetingLength;
        this.serverController = controller;
    }

    /**
     * Increments finishedOperations, called when thread ends processing final calender
     */
    public static void CommonPartOperationFinished() {
        finishedOperations++;
    }

    /**
     * Function gets clients calender via Socket that has been earlier assigned by a Server application.
     * Time objects implement Serialized so its easy to receive and send them through network.
     */
    public void getThisClientsCalendar() {

        clientsCalendar = new ArrayList<>();

        try {

            int sizeOfCalendar = dataInputS.readInt();

            ObjectInputStream serializedIn = new ObjectInputStream(connectionSocket.getInputStream());

            for (int i = 0; i < sizeOfCalendar; i++)
                clientsCalendar.add((Time) serializedIn.readObject());

            System.out.println("Calendar has been acquired!");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException c) {
            System.out.println("Class not found");
            c.printStackTrace();
        }
    }

    /**
     * After all ClientHandlers finished processing stage, each one of them sends finalCalendar to Client via Socket.
     * @param finalCalendar Calendar that is supposed to be sent to particular Client via socket
     */
    public void sendResultToClient(ArrayList<String> finalCalendar) {

        //send size of transmitted array
        try {
            dataOutputS.writeInt(finalCalendar.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //sending array in String words
        for (String date : finalCalendar) {

            try {
                dataOutputS.writeUTF(date);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Updates finalCalendar accessed by all ClientHandlers, it works because of mathematical properties of common part operation.
     * Common part calendar is calculated and then assigned to final calendar.
     */
    public void updateCommonResource() {

        if (finishedOperations > 0)
            clientsCalendar = Calendars.solution(clientsCalendar, finalCalendar, MeetingLength);

        finalCalendar.clear();

        finalCalendar.addAll(clientsCalendar);

    }

    /**
     * Close socket connection between Client and ClientHandler
     */
    public void endConnection() {
        try {
            dataInputS.close();
            dataOutputS.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is supposed to simulate calculation.
     */
    public void sleepFor2000ms() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Gets clientsCalendar.
     *
     * @return Value of clientsCalendar.
     */
    public ArrayList<Time> getClientsCalendar() {
        return clientsCalendar;
    }

    /**
     * Sets new MeetingLength.
     *
     * @param MeetingLength New value of MeetingLength.
     */
    public void setMeetingLength(double MeetingLength) {
        this.MeetingLength = MeetingLength;
    }

    /**
     * Sets new clientsCalendar.
     *
     * @param clientsCalendar New value of clientsCalendar.
     */
    public void setClientsCalendar(ArrayList<Time> clientsCalendar) {
        this.clientsCalendar = clientsCalendar;
    }

    /**
     * Gets serverController.
     *
     * @return Value of serverController.
     */
    public ServerController getServerController() {
        return serverController;
    }

    /**
     * Sets new finishedOperations.
     *
     * @param finishedOperations New value of finishedOperations.
     */
    public static void setFinishedOperations(int finishedOperations) {
        finishedOperations = finishedOperations;
    }

    /**
     * Gets MeetingLength.
     *
     * @return Value of MeetingLength.
     */
    public double getMeetingLength() {
        return MeetingLength;
    }

    /**
     * Gets finishedOperations.
     *
     * @return Value of finishedOperations.
     */
    public static int getFinishedOperations() {
        return finishedOperations;
    }

    /**
     * Sets new minNumbOfCommonPartOperations.
     *
     * @param minNumbOfCommonPartOperations New value of minNumbOfCommonPartOperations.
     */
    public void setMinNumbOfCommonPartOperations(int minNumbOfCommonPartOperations) {
        this.minNumbOfCommonPartOperations = minNumbOfCommonPartOperations;
    }

    /**
     * Gets dataOutputS.
     *
     * @return Value of dataOutputS.
     */
    public DataOutputStream getDataOutputS() {
        return dataOutputS;
    }

    /**
     * Sets new connectionSocket.
     *
     * @param connectionSocket New value of connectionSocket.
     */
    public void setConnectionSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    /**
     * Sets new finalCalendar.
     *
     * @param finalCalendar New value of finalCalendar.
     */
    public void setFinalCalendar(ArrayList<Time> finalCalendar) {
        this.finalCalendar = finalCalendar;
    }

    /**
     * Gets finalCalendar.
     *
     * @return Value of finalCalendar.
     */
    public ArrayList<Time> getFinalCalendar() {
        return finalCalendar;
    }

    /**
     * Gets commonLock.
     *
     * @return Value of commonLock.
     */
    public Lock getCommonLock() {
        return commonLock;
    }

    /**
     * Sets new dataInputS.
     *
     * @param dataInputS New value of dataInputS.
     */
    public void setDataInputS(DataInputStream dataInputS) {
        this.dataInputS = dataInputS;
    }

    /**
     * Gets connectionSocket.
     *
     * @return Value of connectionSocket.
     */
    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    /**
     * Sets new serverController.
     *
     * @param serverController New value of serverController.
     */
    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    /**
     * Gets dataInputS.
     *
     * @return Value of dataInputS.
     */
    public DataInputStream getDataInputS() {
        return dataInputS;
    }

    /**
     * Gets minNumbOfCommonPartOperations.
     *
     * @return Value of minNumbOfCommonPartOperations.
     */
    public int getMinNumbOfCommonPartOperations() {
        return minNumbOfCommonPartOperations;
    }

    /**
     * Sets new dataOutputS.
     *
     * @param dataOutputS New value of dataOutputS.
     */
    public void setDataOutputS(DataOutputStream dataOutputS) {
        this.dataOutputS = dataOutputS;
    }
}