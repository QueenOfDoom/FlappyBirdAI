package com.qod.flappy.config;

import com.qod.flappy.network.Map;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.qod.flappy.network.Map.logger;

public class ImageConfig {
    public static final boolean BUILD = false;

    public static final Image background = BUILD ?
            getBuildImage("images/background.jpg") :
            getDevImage("images/background.jpg");

    public static final Image bird_1 = BUILD ?
            getBuildImage("images/bird_1.png") :
            getDevImage("images/bird_1.png");

    public static final Image bird_2 = BUILD ?
            getBuildImage("images/bird_2.png") :
            getDevImage("images/bird_2.png");

    public static final Image pipe_up = BUILD ?
            getBuildImage("images/pipe_top.png") :
            getDevImage("images/pipe_top.png");

    public static final Image pipe_down = BUILD ?
            getBuildImage("images/pipe_bottom.png") :
            getDevImage("images/pipe_bottom.png");

    /**
     * Parses resource paths.
     * @param localPath - path inside the resources/ folder
     * @return image from path
     */
    public static Image getDevImage(String localPath) {
        String path = Objects.requireNonNull(Map.class.getClassLoader().getResource(localPath)).toString().substring(5);
        logger.debug("Parsed Resource: " + path);
        Image image = null;
        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            logger.error("Could not load parsed resource: " + path);
        }
        return image;
    }

    /**
     * Parses resource paths for the build.
     * @param localPath - path inside the resources/ folder
     * @return image from path
     */
    public static Image getBuildImage(String localPath) {
        String path = Objects.requireNonNull(ClassLoader.getSystemResource(localPath)).toString();
        path = path.substring(path.indexOf("images/"));
        Image img = Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource(path));
        logger.debug("Read: " + img.getWidth(null) + "x" + img.getHeight(null) + " from: " + path);
        return img;
    }
}