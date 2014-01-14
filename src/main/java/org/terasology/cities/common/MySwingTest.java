
package org.terasology.cities.common;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JFrame;

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
					protected void  drawPixel(int x, int y, int aColor) {
						g.setColor(new Color(aColor));
						g.drawLine(x, y, x, y);
					}
				};
				
				int mode = Touch.LINE_THICKNESS_MIDDLE;
				murphy.drawThickLine(12, 12, 83, 44, 5, mode, 0x404000);
				murphy.drawThickLineSimple(12, 12, 34, 78, 5, mode, 0x80);
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
