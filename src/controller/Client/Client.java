package controller.Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import view.Client.ClientGUI;
import model.Calendars;
import model.Time;
import view.Client.GUIResponse;

public class Client extends Thread{

    public ClientGUI ClGUI;

    private Socket connectionSocket = null;
    private String ipAddress;

    private DataInputStream dataInputS;
    private DataOutputStream dataOutputS;


    public ArrayList<Time> calendar;
    public ArrayList<String> finalCalendarString;

    private int portNumb = 5055;

    private GUIResponse guiResponse;

    //this function runs everything
    private  void connectToServerSocket(){

        //connect to local host to correct port number
        try {
            connectionSocket = new Socket(ipAddress, portNumb);

            dataInputS = new DataInputStream(connectionSocket.getInputStream());
            dataOutputS = new DataOutputStream(connectionSocket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }

        sendCalendarToServer();
        getFinalCalendarFromServer();

        try {
            dataOutputS.close();
            dataInputS.close();
            connectionSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Client(String ip){

        finalCalendarString = new ArrayList<>();
        calendar = new ArrayList<>();
        ClGUI = new ClientGUI();
        ClGUI.bindWithClient(this);

    }

    @Override
    public void run() {
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        connectToServerSocket();
        Calendars.justPrintFormattedToString(finalCalendarString);


        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ClGUI.setVisible(false);

        guiResponse = new GUIResponse(finalCalendarString, this);

    }

    private void sendCalendarToServer(){

        try {
            dataOutputS.writeInt(calendar.size());

            ObjectOutputStream serializedOutput = new ObjectOutputStream(connectionSocket.getOutputStream());
            for(Time date : calendar) {
                System.out.println(date.beg + " " + date.end);
                serializedOutput.writeObject(date);
            }
            System.out.println("Calendar has been sent successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getFinalCalendarFromServer(){

        try {
            int sizeOfCalendar = dataInputS.readInt();

            for(int i = 0 ; i < sizeOfCalendar ; i++)
                finalCalendarString.add(dataInputS.readUTF());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ClientGUI getClGUI() {
        return ClGUI;
    }

    public static void main(String[] args) {
        Client client = new Client("192.168.1.110");
    }
}
