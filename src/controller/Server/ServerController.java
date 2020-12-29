package controller.Server;

import model.Server.Server;
import view.Server.ServerGUI;

import javax.swing.*;

public class ServerController {

    private Server server;
    private ServerGUI serverGUI;

    public ServerController(Server server, ServerGUI serverGUI) {
        this.server = server;
        this.serverGUI = serverGUI;

        connectGUIToController();
    }

    public void viewHasFormForModel(int NumberOfClients, double meetingLength){

        if (server != null){
            server.setNumberOfClients(NumberOfClients);
            server.setMeetingLength(meetingLength);

            server.start();
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
    }

    public Server getServer() {
        return server;
    }

    public ServerGUI getServerGUI() {
        return serverGUI;
    }

    public void closeAllResources() {
        server.cleanUpResources();
    }
}
