import java.awt.Color;

/* GamePanel���� ��Ʈ���� ��� �ϳ��� ������ ��� Ŭ����
 * ��� ����� ��Ÿ���� ���� blockNum
 * ��� ������ ��Ÿ���� Color ��ü blockColor
 */
public class BlockModel{
	
	// �ν��Ͻ� ������
	private int blockNum;
	private Color blockColor;

	// ������
	BlockModel() {
		blockNum = 0;
		blockColor = TetrisModel.COLOR[(int)(Math.random()*7)];
	}
	BlockModel(int n, Color color) {
		blockNum = n;
		blockColor = color;
	}

	// getter 
	public int getBlockNum() {return blockNum;}
	public Color getBlockColor() {return blockColor;}
	
	// setter
	public void setBlockNum(int num) {blockNum = num;}
	public void setBlockColor(Color color) {blockColor = color;}
}