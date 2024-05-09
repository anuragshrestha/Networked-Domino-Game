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

