package view.Client;

import model.Client.Client;

import org.json.simple.JSONObject;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class GUIResponse extends JFrame {

    ArrayList<String> finalCalendar;
    Client client;
    JList<String> calendar;
    JButton downloadJSON;
    JLabel finalCalendarLabel;
    JButton downloadFile;

    public GUIResponse (ArrayList<String> finalCalendar, Client client) {

        this.client = client;
        this.finalCalendar = finalCalendar;

        if (this.client != null)
            setLocation(client.getClGUI().getLocation());

        setSize(new Dimension(600, 400));

        addButtons();

        initResultCalendar();

        addActionListeners();


        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void addButtons(){
        downloadJSON = new JButton();
        add(downloadJSON);

        downloadJSON.setSize(250, 60);
        downloadJSON.setLocation(this.getWidth() / 4 + this.getWidth() / 2 - downloadJSON.getWidth() / 2, 250);
        downloadJSON.setText("Download JSON file");


        downloadFile = new JButton();
        add(downloadFile);

        downloadFile.setSize(250, 60);
        downloadFile.setLocation(this.getWidth() / 4 + this.getWidth() / 2 - downloadFile.getWidth() / 2, 160);
        downloadFile.setText("Download file");

    }

    private void addActionListeners(){

        downloadJSON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createJSON();
            }
        });

        downloadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFile();
            }
        });
    }

    private void createFile(){
        int counter = 1;

        try {

            FileWriter file = new FileWriter("./OutputFiles/meetings");

            file.write("meetings\n");
            file.flush();

            for (String meeting : finalCalendar) {

                file.write("nr " + counter + " " + meeting + "\n");
                file.flush();
                counter++;
            }

            file.close();
        } catch (IOException e){
            e.printStackTrace();
        }


    }
    @SuppressWarnings("unchecked")
    private void createJSON(){
        int counter = 1;

        try {

            FileWriter file = new FileWriter("./OutputFiles/meetings.json");

            file.write("\"meetings\" : {\n");
            file.flush();

            for (String meeting : finalCalendar) {


                JSONObject meetingDetails = new JSONObject();
                meetingDetails.put("ID", counter);
                meetingDetails.put("bounds", meeting);

                JSONObject meetingObj = new JSONObject();
                meetingObj.put("meeting", meetingDetails);

                file.write(meetingObj.toJSONString());
                if (counter != finalCalendar.size())
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

    private void initResultCalendar(){
        DefaultListModel<String> list = new DefaultListModel<>();
        calendar = new JList<>();

        int counter = 1;

        for (String line : finalCalendar) {
            list.addElement("nr " + counter + " : " + line);
            counter++;
        }

        calendar.setModel(list);

        calendar.setBounds(25,70 , 250, 20 * finalCalendar.size() + 80);
        calendar.setFont(new Font("myFont", Font.PLAIN  ,20));
        add(calendar);

        finalCalendarLabel = new JLabel("All available meetings :");
        add(finalCalendarLabel);
        finalCalendarLabel.setLocation(25, 10);
        finalCalendarLabel.setSize(250, 50);
        finalCalendarLabel.setFont(new Font("myFont", Font.PLAIN  ,20));

    }

    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");
        list.add("1");

        new GUIResponse(list,null);
    }
}
