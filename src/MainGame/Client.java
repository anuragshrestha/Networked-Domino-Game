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
import java.util.*;

import javafx.scene.control.TextInputDialog;


public class Client extends Application {
    private Socket socket;

    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    private TilePane playYardView = new TilePane(Orientation.HORIZONTAL);
    private PlayYard playYard;
    private HBox dominoPane = new HBox(10);  // Horizontal layout for dominoes
    private Label statusLabel = new Label("Attempting to connect...");
    private List<Domino> clientHand = new ArrayList<>();



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

    public static void main(String[] args)  {

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
        playYardView.setStyle("-fx-border-color: blue; -fx-border-width: 2px;");

        // Label below the play yard
        Label playYardLabel = new Label("Below is the PlayYard");
        playYardLabel.setPadding(new Insets(10));

        VBox playArea = new VBox(10, playYardLabel, playYardView);
        playArea.setAlignment(Pos.CENTER);

        // Status and dominoes pane at the top
        VBox topArea = new VBox(10, statusLabel, dominoPane);
        topArea.setAlignment(Pos.CENTER);

        root.setTop(topArea);
        root.setCenter(playArea);
        Scene scene = new Scene(root, 800, 600);
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
            while (!connected && attempts < 2) {
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
                        List<Domino> dominoes = new ArrayList<>(); // Simulate receiving dominoes
                        updateDominoes(dominoes);
                        Thread.sleep(3000); // Wait 5 seconds before retrying
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }).start();
    }
    private void updateDominoes(List<Domino> dominoes) {

        for (Domino domino : dominoes) {
            DominoView dominoView = new DominoView(domino, playYard, this);  // here
            dominoPane.getChildren().add(dominoView);
        }
    }

    public void removeDominoFromHand(Domino domino) {

        clientHand.remove(domino);
        updateDominoes(new ArrayList<>(clientHand));
    }

    public void refreshHandDisplay() {

        dominoPane.getChildren().clear();  // Clear the visual display of dominoes
        for (Domino domino : clientHand) {
            dominoPane.getChildren().add(new DominoView(domino, playYard, this));
        }
    }

    public void updatePlayYardView(Void unused) {
        playYardView.getChildren().clear();
        for (Domino domino : playYard.getDominosInPlay()) {
            DominoView dominoView = new DominoView(domino, playYard, this);
            playYardView.getChildren().add(dominoView);
        }
    }

    public List<Domino> getClientHand() {
        return new ArrayList<>(clientHand);
    }
}


