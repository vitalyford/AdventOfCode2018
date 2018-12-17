import java.util.*;

class Point implements Comparable<Point> {
	int row;
	int col;
	Point parent;
	Character c;
	int health;
	int steps;
	boolean dead;
	int attack;
	
	Point() {
		row = col = -1;
		parent = null;
		c = null;
		health = 200;
		steps = 0;
		dead = false;
		attack = 3;
	}
	
	Point(int row, int col, Character c, Point parent, int attack) {
		this.row = row;
		this.col = col;
		this.parent = parent;
		this.c = c;
		health = 200;
		steps = (parent == null) ? 0 : parent.steps + 1;
		dead = false;
		this.attack = attack;
	}
	
	public int compareTo(Point p) {
		if (steps == p.steps) {
			if (row == p.row) return col - p.col;
			return row - p.row;
		}
		return steps - p.steps;
	}
	
	@Override
	public boolean equals(Object p) {
		return (row == ((Point)p).row && col == ((Point)p).col);
	}
	
	
	public String toString() {
		return row + " " + col;
	}
}

public class AdventOfCode2018Day15 {
	
	static int elfCount = 0;
	static int goblinCount = 0;
	
	/**
	 * Helper method to process the 4 surrounding points around the current point p
	 * @return True if the closest enemy is found and false otherwise
	 */
	private static boolean processSurroundingPoints(Character[][] grid, Point p, Point player, ArrayList<Point> visited, 
			PriorityQueue<Point> paths, int changeRow, int changeCol, PriorityQueue<Point> players) {
		
		Point newP = new Point(p.row+changeRow, p.col+changeCol, '.', p, 3);
		if (visited.contains(newP)) return false; // no reason to continue if we have already been at this point
		
		if (grid[newP.row][newP.col] == '.') {
			paths.add(newP);
			visited.add(newP);
		}
		else if (grid[newP.row][newP.col] == oppositeEnemy(player.c)) { // found the enemy != '#' && grid[newP.row][newP.col] !=
			// move towards the closest enemy
			updateLocation(grid, newP, player, changeRow, changeCol, players);
			return true;
		}
		return false;
	}
	
	/**
	 * @return True if the game is over and the field is clean of enemies, false otherwise
	 */
	private static void updateLocation(Character[][] grid, Point newP, Point player, int changeRow, 
			int changeCol, PriorityQueue<Point> players) {
		Point start = newP;
		while (start.parent.parent != null) start = start.parent;
		// check if the enemy is literally right in front of us
		if ((Math.abs(player.row - newP.row) + Math.abs(player.col - newP.col)) == 1) {
			// just fight!
			fightTheEasiestTarget(grid, player, players);
		}
		else {
			// move and try to fight
			grid[player.row][player.col] = '.';
			player.row = start.row;
			player.col = start.col;
			grid[player.row][player.col] = player.c;
			fightTheEasiestTarget(grid, player, players);
		}
	}
	
	private static void fightTheEasiestTarget(Character[][] grid, Point player, PriorityQueue<Point> players) {
		int minHealth = Integer.MAX_VALUE;
		Point selectedEnemy = null;
		if (grid[player.row+1][player.col] == oppositeEnemy(player.c)) { // test the bottom
			Point enemy = findEnemyByLocation(players, player.row+1, player.col);
			if (enemy != null && !enemy.dead && minHealth >= enemy.health) {
				minHealth = enemy.health;
				selectedEnemy = enemy;
			}
		}
		if (grid[player.row][player.col+1] == oppositeEnemy(player.c)) { // test the right
			Point enemy = findEnemyByLocation(players, player.row, player.col+1);
			if (enemy != null && !enemy.dead && minHealth >= enemy.health) {
				minHealth = enemy.health;
				selectedEnemy = enemy;
			}
		}
		if (grid[player.row][player.col-1] == oppositeEnemy(player.c)) { // test the left
			Point enemy = findEnemyByLocation(players, player.row, player.col-1);
			if (enemy != null && !enemy.dead && minHealth >= enemy.health) {
				minHealth = enemy.health;
				selectedEnemy = enemy;
			}
		}
		if (grid[player.row-1][player.col] == oppositeEnemy(player.c)) { // test the top
			Point enemy = findEnemyByLocation(players, player.row-1, player.col);
			if (enemy != null && !enemy.dead && minHealth >= enemy.health) {
				minHealth = enemy.health;
				selectedEnemy = enemy;
			}
		}
		
		if (selectedEnemy != null) {
			selectedEnemy.health -= player.attack;
			if (selectedEnemy.health <= 0) { // RIP :(
				grid[selectedEnemy.row][selectedEnemy.col] = '.';
				if (selectedEnemy.c == 'E') elfCount--;
				else goblinCount--;
				selectedEnemy.dead = true; // enemy is dead for real
			}
		}
	}
	
	private static Point findEnemyByLocation(PriorityQueue<Point> players, int row, int col) {
		for (Point p : players) {
			if (p.row == row && p.col == col && !p.dead) return p;
		}
		return null;
	}
	
	private static Character oppositeEnemy(Character c) {
		return (c == 'G') ? 'E' : 'G';
	}
	
