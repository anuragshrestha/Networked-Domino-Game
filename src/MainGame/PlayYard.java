package MainGame;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayYard {

    private List<Domino> dominosInPlay = new ArrayList<>();
    private List<Consumer<Domino>> observers = new ArrayList<>();
    private Consumer<Void> onChange; // Callback to trigger GUI update
    private StringBuilder topRowString = new StringBuilder();
    private StringBuilder botRowString = new StringBuilder();
    private int leftCount = 0;  // Tracks the number of dominos on the left
    private int rightCount = 0; // Tracks the number of dominos on the right


    //private static final int MAX_PER_LINE = 1; // or any other number based on your requirement
    private int[] chainEnds = new int[2];

    /**
     * Constructs a new PlayYard object.
     * Initializes an empty list to store dominoes currently in play.
     */
    public PlayYard(Consumer<Void> onChange) {
        this.onChange = onChange;
    }

    public void addObserver(Consumer<Domino> observer) {
        observers.add(observer);
    }

    private void notifyObservers(Domino domino) {
        for (Consumer<Domino> observer : observers) {
            observer.accept(domino);
        }
    }

    /**
     * Adds a domino to the play yard either on the left or right side, based on the specified side.
     * Adjusts the visual representation of the play yard accordingly.
     *
     * @param domino The domino to add.
     * @param side   The side to add the domino on (0 for left, 1 for right).
     */
    public void addDomino(Domino domino, int side) {

        dominosInPlay.add(domino);
        System.out.println("Domino added to PlayYard: " + domino);

        notifyObservers(domino);
        if (onChange != null) {
            onChange.accept(null);
        }

    }




    public List<Domino> getDominosInPlay() {
        return dominosInPlay;
    }

}



