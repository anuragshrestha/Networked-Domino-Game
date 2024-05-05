package MainGame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javafx.scene.control.TextInputDialog;
import java.util.Optional;


public class Client extends Application {
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private HBox dominoPane = new HBox(10);  // Horizontal layout for dominoes
    private Label statusLabel = new Label("Attempting to connect...");


    private String username;

//    public  Client (Socket socket, String username) {
//        try {
//
//            this.socket = socket;
//            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            this.username = username;
//
//        } catch (IOException e) {
//            closeEverything(socket, bufferedReader, bufferedWriter);
//        }
//    }

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
           // closeEverything(socket, bufferedReader, bufferedWriter);
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
//                        closeEverything(socket, bufferedReader, bufferedWriter);

                    }
                }

            }
        }).start();

    }

//    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
//
//        ClientHandler.closeClient(socket, bufferedReader, bufferedWriter);
//    }

    public static void main(String[] args)  {

//        Properties prop = new Properties();
//        try {
//            prop.load(new FileInputStream("config.properties"));
//            int portNumber = Integer.parseInt(prop.getProperty("portNumber"));
//            System.out.println("You chosed port number: " + portNumber);
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Please enter your name:");
//            String username = scanner.nextLine();
//            Socket socket = new Socket("localhost", portNumber);
//            Client client = new Client( socket, username);
//            client.listenFromMessage();
//            client.sendMessage();
//        } catch (IOException e) {
//            System.out.println("Error reading from the properties file.");
//            e.printStackTrace();
//        }

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("Domino Client");

        // Add a status label to the pane for connection status updates
        dominoPane.getChildren().add(statusLabel);

        Scene scene = new Scene(dominoPane, 800, 200);
        primaryStage.setScene(scene);
        primaryStage.show();

        String serverAddress = promptForServerAddress();

        // Connect to the server and setup dominoes asynchronously
        connectToServer(serverAddress, 1024);
    }

    private String promptForServerAddress() {
        TextInputDialog dialog = new TextInputDialog("localhost");
        dialog.setTitle("Connect to Server");
        dialog.setHeaderText("Server Connection");
        dialog.setContentText("Please enter the server address:");

        Optional<String> result = dialog.showAndWait();
//        if (result.isPresent()) {
//            return result.get();
//        } else {
//            return "localhost";  // Default server address
//        }
        return result.get();
    }


    private void connectToServer(String host, int port) {
        new Thread(() -> {
            boolean connected = false;
            int attempts = 0;
            while (!connected && attempts < 10) {  // Try to connect up to 10 times
                try (Socket socket = new Socket(host, port);
                     ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                    List<Domino> dominoes = (List<Domino>) objectInputStream.readObject();
                    Platform.runLater(() -> {
                        updateDominoes(dominoes);
                        statusLabel.setText("Connected and dominoes received!");
                    });
                    connected = true;

                } catch (IOException | ClassNotFoundException e) {
                    attempts++;
                    String errMsg = "Failed to connect or receive data (Attempt " + attempts + "): " + e.getMessage();
                    System.err.println(errMsg);
                    Platform.runLater(() -> statusLabel.setText(errMsg));
                    try {
                        Thread.sleep(5000); // Wait 5 seconds before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }
    private void updateDominoes(List<Domino> dominoes) {
        dominoPane.getChildren().remove(statusLabel);  // Clear the status label before displaying dominoes
        for (Domino domino : dominoes) {
            DominoView dominoView = new DominoView(domino);
            dominoPane.getChildren().add(dominoView);
        }
    }
}


