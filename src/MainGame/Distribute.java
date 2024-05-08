package MainGame;

import javax.crypto.spec.DESedeKeySpec;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Distribute {
    private List<Domino> deck;
    private List<List<Domino>> hands;

    /**
     * Constructor for the domino.Distribute class.
     * Initializes the deck with a new set of dominoes from the domino.BoneYard class,
     * and prepares empty hands for both the human and computer players.
     */
    public Distribute(int numOfPlayers) {
        BoneYard boneYard = new BoneYard();
        deck = new LinkedList<>(boneYard.getDominoes());
        Collections.shuffle(deck);

        hands = new LinkedList<>();

        for (int i = 0; i < numOfPlayers; i++){
            hands.add(new LinkedList<>());
        }


        for (int i = 0; i < 7; i++){
            for (int j = 0; j < numOfPlayers; j++){
                if (!deck.isEmpty()){
                    hands.get(j).add(deck.removeFirst());
                }
            }
        }
    }




    /**
     * Returns the remaining dominoes in the deck after distribution to the players.
     *
     * @return A list of domino.Domino objects that are left in the deck.
     */
    public List<Domino> getRemainingDeck() {
        return deck;
    }

    public List<Domino> getHand(int playerIndex) {
        return hands.get(playerIndex);
    }


}
