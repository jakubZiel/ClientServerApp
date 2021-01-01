package controller.Client;

import model.Client.Client;
import view.Client.ClientGUI;

import view.Client.ResponsePanel;

import javax.swing.*;
import java.awt.*;

public class ClientController {
    private Client client;
    private ClientGUI clientGUI;

    public ClientController() {
        this.clientGUI = new ClientGUI();
        this.client = new Client("127.0.0.1");

        connectClientToController();
        connectGUIToController();
    }

    private void connectClientToController() {
        this.client.setClientController(this);
    }

    public void connectGUIToController(){
        this.clientGUI.setClientController(this);
    }

    public Client getClient() {
        return client;
    }

    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public void viewSendsCalendarToModelAndRequestsFinalCalendar(String address){
        client.start();
    }

    public void moveGUILocation(int dx, int dy){
        Point location = clientGUI.getLocation();

        location.x += dx;
        location.y += dy;

        clientGUI.setLocation(location);
    }

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

    public void removeIndexFromClientsCalendar(int index) {
        client.getCalendar().remove(index);
    }

    public boolean validateCalendarInput(int beg2, int end2, int beg1, int end1, int dstIndex, boolean notSelected){

        return client.validateCalendarInput(beg2, end2, beg1, end1, dstIndex, notSelected);
    }

    public void displayServerIsNotReady() {
        clientGUI.displayInfoServerIsNotReady();
        client = new Client(client.getIpAddress());
        client.setClientController(this);
        clientGUI.clearCalendar();
    }

    public void showThatClientIsConnected(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clientGUI.setIsConnectedTrue();
            }
        });
    }

    public void showThatClientIsNotConnected(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clientGUI.setIsConnectedFalse();
            }
        });
    }

    public void restoreClientThread(){
        client = new Client("127.0.0.1");
        client.setClientController(this);

    }

    public static void main(String[] args) throws InterruptedException {
        ClientController controller = new ClientController();
    }
}
