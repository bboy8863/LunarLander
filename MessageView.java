import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;
import java.text.DecimalFormat;

public class MessageView extends JPanel implements Observer {

    // status messages for game
    JLabel fuel = new JLabel("Fuel: 50");
    JLabel speed = new JLabel("Speed: 0.00");
    JLabel message = new JLabel("Paused");
	Ship ship;

    public MessageView(GameModel model, Ship ship_) {

		//model= model_;
		ship = ship_;
		
        // want the background to be black
        setBackground(Color.BLACK);

        setLayout(new FlowLayout(FlowLayout.LEFT));

        add(fuel);
        add(speed);
        add(message);

        for (Component c: this.getComponents()) {
            c.setForeground(Color.WHITE);
            c.setPreferredSize(new Dimension(100, 20));
        }
		speed.setForeground(Color.GREEN);
		
    }


    @Override
    public void update(Observable o, Object arg) {
		int f = (int)ship.getFuel();
		if (f < 10) {
			fuel.setForeground(Color.RED);
		} else {
			fuel.setForeground(Color.WHITE);
		}
		fuel.setText("Fuel: " +f);
		
		
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		String temp = decimalFormat.format(ship.getSpeed());
		
		if (ship.getSpeed() <= ship.getSafeLandingSpeed()) {
			speed.setForeground(Color.GREEN);
		} else {
			speed.setForeground(Color.WHITE);
		}
		speed.setText("Speed: " +temp);
		
		if (ship.getCrash()){
			message.setText("CRASH");
		} else if (ship.getLand()) {
			message.setText("LANDED!");
		} else if (ship.isPaused()) {
			message.setText("(Paused)");
		} else {
			message.setText("");
		}
    }
}