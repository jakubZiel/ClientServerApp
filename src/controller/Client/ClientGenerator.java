package controller.Client;

public class ClientGenerator {

    public static void main(String[] args) {

            if (args.length != 1) {
                System.out.println("Wrong number of arguments, should be 1 and be a number");
                return;
            }

            try {
                int numberOfClients = Integer.parseInt(args[0]);

                Client[] clients = new Client[numberOfClients];

                for (Client client : clients) {
                    client = new Client("192.168.1.110");
                    client.start();
                }
            } catch (NumberFormatException e){
                System.out.println("Argument is not a number!!!");
            }
    }
}
