package com.qod.flappy.network;

import com.qod.flappy.config.ImageConfig;
import com.qod.flappy.graphics.Bird;
import com.qod.flappy.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Map extends JFrame {

    static int amount = 100;

    private static final BirdController[] BIRD_CONTROLLERS = new BirdController[amount];
    private static final int[] distance = new int[amount];
    private static final int[] toPipeMiddle = new int[amount];
    private static final int[] duration = new int[amount];

    public static Image UpperPipe;
    public static Image LowerPipe;
    private static Image background;

    static int xPosOfTheBird = 100;
    static int[] yPosOfTheBird = new int[amount];
    private static int timer = 0;

    private static final HashMap<Genome, Double> GenomesAndItsFitness = new HashMap<>();
    private static Evaluator evaluator;
    private static GameSpeed gameSpeed = GameSpeed.FOUR;
    private static int generationCounter = 1;
    private static final ArrayList<FlappyBirdGenome> tmpFlappyList = new ArrayList<>();
    private static final ArrayList<Genome> tmpGenomeList = new ArrayList<>();
    private static boolean helper = true;
    private static boolean starting = true;
    public static boolean save = false;

    public static final Logger logger = LogManager.getLogger(Map.class);

    private enum GameSpeed {
        ONE,
        TW0,
        THREE,
        FOUR
    }

    public void paint(Graphics g) {
        Image dbImage = createImage(getWidth(), getHeight());
        Graphics dbg = dbImage.getGraphics();
        paintOneScene(dbg);
        g.drawImage(dbImage, 0, 0, this);
    }

    private void paintOneScene(Graphics g){
        long start = System.currentTimeMillis();
        g.drawImage(background, 0,0,this);

        // Busy Wait if Starting
        while (starting){ Util.sleep(5); }

        // Feed new Inputs, Set new Positions
        for(int i = 0; i < amount; i++) {
            tmpFlappyList.get(i).setInput(
                    Pipes.NextPipeHeight - yPosOfTheBird[i] + 65,
                    BIRD_CONTROLLERS[i].state,
                    Pipes.NextPipeHeight - yPosOfTheBird[i],
                    Pipes.toTheNextPipe);

            yPosOfTheBird[i] = (int) BIRD_CONTROLLERS[i]
                    .move((int) (0.05 * tmpFlappyList.get(i)
                            .count(tmpFlappyList.get(i).OutputNode)), i);
        }

        timer++;

        // Redraw Birds at correct positions
        for(int i = 0; i < amount; i++) {
            try {
                if (BIRD_CONTROLLERS[i].alive) {
                    Bird.drawBird(g, xPosOfTheBird - 1, yPosOfTheBird[i] - 1, BIRD_CONTROLLERS[i]);
                } else {
                    distance[i] = BIRD_CONTROLLERS[i].counter.counter;

                    toPipeMiddle[i] = (distance[i] >= 1) ?
                            (int) BIRD_CONTROLLERS[i].lengthBetweenPipes / distance[i] + 1 :
                            1000;

                    if(duration[i] == 0) duration[i] = timer;
                }
                if(Counter.GlobalCounter < BIRD_CONTROLLERS[i].counter.counter) Counter.addToGlobalCounter();

            } catch (NullPointerException ignored){ }
        }

        // Skip Generation
        if(KeyListener.key_map[5] == 1){
            helper = false;
            Util.sleep(50);
        }

        // Adapt Speed
        if(KeyListener.key_map[6] == 1){
            gameSpeed = GameSpeed.ONE;
        } else if(KeyListener.key_map[7] == 1){
            gameSpeed = GameSpeed.TW0;
        } else if(KeyListener.key_map[8] == 1){
            gameSpeed = GameSpeed.THREE;
        } else if(KeyListener.key_map[9] == 1){
            gameSpeed = GameSpeed.FOUR;
        }

        if(negativeDistance() && helper) {
            Pipes.draw(g);
            WAIT();
            Pipes.checkForNewPipe();
            Pipes.move();
            g.drawString(Counter.GlobalCounter + "", 190,50);
            g.drawString("Generation " + generationCounter , 10,50);
        } else {
            generationCounter++;
            System.out.println("------------------");
            Util.sleep(50);

            for(int i = 0; i < amount; i++){
                double score = 100 * (distance[i] + 1) + 5 * duration[i] + 100.0 / toPipeMiddle[i];
                GenomesAndItsFitness.put(tmpGenomeList.get(i), score);
            }
            tmpFlappyList.clear();
            tmpGenomeList.clear();
            evaluator.evaluate();
            System.out.println(evaluator.getSpeciesAmount()  + "Amount of species");
            System.out.println(evaluator.getHighestFitness() + " Highest fitness");
            Util.sleep(5);
            Pipes.generatePipes();

            for(int i = 0; i < amount; i++){
                yPosOfTheBird[i] = 100;
                xPosOfTheBird = 100;
                BIRD_CONTROLLERS[i].setNew();
                BIRD_CONTROLLERS[i].counter.resetCounter();
                distance[i] = -1;
                duration[i] = 0;
            }
            timer = 0;
            helper = true;
            Counter.resetGlobalCounter();
            for (Genome genome : evaluator.getGenomes()) {
                tmpFlappyList.add(new FlappyBirdGenome(genome));
                tmpGenomeList.add(genome);
            }
        }
        long end = System.currentTimeMillis();
        logger.debug("It took " + (end-start) + "ms to render frame.");
        repaint();
    }

    public static void main(String[] a) {
        logger.info("Started Application");
        background = ImageConfig.background;
        UpperPipe = ImageConfig.pipe_down;
        LowerPipe = ImageConfig.pipe_up;
        logger.info("Loaded Background Images");

        FlappyBirdGenome flappyBirdGenome = new FlappyBirdGenome();
        logger.debug("Loaded Flappy Bird Genome.");

        evaluator = new Evaluator(amount, flappyBirdGenome.genome) {
            @Override
            protected double fitness(Genome genome) {
                return GenomesAndItsFitness.get(genome);
            }
        };
        logger.debug("Created Evaluator.");


        Bird.setupPlayer();
        logger.debug("Set up Map.");
        new Map();
    }

    private boolean negativeDistance(){
        for(int i: distance){
            if(i < 0) return true;
        }
        return false;
    }

    private Map(){
        addKeyListener(new KeyListener());
        setSize(400, 400);
        setTitle("Flappy Bird A.I.");
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(50,50);

        logger.debug("Built JFrame");

        for(int i = 0; i < amount; i++){
            BIRD_CONTROLLERS[i] = new BirdController();
            yPosOfTheBird[i] = 100;
            distance[i] = -1;
            duration[i] = 0;
            toPipeMiddle[i] = 1000;
        }

        logger.debug("Created Bird Controllers");

        for (Genome genome : evaluator.getGenomes()) {
            tmpFlappyList.add(new FlappyBirdGenome(genome));
            tmpGenomeList.add(genome);
        }

        logger.debug("Extracted Genomes into Arrays");

        gameSpeed = GameSpeed.THREE;
        starting = false;
    }

    private static void WAIT(){
        if(gameSpeed == GameSpeed.ONE) {
            Util.sleep(500);
        }
        else if(gameSpeed == GameSpeed.TW0){
            Util.sleep(200);
        }
        else if(gameSpeed == GameSpeed.THREE){
            Util.sleep(50);
        }
        else if(gameSpeed == GameSpeed.FOUR){
            Util.sleep(1);
        }
    }
}