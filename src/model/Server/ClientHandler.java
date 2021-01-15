package model.Server;
import java.io.*;
import java.net.*;

import controller.Server.ServerController;
import model.Data.Lock;
import model.Data.Calendars;
import model.Data.Time;
import java.util.ArrayList;

public class ClientHandler extends Thread {



    static int finishedOperations = 0;

    DataInputStream dataInputS;
    DataOutputStream dataOutputS;
    Socket connectionSocket;
    ArrayList<Time> finalCalendar;
    ArrayList<Time> clientsCalendar;
    int minNumbOfCommonPartOperations;
    Lock commonLock;
    double MeetingLength;

    public ServerController serverController;


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

    public static void CommonPartOperationFinished() {
        finishedOperations++;
    }

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

    public void updateCommonResource() {

        if (finishedOperations > 0)
            clientsCalendar = Calendars.solution(clientsCalendar, finalCalendar, MeetingLength);

        finalCalendar.clear();

        finalCalendar.addAll(clientsCalendar);

    }

    public void endConnection() {
        try {
            dataInputS.close();
            dataOutputS.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sleepFor2000ms() {
        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}