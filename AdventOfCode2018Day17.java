import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.FileWriter;
import java.io.IOException;

class Point {
	int row;
	int col;
	
	Point() { row = col = 0; }
	
	Point(int row, int col) { this.row = row; this.col = col; }
	
	@Override
	public boolean equals(Object o) {
		return (row == ((Point)o).row && col == ((Point)o).col);
	}
	
	public String toString() {
		return row + " " + col;
	}
}

class Range {
	int x1;
	int x2;
	int y1;
	int y2;
	
	Range() {
		x1 = x2 = 0;
		y1 = y2 = 0;
	}
	
	Range(int x1, int x2, int y1, int y2) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
	}
}

public class AdventOfCode2018Day17 {
	
	private static int waterSourceX = -1;
	
	private static Character[][] makeGrid(ArrayList<Range> ranges) {
		// find the top-left and bottom-right corners of the grid
		int topX = Integer.MAX_VALUE, topY = 0, bottomX = Integer.MIN_VALUE, bottomY = Integer.MIN_VALUE;
		for (Range r : ranges) {
			if (r.x1 < topX) topX = r.x1;
			if (r.x2 > bottomX) bottomX = r.x2;
			if (r.y2 > bottomY) bottomY = r.y2;
		}
		// make room for the water overflow
		topX--; bottomX++;
		//System.out.println(topX + " " + topY + " " + bottomX + " " + bottomY);
		Character[][] grid = new Character[bottomY - topY + 1][bottomX - topX + 1];
    	for (int row = 0; row < grid.length; row++) {
    		for (int col = 0; col < grid[0].length; col++) {
    			grid[row][col] = '.';
    		}
    	}
		// fill up the grid
		for (Range r : ranges) {
			for (int row = r.y1 - topY; row <= r.y2 - topY; row++) {
				for (int col = r.x1 - topX; col <= r.x2 - topX; col++) {
					grid[row][col] = '#';
				}
			}
		}
		grid[0][500-topX] = '+'; // set the spring water source
		waterSourceX = 500 - topX;
		return grid;
	}
	
	private static boolean thisIsWaterLevel(Point top, Character[][] grid) {
		boolean reachedLeftWall = false, reachedRightWall = false;
		// go left until the wall
		if (top.row + 1 >= grid.length) return false;
		for (int col = top.col-1; col >= 0; col--) {
			if (grid[top.row+1][col] == '.' || grid[top.row+1][col] == '|') break;
			if (grid[top.row][col] == '#') { reachedLeftWall = true; break; }
		}
		// go right until the wall
		for (int col = top.col+1; col < grid[0].length; col++) {
			if (grid[top.row+1][col] == '.' || grid[top.row+1][col] == '|') break;
			if (grid[top.row][col] == '#') { reachedRightWall = true; break; }
		}
		return reachedLeftWall && reachedRightWall;
	}
	
