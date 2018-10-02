import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import java.awt.geom.*; 

// the actual game view
public class PlayView extends JPanel implements Observer {
	
	AffineTransform transform = new AffineTransform();
	Ship ship;
	
	private GameModel model;
	boolean pause = true;
	
    public PlayView(GameModel model_, Ship ship_) {
		
		ship = ship_;
		ship.addObserver(this);
		transform.translate(345, 45);
		
		transform.scale(3,3);
		transform.translate(-345, -45);
		
		
		model = model_;
		
		
        // needs to be focusable for keylistener
        setFocusable(true);

        // want the background to be black
        setBackground(Color.BLACK);
		
		this.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
					char c = e.getKeyChar();
					if (c == ' ') {
						if (ship.getCrash() || ship.getLand()){
							ship.reset();
						} else {
							pause = !pause;
							ship.setPaused(pause);
						}
					
					} else if (c == 'a') {
						ship.thrustLeft();
					} else if (c == 'w') {
						ship.thrustUp();
					} else if (c == 's') {
						ship.thrustDown();
					} else if (c == 'd') {
						ship.thrustRight();
					} 
				
			}
		});
		

    }

    @Override
    public void update(Observable o, Object arg) {
		//System.out.println("chen");
		repaint();
		

    }
	
	// some optimization to cache points for drawing
    
    int[] xpoints, ypoints;
    int npoints = 0;

    void cachePointsArray() {
        xpoints = new int[model.terrain.size()];
        ypoints = new int[model.terrain.size()];
        for (int i=0; i < model.terrain.size(); i++) {
            xpoints[i] = (int)model.terrain.get(i).x;
            ypoints[i] = (int)model.terrain.get(i).y;
			model.pointsChanged2 = false;
        }
        npoints = model.terrain.size();
        //model.pointsChanged = false;
    }
	
	//@Override
	public void paintComponent(Graphics g) { // print everything
		
		
		super.paintComponent(g);
		double x = ship.getShape().x;
		double y = ship.getShape().y;
		
		
		transform.setToIdentity();
		transform.scale(3,3);
		
		
		transform.translate(-(x+5), -(y+5));
		
		transform.translate( 115,49 );
		
		
		
		
		Graphics2D g2 = (Graphics2D)g;
        // multiply in this shape's transform
        g2.transform(transform);
		
		
		
		// draw the background 
		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRect((int)model.worldBounds.getX(), (int)model.worldBounds.getY(),
			(int)model.worldBounds.getWidth(),(int)model.worldBounds.getHeight());
		
		if (model.pointsChanged2) 
			cachePointsArray();
		
		
		// draw the terrain
		//Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.DARK_GRAY);
		g2.fillPolygon(xpoints, ypoints, npoints);
			
		// draw the pad 	
		g2.setColor(Color.RED);
		g2.fillRect((int)model.pad.getX(), (int)model.pad.getY(),
			(int)model.pad.getWidth(),(int)model.pad.getHeight());
			
		// draw the ship	
		
		g2.setColor(Color.BLUE);
		
		g2.fillRect((int)x,(int)y,10,10);
		
		
	}
	
	
}
