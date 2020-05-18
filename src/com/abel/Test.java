package com.abel;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


class Test {


    public Test() {
        InputStream input = getClass().getResourceAsStream("/music/bgm.mp3"); //音频存放在src下
        Music music = new Music(input);
        music.start();
    }

    public static void main(String[] args) {
        Test test = new Test();
    }
}

//播放音频类
class Music extends Thread {
    Player player;
    InputStream input;

    //构造
    public Music(InputStream input) {
        this.input = input;
    }

    @Override
    public synchronized void run() {
        // TODO Auto-generated method stub
//        super.start();
        try {
            play();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JavaLayerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    //播放方法
    public void play() throws FileNotFoundException, JavaLayerException {

        BufferedInputStream buffer = new BufferedInputStream(input);
        player = new Player(buffer);
        player.play();
    }
}