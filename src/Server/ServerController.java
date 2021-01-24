package Server;

import javax.swing.*;
import java.awt.*;


/**
 *  Server Controller class, functions as a interface between model layer and view layer.
 *  Works according to MVC project pattern.
 */

public class ServerController {



    private Server server;
    private ServerGUI serverGUI;
    private boolean serverRunning = false;


    public ServerController(Server server, ServerGUI serverGUI) {
        this.server = server;
        this.serverGUI = serverGUI;

        connectGUIToController();
    }

    /**
     * Informs that view has already acquired settings for a new session, and starts or notifies Server thread.
     * @param NumberOfClients number of clients in new session
     * @param meetingLength length of new session
     */
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

    /**
     * Sets GUIs controller to this object.
     */
    public void connectGUIToController(){
        serverGUI.setServerController(this);
    }

    /**
     * Model request JList of currently connected clients to refresh, because new client has been connected.
     * @param newItem Name of new client
     */
    public void modelRequestsRefreshingConnectedClientsList(String newItem){

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                serverGUI.refreshClientsList(newItem);
            }
        });
    }

    /**
     * Called after previous session has been already finished. Sets view to show 'inactive' state.
     */
    public void restartServerView(){

       serverGUI.restartListModel();
       serverGUI.getStatusBar().setForeground(Color.RED);
       serverGUI.getStatusBar().setText("status : inactive");
    }

    /**
     * Closes all the servers resources.
     */
    public void terminateServer() {
        server.terminateServer();
    }

    public Server getServer() {
        return server;
    }

    public ServerGUI getServerGUI() {
        return serverGUI;
    }

    public boolean isServerRunning() {
        return serverRunning;
    }

    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }
}