	private static void runWater(Character[][] g, Stack<Point> stack, Stack<Point> visited) {
		while (!stack.isEmpty()) {
			Point top = stack.peek();
			//visited.push(top);
			if (g[top.row][top.col] == '.') g[top.row][top.col] = '|';
			// try to go down
			Point down  = new Point(top.row + 1, top.col);
			Point left  = new Point(top.row, top.col - 1);
			Point right = new Point(top.row, top.col + 1);
			Character fillChar = g[top.row][top.col];
			if (down.row < g.length && g[down.row][down.col] == '.') {
				g[down.row][down.col] = '|';
				stack.push(down);
			}
			else if (left.col >= 0 && g[left.row][left.col] == '.' && left.row+1 < g.length && g[top.row+1][top.col] != '|') {
				// check if it is even worth going left
				if (thisIsWaterLevel(top, g)) {
					// fill in with water as ~
					g[top.row][top.col] = fillChar = '~';
				}
				// try to go left
				boolean canGoLeft = true;
				while (canGoLeft) {
					left = new Point(top.row, top.col - 1);
					if (g[left.row][left.col] == '#') break;
					if (g[left.row+1][left.col] == '#' || g[left.row+1][left.col] == '~') {
						g[left.row][left.col] = fillChar;
						stack.push(left);
						top = stack.peek();
					}
					else if (g[left.row+1][left.col] == '.') {
						g[left.row][left.col] = '|';
						stack.push(left);
						canGoLeft = false; // at this point we need to go down
					}
					else {
						stack.pop();
						canGoLeft = false;
					}
					if (left.col < 0) canGoLeft = false;
				}
			}
			else if (right.col < g[0].length && g[right.row][right.col] == '.' && right.row+1 < g.length && g[top.row+1][top.col] != '|') {
				// check if it is even worth going right
				if (thisIsWaterLevel(top, g)) {
					// fill in with water as ~
					g[top.row][top.col] = fillChar = '~';
				}
				// try to go right
				boolean canGoRight = true;
				while (canGoRight) {
					right = new Point(top.row, top.col + 1);
					if (g[right.row][right.col] == '#') break;
					if (g[right.row+1][right.col] == '#' || g[right.row+1][right.col] == '~') {
						g[right.row][right.col] = fillChar;
						stack.push(right);
						top = stack.peek();
					}
					else if (g[right.row+1][right.col] == '.') {
						g[right.row][right.col] = '|';
						stack.push(right);
						canGoRight = false; // at this point we need to go down
					}
					else {
						stack.pop();
						canGoRight = false;
					}
					if (right.col >= g[0].length) canGoRight = false;
				}
			}
			else {
				if (thisIsWaterLevel(top, g)) g[top.row][top.col] = '~';
				stack.pop();
			}
			// make sure that the whole level of ~ is filled up
			if (g[top.row][top.col] == '~') {
				// go left until the wall
				for (int col = top.col-1; col >= 0; col--) {
					if (g[top.row][col] == '|' || g[top.row][col] == '~') g[top.row][col] = '~';
					else break;
				}
				// go right until the wall
				for (int col = top.col+1; col < g[0].length; col++) {
					if (g[top.row][col] == '|' || g[top.row][col] == '~') g[top.row][col] = '~';
					else break;
				}
			}
		}
	}
	
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        Pattern patternX = Pattern.compile("x=(\\d*)(?:\\.{2}(\\d*))?");
        Pattern patternY = Pattern.compile("y=(\\d*)(?:\\.{2}(\\d*))?");
        ArrayList<Range> ranges = new ArrayList<>();
        while (true) {
        	String line = s.nextLine();
        	if (line.equals("")) break;
        	Matcher mX = patternX.matcher(line);
        	Matcher mY = patternY.matcher(line);
        	mX.find();
        	mY.find();
        	int x1 = 0, x2 = 0, y1 = 0, y2 = 0;
        	if (mX.group(2) == null) x1 = x2 = Integer.parseInt(mX.group(1));
        	else { x1 = Integer.parseInt(mX.group(1)); x2 = Integer.parseInt(mX.group(2)); }
        	if (mY.group(2) == null) y1 = y2 = Integer.parseInt(mY.group(1));
        	else { y1 = Integer.parseInt(mY.group(1)); y2 = Integer.parseInt(mY.group(2)); }
        	ranges.add(new Range(x1, x2, y1, y2));
        }
        s.close();
        Character[][] grid = makeGrid(ranges);
        
        Stack<Point> stack = new Stack<>();
        Stack<Point> visited = new Stack<>();
        stack.push(new Point(1, waterSourceX)); // push the water source + 1 on the stack
        // use DFS to run the water around
        runWater(grid, stack, visited);
        FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("grid.txt");
			writeGrid(grid, fileWriter);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //printGrid(grid);
        System.out.println("Answer to the first part: " + countWater(grid));
    }
    
    private static void writeGrid(Character[][] grid, FileWriter fileWriter) throws IOException {
    	for (int row = 0; row < grid.length; row++) {
    		for (int col = 0; col < grid[0].length; col++) {
    			fileWriter.write(grid[row][col]);
    		}
    		fileWriter.write("\n");
    	}
    	fileWriter.write("\n");
    }
    
    private static int countWater(Character[][] grid) {
    	int sum = 0;
    	int water = 0;
    	boolean firstClayFound = false;
    	for (int row = 0; row < grid.length; row++) {
    		for (int col = 0; col < grid[0].length; col++) {
    			if (grid[row][col] == '#') firstClayFound = true;
    			else if (firstClayFound && (grid[row][col] == '~' || grid[row][col] == '|')) sum++;
    			if (firstClayFound && grid[row][col] == '~') water++;
    		}
    	}
    	System.out.println("Answer to the second part: " + water);
    	return sum;
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
}
/*

*/