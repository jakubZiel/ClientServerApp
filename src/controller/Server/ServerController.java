package controller.Server;

import model.Server.Server;
import view.Server.ServerGUI;

import javax.swing.*;
import java.awt.*;

public class ServerController {

    private Server server;
    private ServerGUI serverGUI;
    private boolean serverRunning = false;


    public ServerController(Server server, ServerGUI serverGUI) {
        this.server = server;
        this.serverGUI = serverGUI;

        connectGUIToController();
    }

    public void viewHasFormForModel(int NumberOfClients, double meetingLength){

        if (server != null){
            server.setNumberOfClients(NumberOfClients);
            server.setMeetingLength(meetingLength);

            if (!serverRunning){
                serverRunning = true;
                server.start();


            } else synchronized (server.getServerLock()){
                server.getServerLock().notify();
            }

        }
    }

    public void connectGUIToController(){
        serverGUI.setServerController(this);
    }

    public void modelRequestsRefreshingConnectedClientsList(String newItem){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                serverGUI.refreshClientsList(newItem);
            }
        });
    }

    public void restartServerView(){

       serverGUI.restartListModel();
       serverGUI.getStatusBar().setForeground(Color.RED);
       serverGUI.getStatusBar().setText("status : inactive");
    }

    public Server getServer() {
        return server;
    }

    public ServerGUI getServerGUI() {
        return serverGUI;
    }

    public void terminateServer() {
        server.terminateServer();
    }

}
