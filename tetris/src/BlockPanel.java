import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class BlockPanel extends JPanel {

	private int blockNum;
	private Color blockColor;
	private Image buffImage;
	private Graphics buffg;

	BlockPanel() {
		blockNum = 0;
		blockColor = Color.ORANGE;
		setPreferredSize(new Dimension(80, 80));
	}

	BlockPanel(int n, Color color) {
		blockNum = n;
		blockColor = color;
		setPreferredSize(new Dimension(80, 80));
	}

	public int getBlockNum() {
		return blockNum;
	}

	public Color getBlockColor() {
		return blockColor;
	}

	public void setBlockNum(int num) {
		blockNum = num;
	}

	public void setBlockColor(Color color) {
		blockColor = color;
	}

	@Override
	public void paint(Graphics g) {
		buffImage = createImage(80,80);
		buffg = buffImage.getGraphics();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (TetrisModel.BLOCKS[blockNum][0][i][j] == 1) {
					buffg.setColor(blockColor);
					buffg.fill3DRect(j * TetrisModel.BLOCKSIZE, i * TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE,TetrisModel.BLOCKSIZE, true);
				}
				else {
					buffg.setColor(Color.white);
					buffg.fillRect(j * TetrisModel.BLOCKSIZE, i * TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE,TetrisModel.BLOCKSIZE);
				}
		
		g.drawImage(buffImage, 0, 0, this);
	}
	
	@Override
	public void update(Graphics g) {
		paint(g);
	}
	 
}
