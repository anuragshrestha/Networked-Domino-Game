package MainGame;



import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class DominoView extends Pane {
    private static final int TILE_HEIGHT = 30;
    private static final int TILE_WIDTH = 60;
    private static final int DOT_RADIUS = 2;
    private static final int DOT_MARGIN = 5;
    private Domino domino;
    private Rectangle rect;
    private boolean isSelected = false; // Track selection state
    private static DominoView selectedDominoView = null;
    private PlayYard playYard;
    private Client client;




    /**
     * Constructor for creating a visual representation of a domino.
     * Initializes the domino piece with specified properties and sets up its visual representation.
     * @param domino The domino object this view will represent.
     */
    public DominoView(Domino domino, PlayYard playYard,Client client) {

        this.domino = domino;
        this.playYard = playYard;
        this.client = client;
        createDominoView();
        setOnMouseClicked(this::handleDominoSelected);
    }

    private void handleDominoSelected(MouseEvent event) {
        playYard.addDomino(domino, 1); // Add to PlayYard
        client.removeDominoFromHand(this.domino);
        client.refreshHandDisplay(); // Call a new method to refresh the display
    }


    /**
     * Creates the visual components of the domino and sets up event handlers.
     * This includes creating the domino's rectangle, dots (if face up), and
     * handling mouse click events for selection.
     */
    private void createDominoView() {

        rect = new Rectangle(TILE_WIDTH, TILE_HEIGHT, Color.WHITE);
        rect.setStroke(Color.BLACK);
        this.getChildren().add(rect);

        Pane leftHalf = createHalf(domino.getSide1());
        leftHalf.setLayoutX(0);

        Pane rightHalf = createHalf(domino.getSide2());
        rightHalf.setLayoutX((double) TILE_WIDTH / 2);

        Line separator = new Line((double) TILE_WIDTH / 2, 0, (double) TILE_WIDTH / 2,
                TILE_HEIGHT);
        this.getChildren().addAll(leftHalf, rightHalf, separator);
        this.setOnMouseClicked(this::handleMouseClick);
    }

    /**
     * Creates one half of the domino, displaying the appropriate number of dots based on the value.
     *
     * @param value The number of dots to display.
     * @return A Pane containing the dots in the correct pattern for the given value.
     */
    private Pane createHalf(int value) {
        Pane half = new Pane();
        half.setPrefSize((double) TILE_HEIGHT / 2, TILE_WIDTH);

        double[][] positions = getDotPositions();
        boolean[][] dotPatterns = getDotPatterns();

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


    private void handleMouseClick(MouseEvent event) {
        if (!isSelected) {
            playYard.addDomino(domino, 1); // Add to the right for now
            System.out.println("Domino added: " + domino); // Print domino added
            select();
        } else {
            deselect();
        }
    }


    public void select() {
        if (selectedDominoView != null) {
            selectedDominoView.deselect(); // Deselect previously selected domino
        }
        isSelected = true;
        selectedDominoView = this;
        setIndicator();
    }

    public void deselect() {
        isSelected = false;
        clearIndicator();
        if (selectedDominoView == this) {
            selectedDominoView = null;
        }
    }

    public void setIndicator() {
        // Apply the style directly to the rectangle
        rect.setStrokeWidth(1); // Set stroke width to 1
        rect.setStroke(Color.GREEN); // Change the color to green
    }

    public void clearIndicator() {
        // Reset the rectangle's stroke to default
        rect.setStrokeWidth(1); // Keep the line thin but visible
        rect.setStroke(Color.BLACK); // Change the color back to black
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
        double halfWidth = (double) TILE_WIDTH / 2;
        double quarterHeight = (double) TILE_HEIGHT / 4;

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
}
