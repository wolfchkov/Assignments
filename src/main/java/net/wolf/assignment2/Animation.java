/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wolf.assignment2;

import java.util.ArrayList;
import java.util.List;

/**
 *
A collection of particles is contained in a linear chamber. They all have the 
same speed, but some are headed toward the right and others are headed toward 
the left. These particles can pass through each other without disturbing the 
motion of the particles, so all the particles will leave the chamber relatively
quickly. We will be given the initial conditions by a String init containing at
each position an 'L' for a leftward moving particle, an 'R' for a rightward 
moving particle, or a '.' for an empty location. init shows all the positions 
in the chamber. Initially, no location in the chamber contains two particles 
passing through each other. 

We would like an animation of the process. At each unit of time, we want a 
string showing occupied locations with an 'X' and unoccupied locations with a
'.'. Create a class Animation that contains a method animate that is given an 
int speed and a String init giving the initial conditions. The speed is the 
number of positions each particle moves in one time unit. 

The method will return a String[] in which each successive element shows the 
occupied locations at the next time unit. The first element of the return should 
show the occupied locations at the initial instant (at time = 0) in the 'X','.' 
format. The last element in the return should show the empty chamber at the first
time that it becomes empty. 
 * @author Andrey
 */
public class Animation {
    private final static char LEFT_PARTICLE = 'L';
    private final static char RIGHT_PARTICLE = 'R';
    private final static char EMPTY = '.';
    private final static char DISPLAY_PARTICLE = 'X';

    /**
     * Animate particles movement
     * @param speed particles speed 
     * @param init initial position of particles
     * @return animated particles movement
     */
    public String[] animate(int speed, String init) {
    
        // create frames for left and right particles direction 
        char[] leftFrame = createFrame(init, LEFT_PARTICLE);
        char[] rightFrame = createFrame(init, RIGHT_PARTICLE);
        //  add init frame 
        List<String> animation = new ArrayList<>();
        animation.add( dislpay(leftFrame, rightFrame) );

        int count = init.length();
        
        boolean play;
        do {
            //on start stop play
            play = false;
            //run by the chamber and move each particle on passed way
            //if at least the one particle exists, continue playing
            for (int i = 0; i < count; i ++) {
                //move the left particle, from the begining of chamber, if it exists in 'i' position
                char particle = leftFrame[i];
                if (particle == LEFT_PARTICLE) {
                    int pos = i - speed;
                    if (pos >= 0) {
                        leftFrame[pos] = LEFT_PARTICLE;
                        play = true;
                    }
                    leftFrame[i] = EMPTY;
                }
                
                 //move the right particle, from the ending of chamber, if it exists in 'rp' position
                int rp = count - i - 1;
                particle = rightFrame[rp];                
                if (particle == RIGHT_PARTICLE) {
                    int pos = rp + speed;
                    if (pos < count) {
                        rightFrame[pos] = RIGHT_PARTICLE;
                        play = true;
                    }
                    rightFrame[rp] = EMPTY;
                }
                
            }
            //create display frame and add it to animation
            String dislpay = dislpay(leftFrame, rightFrame);
            animation.add( dislpay );
        } while (play);
        
        String[] rezult = new String[animation.size()];
        
        return animation.toArray(rezult);
    }

    /**
     * Create a display frame from left and right frames
     * @param leftFrame
     * @param rightFrame
     * @return 
     */
    private String dislpay(char[] leftFrame, char[] rightFrame) {
        int length = leftFrame.length;
        char[] dislpay = new char[length];
        
        for (int i=0; i < length; ++i) {
            if (leftFrame[i] == LEFT_PARTICLE
                    || rightFrame[i] == RIGHT_PARTICLE)  {
                dislpay[i] = DISPLAY_PARTICLE;
            } else {
                dislpay[i] = EMPTY;
            }
        }
        return new String(dislpay);
    }

    private char[] createFrame(String particles, char particle) {
        int length = particles.length();
        char[] frame = new char[length];
        for (int i=0; i < length; ++i) {
            if (particles.charAt(i) == particle)  {
                frame[i] = particle;
            } else {
                frame[i] = EMPTY;
            }
        }
        return frame;
    }
    
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Expected two parameters, initial particles string, and speed!");
            return;
        }
        
        String particles = args[0];
        if (!particles.chars().allMatch((ch) -> ch == EMPTY || ch == LEFT_PARTICLE || ch == RIGHT_PARTICLE)) {
            System.out.println("Initial particles string must contains only 'L' or 'R' or '.' !");
            return;
        }
        int speed;
        try {
            speed = Integer.parseInt(args[1]);
        } catch(NumberFormatException nfe) {
            System.out.println("Incorrrect '" + args[1] + "' speed!");
            return;
        }
        
        String[] animated = new  Animation().animate(speed, particles);
        int time = 0;
        for (String frame : animated) {
            System.out.println("Frame in time " + time + ":\t" + frame);
            time++;
        }
    }
}
