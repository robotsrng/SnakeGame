public enum Direction { 
		NORTH (0, -10),
		SOUTH (0, 10),
		EAST (10, 0),
		WEST (-10, 0);
		
		private final int[] movement = new int[2] ;
		
		Direction(int x, int y){ 
			this.movement[0] = x; 
			this.movement[1] = y ; 
		}
		
		public int[] move() {
			return movement ; 
		}
	}