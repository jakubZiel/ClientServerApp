package view.Client;

import controller.Client.ClientController;
import model.Data.Time;

import javax.swing.*;
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

    public ClientController clientController;

    private DefaultListModel listModel;

    private JPanel newPanel;

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
                connectAction();
            }
        });

        addBoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
    }

    //action listeners bodies

    public  void addBoundToSet(){

        String ipAddress = textField1.getText();
        int beg2 = (int)spinnerStartBound1.getValue();
        int end2 = (int)spinnerEndBound1.getValue();
        int beg1 = (int)spinnerStartBound2.getValue();
        int end1 = (int)spinnerEndBound2.getValue();

        Time t = new Time(beg2, beg1, end2, end1);

        if (t.end < t.beg) {
            JOptionPane.showMessageDialog(this, "Wrong bound format. (Beginning is later than ending)");
            return;
        }

        double last;

        if(clientController.getClient().calendar.size() == 0) {
            last = t.end;
            listModel.addElement("[" + beg2 + ":" + beg1+ "," + end2+ ":" + end1 + "]");
            clientController.getClient().calendar.add(t);
            return;
        }
        else last = clientController.getClient().calendar.get(clientController.getClient().calendar.size()-1).end;

        if(last > t.beg || last > t.end){
        }else {
            clientController.getClient().calendar.add(t);
            listModel.addElement("[" + beg2 + ":" + beg1+ "," + end2+ ":" + end1 + "]");
        }
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


}
