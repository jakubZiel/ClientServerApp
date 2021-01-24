package Client;
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import Data.Calendars;
import Data.Time;
import org.json.simple.JSONObject;


/**
 * Client thread that requests connection to Server and after being approved sends
 * earlier gathered data to its ClientHandler and then waits for final Calendar to be transferred back to client.
 *
 */
public class Client extends Thread{

    private ClientController clientController;

    private Socket connectionSocket = null;
    private String ipAddress;

    private DataInputStream dataInputS;
    private DataOutputStream dataOutputS;


    private ArrayList<Time> calendar;
    private ArrayList<String> finalCalendarString;

    private int portNumb = 5055;

    /**
     *
     * Connects to clientHandler via socket.
     * @throws IOException
     */
    private  void connectToServerSocket() throws IOException {

        //connect to local host to correct port number

            connectionSocket = new Socket(ipAddress, portNumb);

            dataInputS = new DataInputStream(connectionSocket.getInputStream());
            dataOutputS = new DataOutputStream(connectionSocket.getOutputStream());
    }

    /**
     * Closes socket connection to ClientHandler.
     */
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

    /**
     * Main class function that starts thread and therefore the process of sending and acquiring back final calendar.
     * It connects, requests view updates sends calendar and then wait for a returning set of meetings. At the end it also
     * prints result to standard output.
     */
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

    /**
     * Procedure sends calendar via socket to clientHandler for further processing.
     */

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

    /**
     * Procedure waits for finalCalendar that is incoming from ClientHandler.
     * Every received object is put in final Calendar of strings.
     */
    private void getFinalCalendarFromServer(){

        try {
            int sizeOfCalendar = dataInputS.readInt();

            for(int i = 0 ; i < sizeOfCalendar ; i++)
                finalCalendarString.add(dataInputS.readUTF());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if this time object can be put in the list of time objects at particular index.
     * Function check whether will be sorted after insertion of object.
     *
     * @param beg2 hour of beginning
     * @param beg1 minutes of beginning
     * @param end2 hour of ending
     * @param end1 minutes of ending
     * @param dstIndex index to insert new Time object at
     * @param notSelected checks if any JList item is selected
     * @return true if time object can be inserted
     */

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
    /**
     * Creates simple file to store result of requests. Saves it to ./OutputFiles/meetings#date
     */
    public void createFile(){
        int counter = 1;

        Date currentDate = new Date(System.currentTimeMillis());

        SimpleDateFormat ft = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

        String fileName = ft.format(currentDate);

        try {

            File newFile = new File("./OutputFiles/meetings" + fileName);

            if (newFile.createNewFile()) {
                FileWriter fileWriter = new FileWriter("./OutputFiles/meetings" + fileName);

                fileWriter.write("meetings\n");
                fileWriter.flush();

                for (String meeting : clientController.getClient().getFinalCalendarString()) {

                    fileWriter.write("nr " + counter + " " + meeting + "\n");
                    fileWriter.flush();
                    counter++;
                }

                fileWriter.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Create JSON file to store result of requests. Saves it to ./OutputFiles/meetings#date.JSON
     */
    @SuppressWarnings("unchecked")
    public void createJSON(){
        int counter = 1;

        Date currentDate = new Date(System.currentTimeMillis());

        SimpleDateFormat ft = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");

        String fileName = ft.format(currentDate);

        try {

            File newFile = new File("./OutputFiles/meetings" + fileName + ".json");
            FileWriter file = new FileWriter("./OutputFiles/meetings" + fileName + ".json");


            newFile.createNewFile();
            file.write("\"meetings\" : {\n");
            file.flush();

            for (String meeting : clientController.getClient().getFinalCalendarString()) {


                JSONObject meetingDetails = new JSONObject();
                meetingDetails.put("ID", counter);
                meetingDetails.put("bounds", meeting);

                JSONObject meetingObj = new JSONObject();
                meetingObj.put("meeting", meetingDetails);

                file.write(meetingObj.toJSONString());
                if (counter != clientController.getClient().getFinalCalendarString().size())
                    file.write(",");

                file.write("\n");
                file.flush();

                System.out.println(meetingObj.toJSONString());
                counter++;
            }

            file.write("}");
            file.flush();

            file.close();


        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }


    public void setConnectionSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setDataInputS(DataInputStream dataInputS) {
        this.dataInputS = dataInputS;
    }

    public void setCalendar(ArrayList<Time> calendar) {
        this.calendar = calendar;
    }

    public Socket getConnectionSocket() {
        return connectionSocket;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public DataInputStream getDataInputS() {
        return dataInputS;
    }

    public DataOutputStream getDataOutputS() {
        return dataOutputS;
    }

    public void setDataOutputS(DataOutputStream dataOutputS) {
        this.dataOutputS = dataOutputS;
    }

    public ArrayList<Time> getCalendar() {
        return calendar;
    }

    public ArrayList<String> getFinalCalendarString() {
        return finalCalendarString;
    }

    public void setFinalCalendarString(ArrayList<String> finalCalendarString) {
        this.finalCalendarString = finalCalendarString;
    }

    public int getPortNumb() {
        return portNumb;
    }

    public void setPortNumb(int portNumb) {
        this.portNumb = portNumb;
    }
}
