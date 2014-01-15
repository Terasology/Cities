/*
 * Copyright 2014 MovingBlocks
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

package org.terasology.cities.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.terasology.cities.common.Touch.ThicknessMode;

/**
 * TODO Type description
 * @author mdummer
 */
public class MySwingTest
{
	public static void main(String[] args)
	{
        //Create and set up the window.
        final JFrame frame = new JFrame("Murphy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(new JComponent()
		{
			private static final long serialVersionUID = -3019274194814342555L;
			
			@Override
			protected void paintComponent(final Graphics g)
			{
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.scale(4, 4);
				
				Touch murphy = new Touch() {
					@Override
					protected void  drawPixel(int x, int y, Color aColor) {
						g.setColor(aColor);
						g.drawLine(x, y, x, y);
					}
				};
				
				ThicknessMode mode = Touch.ThicknessMode.MIDDLE;
                Color black = new Color(0x000000);
                Color gray = new Color(0x404040);

                murphy.drawThickLine(22, 12, 83, 44, 5, mode, new Color(0x808000));
				murphy.drawThickLineSimple(12, 52, 74, 28, 5, mode, new Color(0xA0));
                murphy.drawThickLine(50, 70, 80, 70, 5, Touch.ThicknessMode.CLOCKWISE, gray);
                murphy.drawThickLine(30, 70, 100, 70, 1, Touch.ThicknessMode.MIDDLE, black);
                murphy.drawThickLine(50, 80, 80, 80, 5, Touch.ThicknessMode.COUNTERCLOCKWISE, gray);
                murphy.drawThickLine(30, 80, 100, 80, 1, Touch.ThicknessMode.MIDDLE, black);
                murphy.drawThickLine(50, 90, 80, 90, 5, Touch.ThicknessMode.MIDDLE, gray);
                murphy.drawThickLine(30, 90, 100, 90, 1, Touch.ThicknessMode.MIDDLE, black);

                murphy.drawThickLine(-15, 70, 10, 10, 8, Touch.ThicknessMode.MIDDLE, black);
                
//				murphy.drawLine(14, 12, 120, 18, 0xFFFF00FF);
//				murphy.thickPerpBtnClick(new Point(4, 2), new Point(20, 8));
			}
		});
        
        Timer t = new Timer();
        t.schedule(new TimerTask()
		{
			
			@Override
			public void run()
			{
				frame.repaint();
				
			}
		}, 1000, 1000);
        
        frame.setLocation(500, 200);
        frame.setSize(600, 400);
        frame.setVisible(true);
	}
}
