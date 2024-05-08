package MainGame;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

public class DominoView extends Pane {
    private static final int TILE_WIDTH = 30;
    private static final int TILE_HEIGHT = 60;
    private static final int DOT_RADIUS = 2;

    private Domino domino;
    private Rectangle rect; // Reference to the domino rectangle for styling
    private boolean isSelected = false;
    private static DominoView selectedDominoView = null;

    private PlayYard playYard;

    private Client client;

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

    private void createDominoView() {
        rect = new Rectangle(TILE_HEIGHT, TILE_WIDTH, Color.WHITE);
        rect.setStroke(Color.BLACK);
        this.getChildren().add(rect);

        Pane leftHalf = createHalf(domino.getSide1());
        leftHalf.setLayoutX(0);

        Pane rightHalf = createHalf(domino.getSide2());
        rightHalf.setLayoutX(TILE_HEIGHT / 2);

        Line separator = new Line(TILE_HEIGHT / 2, 0, TILE_HEIGHT / 2, TILE_WIDTH);
        this.getChildren().addAll(leftHalf, rightHalf, separator);

        this.setOnMouseClicked(this::handleMouseClick);
    }


    private void handleDominoSelected() {
        playYard.addDomino(domino, 1); // Assume side is always 1 for this example
        client.removeDominoFromHand(domino);
        client.updateDominoes(client.getClientHand()); // Refresh client's hand display
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
        setIndicator(); // Apply green border
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

    private Pane createHalf(int value) {
        Pane half = new Pane();
        half.setPrefSize(TILE_HEIGHT / 2, TILE_WIDTH);

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

    private double[][] getDotPositions() {
        double halfWidth = TILE_HEIGHT / 2;
        double quarterHeight = TILE_WIDTH / 4;
        return new double[][] {
                {halfWidth / 2, quarterHeight * 2}, {halfWidth / 2, quarterHeight}, {halfWidth / 2, quarterHeight * 3},
                {quarterHeight, quarterHeight}, {quarterHeight * 3, quarterHeight},
                {quarterHeight, quarterHeight * 3}, {quarterHeight * 3, quarterHeight * 3}
        };
    }

    private boolean[][] getDotPatterns() {
        return new boolean[][] {
                {false, false, false, false, false, false, false}, {true, false, false, false, false, false, false},
                {false, true, true, false, false, false, false}, {true, true, true, false, false, false, false},
                {false, true, true, true, true, false, false}, {true, true, true, true, true, false, false},
                {false, true, true, true, true, true, true}
        };
    }


}

