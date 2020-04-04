import model.IModel;
import model.Model;
import presenter.IPresenter;
import presenter.Presenter;
import view.IView;
import view.View;

import javax.swing.*;

public class Application {

    public Application() {
        IModel model = new Model();
        IView view = new View();

        IPresenter presenter = new Presenter();
        presenter.setModel(model);
        presenter.setView(view);
        presenter.initListeners();
        presenter.startGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Application();
            }
        });
    }

}
