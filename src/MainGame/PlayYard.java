package MainGame;

import java.util.ArrayList;
import java.util.List;

public class PlayYard {

    private List<Domino> dominosInPlay;
    private StringBuilder topRowString = new StringBuilder();
    private StringBuilder botRowString = new StringBuilder();
    private int leftCount = 0;  // Tracks the number of dominos on the left
    private int rightCount = 0; // Tracks the number of dominos on the right



    //private static final int MAX_PER_LINE = 1; // or any other number based on your requirement
    private int[] chainEnds = new int[2]; // Stores the current ends of the domino chain

    /**
     * Constructs a new PlayYard object.
     * Initializes an empty list to store dominoes currently in play.
     */
    public PlayYard() {
        this.dominosInPlay = new ArrayList<>();

    }



    /**
     * Adds a domino to the play yard either on the left or right side, based on the specified side.
     * Adjusts the visual representation of the play yard accordingly.
     *
     * @param domino The domino to add.
     * @param side   The side to add the domino on (0 for left, 1 for right).
     */
    public synchronized void addDomino(Domino domino, int side) {
        // If it's the first domino, simply add it to the play yard
        if (dominosInPlay.isEmpty()) {

            dominosInPlay.add(domino);
            topRowString.append("[").append(domino).append("]");
            return;
        }

        // Add the domino to the correct side
        if (side == 0) {
            dominosInPlay.add(0, domino);
            if (leftCount % 2 == 0) {
                botRowString.insert(0, "[" + domino + "]");
            } else {
                topRowString.insert(0, "[" + domino + "]");
            }
            leftCount++;
        } else if (side == 1) {
            dominosInPlay.add(domino);
            if (rightCount % 2 == 0) {
                botRowString.append("[" + domino + "]");

            } else {
                topRowString.append("[" + domino + "]");

            }
            rightCount++;
        }
        updateEnds();
    }

    /**
     * Updates the values of the chain ends based on the first and last dominoes in play.
     */

    private void updateEnds() {
        if (!dominosInPlay.isEmpty()) {
            // Update leftmost end based on the first domino in the list
            Domino firstDomino = dominosInPlay.get(0);
            chainEnds[0] = firstDomino.getSide1(); // Assume the first side of the first domino as the leftmost end

            // Update rightmost end based on the last domino in the list
            Domino lastDomino = dominosInPlay.get(dominosInPlay.size() - 1);
            chainEnds[1] = lastDomino.getSide2(); // Assume the second side of the last domino as the rightmost end
        } else {
            chainEnds[0] = -1; // Reset ends when playyard is empty
            chainEnds[1] = -1;
        }
    }


    /**
     * Returns the list of dominoes currently in play.
     *
     * @return A list of Domino objects in the play yard.
     */
    public ArrayList<Domino> getDominosInPlay() {
        return (ArrayList<Domino>) dominosInPlay;
    }

    /**
     * Retrieves the current ends of the domino chain.
     *
     * @return An array containing the values at the ends of the domino chain.
     */
    public int[] getChainEnds() {
        return chainEnds;
    }

    /**
     * Prints the current state of the play yard, showing dominoes in play and their arrangement.
     *
     * @return
     */

    public String displayPlayYard() {
        StringBuilder display = new StringBuilder();
        if (dominosInPlay.isEmpty()) {
            display.append("Play yard is empty.");
        } else {
            if (leftCount % 2 == 0) {
                display.append(topRowString).append("\n").append("  ").append(botRowString);
            } else {
                display.append("  ").append(topRowString).append("\n").append(botRowString);
            }
        }

        return display.toString();
    }


    public  boolean hasPlaybleDomino(PlayYard playYard, List<Domino> hand) {
        int[] chainEnds = playYard.getChainEnds(); // Retrieve the current chain ends from the play yard

        for (Domino domino : hand) {
            boolean isWildcardSide1 = domino.getSide1() == 0;
            boolean isWildcardSide2 = domino.getSide2() == 0;

            // Check if either side of the domino matches the chain ends or is a wildcard
            if (isWildcardSide1 || isWildcardSide2 || // If either side is a wildcard
                    domino.getSide1() == chainEnds[0] || // Side 1 matches left end
                    domino.getSide1() == chainEnds[1] || // Side 1 matches right end
                    domino.getSide2() == chainEnds[0] || // Side 2 matches left end
                    domino.getSide2() == chainEnds[1]) { // Side 2 matches right end
                return true; // There is at least one domino that can be played
            }
        }
        return false; // No playable domino found
    }


