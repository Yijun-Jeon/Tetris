import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MyTetrisPanel extends JPanel {

	JPanel blockPanel;
	GamePanel gamePanel;
	
	public MyTetrisPanel() {
		setPreferredSize(new Dimension(510,540));
		setBackground(Color.white);
		setLayout(null);
		
		gamePanel = new GamePanel();
		gamePanel.setBounds(10,10,490,520);
		gamePanel.setBackground(Color.white);
		gamePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		add(gamePanel);
		
		blockPanel = new JPanel();
		blockPanel.setBounds(310,120,160,380);
		blockPanel.setBackground(Color.white);
		blockPanel.setBorder(BorderFactory.createTitledBorder("NEXT"));
		gamePanel.add(blockPanel);
	}
}
