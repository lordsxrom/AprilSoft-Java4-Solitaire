package presenter;

import model.game.enums.States;

import java.awt.image.BufferedImage;

public interface ModelListener {
    void updateTableImage(BufferedImage tableImage);

    void updateState(States state);
}
