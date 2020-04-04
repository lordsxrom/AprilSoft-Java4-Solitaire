package model;

import model.game.Card;
import model.game.Pile;
import model.game.Utils;
import model.game.enums.Piles;
import model.game.enums.Ranks;
import model.game.enums.States;
import model.game.enums.Suits;
import presenter.ModelListener;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;


public class Model implements IModel {

    private ModelListener listener;

    private ArrayList<Pile> piles;
    public States state;

    public Model() {
        createPiles();
    }

    private void createPiles() {
        piles = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            Pile pile = new Pile();
            switch (i) {
                case 0: {
                    pile.setType(Piles.STOCK);
                    break;
                }
                case 1: {
                    pile.setType(Piles.TALON);
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5: {
                    pile.setType(Piles.FOUNDATION);
                    break;
                }
                default: {
                    pile.setType(Piles.TABLEAU);
                    break;
                }
            }
            piles.add(pile);
        }
    }

    public void start() {
        piles.forEach(Pile::clear);

        dial(getCardPack());

        state = States.GAME;

        // level 3
//        nomKarti = -1;
//        nomStopki = -1;
        listener.updateTableImage(drawTableImage());
    }

    private ArrayList<Card> getCardPack() {
        ArrayList<Card> pack = new ArrayList<>();
        for (Ranks rank : Ranks.values()) {
            for (Suits suit : Suits.values()) {
                pack.add(new Card(suit, rank));
            }
        }
        return pack;
    }

    private void dial(ArrayList<Card> pack) {
        for (int i = 6; i < 13; i++) {
            for (int j = 6; j <= i; j++) {
                int rnd = (int) (Math.random() * pack.size());
                Card card = pack.get(rnd);
                if (j < i)
                    card.setFaced(true);
                piles.get(i).add(card);
                pack.remove(card);
            }
        }

        Collections.shuffle(pack);
        piles.get(0).addAll(pack);
    }

    private BufferedImage drawCard(Card card) {
        return card.isFaced() ? card.getFace() : card.getBack();
    }

    private BufferedImage drawPill(Pile pile) {
        BufferedImage img = null;
        switch (pile.getType()) {
            case STOCK:
            case TALON:
            case FOUNDATION: {
                if (!pile.isEmpty()) {
                    Card card = pile.get(pile.size() - 1);
                    img = drawCard(card);
                } else {
//                    img = new BufferedImage(Utils.CARD_WIDTH, Utils.CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
//                    Graphics g = img.getGraphics();
//                    g.drawImage(img, 0, 0, Color.GRAY, null);
                }
                break;
            }
            case TABLEAU: {
                if (!pile.isEmpty()) {
                    img = new BufferedImage(Utils.CARD_WIDTH, (pile.size() - 1) * 30 + Utils.CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
                    Graphics g = img.getGraphics();
                    int y = 0;
                    for (Card card : pile.getCards()) {
                        g.drawImage(drawCard(card), 0, y, null);
                        y = y + 30;
                    }
                } else {
//                    img = new BufferedImage(Utils.CARD_WIDTH, Utils.CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
//                    Graphics g = img.getGraphics();
//                    g.drawImage(null, 0, 0, Color.GRAY, null);
                }
                break;
            }
        }

        return img;
    }

    private BufferedImage drawTableImage() {
        BufferedImage img = new BufferedImage(Utils.TABLE_WIDTH, Utils.TABLE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();

        int x = 30;
        int y = 15;
        for (int i = 0; i < piles.size(); i++) {
            g.drawImage(drawPill(piles.get(i)), x, y, null);
            x = x + 72 + 38;
            if (i == 2) x = x + 72 + 38;
            if (i == 5) {
                y = 130;
                x = 30;
            }
        }

        return img;
    }

    @Override
    public void setListener(ModelListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMouseDragged(int mX, int mY) {

    }

    @Override
    public void onMouseReleased(int mX, int mY) {

    }

    @Override
    public void onMousePressed(int mX, int mY) {
        int pillNumber = findPillByCoord(mX, mY);
        if (pillNumber == -1) return;

        Pile pile = piles.get(pillNumber);
        if (pile.getType() == Piles.STOCK) {
            if (!pile.isEmpty()) {
                Card card = pile.get(pile.size() - 1);
                card.setFaced(true);
                piles.get(1).add(card);
                pile.remove(card);
            } else {
                Pile talon =  piles.get(1);
                ArrayList<Card> cards = talon.getCards();
                Collections.reverse(cards);
                cards.forEach(it -> it.setFaced(false));
                pile.addAll(cards);
                talon.clear();
            }
        }

        listener.updateTableImage(drawTableImage());
    }

    @Override
    public void onMouseDoublePressed(int mX, int mY) {

    }

    @Override
    public void onStart() {
        start();
    }

    private int findPillByCoord(int mX, int mY) {
        int nom = -1;

        if ((mY >= 15) && (mY <= (15 + 97))) {
            if ((mX >= 30) && (mX <= (30 + 72))) nom = 0;
            if ((mX >= 140) && (mX <= (140 + 72))) nom = 1;
            if ((mX >= 360) && (mX <= (360 + 72))) nom = 2;
            if ((mX >= 470) && (mX <= (470 + 72))) nom = 3;
            if ((mX >= 580) && (mX <= (580 + 72))) nom = 4;
            if ((mX >= 690) && (mX <= (690 + 72))) nom = 5;
        } else if ((mY >= 130) && (mY <= 700)) {
            if ((mX >= 30) && (mX <= (30 + 72))) nom = 6;
            if ((mX >= 140) && (mX <= (140 + 72))) nom = 7;
            if ((mX >= 250) && (mX <= (250 + 72))) nom = 8;
            if ((mX >= 360) && (mX <= (360 + 72))) nom = 9;
            if ((mX >= 470) && (mX <= (470 + 72))) nom = 10;
            if ((mX >= 580) && (mX <= (580 + 72))) nom = 11;
            if ((mX >= 690) && (mX <= (690 + 72))) nom = 12;
        }

        return nom;
    }
}
