package model.game.enums;

public enum Suits {
    DIAMONDS,
    CLUBS,
    HEARTS,
    SPADES;

    public static boolean isSame(Suits suit_1, Suits suit_2) {
        return suit_1 == suit_2;
    }

    public static boolean isSimilar(Suits suit_1, Suits suit_2) {
        return (suit_1 == Suits.DIAMONDS || suit_1 == Suits.HEARTS) && (suit_2 == Suits.DIAMONDS || suit_2 == Suits.HEARTS) ||
                (suit_1 == Suits.SPADES || suit_1 == Suits.CLUBS) && (suit_2 == Suits.SPADES || suit_2 == Suits.CLUBS);
    }

}
