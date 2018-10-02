/*
ENHANCEMENT: 

	- when the ship crash, the ship breaks in two, and a red "CRASH" text appear
	
	- when the ship lands, a little person comes out with his hands up, a green "LANDED" text appear

*/

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class A3Enhanced extends JPanel {

    A3Enhanced() {
        // create the model
        GameModel model = new GameModel(60, 700, 200, 20);
		Ship ship = new Ship(60,350,50, model);

        PlayViewE playView = new PlayViewE(model, ship);
		model.addObserver(playView);
		ship.addObserver(playView);
        EditView editView = new EditView(model);
		model.addObserver(editView);
        editView.setPreferredSize(new Dimension(700, 200));

        // layout the views
        setLayout(new BorderLayout());
		MessageView messageview = new MessageView(model,ship);
        add(messageview, BorderLayout.NORTH);
		ship.addObserver(messageview);

        // nested Border layout for edit view
        JPanel editPanel = new JPanel();
		
		ToolBarView toolbarview = new ToolBarView(model);
		model.addObserver(toolbarview);
        editPanel.setLayout(new BorderLayout());
        editPanel.add(toolbarview, BorderLayout.NORTH);
        editPanel.add(editView, BorderLayout.CENTER);
        add(editPanel, BorderLayout.SOUTH);

        // main playable view will be resizable
        add(playView, BorderLayout.CENTER);

        // for getting key events into PlayView
        playView.requestFocusInWindow();

    }

    public static void main(String[] args) {
        // create the window
        JFrame f = new JFrame("A3Enhanced"); // jframe is the app window
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(700, 600); // window size
        f.setContentPane(new A3Enhanced()); // add main panel to jframe
        f.setVisible(true); // show the window
    }
}
