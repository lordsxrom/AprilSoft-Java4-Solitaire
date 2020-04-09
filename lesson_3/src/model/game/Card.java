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

    private boolean isSelected = false;
    private boolean isFaced = false;

    private BufferedImage face;
    private BufferedImage back;

    public Card(Suits suit, Ranks rank) {
        this.suit = suit;
        this.rank = rank;
        try {
            back = ImageIO.read(new File("img2/k0.png"));
            face = ImageIO.read(new File("img2/" + suit.ordinal() + "_" + rank.ordinal() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Suits getSuit() {
        return suit;
    }

    public void setSuit(Suits suit) {
        this.suit = suit;
    }

    public Ranks getRank() {
        return rank;
    }

    public void setRank(Ranks rank) {
        this.rank = rank;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
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

    public void setFace(BufferedImage face) {
        this.face = face;
    }

    public BufferedImage getBack() {
        return back;
    }

    public void setBack(BufferedImage back) {
        this.back = back;
    }
}
