package MainGame;



import java.util.Objects;

public class Domino {
    private int side1;
    private int side2;
    private int offsetY;


    /**
     * Constructor for the domino.Domino class. Initializes a domino with specified side values.
     * An optional offsetY is initialized for potential use in graphical representations or
     * game logic requiring vertical positioning.
     *
     * @param side1 The value of the first side of the domino.
     * @param side2 The value of the second side of the domino.
     */
    public Domino(int side1, int side2) {
        this.side1 = side1;
        this.side2 = side2;
        this.offsetY = 0; // Default value, can be changed as needed
    }

    /**
     * Rotates the domino, swapping the values of side1 and side2.
     * This method can be useful for games where the orientation of the domino matters.
     */
    public void rotate() {
        int temp = this.side1;
        this.side1 = this.side2;
        this.side2 = temp;
    }


    /**
     * Gets the value of side1 of the domino.
     *
     * @return The value of side1.
     */
    public int getSide1() {
        return side1;
    }


    /**
     * Gets the value of side2 of the domino.
     *
     * @return The value of side2.
     */
    public int getSide2() {
        return side2;
    }



    /**
     * Checks if this domino is equal to another object.
     * Two dominoes are considered equal if they have the same side1 and side2 values, regardless of their order.
     *
     * @param obj The object to compare this domino against.
     * @return true if the given object represents a domino.Domino equivalent to this domino, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Domino)) return false;
        Domino other = (Domino) obj;
        return side1 == other.side1 && side2 == other.side2;
    }


    /**
     * Generates a hash code for a domino.Domino object.
     * The hash code is calculated using both sides of the domino, ensuring
     * that the order of sides does not affect the hash code.
     *
     * @return A hash code value for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(side1, side2);
    }


    /**
     * Returns a string representation of the domino.
     * This method provides a textual representation, showing the values of both sides separated by a "|".
     *
     * @return A string representation of the domino.
     */
    @Override
    public String toString() {
        return side1 + "|" + side2;
    }
}
