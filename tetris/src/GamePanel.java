import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GamePanel extends JPanel implements Runnable{

	private int end;
	private int random1, random2;
	private int random;
	private BlockPanel[] nextBlocks;
	private int score;
	private int width, height;
	private int rotation;
	private int count1, count2;
	private boolean gameOver;
	private int curX[], curY[];
	private JPanel nextPanel;
	private JButton btn;
	private JLabel lblScoreNum, lblScore, lblStage, lblStageNum, lblDialog;
	private JDialog JD;
	private Thread TetrisThread;
	
	public GamePanel() {
		
		end = 0;
		random1 = 0; random2 = (int)(Math.random()*7);
		score = 0;
		
		width = 100;
		height = 0;
		rotation = 0;
		
		count1 = 0; count2 = 0;
		gameOver = false;
		
		curX = new int [4];
		curY = new int [4];
		
		this.setLayout(null);
		this.setPreferredSize(new Dimension(720,600));
		
		btn = new JButton("재도전");
		btn.addActionListener(new BtnListener());
		
		lblStage = new JLabel("STAGE", SwingConstants.CENTER);
        lblStage.setFont(new Font("arial",Font.BOLD,13));
		lblStage.setBounds(310,30,160,15);
		add(lblStage);
		
		lblStageNum = new JLabel("1", SwingConstants.CENTER);
        lblStageNum.setFont(new Font("arial",Font.PLAIN,13));
        lblStageNum.setBounds(310,50,160,15);
		add(lblStageNum);
		
		lblScore = new JLabel("SCORE",SwingConstants.CENTER);
        lblScore.setFont(new Font("arial",Font.BOLD,13));
        lblScore.setBounds(310,80,160,15);
        add(lblScore);
        
		lblScoreNum = new JLabel(Integer.toString(score*100), SwingConstants.CENTER);
		lblScoreNum.setFont(new Font("arial",Font.PLAIN,13));
		lblScoreNum.setBounds(310, 100, 160, 15);
		add(lblScoreNum);
	
        lblDialog = new JLabel();
        
        JD = new JDialog();
		JD.setTitle("점수");
		JD.setSize(250,190);
		JD.setLayout(new FlowLayout(FlowLayout.CENTER,150,30));
		JD.add(btn);
		JD.add(lblDialog);
		
		nextPanel = new JPanel();
		nextPanel.setBounds(310,120,160,380);
		nextPanel.setBackground(Color.white);
		nextPanel.setBorder(BorderFactory.createTitledBorder("NEXT"));
		this.add(nextPanel);
		
		nextBlocks = new BlockPanel[4];
		for(int i=0;i<4;i++) {
			random = (int)(Math.random()*7);
			nextBlocks[i] = new BlockPanel();
			nextBlocks[i].setBlockNum((int)(Math.random()*7));
			nextBlocks[i].setBlockColor(Color.BLUE);
			nextPanel.add(nextBlocks[i]);
		}
		
		this.addKeyListener(new KeyBoardListener());
		this.setFocusable(true);
		this.requestFocus(true);
		this.start();
	}
	
	public void paintComponent(Graphics page) {
		super.paintComponent(page);
		this.requestFocus(true);
		this.setBackground(Color.white);
		
		page.setColor(Color.GRAY);
		page.draw3DRect(28, 70, 5, 395, true); //기둥
		page.draw3DRect(245, 70, 5, 395, true);
		page.draw3DRect(15, 465, 248, 5, true);//바닥
		page.draw3DRect(15, 65, 248, 5, true);//천장
		
		page.setColor(Color.orange);
		
		lblScoreNum.setText(Integer.toString(score*100));
		
		gameOverCheck();
		previewBlock(page);
		removeLine(count1, count2, page);
		blockToWall();
		makeWall(page);
		
		if (end == 1) {
			random2 = (int)(Math.random()*7);
			end = 0;
		}
	}
		
	public void previewBlock(Graphics g) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (TetrisModel.BLOCKS[random2][0][i][j] == 1)
					g.fill3DRect(j*TetrisModel.BLOCKSIZE+120,  i*TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
	}
	
	public void gameOverCheck() {
		for (int i = 1; i < 11; i++) {
			if (TetrisModel.GAMEBOARD[2][i] == 1) {
				gameOver = true;
				
				lblDialog.setText(lblScoreNum.getText());
				JD.setVisible(true);
			}
		}
	}
	
	public void removeLine(int count1, int count2, Graphics g) {
		for (int y = 0; y < 20; y++) {
			for (int x = 1; x < 11 ; x++)
				if (TetrisModel.GAMEBOARD[y][x] == 1)
					count2++;
			if (count2 == 10) {
				for (int i = y; i > 1; i--) {
					for (int j = 1; j < 12; j++) {
						TetrisModel.GAMEBOARD[i][j] = 0;
						TetrisModel.GAMEBOARD[i][j] = TetrisModel.GAMEBOARD[i-1][j];
					}
				}
				score++;
			}
			else
				blockDown(count1, g);
			count2 = 0;
		}
	}
	
	public void blockDown(int count1, Graphics g) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (TetrisModel.BLOCKS[random1][rotation][i][j] == 1) {
					curX[count1] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					curY[count1] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					g.fill3DRect(curX[count1]*TetrisModel.BLOCKSIZE+20, curY[count1]*TetrisModel.BLOCKSIZE+60, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
					count1++;
				}
			}
		}
	}
	
	public void makeWall(Graphics g) {
		g.setColor(Color.gray);
		for (int y = 0; y < 20; y++)
			for (int x = 1; x < 11; x++)
				if (TetrisModel.GAMEBOARD[y][x] == 1)
					g.fill3DRect(x*TetrisModel.BLOCKSIZE+20, y*TetrisModel.BLOCKSIZE+60, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
	}
	
	public void blockToWall() {
		try {
			for (int i = 0; i < 4; i++) {
				if (TetrisModel.GAMEBOARD[curY[i]+1][curX[i]] == 1) {
					for (int j = 0; j < 4; j++) {
						TetrisModel.GAMEBOARD[curY[j]][curX[j]] = 1;
						width = 100;
						height = 0;
						end = 1;
						rotation = 0;
						random1 = random2;
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) { System.out.println("Error"); };
		for (int i = 0; i < 4; i++)
			System.out.print("("+curY[i]+","+curX[i]+")");
		System.out.println();
	}
	
	public void rotationCheck() {
		int count2 = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int rotation2 = (rotation%4) + 1;
				if (rotation2 == 4)
					rotation2 = 0;
				if (TetrisModel.BLOCKS[random1][rotation2][i][j] == 1) {
					curX[count2] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					curY[count2] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					count2++;
				}
			}
		}
		
		int check = 0, blank = 0, error = 0;
		if ((TetrisModel.GAMEBOARD[curY[0]][curX[0]] == 1) || (random1 == 6 && TetrisModel.GAMEBOARD[curY[2]][curX[2]] == 1) || (random1 == 1 && TetrisModel.GAMEBOARD[curY[1]][curX[1]] == 1)) {
			check = 1;
			error++;
			System.out.println("check1");
			if (random1 == 3) {
				for (int i = 1; i < 5; i++)
					if (TetrisModel.GAMEBOARD[curY[0]][curX[0]+i] == 0)
						blank++;
				if (blank < 4)
					check = 4;
				System.out.println(blank);
			}
			else {
				for (int i = 1; i < 4; i++)
					if (TetrisModel.GAMEBOARD[curY[0]][curX[0]+i] == 0)
						blank++;
				if (blank < 3)
					check = 4;
				System.out.println("blank2");
				System.out.println(blank);
			}
		}
		else if (TetrisModel.GAMEBOARD[curY[2]][curX[2]] == 1) {
			error++;
			check = 2;
			System.out.println("check2");
			for (int i = 1; i < 5; i++)
				if (TetrisModel.GAMEBOARD[curY[2]][curX[2]-i] == 0)
					blank++;
			if (blank < 4)
				check = 4;
			System.out.println("blank2");
			System.out.println(blank);
		}
		else if (TetrisModel.GAMEBOARD[curY[3]][curX[3]] == 1) {
			error++;
			check = 3;
			System.out.println("check3");
			for (int i = 0; i < 5; i++)
				if (TetrisModel.GAMEBOARD[curY[3]][curX[3]-i] == 0)
					blank++;
			if (blank < 4)
				check = 4;
			System.out.println(blank);
		}
		
		if (check == 1) {
			width += TetrisModel.BLOCKSIZE;
			rotation++;
			rotation = rotation % 4;
		}
		else if (check == 2) {
			width -= TetrisModel.BLOCKSIZE * 2;
			rotation++;
			rotation = rotation % 4;
		}
		else if (check == 3) {
			width -= TetrisModel.BLOCKSIZE;
			rotation++;
			rotation = rotation % 4;
		}
		else if (check == 4)
			System.out.println("ban");
		else {
			rotation++;
			rotation = rotation % 4;
		}
	}
	
	public boolean collisionLeft() {
		for (int i = 0; i < 4; i++)
			if (TetrisModel.GAMEBOARD[curY[i]][curX[i]-1] == 1)
				return true;
		return false;
	}
	public boolean collisionRight() {
		for (int i = 0; i < 4; i++)
			if (TetrisModel.GAMEBOARD[curY[i]][curX[i]+1] == 1)
				return true;
		return false;
	}
	
	private void rotateBlock() {
		rotationCheck();
		if(gameOver == false)
			this.repaint();
	}
	private void moveDown() {
		if (gameOver == false) {
			height += TetrisModel.BLOCKSIZE;
			this.repaint();
		}
	}
	private void moveLeft() {
		boolean collision = collisionLeft();
		if (collision == false && gameOver == false) {
			width -= TetrisModel.BLOCKSIZE;
			this.repaint();
		}
	}
	private void moveRight() {
		boolean collision = collisionRight();
		if (collision == false && gameOver == false) {
			width += TetrisModel.BLOCKSIZE;
			this.repaint();
		}
	}
	
	public void down() {
		height += TetrisModel.BLOCKSIZE;
		repaint();
	}
	
	public void start() {
		if (TetrisThread == null)
			TetrisThread = new Thread(this);
		TetrisThread.start();
	}
	
	public void run() {
		while (true) {
			try {
				TetrisThread.sleep(300);
				if (gameOver == false)
					down();
			} catch(InterruptedException e) {
				return;
			}
		}
	}
	
	private class KeyBoardListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent e) { 
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_UP)
				rotateBlock();
			if (keyCode == KeyEvent.VK_DOWN)
				moveDown();
			if (keyCode == KeyEvent.VK_LEFT)
				moveLeft();
			if (keyCode == KeyEvent.VK_RIGHT)
				moveRight();
		}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}	
	}
	
	private class BtnListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			gameOver = false;
			for (int y = 0; y < 20; y++)
				for (int x = 1; x < 11; x++)
					TetrisModel.GAMEBOARD[y][x] = 0;
			score = 0; width = 100; height = 0;
		}
	}
}
