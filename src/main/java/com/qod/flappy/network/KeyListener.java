package com.qod.flappy.network;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyListener extends KeyAdapter {

    public static int[] key_map = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.VK_UP:
                key_map[0] = 1;
                break;
            case KeyEvent.VK_DOWN:
                key_map[1] = 1;
                break;
            case KeyEvent.VK_LEFT:
                key_map[2] = 1;
                break;
            case KeyEvent.VK_RIGHT:
                key_map[3] = 1;
                break;
            case KeyEvent.VK_SPACE:
                key_map[4] = 1;
                break;
            case KeyEvent.VK_ESCAPE:
                key_map[5] = 1;
                break;

            case KeyEvent.VK_1:
                key_map[6] = 1; //Speed = 1
                break;
            case KeyEvent.VK_2:
                key_map[7] = 1; //Speed = 2
                break;
            case KeyEvent.VK_3:
                key_map[8] = 1; //Speed = 3
                break;
            case KeyEvent.VK_4:
                key_map[9] = 1; //Speed = 4
                break;
            case KeyEvent.VK_P:
                Map.save = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        switch (event.getKeyCode()){
            case KeyEvent.VK_UP:
                key_map[0] = 0;
                break;
            case KeyEvent.VK_DOWN:
                key_map[1] = 0;
                break;
            case KeyEvent.VK_LEFT:
                key_map[2] = 0;
                break;
            case KeyEvent.VK_RIGHT:
                key_map[3] = 0;
                break;
            case KeyEvent.VK_SPACE:
                key_map[4] = 0;
                break;
            case KeyEvent.VK_ESCAPE:
                key_map[5] = 0;
                break;

            case KeyEvent.VK_1:
                key_map[6] = 0;
                break;
            case KeyEvent.VK_2:
                key_map[7] = 0;
                break;
            case KeyEvent.VK_3:
                key_map[8] = 0;
                break;
            case KeyEvent.VK_4:
                key_map[9] = 0;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e){
    }
}