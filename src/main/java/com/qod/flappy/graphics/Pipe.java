package com.qod.flappy.graphics;

import com.qod.flappy.network.Map;

import java.awt.*;

public class Pipe {
    /**
     * Draws a set of 2 pipes on given frame with given positions.
     * @param g Graphics Object
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public static void draw(Graphics g, int x, int y){
        g.drawImage(Map.UpperPipe, x, y + 50 - 400, null);
        g.drawImage(Map.LowerPipe, x, y + 80, null);
    }
}