package structures.basic;


public class Hand {
    private String suit;
    private int value;

    public void Card(String suit, int value) {
        this.suit = suit;
        this.value = value;
    }

    public String getSuit() {
        return suit;
    }

    public int getValue() {
        return value;
    }
}
