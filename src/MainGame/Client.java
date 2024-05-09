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

    private List<Domino> hand = new ArrayList<>(); // List to hold the client's hand of dominos

    private PlayYard playYard;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Send the username to the server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (socket.isConnected()) {
                System.out.println("Enter 'P' to play, 'D' to draw, or 'Q' to quit:");
                String command = scanner.nextLine().trim().toUpperCase();

                switch (command) {
                    case "P":
                        playDomino(scanner);
                        break;
                    case "D":
                        drawDomino();
                        break;
                    case "Q":
                        bufferedWriter.write("QUIT");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                        System.out.println("Quitting the game...");
                        return;
                    default:
                        System.out.println("Invalid input. Please enter 'P' to play, 'D' to draw, or 'Q' to quit.");
                        break;
                }
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } finally {
            scanner.close();
        }
    }

    private void playDomino(Scanner scanner) throws IOException {
        System.out.println("Choose an index of domino to play:");

        String index = scanner.nextLine().trim();

        if (index.matches("\\d+")) {
            bufferedWriter.write("PLAY " + index);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            System.out.println("Add to Left [L] or Right [R]?");
            String side = scanner.nextLine().trim().toUpperCase();
            bufferedWriter.write("SIDE " + side);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            System.out.println("Do you want to rotate the domino? (Yes/No):");
            String rotate = scanner.nextLine().trim().toUpperCase();
            bufferedWriter.write("ROTATE " + rotate);
            Domino selectedDomino = hand.get(Integer.parseInt(index));
            playYard.addDomino(selectedDomino, Integer.parseInt(side));



            bufferedWriter.newLine();
            bufferedWriter.flush();
        } else {
            System.out.println("Invalid index. Please enter a valid number.");
        }
    }

    private void drawDomino() throws IOException {
        bufferedWriter.write("DRAW");
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    public void listenForMessage() {
        new Thread(() -> {
            while (socket.isConnected()) {
                try {
                    String messageFromServer = bufferedReader.readLine();
                    System.out.println(messageFromServer);
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the server's hostname:");
        String hostname = scanner.nextLine();
        System.out.println("Enter your username:");
        String username = scanner.nextLine();

        try {
            Socket socket = new Socket(hostname, 49155); // Port number should be consistent with server settings
            Client client = new Client(socket, username);
            client.listenForMessage();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println("Error connecting to the server: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
