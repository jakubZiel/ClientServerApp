package view.Server;

import model.Server.Server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


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
    private Server CurrentServer;

    private JList ClientList;
    private DefaultListModel listModel;

    public ServerGUI(){
        super("Server User Interface");
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(600,450);

        initializeListModel();
        addActionListeners();

        setVisible(true);
    }

    //action listeners
    public void addActionListeners(){

        TurnOff.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Server terminated");
                System.exit(69);
            }
        });

        startServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { FormHasBeenFinished(); }
        });
    }

    //additional methods
    public void initializeListModel(){
        listModel = new DefaultListModel();
        ClientList.setModel(listModel);
    }

    public void refreshClientList(String string){
        listModel.addElement(string);
    }

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

        if(CurrentServer != null) {
            CurrentServer.setNumberOfClients(NumberOfClientPara);
            CurrentServer.setMeetingLength(meetingLength);
        }
        synchronized (CurrentServer) {
            CurrentServer.notify();
        }
    }

    private boolean isNumberOfClientsOK(int result){
        return result > 0 && result < 10;
    }

    public void setCurrentServer(Server currentServer) {
        CurrentServer = currentServer;
    }
}
