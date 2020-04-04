package view;

import presenter.ViewListener;

import java.awt.image.BufferedImage;

public interface IView {
    void setListener(ViewListener listener);

    void updateState(int state);

    void updateTableImage(BufferedImage tableImage);

    void updateCardImage(BufferedImage cardImage);
}