import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;;

public class SnakeGameBoard implements ActionListener {
//ADD VALIDATION TO USERNAME AND CREATE A RESET FOR DYING
	//TODO Reconfigure file structure. doesnt see highscore files
	public JFrame frame ;
	public JLabel lblScore ;
	public JLabel lblLives ;
	public static JPanel infoPanel ; 
	public SnakeGameModel snakeGameModel ;
	public SnakeTail snakeTail ;
	public SnakeTail playerTwo = null; 
	public GameBoardPanel gameBoard ; 
	public volatile boolean running = false;
	public volatile int speed = 200 ; 
	Timer timer=new Timer(speed, this);
	public int players = 0 ;

	// ADD EVENT LISTENER THAT CHECKS IF FRUIT IS EATEN
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SnakeGameBoard window = new SnakeGameBoard();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	/**
	 * Create the application.
	 */
	public SnakeGameBoard() {
		this.snakeTail = new SnakeTail(this) ;
		this.snakeGameModel = new SnakeGameModel(this) ; 
		initialize(); 
		frame.repaint();
		timer.start();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 500, 525);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar() ; 
		JMenu file = new JMenu("File") ; 
		JMenuItem highscores = new JMenuItem("Highscores") ;
		JMenuItem addSnake = new JMenuItem("Add Snake") ; 

		highscores.addActionListener((ActionEvent event) -> {
			ArrayList<String> scores = snakeGameModel.getHighscores() ; 
			String displayScores = "" ; 
			for(String record : scores) { 
				displayScores += record + "\n" ; 
			}

			JOptionPane.showMessageDialog(frame,
					displayScores, "HIGHSCORES",
					JOptionPane.PLAIN_MESSAGE);
		});

		addSnake.addActionListener((ActionEvent event) -> {
			this.playerTwo = new SnakeTail(this) ; 
			gameBoard.revalidate();
			gameBoard.repaint();
		});

