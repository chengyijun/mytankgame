package com.abel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;

class GameConfig {
    static int BullerSpeed = 2;

    static int MyTankSpeed = 2;
    static int MyTankBullerNumber = 5;

    static int EnemyTankNumber = 5;
    static int EnemyTankSpeed = 1;
    static int EnemyTankBullerNumber = 3;
}

public class MyTankGame extends JFrame {
    MyPanel myPanel = null;


    public static void main(String[] args) {
        MyTankGame myTankGame = new MyTankGame();
    }

    public MyTankGame() throws HeadlessException {
        myPanel = new MyPanel();
        // 启动myPanel线程
        Thread t = new Thread(myPanel);
        t.start();
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

class MyPanel extends JPanel implements KeyListener, Runnable {
    // 我的坦克
    MyTank myTank = null;
    // 我的子弹
    Vector<Buller> myBullers = null;
    // 敌人的坦克
    Vector<EnemyTank> ets = null;
    // 敌人坦克数量
    int etsSize = GameConfig.EnemyTankNumber;

    public MyPanel() {
        this.setBackground(Color.black);
        // 初始化我的坦克
        myTank = new MyTank(180, 220);
        // 初始化我的子弹
        myBullers = new Vector<Buller>();
        // 初始化敌人的坦克
        ets = new Vector<EnemyTank>();
        for (int i = 0; i < etsSize; i++) {
            EnemyTank et = new EnemyTank((i + 1) * 50, 10);
            Thread t = new Thread(et);
            t.start();
            ets.add(et);
        }

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 绘制我的坦克
        drawTank(myTank.x, myTank.y, myTank.direct, myTank.kind, g);
        // 绘制我的子弹
        for (int i = 0; i < myBullers.size(); i++) {
            Buller myBuller = myBullers.get(i);
            if (myBuller.isAlive) {
                // 子弹还活着就绘制
                g.drawRect(myBuller.x, myBuller.y, 1, 1);
            } else {
                // 子弹死亡了 就从子弹向量移除
                myBullers.remove(myBuller);
            }
        }
        // 绘制敌人的坦克
        for (int i = 0; i < ets.size(); i++) {
            EnemyTank et = ets.get(i);
            // 绘制敌人坦克的子弹
            for (int j = 0; j < et.bullers.size(); j++) {
                Buller etBuller = et.bullers.get(j);
                if (etBuller.isAlive) {
                    // 子弹还活着就绘制
                    g.setColor(Color.yellow);
                    g.drawRect(etBuller.x, etBuller.y, 1, 1);
                } else {
                    // 子弹死亡了 就从子弹向量移除
                    et.bullers.remove(etBuller);
                }
            }
            drawTank(et.x, et.y, et.direct, et.kind, g);
        }
    }

    /**
     * @param x      int 坦克横坐标
     * @param y      int 坦克纵坐标
     * @param direct int 坦克的方向
     * @param kind   int 坦克的类型
     * @param g      Graphics 画笔
     */
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
        // 通过按j键 我的坦克发射子弹
        if (e.getKeyChar() == 'j' && myBullers.size() < GameConfig.MyTankBullerNumber) {
            // 发射子弹
            System.out.println("发射子弹");
            // 创建我的子弹
            Buller myBuller = null;
            switch (myTank.direct) {
                case 0:
                    myBuller = new Buller(myTank.x + 10, myTank.y, myTank.direct);
                    break;
                case 1:
                    myBuller = new Buller(myTank.x + 20, myTank.y + 10, myTank.direct);
                    break;
                case 2:
                    myBuller = new Buller(myTank.x + 10, myTank.y + 20, myTank.direct);
                    break;
                case 3:
                    myBuller = new Buller(myTank.x, myTank.y + 10, myTank.direct);
                    break;
            }
            Thread t1 = new Thread(myBuller);
            t1.start();
            myBullers.add(myBuller);
        }

        // 通过按方向键移动坦克
        switch (e.getKeyCode()) {
            case 38:
                // 设置我的坦克方向向上
                myTank.direct = 0;
                // 移动坦克
                if (myTank.y > 0) {
                    myTank.y--;
                }
                break;
            case 39:
                myTank.direct = 1;
                if (myTank.x < 360) {
                    myTank.x++;
                }
                break;
            case 40:
                myTank.direct = 2;
                if (myTank.y < 240) {
                    myTank.y++;
                }
                break;
            case 37:
                myTank.direct = 3;
                if (myTank.x > 0) {
                    myTank.x--;
                }
                break;
        }
        // 重绘
        this.repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.repaint();
            // 退出线程条件 我的坦克死亡 游戏结束
            if (!myTank.isAlive) {
                break;
            }
        }
    }
}

