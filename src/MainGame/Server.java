

package MainGame;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private ServerSocket serverSocket;
    private Distribute distribute;
    private int clientCount = 0;
    private static final int MAX_CLIENTS = 2;

    private List<Socket> clientSockets = new ArrayList<>();

    List<Domino> clientHand;


    private PlayYard playYard = new PlayYard(v -> {
        System.out.println("PlayYard state changed");
    });



    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        distribute = new Distribute();
    }

    public void startServer() {
        System.out.println("Server is starting...");
        try {
            while (!serverSocket.isClosed() && clientSockets.size() < MAX_CLIENTS) {
                Socket socket = serverSocket.accept();  // Accept new connections
                System.out.println("Client connection attempt: " + (clientSockets.size() + 1));
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
                        ClientHandler clientHandler = new ClientHandler(clientSocket, clientHand, playYard);
                        new Thread(clientHandler).start();
                        ClientHandler.clientHandlers.add(clientHandler);
                        System.out.println("Client " + ClientHandler.clientHandlers.size() +
                                " connected and received dominoes.");
                    }

                    closeServerSocket();  // Close server socket as no more clients should be accepted
                }
            }
        } catch (IOException e) {
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
            int port = 1234;
            Server server = new Server(port);
            server.startServer();
        } catch (IOException e) {
            System.out.println("Failed to start the server: " + e.getMessage());
        }
    }
}

