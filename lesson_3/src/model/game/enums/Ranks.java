package model.game.enums;

public enum Ranks {
    ACE,
    TWO,
    THREE,
    FOUR,
    FIVE,
    SIX,
    SEVEN,
    EIGHT,
    NINE,
    TEN,
    JACK,
    QUEEN,
    KING;

    public static boolean isFitToTableau(Ranks rank_1, Ranks rank_2) {
        return rank_1.ordinal() + 1 == rank_2.ordinal();
    }

    public static boolean isFitToFoundation(Ranks rank_1, Ranks rank_2) {
        return rank_1.ordinal() == rank_2.ordinal() + 1;
    }

}
