import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class GamePanel extends JPanel implements Runnable{

	private int nBlock;
	private Color color;
	private int rotation;
	private int width, height, weight, temp;
	private int curX[], curY[], silhouetteX[], silhouetteY[], tempY[];
	
	private int end;
	private int score;
	private int stage;
	private boolean gameOver, firstRun, bTeleport, stageClear;
	
	private int curCount, lineCount, silhouetteCount;
	
	private JPanel nextPanel;
	private BlockModel[] nextBlocks;
	
	private Thread TetrisThread;
	private JButton btnStart, btnRestart, btnNext;
	private BtnListener btnL;
	private JLabel lblScoreNum, lblScore, lblStage, lblStageNum, lblFirst, lblSecond, lblThird;
	private ImageIcon backgroundImg, firstImg, secondImg, thirdImg, startImg, scoreImg, stageImg, restartImg, nextImg;
	
	public GamePanel() {
		
		setLayout(null);
		setPreferredSize(new Dimension(680,600));
		
		nBlock = 0;
		width = 100;
		height = 0;
		weight = 340;
		temp = 0;
		rotation = 0;
		
		end = 0;
		score = 0;
		stage = 1;
		gameOver = false;
		stageClear = false;
		bTeleport = false;
		firstRun = true;
		
		curCount = lineCount = silhouetteCount = 0;
		
		curX = new int [4];
		curY = new int [4];
		silhouetteX = new int [4];
		silhouetteY = new int [4];
		tempY = new int [4];
		
		btnL = new BtnListener();

		// Start Page
		firstImg = new ImageIcon("./img/first.png");
	    lblFirst = new JLabel(firstImg);
	    lblFirst.setBounds(0,0,440,520);
	    lblFirst.setVisible(firstRun || gameOver || stageClear);
	    add(lblFirst);
	    
	    secondImg = new ImageIcon("./img/first.png");
	    lblSecond = new JLabel(secondImg);
	    lblSecond.setBounds(0,0,440,520);
	    lblSecond.setVisible(gameOver);
	    add(lblSecond);
	    
	    thirdImg = new ImageIcon("./img/empty.png");
	    lblThird = new JLabel(thirdImg);
	    lblThird.setBounds(50,180,350,250);
	    lblThird.setVisible(stageClear);
	    add(lblThird);
	    
        startImg = new ImageIcon("./img/start.png");
        btnStart = new JButton("");
        btnStart.setIcon(startImg);
        btnStart.setBounds(140, 300, 160, 50);
        btnStart.setBackground(new Color(0,0,0,0));
        btnStart.setForeground(new Color(0,0,0,0));
        btnStart.setVisible(firstRun);
        btnStart.setBorderPainted(false);
        btnStart.addActionListener(btnL);
        lblFirst.add(btnStart);
       
        // Game Page
        backgroundImg = new ImageIcon("./img/background.png");
		sound("sound/bgm.wav");
		
        // stage
		stageImg = new ImageIcon("./img/stage.png");
		lblStage = new JLabel(stageImg, SwingConstants.CENTER);
        lblStage.setFont(new Font("arial",Font.BOLD,15));
        lblStage.setForeground(new Color(68, 68, 173));
		lblStage.setBounds(270,50,130,20);
		add(lblStage);
		
		lblStageNum = new JLabel(Integer.toString(stage), SwingConstants.CENTER);
        lblStageNum.setFont(new Font("arial",Font.BOLD,13));
        lblStageNum.setForeground(Color.white);
        lblStageNum.setBounds(270,75,130,15);
		add(lblStageNum);
		
		// score
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
		
		// next block
		nextPanel = new JPanel();
		nextPanel.setBounds(270,160,140,340);
		nextPanel.setBackground(new Color(236, 236, 237, 0));
		add(nextPanel);
		
		nextBlocks = new BlockModel[4];
		for(int i=0;i<4;i++) {
			nextBlocks[i] = new BlockModel();
			nextBlocks[i].setBlockNum((int)(Math.random()*7));
			nextBlocks[i].setBlockColor(TetrisModel.COLOR[(int)(Math.random()*7)]); //
			color = nextBlocks[i].getBlockColor();
		}
		
		// re-start 
		restartImg = new ImageIcon("./img/restart.png");
		btnRestart = new JButton("");
		btnRestart.setIcon(restartImg);
		btnRestart.setBounds(140, 300, 160, 50);
		btnRestart.setBackground(new Color(0,0,0,0));
		btnRestart.setForeground(new Color(0,0,0,0));
		btnRestart.setVisible(gameOver);
		btnRestart.setBorderPainted(false);
		btnRestart.addActionListener(new BtnListener());
		lblSecond.add(btnRestart);
		
		nextImg = new ImageIcon("./img/next.png");
		btnNext = new JButton("");
		btnNext.setIcon(nextImg);
		btnNext.setBounds(95, 135, 160, 50);
		btnNext.setBackground(Color.white);
		btnNext.setForeground(new Color(0,0,0,0));
		btnNext.setVisible(stageClear);
		btnNext.setBorderPainted(false);
		btnNext.addActionListener(new BtnListener());
		lblThird.add(btnNext);
		
		addKeyListener(new KeyBoardListener());
		setFocusable(true);
		requestFocus(true);
		start();
	}
	
	public void paintComponent(Graphics page) {
		super.paintComponent(page);
		requestFocus(true);
		setBackground(new Color(15,24,55));
		
		page.drawImage(backgroundImg.getImage(), 0, 0, null);
		setOpaque(false);
	
		page.setColor(new Color(236, 236, 237, 127));
		page.fillRoundRect(28, 95, 222, 405, 20,20); 
		page.fillRoundRect(270,160,130,340, 20,20); 
		page.fillRoundRect(270,35,130,110, 20,20);
		page.setColor(new Color(236, 236, 237, 255));
		page.drawLine(28, 150, 250, 150);
		
		lblScoreNum.setText(Integer.toString(score*100));
		lblStageNum.setText(Integer.toString(stage));
		
		drawNextBlocks(page);
		gameOverCheck();
		stageClearCheck();
		removeLine(curCount, lineCount, silhouetteCount, page);
		blockToWall();
		makeWall(page);
		
		if(end == 1){
			blockToNext();
			end = 0;
		}
	}
	
	private void drawNextBlocks(Graphics g) {
		for(int i = 0;i < 4;i++) {
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					if (TetrisModel.BLOCKS[nextBlocks[i].getBlockNum()][0][y][x] == 1) {
						g.setColor(nextBlocks[i].getBlockColor());
						g.fill3DRect(x * TetrisModel.BLOCKSIZE + 300, y * TetrisModel.BLOCKSIZE + 170 + i*80, TetrisModel.BLOCKSIZE,TetrisModel.BLOCKSIZE, true);
					}
				}
			}
		}
	}
		
	private void gameOverCheck() {
		for (int x = 1; x < TetrisModel.BOARDWIDTH; x++) {
			if (TetrisModel.GAMEBOARD[3][x] == 1) {
				gameOver = true;
				lblSecond.setVisible(gameOver);
				btnRestart.setVisible(gameOver);
			}
		}
	}
	
	private void stageClearCheck() {
		if (score*100 >= 100) {
			stageClear = true;
			if (stage <= 4) {
				lblThird.setVisible(stageClear);
				btnNext.setVisible(stageClear);
				lblStageNum.setText(Integer.toString(stage));
			}
			else if (stage >= 5) {
				lblSecond.setVisible(stageClear);
				btnRestart.setVisible(stageClear);
			}
		}
	}
	
	private void removeLine(int curCount, int lineCount, int silhouetteCount, Graphics g) {
		for (int y = 0; y < TetrisModel.BOARDHEIGHT; y++) {
			for (int x = 1; x < TetrisModel.BOARDWIDTH ; x++)
				if (TetrisModel.GAMEBOARD[y][x] == 1)
					lineCount++;
			if (lineCount == TetrisModel.BOARDWIDTH-1) {
				for (int i = y; i > 1; i--) {
					for (int j = 1; j < TetrisModel.BOARDWIDTH; j++) {
						TetrisModel.GAMEBOARD[i][j] = 0;
						TetrisModel.GAMEBOARD[i][j] = TetrisModel.GAMEBOARD[i-1][j];
					}
				}
				score++;
			}
			else {
				blockDown(curCount, g);
				makeSilhouette(silhouetteCount, g);
			}
			lineCount = 0;
		}
	}
	
	private void blockDown(int curCount, Graphics g) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (TetrisModel.BLOCKS[nBlock][rotation][i][j] == 1) {
					curX[curCount] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					curY[curCount] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					
					g.setColor(color);
					g.fill3DRect(curX[curCount]*TetrisModel.BLOCKSIZE+20, curY[curCount]*TetrisModel.BLOCKSIZE+90, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
					
					curCount++;
				}
			}
		}
	}
	
	private void makeSilhouette(int silhouetteCount, Graphics g) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (TetrisModel.BLOCKS[nBlock][rotation][i][j] == 1) {
					silhouetteX[silhouetteCount] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					silhouetteY[silhouetteCount] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					
					tempY[silhouetteCount] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					temp = 0;
					
					while (true) {
						if (TetrisModel.GAMEBOARD[tempY[silhouetteCount]][silhouetteX[silhouetteCount]] == 0) {
							temp += 20;
							tempY[silhouetteCount] = (i*TetrisModel.BLOCKSIZE+height+temp)/TetrisModel.BLOCKSIZE;
						}
						else
							break;
					}
					if (weight > temp)
						weight = temp;
					silhouetteCount++;
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			g.drawRect(silhouetteX[i]*TetrisModel.BLOCKSIZE+20, (silhouetteY[i]+weight/20)*TetrisModel.BLOCKSIZE+70, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE);
		}
	}
	
	private void blockToWall() {
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
					break;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) { System.out.println("Block to Wall Error"); };
	}
	
	private void makeWall(Graphics g) {
		g.setColor(Color.gray);
		for (int y = 0; y < TetrisModel.BOARDHEIGHT; y++) {
			for (int x = 1; x < TetrisModel.BOARDWIDTH; x++) {
				if (TetrisModel.GAMEBOARD[y][x] == 1)
					g.fill3DRect(x*TetrisModel.BLOCKSIZE+20, y*TetrisModel.BLOCKSIZE+90, TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
			}
		}
	}
	
	private void blockToNext() {
		nBlock = nextBlocks[0].getBlockNum();
		color = nextBlocks[0].getBlockColor();
		for(int i = 0;i < 3;i++) {
			nextBlocks[i].setBlockNum(nextBlocks[i+1].getBlockNum());
			nextBlocks[i].setBlockColor(nextBlocks[i+1].getBlockColor());
		}
		nextBlocks[3].setBlockNum((int)(Math.random()*7));
		nextBlocks[3].setBlockColor(TetrisModel.COLOR[(int)(Math.random()*7)]);
	}
			
	private void moveDown() {
		if (gameOver == false && bTeleport == false && stageClear == false) {
			height += TetrisModel.BLOCKSIZE;
			repaint();
		}
	}
	
	private void moveLeft() {
		boolean collision = collisionLeft();
		if (collision == false && gameOver == false && bTeleport == false && stageClear == false) {
			width -= TetrisModel.BLOCKSIZE;
			weight = 340;
			repaint();
		}
	}
	
	private void moveRight() {
		boolean collision = collisionRight();
		 if (collision == false && gameOver == false && bTeleport == false && stageClear == false){
			width += TetrisModel.BLOCKSIZE;
			weight = 340;
			repaint();
		}
	}
	
	private void down() {
		if (!bTeleport)
			height += TetrisModel.BLOCKSIZE;
		else
			height += (weight-20);
		repaint();
	}
	
	private void rotateBlock() {
		rotationCheck();
		if(gameOver == false && bTeleport == false && stageClear == false) {
			weight = 340;
			repaint();
		}
	}
	
	private boolean collisionLeft() {
		for (int i = 0; i < 4; i++)
			if (TetrisModel.GAMEBOARD[curY[i]][curX[i]-1] == 1)
				return true;
		return false;
	}
	private boolean collisionRight() {
		for (int i = 0; i < 4; i++)
			if (TetrisModel.GAMEBOARD[curY[i]][curX[i]+1] == 1)
				return true;
		return false;
	}
	
	private void rotationCheck() {
		int count = 0;
		int rotation2 = (rotation+1) % 4;
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (TetrisModel.BLOCKS[nBlock][rotation2][i][j] == 1) {
					curX[count] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					curY[count] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					count++;
				}
			}
		}
		
		int error = 0;
		int space = 0;
		try {
			if(nBlock == 3 || nBlock == 4){
				if(TetrisModel.GAMEBOARD[curY[0]][curX[0]] == 1){
					error = 1;
					for (int i = 1; i < 5; i++)
						if (TetrisModel.GAMEBOARD[curY[0]][curX[0]+i] == 0) // Move right 20
							space++;
					if (space < 4) // No space for 20
						error = 4;
				}
				else if(TetrisModel.GAMEBOARD[curY[2]][curX[2]] == 1){
					// ■■■■ to Right Wall
					error = 2;
					for (int i = 1; i < 5; i++)
						if (TetrisModel.GAMEBOARD[curY[2]][curX[2]-i] == 0) // Move left 40
							space++;
					if (space < 4)
						error = 4;
				}
				else if(TetrisModel.GAMEBOARD[curY[3]][curX[3]] == 1){
					// ■■■■ to Right Wall
					error = 3;
					for (int i = 1; i < 5; i++)
						if (TetrisModel.GAMEBOARD[curY[3]][curX[3]-i] == 0) // Move left 20
							space++;
					if (space < 4)
						error = 4;
				}
			}
			else{
				if((TetrisModel.GAMEBOARD[curY[0]][curX[0]] == 1) ||
						(nBlock == 6 && TetrisModel.GAMEBOARD[curY[2]][curX[2]] == 1) ||
						(nBlock == 1 && TetrisModel.GAMEBOARD[curY[1]][curX[1]] == 1)) {
					error  = 1;
					for (int i = 1; i < 4; i++)
						if (TetrisModel.GAMEBOARD[curY[0]][curX[0]+i] == 0)
							space++;
					if (space < 3)
						error = 4;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) {error = 4;}
		
		switch(error) {
		case 1:
			width += TetrisModel.BLOCKSIZE;
			rotation = ++rotation % 4;
			break;
		case 2:
			width -= TetrisModel.BLOCKSIZE * 2;
			rotation = ++rotation % 4;
			break;
		case 3:
			width -= TetrisModel.BLOCKSIZE;
			rotation = ++rotation % 4;
			break;
		case 4:
			System.out.println("Invalid Rotate!\n");
			break;
		default:
			rotation = ++rotation % 4;			
		}
	}

	public void start() {
		if ( TetrisThread == null)
			TetrisThread = new Thread(this);
		TetrisThread.start();
	}
	
	public void run() {
		while (true) {
			try {
				TetrisThread.sleep(600-stage*100);
				if (!firstRun && !gameOver && !stageClear)
					down();
			} catch(InterruptedException e) {
				return;
			}
		}
	}
	
	private static void sound(String file) {
		try {
			AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);

		} catch (Exception e) {
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
			
			if(obj == btnStart) {
                firstRun = false;
                lblFirst.setVisible(firstRun);
			}
			else if (obj == btnRestart) {
				gameOver = false;
				for (int y = 0; y < TetrisModel.BOARDHEIGHT; y++)
					for (int x = 1; x < TetrisModel.BOARDWIDTH; x++)
						TetrisModel.GAMEBOARD[y][x] = 0;
				lblSecond.setVisible(gameOver);
				score = 0; width = 100; height = 0; stage = 1;
			}
			else if (obj == btnNext) {
				stageClear = false;
				for (int y = 0; y < TetrisModel.BOARDHEIGHT; y++)
					for (int x = 1; x < TetrisModel.BOARDWIDTH; x++)
						TetrisModel.GAMEBOARD[y][x] = 0;
				for (int i = 0; i < 4; i++)
					blockToNext();
				lblThird.setVisible(stageClear);
				score = 0; width = 100; height = 0; stage++;
			}
		}
	}
}