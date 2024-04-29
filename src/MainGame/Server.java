package MainGame;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Server {

    private ServerSocket serverSocket;
    private Distribute distribute;
    private static int maxClient;
   private static int portNumber;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.distribute = new Distribute();
        distribute.shuffleAndDistribute();
    }

    public void startServer() {

        try {
            int clientCount = 0;
            while (!serverSocket.isClosed() && clientCount < maxClient) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");
                List<Domino> clientHand = (clientCount == 0) ?
                        distribute.getHumanHand() : distribute.getComputerHand();
                ClientHandler clientHandler = new ClientHandler(socket, clientHand);

                Thread thread = new Thread(clientHandler);
                thread.start();
                clientCount++;
            }
            if (clientCount == 3) {
                System.out.println("No more than 3 connections are accepted.");
                closeServerSocket();
            }
        } catch (IOException e) {
            System.out.println("You chosed more than three clients");
            e.printStackTrace();
        }
    }


    public void closeServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void promptUser() throws IOException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("How many human players (client) do you want? Select up to 3");
        maxClient = Integer.parseInt(scanner.nextLine());
        System.out.println("What is your port number?");
        portNumber = Integer.parseInt(scanner.nextLine());
        System.out.println("You chosed port number: " + portNumber);
        try {
            Properties prop = new Properties();
            prop.setProperty("portNumber", String.valueOf(portNumber));
            prop.store(new FileOutputStream("config.properties"), null);
        } catch (IOException e) {
            System.out.println("Error while writing to the properties file.");
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {

        try {
            promptUser();
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

