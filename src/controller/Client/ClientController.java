package controller.Client;

import model.Client.Client;
import view.Client.ClientGUI;

import view.Client.ResponsePanel;

import javax.swing.*;
import java.awt.*;

public class ClientController {
    private Client client;
    private ClientGUI clientGUI;

    public ClientController(Client client, ClientGUI clientGUI) {
        this.client = client;
        this.clientGUI = clientGUI;

        connectGUIToController();
    }

    public void connectGUIToController(){

        clientGUI.setClientController(this);
    }

    public Client getClient() {
        return client;
    }

    public ClientGUI getClientGUI() {
        return clientGUI;
    }

    public void viewSendsCalendarToModelAndRequestsFinalCalendar(String address){
        client.setIpAddress(address);
        synchronized (client){
            client.notify();
        }
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


    public boolean isServerListening(){
       return client.checkIfServerIsListening();
    }
}
