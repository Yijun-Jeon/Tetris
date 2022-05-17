import java.awt.Color;

public class BlockModel{

	private int blockNum;
	private Color blockColor;

	BlockModel() {
		blockNum = 0;
		blockColor = TetrisModel.COLOR[(int)(Math.random()*7)];
	}

	BlockModel(int n, Color color) {
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