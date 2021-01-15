package view.Client;

import controller.Client.ClientController;
import model.Data.Time;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

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

    public ClientController clientController;

    private DefaultListModel listModel;

    private JPanel newPanel;
    private boolean sent = false;


    public ClientGUI(){
        super("Client User Interface");

        setContentPane(MainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 450);
        setAdvancedComponentSettings();

        setVisible(true);
    }

    private void setAdvancedComponentSettings(){
        setSpinner();
        initializeListModel();
        addActionListeners();
        addFocusListeners();
    }

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

                System.exit(69);
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
    //action listeners bodies

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



    public void connectAction(){

        String  ipInput = textField1.getText();
        if(ipInput.equals("")) {
            JOptionPane.showMessageDialog(this, "You need to put server ip!");
            return;
        }

        if(ipInput.equals("localhost"))
            ipInput = "127.0.0.1";
        clientController.getClient().setIpAddress(ipInput);

        clientController.viewSendsCalendarToModelAndRequestsFinalCalendar(ipInput);
    }

    //additional methods



    public void initializeListModel() {
        listModel = new DefaultListModel();
        list1.setModel(listModel);
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }

    public void setResponsePanel(ResponsePanel responsePanel) {
        this.responsePanel = responsePanel;
    }

    public ResponsePanel getResponsePanel() {
        return responsePanel;
    }

    public void displayInfoServerIsNotReady() {
        JOptionPane.showMessageDialog(this, "Can not sent form, server is not ready!");
    }

    public void setSent(boolean sent){
        this.sent = sent;
    }

    public void setIsConnectedTrue(){
        isConnected.setText("Connected");
        isConnected.setForeground(Color.GREEN);
    }

    public void setIsConnectedFalse(){
        isConnected.setText("Not Connected");
        isConnected.setForeground(Color.RED);
    }


    public void clearCalendar(){
        DefaultListModel model;
        model = (DefaultListModel) list1.getModel();
        model.clear();
        revalidate();
        repaint();
    }

    public JPanel getMainPanel() {
        return MainPanel;
    }
}