    public static boolean isPlayableDomino(PlayYard playYard, Domino domino, int[] playDetails) {

        int[] chainEnds = playYard.getChainEnds(); // Retrieve the current chain ends from the play yard

        if (playYard.getDominosInPlay().isEmpty()) {
            playDetails[0] = 0; // Arbitrarily choose a side, e.g., 0 for left
            playDetails[1] = 0; // No rotation needed
            return true; // Any domino can be played if it's the first piece
        }


        boolean isWildcardSide1 = domino.getSide1() == 0;
        boolean isWildcardSide2 = domino.getSide2() == 0;

        // Check if domino can be played without rotation
        boolean canPlayLeft = isWildcardSide2 || domino.getSide2() == chainEnds[0];
        boolean canPlayRight = isWildcardSide1 || domino.getSide1() == chainEnds[1] ;

        if (canPlayLeft || canPlayRight) {
            // Determine the side to play on and if rotation is needed
            if (canPlayLeft) {
                playDetails[0] = 0; // Side 0 for left
                playDetails[1] = domino.getSide1() == chainEnds[0] || isWildcardSide1 ? 0 : 1;
                // Rotation needed if playing side 2 on left
            } else {
                playDetails[0] = 1; // Side 1 for right
                playDetails[1] = domino.getSide2() == chainEnds[1] || isWildcardSide2 ? 0 : 1;
                // Rotation needed if playing side 1 on right
            }
            return true;
        }

        // If not playable in current orientation, check if rotation makes it playable
        domino.rotate(); // Temporarily rotate to check
        isWildcardSide1 = domino.getSide1() == 0;
        isWildcardSide2 = domino.getSide2() == 0;
        canPlayLeft = isWildcardSide2 || domino.getSide2() == chainEnds[0] ;
        canPlayRight = isWildcardSide1 || domino.getSide1() == chainEnds[1] ;

        if (canPlayLeft || canPlayRight) {
            // Determine the side to play on after rotation
            if (canPlayLeft) {
                playDetails[0] = 0; // Side 0 for left
                playDetails[1] = 1; // Rotation was performed
            } else {
                playDetails[0] = 1; // Side 1 for right
                playDetails[1] = 1; // Rotation was performed
            }
            return true;
        } else {
            domino.rotate(); // Rotate back to original orientation if not playable
            return false;
        }
    }


    public static boolean checkWinner(List<Domino> humanHand, List<Domino> computerHand,
                                      PlayYard playYard, Distribute distribute, boolean lastPlayerIsHuman) {
        // Check if either player has won by emptying their hand
        if (humanHand.isEmpty()) {
            System.out.println("Human wins: Human has no more dominos.");
            return true;
        } else if (computerHand.isEmpty()) {
            System.out.println("Computer wins: Computer has no more dominos.");
            return true;
        }

        // Check for playable dominos and if the deck is empty
        boolean humanHasPlayable = playYard.hasPlaybleDomino(playYard, humanHand);
        boolean computerHasPlayable = playYard.hasPlaybleDomino(playYard, computerHand);
        boolean deckIsEmpty = distribute.getRemainingDeck().isEmpty();

        if (deckIsEmpty) {
            if (!humanHasPlayable && computerHasPlayable) {
                System.out.println("Computer wins: Human has no playable domino and the deck is empty" +
                        " and the computer has the Playbale");
                return true;
            } else if (!computerHasPlayable && humanHasPlayable) {
                System.out.println("Human wins: Computer has no playable domino and " +
                        "the deck is empty but the human has");
                return true;
            } else if (!humanHasPlayable && !computerHasPlayable) {
                // Tiebreaker based on the total dots
                int humanDots = countDot(humanHand);
                int computerDots = countDot(computerHand);

                if (humanDots < computerDots) {
                    System.out.println("Human wins: Lower total dots.");
                } else if (computerDots < humanDots) {
                    System.out.println("Computer wins: Lower total dots.");
                } else {
                    // Last player to play wins in case of a tie
                    if (lastPlayerIsHuman) {
                        System.out.println("Human wins: Last to play in case of a tie.");
                    } else {
                        System.out.println("Computer wins: Last to play in case of a tie.");
                    }
                }
                return true;
            }
        }
        return false;
    }


    /**
     * Counts the total number of dots in a player's hand.
     *
     * @param hand The player's hand to count dots in.
     * @return The total number of dots in the hand.
     */
    public static int countDot(List<Domino> hand) {
        int totalDots = 0;
        for (Domino domino : hand) {
            totalDots += domino.getSide1() + domino.getSide2();
        }
        return totalDots;
    }









}
