package Client;

import Data.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
/**
 * View module of MVC for Client App. Main responsibilities are to show the result of request to screen, and take input from user.
 * GUI provides possibility to make multiple requests and to download result of request as a simple file or JSON.
 */
public class ClientGUI extends JFrame {

    private JPanel MainPanel;
    private JSpinner spinnerStartBound1;
    private JSpinner spinnerEndBound1;
    private JLabel startBound;
    private JPanel endBound;
    private JLabel end;
    private JButton shutDownButton;
    private JButton connectButton;
    private JList list1;
    private JButton addBoundButton;
    private JTextField textField1;
    private JSpinner spinnerStartBound2;
    private JSpinner spinnerEndBound2;
    private JLabel hrsMin;
    private JLabel hrsMin2;
    private JButton removeBound;
    private JLabel isConnected;
    private ResponsePanel responsePanel;

    private ClientController clientController;

    private DefaultListModel listModel;

    private boolean sent = false;


    public ClientGUI(){
        super("Client User Interface");

        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 450);
        setAdvancedComponentSettings();

        setVisible(true);
    }

    /**
     * Utility function to set JComponents.
     */
    private void setAdvancedComponentSettings(){
        setSpinner();
        initializeListModel();
        addActionListeners();
        addFocusListeners();
    }

    /**
     * Sets spinners.
     */
    private void setSpinner(){
        SpinnerNumberModel modelHours1 = new SpinnerNumberModel(0, 0, 23, 1);
        SpinnerNumberModel modelMinutes1 = new SpinnerNumberModel(0, 0, 59, 1);
        SpinnerNumberModel modelHours2 = new SpinnerNumberModel(0, 0, 23, 1);
        SpinnerNumberModel modelMinutes2 = new SpinnerNumberModel(0, 0, 59, 1);

        spinnerEndBound2.setModel(modelMinutes1);
        spinnerEndBound1.setModel(modelHours1);
        spinnerStartBound1.setModel(modelHours2);
        spinnerStartBound2.setModel(modelMinutes2);
    }

    /**
     * Ads focus listener to ip address input field.
     */

    public void addFocusListeners(){
        textField1.setText("localhost");

        textField1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                textField1.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });
    }

    /**
     * Adds actionListeners to buttons.
     */

    public void  addActionListeners(){

        connectButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (!sent)
                connectAction();
            }
        });

        addBoundButton.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent e) {

                if (!sent)
                    addBoundToSet();
            }
        });

        shutDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Client program terminated");

                System.exit(2137);
            }
        });

        removeBound.addActionListener(new ActionListener() {

            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {

                if (!list1.isSelectionEmpty() && !sent) {
                    index = list1.getSelectedIndex();
                    DefaultListModel<String> model = (DefaultListModel<String>) list1.getModel();
                    model.remove(index);
                    list1.revalidate();
                    list1.repaint();

                    clientController.removeIndexFromClientsCalendar(index);
                }
            }
        });
    }

    /**
     * Add Item to JList representation of client calendar. After Clicking add button, procedure acquires
     * values of setting fields and adds element at its place.
     */
    public  void addBoundToSet(){

        int beg2 = (int)spinnerStartBound1.getValue();
        int end2 = (int)spinnerEndBound1.getValue();
        int beg1 = (int)spinnerStartBound2.getValue();
        int end1 = (int)spinnerEndBound2.getValue();

        Time t = new Time(beg2, beg1, end2, end1);

        if (t.end <= t.beg) {
            JOptionPane.showMessageDialog(this, "Wrong bound format. (Beginning is later than ending or equal to)");
            return;
        }

        tryToInsertToSet(beg2,beg1,end2,end1);

        revalidate();
        repaint();

    }

    /**
     * Utility function that insert time object into calendar. Uses validation from model.
     * @param beg2 hour of beginning
     * @param beg1 minutes of beginning
     * @param end2 hour of ending
     * @param end1 minutes of ending
     */
    private void tryToInsertToSet(int beg2,int beg1, int end2, int end1){
        String beg1String;
        String end1String;

        if (beg1 < 10)
            beg1String = '0' + String.valueOf(beg1);
        else
            beg1String = String.valueOf(beg1);

        if (end1 < 10)
            end1String = '0' + String.valueOf(end1);
        else
            end1String = String.valueOf(end1);

        if (!list1.isSelectionEmpty() && clientController.validateCalendarInput(beg2,beg1,end2,end1,list1.getSelectedIndex(), list1.isSelectionEmpty())){
            listModel.insertElementAt("[" + beg2 + ":" + beg1String + "," + end2 + ":" + end1String + "]", list1.getSelectedIndex());
            }
        else if (list1.isSelectionEmpty() && clientController.validateCalendarInput(beg2,beg1,end2,end1,list1.getModel().getSize(),list1.isSelectionEmpty()))
            listModel.addElement("[" + beg2 + ":" + beg1String+ "," + end2+ ":" + end1String + "]");

    }

    /**
     * Connects to server at certain ip and port.
     */
    public void connectAction(){

        String  ipInput = textField1.getText();
        if(ipInput.equals("")) {
            JOptionPane.showMessageDialog(this, "You need to put server ip!");
            return;
        }

        if(ipInput.equals("localhost"))
            ipInput = "127.0.0.1";
        clientController.getClient().setIpAddress(ipInput);

        clientController.viewSendsCalendarToModelAndRequestsFinalCalendar();
    }

    /**
     * Initializes JList model.
     */
    public void initializeListModel() {
        listModel = new DefaultListModel<String>();
        list1.setModel(listModel);
    }



    /**
     * Display dialog information that server is not running yet.
     */
    public void displayInfoServerIsNotReady() {
        JOptionPane.showMessageDialog(this, "Can not sent form, server is not ready!");
    }

    public void setSent(boolean sent){
        this.sent = sent;
    }

    /**
     * Sets view to connected state.
     */
    public void setIsConnectedTrue(){
        isConnected.setText("Connected");
        isConnected.setForeground(Color.GREEN);
    }

    /**
     * Sets view to disconnected state.
     */
    public void setIsConnectedFalse(){
        isConnected.setText("Not Connected");
        isConnected.setForeground(Color.RED);
    }

    /**
     * Clears  JList after end of session.
     */
    public void clearCalendar(){
        DefaultListModel<String> model;
        model = (DefaultListModel) list1.getModel();
        model.clear();
        revalidate();
        repaint();
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }


    /**
     * Sets new clientController.
     *
     * @param clientController New value of clientController.
     */
    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
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
     * Sets new listModel.
     *
     * @param listModel New value of listModel.
     */
    public void setListModel(DefaultListModel listModel) {
        this.listModel = listModel;
    }

    /**
     * Sets new MainPanel.
     *
     * @param MainPanel New value of MainPanel.
     */
    public void setMainPanel(JPanel MainPanel) {
        this.MainPanel = MainPanel;
    }

    /**
     * Gets sent.
     *
     * @return Value of sent.
     */
    public boolean isSent() {
        return sent;
    }

    /**
     * Gets clientController.
     *
     * @return Value of clientController.
     */
    public ClientController getClientController() {
        return clientController;
    }

    /**
     * Sets new responsePanel.
     *
     * @param responsePanel New value of responsePanel.
     */
    public void setResponsePanel(ResponsePanel responsePanel) {
        this.responsePanel = responsePanel;
    }

    /**
     * Gets responsePanel.
     *
     * @return Value of responsePanel.
     */
    public ResponsePanel getResponsePanel() {
        return responsePanel;
    }


    /**
     * Sets new shutDownButton.
     *
     * @param shutDownButton New value of shutDownButton.
     */
    public void setShutDownButton(JButton shutDownButton) {
        this.shutDownButton = shutDownButton;
    }

    /**
     * Gets connectButton.
     *
     * @return Value of connectButton.
     */
    public JButton getConnectButton() {
        return connectButton;
    }

    /**
     * Sets new removeBound.
     *
     * @param removeBound New value of removeBound.
     */
    public void setRemoveBound(JButton removeBound) {
        this.removeBound = removeBound;
    }

    /**
     * Sets new endBound.
     *
     * @param endBound New value of endBound.
     */
    public void setEndBound(JPanel endBound) {
        this.endBound = endBound;
    }

    /**
     * Sets new isConnected.
     *
     * @param isConnected New value of isConnected.
     */
    public void setIsConnected(JLabel isConnected) {
        this.isConnected = isConnected;
    }

    /**
     * Sets new spinnerEndBound1.
     *
     * @param spinnerEndBound1 New value of spinnerEndBound1.
     */
    public void setSpinnerEndBound1(JSpinner spinnerEndBound1) {
        this.spinnerEndBound1 = spinnerEndBound1;
    }

    /**
     * Gets end.
     *
     * @return Value of end.
     */
    public JLabel getEnd() {
        return end;
    }

    /**
     * Gets hrsMin2.
     *
     * @return Value of hrsMin2.
     */
    public JLabel getHrsMin2() {
        return hrsMin2;
    }

    /**
     * Gets shutDownButton.
     *
     * @return Value of shutDownButton.
     */
    public JButton getShutDownButton() {
        return shutDownButton;
    }

    /**
     * Gets hrsMin.
     *
     * @return Value of hrsMin.
     */
    public JLabel getHrsMin() {
        return hrsMin;
    }

    /**
     * Sets new end.
     *
     * @param end New value of end.
     */
    public void setEnd(JLabel end) {
        this.end = end;
    }

    /**
     * Sets new hrsMin2.
     *
     * @param hrsMin2 New value of hrsMin2.
     */
    public void setHrsMin2(JLabel hrsMin2) {
        this.hrsMin2 = hrsMin2;
    }

    /**
     * Sets new spinnerStartBound1.
     *
     * @param spinnerStartBound1 New value of spinnerStartBound1.
     */
    public void setSpinnerStartBound1(JSpinner spinnerStartBound1) {
        this.spinnerStartBound1 = spinnerStartBound1;
    }

    /**
     * Gets addBoundButton.
     *
     * @return Value of addBoundButton.
     */
    public JButton getAddBoundButton() {
        return addBoundButton;
    }

    /**
     * Gets spinnerStartBound1.
     *
     * @return Value of spinnerStartBound1.
     */
    public JSpinner getSpinnerStartBound1() {
        return spinnerStartBound1;
    }

    /**
     * Sets new connectButton.
     *
     * @param connectButton New value of connectButton.
     */
    public void setConnectButton(JButton connectButton) {
        this.connectButton = connectButton;
    }

    /**
     * Gets spinnerEndBound2.
     *
     * @return Value of spinnerEndBound2.
     */
    public JSpinner getSpinnerEndBound2() {
        return spinnerEndBound2;
    }

    /**
     * Gets startBound.
     *
     * @return Value of startBound.
     */
    public JLabel getStartBound() {
        return startBound;
    }

    /**
     * Sets new addBoundButton.
     *
     * @param addBoundButton New value of addBoundButton.
     */
    public void setAddBoundButton(JButton addBoundButton) {
        this.addBoundButton = addBoundButton;
    }

    /**
     * Gets list1.
     *
     * @return Value of list1.
     */
    public JList getList1() {
        return list1;
    }

    /**
     * Sets new spinnerStartBound2.
     *
     * @param spinnerStartBound2 New value of spinnerStartBound2.
     */
    public void setSpinnerStartBound2(JSpinner spinnerStartBound2) {
        this.spinnerStartBound2 = spinnerStartBound2;
    }

    /**
     * Gets removeBound.
     *
     * @return Value of removeBound.
     */
    public JButton getRemoveBound() {
        return removeBound;
    }

    /**
     * Gets endBound.
     *
     * @return Value of endBound.
     */
    public JPanel getEndBound() {
        return endBound;
    }

    /**
     * Gets spinnerEndBound1.
     *
     * @return Value of spinnerEndBound1.
     */
    public JSpinner getSpinnerEndBound1() {
        return spinnerEndBound1;
    }

    /**
     * Gets spinnerStartBound2.
     *
     * @return Value of spinnerStartBound2.
     */
    public JSpinner getSpinnerStartBound2() {
        return spinnerStartBound2;
    }

    /**
     * Sets new startBound.
     *
     * @param startBound New value of startBound.
     */
    public void setStartBound(JLabel startBound) {
        this.startBound = startBound;
    }

    /**
     * Sets new spinnerEndBound2.
     *
     * @param spinnerEndBound2 New value of spinnerEndBound2.
     */
    public void setSpinnerEndBound2(JSpinner spinnerEndBound2) {
        this.spinnerEndBound2 = spinnerEndBound2;
    }

    /**
     * Gets textField1.
     *
     * @return Value of textField1.
     */
    public JTextField getTextField1() {
        return textField1;
    }

    /**
     * Gets isConnected.
     *
     * @return Value of isConnected.
     */
    public JLabel getIsConnected() {
        return isConnected;
    }

    /**
     * Sets new list1.
     *
     * @param list1 New value of list1.
     */
    public void setList1(JList list1) {
        this.list1 = list1;
    }

    /**
     * Sets new hrsMin.
     *
     * @param hrsMin New value of hrsMin.
     */
    public void setHrsMin(JLabel hrsMin) {
        this.hrsMin = hrsMin;
    }

    /**
     * Sets new textField1.
     *
     * @param textField1 New value of textField1.
     */
    public void setTextField1(JTextField textField1) {
        this.textField1 = textField1;
    }
}
