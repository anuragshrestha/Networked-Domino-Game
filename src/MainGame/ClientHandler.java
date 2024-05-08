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
            System.out.println("client handler list is " + clientHandlerList.size());
            isLastClient = lastClient;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.consoleReader = new BufferedReader(new InputStreamReader(System.in));
            this.clientUsername = bufferedReader.readLine();
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat.");

            if (!isLastClient) {
                sendMessageToClient("Welcome " + clientUsername + "! Waiting for other players to join...");
            } else {
                sendMessageToClient("Welcome " + clientUsername + "! Preparing to start the game...");
            }
            sendMessageToClient("Your initial hand of dominoes: " + hand.toString());
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }



    // need to fix this method
    public void promptPlayerToMove(int currentPlayerIndex) {

        ClientHandler currentPlayer = clientHandlerList
                .get(currentPlayerIndex);
        if (clientHandlerList.get(currentPlayerIndex) != currentPlayer) {
            currentPlayer.sendMessageToClient("It's not your turn.");
            return;
        }
        int handSize = currentPlayer.getHandSize();
        currentPlayer.sendMessageToClient("Your turn to play. Available moves: Play (P) or Quit (Q).");

        try {


            String action = consoleReader.readLine();

            if ("Q".equalsIgnoreCase(action)) {
                System.out.println("Game ended by user.");
                System.exit(0);
            }


            if ("P".equalsIgnoreCase(action)) {

                sendMessageToClient("Select an index to play: ");
                action = scanner.nextLine();


                if (action.matches("\\d+")) {
                    int index = Integer.parseInt(action);
                    if (handSize == 0) {
                        currentPlayer.sendMessageToClient("You have no dominos left to play.");
                    } else if (index >= 0 && index < handSize) {

                        Domino selectedDomino = currentPlayer.getDomino(index);

                        sendMessageToClient("Add to Left [L] or Right [R]?");
                        String sideChoice = consoleReader.readLine();
                        int side = "L".equalsIgnoreCase(sideChoice) ? 0 : 1;

                        // Ask if the human wants to rotate the domino before trying to match
                        sendMessageToClient("Do you want to rotate the domino? " +
                                "(yes/no): ");
                        String rotateChoice = consoleReader.readLine();
                        boolean rotated = false;
                        if ("yes".equalsIgnoreCase(rotateChoice)) {
                            selectedDomino.rotate();
                            rotated = true;
                        }

                        boolean canPlay = false;
                        if (playYard.getDominosInPlay().isEmpty()) {
                            canPlay = true;
                        } else {
                            // Check if the selected domino's side is a wildcard or matches the chain end
                            boolean isWildcard1 = selectedDomino.getSide2() == 0;
                            boolean isWildcard2 = selectedDomino.getSide1() == 0;
                            if (side == 0) { // Left side
                                canPlay = isWildcard1 || selectedDomino.getSide2() == playYard.getChainEnds()[0];
                            } else if (side == 1) {
                                canPlay = isWildcard2 || selectedDomino.getSide1() == playYard.getChainEnds()[1];
                            }
                        }

                        if (!canPlay && rotated) {
                            // Rotate back to original orientation if it doesn't match
                            selectedDomino.rotate();
                        }

                        if (canPlay) {
                            playYard.addDomino(selectedDomino, side);
                            currentPlayer.removeDomino(index);
                            printPlayed(selectedDomino, playYard, side, currentPlayer.getClientUsername());
                        } else {
                            sendMessageToClient("This domino cannot be played. " +
                                    "Please select another one or " +
                                    "draw from the deck.");

                        }
                    } else {
                        currentPlayer.sendMessageToClient("Invalid index. Please select a valid piece" +
                                " from your hand.");
                    }
                }
            }
        } catch (IOException e) {
            sendMessageToUser("Error reading from the console.");
        }
    }

    private void printPlayed(Domino domino, PlayYard playYard, int sideValue, String player) {

        sendMessageToUser("You played: " + domino + " on the " +
                (sideValue == 0 ? "left" : "right") + " side.");
        broadcastMessage(player + " played: " + domino + " on the " +
                (sideValue == 0 ? "left" : "right") + " side.");
    }

    @Override
    public void run() {
        String messageFromClient;


        try {
            while (socket.isConnected()) {
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient == null) throw new IOException("domino.Client disconnected");
                //server.processPlayerMove(messageFromClient, this);
                broadcastMessage(messageFromClient);
            }
        }catch (IOException e) {
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

    public void sendMessageToUser(String messageToSend) {

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

    public void sendMessageToClient(String message) {
        try {
            bufferedWriter.write(message);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
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

    public String getClientUsername() {
        return clientUsername;
    }

    public int getHandSize() {
        return this.hand.size();
    }

    public Domino getDomino(int index) {
        if (index >= 0 && index < this.hand.size()) {
            return this.hand.get(index);
        }
        return null;
    }
    public void removeDomino(int index) {
        if (index >= 0 && index < this.hand.size()) {
            this.hand.remove(index);
        }
    }

}

