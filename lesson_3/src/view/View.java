package view;

import presenter.ViewListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class View implements IView {

    private ViewListener listener;

    private JPanel panel;
    private Timer tmDraw;

    private BufferedImage tableImage;
    private BufferedImage cardImage;

    public View() {
        initPanel();
        initFrame();
    }

    private void initPanel() {
        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(tableImage,0,0,null);
            }
        };
        panel.setLayout(null);

        JButton btn1 = new JButton("Новая игра");
        btn1.setBounds(820, 50, 150, 50);
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onStartButtonPressed();
            }
        });
        panel.add(btn1);

        JButton btn2 = new JButton("Выход");
        btn2.setBounds(820, 150, 150, 50);
        btn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(btn2);

        tmDraw = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.repaint();
            }
        });
        tmDraw.start();

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                int mX = mouseEvent.getX();
                int mY = mouseEvent.getY();
                int btnType = mouseEvent.getButton();
                int clickCount = mouseEvent.getClickCount();

                listener.onMousePressed(mX, mY, btnType, clickCount);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                int mX = mouseEvent.getX();
                int mY = mouseEvent.getY();
                int btnType = mouseEvent.getButton();

                listener.onMouseReleased(mX, mY, btnType);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

        panel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                int mX = mouseEvent.getX();
                int mY = mouseEvent.getY();

                listener.onMouseDragged(mX, mY);
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });
    }

    private void initFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("Пасьянс-Косынка");
        frame.setBounds(0, 0, 1000, 700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(panel);
    }

    @Override
    public void setListener(ViewListener listener) {
        this.listener = listener;
    }

    @Override
    public void updateState(int state) {

    }

    @Override
    public void updateTableImage(BufferedImage tableImage) {
        this.tableImage = tableImage;
    }

    @Override
    public void updateCardImage(BufferedImage cardImage) {
        this.tableImage = tableImage;
    }

}
