import java.util.*;

public class AdventOfCode2018Day18 {
	
	private static boolean treesNearbyForOpen(Character[][] grid, int row, int col) {
		int treeCount = 0;
		for (int r = row - 1; r <= row + 1; r++) {
			for (int c = col - 1; c <= col + 1; c++) {
				if (c == col && r == row) continue;
				if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) continue;
				if (grid[r][c] == '|') treeCount++;
			}
		}
		return (treeCount >= 3 ? true : false);
	}
	
	private static boolean lumberyardNearbyForTrees(Character[][] grid, int row, int col) {
		int lumberyardCount = 0;
		for (int r = row - 1; r <= row + 1; r++) {
			for (int c = col - 1; c <= col + 1; c++) {
				if (c == col && r == row) continue;
				if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) continue;
				if (grid[r][c] == '#') lumberyardCount++;
			}
		}
		return (lumberyardCount >= 3 ? true : false);
	}
	
	private static boolean lumberyardNearbyForLumberyard(Character[][] grid, int row, int col) {
		int lumberyardCount = 0, treeCount = 0;
		for (int r = row - 1; r <= row + 1; r++) {
			for (int c = col - 1; c <= col + 1; c++) {
				if (c == col && r == row) continue;
				if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length) continue;
				if (grid[r][c] == '#') lumberyardCount++;
				else if (grid[r][c] == '|') treeCount++;
			}
		}
		return (lumberyardCount >= 1 && treeCount >= 1 ? true : false);
	}
	
	private static Character[][] processGrid(Character[][] grid) {
		Character[][] newGrid = new Character[grid.length][grid[0].length];
		for (int row = 0; row < grid.length; row++) {
			for (int col = 0; col < grid[0].length; col++) {
				// check for open acre
				if (grid[row][col] == '.' && treesNearbyForOpen(grid, row, col)) {
					newGrid[row][col] = '|';
				}
				// check for tree acre
				else if (grid[row][col] == '|' && lumberyardNearbyForTrees(grid, row, col)) {
					newGrid[row][col] = '#';
				}
				// check for lumberyard acre
				else if (grid[row][col] == '#') {
					if (lumberyardNearbyForLumberyard(grid, row, col)) newGrid[row][col] = '#';
					else newGrid[row][col] = '.';
				}
				else newGrid[row][col] = grid[row][col];
			}
		}
		return newGrid;
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
        Character[][] grid = new Character[input.size()][input.get(0).length()];
        for (int row = 0; row < input.size(); row++) {
        	for (int col = 0; col < input.get(0).length(); col++) {
        		grid[row][col] = input.get(row).charAt(col);
        	}
        }
        ArrayList<Integer> answers = new ArrayList<>();
        boolean noGridProcessing = false;
        int startRep = -1, index = -1;
        for (long i = 0; i < 1000000000; i++) {
        	if (startRep == -1) {
	        	if (!noGridProcessing) grid = processGrid(grid);
		        if (i == 9) System.out.println("Answer to the first part: " + countWoodAndLumberyards(grid));
		        int a = countWoodAndLumberyards(grid);
		        index = answers.lastIndexOf(a);
		        if (index != -1 && answers.size() - index == 28 && index > 400) { // this number of trees and lumberyards have been seen before
		        	noGridProcessing = true; // at this point, the results are repetitive
		        	startRep = index;
		        }
		        else {
		        	answers.add(a);
		        }
        	}
        	else {
        		++startRep;
        		if (startRep == answers.size()) startRep = index;
        	}
        }
        System.out.print("Answer to the second part: " + answers.get(startRep));
    }
    
    private static int countWoodAndLumberyards(Character[][] grid) {
    	int trees = 0, lumberyard = 0;
        for (int row = 0; row < grid.length; row++) {
        	for (int col = 0; col < grid[0].length; col++) {
        		if (grid[row][col] == '|') trees++;
        		else if (grid[row][col] == '#') lumberyard++;
        	}
        }
        return trees * lumberyard;
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
.||.###.|.#|.|...|#|..##...|..#....#.|..|..##.#|..
..|..|#..##..|..#.......|#.#|##..|#...#...|.|..||.
#..#|..........#...#.#.|...||...##||..#..|#..#..||
......|#...#|.#|#..|.#.#|....#|...|.#.....#..|#.|.
.##.###||..#..||..|#.|...#.|.||..|.||.|##|#|...|..
.|...||....##||...##....|..#|....#|#.|..#..|.#.##.
.#...||#.....#|...#...#||#...###..|.#.||..#...||..
......|.|.|.#..#..#.|..|.#.#||..|||#.||..|.###..|.
......#|...|..#||..|##.#....#.#...|.....|.|.|.###.
......|.....##....|...|.##|.|..#..........|....#..
.|.|...|.||...#.....|.#...#.##....#..#..........|.
..|..|##.#...|.....#|.|.|..|....|.......|||.......
..#|..|...#..||.......|.|...#..........##.#..|#.|.
||#|.##...##.#.#||....#.||.#....#|....|...#.|.#|..
|.|.#.|.#..#|.#..|.#.||....#..|.|.|#..|||.|.|.#..#
..#..|#|.##....||#|.....|#..|...|##..|#....|##.#..
|...||...##.#..#.|....#..##|.|.#||#.#.|.#..|.#....
.#..||.|...#...#.#.#..#.||..#...#|.##......||||...
...#...|#|.....#|....#...##......##.#...|..#..#..|
.|..|||..|#.|....|#.##..||..#|....|....##.|.|.....
........#...|#......|##|##....##.#|#.|#|........|#
.|....##.|.||#..####..#.##|#....|...|#..##.......#
..|#.|.|..|...|.||.|#..|......#..#...|.#.#..###||.
#.##||.#||.|.|#|.....#|.#|...#|.#|#||#||.|#...#|.|
|..|..#...|##.|...........#.|##.|..##..||.#|..|||#
..|.|..|.|..|..||..##.|.#...#|..|.|..|#.##.#||....
.....#..|.|..|||.###...|#|#..|..#....#......##.|..
|...##.|.#.............|.#.#.....#|#..#...#...|.|.
|##|.|...|.|.....#|....##....|.#....|...##..#...|.
|.....##..#.#.|.|..|##.#|#.....||.#...|.#..#|.#|..
#.#..#......#........|.|..#|...###...|...#..#..#..
.||..#.....##.##..||.|...#.|..||.......|#|.|.#|..|
||##.....##...#...#|.#|....|||.|.#..#.#...|....|..
|....||...|.||......#...#.#..|.#..|.|##.|#........
..#.|.|...|.|..|#...#.|.|..|........#.|.#...|.|...
.#|.|.|.|#..##|.|..|.||.|.#.##..|...|.|....|...|..
|#..|....#...#|##.....|...|..#|..##.||#.#|...|#.||
|..#||...#.|......|..|#.##.#.....##.#|#...###||...
|.|.#..#|...|#.|...|...#.|.......##....#|...|.|.|#
...|...#..##|..|.....#.|...|.#..|.|#..|...........
...##|..#...#|...#...#..###..||....##.#..###.#|.|.
.#.|.....#...#..#|||#..|#||.#||.#..|.|.#|..#.#||..
#..|..#..||#.|##...|..#|.||#|####.#.|...|..|#|#...
|#..#..#..#..#.......|.#.|..||..|........|...#.|..
.|#.#....|..##..|..|||.|.#.||....|#|....|#...#|.#.
#..|.....||#....##.|...#.#|.......|..|||||..#..#|#
#|.#|..#....#|#...||..|......||.#|.|.|...||..#..#.
#|..|##..##|#...||..||#.#.|.|...#...#..||.|..#.|..
|#.....#..#.#.#...||.....|...||.#.#.|....|.||.#.#.
##.##|..||...|###|.||.||..#||..#.||.#|.||..|...|..


*/