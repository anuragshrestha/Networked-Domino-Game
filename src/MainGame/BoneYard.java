package MainGame;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoneYard {
    private final List<Domino> dominoes = new ArrayList<>();

    /**
     * Constructor for the BoneYard class.
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
     * Provides an unmodifiable view of the dominoes in the boneyard.
     * This ensures that the original list cannot be modified directly, preserving the integrity of the boneyard.
     *
     * @return An unmodifiable list of Domino objects.
     */
    public List<Domino> getDominoes() {

        return Collections.unmodifiableList(dominoes);
    }




}

