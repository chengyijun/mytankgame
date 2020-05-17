/**
 * 功能:坦克游戏的5.0[]
 * 1.画出坦克.
 * 2.我的坦克可以上下左右移动
 * 3.可以发射子弹,子弹连发(最多5)
 * 4.当我的坦克击中敌人坦克时，敌人就消失(爆炸的效果)
 * 5.我被击中后，显示爆炸效果
 * 6.防止敌人坦克重叠运动(*)
 * 6.1决定把判断是否碰撞的函数写到EnemyTank类
 * 7.可以分关(*)
 * 7.1做一个开始的Panle,它是一个空的
 * 7.2闪烁效果
 * 8.可以在玩游戏的时候暂停和继续（*）
 * 8.1当用户点击暂停时，子弹的速度和坦克速度设为0,并让坦克的方向不要变化
 * 9.可以记录玩家的成绩（*）
 * 9.1用文件流.
 * 9.2单写一个记录类，完成对玩家记录
 * 9.3先完成保存共击毁了多少辆敌人坦克的功能.
 * 9.4存盘退出游戏,可以记录当时的敌人坦克坐标，并可以恢复
 * 10.java如何操作声音文件（*）
 * 10.1对声音文件的操作
 */
package com.abel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

class GameConfig {
    static int BullerSpeed = 5;
    static int MyTankSpeed = 2;
    static int MyTankBullerNumber = 5;
    static int EnemyTankNumber = 7;
    static int EnemyTankSpeed = 1;
    static int EnemyTankBullerNumber = 3;

    static boolean isEnemyTankGetRandomDirect = true;

    public static void setIsEnemyTankGetRandomDirect(boolean isEnemyTankGetRandomDirect) {
        GameConfig.isEnemyTankGetRandomDirect = isEnemyTankGetRandomDirect;
    }


    public static void setBullerSpeed(int bullerSpeed) {
        BullerSpeed = bullerSpeed;
    }

    public static void setMyTankSpeed(int myTankSpeed) {
        MyTankSpeed = myTankSpeed;
    }

    public static void setEnemyTankSpeed(int enemyTankSpeed) {
        EnemyTankSpeed = enemyTankSpeed;
    }

}

public class MyTankGame extends JFrame implements ActionListener {
    // 游戏面板
    MyPanel myPanel = null;
    // 关卡信息面板
    StagePanel stagePanel = null;
    // 菜单栏
    JMenuBar jMenuBar = null;
    // 菜单
    JMenu jMenu = null;
    // 菜单项
    JMenuItem jMenuItemExit = null;
    JMenuItem jMenuItemNewGame = null;
    JMenuItem jMenuItemPauseGame = null;
    JMenuItem jMenuItemContinueGame = null;


    public static void main(String[] args) {
        MyTankGame myTankGame = new MyTankGame();
    }

    public MyTankGame() throws HeadlessException {

        // 创建菜单
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("游戏");
        jMenuItemNewGame = new JMenuItem("新游戏");
        jMenuItemPauseGame = new JMenuItem("暂停游戏");
        jMenuItemContinueGame = new JMenuItem("继续游戏");
        jMenuItemExit = new JMenuItem("退出");
        jMenuBar.add(jMenu);
        jMenu.add(jMenuItemNewGame);
        jMenu.add(jMenuItemPauseGame);
        jMenu.add(jMenuItemContinueGame);
        jMenu.add(jMenuItemExit);
        this.setJMenuBar(jMenuBar);
        // 菜单监听
        jMenuItemExit.addActionListener(this);
        jMenuItemExit.setActionCommand("exit");
        jMenuItemNewGame.addActionListener(this);
        jMenuItemNewGame.setActionCommand("newgame");
        jMenuItemPauseGame.addActionListener(this);
        jMenuItemPauseGame.setActionCommand("pausegame");
        jMenuItemContinueGame.addActionListener(this);
        jMenuItemContinueGame.setActionCommand("continuegame");


        // 创建关卡信息面板
        createStagePanel();
    }

    /**
     * 创建关卡信息面板
     */
    private void createStagePanel() {
        stagePanel = new StagePanel();
        Thread t = new Thread(stagePanel);
        t.start();
        this.add(stagePanel);
        panelConfig();
    }

