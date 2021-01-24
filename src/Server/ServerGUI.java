package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * View module of MVC for Server App. Main responsibilities are to show currently connected clients and acquire new session parameters from user.
 * GUI provides possibility to make multiple sessions and to close connection at will.
 */

public class ServerGUI extends JFrame{


    private JPanel mainPanel;
    private JPanel PanelLeft;
    private JPanel PanelRight;
    private JSpinner setNumberOfClients;
    private JLabel MeetingLength;
    private JTextField MeetingLengthField;
    private JButton TurnOff;
    private JLabel NumberOfClients;
    private JButton startServerButton;

    private JList ClientList;
    private JLabel statusBar;
    private DefaultListModel listModel;

    private ServerController serverController;

    /**
     * Sets actions Listeners, initializes JList model and make frame visible.
     */
    public ServerGUI(){
        super("Server User Interface");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600,450);

        initializeListModel();
        addActionListeners();

        setVisible(true);
    }

    /**
     * Add action Listeners to buttons startServer and TurnOff.
     */
    public void addActionListeners(){

        TurnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Server terminated");

                serverController.terminateServer();

                System.exit(69);
            }
        });

        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { FormHasBeenFinished(); }
        });
    }

    /**
     * Sets new data model for JList.
     */
    public void initializeListModel(){
        listModel = new DefaultListModel();
        ClientList.setModel(listModel);
    }

    /**
     * After receiving new connection, function updates JList with new ClientConnection and its number.
     * @param string takes Argumetn that is to be added to JList, in our case it is 'Client nr x'
     */
    public void refreshClientsList(String string){
        listModel.addElement(string);
    }

    /**
     * If everything is filled, it confirms that model can be set with data recently acquired from form. It also sets
     * server status to active.
     */
    public void FormHasBeenFinished(){
        String textField = MeetingLengthField.getText();
        Double meetingLength;
        int NumberOfClientPara = (int)setNumberOfClients.getValue();

        if(textField.equals("")){
            JOptionPane.showMessageDialog(this, "Incorrect length of the meeting");
            return;
        }else if(!isNumberOfClientsOK(NumberOfClientPara)){
            JOptionPane.showMessageDialog(this, "Incorrect number of Clients");
            return;
        }
        else  meetingLength = Double.parseDouble(MeetingLengthField.getText());

        statusBar.setText("status : active");
        statusBar.setForeground(Color.GREEN);

        serverController.viewHasFormForModel(NumberOfClientPara, meetingLength);
    }

    /**
     * Check if number of clients is a natural number.
     * @param result number of clients that we expect for incoming session
     * @return if is or is not a natural number smaller than 10
     */
    private boolean isNumberOfClientsOK(int result){
        return result > 0 && result < 10;
    }

    /**
     * Clears JList model of items (connected clients) after end of session
     */
    public void restartListModel(){
        listModel.clear();
    }

    public JLabel getStatusBar() {
        return statusBar;
    }

    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    public ServerController getServerController() {
        return serverController;
    }


}


