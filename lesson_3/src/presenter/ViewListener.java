package presenter;

public interface ViewListener {
    void onMouseDragged(int mX, int mY);
    void onMouseReleased(int mX, int mY, int btnType);
    void onMousePressed(int mX, int mY, int btnType, int clickCount);
    void onStartButtonPressed();
}