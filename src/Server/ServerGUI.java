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


    /**
     * Gets ClientList.
     *
     * @return Value of ClientList.
     */
    public JList getClientList() {
        return ClientList;
    }

    /**
     * Sets new statusBar.
     *
     * @param statusBar New value of statusBar.
     */
    public void setStatusBar(JLabel statusBar) {
        this.statusBar = statusBar;
    }

    /**
     * Sets new NumberOfClients.
     *
     * @param NumberOfClients New value of NumberOfClients.
     */
    public void setNumberOfClients(JLabel NumberOfClients) {
        this.NumberOfClients = NumberOfClients;
    }

    /**
     * Sets new setNumberOfClients.
     *
     * @param setNumberOfClients New value of setNumberOfClients.
     */
    public void setSetNumberOfClients(JSpinner setNumberOfClients) {
        this.setNumberOfClients = setNumberOfClients;
    }

    /**
     * Sets new serverController.
     *
     * @param serverController New value of serverController.
     */
    public void setServerController(ServerController serverController) {
        this.serverController = serverController;
    }

    /**
     * Sets new ClientList.
     *
     * @param ClientList New value of ClientList.
     */
    public void setClientList(JList ClientList) {
        this.ClientList = ClientList;
    }

    /**
     * Sets new PanelRight.
     *
     * @param PanelRight New value of PanelRight.
     */
    public void setPanelRight(JPanel PanelRight) {
        this.PanelRight = PanelRight;
    }

    /**
     * Gets serverController.
     *
     * @return Value of serverController.
     */
    public ServerController getServerController() {
        return serverController;
    }

    /**
     * Gets setNumberOfClients.
     *
     * @return Value of setNumberOfClients.
     */
    public JSpinner getSetNumberOfClients() {
        return setNumberOfClients;
    }

    /**
     * Gets PanelLeft.
     *
     * @return Value of PanelLeft.
     */
    public JPanel getPanelLeft() {
        return PanelLeft;
    }

    /**
     * Gets PanelRight.
     *
     * @return Value of PanelRight.
     */
    public JPanel getPanelRight() {
        return PanelRight;
    }

    /**
     * Gets startServerButton.
     *
     * @return Value of startServerButton.
     */
    public JButton getStartServerButton() {
        return startServerButton;
    }

    /**
     * Sets new TurnOff.
     *
     * @param TurnOff New value of TurnOff.
     */
    public void setTurnOff(JButton TurnOff) {
        this.TurnOff = TurnOff;
    }

    /**
     * Sets new mainPanel.
     *
     * @param mainPanel New value of mainPanel.
     */
    public void setMainPanel(JPanel mainPanel) {
        this.mainPanel = mainPanel;
    }

    /**
     * Gets statusBar.
     *
     * @return Value of statusBar.
     */
    public JLabel getStatusBar() {
        return statusBar;
    }

    /**
     * Sets new startServerButton.
     *
     * @param startServerButton New value of startServerButton.
     */
    public void setStartServerButton(JButton startServerButton) {
        this.startServerButton = startServerButton;
    }

    /**
     * Sets new listModel.
     *
     * @param listModel New value of listModel.
     */
    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }

    /**
     * Gets TurnOff.
     *
     * @return Value of TurnOff.
     */
    public JButton getTurnOff() {
        return TurnOff;
    }

    /**
     * Sets new MeetingLengthField.
     *
     * @param MeetingLengthField New value of MeetingLengthField.
     */
    public void setMeetingLengthField(JTextField MeetingLengthField) {
        this.MeetingLengthField = MeetingLengthField;
    }

    /**
     * Sets new MeetingLength.
     *
     * @param MeetingLength New value of MeetingLength.
     */
    public void setMeetingLength(JLabel MeetingLength) {
        this.MeetingLength = MeetingLength;
    }

    /**
     * Gets MeetingLengthField.
     *
     * @return Value of MeetingLengthField.
     */
    public JTextField getMeetingLengthField() {
        return MeetingLengthField;
    }

    /**
     * Sets new PanelLeft.
     *
     * @param PanelLeft New value of PanelLeft.
     */
    public void setPanelLeft(JPanel PanelLeft) {
        this.PanelLeft = PanelLeft;
    }

    /**
     * Gets listModel.
     *
     * @return Value of listModel.
     */
    public DefaultListModel getListModel() {
        return listModel;
    }

    /**
     * Gets NumberOfClients.
     *
     * @return Value of NumberOfClients.
     */
    public JLabel getNumberOfClients() {
        return NumberOfClients;
    }

    /**
     * Gets mainPanel.
     *
     * @return Value of mainPanel.
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Gets MeetingLength.
     *
     * @return Value of MeetingLength.
     */
    public JLabel getMeetingLength() {
        return MeetingLength;
    }
}


