package com.abel;

import javax.swing.*;
import java.awt.*;

public class MyTankGame extends JFrame {
    MyPanel myPanel = null;


    public static void main(String[] args) {
        MyTankGame myTankGame = new MyTankGame();
    }

    public MyTankGame() throws HeadlessException {
        myPanel = new MyPanel();

        this.add(myPanel);
        this.setTitle("我的坦克大战游戏");
        this.setSize(400, 300);
        this.setLocation(700, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}

class MyPanel extends JPanel {
    // 我的坦克
    MyTank myTank = null;

    public MyPanel() {
        this.setBackground(Color.black);
        myTank = new MyTank(20, 20);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawTank(myTank.x, myTank.y, g);
    }

    private void drawTank(int x, int y, Graphics g) {
        g.setColor(Color.cyan);
        g.fill3DRect(x, y, 5, 20, false);
        g.fill3DRect(x + 15, y, 5, 20, false);
        g.fill3DRect(x + 5, y + 5, 10, 10, false);
        g.drawLine(x + 10, y + 10, x + 10, y);
    }
}

class Tank {
    int x = 10;
    int y = 10;
    int direct = 0; // 0123 代表 上右下左
    int speed = 1;
    int kind = 0; //0-我的坦克 1-敌方坦克

    public Tank(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class MyTank extends Tank {
    public MyTank(int x, int y) {
        super(x, y);
        this.direct = 0;
        this.kind = 0;

    }
}