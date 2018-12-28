import java.util.*;

/*
 * Use A* algorithm for pathfinding: f(n) = g(n) + h(n)
 * and keep positions in a priority queue
 */
class Position implements Comparable<Position> {
	int row;
	int col;
	int g;       // the current cost of this position in minutes
	char equip;  // c = climbing gear, t = torch, n = neither
	char region; // . rocky, = wet, | narrow
	Position target;
	boolean deleteme;
	
	Position(int row, int col, char equip, char region, Position target, int g) {
		this.row = row;
		this.col = col;
		this.equip = equip;
		this.target = target;
		this.region = region;
		this.g = g;
		deleteme = false;
	}
	
	public Position copyMe() {
		Position p = new Position(row, col, equip, region, target, g);
		return p;
	}
	
	// compare by the cost (aka f(n))
	public int compareTo(Position p) {
		return (g + h()) - (p.g + p.h());
	}
	
	@Override
	public boolean equals(Object p) {
		return (row == ((Position)p).row && col == ((Position)p).col && equip == ((Position)p).equip);
	}
	
	// manhattan distance for heuristics
	public int h() {
		return (int)(((Math.abs(row - target.row) + Math.abs(col - target.col))));
	}
	
	public boolean allowedToGo(char reg) {
		boolean canGo = true;
		switch (reg) {
		case '.':
			canGo = (equip == 'c' || equip == 't');
			break;
		case '=':
			canGo = (equip == 'c' || equip == 'n');
			break;
		case '|':
			canGo = (equip == 't' || equip == 'n');
			break;
		}
		return canGo;
	}
	
	public void switchGear() {
		switch (equip) {
		case 'c':
			if (region == '.') equip = 't';
			else if (region == '=') equip = 'n';
			break;
		case 't':
			if (region == '.') equip = 'c';
			else if (region == '|') equip = 'n';
			break;
		case 'n':
			if (region == '=') equip = 'c';
			else if (region == '|') equip = 't';
			break;
		}
	}
}

public class AdventOfCode2018Day22 {
	
	private static boolean isVisited(Position node, PriorityQueue<Position> pq, ArrayList<Position> visited) {
		// check if the node is in the pq
		for (Position p : pq) {
			if (p.equals(node) && !p.deleteme) {
				if (node.g >= p.g) return true;
				else p.deleteme = true;
			}
		}
		// check if we have already visited it
		boolean isVisited = false;
		for (Position p : visited) {
			if (p.equals(node)) {
				if (node.g >= p.g) return true;
				else {
					p.g = node.g;
					isVisited = true;
				}
			}
		}
		if (!isVisited) visited.add(node);
		return false;
	}
	