    /**
     * 面板的基本参数
     */
    private void panelConfig() {
        this.setTitle("我的坦克大战游戏");
        this.setSize(600, 500);
        this.setLocation(700, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    /**
     * 创建游戏面板
     */
    private void createGamePanel() {
        // 创建游戏面板
        myPanel = new MyPanel();
        // 启动myPanel线程
        Thread t = new Thread(myPanel);
        t.start();
        // 注册监听
        this.addKeyListener(myPanel);
        // 移除关卡信息面板
        this.remove(stagePanel);
        this.add(myPanel);

        panelConfig();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "exit":
                System.exit(0);
                break;
            case "newgame":
                createGamePanel();
                break;
            case "pausegame":
                GameConfig.setBullerSpeed(0);
                GameConfig.setEnemyTankSpeed(0);
                GameConfig.setMyTankSpeed(0);
                GameConfig.setIsEnemyTankGetRandomDirect(false);
                break;
            case "continuegame":
                GameConfig.setBullerSpeed(5);
                GameConfig.setEnemyTankSpeed(1);
                GameConfig.setMyTankSpeed(2);
                GameConfig.setIsEnemyTankGetRandomDirect(true);
                break;
        }
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
    // 炸弹
    Bomb bomb = null;

    public MyPanel() {
//        this.setBackground(Color.black);
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
            et.setEts(ets);
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 绘制游戏背景
        g.setColor(Color.black);
        g.fillRect(0, 0, 400, 300);
        // 绘制我的坦克
        drawMyTank(g);
        // 绘制我的子弹
        drawMyTankBuller(g);
        // 绘制敌人的坦克和敌人坦克的子弹
        drawEnemyTankAndBuller(g);
        // 绘制炸弹
        showBomb(g, bomb);

    }

    /**
     * 画出敌人的坦克和敌人的坦克子弹
     *
     * @param g
     */
    private void drawEnemyTankAndBuller(Graphics g) {
        for (int i = 0; i < ets.size(); i++) {
            EnemyTank et = ets.get(i);
            if (et.isAlive) {
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
            } else {
                ets.remove(et);
            }
        }
    }

    /**
     * 画出我的坦克子弹
     *
     * @param g
     */
    private void drawMyTankBuller(Graphics g) {
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
    }

    /**
     * 画出我的坦克
     *
     * @param g
     */
    private void drawMyTank(Graphics g) {
        if (myTank.isAlive) {
            drawTank(myTank.x, myTank.y, myTank.direct, myTank.kind, g);
        }
    }

    /**
     * 画出炸弹
     *
     * @param g
     * @param bomb
     */
    private void showBomb(Graphics g, Bomb bomb) {
        if (bomb == null || !bomb.isAlive) {
            return;
        }
        File file1 = new File("1.png");
        File file2 = new File("2.png");
        File file3 = new File("3.png");
        Image image1 = null;
        Image image2 = null;
        Image image3 = null;
        try {
            image1 = ImageIO.read(file1);
            image2 = ImageIO.read(file2);
            image3 = ImageIO.read(file3);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (bomb.leftLife > 6) {
            g.drawImage(image1, bomb.x, bomb.y, this);
        } else if (bomb.leftLife > 3) {
            g.drawImage(image2, bomb.x, bomb.y, this);
        } else {
            g.drawImage(image3, bomb.x, bomb.y, this);
        }
    }

    /**
     * 画坦克基本方法
     *
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
//            System.out.println("发射子弹");
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
        moveMyTankByPress(e);
        // 重绘
        this.repaint();
    }

    /**
     * 根据按键监听 移动我的坦克
     *
     * @param e
     */
    private void moveMyTankByPress(KeyEvent e) {
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
                if (myTank.x < 380) {
                    myTank.x++;
                }
                break;
            case 40:
                myTank.direct = 2;
                if (myTank.y < 280) {
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * 检测子弹是否击中坦克
     *
     * @param buller
     * @param tank
     * @return
     */
    private boolean isShot(Buller buller, Tank tank) {
        boolean flag = false;
        if (buller.x > tank.x && buller.x < tank.x + 20 && buller.y > tank.y && buller.y < tank.y + 20) {
            flag = true;
        }
        return flag;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 检测敌人坦克的子弹是否击中了我的坦克
            isEnemyHitMyTank();
            // 检测我的坦克子弹是否击中敌人的坦克
            hitEnemyTank();
            // 持续重绘面板 使敌人坦克和子弹运动起来
            this.repaint();
            // 退出线程条件 我的坦克死亡 游戏结束
            if (!myTank.isAlive) {
                break;
            }
        }
    }

    private void isEnemyHitMyTank() {
        for (int i = 0; i < ets.size(); i++) {
            EnemyTank et = ets.get(i);
            Vector<Buller> etBullers = et.bullers;
            for (int j = 0; j < etBullers.size(); j++) {
                Buller etBuller = etBullers.get(j);
                if (isShot(etBuller, myTank)) {
                    // 创建一个炸弹
                    bomb = new Bomb(myTank.x, myTank.y);
                    Thread t = new Thread(bomb);
                    t.start();
                    // 我的坦克死亡
                    myTank.isAlive = false;
                    // 敌人的子弹死亡
                    etBuller.isAlive = false;
                    // 敌人的子弹从子弹向量移除
                    etBullers.remove(etBuller);
                }
            }
        }
    }

    /**
     * 检测我的子弹是否击中敌人的坦克
     */
    private void hitEnemyTank() {
        for (int i = 0; i < myBullers.size(); i++) {
            Buller buller = myBullers.get(i);
            for (int j = 0; j < ets.size(); j++) {
                EnemyTank et = ets.get(j);
                if (this.isShot(buller, et)) {
                    // 我的子弹死亡
                    buller.isAlive = false;
                    myBullers.remove(buller);
                    // 敌方坦克死亡
                    et.isAlive = false;
                    ets.remove(et);
                    // 生成炸弹
                    bomb = new Bomb(et.x, et.y);
                    Thread t = new Thread(bomb);
                    t.start();
                }
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
    Vector<EnemyTank> ets = null;

    /**
     * 使敌人坦克类 获取mypanel上的所有敌人坦克
     *
     * @param ets
     */
    public void setEts(Vector<EnemyTank> ets) {
        this.ets = ets;
    }

    /**
     * 检测敌人的坦克是否相互碰撞
     *
     * @return
     */
    private boolean isEnemyTanksTouch() {
        boolean flag = false;

        for (int i = 0; i < ets.size(); i++) {
            EnemyTank et = ets.get(i);
            if (et != this && et.isAlive) {
                if (isTouch(this, et)) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    /**
     * 两个坦克是否碰撞检测 基本方法
     *
     * @param tankA
     * @param tankB
     * @return
     */
    private boolean isTouch(Tank tankA, Tank tankB) {
        boolean flag = false;
        Rectangle rectA = new Rectangle(tankA.x, tankA.y, 20, 20);
        Rectangle rectB = new Rectangle(tankB.x, tankB.y, 20, 20);
        if (rectA.intersects(rectB)) {
            flag = true;
        }
        return flag;
    }

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
            moveTank();
            // 随机一个方向 让坦克转向
            setEnemyTankRandomDirect();
            // 敌人坦克创建后 紧接创建该坦克的子弹
            launchBuller();
            // 退出线程条件 坦克死亡
            if (!isAlive) {
                break;
            }
        }
    }

    private void setEnemyTankRandomDirect() {
        if (GameConfig.isEnemyTankGetRandomDirect) {
            this.direct = getRandomInt(0, 4);
        }
    }

    /**
     * 让敌人的坦克发射子弹
     */
    private void launchBuller() {
        if (bullers.size() < GameConfig.EnemyTankBullerNumber && GameConfig.BullerSpeed != 0) {
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
    }

    /**
     * 自动移动敌人的坦克
     */
    private void moveTank() {
        if (GameConfig.EnemyTankSpeed == 0)
            return;
        switch (direct) {
            case 0:
                for (int i = 0; i < 30; i++) {
                    if (y > 0 && !isEnemyTanksTouch()) {
                        y -= speed;
                    } else {
                        y++;
                        break;
                    }
                    try {
                        Thread.sleep(this.getRandomInt(100, 200));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 1:
                for (int i = 0; i < 30; i++) {
                    if (x < 380 && !isEnemyTanksTouch()) {
                        x += speed;
                    } else {
                        x--;
                        break;
                    }
                    try {
                        Thread.sleep(this.getRandomInt(100, 200));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 2:
                for (int i = 0; i < 30; i++) {
                    if (y < 280 && !isEnemyTanksTouch()) {
                        y += speed;
                    } else {
                        y--;
                        break;
                    }
                    try {
                        Thread.sleep(this.getRandomInt(100, 200));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                for (int i = 0; i < 30; i++) {
                    if (x > 0 && !isEnemyTanksTouch()) {
                        x -= speed;
                    } else {
                        x++;
                        break;
                    }
                    try {
                        Thread.sleep(this.getRandomInt(100, 200));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
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
            // 移动子弹
            moveBuller();
            // 判断子弹是否碰壁
            isBullerHitBorder();
            // 子弹消失条件判断 终止线程
            if (!this.isAlive) {
                break;
            }
        }
    }

    /**
     * 检测子弹是否到达边界
     */
    private void isBullerHitBorder() {
        if (this.x <= 0 || this.x >= 400 || this.y <= 0 || this.y >= 300) {
            this.isAlive = false;
        }
    }

    /**
     * 使子弹自动移动
     */
    private void moveBuller() {
        if (GameConfig.BullerSpeed == 0) {
            return;
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
    }
}

class Bomb implements Runnable {
    int leftLife = 9;
    int x;
    int y;
    boolean isAlive = true;

    public Bomb(int x, int y) {
        this.x = x;
        this.y = y;
    }


    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 炸弹生命流失 便于绘制不同图片 表示炸弹爆炸过程
            lifeDown();
//            System.out.println("炸弹剩余生命" + leftLife);

            // 退出线程条件  炸弹死亡
            if (!isAlive) {
                break;
            }
        }

    }

    /**
     * 使炸弹生命流失
     */
    private void lifeDown() {
        if (leftLife > 0) {
            leftLife--;
        } else {
            isAlive = false;
        }
    }
}

class StagePanel extends JPanel implements Runnable {
    int num = 0;

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // 画出关卡提示信息
        if (num % 2 == 0) {
            drawStageMessage(g);
        }
    }

    private void drawStageMessage(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("黑体", Font.BOLD, 40));
        g.drawString("Stage 1", 130, 130);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            num++;
            repaint();
            // 退出线程
            if (false) {
                break;
            }
        }
    }
}