// ** GUI 관련 라이브러리 **
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
// ** 사운드 관련 라이브러리 **
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/* 테트리스 블록, 게임보드, 스테이지, 점수, 다음 블록들을 표현하기 위한 클래스
 * JPanel을 상속받고 쓰레드를 적용하기 위해 Runnable을 구현
 */
public class GamePanel extends JPanel implements Runnable{

	// ** 인스턴스 데이터 **	
	// 1. 현재 블록 데이터
	private int nBlock; // 블록 모양
	private Color color; // 블록 색깔
	private int rotation; // 블록 회전
	private int width,height; // 게임보드 내 블록 좌우, 상하 위치
	private int end; // 블록 차례 종료
	private int weight; // 게임보드 내 실루엣 상하 위치
	private int curX[], curY[]; // # 블록의 한 칸마다 게임보드 내 좌표
	private int silhouetteX[], silhouetteY[]; // 블록의 한 칸마다 실루엣 좌표
	private int tempX[], tempY[];

	
	// 2. 게임 진행 데이터
	private int score;  
	private int stage;
	private boolean gameOver;
	private boolean firstRun; // 시작 페이지 실행 여부
	private boolean bTeleport; // 순간이동 적용 여부
	private boolean stageClear;
	
	// 3. 다음 블록 데이터
	private BlockModel[] nextBlocks; // 다음 블록 4개를 담기 위한 BlockModel 객체 배열
	
	// 4. GUI 관련 데이터
	private JButton btnStart; // 첫 시작 버튼
	private JButton btnRestart; // 스테이지 클리어 실패 시 재시작 버튼
	private JButton btnNext; // 다음 스테이지 진행 버튼
	private BtnListener btnL;
	private JLabel lblScoreNum, lblScore, lblStage, lblStageNum; // 게임 진행 관련 레이블
	private JLabel lblFirst, lblSecond, lblThird; // # 이미지 관련 레이블
	private ImageIcon backgroundImg, firstImg, secondImg, thirdImg, startImg, scoreImg, stageImg, restartImg, nextImg;
	
	// 5. 쓰레드 데이터
	private Thread TetrisThread;
	
