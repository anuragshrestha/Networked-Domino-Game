package MainGame;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;


    private String username;

    public  Client (Socket socket, String username) {
        try {

            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;

        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(){
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username +  messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();

            }
        }
        catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenFromMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromGroupChat;

                while(socket.isConnected()){
                    try{
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);

                    }
                    catch (IOException e){
                        closeEverything(socket, bufferedReader, bufferedWriter);

                    }
                }

            }
        }).start();

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){

        ClientHandler.closeClient(socket, bufferedReader, bufferedWriter);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Prompt for the hostname
            System.out.println("Please enter the hostname:");
            String hostname = scanner.nextLine();
            // Prompt for the username
            System.out.println("Please enter your user name:");
            String username = scanner.nextLine();
            Socket socket = new Socket(hostname, 49155);
            Client client = new Client(socket, username);
            client.listenFromMessage();
            client.sendMessage();
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid port number format. Please enter a valid number.");
        } catch (IOException e) {
            System.out.println("Error connecting to the server. Please check the hostname and port number.");
        } finally {
            scanner.close();
        }
    }

}


