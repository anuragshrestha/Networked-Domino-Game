package MainGame;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ClientHandler implements Runnable {


    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private Server server;
    private List<Domino> hand;
    private   int count = 0;
    private boolean isLastClient;
    private Scanner scanner;
    private Random random;
    private PlayYard playYard;
    private BufferedReader consoleReader;
    private  List<ClientHandler> clientHandlerList ;

    public ClientHandler(Socket socket, Server server, List<Domino> hand, boolean lastClient) {

        try {

            this.socket = socket;
            this.server = server;
            this.hand = hand;
            this.playYard = new PlayYard();
            scanner = new Scanner(System.in);
            random = new Random();
            clientHandlerList = server.clientHandlers();
            //System.out.println("client handler list is " + clientHandlerList.size());
            isLastClient = lastClient;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
            this.clientUsername = bufferedReader.readLine();
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat.");

            sendInitialHand(hand);  // Send the initial hand to the client


        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    private void processDominoPlay(int dominoIndex) {
        // Process the domino play, update game state
        Domino dominoPlayed = hand.remove(dominoIndex);
        server.addDominoToPlayYard(dominoPlayed,0);

    }

    private void processDominoRotation(int dominoIndex) {
        if (dominoIndex >= 0 && dominoIndex < hand.size()) {
            Domino domino = hand.get(dominoIndex);
            domino.rotate();
            System.out.println("Domino at index " + dominoIndex + " rotated: " + domino);
        } else {
            System.out.println("Invalid index for domino rotation.");
        }
    }



    private void sendInitialHand(List<Domino> hand) {
        StringBuilder handBuilder = new StringBuilder();
        for (Domino domino : hand) {
            handBuilder.append(domino.getSide1()).append(",").append(domino.getSide2()).append(";");
        }
        try {
            bufferedWriter.write("INIT_HAND " + handBuilder.toString());
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error sending initial hand to client: " + e.getMessage());
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }





    @Override
    // ClientHandler.java - Part of the server that handles individual clients
    public void run() {
        String messageFromClient;

        try {
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient.startsWith("PLAY_DOMINO")) {
                    int index = Integer.parseInt(messageFromClient.split(" ")[1]);
                    System.out.println("the index of domino selected is: " + index);
                    processDominoPlay(index);
                } else if(messageFromClient.startsWith("Rotate-Domino")){
                    int index = Integer.parseInt(messageFromClient.split(" ")[1]);
                    processDominoRotation(index);
                }
            }
        } catch (IOException e) {
            System.out.println("Client " + clientUsername + " disconnected unexpectedly.");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }





    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : server.clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void sendMessageToClient(String messageToSend) {

        for (ClientHandler clientHandler : clientHandlerList) {
            try {
                if (clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }




    public void removeClientHandler() {

        broadcastMessage("SERVER: " + clientUsername + " has left the chat.");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        clientHandlerList.remove(this);
        closeClient(socket, bufferedReader, bufferedWriter);
    }

    public static void closeClient(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

