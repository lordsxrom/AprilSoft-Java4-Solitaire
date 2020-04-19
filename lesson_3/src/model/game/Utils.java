package model.game;

import model.game.enums.Ranks;
import model.game.enums.Suits;

public class Utils {

    public static final int CARD_HEIGHT = 97;
    public static final int CARD_WIDTH = 72;
    public static final int CARD_GAP = 20;

    public static final int TABLE_HEIGHT = 700;
    public static final int TABLE_WIDTH = 1000;

    public static final int MARGIN_LEFT = 30;
    public static final int MARGIN_TOP = 15;

    public static final int SECOND_LINE_Y = 130;
    public static final int LINE_GAP_X = 110;

    public static boolean isFitToTableau(Card card_1, Card card_2) {
        Ranks rank_1 = card_1.getRank();
        Ranks rank_2 = null;

        Suits suit_1 = card_1.getSuit();
        Suits suit_2 = null;

        if (card_2 != null) {
            rank_2 = card_2.getRank();
            suit_2 = card_2.getSuit();
        }

        if (rank_1 == Ranks.KING && rank_2 == null) {
            return true;
        }

        if (card_2 != null) {
            return rank_1.ordinal() + 1 == rank_2.ordinal()
                    && ((suit_1 == Suits.DIAMONDS || suit_1 == Suits.HEARTS) && (suit_2 == Suits.SPADES || suit_2 == Suits.CLUBS) ||
                    (suit_1 == Suits.SPADES || suit_1 == Suits.CLUBS) && (suit_2 == Suits.DIAMONDS || suit_2 == Suits.HEARTS));
        }

        return false;
    }

    public static boolean isFitToFoundation(Card card_1, Card card_2) {
        Ranks rank_1 = card_1.getRank();
        Ranks rank_2 = null;

        Suits suit_1 = card_1.getSuit();
        Suits suit_2 = null;

        if (card_2 != null) {
            rank_2 = card_2.getRank();
            suit_2 = card_2.getSuit();
        }

        if (rank_1 == Ranks.ACE && card_2 == null) {
            return true;
        }

        if (card_2 != null) {
            return suit_1 == suit_2 && rank_1.ordinal() == rank_2.ordinal() + 1;
        }

        return false;
    }

    public static int findPillByCoord(int mX, int mY) {
        int nom = -1;

        if (mY >= 15 && mY <= (15 + Utils.CARD_HEIGHT)) {
            if ((mX >= 30) && (mX <= (30 + Utils.CARD_WIDTH))) nom = 0;
            if ((mX >= 140) && (mX <= (140 + Utils.CARD_WIDTH))) nom = 1;
            if ((mX >= 360) && (mX <= (360 + Utils.CARD_WIDTH))) nom = 2;
            if ((mX >= 470) && (mX <= (470 + Utils.CARD_WIDTH))) nom = 3;
            if ((mX >= 580) && (mX <= (580 + Utils.CARD_WIDTH))) nom = 4;
            if ((mX >= 690) && (mX <= (690 + Utils.CARD_WIDTH))) nom = 5;
        } else if ((mY >= 130) && (mY <= Utils.TABLE_HEIGHT)) {
            if ((mX >= 30) && (mX <= (30 + Utils.CARD_WIDTH))) nom = 6;
            if ((mX >= 140) && (mX <= (140 + Utils.CARD_WIDTH))) nom = 7;
            if ((mX >= 250) && (mX <= (250 + Utils.CARD_WIDTH))) nom = 8;
            if ((mX >= 360) && (mX <= (360 + Utils.CARD_WIDTH))) nom = 9;
            if ((mX >= 470) && (mX <= (470 + Utils.CARD_WIDTH))) nom = 10;
            if ((mX >= 580) && (mX <= (580 + Utils.CARD_WIDTH))) nom = 11;
            if ((mX >= 690) && (mX <= (690 + Utils.CARD_WIDTH))) nom = 12;
        }

        return nom;
    }

}