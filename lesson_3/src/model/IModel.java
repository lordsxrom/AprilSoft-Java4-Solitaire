package model;

import presenter.ModelListener;

public interface IModel {
    void setListener(ModelListener listener);

    void onMouseDragged(int mX, int mY);

    void onMouseReleased(int mX, int mY);

    void onMousePressed(int mX, int mY);

    void onMouseDoublePressed(int mX, int mY);

    void onStart();
}
