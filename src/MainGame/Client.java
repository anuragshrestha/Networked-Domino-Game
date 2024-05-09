package MainGame;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private int playerIndex; // Index to identify which hand to use

    private List<Domino> hand;
    private PlayYard playYard;
    private Distribute distribute; // Distribute object to manage hands and deck

    private Server server;
    private static String host;
    private int port = 49155;

    public Client(String username, int playerIndex) {

        this.username = username;
        this.playerIndex = playerIndex;
        this.server = server;
        this.playYard = new PlayYard(); // Initialize PlayYard


        try {
            socket = new Socket(host, port);
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(bufferedReader, bufferedWriter);
        }
    }

    public  void connectToServer(String host, int port){

        try {

            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("Connected to the server at " + host + " on " + port);
            listenForMessage();
            sendMessage();
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
            closeEverything(bufferedReader, bufferedWriter);
        }
    }

//    public void sendMessage() {
//        Scanner scanner = new Scanner(System.in);
//        try {
//            while (socket.isConnected()) {
//
//                System.out.println("Enter the index of the domino to play:");
//                String input = scanner.nextLine().trim();
//
//                try {
//                    int index = Integer.parseInt(input);
//                    if (index >= 0 && index < hand.size()) {
//                        Domino selectedDomino = this.hand.get(index);
//                        playYard.addDomino(selectedDomino, 0); // Assuming always to the right side for simplicity
//                        hand.remove(index);
//                        System.out.println("Domino played.");
//
//                        // Send a message to the server with the index of the domino played
//                        bufferedWriter.write("PLAY_DOMINO " + index);
//                        bufferedWriter.newLine();
//                        bufferedWriter.flush();
//                    } else {
//                        System.out.println("Invalid index. Please choose a valid index.");
//                    }
//                } catch (NumberFormatException e) {
//                    System.out.println("Invalid input. Please enter a numeric index.");
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        } finally {
//            scanner.close();
//        }
//    }



    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (socket.isConnected()) {
                System.out.println("Select an action: Play (P), Draw (D), or Quit (Q):");
                String action = scanner.nextLine().trim();

                if ("Q".equalsIgnoreCase(action)) {
                    System.out.println("Game ended by user.");
                    bufferedWriter.write("QUIT_GAME");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                    break;
                }

                if ("D".equalsIgnoreCase(action)) {
                    if (!playYard.hasPlaybleDomino(playYard,hand)) {
                        // Assuming the server handles the draw and sends back the new domino and state
                        bufferedWriter.write("DRAW_DOMINO");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        System.out.println("You have drawn from the deck. Please select a piece to play:");
                    } else {
                        System.out.println("You have a playable piece. You cannot draw from the deck now.");
                        continue;
                    }
                }

                if ("P".equalsIgnoreCase(action)) {
                    System.out.println("Your hand: " + hand);
                    System.out.println("Select an index of piece to play:");
                    String indexInput = scanner.nextLine().trim();

                    if (indexInput.matches("\\d+")) {
                        int index = Integer.parseInt(indexInput);
                        if (index >= 0 && index < hand.size()) {
                            Domino selectedDomino = hand.get(index);

                            System.out.println("Add to Left [L] or Right [R]?");
                            String sideChoice = scanner.nextLine().trim();
                            int side = "L".equalsIgnoreCase(sideChoice) ? 0 : 1;

                            System.out.println("Do you want to rotate the domino? (yes/no):");
                            boolean rotated = "yes".equalsIgnoreCase(scanner.nextLine().trim());
                            if (rotated) {
                                selectedDomino.rotate();
                            }

                            if (playYard.hasPlaybleDomino(playYard,hand)) {
                                playYard.addDomino(selectedDomino, side);
                                hand.remove(index);
                                bufferedWriter.write("PLAY_DOMINO " + index + " " + side + " " + rotated);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                System.out.println("Domino played.");
                            } else {
                                if (rotated) { // Rotate back if not playable
                                    selectedDomino.rotate();
                                }
                                System.out.println("This domino cannot be played. Please select another one or draw from the deck.");
                            }
                        } else {
                            System.out.println("Invalid index. Please select a valid piece from your hand.");
                        }
                    } else {
                        System.out.println("Invalid input. Please enter a numeric index.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }


    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    if (messageFromServer != null) {
                        if (messageFromServer.startsWith("GAME_STATE:")) {
                            System.out.println(messageFromServer.replace("GAME_STATE: ", ""));
                        } else if (messageFromServer.startsWith("INIT_HAND")) {
                            handleInitHand(messageFromServer.replace("INIT_HAND ", ""));
                        } else {
                            System.out.println(messageFromServer);
                        }
                    } else {
                        throw new IOException("Server closed the connection.");
                    }
                } catch (IOException e) {
                    System.out.println("Lost connection to server. Closing everything...");
                    closeEverything(bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start();
    }

    private void handleInitHand(String handData) {
        hand = parseHand(handData);
        System.out.println("Received initial hand from server: " + hand);
    }

    private List<Domino> parseHand(String handData) {
        List<Domino> parsedHand = new ArrayList<>();
        String[] dominoStrings = handData.split(";");
        for (String dominoStr : dominoStrings) {
            String[] sides = dominoStr.split(",");
            if (sides.length == 2) {
                try {
                    int side1 = Integer.parseInt(sides[0]);
                    int side2 = Integer.parseInt(sides[1]);
                    parsedHand.add(new Domino(side1, side2));
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing domino data: " + e.getMessage());
                }
            }
        }
        return parsedHand;
    }







    private void closeEverything(BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the server's hostname:");
         host = scanner.nextLine();
        System.out.println("Enter your username:");
        String username = scanner.nextLine();
        System.out.println("Enter your player index (0 or 1 for testing):");
        int playerIndex = Integer.parseInt(scanner.nextLine()); // This is typically managed by the server


            Client client = new Client(username, playerIndex);
            client.connectToServer(host, client.port);

            scanner.close();

    }
}
