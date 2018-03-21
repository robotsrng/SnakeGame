import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class SnakeTail implements Runnable{
	// This will need listeners to listen for arrow keys. it will be a list of coordinates. Everytime the game redraws, it moves the last item to the front and shifts everyother one.
	//it will check every redraw if there is a collision. WRONG WRONG WRONG if i do it that way, the game is moving slow... but thats how everyone else does it.... I'll start there.

	public SnakeCell head = null ; 
	public SnakeGameBoard snakeGameBoard ;
	public int lives = 3 ;
	public int invulnerability = 0 ; 
	public int playerNumber ;

	public SnakeTail(SnakeGameBoard snakeGameBoard) {
		this.snakeGameBoard = snakeGameBoard ;
		if (snakeGameBoard.players == 0) { 
			snakeGameBoard.players++ ;
			this.playerNumber = snakeGameBoard.players ; 
			head = new SnakeCell(250, 250, Direction.NORTH);
		}
		else if (snakeGameBoard.players == 1) {
			snakeGameBoard.players++ ;
			this.playerNumber = snakeGameBoard.players ; 
			head = new SnakeCell(260, 250, Direction.SOUTH);
		}

	}

	@Override
	public void run() {
		// THIS SHOULD HAVE A SCHEDULED EXECTUROR BULLSHIT BUT ILL DO THAT LATER
		try {

			move() ;
		}catch(Exception e) { 
			e.printStackTrace();
		}
	} 

	public void move() {

		if(invulnerability > 0 ) { 
			invulnerability-- ; 
		}
		SnakeCell temp = head ;
		int moveX =  temp.getDirection().move()[0] ;
		int moveY =  temp.getDirection().move()[1] ; 
		Point newHeadPoint = new Point(moveX + temp.x, moveY + temp.y) ;
		if(!checkBounds(newHeadPoint) || !checkCollision(newHeadPoint)) { 
			System.out.println("SNAKETAIL MOVE ERROR");
			if(invulnerability <= 0) {
				die() ; 
				return ;
			} else { return ; }
		}else if (checkFruit(newHeadPoint)) {
			System.out.println("CHECKFRUIT - SNAKETAIL MOVE");
			eat() ; 
			return ;
		}
		if(head.prev == null) {
			head = new SnakeCell(newHeadPoint.x, newHeadPoint.y, temp.direction) ;
			snakeGameBoard.snakeGameModel.setSnakeLocation(printableList(), playerNumber);
			return ;
		}
		head = new SnakeCell(newHeadPoint.x, newHeadPoint.y, temp.direction) ;
		head.prev = temp ;
		temp.next = head ;
		SnakeCell runner = head ; 
		while(true) { 
			if(runner.prev == null) {
				runner.next.prev = null ;
				snakeGameBoard.snakeGameModel.setSnakeLocation(printableList(), playerNumber);
				return ;
			}
			runner = runner.prev ;
			snakeGameBoard.snakeGameModel.setSnakeLocation(printableList(), playerNumber);
		}
	}

	public void eat() {
		SnakeCell temp = head ;
		int moveX =  temp.getDirection().move()[0] ;
		int moveY =  temp.getDirection().move()[1] ; 
		if(head.prev == null) {
			head = new SnakeCell(temp.x + moveX, temp.y + moveY, temp.direction) ;
			temp.next = head ;
			head.prev = temp ; 
			snakeGameBoard.snakeGameModel.setSnakeLocation(printableList(), playerNumber);
			snakeGameBoard.snakeGameModel.addFruit() ;
			snakeGameBoard.updateInfo();
			snakeGameBoard.setSpeed();
			return ;
		}
		head = new SnakeCell(temp.getX() + moveX, temp.getY() + moveY, temp.direction) ;
		head.prev = temp ; 
		temp.next = head ; 
		snakeGameBoard.updateInfo();
		snakeGameBoard.snakeGameModel.setSnakeLocation(printableList(), playerNumber);
		snakeGameBoard.snakeGameModel.addFruit() ;
		snakeGameBoard.setSpeed();
		System.out.println(snakeGameBoard.speed) ;
	}

	public void die() { 
		snakeGameBoard.running = false ; 
		invulnerability = 3 ;
		snakeGameBoard.snakeGameModel.loseLife();
		snakeGameBoard.gameBoard.displayPause();
		if (snakeGameBoard.snakeGameModel.lives < 0 ) {
			snakeGameBoard.gameBoard.displayDeath();
		}
	}

	public ArrayList<Point> printableList() { 
		ArrayList<Point> snake = new ArrayList<Point>() ;
		if(head.prev == null) {
			snake.add(new Point(head.x, head.y)) ; 
			return snake ;  
		}
		SnakeCell temp = head ; 
		snake.add(new Point(head.x, head.y)) ; 
		do { 
			snake.add(new Point(temp.prev.x, temp.prev.y)) ; 
			temp = temp.prev ; 
		}while(temp.prev != null) ;
		return snake ; 
	}

	//****** COLLISION CHECKERS *********
	public boolean checkCollision(Point point) {
		Rectangle checkRect = new Rectangle(point.x, point.y, 10, 10) ; 
		for(Point snake : printableList()) { 
			Rectangle snakeCell = new Rectangle(snake.x, snake.y, 10, 10) ; 
			if(checkRect.intersects(snakeCell)) { 
				return false ;
			}
		}try {
			for(Point snake : snakeGameBoard.snakeGameModel.playerTWoLocation) { 
				Rectangle snakeCell = new Rectangle(snake.x, snake.y, 10, 10) ; 
				if(checkRect.intersects(snakeCell)) { 
					return false ;
				}
			}
		}catch(Exception e) { ; }
		return true; 
	}

	public boolean checkBounds(Point point) {
		if( (0 > point.x || point.x > 490) || (0 > point.y || point.y > 490)) { 
			return false;
		}else { return true; }
	}
	public boolean checkFruit(Point point) {
		Rectangle checkRect = new Rectangle(point.x, point.y, 10, 10) ; 
		if(checkRect.intersects(new Rectangle(snakeGameBoard.snakeGameModel.snakeFruit.x, snakeGameBoard.snakeGameModel.snakeFruit.y, 10, 10))) { 
			return true;
		}else { return false; }
	}
	// ***** linked list with direction for all the snake cells

	class SnakeCell {
		int x ;
		int y ;
		Direction direction; 
		SnakeCell next = null ; 
		SnakeCell prev = null ;

		public SnakeCell(int x, int y, Direction direction) { 
			this.x = x ; 
			this.y = y ; 
			this.direction = direction ; 
		}
		public Direction getDirection() { 
			return direction ; 
		}
		public void setDirection(Direction direction) {
			this.direction = direction ; 
		}

		public int getX() { 
			return x ;
		}
		public int getY() {
			return y ; 
		}

	}

}
