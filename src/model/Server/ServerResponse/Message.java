package model.Server.ServerResponse;

import java.io.Serializable;
import java.net.Socket;

public class Message implements Serializable {


    Socket socketConnection;

    boolean active;

    public Socket getSocketConnection() {
        return socketConnection;
    }

    public void setSocketConnection(Socket socketConnection) {
        this.socketConnection = socketConnection;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}

