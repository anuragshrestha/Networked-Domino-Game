package MainGame;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private ServerSocket serverSocket;
    public  List<ClientHandler> clientHandlers = new ArrayList<>();
    private int currentPlayerIndex = 0;
    private Distribute distribute;


    private static int maxClient = 2;
    private PlayYard playYard;
    private Scanner scanner;
    private Random random;
    private ClientHandler clientHandler;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.playYard = new PlayYard();
    }

    public void startServer() {

        scanner = new Scanner(System.in);
        random = new Random();

        System.out.println("Server started. Waiting for players...");

        try {
            while (!serverSocket.isClosed() && clientHandlers.size() < maxClient) {
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");

                if (clientHandlers.isEmpty()) {
                    distribute = new Distribute(maxClient);
                }

                List<Domino> clientHand = distribute.getHand(clientHandlers.size());
                boolean lastClient = (clientHandlers.size() + 1 == maxClient);
                clientHandler = new ClientHandler(socket, this, clientHand, lastClient);
                clientHandlers.add(clientHandler);
                System.out.println(" The size of client handlers is: " + clientHandlers.size());
                new Thread(clientHandler).start();
                if (clientHandlers.size() == maxClient) {
                    startGame();
                }

            }

        } catch (IOException e) {
            System.out.println("An error occurred while accepting new clients: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public synchronized void addDominoToPlayYard(Domino domino, int side) {
        playYard.addDomino(domino, side);
        broadcastGameState(); // Broadcast updated PlayYard after adding domino
    }

    public void broadcastGameState() {
        String state =  playYard.displayPlayYard();
        for (ClientHandler client : clientHandlers) {
            client.sendMessageToClient(state);
        }
    }


    private void startGame() {
        System.out.println("All players are connected. Starting the game...");
        broadcastMessage("Game has started. Good luck!");

        ClientHandler currentPlayer = clientHandlers.get(currentPlayerIndex);
        processPlayerMove(currentPlayer);

    }







    public void processPlayerMove(ClientHandler player) {

        if (clientHandlers.get(currentPlayerIndex) != player) {
            player.sendMessageToClient("It's not your turn.");
            return;
        }


        currentPlayerIndex += 1;
    }






    private void broadcastMessage(String message) {
        for (ClientHandler client : clientHandlers) {
            client.sendMessageToClient(message);
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
        System.out.println("How many human players (client) do you want? Select up to 2");
        maxClient = Integer.parseInt(scanner.nextLine());
    }

    public List<ClientHandler> clientHandlers() {
        return clientHandlers;
    }



    public static void main(String[] args) {

        try {
            promptUser();
            int portNumber = 49155;
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Server server = new Server(serverSocket);
            server.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



