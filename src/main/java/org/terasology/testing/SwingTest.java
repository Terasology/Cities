/*
 * Copyright 2013 MovingBlocks
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.terasology.testing;

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

        String seed = "asdfghi";

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