	// ** 생성자 **
	public GamePanel() {
		
		setPreferredSize(new Dimension(430,520));
		setBounds(0,0,440,520);
		setBackground(new Color(111, 137, 168));
		setLayout(null);
				
		// 블록의 위치가 게임보드 중앙 상단에서 시작하기 위한 초기값
		width = 100;
		height = 0;
		weight = 340;
		
		// 첫 블록 데이터 설정
		nBlock = (int)(Math.random()*7);
		rotation = 0;
		color = TetrisModel.COLOR[(int)(Math.random()*7)];
		
		end = 0;
		score = 0;
		stage = 1;
		gameOver = false;
		stageClear = false;
		bTeleport = false;
		firstRun = true;
		
		curX = new int [4];
		curY = new int [4];
		silhouetteX = new int [4];
		silhouetteY = new int [4];
		tempX = new int[4];
		tempY = new int [4];
		
		btnL = new BtnListener();
		
		// JLabel
		// 시작 화면
		firstImg = new ImageIcon("./img/first.png");
	    lblFirst = new JLabel(firstImg);
	    lblFirst.setBounds(0,0,440,520);
	    lblFirst.setVisible(firstRun || gameOver || stageClear);
	    add(lblFirst);
	    
	    // 게임오버 화면
	    secondImg = new ImageIcon("./img/first.png");
	    lblSecond = new JLabel(secondImg);
	    lblSecond.setBounds(0,0,440,520);
	    lblSecond.setVisible(gameOver);
	    add(lblSecond);
	    
	    // 넥스트 스테이지 화면
	    thirdImg = new ImageIcon("./img/empty.png");
	    lblThird = new JLabel(thirdImg);
	    lblThird.setBounds(40,180,350,250);
	    lblThird.setVisible(stageClear);
	    add(lblThird);
	   
        // 1. 스테이지
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
		
		// 2. 점수
		scoreImg = new ImageIcon("./img/score.png");
		lblScore = new JLabel(scoreImg,SwingConstants.CENTER);
        lblScore.setFont(new Font("arial",Font.BOLD,15));
        lblScore.setForeground(new Color(68, 68, 173));
        lblScore.setBounds(270,95,130,20);
        add(lblScore);
        
        // 점수는 100 단위로 증가
		lblScoreNum = new JLabel(Integer.toString(score*100), SwingConstants.CENTER);
		lblScoreNum.setFont(new Font("arial",Font.BOLD,13));
		lblScoreNum.setForeground(Color.white);
		lblScoreNum.setBounds(270, 120, 130, 15);
		add(lblScoreNum);
		
		// 3. 다음 블록들		
		nextBlocks = new BlockModel[4];
		// 블록 모두 0~6의 랜덤 정수로 모양과 색깔 부여
		for(int i=0;i<4;i++) {
			nextBlocks[i] = new BlockModel();
			nextBlocks[i].setBlockNum((int)(Math.random()*7));
			nextBlocks[i].setBlockColor(TetrisModel.COLOR[(int)(Math.random()*7)]); //
		}
		
		//JButton
		// 시작 화면의 start 버튼
        startImg = new ImageIcon("./img/start.png");
        btnStart = new JButton("");
        btnStart.setIcon(startImg);
        btnStart.setBounds(140, 300, 160, 50);
        btnStart.setBackground(new Color(0,0,0,0));
        btnStart.setForeground(new Color(0,0,0,0));
        btnStart.setVisible(firstRun);
        btnStart.setBorderPainted(false);
        // 액션리스너 추가
        btnStart.addActionListener(btnL);
        lblFirst.add(btnStart);
		
		// restart 버튼
		restartImg = new ImageIcon("./img/restart.png");
		btnRestart = new JButton("");
		btnRestart.setIcon(restartImg);
		btnRestart.setBounds(140, 300, 160, 50);
		btnRestart.setBackground(new Color(0,0,0,0));
		btnRestart.setForeground(new Color(0,0,0,0));
		btnRestart.setVisible(gameOver);
		btnRestart.setBorderPainted(false);
        // 액션리스너 추가
		btnRestart.addActionListener(btnL);
		lblSecond.add(btnRestart);
		
		// next stage 버튼
		nextImg = new ImageIcon("./img/next.png");
		btnNext = new JButton("");
		btnNext.setIcon(nextImg);
		btnNext.setBounds(90, 135, 160, 50);
		btnNext.setBackground(Color.white);
		btnNext.setForeground(new Color(0,0,0,0));
		btnNext.setVisible(stageClear);
		btnNext.setBorderPainted(false);
        // 액션리스너 추가
		btnNext.addActionListener(btnL);
		lblThird.add(btnNext);

        // 게임 배경화면
        backgroundImg = new ImageIcon("./img/background.png");
        
        // 게임 배경음 실행 메소드
		sound("sound/bgm.wav");
		
		// GamePanel 내에서 키보드를 통한 블록이동을 위해 키보드 리스너 추가 
		addKeyListener(new KeyBoardListener());
		
		/* requestFocus() : 
		 * 키 이벤트는 포커스가 위치해 있어야 키 이벤트가 발생하므로 강제로 포커스를 설정해주어야 한다
		 * setFocusable() :  
		 * 포커스를 받을 수 있는 컴포넌트가 여러개 있을 경우에 포커스를 우선적으로 입력받기 위해 설정
		 */
		requestFocus(true);
		setFocusable(true);
		
		// 일정한 시간마다 자동으로 블록이 내려오기 위한 쓰레드 시작
		start();
	}
	
	// ** 액션 리스너 구현 클래스 **
	private class BtnListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			Object obj = e.getSource();
			
			// 게임 첫 시작을 위한 이벤트 핸들링
			if(obj == btnStart) {
                firstRun = false;
                lblFirst.setVisible(firstRun);
			}
			// 게임 재시작을 위한 이벤트 핸들링
			else if (obj == btnRestart) {
				gameOver = false;
				stageClear = false;
				for (int y = 0; y < TetrisModel.BOARDHEIGHT; y++)
					for (int x = 1; x < TetrisModel.BOARDWIDTH; x++)
						TetrisModel.GAMEBOARD[y][x] = 0;
				lblSecond.setVisible(gameOver);
				score = 0; width = 100; height = 0; stage = 1;
			}
			// 스테이지 클리어를 위한 이벤트 핸들링
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
	
