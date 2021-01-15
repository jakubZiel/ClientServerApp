package model.Client;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import controller.Client.ClientController;
import model.Data.Calendars;
import model.Data.Time;

public class Client extends Thread{

    public ClientController clientController;

    private Socket connectionSocket = null;
    private String ipAddress;

    private DataInputStream dataInputS;
    private DataOutputStream dataOutputS;


    public ArrayList<Time> calendar;
    public ArrayList<String> finalCalendarString;

    private int portNumb = 5055;

    private  void connectToServerSocket() throws IOException {

        //connect to local host to correct port number

            connectionSocket = new Socket(ipAddress, portNumb);

            dataInputS = new DataInputStream(connectionSocket.getInputStream());
            dataOutputS = new DataOutputStream(connectionSocket.getOutputStream());
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

    }

    @Override
    public void run() {


        try {
            connectToServerSocket();

            clientController.getClientGUI().setSent(true);

            clientController.showThatClientIsConnected();

            sendCalendarToServer();
            getFinalCalendarFromServer();

            closeConnection();

            Calendars.justPrintFormattedToString(finalCalendarString);

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            clientController.showFinalCalendarToView();
            clientController.showThatClientIsNotConnected();

        } catch (IOException e){
            clientController.showThatClientIsNotConnected();
            clientController.displayServerIsNotReady();
        }

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

    public ArrayList<String> getFinalCalendarString() {
        return finalCalendarString;
    }

    public static void main(String[] args) {
        Client client = new Client("192.168.1.110");
    }

    public ArrayList<Time> getCalendar() {
        return calendar;
    }

    public boolean validateCalendarInput(int beg2, int beg1, int end2, int end1, int dstIndex, boolean notSelected) {

        Time t = new Time(beg2, beg1, end2, end1);

        double front;
        double back;

        if(clientController.getClient().calendar.size() == 0) {
            clientController.getClient().calendar.add(t);
            return true;
        }

        back = clientController.getClient().calendar.get(clientController.getClient().calendar.size()-1).end;

        if (notSelected){
            if (back < t.beg) {
                clientController.getClient().calendar.add(t);
                return true;
            }
        }else {

            if (dstIndex != 0)
                front = clientController.getClient().calendar.get(dstIndex - 1).end;
            else
                front = 0.0;

                back = clientController.getClient().calendar.get(dstIndex).beg;


            if (dstIndex == 0 &&  clientController.getClient().calendar.size() == 1){
                front = 0.0;
                back = clientController.getClient().calendar.get(0).beg;
            }

            if (t.beg > front && t.end < back) {
                clientController.getClient().calendar.add(dstIndex, t);
                return true;
            }
        }

        return false;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

}
