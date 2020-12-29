package controller.Client;

import model.Client.Client;
import view.Client.ClientGUI;
import view.Client.GUIResponse;

import javax.swing.*;
import java.awt.*;

public class ClientController {
    private Client client;
    private ClientGUI clientGUI;
    private GUIResponse guiResponse;


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

    public void hideViewRequestFromModel(){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                clientGUI.setVisible(false);
            }
        });

    }

    public void setGuiResponse(GUIResponse guiResponse) {
        this.guiResponse = guiResponse;
    }

    public void moveGUILocation(int dx, int dy){
        Point location = clientGUI.getLocation();

        location.x += dx;
        location.y += dy;

        clientGUI.setLocation(location);
    }
}
