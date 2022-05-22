import javax.swing.JFrame;

public class MyTetrisApp {

	public static void main(String[] args) {
		
		JFrame frame = new JFrame("TETRIS");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		
		
		GamePanel primary = new GamePanel();
		frame.getContentPane().add(primary);
		
		frame.pack();
		frame.setVisible(true);
	}
}