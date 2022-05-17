import java.awt.Color;

public class BlockPanel{

	private int blockNum;
	private Color blockColor;

	BlockPanel() {
		blockNum = 0;
		blockColor = Color.ORANGE;
	}

	BlockPanel(int n, Color color) {
		blockNum = n;
		blockColor = color;
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
