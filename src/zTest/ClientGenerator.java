package zTest;

import Client.ClientController;

public class ClientGenerator {

    public static void main(String[] args) {

            if (args.length != 1) {
                System.out.println("Wrong number of arguments, should be 1 and be a number");
                return;
            }

            int counter = 0;

            try {
                int numberOfClients = Integer.parseInt(args[0]);

                ClientController[] clients = new ClientController[numberOfClients];

                for (ClientController client : clients) {
                    client = new ClientController();
                    client.moveGUILocation(40 * counter, 40 * counter);
                    counter++;
                }
            } catch (NumberFormatException e){
                System.out.println("Argument is not a number!!!");
            }
    }
}
