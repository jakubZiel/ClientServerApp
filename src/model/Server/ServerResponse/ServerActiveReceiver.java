package model.Server.ServerResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ServerActiveReceiver extends Thread{

    private Socket connectionSocket;
    private ServerSocket serverSocket;

    private BlockingQueue queue;

    public ServerActiveReceiver(){
        this.queue = new ArrayBlockingQueue(50);
    }

    public  void run(){

        try {
            serverSocket = new ServerSocket(6868);

            while(true) {

                try {
                    Message request = receiveRequest();
                    queue.put(request);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Message receiveRequest() throws IOException {

        connectionSocket = serverSocket.accept();

        Message task = new Message();
        task.setSocketConnection(connectionSocket);

        return task;

    }

    public BlockingQueue getQueue( ) {
        return this.queue;
    }
}
