package MainGame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
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

    private TilePane playYardView = new TilePane(Orientation.HORIZONTAL);
    private PlayYard playYard;
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
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Domino Client");
        BorderPane root = new BorderPane();
        playYard = new PlayYard(this::updatePlayYardView);

        playYardView = new TilePane();
        playYardView.setPrefRows(2);
        playYardView.setMaxWidth(200);
        playYardView.setHgap(5);
        playYardView.setVgap(5);
        playYardView.setOrientation(Orientation.HORIZONTAL);
        playYardView.setPadding(new Insets(10));

        // Add a status label to the pane for connection status updates
        dominoPane.getChildren().add(statusLabel);

        VBox playArea = new VBox(10, playYardView); // Adjust layout as needed
        playArea.setAlignment(Pos.CENTER);
        root.setCenter(playArea);
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
                        statusLabel.setText("");
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

        for (Domino domino : dominoes) {
            DominoView dominoView = new DominoView(domino, playYard);
            dominoPane.getChildren().add(dominoView);
        }
    }

    private void updatePlayYardView(Void unused) {
        playYardView.getChildren().clear();
        for (Domino domino : playYard.getDominosInPlay()) {
            DominoView dominoView = new DominoView(domino, playYard);
            playYardView.getChildren().add(dominoView);
        }
    }
}


