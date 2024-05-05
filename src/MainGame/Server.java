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
    private static int maxClient = 3;
    private int clientCount = 0;
   private static int portNumber;

    public Server(int port) throws IOException {

        //this.serverSocket = serverSocket;
        serverSocket = new ServerSocket(port);
        distribute = new Distribute();
    }

    public void startServer() {

        System.out.println("Server is starting...");
        try {

            while (!serverSocket.isClosed() && clientCount < maxClient) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected. Num of clients: " + (clientCount + 1));
                if (clientCount >= maxClient) {
                    System.out.println("Max clients reached, refusing additional connections.");
                    socket.close();
                    continue;
                }

                List<Domino> clientHand = distribute.getNextHand();
                if (clientHand == null) {
                    System.out.println("No more dominoes available, refusing client connection.");
                    socket.close();
                    continue;
                }
                ClientHandler clientHandler = new ClientHandler(socket, clientHand);

                Thread thread = new Thread(clientHandler);
                thread.start();
                clientCount++;

                System.out.println("Client " + clientCount + " connected and received dominoes.");
            }
            if (clientCount == maxClient) {
                System.out.println("No more than 3 connections are accepted.");
                closeServerSocket();
            }
        } catch (IOException e) {
            System.out.println("Error: You chosed more than three clients");
            e.printStackTrace();
        }
    }


    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed.");
            }
        } catch (IOException e) {
            System.out.println("Failed to close server socket: " + e.getMessage());
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
           // promptUser();
           // ServerSocket serverSocket = new ServerSocket(portNumber);
            Server server = new Server(1024);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

