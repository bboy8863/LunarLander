import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.undo.*;
import javax.vecmath.*;
import java.util.ArrayList;

import java.util.concurrent.ThreadLocalRandom;

public class GameModel extends Observable {
	Rectangle2D.Double pad;
	Rectangle2D.Double padShadow; // old position of the pad while dragging
	Boolean pointsChanged1 = false; // dirty bit
	Boolean pointsChanged2 = false;
	Point2d currentPoint = null;
	Point2d currentPointShadow = null;
	int currentIndex; // the index for undo
	private UndoManager undoManager;
	
	public ArrayList<Point2d> terrain = new ArrayList<Point2d>();


    public GameModel(int fps, int width, int height, int peaks) {

		
		pad = new Rectangle2D.Double(330, 100, 40, 10);
		padShadow = new Rectangle2D.Double(330, 100, 40, 10);
		
		currentPointShadow = new Point2d();
		
		undoManager = new UndoManager();
		
		
        //ship = new Ship(60, width/2, 50);
		
		//terrain
		terrain.add(new Point2d(0, 200));
		for (int i = 0; i < 20; ++i) {
			int r = ThreadLocalRandom.current().nextInt(height/2, height);
			terrain.add(new Point2d(i*((double)width/19), r));
			
		}
		pointsChanged1 = true;
		pointsChanged2 = true;
		
		
		
		
		terrain.add(new Point2d(700, 200));

        worldBounds = new Rectangle2D.Double(0, 0, width, height);

       
		
    }
	

    // World
    // - - - - - - - - - - -
    public final Rectangle2D getWorldBounds() {
        return worldBounds;
    }

    Rectangle2D.Double worldBounds;


    // Ship
    // - - - - - - - - - - -

    public Ship ship;

    // Observerable
    // - - - - - - - - - - -

    // helper function to do both
    void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }
	// pad
	
	boolean padHitTest(int x, int y) {
		boolean temp = (x >= pad.getX() && x <= pad.getX() + pad.getWidth()) && 
					(y >= pad.getY() && y <= pad.getY() + pad.getHeight());
		return temp;
	}
	void movePad(int x, int y) { // x and y are new center of the pad 
		UndoableEdit undoableEdit = new AbstractUndoableEdit() {
			// capture variables for closure
			final Rectangle2D.Double oldValue = 
					new Rectangle2D.Double(pad.getX(), pad.getY(),
									pad.getWidth(), pad.getHeight());
			
			final Rectangle2D.Double newValue = new Rectangle2D.Double(x-20,y-5,40,10);

			// Method that is called when we must redo the undone action
			public void redo() throws CannotRedoException {
				super.redo();
				pad = newValue;
				//System.out.println("Model: redo value to " );
				setChanged();
				notifyObservers();
			}

			public void undo() throws CannotUndoException {
				super.undo();
				pad = oldValue;
				//System.out.println("Model: undo value to " );
				setChanged();
				notifyObservers();
			}
		};
		
		// Add this undoable edit to the undo manager
		undoManager.addEdit(undoableEdit);
		pad.setRect(x-20,y - 5, 40, 10);
		setChangedAndNotify();
	}
	void shiftPad(double x, double y) { // translate them
		if (pad.x + x > 0 && pad.x+x < 700-40 && 
				pad.y+y > 0 && pad.y+y< 200-10) {
			pad.setRect(pad.getX() + x, pad.getY() + y, 40, 10);
			setChangedAndNotify();
		}
	}
	
	void setShadowPad() {
		UndoableEdit undoableEdit = new AbstractUndoableEdit() {
			// capture variables for closure
			final Rectangle2D.Double oldValue = 
					new Rectangle2D.Double(padShadow.getX(), padShadow.getY(),
									padShadow.getWidth(), padShadow.getHeight());
			
			final Rectangle2D.Double newValue = new Rectangle2D.Double(pad.getX(), pad.getY(),
									pad.getWidth(), pad.getHeight());

			// Method that is called when we must redo the undone action
			public void redo() throws CannotRedoException {
				super.redo();
				pad = newValue;
				//System.out.println("Model: redo value to " );
				padShadow.setRect(pad.getX(), pad.getY(), 40, 10);
				setChanged();
				notifyObservers();
			}

			public void undo() throws CannotUndoException {
				super.undo();
				pad = oldValue;
				padShadow.setRect(pad.getX(), pad.getY(), 40, 10);
				//System.out.println("Model: undo value to " );
				setChanged();
				notifyObservers();
			}
		};
		// Add this undoable edit to the undo manager
		undoManager.addEdit(undoableEdit);
		
		padShadow.setRect(pad.getX(), pad.getY(), 40, 10);
		setChangedAndNotify();
	}
	
	// terrain ----------------------------------
	boolean terrainHittest(int x, int y) {
		for (int i=1; i < terrain.size()-1; i++) {
			int pointx = (int)terrain.get(i).x;
			int pointy = (int)terrain.get(i).y;
			double d = ((x - pointx)*(x - pointx) + 
				(x - pointx)*(x - pointx));
			if (d <= 225) {
				currentIndex = i;
				currentPoint = terrain.get(i);
				currentPointShadow.x = currentPoint.x;
				currentPointShadow.y = currentPoint.y;
				return true;
			}
		}
		currentPoint = null;
		return false;
	}

	void shiftTerrain(double y) {
		if (currentPoint != null) {
			pointsChanged1 = true;
			pointsChanged2 = true;
			if (currentPoint.y + y < 200 && currentPoint.y + y> 0) {
				setChangedAndNotify();
				currentPoint.y+=y;
			}
			
		}
	}
	void setShadowTerrain() {
		UndoableEdit undoableEdit = new AbstractUndoableEdit() {
			// capture variables for closure
			final int i = currentIndex;
			
			final double oldValue = currentPointShadow.y;
			
			final double newValue = currentPoint.y;

			// Method that is called when we must redo the undone action
			public void redo() throws CannotRedoException {
				super.redo();
				terrain.get(i).y = newValue;
				//System.out.println("Model: redo value to " );
				pointsChanged1 = true;
				pointsChanged2 = true;
				setChanged();
				notifyObservers();
			}

			public void undo() throws CannotUndoException {
				super.undo();
				terrain.get(i).y = oldValue;
				//System.out.println("Model: undo value to " );
				pointsChanged1 = true;
				pointsChanged2  = true;
				setChanged();
				notifyObservers();
			}
		};
		// Add this undoable edit to the undo manager
		undoManager.addEdit(undoableEdit);
		
		currentPointShadow.x = currentPoint.x;
		currentPointShadow.y = currentPoint.y;
		setChangedAndNotify();
		
		
	}
	// undo and redo methods
	// - - - - - - - - - - - - - -

	public void undo() {
		if (canUndo())
			undoManager.undo();
	}

	public void redo() {
		if (canRedo())
			undoManager.redo();
	}

	public boolean canUndo() {
		return undoManager.canUndo();
	}

	public boolean canRedo() {
		return undoManager.canRedo();
	}

}



