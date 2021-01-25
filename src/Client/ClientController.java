package Client;

import javax.swing.*;
import java.awt.*;

/**
 *  Client Controller class, functions as a interface between model layer and view layer.
 *  Works according to MVC project pattern.
 */

public class ClientController {
    private Client client;
    private ClientGUI clientGUI;

    /**
     * Creates ClientController object, then creates View and Models and connects to them.
     */
    public ClientController() {
        this.clientGUI = new ClientGUI();
        this.client = new Client("127.0.0.1");

        connectClientToController();
        connectGUIToController();
    }

    /**
     * Set this clientController as a clientController of Client object.
     */
    private void connectClientToController() {
        this.client.setClientController(this);
    }
    /**
     * Set this clientController as a clientController of ClientGUI object.
     */
    private void connectGUIToController(){
        this.clientGUI.setClientController(this);
    }

    /**
     *
     *  Function called by view after ConnectButton was clicked, starts thread that make model request final calendar from server.
     */
    public void viewSendsCalendarToModelAndRequestsFinalCalendar(){
        client.start();
    }

    /**
     * Move GUI frame on the screen.
     *
     * @param dx move horizontally
     * @param dy move vertically
     */
    public void moveGUILocation(int dx, int dy){
        Point location = clientGUI.getLocation();

        location.x += dx;
        location.y += dy;

        clientGUI.setLocation(location);
    }

    /**
     * Changes mainPanel of GUI from form-taking to result-displaying. Then repaints the frame.
     */

    public void showFinalCalendarToView() {

        ClientController controller = this;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                clientGUI.setResponsePanel(new ResponsePanel(controller.getClient().getFinalCalendarString(), controller));
                clientGUI.setContentPane(clientGUI.getResponsePanel());
                clientGUI.revalidate();
                clientGUI.repaint();
            }
        });
    }

    /**
     * Removes given Time object from calendar that has not been yet sent to server.
     * @param index object to be removed
     */
    public void removeIndexFromClientsCalendar(int index) {
        client.getCalendar().remove(index);
    }

    /**
     * Check if Time object can be put in between 2 other Time object without breaking rules of "valid calendar".
     * @param beg2 Hours of 1 Time object
     * @param end2 Hours of 2 Time object
     * @param beg1 Minutes of 1 Time object
     * @param end1 Minutes of 2 Time object
     * @param dstIndex where to put element
     * @param notSelected if any item from JList is selected
     * @return True calendar can but put in place
     */
    public boolean validateCalendarInput(int beg2, int end2, int beg1, int end1, int dstIndex, boolean notSelected){

        return client.validateCalendarInput(beg2, end2, beg1, end1, dstIndex, notSelected);
    }

    /**
     * Display that server is not ready, connection has been rejected.
     */
    public void displayServerIsNotReady() {
        clientGUI.displayInfoServerIsNotReady();
        client = new Client(client.getIpAddress());
        client.setClientController(this);
        clientGUI.clearCalendar();
    }
    /**
        Displays that client is not connected to server.
     */
    public void showThatClientIsConnected(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clientGUI.setIsConnectedTrue();
            }
        });
    }

    /**
     * Displays that client is connected to server.
     */
     public void showThatClientIsNotConnected(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clientGUI.setIsConnectedFalse();
            }
        });
    }

    /**
     * Prepare whole app to be relaunched in order to fetch another final calendar.
     */
    public void restoreClientThread(){
        client = new Client("127.0.0.1");
        client.setClientController(this);

    }

    /**
     * Called when user requests to download meetings into a file in plain text format.
     */
    public  void viewWantsToWriteToFilePlain(){

        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                client.createFile();
            }
        });
        task.start();
    }
    /**
     * Called when user requests to download meetings into a file in JSON format.
     */
    public  void viewWantToWriteToFileJSON(){

        Thread task = new Thread(new Runnable() {
            @Override
            public void run() {
                client.createJSON();
            }
        });
        task.start();
    }



    public static void main(String[] args) {
        ClientController controller = new ClientController();
    }

    /**
     * Gets client.
     *
     * @return Value of client.
     */
    public Client getClient() {
        return client;
    }

    /**
     * Gets clientGUI.
     *
     * @return Value of clientGUI.
     */
    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    /**
     * Sets new client.
     *
     * @param client New value of client.
     */
    public void setClient(Client client) {
        this.client = client;
    }

    /**
     * Sets new clientGUI.
     *
     * @param clientGUI New value of clientGUI.
     */
    public void setClientGUI(ClientGUI clientGUI) {
        this.clientGUI = clientGUI;
    }
}
