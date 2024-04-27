package MainGame;



import MainGame.Domino;
import MainGame.PlayYard;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class DominoView extends Pane {
    private static final int TILE_WIDTH = 30;
    private static final int TILE_HEIGHT = 60;
    private static final int DOT_RADIUS = 2;
    private static final int DOT_MARGIN = 5;
    private boolean faceUp;
    private Domino domino;
    private boolean isHumanPiece; // Flag to indicate if this is a human piece

    private static Domino lastPlayedDomino; // Reference to the last played domino (you need to manage this)

    private boolean isSelected = false; // Track selection state
    private static DominoView selectedDominoView = null;





    /**
     * Constructor for creating a visual representation of a domino.
     * Initializes the domino piece with specified properties and sets up its visual representation.
     *
     * @param domino The domino object this view will represent.
     * @param faceUp Determines if the domino should be displayed face up or face down.
     * @param isHumanPiece Indicates if this domino belongs to the human player, affecting interaction capabilities.
     */
    public DominoView(Domino domino, boolean faceUp, boolean isHumanPiece) {
        this.domino = domino; // Initialize the domino field
        this.faceUp = faceUp;
        this.isHumanPiece = isHumanPiece;

        createDominoView();
    }


    /**
     * Creates the visual components of the domino and sets up event handlers.
     * This includes creating the domino's rectangle, dots (if face up), and handling mouse click events for selection.
     */
    private void createDominoView() {
        // Adjust width and height for horizontal layout
        Rectangle rect = new Rectangle(TILE_HEIGHT, TILE_WIDTH, faceUp ? Color.WHITE : Color.GRAY); // Note the swapped dimensions
        rect.setStroke(Color.BLACK);
        this.getChildren().add(rect);

        if (faceUp) {
            Pane leftHalf = createHalf(domino.getSide1()); // Adjust to create left half
            leftHalf.setLayoutX(0);

            Pane rightHalf = createHalf(domino.getSide2()); // Adjust to create right half
            rightHalf.setLayoutX(TILE_HEIGHT / 2);

            Line separator = new Line(TILE_HEIGHT / 2, 0, TILE_HEIGHT / 2, TILE_WIDTH); // Vertical line for separation
            this.getChildren().addAll(leftHalf, rightHalf, separator);
        }

        // Handle mouse click events for selection
        this.setOnMouseClicked(event -> {
            if (isHumanPiece && !isSelected) {
                if (selectedDominoView != null) {
                    selectedDominoView.deselect();
                }
                select();
            }
        });
    }

    /**
     * Creates one half of the domino, displaying the appropriate number of dots based on the value.
     *
     * @param value The number of dots to display.
     * @return A Pane containing the dots in the correct pattern for the given value.
     */
    private Pane createHalf(int value) {
        Pane half = new Pane();
        half.setPrefSize(TILE_HEIGHT / 2, TILE_WIDTH); // Set the size for each half

        double[][] positions = getDotPositions(); // Get the dot positions
        boolean[][] dotPatterns = getDotPatterns(); // Get the dot patterns for the value

        for (int i = 0; i < positions.length; i++) {
            if (dotPatterns[value][i]) {
                Circle dot = new Circle(DOT_RADIUS);
                dot.setFill(Color.BLACK);
                dot.setCenterX(positions[i][0]);
                dot.setCenterY(positions[i][1]);
                half.getChildren().add(dot);
            }
        }
        return half;
    }

    /**
     * Sets a visual indicator on the domino to show if it is playable.
     *
     * @param playable If true, the domino border is set to green, otherwise to red.
     */
    public void setPlayableIndicator(boolean playable) {
        if (playable) {
            // Set the border to green
            this.setStyle("-fx-border-color: green; -fx-border-width: 2;");
        } else {
            // Set the border to red
            this.setStyle("-fx-border-color: red; -fx-border-width: 2;");
        }
    }


    /**
     * Clears any visual indicator from the domino, removing border styling.
     */
    public void clearIndicator() {
        // Remove any border styling
        this.setStyle("-fx-border-color: none;");
    }

    /**
     * Returns the Domino object associated with this DominoView.
     *
     * @return The Domino object.
     */

    public Domino getDomino() {
        return this.domino;
    }


    /**
     * Updates the appearance of the domino view based on its selection status and playability.
     *
     * @param isPlayable Indicates whether the domino is playable.
     */
    private void updateAppearance(boolean isPlayable) {
        Rectangle rect = (Rectangle) this.getChildren().get(0); // Assuming the first child is the rectangle
        if (isSelected) {
            rect.setStroke(isPlayable ? Color.GREEN : Color.RED); // Green if playable, red if not
            rect.setStrokeWidth(3); // Thicker border for selection
        } else {
            rect.setStroke(Color.BLACK);
            rect.setStrokeWidth(1); // Normal border for deselection
        }
    }

    /**
     * Calculates the positions for the dots on a domino tile.
     * This method is designed to work with a graphical representation of a domino,
     * placing the dots in specific positions depending on the value of each half of the domino.
     * The positions are calculated based on the dimensions of the domino tile.
     *
     * @return A two-dimensional array of double values representing the x and y coordinates for each dot position.
     */
    private double[][] getDotPositions() {
        double halfWidth = TILE_HEIGHT / 2; // The full width of each half
        double quarterHeight = TILE_WIDTH / 4; // The quarter height of the domino piece

        // Define the dot positions for one half (either left or right)
        return new double[][] {
                // Center dot
                {halfWidth / 2, quarterHeight * 2},
                // Top center dot
                {halfWidth / 2, quarterHeight},
                // Bottom center dot
                {halfWidth / 2, quarterHeight * 3},
                // Top left dot
                {quarterHeight, quarterHeight},
                // Top right dot
                {quarterHeight * 3, quarterHeight},
                // Bottom left dot
                {quarterHeight, quarterHeight * 3},
                // Bottom right dot
                {quarterHeight * 3, quarterHeight * 3}
        };
    }


    /**
     * Provides patterns for placing dots on each half of the domino based on its value.
     * The method defines a boolean matrix where each row corresponds to a value (0 to 6) on the domino,
     * and each column represents a potential dot position.
     * A true value indicates that a dot should be placed in that position.
     *
     * @return A two-dimensional boolean array representing the pattern of dots for each value.
     */
    private boolean[][] getDotPatterns() {
        return new boolean[][] {
                {false, false, false, false, false, false, false}, // 0
                {true, false, false, false, false, false, false}, // 1
                {false, true, true, false, false, false, false}, // 2
                {true, true, true, false, false, false, false}, // 3
                {false, true, true, true, true, false, false}, // 4
                {true, true, true, true, true, false, false}, // 5
                {false, true, true, true, true, true, true}, // 6
        };
    }


    /**
     * Deselects this domino view, updating its appearance to indicate it is not selected.
     * This method is called when a user clicks on a different domino or when the selection is cleared programmatically.
     * It also manages the static reference to the currently selected domino view, ensuring that only one domino can be selected at a time.
     */
    public void deselect() {
        isSelected = false;
        updateAppearance(false); // When deselected, the appearance is updated without considering playability
        if (selectedDominoView == this) {
            selectedDominoView = null;
        }
    }

    /**
     * Selects this domino view, updating its appearance to indicate it is selected.
     * This method is called when a user clicks on this domino view. It sets the domino as selected
     * and updates the static reference to track this as the currently selected domino view.
     * Ensures that any previously selected domino is deselected.
     */
    public void select() {
        isSelected = true;
        selectedDominoView = this;
    }
}
