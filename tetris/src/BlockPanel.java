import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;

public class BlockPanel extends JPanel {
	
	private int blockNum;
	private Color blockColor;
	
	BlockPanel(){
		blockNum = 0;
		blockColor = Color.ORANGE;
		setPreferredSize(new Dimension(80,80));
	}
	BlockPanel(int n, Color color){
		blockNum = n;
		blockColor = color;
		setPreferredSize(new Dimension(80,80));
	}
	public int getBlockNum() {return blockNum;}
	public Color getBlockColor() {return blockColor;}
	public void setBlockNum(int num) {blockNum = num;}
	public void setBlockColor(Color color) {blockColor = color;}
	
	
	public void paintComponent(Graphics g) {
		g.setColor(blockColor);
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (TetrisModel.BLOCKS[blockNum][0][i][j] == 1)
					g.fill3DRect(j*TetrisModel.BLOCKSIZE,  i*TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
	}
	
}
