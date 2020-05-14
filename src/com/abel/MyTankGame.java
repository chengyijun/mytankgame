package com.abel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MyTankGame extends JFrame {
    MyPanel myPanel = null;


    public static void main(String[] args) {
        MyTankGame myTankGame = new MyTankGame();
    }

    public MyTankGame() throws HeadlessException {
        myPanel = new MyPanel();
        this.add(myPanel);

        // 注册监听
        this.addKeyListener(myPanel);

        this.setTitle("我的坦克大战游戏");
        this.setSize(400, 300);
        this.setLocation(700, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}

class MyPanel extends JPanel implements KeyListener {
    // 我的坦克
    MyTank myTank = null;

    public MyPanel() {
        this.setBackground(Color.black);
        myTank = new MyTank(180, 220);

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        drawTank(myTank.x, myTank.y, myTank.direct, myTank.kind, g);
    }

    private void drawTank(int x, int y, int direct, int kind, Graphics g) {
        switch (kind) {
            case 0:
                g.setColor(Color.cyan);
                break;
            case 1:
                g.setColor(Color.yellow);
                break;
        }
        switch (direct) {
            case 0:
                g.fill3DRect(x, y, 5, 20, false);
                g.fill3DRect(x + 15, y, 5, 20, false);
                g.fill3DRect(x + 5, y + 5, 10, 10, false);
                g.drawLine(x + 10, y + 10, x + 10, y);
                break;
            case 1:
                g.fill3DRect(x, y, 20, 5, false);
                g.fill3DRect(x, y + 15, 20, 5, false);
                g.fill3DRect(x + 5, y + 5, 10, 10, false);
                g.drawLine(x + 10, y + 10, x + 20, y + 10);
                break;
            case 2:
                g.fill3DRect(x, y, 5, 20, false);
                g.fill3DRect(x + 15, y, 5, 20, false);
                g.fill3DRect(x + 5, y + 5, 10, 10, false);
                g.drawLine(x + 10, y + 10, x + 10, y + 20);
                break;
            case 3:
                g.fill3DRect(x, y, 20, 5, false);
                g.fill3DRect(x, y + 15, 20, 5, false);
                g.fill3DRect(x + 5, y + 5, 10, 10, false);
                g.drawLine(x + 10, y + 10, x, y + 10);
                break;
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 38:
                // 设置我的坦克方向向上
                myTank.direct = 0;
                // 移动坦克
                myTank.y--;
                break;
            case 39:
                myTank.direct = 1;
                myTank.x++;
                break;
            case 40:
                myTank.direct = 2;
                myTank.y++;
                break;
            case 37:
                myTank.direct = 3;
                myTank.x--;
                break;
        }
        // 重绘
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

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