class Tank {
    int x = 10;
    int y = 10;
    int direct = 0; // 0123 代表 上右下左
    int speed = 1;
    int kind = 0; //0-我的坦克 1-敌方坦克
    boolean isAlive = true;

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
        this.speed = GameConfig.MyTankSpeed;

    }
}

class EnemyTank extends Tank implements Runnable {
    Vector<Buller> bullers = null;

    public EnemyTank(int x, int y) {
        super(x, y);
        this.direct = 2;
        this.kind = 1;
        this.bullers = new Vector<Buller>();
        this.speed = GameConfig.EnemyTankSpeed;
    }

    @Override
    public void run() {
        while (true) {


            // 移动坦克
            switch (direct) {
                case 0:
                    for (int i = 0; i < 10; i++) {
                        if (y > 0) {
                            y -= speed;
                        }
                        try {
                            Thread.sleep(this.getRandomInt(100, 200));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 1:
                    for (int i = 0; i < 10; i++) {
                        if (x < 400) {
                            x += speed;
                        }
                        try {
                            Thread.sleep(this.getRandomInt(100, 200));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 2:
                    for (int i = 0; i < 10; i++) {
                        if (y < 300) {
                            y += speed;
                        }
                        try {
                            Thread.sleep(this.getRandomInt(100, 200));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case 3:
                    for (int i = 0; i < 10; i++) {
                        if (x > 0) {
                            x -= speed;
                        }
                        try {
                            Thread.sleep(this.getRandomInt(100, 200));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }


            // 随机一个方向 让坦克转向
            this.direct = getRandomInt(0, 4);
            // 敌人坦克创建后 紧接创建该坦克的子弹
            if (bullers.size() < GameConfig.EnemyTankBullerNumber) {
                Buller buller = null;
                switch (direct) {
                    case 0:
                        buller = new Buller(this.x + 10, this.y, this.direct);
                        break;
                    case 1:
                        buller = new Buller(this.x + 20, this.y + 10, this.direct);
                        break;
                    case 2:
                        buller = new Buller(this.x + 10, this.y + 20, this.direct);
                        break;
                    case 3:
                        buller = new Buller(this.x, this.y + 10, this.direct);
                        break;
                }
                Thread t2 = new Thread(buller);
                t2.start();
                this.bullers.add(buller);
            }
            // 退出线程条件 坦克死亡
            if (!isAlive) {
                break;
            }
        }
    }

    /**
     * 得到一个区间 [min,max) 内的随机整数
     *
     * @param min int 最小值
     * @param max int 最大值
     * @return int 随机结果
     */
    private int getRandomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }
}

class Buller implements Runnable {
    int x;
    int y;
    int direct;
    int speed = GameConfig.BullerSpeed;
    boolean isAlive = true;

    public Buller(int x, int y, int direct) {
        this.x = x;
        this.y = y;
        this.direct = direct;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            switch (direct) {
                case 0:
                    if (y > 0) {
                        y -= speed;
                    }
                    break;
                case 1:
                    if (x < 400) {
                        x += speed;
                    }
                    break;
                case 2:
                    if (y < 300) {
                        y += speed;
                    }
                    break;
                case 3:
                    if (x > 0) {
                        x -= speed;
                    }
                    break;
            }
//            System.out.println("子弹x= " + this.x + "子弹y= " + this.y);

            // 判断子弹是否碰壁
            if (this.x == 0 || this.x == 400 || this.y == 0 || this.y == 300) {
                this.isAlive = false;
            }

            // 子弹消失条件判断 终止线程
            if (!this.isAlive) {
                break;
            }
        }
    }
}