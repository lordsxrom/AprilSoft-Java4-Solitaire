package model.game;

import model.game.enums.Ranks;
import model.game.enums.Suits;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Card {

    private Suits suit;
    private Ranks rank;

    private boolean isFaced = false;
    private Coord coord;

    private BufferedImage face;
    private BufferedImage back;

    public Card(Suits suit, Ranks rank, BufferedImage back) {
        this.suit = suit;
        this.rank = rank;
        this.back = back;
        try {
            face = ImageIO.read(new File("img2/" + suit.ordinal() + "_" + rank.ordinal() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Suits getSuit() {
        return suit;
    }

    public Ranks getRank() {
        return rank;
    }

    public boolean isFaced() {
        return isFaced;
    }

    public void setFaced(boolean faced) {
        isFaced = faced;
    }

    public BufferedImage getFace() {
        return face;
    }

    public BufferedImage getBack() {
        return back;
    }

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

}