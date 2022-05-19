import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class GamePanel extends JPanel implements Runnable{

	private int end;
	private int nBlock;
	private Color color;
	private BlockPanel[] nextBlocks;
	private int score;
	private int width, height, weight, temp;
	private int rotation;
	private int count1, count2, count3;
	private boolean gameOver, firstRun, bTeleport;
	private int curX[], curY[], silhouetteX[], silhouetteY[], tempY[];
	private JPanel nextPanel;
	private JButton btn, btnStart;
	private JLabel lblScoreNum, lblScore, lblStage, lblStageNum, lblDialog, lblFirst;
	private JDialog JD;
	private Thread TetrisThread;
	ImageIcon backgroundImg, firstImg, startImg, scoreImg, stageImg;
	
	public GamePanel() {
		
		end = 0;
		nBlock = 0;
		score = 0;
		
		width = 100;
		height = 0;
		weight = 340;
		temp = 0;
		rotation = 0;
		
		count1 = 0; count2 = 0;
		gameOver = false;
		
		curX = new int [4];
		curY = new int [4];
		silhouetteX = new int [4];
		silhouetteY = new int [4];
		tempY = new int [4];
		bTeleport = false;
		
		firstRun = true;
		
		this.setLayout(null);
		this.setPreferredSize(new Dimension(680,600));

		//
		firstImg = new ImageIcon("./img/first.png");
	    lblFirst = new JLabel(firstImg);
	    lblFirst.setBounds(0,0,440,520);
	    lblFirst.setVisible(firstRun);
	    add(lblFirst);
       
		stageImg = new ImageIcon("./img/stage.png");
		lblStage = new JLabel(stageImg, SwingConstants.CENTER);
        lblStage.setFont(new Font("arial",Font.BOLD,15));
        lblStage.setForeground(new Color(68, 68, 173));
		lblStage.setBounds(270,50,130,20);
		add(lblStage);
		
		lblStageNum = new JLabel("1", SwingConstants.CENTER);
        lblStageNum.setFont(new Font("arial",Font.BOLD,13));
        lblStageNum.setForeground(Color.white);
        lblStageNum.setBounds(270,75,130,15);
		add(lblStageNum);
		
		scoreImg = new ImageIcon("./img/score.png");
		lblScore = new JLabel(scoreImg,SwingConstants.CENTER);
        lblScore.setFont(new Font("arial",Font.BOLD,15));
        lblScore.setForeground(new Color(68, 68, 173));
        lblScore.setBounds(270,95,130,20);
        add(lblScore);
        
		lblScoreNum = new JLabel(Integer.toString(score*100), SwingConstants.CENTER);
		lblScoreNum.setFont(new Font("arial",Font.BOLD,13));
		lblScoreNum.setForeground(Color.white);
		lblScoreNum.setBounds(270, 120, 130, 15);
		add(lblScoreNum);
		
        lblDialog = new JLabel();
  
        startImg = new ImageIcon("./img/start.png");
        btnStart = new JButton("");
        btnStart.setIcon(startImg);
        btnStart.setBounds(140, 300, 160, 50);
        btnStart.setBackground(new Color(0,0,0,0));
        btnStart.setForeground(new Color(0,0,0,0));
        btnStart.setVisible(firstRun);
        btnStart.setBorderPainted(false);
        btnStart.addActionListener(new BtnListener());
        lblFirst.add(btnStart);
       
    	btn = new JButton("재도전");
		btn.addActionListener(new BtnListener());
        
        JD = new JDialog();
		JD.setTitle("Á¡¼ö");
		JD.setSize(250,190);
		JD.setLayout(new FlowLayout(FlowLayout.CENTER,150,30));
		JD.add(btn);
		JD.add(lblDialog);
		
		nextPanel = new JPanel();
		nextPanel.setBounds(270,160,140,340);
		nextPanel.setBackground(new Color(236, 236, 237, 0));
		this.add(nextPanel);
		
		nextBlocks = new BlockPanel[4];
		for(int i=0;i<4;i++) {
			nextBlocks[i] = new BlockPanel();
			nextBlocks[i].setBlockNum((int)(Math.random()*7));
			nextBlocks[i].setBlockColor(TetrisModel.COLOR[(int)(Math.random()*7)]); //
			color = nextBlocks[i].getBlockColor();
		}
		
		backgroundImg = new ImageIcon("./img/background.png");
			
		sound("sound/bgm.wav");
		this.addKeyListener(new KeyBoardListener());
		this.setFocusable(true);
		this.requestFocus(true);
		this.start();
	}
	
	public void paintComponent(Graphics page) {
		super.paintComponent(page);
		this.requestFocus(true);
		this.setBackground(new Color(15,24,55));
		
		page.drawImage(backgroundImg.getImage(), 0, 0, null);
		setOpaque(false);
	
		page.setColor(new Color(236, 236, 237, 127));
		page.fillRoundRect(28, 95, 222, 405, 20,20); // 
		page.fillRoundRect(270,160,130,340, 20,20); // 
		page.fillRoundRect(270,35,130,110, 20,20); // 
		
		
		lblScoreNum.setText(Integer.toString(score*100));
		
		drawNextBlocks(page);
		gameOverCheck();
		removeLine(count1, count2, count3, page);
		blockToWall();
		makeWall(page);
		
		if (end == 1) {
			blockToNext();
			end = 0;
		}
	}
	
	public void drawNextBlocks(Graphics g) {
		for(int i=0;i<4;i++) {
			for (int y = 0; y < 4; y++)
				for (int x = 0; x < 4; x++)
					if (TetrisModel.BLOCKS[nextBlocks[i].getBlockNum()][0][y][x] == 1) {
						g.setColor(nextBlocks[i].getBlockColor());
						g.fill3DRect(x * TetrisModel.BLOCKSIZE + 300,
								y * TetrisModel.BLOCKSIZE + 170 + i*80,
								TetrisModel.BLOCKSIZE,TetrisModel.BLOCKSIZE, true);
					}
		}
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
	
	public void removeLine(int count1, int count2, int count3, Graphics g) {
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
			else {
				blockDown(count1, g);
				makeSilhouette(count3, g);
			}
			count2 = 0;
		}
	}
	
	public void blockDown(int count1, Graphics g) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (TetrisModel.BLOCKS[nBlock][rotation][i][j] == 1) {
					curX[count1] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					curY[count1] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					g.setColor(color);
					g.fill3DRect(curX[count1]*TetrisModel.BLOCKSIZE+20, curY[count1]*TetrisModel.BLOCKSIZE+90, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
					count1++;
				}
			}
		}
	}
	
	public void makeSilhouette(int count3, Graphics g) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (TetrisModel.BLOCKS[nBlock][rotation][i][j] == 1) {
					silhouetteX[count3] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					silhouetteY[count3] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					tempY[count3] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					temp = 0;
					while (true) {
						if (TetrisModel.GAMEBOARD[tempY[count3]][silhouetteX[count3]] == 0) {
							temp += 20;
							tempY[count3] = (i*TetrisModel.BLOCKSIZE+height+temp)/TetrisModel.BLOCKSIZE;
						}
						else
							break;
					}
					if (weight > temp)
						weight = temp;
					count3++;
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			g.drawRect(silhouetteX[i]*TetrisModel.BLOCKSIZE+20, (silhouetteY[i]+weight/20)*TetrisModel.BLOCKSIZE+70, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE);
		}
	}
	
	public void makeWall(Graphics g) {
		g.setColor(Color.gray);
		for (int y = 0; y < 20; y++)
			for (int x = 1; x < 11; x++)
				if (TetrisModel.GAMEBOARD[y][x] == 1)
					g.fill3DRect(x*TetrisModel.BLOCKSIZE+20, y*TetrisModel.BLOCKSIZE+90, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
	}
	
	public void blockToWall() {
		try {
			for (int i = 0; i < 4; i++) {
				if (TetrisModel.GAMEBOARD[curY[i]+1][curX[i]] == 1) {
					for (int j = 0; j < 4; j++) {
						TetrisModel.GAMEBOARD[curY[j]][curX[j]] = 1;
						width = 100;
						height = 0;
						weight = 340;
						temp = 0;
						end = 1;
						rotation = 0;
						bTeleport = false;
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) { System.out.println("Error"); };
	}
	
	public void blockToNext() {
		nBlock = nextBlocks[0].getBlockNum();
		color = nextBlocks[0].getBlockColor();
		for(int i=0;i<3;i++) {
			nextBlocks[i].setBlockNum(nextBlocks[i+1].getBlockNum());
			nextBlocks[i].setBlockColor(nextBlocks[i+1].getBlockColor());
		}
		nextBlocks[3].setBlockNum((int)(Math.random()*7));
		nextBlocks[3].setBlockColor(TetrisModel.COLOR[(int)(Math.random()*7)]);
	}
	
	public void rotationCheck() {
		int count2 = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int rotation2 = (rotation%4) + 1;
				if (rotation2 == 4)
					rotation2 = 0;
				if (TetrisModel.BLOCKS[nBlock][rotation2][i][j] == 1) {
					curX[count2] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					curY[count2] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					count2++;
				}
			}
		}
		
		int check = 0, blank = 0, error = 0;
		if ((TetrisModel.GAMEBOARD[curY[0]][curX[0]] == 1) || (nBlock == 6 && TetrisModel.GAMEBOARD[curY[2]][curX[2]] == 1) || (nBlock == 1 && TetrisModel.GAMEBOARD[curY[1]][curX[1]] == 1)) {
			check = 1;
			error++;
			System.out.println("check1");
			if (nBlock == 3) {
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
		if(gameOver == false) {
			weight = 340;
			this.repaint();
		}
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
			weight = 340;
			this.repaint();
		}
	}
	private void moveRight() {
		boolean collision = collisionRight();
		if (collision == false && gameOver == false) {
			width += TetrisModel.BLOCKSIZE;
			weight = 340;
			this.repaint();
		}
	}
	
	public void down() {
		if (!bTeleport)
			height += TetrisModel.BLOCKSIZE;
		else
			height += (weight-20);
		repaint();
	}
	
	public void start() {
		if ( TetrisThread == null)
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
	
	public static void sound(String file) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			if (keyCode == KeyEvent.VK_SPACE)
				bTeleport = true;
		}
		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}	
	}
	
	private class BtnListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			Object obj = e.getSource();
			
			gameOver = false;
			for (int y = 0; y < 20; y++)
				for (int x = 1; x < 11; x++)
					TetrisModel.GAMEBOARD[y][x] = 0;
			score = 0; width = 100; height = 0;
			
			
			if(obj == btnStart) {
				
				firstRun = false;
				lblFirst.setVisible(firstRun);
			}
		}
		
	}

	
}