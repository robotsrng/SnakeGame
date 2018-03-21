import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SnakeGameModel {

	public SnakeTail snakeTail ;
	public volatile Point snakeFruit ;
	public SnakeGameBoard snakeGameBoard ;
	public int lives = 3 ; // ADD SCORE AND LIVES TO GAMEMODEL
	public volatile ArrayList<Point> snakeLocation = new ArrayList<Point>() ;  
	public volatile ArrayList<Point> playerTWoLocation = null ; 

	public SnakeGameModel(SnakeGameBoard snakeGameBoard) { 
		this.snakeGameBoard = snakeGameBoard ;
		this.snakeTail = snakeGameBoard.snakeTail ;
		snakeLocation.add(new Point(250, 250)) ;
		addFruit() ;
	}

	// ****** HELPER FUNCTIONS ***********


	public ArrayList<String> getHighscores(){
		ArrayList<String> scoreList = new ArrayList<String>() ; 
		String highscoreFile = "/home/shanerng/workspace/SnakeGame/highscores.txt";
		BufferedReader br = null;
		String line = "";
		try {

			br = new BufferedReader(new FileReader(highscoreFile));
			while (!(line = br.readLine()).equals("ENDFILE")) {

				// use comma as separator
				String record = line ; 
				scoreList.add(record) ; 
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return scoreList ;
	}

	public Point addFruit() { 
		Random random = new Random() ; 
		while (true) {
			Point fruit = new Point((random.nextInt(49) * 10), (random.nextInt(49) * 10));
			if (!snakeTail.checkCollision(fruit)) { 
				continue ; 
			}else {
				snakeFruit = fruit ;
				return fruit ; 
			}

		}
	}

	public void setSnakeLocation(ArrayList<Point> snake, int player) { 
		if(player == 1) {
			this.snakeLocation = snake ;
		}else{this.playerTWoLocation = snake;}
	}

	public void loseLife() { 
		lives = lives - 1 ; 
		snakeGameBoard.updateInfo() ; 
	}

}
