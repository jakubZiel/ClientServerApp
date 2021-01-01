package model.Server.ServerResponse;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ServerActiveResponder extends Thread {

        private DataOutputStream dataOutputS;

        private BlockingQueue queue;
        private boolean active;

        public void run(){

                Message message;

                while (true){

                        try {
                                message = (Message) queue.take();

                                if (active)
                                        message.setActive(true);
                                else
                                        message.setActive(false);

                                sendBack(message);

                        } catch (InterruptedException e) {
                                e.printStackTrace();
                        }

                }
        }

        public void sendBack(Message msg){
                try {
                        dataOutputS = new DataOutputStream(msg.getSocketConnection().getOutputStream());
                        dataOutputS.writeBoolean(msg.isActive());
                        dataOutputS.flush();

                } catch (IOException e) {
                        e.printStackTrace();
                }

        }

        public void setQueue(BlockingQueue queue) {
                this.queue = queue;
        }

        public void setActive(boolean active) {
                this.active = active;
        }

        public static void main(String[] args) {
                ServerActiveReceiver receiver = new ServerActiveReceiver();
                ServerActiveResponder responder = new ServerActiveResponder();
                responder.setActive(true);
                responder.setQueue(receiver.getQueue());

                responder.start();
                receiver.start();

                try {
                        responder.join();
                } catch (InterruptedException e) {
                        e.printStackTrace();
                }
        }
}
