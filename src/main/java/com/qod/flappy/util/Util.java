package com.qod.flappy.util;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Util {
    /**
     * Gets a random element from a set.
     * @param set set to get the random element from.
     * @param random {@code Random} Object
     * @param <T> set type
     * @return random item out of the set
     */
    public static <T> T getRandomElementFromSet(final Set<T> set, Random random) {
        Iterator<T> iterator = set.iterator();
        for (int i = 0; i < random.nextInt(set.size()) - 1; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    /**
     * Pauses the current Thread for a given period of time
     * @param ms time in milliseconds
     */
    public static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored){}
    }
}