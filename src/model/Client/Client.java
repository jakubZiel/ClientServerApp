package model.Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import controller.Client.ClientController;
import view.Client.ClientGUI;
import model.Data.Calendars;
import model.Data.Time;
import view.Client.GUIResponse;

public class Client extends Thread{

    public ClientController clientController;

    private Socket connectionSocket = null;
    private String ipAddress;

    private DataInputStream dataInputS;
    private DataOutputStream dataOutputS;


    public ArrayList<Time> calendar;
    public ArrayList<String> finalCalendarString;

    private int portNumb = 5055;

    private  void connectToServerSocket() {

        //connect to local host to correct port number
        try {
            connectionSocket = new Socket(ipAddress, portNumb);

            dataInputS = new DataInputStream(connectionSocket.getInputStream());
            dataOutputS = new DataOutputStream(connectionSocket.getOutputStream());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void closeConnection(){
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
        clientController = new ClientController(this, new ClientGUI());

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

        sendCalendarToServer();
        getFinalCalendarFromServer();

        closeConnection();

        Calendars.justPrintFormattedToString(finalCalendarString);


        try {
            sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clientController.hideViewRequestFromModel();

        clientController.setGuiResponse(new GUIResponse(finalCalendarString, clientController));

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


    public ClientController getClientController() {
        return clientController;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public static void main(String[] args) {
        Client client = new Client("192.168.1.110");
    }

}
