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


    /**
     * Sets new serverRunning.
     *
     * @param serverRunning New value of serverRunning.
     */
    public void setServerRunning(boolean serverRunning) {
        this.serverRunning = serverRunning;
    }

    /**
     * Gets serverGUI.
     *
     * @return Value of serverGUI.
     */
    public ServerGUI getServerGUI() {
        return serverGUI;
    }

    /**
     * Gets serverRunning.
     *
     * @return Value of serverRunning.
     */
    public boolean isServerRunning() {
        return serverRunning;
    }

    /**
     * Gets server.
     *
     * @return Value of server.
     */
    public Server getServer() {
        return server;
    }

    /**
     * Sets new serverGUI.
     *
     * @param serverGUI New value of serverGUI.
     */
    public void setServerGUI(ServerGUI serverGUI) {
        this.serverGUI = serverGUI;
    }

    /**
     * Sets new server.
     *
     * @param server New value of server.
     */
    public void setServer(Server server) {
        this.server = server;
    }
}
