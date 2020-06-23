package Group10.Agents.AStar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;

public class AFrame extends JFrame{
	
	private int squareSize;
	private Node[][] matrix;
	
	public AFrame(Node[][] matrix, int squareSize) {
		this.squareSize = squareSize;
		this.matrix = matrix;
		setSize(matrix.length*squareSize*2, matrix[0].length*squareSize*2);
		setResizable(false);
		setVisible(true);  
	}
	
	public void paint( Graphics g )    {  
		/*for (int x = 0; x <= matrix.length*squareSize; x += squareSize) {
			for ( int y = 0; y <=  matrix[0].length*squareSize; y += squareSize) {
				g.drawRect( x*2, y*2, squareSize*2, squareSize*2);
				//g.fillRect(x, y, 30, 30);
			} 
		}*/
	 } 
	
	public void color(Graphics g, int color, int x, int y) {
		if(color == -1) {
			g.setColor(Color.YELLOW);
		}
		else if(color == 0) {
			g.setColor(Color.WHITE);
		}
		else if(color == 1) {
			g.setColor(Color.BLUE);
		}
		else if(color == 2) {
			g.setColor(Color.RED);
		}
		else if(color == 3) {
			g.setColor(Color.GREEN);
		}
		else if(color == 4) {
			g.setColor(Color.CYAN);
		}
		else if(color == 5) {
			g.setColor(Color.DARK_GRAY);
		}
		else if(color == 6) {
			g.setColor(Color.BLACK);
		}
		else if(color == 7) {
			g.setColor(Color.PINK);
		}
		else if(color == 8) {
			g.setColor(Color.ORANGE);
		}
		g.fillRect(x-squareSize, (this.getHeight() - y)-squareSize, squareSize*2, squareSize*2);
	}	
}