	/* 키보드 이벤트 입력과 쓰레드 진행에 따라 repaint()를 통해
	 * 블록과 게임보드 내에 변화가 있는지 검사하고 현재 상태를 계속해서 반영 
	 */ 
	public void paintComponent(Graphics page) {
		super.paintComponent(page);
		setBackground(new Color(15,24,55));
		
		page.drawImage(backgroundImg.getImage(), 0, 0, null);
	
		page.setColor(new Color(236, 236, 237, 127));
		page.fillRoundRect(28, 95, 222, 405, 20,20); // 게임보드
		page.fillRoundRect(270,160,130,340, 20,20);  // 다음블록
		page.fillRoundRect(270,35,130,110, 20,20);	 // 스테이지, 점수
		page.setColor(new Color(236, 236, 237, 255));
		page.drawLine(28, 150, 250, 150);			 // 게임오버 라인
		
		lblScoreNum.setText(Integer.toString(score*100));
		lblStageNum.setText(Integer.toString(stage));
		
		// # 게임 진행 단계
		drawNextBlocks(page); 						 // 다음 블록 그림
		removeLine(); 								 // 라인 제거 검사
		drawWall(page);								 // 벽 그림
		gameOverCheck(); 	  						 // 게임 오버 검사
		stageClearCheck();	  						 // 스테이지 클리어 검사
		drawBlock(page);					 		 // 현재 블록 그림
		blockToWall();								 // 블록 착지 검사
		
		if(end == 1){
			blockToNext();							 // 블록이 착지했다면 다음 블록으로 진행							
			end = 0;
		}
	}
	
	// ** 게임 관리 메소드 **
	// nextBlocks의 원소 순서대로 다음 블록들을 그려주는 메소드 
	private void drawNextBlocks(Graphics g) {
		for(int i = 0;i < 4;i++) {
			for (int y = 0; y < 4; y++) {
				for (int x = 0; x < 4; x++) {
					if (TetrisModel.BLOCKS[nextBlocks[i].getBlockNum()][0][y][x] == 1) {
						g.setColor(nextBlocks[i].getBlockColor());
						g.fill3DRect(x * TetrisModel.BLOCKSIZE + 300,
								y * TetrisModel.BLOCKSIZE + 170 + i*80,
								TetrisModel.BLOCKSIZE,TetrisModel.BLOCKSIZE, true);
					}
				}
			}
		}
	}
	
