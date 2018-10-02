import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;
import java.awt.geom.*; 
import javax.vecmath.*;

// the editable view of the terrain and landing pad
public class EditView extends JPanel implements Observer {
	
	enum DRAG { PAD, TERRAIN, NONE }
	
	DRAG drag = DRAG.NONE;
	
	//boolean padDrag = true; // flag for moving pad
	
	Point2d last = new Point2d(0,0);

	GameModel model;

    public EditView(GameModel model_) {
		model = model_;
		// for double clicks 
		this.addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e) {
				if(e.getClickCount()==2){
					model.movePad(e.getX(), e.getY());
				} else {
					last.x = e.getX();
					last.y = e.getY();
					if (model.padHitTest(e.getX(), e.getY())) {
					
						drag = DRAG.PAD;
						
					} else if (model.terrainHittest(e.getX(), e.getY())) {
						drag = DRAG.TERRAIN;
					
					
					} else {
						drag = DRAG.NONE;
						
						
					
					
					}	
				}
            }
        });
		
		this.addMouseListener(new MouseAdapter(){
            public void mouseReleased(MouseEvent e) {
				if (drag == DRAG.PAD) {
					model.setShadowPad();
				} else if (drag == DRAG.TERRAIN) {
					model.setShadowTerrain();
				}
				drag = DRAG.NONE;
				
			}
		});
		
		this.addMouseMotionListener(new MouseAdapter(){
            public void mouseDragged(MouseEvent e) {
							
				if (drag == DRAG.PAD) {
					model.shiftPad((double)(e.getX())-last.x, 
					      (double)(e.getY()-last.y));
					last.x += (double)(e.getX())-last.x;
					last.y += (double)(e.getY())-last.y;
					
				} else if (drag == DRAG.TERRAIN) {
					model.shiftTerrain((double)(e.getY()-last.y));
					last.y += (double)(e.getY())-last.y;
				}
            }
        }); 
		
        // want the background to be black
        setBackground(Color.BLACK);
		

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
        }
        npoints = model.terrain.size();
		model.pointsChanged1 = false;
        
    }
	
	@Override
	public void paintComponent(Graphics g) { // print everything
		
		
		super.paintComponent(g);
		
		// draw the background 
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect((int)model.worldBounds.getX(), (int)model.worldBounds.getY(),
			(int)model.worldBounds.getWidth(),(int)model.worldBounds.getHeight());
		
		if (model.pointsChanged1) 
			cachePointsArray();
		
		// draw circle 
		g.setColor(Color.GRAY);
		for (int i=1; i < model.terrain.size()-1; i++) {
            int x = (int)model.terrain.get(i).x;
            int y = (int)model.terrain.get(i).y;
			g.drawOval(x-15,y-15,30, 30);
        }
		
		
		// draw the terrain
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.DARK_GRAY);
		g2.fillPolygon(xpoints, ypoints, npoints);
			
		// draw the pad 	
		g.setColor(Color.RED);
		g.fillRect((int)model.pad.getX(), (int)model.pad.getY(),
			(int)model.pad.getWidth(),(int)model.pad.getHeight());
		
	}

}
