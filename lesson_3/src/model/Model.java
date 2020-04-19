package model;

import model.game.Card;
import model.game.Coord;
import model.game.Pile;
import model.game.Utils;
import model.game.enums.Piles;
import model.game.enums.Ranks;
import model.game.enums.States;
import model.game.enums.Suits;
import presenter.ModelListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Model implements IModel {

    private ModelListener listener;

    private ArrayList<Pile> piles;
    public States state;

    private BufferedImage back;
    private BufferedImage empty;
    private BufferedImage table;

    private ArrayList<Card> handledCards = new ArrayList<>();
    private Pile handledPill;
    private Coord dCoord;
    private Coord oldCoord;

    public Model() {
        initImages();
        createPiles();
    }

    private void initImages() {
        try {
            back = ImageIO.read(new File("img2/back.png"));
            empty = ImageIO.read(new File("img2/empty.png"));
            table = ImageIO.read(new File("img2/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPiles() {
        piles = new ArrayList<>();

        for (int i = 0; i < 13; i++) {
            Pile pile = new Pile();
            switch (i) {
                case 0: {
                    pile.setType(Piles.STOCK);
                    pile.setCoord(new Coord(30, 15));
                    break;
                }
                case 1: {
                    pile.setType(Piles.TALON);
                    pile.setCoord(new Coord(140, 15));
                    break;
                }
                case 2:
                case 3:
                case 4:
                case 5: {
                    pile.setType(Piles.FOUNDATION);
                    pile.setCoord(new Coord(140 + i * 110, 15));
                    break;
                }
                default: {
                    pile.setType(Piles.TABLEAU);
                    pile.setCoord(new Coord(i * 110 - 630, 130));
                    break;
                }
            }
            piles.add(pile);
        }
    }

    public void start() {
        piles.forEach(Pile::clear);
        handledCards.clear();
        handledPill = null;

        dial(getCardPack());

        state = States.GAME;

        listener.updateTableImage(drawTableImage());
    }

    private ArrayList<Card> getCardPack() {
        ArrayList<Card> pack = new ArrayList<>();
        for (Ranks rank : Ranks.values()) {
            for (Suits suit : Suits.values()) {
                pack.add(new Card(suit, rank, back));
            }
        }
        return pack;
    }

    private void dial(ArrayList<Card> pack) {
        for (int i = 6; i < 13; i++) {
            Pile tableau = piles.get(i);
            for (int j = 6; j <= i; j++) {
                int rnd = (int) (Math.random() * pack.size());
                Card card = pack.get(rnd);
                if (j == i)
                    card.setFaced(true);
                card.setCoord(new Coord(tableau.getCoord().x, tableau.getCoord().y + tableau.size() * Utils.CARD_GAP));
                tableau.add(card);
                pack.remove(card);
            }
        }

        Collections.shuffle(pack);
        Pile stock = piles.get(0);
        pack.forEach(card -> card.setCoord(stock.getCoord()));
        stock.addAll(pack);
    }

    private BufferedImage drawCard(Card card) {
        return card.isFaced() ? card.getFace() : card.getBack();
    }

    private BufferedImage drawTableImage() {
        // Создаем заготовку под размер стола
        BufferedImage img = new BufferedImage(Utils.TABLE_WIDTH, Utils.TABLE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();

        // Рисуем фон
        g.drawImage(table, 0, 0, Utils.TABLE_WIDTH, Utils.TABLE_HEIGHT, null);

        // Рисуем стопки
        for (Pile pile : piles) {
            g.drawImage(empty, pile.getCoord().x, pile.getCoord().y, null);
            for (Card card : pile.getCards()) {
                g.drawImage(drawCard(card), card.getCoord().x, card.getCoord().y, null);
            }
        }

        // Рисуем карты в руке
        handledCards.forEach(card -> g.drawImage(drawCard(card), card.getCoord().x, card.getCoord().y, null));

        return img;
    }

    @Override
    public void setListener(ModelListener listener) {
        this.listener = listener;
    }

    @Override
    public void onMouseDragged(int mX, int mY) {
        if (!handledCards.isEmpty()) {
            int y = 0;
            for (Card card : handledCards) {
                card.setCoord(new Coord(mX - dCoord.x, mY - dCoord.y + y));
                y += Utils.CARD_GAP;
            }
            listener.updateTableImage(drawTableImage());
        }
    }

    @Override
    public void onLeftMouseReleased(int mX, int mY) {
        if (handledCards.isEmpty())
            return;

        int pillNumber = Utils.findPillByCoord(mX, mY);
        if (pillNumber == -1 || !isCardLayDown(pillNumber)) {
            int y = 0;
            for (Card card : handledCards) {
                card.setCoord(new Coord(oldCoord.x, oldCoord.y + y));
                y += Utils.CARD_GAP;
            }
        } else {
            Pile pile = piles.get(pillNumber);
            switch (pile.getType()) {
                case FOUNDATION: {
                    for (Card card : handledCards) {
                        card.setCoord(new Coord(pile.getCoord().x, pile.getCoord().y));
                        pile.add(card);
                        handledPill.remove(card);
                    }
                    break;
                }
                case TABLEAU: {
                    Coord coord = pile.isEmpty() ? pile.getCoord() : pile.getLast().getCoord();
                    int y = pile.isEmpty() ? 0 : Utils.CARD_GAP;

                    for (Card card : handledCards) {
                        card.setCoord(new Coord(coord.x, coord.y + y));
                        y += Utils.CARD_GAP;
                        pile.add(card);
                        handledPill.remove(card);
                    }
                    break;
                }
            }
        }

        handledPill = null;
        handledCards.clear();

        openClosedTableauCards();
        testEndGame();

        listener.updateTableImage(drawTableImage());
    }

    @Override
    public void onRightMouseReleased(int mX, int mY) {
        for (Pile pile : piles) {
            if (pile.getType() == Piles.TALON || pile.getType() == Piles.TABLEAU) {
                if (pile.isEmpty()) continue;

                Card card = pile.getLast();
                for (Pile foundation : piles) {
                    if (foundation.getType() == Piles.FOUNDATION) {
                        Card card_ = foundation.getLast();
                        if (Utils.isFitToFoundation(card, card_)) {
                            card.setCoord(foundation.getCoord());
                            foundation.add(card);
                            pile.remove(card);

                            openClosedTableauCards();
                            testEndGame();
                            listener.updateTableImage(drawTableImage());
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isCardLayDown(int pillNumber) {
        Pile pill_2 = piles.get(pillNumber);

        Card card_1 = handledCards.get(0);
        Card card_2 = pill_2.getLast();

        if (pill_2.getType() == Piles.FOUNDATION) {
            return Utils.isFitToFoundation(card_1, card_2);
        }

        if (pill_2.getType() == Piles.TABLEAU) {
            return Utils.isFitToTableau(card_1, card_2);
        }

        return false;
    }

    private void testEndGame() {
        if (piles.get(2).size() == 13
                && piles.get(3).size() == 13
                && piles.get(4).size() == 13
                && piles.get(5).size() == 13) {
            state = States.WIN;
            listener.updateState(state);
        }
    }

    @Override
    public void onMousePressed(int mX, int mY) {
        int pillNumber = Utils.findPillByCoord(mX, mY);
        if (pillNumber == -1)
            return;

        Pile pile = piles.get(pillNumber);
        switch (pile.getType()) {
            case STOCK: {
                if (!pile.isEmpty()) {
                    Pile talon = piles.get(1);
                    Card card = pile.get(pile.size() - 1);
                    card.setFaced(true);
                    card.setCoord(talon.getCoord());
                    talon.add(card);
                    pile.remove(card);
                } else {
                    Pile talon = piles.get(1);
                    ArrayList<Card> cards = talon.getCards();
                    Collections.reverse(cards);
                    cards.forEach(it -> {
                        it.setFaced(false);
                        it.setCoord(pile.getCoord());
                    });
                    pile.addAll(cards);
                    talon.clear();
                }
                break;
            }
            case FOUNDATION:
            case TALON:
            case TABLEAU: {
                if (pile.isEmpty()) return;

                int cardNumber;
                if (mY >= pile.getLast().getCoord().y && mY <= pile.getLast().getCoord().y + Utils.CARD_HEIGHT) {
                    cardNumber = pile.size() - 1;
                } else {
                    cardNumber = (mY - pile.getCoord().y) / Utils.CARD_GAP;
                }

                if (cardNumber >= pile.size() || !pile.get(cardNumber).isFaced()) {
                    return;
                }

                for (int i = cardNumber; i < pile.size(); i++) {
                    Card card = pile.get(i);
                    handledCards.add(card);
                }
                handledPill = pile;

                dCoord = new Coord(mX - pile.get(cardNumber).getCoord().x, mY - pile.get(cardNumber).getCoord().y);
                oldCoord = new Coord(pile.get(cardNumber).getCoord().x, pile.get(cardNumber).getCoord().y);

                break;
            }
        }

        listener.updateTableImage(drawTableImage());
    }

    @Override
    public void onMouseDoublePressed(int mX, int mY) {
        int pillNumber = Utils.findPillByCoord(mX, mY);
        if (pillNumber == -1) return;

        Pile pile = piles.get(pillNumber);
        if (pile.isEmpty()) return;

        if (pile.getType() == Piles.TALON || pile.getType() == Piles.TABLEAU) {
            Card card = pile.getLast();
            for (Pile foundation : piles) {
                if (foundation.getType() == Piles.FOUNDATION) {
                    Card card_ = foundation.getLast();
                    if (Utils.isFitToFoundation(card, card_)) {
                        card.setCoord(foundation.getCoord());
                        foundation.add(card);
                        pile.remove(card);

                        openClosedTableauCards();
                        testEndGame();
                        listener.updateTableImage(drawTableImage());
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onStart() {
        start();
    }

    private void openClosedTableauCards() {
        piles.stream().
                filter(it -> !it.isEmpty() && it.getType() == Piles.TABLEAU).
                map(Pile::getLast).
                forEach(it -> it.setFaced(true));
    }

}