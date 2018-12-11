import java.util.*;

/**
 * Use the integral image algorithm, very well known in computer vision
 * https://en.wikipedia.org/wiki/Summed-area_table
 * It will help with the finding the summed areas of any square size
 * 
 */
public class AdventOfCode2018Day11 {
	
	private static void calcPower(int[][] grid, int serial, int n, int m) {
		// do the calculation part described in the problem
		for (int row = 0; row < n; row++) {     // Y
			for (int col = 0; col < m; col++) { // X
				int rackID = (col + 1) + 10;
				int pwrLvl = rackID * (row + 1);
				pwrLvl += serial;
				pwrLvl *= rackID;
				pwrLvl = (pwrLvl < 100) ? 0 : ((int)(pwrLvl / 100) % 10);
				pwrLvl -= 5;
				grid[row][col] = pwrLvl;
			}
		}

		// do the integral image
		for (int row = 0; row < n; row++) {     // Y
			for (int col = 0; col < m; col++) { // X
				if (row == 0 && col == 0) continue;
				if (row == 0) {
					grid[row][col] += grid[row][col-1];
				}
				else if (col == 0) {
					grid[row][col] += grid[row-1][col];
				}
				else {
					grid[row][col] += grid[row][col-1] + grid[row-1][col] - grid[row-1][col-1];
				}
			}
		}
	}
	
	private static void findMaxPowerForAnySquare(int[][] g, int n, int m, int startSquare, int endSquare) {
		int maxPower = Integer.MIN_VALUE;
		int topX = -1, topY = -1;
		int squareSize = -1;
		for (int s = startSquare; s <= endSquare; s++) { // define square size
			for (int r = s - 1; r < n; r++) {
				for (int c = s - 1; c < m; c++) {
					// look up an s x s square's power and keep track of the max
					int total = 0;
					if (r-s < 0 && c-s < 0) total = g[r][c];
					else if (r-s < 0 && c-s >= 0) {
						total = g[r][c] - g[r][c-s];
					}
					else if (c-s < 0 && r-s >= 0) {
						total = g[r][c] - g[r-s][c];
					}
					else {
						total = g[r][c] + g[r-s][c-s] - g[r][c-s] - g[r-s][c];
					}
					if (total > maxPower) {
						maxPower = total;
						squareSize = s;
						topX = c - s + 1;
						topY = r - s + 1;
					}
				}
			}
		}
		if (startSquare == endSquare)
			System.out.println("Answer to the first part: " + (topX+1) + "," + (topY+1));
		else
			System.out.println("Answer to the second part: " + (topX+1) + "," + (topY+1) + "," + squareSize);
	}
	
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        int serial = s.nextInt();
        s.close();
        
        int n = 300, m = 300;
        int[][] grid = new int[n][m];
        
        calcPower(grid, serial, n, m);
        
        // first part
        findMaxPowerForAnySquare(grid, n, m, 3, 3);
        // second part
        findMaxPowerForAnySquare(grid, n, m, 1, 300);
    }
}

