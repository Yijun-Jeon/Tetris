import java.awt.Color;

/* GamePanel에서 테트리스 블록 하나의 정보를 담는 클래스
 * 블록 모양을 나타내는 정수 blockNum
 * 블록 색깔을 나타내는 Color 객체 blockColor
 */
public class BlockModel{
	
	// 인스턴스 데이터
	private int blockNum;
	private Color blockColor;

	// 생성자
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