package com.qod.flappy.graphics;

import com.qod.flappy.config.ImageConfig;
import com.qod.flappy.network.BirdController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.qod.flappy.network.Map.logger;


public class Bird {
    private static final AffineTransformOp[] ops = new AffineTransformOp[5];
    private static int tick;

    private static BufferedImage birdOne;
    private static BufferedImage birdTwo;

    /**
     * Set up Bird Images and Rotations
     */
    public static void setupPlayer(){
        birdOne = toBufferedImage(ImageConfig.bird_1);
        birdTwo = toBufferedImage(ImageConfig.bird_2);
        logger.info("Loaded Bird Images");

        //Measure Textures
        double locationX = birdOne.getWidth(null) / 2.0;
        double locationY = birdOne.getHeight(null) / 2.0;

        //Save Rotation States
        AffineTransform tx1 = AffineTransform.getRotateInstance(Math.toRadians(-30), locationX, locationY);
        ops[0] = new AffineTransformOp(tx1, AffineTransformOp.TYPE_BILINEAR);
        AffineTransform tx2 = AffineTransform.getRotateInstance(Math.toRadians(-15), locationX, locationY);
        ops[1] = new AffineTransformOp(tx2, AffineTransformOp.TYPE_BILINEAR);
        AffineTransform tx3 = AffineTransform.getRotateInstance(Math.toRadians(15), locationX, locationY);
        ops[2] = new AffineTransformOp(tx3, AffineTransformOp.TYPE_BILINEAR);
        AffineTransform tx4 = AffineTransform.getRotateInstance(Math.toRadians(30), locationX, locationY);
        ops[3] = new AffineTransformOp(tx4, AffineTransformOp.TYPE_BILINEAR);
        AffineTransform tx5 = AffineTransform.getRotateInstance(Math.toRadians(60), locationX, locationY);
        ops[4] = new AffineTransformOp(tx5, AffineTransformOp.TYPE_BILINEAR);
        logger.info("Saved Bird Rotation States");
    }

    /**
     * Draws Transformed Bird on Graphics. Bird Texture Changes every 20 "Bird Ticks",
     * which are measured inside {@code tick}.
     * @param g graphics to draw on
     * @param x x-position on frame
     * @param y y-position on frame
     * @param birdController responsible controller for the {@code Bird}
     */
    public static void drawBird(Graphics g, int x, int y, BirdController birdController){
        Image i;
        if (ops[0] == null) setupPlayer();

        if(birdController.state < - 0.5){
            if ((tick / 20) % 2 == 0) {
                i = ops[(birdController.state > 1) ? 1 : 0].filter(birdOne,null);
            } else{
                i = ops[(birdController.state > 1) ? 1 : 0].filter(birdTwo,null);
            }
            tick++;
        } else if((birdController.state >= -0.5)&&(birdController.state <= 0.5)){
            i = birdOne;
        } else if((birdController.state > 0.5)&&(birdController.state <= 1)){
            i = ops[2].filter(birdOne,null);
        } else if((birdController.state > 1)&&(birdController.state <= 2)){
            i = ops[3].filter(birdOne,null);
        } else{
            i = ops[4].filter(birdOne,null);
        }
        g.drawImage(i, x,y,null);
    }

    /**
     * Converts Image to BufferedImage
     * @param img image to be converted
     * @return buffered image from {@code img}
     */
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) return (BufferedImage) img;

        BufferedImage bufferedImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D bGr = bufferedImage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();
        return bufferedImage;
    }
}