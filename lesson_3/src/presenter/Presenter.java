package presenter;

import model.IModel;
import model.game.enums.States;
import view.IView;

import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class Presenter implements IPresenter {

    private IView view;
    private IModel model;

    @Override
    public void setView(IView view) {
        this.view = view;
    }

    @Override
    public void setModel(IModel model) {
        this.model = model;
    }

    @Override
    public void initListeners() {
        view.setListener(viewListener);
        model.setListener(modelListener);
    }

    @Override
    public void startGame() {
        model.onStart();
    }

    private ModelListener modelListener = new ModelListener() {
        @Override
        public void updateTableImage(BufferedImage tableImage) {
            view.updateTableImage(tableImage);
        }

        @Override
        public void updateState(States state) {
            view.updateState(state.ordinal());
        }
    };

    private ViewListener viewListener = new ViewListener() {
        @Override
        public void onMouseDragged(int mX, int mY) {
            model.onMouseDragged(mX, mY);
        }

        @Override
        public void onMouseReleased(int mX, int mY, int btnType) {
            model.onLeftMouseReleased(mX, mY);
            if (btnType == MouseEvent.BUTTON1) {
                model.onLeftMouseReleased(mX, mY);
            } else if (btnType == MouseEvent.BUTTON3) {
                model.onRightMouseReleased(mX, mY);
            }
        }

        @Override
        public void onMousePressed(int mX, int mY, int btnType, int clickCount) {
            if (btnType == MouseEvent.BUTTON1 && clickCount == 1) {
                model.onMousePressed(mX, mY);
            } else if (btnType == MouseEvent.BUTTON1 && clickCount == 2) {
                model.onMouseDoublePressed(mX, mY);
            }
        }

        @Override
        public void onStartButtonPressed() {
            model.onStart();
        }
    };

}