		JMenuItem exit = new JMenuItem("Exit") ;
		exit.addActionListener((ActionEvent event) -> {
			System.exit(0);
		});
		menuBar.add(file);
		file.add(highscores);
		file.add(addSnake) ; 
		file.add(exit) ; 
		frame.add(menuBar);
		gameBoard = new GameBoardPanel(this);
		frame.getContentPane().add(gameBoard, BorderLayout.CENTER);
		infoPanel = new JPanel() ; 
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(infoPanel, BorderLayout.NORTH) ;
		lblScore = new JLabel("SCORE = 0");
		infoPanel.add(lblScore, BorderLayout.EAST);
		JLabel lblSpace = new JLabel("       ");
		infoPanel.add(lblSpace, BorderLayout.WEST);	
		lblLives = new JLabel("LIVES = " + snakeGameModel.lives);
		infoPanel.add(lblLives, BorderLayout.WEST);


	}
	// ***** HELPER FUNCTIONS *******

	@Override
	public void actionPerformed(ActionEvent ev) {
		if(ev.getSource()==timer){
			if(running == true) { 
				snakeTail.move();
				if(playerTwo != null) { 
					playerTwo.move();
				}
				gameBoard.repaint() ;// this will call at every 1 second
			}
		}
	}

	public void updateInfo() { 
		lblLives.setText("Lives = "  +  snakeGameModel.lives) ;
		lblScore.setText("Score = " + (snakeTail.printableList().size() - 1));	
	}

	public void setSpeed() {
		double score = snakeTail.printableList().size() - 1 ; 
		double temp = 200 / (1+(score / 10)+(score / 100)); 
		this.speed = (int) temp ; 
		timer.setDelay(speed);
	}

	// ***** Game Board Class Only Below here!!!!

	class GameBoardPanel extends JPanel implements KeyListener{ 

		public SnakeGameBoard snakeGameBoard ;
		public JLabel lblPause ;


		public GameBoardPanel(SnakeGameBoard board) {
			this.setBackground(Color.BLACK);
			this.snakeGameBoard = board ; 
			this.setFocusable(true);
			this.addKeyListener(this) ;
			Dimension prefsize = new Dimension(500,500);
			setPreferredSize(prefsize);

			lblPause = new JLabel("PRESS SPACE TO CONTINUE", JLabel.CENTER) ; 
			lblPause.setOpaque(true);
			lblPause.setForeground(Color.BLUE); 
			lblPause.setPreferredSize(new Dimension(200, 20));
			add(lblPause, BorderLayout.CENTER);

		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);       
			g.setColor(Color.RED);
			g.fillRect(snakeGameModel.snakeFruit.x, snakeGameModel.snakeFruit.y, 10, 10);
			g.setColor(Color.WHITE);
			for(Point cell : snakeGameModel.snakeLocation) { 
				g.fillRect(cell.x, cell.y, 10, 10);
			}
			if(snakeGameModel.playerTWoLocation != null) {
				g.setColor(Color.BLUE);
				for(Point cell : snakeGameModel.playerTWoLocation) { 
					g.fillRect(cell.x, cell.y, 10, 10);
				}
			}
			Toolkit.getDefaultToolkit().sync();
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();

			if (key == KeyEvent.VK_SPACE)
			{
				snakeGameBoard.running = !snakeGameBoard.running ; 
				displayPause() ;  
			}
			else if (running == true) { 
				if (key == KeyEvent.VK_KP_LEFT || key == KeyEvent.VK_LEFT)
				{
					if (snakeTail.head.direction == Direction.EAST) { 
						return ;
					}
					snakeTail.head.setDirection(Direction.WEST);
					System.out.println("west") ;
				}
				else if (key == KeyEvent.VK_KP_RIGHT || key == KeyEvent.VK_RIGHT)
				{
					if (snakeTail.head.direction == Direction.WEST) { 
						return ;
					}
					snakeTail.head.setDirection(Direction.EAST);
					System.out.println("east") ;
				}
				else if (key == KeyEvent.VK_KP_UP || key == KeyEvent.VK_UP)
				{
					if (snakeTail.head.direction == Direction.SOUTH) { 
						return ;
					}
					snakeTail.head.setDirection(Direction.NORTH);
					System.out.println("north") ;
				}
				else if (key == KeyEvent.VK_KP_DOWN || key == KeyEvent.VK_DOWN)
				{
					if (snakeTail.head.direction == Direction.NORTH) { 
						return ;
					}
					snakeTail.head.setDirection(Direction.SOUTH);
					System.out.println("down");
				}	
				if(playerTwo != null) {
					if (key == KeyEvent.VK_A)
					{
						if (playerTwo.head.direction == Direction.EAST) { 
							return ;
						}
						playerTwo.head.setDirection(Direction.WEST);
						System.out.println("west") ;
					}
					else if (key == KeyEvent.VK_D)
					{
						if (playerTwo.head.direction == Direction.WEST) { 
							return ;
						}
						playerTwo.head.setDirection(Direction.EAST);
						System.out.println("east") ;
					}
					else if (key == KeyEvent.VK_W)
					{
						if (playerTwo.head.direction == Direction.SOUTH) { 
							return ;
						}
						playerTwo.head.setDirection(Direction.NORTH);
						System.out.println("north") ;
					}
					else if (key == KeyEvent.VK_S)
					{
						if (playerTwo.head.direction == Direction.NORTH) { 
							return ;
						}
						playerTwo.head.setDirection(Direction.SOUTH);
						System.out.println("down");
					}
				}
			}
		}

		public void displayPause() { 
			if(snakeGameBoard.running == false) { 
				add(lblPause) ; 
			}else { 
				remove(lblPause);
				revalidate();
			} 
			repaint() ;
		}

		public void displayDeath() {
			ArrayList<String> scores = snakeGameModel.getHighscores() ; 
			int playerScore = snakeTail.printableList().size() - 1 ;
			if( playerScore < Integer.valueOf(scores.get(9).split(" ")[2]) ) {
				JOptionPane.showMessageDialog(lblPause, "You have died. \n You Suck.");
			}else {				
				String userName = JOptionPane.showInputDialog(frame,
						"You have died, enter a name for the highscore list.", "HIGHSCORES");
				int index = 100 ; 
				for(String record : scores) {
					if (playerScore > Integer.valueOf(record.split(" ")[2])) {
						index = scores.indexOf(record) ;
						break ;
					}
				}
				System.out.println(index) ;
				String temp = (index + 1) + ". " + userName + " " + playerScore ;
				System.out.println(temp);
				scores.remove(index) ;
				scores.add(index, temp) ;
				for(String rec : scores) { 
					System.out.println(rec) ; 
				}
				BufferedWriter bw = null ;
				FileWriter highScores = null;
				try {
					highScores = new FileWriter("/home/shanerng/workspace/SnakeGame/highscores.txt");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				try {
					bw = new BufferedWriter(highScores) ; 
				}catch(Exception e) {;};
				String displayScores = "" ;
				for(String record : scores) {
					displayScores += record + "\n" ; 
					try {
						bw.write(record + "\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				try {
					bw.write("ENDFILE");
					bw.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				JOptionPane.showMessageDialog(frame,
						displayScores, "HIGHSCORES",
						JOptionPane.PLAIN_MESSAGE);				
			}

		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}



	}


}
