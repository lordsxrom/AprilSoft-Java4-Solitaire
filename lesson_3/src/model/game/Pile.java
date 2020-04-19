package model.game;

import model.game.enums.Piles;

import java.util.ArrayList;
import java.util.Collection;

public class Pile {

    private ArrayList<Card> cards;
    private Piles type;
    private Coord coord;

    public Pile() {
        cards = new ArrayList<>();
    }

    public Card get(int num) {
        return cards.get(num);
    }

    public void add(Card elem) {
        cards.add(elem);
    }

    public void addAll(Collection<Card> elem) {
        cards.addAll(elem);
    }

    public void remove(Card card) {
        cards.remove(card);
    }

    public int size() {
        return cards.size();
    }

    public void clear() {
        cards.clear();
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Piles getType() {
        return type;
    }

    public void setType(Piles type) {
        this.type = type;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }

    public Card getLast() {
        if (cards.isEmpty())
            return null;
        return cards.get(cards.size() - 1);
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

}