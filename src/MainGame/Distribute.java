package MainGame;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Distribute {
    private List<Domino> deck;
    private List<Domino> humanHand;
    private List<Domino> computerHand;

    /**
     * Constructor for the Distribute class.
     * Initializes the deck with a new set of dominoes from the BoneYard class,
     * and prepares empty hands for both the human and computer players.
     */
    public Distribute() {
        BoneYard boneYard = new BoneYard();
        deck = new LinkedList<>(boneYard.getDominoes());
        humanHand = new LinkedList<>();
        computerHand = new LinkedList<>();
    }


    /**
     * Shuffles the deck and distributes an equal number of dominoes to both the human and computer hands.
     * Each player receives 7 dominoes, simulating the start of a standard domino game.
     */
    public void shuffleAndDistribute() {
        Collections.shuffle(deck);
        for (int i = 0; i < 7; i++) {
            humanHand.add(deck.remove(0));
            computerHand.add(deck.remove(0));
        }
    }

    /**
     * Returns the current set of dominoes in the human player's hand.
     *
     * @return A list of Domino objects representing the human's hand.
     */
    public List<Domino> getHumanHand() {
        return humanHand;
    }

    /**
     * Returns the current set of dominoes in the computer player's hand.
     *
     * @return A list of Domino objects representing the computer's hand.
     */
    public List<Domino> getComputerHand() {
        return computerHand;
    }


    /**
     * Returns the remaining dominoes in the deck after distribution to the players.
     *
     * @return A list of Domino objects that are left in the deck.
     */
    public List<Domino> getRemainingDeck() {
        return deck; // Assuming 'deck' contains the undistributed dominoes
    }


    /**
     * Draws a domino from the deck and adds it to the human player's hand.
     * Also, prints the drawn domino and the current size of the human hand.
     */
    public void drawFromDeckToHumanHand() {
        if (!deck.isEmpty()) {
            Domino drawnDomino = deck.remove(0); // Draw the first domino from the deck
            humanHand.add(drawnDomino); // Add it to the human hand
            System.out.println("Human drew: " + drawnDomino); // Print the domino drawn by the human
            System.out.println("Human hand: " + humanHand );
        }
        System.out.println("Cards remaining in deck: " + deck.size());
        // Print the number of cards remaining in the deck
    }

    /**
     * Draws a domino from the deck and adds it to the computer player's hand.
     * Prints the new size of the computer's hand and the remaining dominoes in the deck.
     *
     * @return null. This method could be modified to return the drawn Domino for additional functionality.
     */
    public Domino drawFromDeckToComputerHand() {
        if (!deck.isEmpty()) {
            Domino drawnDomino = deck.remove(0); // Draw the first domino from the deck
            computerHand.add(drawnDomino); // Corrected to add it to the computer hand
            System.out.println("Computer have "+ computerHand.size() + " pieces");
        }
        System.out.println("Cards remaining in deck: " + deck.size());
        // Print the number of cards remaining in the deck, only print this for the computer's draw as well
        return null;
    }
}

