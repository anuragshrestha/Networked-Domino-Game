package MainGame;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BoneYard {
    private final List<Domino> dominoes = new ArrayList<>();

    /**
     * Constructor for the domino.BoneYard class.
     * Initializes the boneyard with a set of dominoes ranging from [0,0] to [6,6].
     * Each domino is represented only once, ensuring a standard double-six domino set is created.
     */
    public BoneYard() {
        for (int i = 0; i <= 6; i++) {
            for (int j = i; j <= 6; j++) {
                dominoes.add(new Domino(i, j));
            }
        }
    }


    /**
     * This method is called from different class to
     * draw random dominos from the Boneyard
     *
     * @return random domino
     */
    public Domino drawDomino() {
        if (!dominoes.isEmpty()) {
            int index = new Random().nextInt(dominoes.size());
            return dominoes.get(index);
        }
        return null;
    }

    /**
     * Provides an unmodifiable view of the dominoes in the boneyard.
     * This ensures that the original list cannot be modified directly, preserving the integrity of the boneyard.
     *
     * @return An unmodifiable list of domino.Domino objects.
     */
    public List<Domino> getDominoes() {

        return Collections.unmodifiableList(dominoes);
    }


    /**
     * Shuffles the dominoes in the boneyard to ensure a random order.
     * This method uses Collections.shuffle to randomly permute the list of dominoes.
     */
    public void shuffle() {
        Collections.shuffle(dominoes);
    }

}
