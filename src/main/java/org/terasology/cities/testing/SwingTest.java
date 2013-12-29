/*
 * Copyright 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.cities.testing;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A simple JFrame-based test class with a main method
 * @author Martin Steiger
 */
public final class SwingTest {

    private SwingTest() {
        // private
    }

    /**
     * @param args ignored
     */
    public static void main(String[] args) {

        String seed = "a";

        final JFrame frame = new JFrame();
        final JLabel status = new JLabel();

        frame.add(new JCityComponent(seed, status));
        frame.add(status, BorderLayout.SOUTH);
        frame.setTitle("City renderer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.setAlwaysOnTop(true);
        
        // align right border at the right border of the default screen 
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        frame.setLocation(screenWidth - frame.getWidth(), 100);
        
        // repaint every second to see changes while debugging
        int updateInteval = 1000;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                frame.repaint();
            }
            
        }, updateInteval, updateInteval);
        
        frame.setVisible(true);
    }
}
