package MainGame;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Server {

    private ServerSocket serverSocket;
    private Distribute distribute;
    private static final int MAX_CLIENTS= 2;
    private List<Socket> clientSockets = new ArrayList<>();
   private static int portNumber;


    private PlayYard playYard = new PlayYard(v -> {
        System.out.println("PlayYard state changed");
    });

    public Server(int portNumber) throws IOException {

        serverSocket = new ServerSocket(portNumber);
        distribute = new Distribute();
    }

    public void startServer() {

        System.out.println("Server is starting...");
        System.out.println("Waiting for clients to join...");
        try {

            while (!serverSocket.isClosed() && clientSockets.size() < MAX_CLIENTS) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected. Num of clients: " + (clientSockets.size() + 1));
                clientSockets.add(socket);

                // Check if the maximum number of clients have connected
                if (clientSockets.size() == MAX_CLIENTS) {
                    System.out.println("Max clients reached, distributing dominoes.");
                    for (Socket clientSocket : clientSockets) {
                        List<Domino> clientHand = distribute.getNextHand();
                        if (clientHand == null) {
                            System.out.println("No more dominoes available, closing connection.");
                            clientSocket.close();
                            continue;
                        }
                        ClientHandler clientHandler = new ClientHandler(socket, clientHand, playYard);

                        Thread thread = new Thread(clientHandler);
                        thread.start();
                        ClientHandler.clientHandlers.add(clientHandler);
                        System.out.println("Client " + ClientHandler.clientHandlers.size() +
                                " connected and received dominoes.");
                    }
                    closeServerSocket();
                }
            }
        }
        catch(IOException e){
            System.out.println("An error occurred while accepting a connection: " + e.getMessage());
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

    public static void main(String[] args) {

        try {
            Server server = new Server(1024);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