	private static void move(Character[][] grid, PriorityQueue<Point> players) {
		// use BFS to find the nearest places the player can go until it reaches an enemy
		for (Point player : players) {
			if (player.dead) continue;
			PriorityQueue<Point> paths = new PriorityQueue<>();
			ArrayList<Point> visited = new ArrayList<>();
			paths.add(player);
			visited.add(player);
			while (!paths.isEmpty()) {
				Point p = paths.poll();
				// add top, left, right, bottom points from the current one to the paths
				// first, try to check the top one, then left, right, and bottom
				if (processSurroundingPoints(grid, p, player, visited, paths, -1,  0, players)) break; // top
				if (processSurroundingPoints(grid, p, player, visited, paths,  0, -1, players)) break; // left
				if (processSurroundingPoints(grid, p, player, visited, paths,  0,  1, players)) break; // right
				if (processSurroundingPoints(grid, p, player, visited, paths,  1,  0, players)) break; // bottom
			}
		}
	}
	
	private static void printGrid(Character[][] grid) {
        for (int row = 0; row < grid.length; row++) {
        	for (int col = 0; col < grid[0].length; col++) {
        		System.out.print(grid[row][col]);
        	}
        	System.out.println();
        }
        System.out.println();
	}
	
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        ArrayList<String> input = new ArrayList<>();
        while (true) {
        	String line = s.nextLine();
        	if (line.equals("")) break;
        	input.add(line);
        }
        s.close();
        // create the playground and find all players
        int eCount = 0, gCount = 0;
        Character[][] g = new Character[input.size()][input.get(0).length()];
        PriorityQueue<Point> ps = new PriorityQueue<>();
        for (int row = 0; row < input.size(); row++) {
        	for (int col = 0; col < input.get(0).length(); col++) {
        		g[row][col] = input.get(row).charAt(col);
        		if (g[row][col] == 'E' || g[row][col] == 'G') {
        			ps.add(new Point(row, col, g[row][col], null, 3));
        			if (g[row][col] == 'E') eCount++;
        			else gCount++;
        		}
        	}
        }

        int startAttack = 3;
        while (true) { // answers the second part, trying to brute-force the attack
        	elfCount = eCount;
        	goblinCount = gCount;
        	Character[][] grid = copyGrid(g);
        	PriorityQueue<Point> players = copyPlayers(ps, startAttack);
	        // move everyone one step further if applicable
	        //printGrid(grid);
	        int countSteps = 0;
	        while (true) {
	        	move(grid, players);
	        	//System.out.println("Round #" + countSteps);
	        	//printGrid(grid);
	        	// delete players who are dead and re-add them to the queue
	        	PriorityQueue<Point> temp = new PriorityQueue<>();
	        	while (!players.isEmpty()) if (!players.peek().dead) temp.add(players.poll()); else players.poll();
	        	players = temp;
	        	if (elfCount == 0 || goblinCount == 0) break;
	        	countSteps++;
	        }
	        if (startAttack == 3) { // answer to the first part
		        // find sum of all hit points or health for the first part
		        int sum = sumHitPoints(players);
		        System.out.println("Answer to the first part: " + countSteps * sum);
	        }
	        if (elfCount == eCount) {
	            // find sum of all hit points or health for the second part
	            int sum = sumHitPoints(players);
	            System.out.println("Answer to the second part: " + countSteps * sum);
	        	break;
	        }
	        startAttack++;
        }
    }
    
    private static int sumHitPoints(PriorityQueue<Point> players) {
    	int sum = 0;
    	for (Point player : players) {
        	if (!player.dead) {
        		sum += player.health;
        	}
        }
    	return sum;
    }
    
    private static Character[][] copyGrid(Character[][] g) {
    	Character[][] grid = new Character[g.length][g[0].length];
    	for (int row = 0; row < g.length; row++) {
    		for (int col = 0; col < g[0].length; col++) {
    			grid[row][col] = g[row][col];
    		}
    	}
    	return grid;
    }
    
    private static PriorityQueue<Point> copyPlayers(PriorityQueue<Point> ps, int startAttack) {
    	PriorityQueue<Point> players = new PriorityQueue<>();
    	for (Point p : ps) {
    		if (p.c == 'E')
    			players.add(new Point(p.row, p.col, p.c, p.parent, startAttack));
    		else
    			players.add(new Point(p.row, p.col, p.c, p.parent, p.attack));
    	}
    	return players;
    }
}

/*
################################
######################........##
####################...........#
##############G.G..G.#.......#.#
#############.....G...#....###.#
############..##.G.............#
#############...#...GG......####
#############......G......######
##############G....EG.....######
#############.......G.....######
############.....G......#.######
###########......E...G.#########
##########....#####......#######
##########G..#######......######
######......#########....#######
#####....G..#########....#######
###.......#.#########....#######
###.G.....#.#########E...#######
#........##.#########E...#######
#.......###..#######...E.#######
#.#.#.........#####.......######
#.###.#.###.G..............#####
####.....###..........E.##.#####
#......G####.E..........########
###..G..####...........####..###
####..########..E......###...###
###..............#...E...#.#####
##.........##....##........#####
#.......#.####.........#########
#...##G.##########....E#########
#...##...#######################
################################


*/