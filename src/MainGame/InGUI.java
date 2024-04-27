package MainGame;



import MainGame.BoneYard;
import MainGame.Distribute;
import MainGame.Domino;
import MainGame.PlayYard;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class InGUI extends Application {
    private Distribute distribute;
    private BoneYard boneYard;
    private TilePane humanHandView;
    private TilePane computerHandView;
    private TilePane deckView;

    private Label winnerLabel = new Label();

    private DominoView selectedHumanDominoView; // To track the selected piece from the human side
    private TilePane playYardView; // To visually represent the play area
    private PlayYard playYard; // To manage pieces in the play area



    private boolean isHumanTurn = true;

    /**
     * Starts the JavaFX application, setting up the primary stage and initializing game components.
     *
     * @param primaryStage The primary window of the application.
     */
    @Override
    public void start(Stage primaryStage) {
        distribute = new Distribute(); // Initialize your DominoPackage.Distribute object here
        boneYard = new BoneYard();
        playYard = new PlayYard(); // Initialize DominoPackage.PlayYard


        humanHandView = new TilePane();
        computerHandView = new TilePane();
        deckView = new TilePane();

        humanHandView.setHgap(5);
        humanHandView.setVgap(5);
        computerHandView.setHgap(5);
        computerHandView.setVgap(5);

        playYardView = new TilePane();
        playYardView.setPrefRows(2);
        playYardView.setMaxWidth(200);
        playYardView.setHgap(5);
        playYardView.setVgap(5);
        playYardView.setOrientation(Orientation.HORIZONTAL);
        playYardView.setPadding(new Insets(10)); // Adjust as necessary

        deckView.setHgap(5);
        deckView.setVgap(5);
        deckView.setOrientation(Orientation.VERTICAL);
        deckView.setPrefTileWidth(100); // Adjust the width as necessary
        deckView.setPrefColumns(1); // Display the deck in a single column


        Label boneyardLabel = new Label("Boneyard");
        boneyardLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14)); // Set the label to bold


        winnerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14)); // Set the label to bold



        BorderPane root = new BorderPane();

        VBox deckContainer = new VBox(5); // 5 is the spacing between elements in the VBox
        deckContainer.setAlignment(Pos.TOP_CENTER); // Align the content to the top center
        deckContainer.getChildren().addAll(boneyardLabel, deckView);
        // Add the Boneyard label and the deck view to the VBox


        // Create VBox for human side with a label
        VBox humanSide = new VBox();
        humanSide.setAlignment(Pos.CENTER);
        humanSide.setPadding(new Insets(10));
        humanSide.getChildren().addAll(new Label("Human Side"), humanHandView);

        // Create VBox for computer side with a label
        VBox computerSide = new VBox();
        computerSide.setAlignment(Pos.CENTER);
        computerSide.setPadding(new Insets(10));
        computerSide.getChildren().addAll(new Label("Computer Side"), computerHandView);

        //root.setBottom(humanSide);
        root.setTop(computerSide);
        // Set the deck view on the right side of the BorderPane
        root.setRight(deckContainer);

        Button distributeButton = new Button("DominoPackage.Distribute");
        distributeButton.setOnAction(e -> {
            distribute.shuffleAndDistribute();
            displayHands(true); // Assuming you want to show the hands face up after distribution
            refreshDeck(); // Update the deck display
            distributeButton.setVisible(false);
        });


        Button drawFromBoneyard = new Button("DrawFromBoneYard");
        drawFromBoneyard.setOnAction(e -> {
            if (!distribute.getRemainingDeck().isEmpty()) {
                // Check if it's the start of the game by verifying if the playYard is empty
                if (playYard.getDominosInPlay().isEmpty()) {
                    System.out.println("Cannot draw from the deck at the start of the game. Please play a domino first.");
                } else {
                    // It's not the start of the game, so check if the human player has at least one playable domino
                    if (!PlayYard.hasPlaybleDomino(playYard, distribute.getHumanHand())) {
                        distribute.drawFromDeckToHumanHand(); // Draw from the deck and add to human hand
                        displayHands(true); // Update the display of hands
                        refreshDeck(); // Refresh the display of the remaining deck
                        System.out.println("A domino has been drawn from the deck.");
                    } else {
                        System.out.println("Cannot draw from the deck. You have playable dominos.");
                    }
                }
            } else {
                drawFromBoneyard.setVisible(false);
                boneyardLabel.setText("Empty Boneyard");
                System.out.println("The deck is empty."); // Or handle this in the GUI
            }
        });



        //root.setCenter(drawFromBoneyard);
        Button throwButton = new Button("Throw");
        throwButton.setOnAction(e -> {
            checkAndDisplayWinner(distribute.getHumanHand(), distribute.getComputerHand(), playYard, distribute, true);
            if (isHumanTurn && selectedHumanDominoView != null) {
                Domino humanDomino = selectedHumanDominoView.getDomino();
                int[] playDetails = new int[2]; // Array to store play details: [side, rotationNeeded]

                // Automatically determine if the domino can be played, which side, and if rotation is needed
                boolean canPlay = PlayYard.isPlayableDomino(playYard, humanDomino, playDetails);
                if (canPlay) {

                    playYard.addDomino(humanDomino, playDetails[0]); // Add domino on the determined side
                    distribute.getHumanHand().remove(humanDomino);

                    selectedHumanDominoView = null;
                    refreshDisplays();



                    isHumanTurn = false; // Switch turn to computer
                    playDominoForComputer(); // Trigger computer's turn
                }
            } else if (!isHumanTurn) {
                System.out.println("It's not your turn.");
            } else {
                System.out.println("No human domino selected.");
            }
        });




        HBox buttonBox = new HBox(10); // 10 is the spacing between elements
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT); // Align the HBox at the bottom right
        buttonBox.getChildren().addAll(distributeButton,drawFromBoneyard, throwButton);
        buttonBox.setPadding(new Insets(10)); // Add some padding for aesthetics

        // Set the HBox in the bottom region of the BorderPane
