import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;

public class MyTetrisPanel extends JPanel {
	
	// 인스턴스 데이터
	private GamePanel gamePanel;
	
	// 생성자
	public MyTetrisPanel() {
		setPreferredSize(new Dimension(430,520));
		setBackground(new Color(111, 137, 168));
		setLayout(null);
		
		gamePanel = new GamePanel();
		gamePanel.setBounds(0,0,440,520);
		gamePanel.setBackground(Color.white);
		add(gamePanel);     
	}
}