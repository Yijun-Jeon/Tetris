import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class BlockPanel extends JPanel {
	
	private int block;
	
	BlockPanel(){
		
		block = 0;
		
		setPreferredSize(new Dimension(80,80));
	}
	
	public void paintComponent(Graphics g) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (TetrisModel.BLOCKS[block][0][i][j] == 1)
					g.fill3DRect(j*TetrisModel.BLOCKSIZE,  i*TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
	}
	
}
