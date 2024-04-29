package MainGame;

import java.io.*;
import java.net.Socket;
import java.util.Properties;
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

    public static void main(String[] args) throws IOException {

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("config.properties"));
            int portNumber = Integer.parseInt(prop.getProperty("portNumber"));
            System.out.println("You chosed port number: " + portNumber);
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please enter your name:");
            String username = scanner.nextLine();
            Socket socket = new Socket("localhost", portNumber);
            Client client = new Client( socket, username);
            client.listenFromMessage();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println("Error reading from the properties file.");
            e.printStackTrace();
        }
    }
 }