	private static Position runAstar(char[][] grid, Position target) {
		ArrayList<Position> visited = new ArrayList<>();
		Position start = new Position(0, 0, 't', grid[0][0], target, 0);
		PriorityQueue<Position> pq = new PriorityQueue<>();
		pq.add(start);
		visited.add(start);
		int count = 0;
        while (!pq.isEmpty()) {
        	Position curr = pq.poll();
        	if (curr.deleteme) continue;
        	if (count++ % 10000 == 0) System.out.println(curr.row + "," + curr.col + ":" + curr.g + " " + curr.h());
        	if (curr.equals(target) && curr.equip != 't') {
        		System.out.println("Almost there:" + curr.row + "," + curr.col + ":" + curr.g + " " + curr.h() + " " + curr.equip);
        	}
        	if (curr.row + 1 == grid.length || curr.col + 1 == grid[0].length) System.out.println("Need more padding");
    		if (curr.equals(target) && curr.equip == 't') {
    			return curr;
    		}
    		// options: top, right, bottom, left, switch gear, or put both away
    		if (curr.row - 1 >= 0 && curr.allowedToGo(grid[curr.row-1][curr.col])) { // can go top
    			Position top = new Position(curr.row-1, curr.col, curr.equip, grid[curr.row-1][curr.col], target, curr.g+1);
    			if (!isVisited(top, pq, visited)) pq.add(top);
    		}
    		if (curr.col + 1 < grid[0].length && curr.allowedToGo(grid[curr.row][curr.col+1])) { // can go right
    			Position right = new Position(curr.row, curr.col+1, curr.equip, grid[curr.row][curr.col+1], target, curr.g+1);
    			if (!isVisited(right, pq, visited)) pq.add(right);
    		}
    		if (curr.row + 1 < grid.length && curr.allowedToGo(grid[curr.row+1][curr.col])) { // can go bottom
    			Position bottom = new Position(curr.row+1, curr.col, curr.equip, grid[curr.row+1][curr.col], target, curr.g+1);
    			if (!isVisited(bottom, pq, visited)) pq.add(bottom);
    		}
    		if (curr.col - 1 >= 0 && curr.allowedToGo(grid[curr.row][curr.col-1])) { // can go left
    			Position left = new Position(curr.row, curr.col-1, curr.equip, grid[curr.row][curr.col-1], target, curr.g+1);
    			if (!isVisited(left, pq, visited)) pq.add(left);
    		}
    		// switching gear
			Position switchEquip = new Position(curr.row, curr.col, curr.equip, curr.region, target, curr.g+7);
			switchEquip.switchGear();
			if (!isVisited(switchEquip, pq, visited)) pq.add(switchEquip);
        }
        return null;
	}
	
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        s.next();
        int depth = s.nextInt();
        s.next();
        String[] t = s.nextLine().trim().split(",");
        int tCol = Integer.parseInt(t[0]);
        int tRow = Integer.parseInt(t[1]);
        s.close();
        
        // find geologic index of every cell in the grid
        // then calculate the erosion level
        int incRow = 250; // padding for the grid, extending beyond the target
        int incCol = 250;
        int[][] erosion = new int[tRow+1+incRow][tCol+1+incCol];
        for (int row = 0; row <= tRow+incRow; row++) {
        	for (int col = 0; col <= tCol+incCol; col++) {
        		int geo = 0;
        		if ((row == 0 && col == 0) || (row == tRow && col == tCol)) {
        			geo = 0;
        		}
        		else if (row == 0) {
        			geo = col * 16807;
        		}
        		else if (col == 0) {
        			geo = row * 48271;
        		}
        		else {
        			geo = erosion[row][col-1] * erosion[row-1][col];
        		}
        		erosion[row][col] = (geo + depth) % 20183;
        	}
        }
        
        // find rocky, wet, or narrow
        char[][] g = new char[tRow+1+incRow][tCol+1+incCol]; // define the grid
        int riskLevel = 0;
        for (int row = 0; row <= tRow+incRow; row++) {
        	for (int col = 0; col <= tCol+incCol; col++) {
        		if (erosion[row][col] % 3 == 0) {
        			g[row][col] = '.';
        		}
        		else if (erosion[row][col] % 3 == 1) {
        			g[row][col] = '=';
        			if (row <= tRow && col <= tCol) riskLevel += 1;
        		}
        		else {
        			g[row][col] = '|';
        			if (row <= tRow && col <= tCol) riskLevel += 2;
        		}
        	}
        }
        
        System.out.println("Answer to the first part: " + riskLevel);
        System.out.println("Go, grab some coffee, it will take some time to finish but it eventually will find the answer.");
        System.out.println("For now, just outputting some debugging info to keep you entertained.");
        
        Position target = new Position(tRow, tCol, 't', '.', null, 0);
        try {
        	System.out.println("Answer to the second part: " + runAstar(g, target).g);
        }
        catch (Exception e) {
        	System.out.println("Whoops, it could not find the target, sorry");
        }
    }
    
    // helper method, does not do much but prints the grid
    private static void printGrid(char[][] grid) {
        for (int row = 0; row < grid.length; row++) {
        	for (int col = 0; col < grid[0].length; col++) {
        		System.out.print(grid[row][col]);
        	}
        	System.out.println();
        }
        System.out.println();
    }
}

/*
depth: 4002
target: 5,746


depth: 510
target: 10,10


*/