	/* # 게임보드 내의 특정 라인이 블록으로 다 채워졌는지 검사하는 메소드
	 * 한 라인이 다 채워졌다면, 상위 라인들의 값을 밑으로 내려서 블록 벽이 내려오도록 표현
	 */
	private void removeLine() {
		int lineCount = 0;
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
			lineCount = 0;
		}
	}
	
	// 게임보드 내의 블록 벽을 그리는 메소드
	private void drawWall(Graphics g) {
		g.setColor(Color.gray);
		for (int y = 0; y < TetrisModel.BOARDHEIGHT; y++) {
			for (int x = 1; x < TetrisModel.BOARDWIDTH; x++) {
				if (TetrisModel.GAMEBOARD[y][x] == 1)
					g.fill3DRect(x*TetrisModel.BLOCKSIZE+20,
							y*TetrisModel.BLOCKSIZE+90,
							TetrisModel.BLOCKSIZE,
							TetrisModel.BLOCKSIZE, true);
			}
		}
	}
	
	/* # 게임보드의 블록 벽이 게임오버 라인에 도달했는지 검사하는 메소드
	 * 만약 도달했다면, 재시작 페이지로 설정
	 */
	private void gameOverCheck() {
		for (int x = 1; x < TetrisModel.BOARDWIDTH; x++) {
			if (TetrisModel.GAMEBOARD[3][x] == 1) {
				gameOver = true;
				lblSecond.setVisible(gameOver);
				btnRestart.setVisible(gameOver);
			}
		}
	}
	
	/* # 현재 점수가 일정 점수를 초과했는지 검사하는 메소드
	 * 초과했다면, 다음 스테이지 진행 페이지로 설정
	 * 만약 마지막 스테이지라면 재시작 페이지로 설정
	 */
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
	
	/* 블록을 현재 위치에 따라 게임보드 내에 그려주고
	 * 실루엣을 그려주는 메소드
	*/
	private void drawBlock(Graphics g) {
		int curCount = 0 ;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if(curCount == 4) break;
				if (TetrisModel.BLOCKS[nBlock][rotation][i][j] == 1) {
					curX[curCount] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					curY[curCount] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					
					g.setColor(color);
					g.fill3DRect(curX[curCount]*TetrisModel.BLOCKSIZE+20,
							curY[curCount]*TetrisModel.BLOCKSIZE+90,
							TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE, true);
					
					curCount++;
				}
			}
		}
		makeSilhouette(g);
	}
	
	/* 블록의 각 구성단위가 최대로 내려갈 수 있는 거리를 temp로 계산하여 tempY에 저장
	 * tempY의 최소값을 weight로 선택하여 블록의 현재 위치에서 weight만큼 내려간 위치에 실루엣을 그림
	*/
	private void makeSilhouette(Graphics g) {
		int silhouetteCount = 0;
		int temp = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if(silhouetteCount == 4) break;
				if (TetrisModel.BLOCKS[nBlock][rotation][i][j] == 1) {
					silhouetteX[silhouetteCount] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					silhouetteY[silhouetteCount] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE; //블록의 현재 위치
					
					tempY[silhouetteCount] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE; //tempY 초기화
					temp = 0; //temp 초기화
					
					while (true) {
						if (TetrisModel.GAMEBOARD[tempY[silhouetteCount]][silhouetteX[silhouetteCount]] == 0) { //gameboard가 빈 칸이면
							temp += 20; //temp를 증가시키고
							tempY[silhouetteCount] = (i*TetrisModel.BLOCKSIZE+height+temp)/TetrisModel.BLOCKSIZE; //tempY 업데이트해서 아래 칸으로 이동
						}
						else //gameboard가 빈칸이 아니면 무한루프 탈출
							break;
					}
					if (weight > temp) //while문에서 계산된 temp가 weight보다 작으면
						weight = temp; //weight를 temp로 업데이트 -> temp 중에 최소값이 weight로 결정됨
					silhouetteCount++;
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			g.drawRect(silhouetteX[i]*TetrisModel.BLOCKSIZE+20,
					(silhouetteY[i]+weight/20)*TetrisModel.BLOCKSIZE+70, //블록의 현재 위치에서 weight만큼 내려가서 실루엣을 그림
					TetrisModel.BLOCKSIZE, TetrisModel.BLOCKSIZE);
		}
	}
	
	/* # 블록이 벽에 착지했는지 검사하는 메소드
	 * 착지했다면, 게임보드에 반영
	 * 다음 블록 진행을 위한 현재 블록 데이터 초기화
	 */
	private void blockToWall() {
		try {
			for (int i = 0; i < 4; i++) {
				if (TetrisModel.GAMEBOARD[curY[i]+1][curX[i]] == 1) {
					for (int j = 0; j < 4; j++) 
						TetrisModel.GAMEBOARD[curY[j]][curX[j]] = 1;
					width = 100;
					height = 0;
					weight = 340;
					end = 1;
					rotation = 0;
					bTeleport = false;
					break;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) { System.out.println("Block to Wall Error"); };
	}
	
	/* 현재 블록을 바로 다음 차례 블록으로 바꾸어주는 메소드
	 * 선입선출로 queue의 역할을 하게 함
	 */
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
	
	// ** 키 리스너 구현 클래스 **
	private class KeyBoardListener implements KeyListener {

		// 키를 계속 누르고 있는 경우 허용
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
		// 키를 계속 누르고 있는 경우 배제
		public void keyReleased(KeyEvent e) {
			int keyCode = e.getKeyCode();
			if (keyCode == KeyEvent.VK_SPACE)
				bTeleport = true;
		}
		public void keyTyped(KeyEvent e) {}
	}
	
	// 블록 이동 관련 메소드
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
	private boolean collisionLeft() {
		for (int i = 0; i < 4; i++)
			if (TetrisModel.GAMEBOARD[curY[i]][curX[i]-1] == 1)
				return true;
		return false;
	}
	private void moveRight() {
		boolean collision = collisionRight();
		 if (collision == false && gameOver == false && bTeleport == false && stageClear == false){
			width += TetrisModel.BLOCKSIZE;
			weight = 340;
			repaint();
		}
	}
	private boolean collisionRight() {
		for (int i = 0; i < 4; i++)
			if (TetrisModel.GAMEBOARD[curY[i]][curX[i]+1] == 1)
				return true;
		return false;
	}
	private void down() {
		if (!bTeleport)
			height += TetrisModel.BLOCKSIZE;
		else
			height += (weight-20);
		repaint();
	}

	// 블록 회전 관련 메소드
	private void rotateBlock() {
		rotationCheck();
		if(gameOver == false && bTeleport == false && stageClear == false) {
			weight = 340;
			repaint();
		}
	}
	
	/* # 블록 회전시 벽과 겹치거나 회전 여유공간이 없는 경우를 검사하는 메소드
	 * 검사의 기준은 항상 게임보드 내에서 상대적으로 위치하는 블록의 4 X 4 행렬 내의 값
	 * 예외를 4개의 케이스로 나누어 벽과 겹치는 경우는 벽에서 필요공간만큼 멀어지게 처리
	 * 회전 여유공간이 없는 경우는 회전 자체를 막고 콘솔 출력을 통해 알려줌
	 */
	private void rotationCheck() {
		int count = 0;
		int rotation2 = (rotation+1) % 4;
		
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (TetrisModel.BLOCKS[nBlock][rotation2][i][j] == 1) {
					tempX[count] = (j*TetrisModel.BLOCKSIZE+width)/TetrisModel.BLOCKSIZE;
					tempY[count] = (i*TetrisModel.BLOCKSIZE+height)/TetrisModel.BLOCKSIZE;
					count++;
				}
			}
		}
		
		int error = 0; // 예외의 종류
		int space = 0; // 회전 여유 공간
		try {//				    ■
             // 	■■■■ 	   ■■■
			if(nBlock == 3 || nBlock == 4){ // 왼쪽, 오르쪽 벽과 모두 겹치는 경우가 발생하는 모양
				if(TetrisModel.GAMEBOARD[tempY[0]][tempX[0]] == 1){ // 왼쪽 벽과 한 칸이 겹치게 됨
					error = 1;
					for (int i = 1; i < 5; i++)
						if (TetrisModel.GAMEBOARD[tempY[0]][tempX[0]+i] == 0) 
							space++;
					if (space < 4) 
						error = 4;
				}
				else if(TetrisModel.GAMEBOARD[tempY[2]][tempX[2]] == 1){ // 오른쪽 벽과 두 칸이 겹치게 됨
					error = 2;
					for (int i = 1; i < 5; i++)
						if (TetrisModel.GAMEBOARD[tempY[2]][tempX[2]-i] == 0) 
							space++;
					if (space < 4)
						error = 4;
				}
				else if(TetrisModel.GAMEBOARD[tempY[3]][tempX[3]] == 1){ // 오른쪽 벽과 한 칸이 겹치게 됨
					error = 3;
					for (int i = 1; i < 5; i++)
						if (TetrisModel.GAMEBOARD[tempY[3]][tempX[3]-i] == 0)
							space++;
					if (space < 4)
						error = 4;
				}
			}
			else{ // 왼쪽 벽만 겹치는 경우가 발생하는 모양
				if((TetrisModel.GAMEBOARD[tempY[0]][tempX[0]] == 1) ||
						(nBlock == 6 && TetrisModel.GAMEBOARD[tempY[2]][tempX[2]] == 1) ||//  ■■ 
			            																	// ■■
						(nBlock == 1 && TetrisModel.GAMEBOARD[tempY[1]][tempX[1]] == 1)) //     ■
				{ 																	       // ■■■
					error  = 1;
					for (int i = 1; i < 4; i++)
						if (TetrisModel.GAMEBOARD[tempY[0]][tempX[0]+i] == 0)
							space++;
					if (space < 3)
						error = 4;
				}
			}
		}catch(ArrayIndexOutOfBoundsException e) {error = 4;}
		
		switch(error) {
		case 1: // 왼쪽 벽과 한 칸 겹치는 경우
			width += TetrisModel.BLOCKSIZE;
			rotation = ++rotation % 4;
			break;
		case 2: // 오른쪽 벽과 두 칸 겹치는 경우
			width -= TetrisModel.BLOCKSIZE * 2;
			rotation = ++rotation % 4;
			break;
		case 3: // 오른쪽 벽과 한 칸 겹치는 경우
			width -= TetrisModel.BLOCKSIZE;
			rotation = ++rotation % 4;
			break;
		case 4: // 회전이 불가능한 경우
			System.out.println("Invalid Rotate!\n");
			break;
		default: // 정상 회전 가능한 경우
			rotation = ++rotation % 4;			
		}
	}

	// ** 쓰레드 구현 메소드 **
	public void start() {
		if ( TetrisThread == null)
			TetrisThread = new Thread(this);
		TetrisThread.start();
	}
	/* 일정 시간 간격마다 블록이 밑으로 이동하도록 함
	 * 스테이지가 높아질수록 시간 간격은 줄어듦
	 */
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
	
	// ** 사운드 재생 메소드 **
	private static void sound(String file) {
		try {
			//입력 받은 파일을 열어주고 버퍼를 이용해서 빠른 속도로 읽음
			AudioInputStream bgm = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			Clip clip = AudioSystem.getClip(); //사운드 재생하는 데 사용하는 클립 가져옴
			clip.open(bgm); 
			clip.start(); // 사운드 재생
			clip.loop(Clip.LOOP_CONTINUOUSLY); // 사운드 반복

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}