//        root.setBottom(buttonBox);

        VBox bottomSection = new VBox();
        bottomSection.getChildren().addAll(humanSide, buttonBox);
        // After initializing playYardView and other components

        VBox playArea = new VBox(10, playYardView,winnerLabel); // Adjust layout as needed
        playArea.setAlignment(Pos.CENTER);
        root.setCenter(playArea);
// Add playArea to your root layout where appropriate, or directly add playYardView if not using playArea

        root.setBottom(bottomSection);

        Scene scene = new Scene(root, 600, 300);
        primaryStage.setTitle("DominoPackage.Domino Game");
        primaryStage.setScene(scene);

        // Set the stage to full screen
        primaryStage.setFullScreen(true);

        // Disable resizing
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Refreshes the visual representation of the play yard, updating it with the current dominoes in play.
     */
    private void refreshPlayYardView() {
        playYardView.getChildren().clear();
        for (Domino domino : playYard.getDominosInPlay()) {
            DominoView dominoView = new DominoView(domino, true, false); // true for faceUp, false for isHumanPiece
            playYardView.getChildren().add(dominoView);
        }
    }

    /**
     * Refreshes all dynamic game displays, including hands, deck, and play yard, to reflect the current game state.
     */
    private void refreshDisplays() {
        // Refresh the display of hands, deck, and play yard
        displayHands(true);
        refreshDeck();
        refreshPlayYardView();
    }

    /**
     * Simulates the computer's turn, attempting to play a domino or draw from the deck as needed.
     */
    private void playDominoForComputer() {

        checkAndDisplayWinner(distribute.getHumanHand(), distribute.getComputerHand(), playYard, distribute, false);

        int[] playDetails = new int[2]; // Array to store play details: [side, rotationNeeded]
        boolean played = false;

        // Keep trying to play or draw until a domino is played or the deck is empty
        while (!played && (distribute.getRemainingDeck().size() > 0 || !distribute.getComputerHand().isEmpty())) {
            // First, try to play from the current hand
            for (Domino domino : new ArrayList<>(distribute.getComputerHand())) { // Use a copy to avoid ConcurrentModificationException
                if (PlayYard.isPlayableDomino(playYard, domino, playDetails)) {
                    playYard.addDomino(domino, playDetails[0]); // Play the domino on the determined side
                    distribute.getComputerHand().remove(domino);
                    System.out.println("Computer played: " + domino + " on the " + (playDetails[0] == 0 ? "left" : "right") + " side.");
                    played = true;
                    break; // DominoPackage.Domino was played, exit loop
                }
            }

            // If no domino was played and the deck is not empty, draw from the deck
            if (!played && distribute.getRemainingDeck().size() > 0) {
                Domino drawnDomino = distribute.drawFromDeckToComputerHand(); // Assume this method adds the domino to the computer's hand and returns it
                System.out.println("Computer drew from the deck.");
                // After drawing, attempt to play the drawn domino immediately in the next iteration
            } else if (distribute.getRemainingDeck().size() == 0) {
                // If the deck is empty and no playable domino was found, break the loop
                break;
            }
        }

        if (!played) {
            System.out.println("Computer could not play. Passing turn to human.");
        }
        refreshDisplays();


        isHumanTurn = true; // Switch turn back to human
    }

    /**
     * Checks the game state for a winner and updates the winner label with the appropriate message.
     *
     * @param humanHand         The human player's hand of dominoes.
     * @param computerHand      The computer player's hand of dominoes.
     * @param playYard          The current state of the play yard.
     * @param distribute        The distribution of dominoes between players and deck.
     * @param lastPlayerIsHuman Flag indicating if the last player to play was human.
     */
    private void checkAndDisplayWinner(List<Domino> humanHand, List<Domino> computerHand, PlayYard playYard, Distribute distribute, boolean lastPlayerIsHuman) {
        boolean hasWinner = PlayYard.checkWinner(distribute.getHumanHand(), distribute.getComputerHand(), playYard, distribute, !isHumanTurn);
        if (hasWinner) {
            String winnerMessage = "";
            if (distribute.getHumanHand().isEmpty()) {
                winnerMessage = "Human wins!";
            } else if (distribute.getComputerHand().isEmpty()) {
                winnerMessage = "Computer wins!";
            }
            boolean humanHasPlayable = playYard.hasPlaybleDomino(playYard, distribute.getHumanHand());
            boolean computerHasPlayable = playYard.hasPlaybleDomino(playYard, distribute.getComputerHand());
            boolean deckIsEmpty = distribute.getRemainingDeck().isEmpty();
            if (deckIsEmpty) {
                if (!humanHasPlayable && computerHasPlayable) {
                    winnerMessage ="Computer wins: Human has no playable domino and the deck is empty but the computer has";

                } else if (!computerHasPlayable && humanHasPlayable) {
                    winnerMessage = "Human wins: Computer has no playable domino and the deck is empty but the human has.";

                } else if (!humanHasPlayable && !computerHasPlayable) {
                    // Tiebreaker based on the total dots
                    int humanDots = PlayYard.countDot(distribute.getHumanHand());
                    int computerDots = PlayYard.countDot(distribute.getComputerHand());

                    if (humanDots < computerDots) {
                        winnerMessage = "Human wins: Lower total dots.";
                    } else if (computerDots < humanDots) {
                        winnerMessage = "Computer wins: Lower total dots.";
                    } else {
                        // Last player to play wins in case of a tie
                        if (lastPlayerIsHuman) {
                            winnerMessage= "Human wins: Last to play in case of a tie.";
                        } else {
                            winnerMessage = "Computer wins: Last to play in case of a tie.";
                        }
                    }
                }
            }
            winnerLabel.setText(winnerMessage);
        }
    }

    /**
     * Updates the display of human and computer hands on the UI.
     *
     * @param faceUp Flag indicating whether dominoes should be displayed face up (true) or face down (false).
     */
    private void displayHands(boolean faceUp) {
        humanHandView.getChildren().clear();
        computerHandView.getChildren().clear();
        // Assuming hands are displayed face up after distribution
        addDominoViewsToPane(humanHandView, distribute.getHumanHand(), faceUp);
        addDominoViewsToPane(computerHandView, distribute.getComputerHand(), faceUp);
    }

    /**
     * Refreshes the visual representation of the deck in the UI, showing how many dominoes are left to draw.
     */
    private void refreshDeck() {
        deckView.getChildren().clear();
        List<Domino> remainingDeck = distribute.getRemainingDeck(); // Implement this method in DominoPackage.Distribute
        for (Domino domino : remainingDeck) {
            // Dominoes in the deck are shown face-down and are not selectable,
            // so the isHumanPiece parameter can safely be false.
            DominoView dominoView = new DominoView(domino, false, false); // false for faceUp, false for isHumanPiece
            deckView.getChildren().add(dominoView);
        }
    }


    /**
     * Adds domino views to a specified tile pane for either human or computer hand.
     *
     * @param pane    The tile pane to add domino views to.
     * @param hand    The list of dominoes to create views for.
     * @param faceUp Indicates whether the dominoes should be displayed face up or face down.
     */
    private void addDominoViewsToPane(TilePane pane, List<Domino> hand, boolean faceUp) {
        for (Domino domino : hand) {
            boolean displayFaceUp = faceUp && hand != distribute.getComputerHand();
//            boolean displayFaceUp = faceUp ;
            boolean isHumanPiece = pane == humanHandView; // True if adding to humanHandView, false otherwise.
            DominoView dominoView = new DominoView(domino, displayFaceUp, isHumanPiece);

            if (isHumanPiece) {
                // If it's a human piece, set the mouse click event to select/deselect.
                dominoView.setOnMouseClicked(event -> {
                    // Clear previous selections
                    if (selectedHumanDominoView != null) {
                        selectedHumanDominoView.clearIndicator();
                    }
                    int[] playDetails = new int[2];
                    // Select this domino and update the static reference
                    selectedHumanDominoView = dominoView;
                    boolean isPlayable = PlayYard.isPlayableDomino(playYard, domino,playDetails);
                    dominoView.setPlayableIndicator(isPlayable);
                });
            }
            pane.getChildren().add(dominoView);
        }
    }


    /**
     * The main method to launch the JavaFX application.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
