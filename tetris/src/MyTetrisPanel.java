import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyTetrisPanel extends JPanel {
	
	GamePanel gamePanel;
	
	public MyTetrisPanel() {
		setPreferredSize(new Dimension(460,540));
		setBackground(new Color(111, 137, 168));
		setLayout(null);
		
		gamePanel = new GamePanel();
		gamePanel.setBounds(10,10,440,520);
		gamePanel.setBackground(Color.white);
		add(gamePanel);
	
	
            
	}
	
}