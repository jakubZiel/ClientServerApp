package model.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class CheckingConnection {

    private String ip;
    private int port;

    private Socket connectionSocket;

    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;

    boolean serverActive;

    public boolean checkIfConnectionAvailable(){
        try {

            connectionSocket = new Socket(ip,port);

            dataInputStream = new DataInputStream(connectionSocket.getInputStream());
            dataOutputStream = new DataOutputStream(connectionSocket.getOutputStream());

            connectionSocket.setSoTimeout(2500);

            dataOutputStream.writeBoolean(false);

            serverActive = dataInputStream.readBoolean();

            dataInputStream.close();
            dataOutputStream.close();
            connectionSocket.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return serverActive;

    }

    public CheckingConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        CheckingConnection check = new CheckingConnection("127.0.0.1", 6868);
        System.out.println(check.checkIfConnectionAvailable());
    }
}
