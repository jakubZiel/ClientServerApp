package Client;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class displays results of successful request for final calendar, also allows user to download result to file or perform another request.
 */

public class ResponsePanel extends JPanel {

    private ArrayList<String> finalCalendar;
    private ClientController clientController;
    private JList<String> calendar;
    private JButton downloadJSON;
    private JLabel finalCalendarLabel;
    private JButton downloadFile;
    private JButton sendAnotherRequest;

    /**
     * Creates new ResponsePanel. Adds needed Listeners and Buttons. Panel is already set as Visible without LayoutManager.
     * @param finalCalendar calendar that will be displayed as final result
     * @param controller controller that link view to model for this client app
     */
    public ResponsePanel (ArrayList<String> finalCalendar, ClientController controller) {

        this.clientController = controller;
        this.finalCalendar = finalCalendar;

        setSize(new Dimension(600, 400));

        addButtons();

        initResultCalendar();

        addActionListeners();


        setLayout(null);
        setVisible(true);
    }

    /**
     * Adds and resizes buttons.
     */
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

        sendAnotherRequest = new JButton();
        add(sendAnotherRequest);
        sendAnotherRequest.setSize(250,60);
        sendAnotherRequest.setLocation(this.getWidth() / 4 + this.getWidth() / 2 - downloadFile.getWidth() / 2, 70);
        sendAnotherRequest.setText("Next request");
    }

    /**
     * Adds action Listeners to buttons.
     */
    private void addActionListeners(){

        downloadJSON.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.viewWantToWriteToFileJSON();
            }
        });

        downloadFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clientController.viewWantsToWriteToFilePlain();
            }
        });


        sendAnotherRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                clientController.getClientGUI().setSent(false);
                clientController.restoreClientThread();
                clientController.getClientGUI().setContentPane(clientController.getClientGUI().getMainPanel());
                clientController.getClientGUI().clearCalendar();

            }
        });
    }


    /**
     * Initializes JList that displays result calendar after successful fetch.
     */
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


    /**
     * Sets new finalCalendar.
     *
     * @param finalCalendar New value of finalCalendar.
     */
    public void setFinalCalendar(ArrayList<String> finalCalendar) {
        this.finalCalendar = finalCalendar;
    }

    /**
     * Gets sendAnotherRequest.
     *
     * @return Value of sendAnotherRequest.
     */
    public JButton getSendAnotherRequest() {
        return sendAnotherRequest;
    }

    /**
     * Sets new sendAnotherRequest.
     *
     * @param sendAnotherRequest New value of sendAnotherRequest.
     */
    public void setSendAnotherRequest(JButton sendAnotherRequest) {
        this.sendAnotherRequest = sendAnotherRequest;
    }

    /**
     * Sets new clientController.
     *
     * @param clientController New value of clientController.
     */
    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    /**
     * Gets finalCalendarLabel.
     *
     * @return Value of finalCalendarLabel.
     */
    public JLabel getFinalCalendarLabel() {
        return finalCalendarLabel;
    }

    /**
     * Gets downloadFile.
     *
     * @return Value of downloadFile.
     */
    public JButton getDownloadFile() {
        return downloadFile;
    }

    /**
     * Sets new downloadFile.
     *
     * @param downloadFile New value of downloadFile.
     */
    public void setDownloadFile(JButton downloadFile) {
        this.downloadFile = downloadFile;
    }

    /**
     * Gets calendar.
     *
     * @return Value of calendar.
     */
    public JList<String> getCalendar() {
        return calendar;
    }

    /**
     * Gets clientController.
     *
     * @return Value of clientController.
     */
    public ClientController getClientController() {
        return clientController;
    }

    /**
     * Gets downloadJSON.
     *
     * @return Value of downloadJSON.
     */
    public JButton getDownloadJSON() {
        return downloadJSON;
    }

    /**
     * Sets new calendar.
     *
     * @param calendar New value of calendar.
     */
    public void setCalendar(JList<String> calendar) {
        this.calendar = calendar;
    }

    /**
     * Gets finalCalendar.
     *
     * @return Value of finalCalendar.
     */
    public ArrayList<String> getFinalCalendar() {
        return finalCalendar;
    }

    /**
     * Sets new finalCalendarLabel.
     *
     * @param finalCalendarLabel New value of finalCalendarLabel.
     */
    public void setFinalCalendarLabel(JLabel finalCalendarLabel) {
        this.finalCalendarLabel = finalCalendarLabel;
    }

    /**
     * Sets new downloadJSON.
     *
     * @param downloadJSON New value of downloadJSON.
     */
    public void setDownloadJSON(JButton downloadJSON) {
        this.downloadJSON = downloadJSON;
    }
}
