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

    private int nomStopki;
    private int nomKarti;
    private int dx, dy;
    private int oldX, oldY;

    public Model() {
        initImages();
        createPiles();
    }

    private void initImages() {
        try {
            back = ImageIO.read(new File("img2/k0.png"));
            empty = ImageIO.read(new File("img2/k1.png"));
            table = ImageIO.read(new File("img2/fon.png"));
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

        dial(getCardPack());

        state = States.GAME;
        nomKarti = -1;
        nomStopki = -1;

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
        pack.forEach(it -> it.setCoord(stock.getCoord()));
        stock.addAll(pack);
    }

    private BufferedImage drawCard(Card card) {
        BufferedImage cardImg = card.isFaced() ? card.getFace() : card.getBack();
        if (card.isSelected()) {
            BufferedImage selectedCardImg = new BufferedImage(Utils.CARD_WIDTH, Utils.CARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics g = selectedCardImg.getGraphics();
            g.setColor(Color.RED);
            g.drawImage(cardImg, 0, 0, null);
            g.drawRect(0, 0, Utils.CARD_WIDTH, Utils.CARD_HEIGHT);
            return selectedCardImg;
        }
        return cardImg;
    }

    private BufferedImage drawPill(Pile pile) {
        BufferedImage img = null;
        switch (pile.getType()) {
            case STOCK:
            case TALON:
            case FOUNDATION: {
                if (pile.isEmpty()) {
                    return empty;
                }
                Card card = pile.get(pile.size() - 1);
                if (card.isHandled()) {
                    return pile.size() < 2 ? empty : drawCard(pile.get(pile.size() - 2));
                }
                img = drawCard(card);
                break;
            }
            case TABLEAU: {
                if (pile.isEmpty()) {
                    return empty;
                }
                long handled = pile.getCards().stream().filter(Card::isHandled).count(); // TODO
                img = new BufferedImage(Utils.CARD_WIDTH,
                        Utils.CARD_HEIGHT + (pile.size() - 1) * Utils.CARD_GAP,
                        BufferedImage.TYPE_INT_RGB);
                Graphics g = img.getGraphics();
                int y = 0;
                for (Card card : pile.getCards()) {
                    if (card.isHandled())
                        continue;
                    g.drawImage(drawCard(card), 0, y, null);
                    y += Utils.CARD_GAP;
                }
                break;
            }
        }
        return img;
    }

    private BufferedImage drawTableImage() {
        // Создаем заготовку под размер стола
        BufferedImage img = new BufferedImage(Utils.TABLE_WIDTH, Utils.TABLE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = img.getGraphics();

        // Рисуем фон
        g.drawImage(table, 0, 0, Utils.TABLE_WIDTH, Utils.TABLE_HEIGHT, null);

        // Рисуем стопки
        for (Pile pile : piles) {
            g.drawImage(drawPill(pile), pile.getCoord().x, pile.getCoord().y, null);
        }

        // Рисуем карты в руке
        if (nomKarti != -1 && nomStopki != -1) {
            Pile pile = piles.get(nomStopki);
            for (int i = nomKarti; i < pile.size(); i++) {
                Card card = pile.get(i);
                g.drawImage(drawCard(card), card.getCoord().x, card.getCoord().y, null);
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
        if (nomStopki != -1 && nomKarti != -1) {
            Pile pile = piles.get(nomStopki);
            int y = 0;
            for (int i = nomKarti; i < pile.size(); i++) {
                Card card = pile.get(i);
                card.setCoord(new Coord(mX - dx, mY - dy + y));
                card.setHandled(true);
                y += Utils.CARD_GAP;
            }
            listener.updateTableImage(drawTableImage());
        }
    }

    @Override
    public void onLeftMouseReleased(int mX, int mY) {
        int pillNumber = findPillByCoord(mX, mY);

        if (nomStopki != -1 && nomKarti != -1) {
            Pile pile = piles.get(nomStopki);
            pile.get(nomKarti).setSelected(false);

            if (pillNumber == -1 || !isCardLayDown(nomStopki, pillNumber)) {
                for (int i = nomKarti; i < piles.get(nomStopki).size(); i++) {
                    Card card = piles.get(nomStopki).get(i);
                    card.setCoord(new Coord(oldX, oldY));
                    card.setHandled(false);
                }
            }

            nomStopki = -1;
            nomKarti = -1;
            openClosedTableauCards();

            listener.updateTableImage(drawTableImage());
        }
    }

    @Override
    public void onRightMouseReleased(int mX, int mY) {

    }

    private boolean isCardLayDown(int pillNumber_1, int pillNumber_2) {
        boolean result = false;

        Pile pill_1 = piles.get(pillNumber_1);
        Pile pill_2 = piles.get(pillNumber_2);

        Card card_1 = pill_1.get(nomKarti);
        Card card_2 = null;

        if (pill_2.size() > 0) {
            card_2 = pill_2.get(pill_2.size() - 1);
        }

        if (pill_2.getType() == Piles.FOUNDATION) {
            if (nomKarti == (pill_1.size() - 1)) {
                if (card_2 == null) {
                    if (card_1.getRank() == Ranks.ACE)
                        result = true;
                } else if (Ranks.isFitToFoundation(card_1.getRank(), card_2.getRank()) &&
                        Suits.isSame(card_1.getSuit(), card_2.getSuit())) {
                    result = true;
                }

                if (result) {
                    card_1.setCoord(new Coord((110 * (pillNumber_2 + 1)) + 30, 15));
                    card_1.setHandled(false);
                    pill_2.add(card_1);
                    pill_1.remove(nomKarti);

                    testEndGame();
                }
            }
        }

        if (pill_2.getType() == Piles.TABLEAU) {
            int x = pill_2.getCoord().x;
            int y = pill_2.getCoord().y;

            if (card_2 == null) {
                if (card_1.getRank() == Ranks.KING)
                    result = true;
            } else {
                if (card_2.isFaced() &&
                        Ranks.isFitToTableau(card_1.getRank(), card_2.getRank()) &&
                        !Suits.isSimilar(card_1.getSuit(), card_2.getSuit())) {
                    y = card_2.getCoord().y + Utils.CARD_GAP;
                    result = true;
                }
            }

            if (result) {
                for (int i = nomKarti; i < pill_1.size(); i++) {
                    Card card = pill_1.get(i);
                    card.setCoord(new Coord(x, y));
                    card.setHandled(false);
                    pill_2.add(card);
                    y += Utils.CARD_GAP;
                }
                for (int i = pill_1.size() - 1; i >= nomKarti; i--) {
                    pill_1.remove(i);
                }
            }
        }

        return result;
    }

    private void testEndGame() {

    }

    @Override
    public void onMousePressed(int mX, int mY) {
        int pillNumber = findPillByCoord(mX, mY);
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
            case TALON: {
                if (!pile.isEmpty()) {
                    int cardNumber = pile.size() - 1;
                    Card card = pile.get(cardNumber);
                    card.setSelected(true);

                    nomKarti = cardNumber;
                    nomStopki = pillNumber;

                    dx = mX - card.getCoord().x;
                    dy = mY - card.getCoord().y;

                    oldX = card.getCoord().x;
                    oldY = card.getCoord().y;
                }
                break;
            }
            case TABLEAU: {
                if (!pile.isEmpty()) {
                    int cardNumber = (mY - 130) / Utils.CARD_GAP;
                    if (cardNumber >= pile.size() || !pile.get(cardNumber).isFaced()) {
                        return;
                    }

                    Card card = pile.get(cardNumber);
                    card.setSelected(true);

                    nomKarti = cardNumber;
                    nomStopki = pillNumber;

                    dx = mX - card.getCoord().x;
                    dy = mY - card.getCoord().y;

                    oldX = card.getCoord().x;
                    oldY = card.getCoord().y;
                }
                break;
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

    private void openClosedTableauCards() {
        piles.stream().filter(it -> !it.isEmpty() && it.getType() == Piles.TABLEAU).forEach(it -> {
            Card card = it.get(it.size() - 1);
            if (!card.isFaced())
                card.setFaced(true);
        });
    }
}