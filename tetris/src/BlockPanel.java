import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;

public class BlockPanel extends JPanel {

	private int blockNum;
	private Color blockColor;

	BlockPanel() {
		blockNum = 0;
		blockColor = TetrisModel.COLOR[(int)(Math.random()*7)];
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
}