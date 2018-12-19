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
y=1195, x=558..562
x=522, y=879..891
x=466, y=1293..1304
y=558, x=550..556
x=543, y=1128..1141
x=574, y=1496..1500
x=533, y=1053..1064
x=434, y=1130..1144
y=1723, x=487..491
y=1000, x=512..532
y=111, x=505..524
y=997, x=520..525
x=484, y=76..87
y=325, x=415..427
x=463, y=1236..1248
x=508, y=364..377
x=490, y=1748..1759
x=477, y=876..890
y=781, x=453..464
x=557, y=941..944
y=24, x=519..531
x=432, y=930..941
x=571, y=175..185
x=462, y=668..694
x=570, y=1740..1756
x=516, y=158..172
y=1699, x=541..543
x=542, y=598..608
x=448, y=868..873
x=516, y=1527..1530
x=478, y=1785..1797
x=520, y=1567..1579
y=1548, x=418..425
x=414, y=1913..1925
x=561, y=249..251
x=557, y=1784..1794
y=1044, x=511..531
x=492, y=58..72
x=517, y=200..210
x=503, y=628..635
x=506, y=1873..1882
x=496, y=332..334
x=442, y=568..589
x=418, y=1641..1664
y=654, x=422..426
y=253, x=443..445
y=976, x=521..548
x=422, y=1418..1433
x=455, y=375..378
x=522, y=519..528
x=427, y=976..984
y=1285, x=515..540
x=471, y=1916..1923
x=503, y=1372..1382
x=482, y=1501..1507
x=439, y=192..212
y=1448, x=467..472
x=540, y=1840..1868
y=1405, x=493..495
x=558, y=768..782
x=495, y=1402..1405
x=485, y=120..127
y=476, x=450..454
y=1759, x=490..509
x=527, y=128..144
x=433, y=330..333
x=504, y=777..779
x=528, y=1659..1667
y=1601, x=427..436
x=508, y=328..337
y=955, x=463..534
x=468, y=1135..1151
x=450, y=62..65
x=462, y=1334..1347
x=496, y=201..210
y=1523, x=424..430
y=1445, x=497..499
y=826, x=517..519
x=422, y=269..287
x=491, y=1721..1723
x=486, y=280..291
y=125, x=553..557
x=477, y=1120..1121
x=518, y=273..289
x=531, y=410..422
x=480, y=816..827
x=551, y=489..509
x=501, y=724..733
x=453, y=1391..1405
x=418, y=1015..1018
x=487, y=1128..1130
x=458, y=602..607
x=513, y=777..785
y=1512, x=437..440
x=543, y=1710..1713
x=524, y=98..111
x=491, y=1461..1473
x=506, y=180..191
x=528, y=1900..1907
x=426, y=824..828
y=86, x=525..530
y=1604, x=537..542
x=475, y=922..933
x=468, y=1050..1054
x=512, y=1374..1387
x=429, y=1919..1922
y=328, x=444..471
x=445, y=221..233
y=859, x=420..424
x=562, y=1543..1548
x=471, y=896..899
y=1429, x=502..519
x=418, y=959..968
x=510, y=770..782
x=498, y=724..733
x=554, y=1705..1717
x=522, y=1426..1443
y=1220, x=443..466
y=37, x=549..556
y=636, x=525..540
x=436, y=1150..1169
x=493, y=712..718
x=559, y=266..276
x=566, y=1845..1853
y=525, x=477..492
x=436, y=1857..1867
y=1064, x=517..533
x=478, y=160..172
x=458, y=360..361
x=467, y=1543..1545
x=567, y=1142..1147
x=528, y=410..422
y=1029, x=537..556
y=1819, x=466..486
x=525, y=578..585
y=1433, x=422..441
y=459, x=551..560
y=1672, x=552..562
x=502, y=1483..1494
y=733, x=498..501
y=1359, x=538..544
x=553, y=730..751
y=289, x=518..539
y=224, x=428..430
x=510, y=821..840
y=41, x=464..482
y=508, x=505..522
y=274, x=498..511
x=518, y=346..358
y=1119, x=552..555
x=561, y=174..185
x=492, y=1193..1202
y=103, x=511..513
y=1672, x=508..523
x=523, y=1649..1672
y=887, x=420..432
x=566, y=235..251
x=482, y=903..918
x=512, y=988..1000
x=559, y=1853..1878
x=513, y=726..736
y=1036, x=463..471
x=490, y=1011..1033
x=516, y=754..756
x=463, y=941..955
y=1759, x=419..446
x=446, y=832..852
y=1622, x=505..528
y=1859, x=443..535
y=430, x=544..563
x=498, y=578..596
y=287, x=497..499
x=545, y=1113..1123
x=484, y=964..969
x=435, y=1912..1925
x=525, y=1039..1041
y=547, x=514..529
x=497, y=56..68
x=517, y=1764..1774
y=162, x=558..561
x=552, y=1645..1672
x=448, y=1373..1380
y=1705, x=517..522
x=443, y=84..98
x=504, y=1458..1466
x=439, y=892..902
y=1365, x=449..495
x=538, y=453..467
x=555, y=1769..1778
x=467, y=180..191
y=1304, x=466..472
x=546, y=1011..1017
x=541, y=749..751
x=457, y=1049..1054
y=1554, x=513..525
x=527, y=1531..1539
y=1657, x=493..499
y=1375, x=482..488
x=569, y=145..166
x=531, y=1544..1557
x=472, y=813..821
x=541, y=412..425
y=608, x=540..542
y=283, x=530..533
x=511, y=900..911
y=1809, x=496..515
y=1474, x=555..566
x=502, y=1416..1429
x=527, y=900..914
y=791, x=526..538
x=551, y=1396..1406
y=1580, x=547..553
x=562, y=1195..1199
y=1447, x=541..543
x=481, y=1172..1186
y=1562, x=547..568
y=589, x=442..468
x=513, y=877..886
y=334, x=496..500
y=691, x=416..431
x=443, y=192..212
y=1387, x=495..512
y=444, x=470..496
x=507, y=1731..1741
x=442, y=752..765
x=474, y=1342..1344
x=470, y=436..444
y=309, x=469..476
x=487, y=1721..1723
x=558, y=155..162
y=1138, x=524..530
x=501, y=1729..1738
x=469, y=488..511
x=567, y=1309..1333
x=501, y=904..907
y=656, x=479..495
y=1205, x=487..506
x=476, y=259..271
x=499, y=482..487
x=492, y=883..886
x=433, y=443..454
y=1186, x=481..504
x=459, y=964..969
y=1265, x=453..456
y=694, x=530..533
y=42, x=497..500
y=1599, x=496..515
y=1699, x=438..440
y=1509, x=528..530
y=1844, x=432..453
y=1840, x=458..475
y=821, x=468..472
x=441, y=1867..1878
x=492, y=238..251
y=944, x=542..557
x=547, y=1535..1562
y=1778, x=535..555
x=542, y=941..944
y=1868, x=538..540
y=45, x=492..510
y=698, x=481..491
x=540, y=1566..1579
y=698, x=515..540
y=1774, x=445..489
x=433, y=1612..1636
y=333, x=418..433
x=538, y=582..586
x=488, y=1372..1375
x=445, y=1095..1097
x=447, y=415..439
y=841, x=546..562
y=74, x=539..559
y=210, x=453..478
x=466, y=1809..1819
y=1199, x=558..562
x=512, y=1527..1530
x=482, y=305..316
y=1183, x=560..563
y=914, x=527..536
y=1465, x=454..462
x=438, y=1539..1565
y=172, x=478..496
y=635, x=475..503
x=443, y=1047..1073
x=498, y=796..802
x=476, y=302..309
x=519, y=12..24
y=1097, x=440..445
y=439, x=447..466
x=557, y=1212..1216
y=422, x=528..531
y=835, x=551..554
x=439, y=1805..1813
x=515, y=1271..1285
x=496, y=1585..1599
x=515, y=580..591
x=457, y=1207..1208
y=873, x=448..471
x=530, y=457..460
x=487, y=1649..1662
x=436, y=870..889
x=460, y=1392..1409
x=499, y=904..907
y=1186, x=537..556
x=533, y=283..286
y=1548, x=556..562
x=558, y=1505..1517
y=233, x=488..507
y=511, x=436..438
x=522, y=1705..1713
x=461, y=221..233
y=514, x=425..444
y=639, x=445..448
x=478, y=199..210
x=533, y=1426..1450
x=486, y=518..521
x=571, y=1420..1427
y=890, x=477..500
y=1494, x=502..524
y=1918, x=541..564
x=525, y=554..566
x=510, y=1460..1473
y=1165, x=477..492
x=534, y=476..487
y=468, x=499..514
y=291, x=486..505
y=1111, x=461..476
x=502, y=1458..1466
x=542, y=1524..1527
x=570, y=1844..1853
y=1197, x=515..517
x=452, y=244..256
x=487, y=1395..1408
y=1726, x=476..498
x=462, y=791..795
x=483, y=738..760
x=505, y=577..596
x=519, y=1415..1429
x=511, y=754..756
y=127, x=478..485
x=500, y=1672..1689
x=517, y=1302..1307
x=491, y=687..698
x=463, y=1616..1623
x=458, y=838..840
x=505, y=281..291
y=1288, x=463..480
x=482, y=37..41
y=308, x=506..511
x=538, y=780..791
x=564, y=1912..1918
y=759, x=503..523
y=1358, x=454..471
y=1548, x=448..473
x=535, y=1769..1778
x=513, y=499..504
y=210, x=496..517
y=1343, x=504..528
y=760, x=483..487
x=444, y=492..514
y=742, x=546..548
x=505, y=77..87
y=97, x=464..484
y=1893, x=545..559
y=1794, x=537..557
x=437, y=1499..1512
x=569, y=1902..1922
x=420, y=832..859
x=423, y=46..49
y=1473, x=491..510
x=501, y=124..127
x=523, y=778..785
y=487, x=499..508
x=460, y=1866..1878
x=472, y=1371..1377
y=782, x=558..569
y=1058, x=525..527
x=548, y=1625..1632
x=518, y=554..566
x=455, y=1678..1686
y=1003, x=482..509
x=563, y=614..628
x=561, y=155..162
x=523, y=1259..1260
x=553, y=111..125
x=537, y=1003..1029
y=1000, x=560..565
y=1681, x=474..492
y=127, x=501..503
x=515, y=688..698
y=67, x=544..552
y=98, x=443..456
x=438, y=1715..1727
y=1408, x=487..502
y=345, x=524..545
x=445, y=976..984
x=550, y=1365..1374
x=523, y=1785..1797
y=1557, x=497..531
x=467, y=834..847
y=830, x=551..554
x=568, y=1534..1562
y=918, x=555..557
x=495, y=1375..1387
x=450, y=459..476
x=478, y=1295..1307
y=736, x=493..513
y=596, x=498..505
x=553, y=792..798
y=840, x=458..464
x=453, y=1825..1844
y=847, x=467..489
x=573, y=615..628
x=532, y=516..525
x=562, y=266..276
x=420, y=870..887
x=527, y=1169..1171
y=657, x=455..460
x=436, y=210..227
x=454, y=1465..1475
x=511, y=1413..1426
x=561, y=729..751
y=425, x=521..541
x=518, y=706..715
x=526, y=1658..1667
y=506, x=436..438
x=547, y=1570..1580
y=1550, x=513..525
x=497, y=347..358
x=470, y=1608..1632
x=521, y=413..425
x=532, y=767..768
x=490, y=122..131
y=1454, x=557..565
y=1123, x=545..562
y=597, x=480..493
x=533, y=692..694
x=528, y=1610..1622
y=965, x=436..450
x=441, y=428..434
x=557, y=1397..1406
x=472, y=1293..1304
x=464, y=917..927
x=421, y=1260..1282
y=1896, x=444..446
x=553, y=583..586
x=517, y=1054..1064
x=502, y=1302..1307
y=751, x=553..561
x=431, y=541..557
x=520, y=995..997
x=507, y=527..529
x=414, y=542..557
x=505, y=1611..1622
x=553, y=1569..1580
x=452, y=1028..1037
y=1721, x=487..491
x=522, y=497..508
y=212, x=439..443
x=504, y=1323..1343
y=1507, x=456..482
x=479, y=651..656
x=472, y=1455..1479
y=1432, x=541..543
x=538, y=1341..1359
x=519, y=55..68
x=516, y=877..886
x=494, y=883..886
y=294, x=453..480
y=1002, x=428..442
x=499, y=285..287
x=550, y=536..558
x=446, y=1752..1759
x=508, y=9..22
x=573, y=494..504
y=1120, x=495..499
x=440, y=164..186
y=1017, x=543..546
y=941, x=432..446
y=607, x=445..458
x=473, y=1212..1225
y=1923, x=459..471
y=1282, x=421..434
y=755, x=460..477
y=1713, x=543..548
x=414, y=30..54
x=468, y=359..361
x=454, y=338..345
x=453, y=1864..1873
y=1022, x=503..523
y=377, x=508..532
x=497, y=1746..1754
x=449, y=667..694
x=445, y=603..607
x=466, y=1218..1220
x=492, y=33..45
y=908, x=518..522
x=549, y=27..37
x=528, y=1426..1443
x=560, y=1095..1105
y=1922, x=569..573
x=530, y=1507..1509
x=440, y=1095..1097
x=444, y=1488..1494
x=530, y=283..286
x=447, y=1258..1269
x=540, y=1624..1632
x=576, y=1150..1163
x=539, y=1036..1062
y=1741, x=489..507
x=509, y=977..1003
x=448, y=1489..1494
y=1756, x=548..570
x=506, y=792..805
y=1667, x=526..528
x=505, y=368..382
x=514, y=535..547
y=1577, x=472..480
x=471, y=559..565
x=555, y=859..880
y=969, x=459..484
y=880, x=545..555
x=492, y=1138..1165
x=529, y=1297..1302
x=454, y=1082..1100
x=474, y=1276..1285
x=448, y=1864..1873
x=549, y=671..674
x=436, y=892..902
y=1686, x=455..460
x=535, y=1848..1859
x=556, y=1003..1029
y=153, x=424..438
y=345, x=454..466
x=437, y=772..783
y=1568, x=457..471
x=499, y=464..468
x=421, y=1641..1664
x=470, y=1276..1285
y=995, x=520..525
x=508, y=1439..1449
x=517, y=1906..1917
x=467, y=108..115
x=571, y=1710..1725
x=483, y=1257..1276
y=660, x=510..531
x=509, y=434..446
x=563, y=422..430
x=419, y=1753..1759
y=1333, x=544..567
x=539, y=62..74
x=508, y=1413..1426
x=492, y=515..525
x=488, y=222..233
x=487, y=1195..1205
x=526, y=210..232
x=524, y=1484..1494
x=522, y=902..908
x=557, y=1710..1725
x=439, y=16..19
x=466, y=790..795
x=540, y=598..608
y=256, x=437..452
y=1867, x=415..436
x=516, y=1502..1512
x=474, y=327..337
y=1171, x=527..531
x=569, y=768..782
y=1713, x=517..522
x=515, y=1806..1809
y=531, x=549..573
x=471, y=1011..1036
y=1307, x=461..478
y=868, x=483..504
y=565, x=462..471
x=562, y=1114..1123
x=465, y=1543..1545
x=432, y=1826..1844
x=437, y=92..101
x=453, y=1177..1203
x=545, y=1852..1878
x=421, y=170..192
x=465, y=745..749
x=462, y=1206..1208
x=499, y=159..172
y=1616, x=457..463
x=465, y=16..19
y=1450, x=533..549
x=539, y=34..40
x=500, y=332..334
x=499, y=1647..1657
x=428, y=214..224
x=482, y=518..521
x=461, y=1296..1307
y=1806, x=454..459
x=520, y=858..870
x=489, y=1732..1741
x=505, y=1650..1662
y=1689, x=500..503
x=490, y=1906..1917
x=544, y=67..70
y=20, x=554..570
y=1322, x=482..488
y=715, x=518..522
x=443, y=1784..1801
x=520, y=299..311
x=457, y=1555..1568
x=568, y=1192..1205
y=671, x=541..549
x=436, y=954..965
x=426, y=1244..1253
y=756, x=511..516
x=530, y=1136..1138
y=311, x=499..520
x=476, y=1093..1111
x=462, y=59..63
x=525, y=129..144
x=487, y=1691..1705
y=454, x=430..433
x=528, y=435..446
y=1285, x=470..474
y=1307, x=502..517
x=470, y=475..481
x=436, y=506..511
x=565, y=1442..1454
x=463, y=1257..1269
x=529, y=536..547
x=534, y=942..955
y=1141, x=508..543
x=509, y=1588..1595
x=465, y=132..145
y=1491, x=569..571
x=493, y=1402..1405
y=233, x=445..461
y=1676, x=480..485
x=491, y=8..22
x=486, y=57..72
x=523, y=1523..1535
y=1347, x=462..485
x=438, y=506..511
x=479, y=259..271
x=530, y=44..50
y=511, x=453..469
x=436, y=29..54
y=1738, x=499..501
y=1662, x=487..505
x=566, y=1471..1474
y=779, x=500..504
y=428, x=474..478
x=525, y=622..636
x=521, y=393..405
x=453, y=1370..1377
y=1405, x=434..453
x=419, y=1070..1091
x=459, y=163..186
y=101, x=418..437
x=453, y=199..210
x=453, y=763..781
x=543, y=1432..1447
x=563, y=1181..1183
y=902, x=436..439
y=1527, x=542..549
x=489, y=816..824
x=572, y=1366..1374
x=513, y=1765..1774
x=445, y=247..253
x=453, y=284..294
x=523, y=1010..1022
y=1195, x=442..444
x=499, y=1443..1445
x=492, y=1668..1681
x=499, y=922..933
x=532, y=1698..1720
y=824, x=489..507
x=554, y=766..768
x=541, y=518..528
x=473, y=521..549
y=586, x=538..553
y=177, x=523..530
y=1342, x=468..474
x=550, y=144..166
x=528, y=1322..1343
x=436, y=1177..1203
x=503, y=1671..1689
y=405, x=421..521
y=1374, x=550..572
y=1147, x=567..571
x=503, y=1627..1640
x=529, y=308..320
y=185, x=561..571
y=1360, x=454..471
y=1091, x=415..419
x=419, y=1613..1636
x=439, y=1738..1742
y=611, x=533..552
x=451, y=521..549
y=1797, x=478..523
y=19, x=439..465
x=424, y=833..859
y=751, x=537..541
y=518, x=482..486
x=444, y=1892..1896
x=537, y=1785..1794
x=520, y=1039..1041
x=505, y=97..111
x=503, y=748..759
x=427, y=1919..1922
x=435, y=584..606
x=482, y=978..1003
y=781, x=467..490
y=1380, x=436..448
x=415, y=1858..1867
x=504, y=880..891
x=444, y=1191..1195
y=1377, x=453..472
x=415, y=298..325
x=495, y=1356..1365
x=549, y=519..531
y=886, x=492..494
x=475, y=140..150
x=475, y=1826..1840
x=535, y=1258..1260
x=500, y=31..42
x=507, y=816..824
y=1248, x=463..528
x=508, y=1649..1672
y=1121, x=460..477
x=446, y=930..941
x=468, y=569..589
x=555, y=665..677
x=415, y=1071..1091
x=515, y=1172..1197
x=538, y=1840..1868
x=516, y=1212..1225
x=508, y=483..487
y=1588, x=507..509
y=72, x=486..492
x=530, y=626..631
x=445, y=1764..1774
x=551, y=830..835
x=514, y=463..468
x=547, y=1072..1084
x=431, y=62..65
x=443, y=916..927
x=439, y=705..720
y=481, x=468..470
y=251, x=470..492
x=454, y=1780..1806
x=417, y=269..287
y=1105, x=534..560
y=1059, x=471..495
x=523, y=156..177
y=1878, x=441..460
x=434, y=1390..1405
x=451, y=107..115
x=525, y=1051..1058
x=468, y=476..481
x=547, y=318..320
y=509, x=544..551
y=907, x=499..501
y=1406, x=551..557
y=487, x=513..534
x=530, y=692..694
y=361, x=458..468
y=783, x=433..437
x=427, y=46..49
x=500, y=528..529
x=490, y=900..911
y=677, x=528..555
y=1545, x=465..467
x=554, y=830..835
y=1539, x=527..537
x=499, y=1095..1120
x=425, y=1529..1548
y=1530, x=512..516
x=531, y=1170..1171
x=566, y=1495..1500
y=1565, x=433..438
x=450, y=953..965
x=555, y=1111..1119
x=496, y=160..172
y=358, x=497..518
y=49, x=423..427
x=573, y=1903..1922
y=1362, x=415..434
x=533, y=600..611
x=468, y=1342..1344
y=186, x=440..459
y=1813, x=416..439
y=1054, x=457..468
y=1187, x=427..430
x=541, y=671..674
x=532, y=363..377
y=529, x=500..507
x=513, y=557..570
x=530, y=77..86
x=537, y=1585..1604
x=426, y=429..434
y=927, x=443..464
x=436, y=1373..1380
x=453, y=488..511
y=889, x=436..442
x=424, y=147..153
y=1253, x=426..435
x=463, y=1012..1036
x=515, y=499..504
x=480, y=1011..1033
x=422, y=753..765
x=461, y=815..827
x=562, y=1645..1672
x=430, y=444..454
y=275, x=468..485
x=441, y=1027..1037
x=556, y=1543..1548
y=87, x=484..505
x=509, y=1699..1720
y=1925, x=414..435
y=1579, x=520..540
x=459, y=1917..1923
x=543, y=1011..1017
x=496, y=1806..1809
y=718, x=513..532
x=433, y=1540..1565
y=232, x=524..526
x=432, y=869..887
y=1033, x=480..490
x=507, y=221..233
x=437, y=245..256
y=309, x=443..456
y=1276, x=483..505
y=840, x=510..525
x=471, y=868..873
y=378, x=437..455
y=803, x=432..449
x=534, y=626..631
x=428, y=994..1002
x=505, y=496..508
x=498, y=252..274
y=544, x=519..523
x=511, y=1032..1044
x=486, y=1053..1056
x=541, y=1432..1447
y=144, x=525..527
x=552, y=67..70
x=552, y=601..611
y=504, x=563..573
x=456, y=83..98
y=566, x=518..525
x=556, y=27..37
x=553, y=1098..1100
x=565, y=983..1000
x=570, y=14..20
x=442, y=871..889
x=461, y=1092..1111
x=424, y=1496..1523
x=485, y=261..275
x=417, y=704..720
x=526, y=780..791
y=1475, x=454..462
x=464, y=837..840
x=507, y=156..167
y=227, x=421..436
x=440, y=1499..1512
y=1494, x=444..448
x=480, y=1573..1577
x=483, y=855..868
y=1727, x=425..438
x=525, y=1296..1302
y=172, x=499..516
x=509, y=1684..1687
x=538, y=249..251
x=485, y=1665..1676
x=532, y=457..460
y=1062, x=539..545
y=1902, x=426..452
x=432, y=799..803
x=482, y=1053..1056
x=528, y=516..525
x=539, y=274..289
y=1084, x=523..547
y=131, x=490..515
x=436, y=1590..1601
x=497, y=31..42
x=557, y=110..125
y=382, x=429..505
x=510, y=646..660
y=567, x=483..507
x=487, y=739..760
x=431, y=668..691
y=585, x=520..525
x=501, y=1372..1382
x=437, y=375..378
y=1382, x=501..503
x=456, y=1501..1507
y=316, x=464..482
y=525, x=528..532
x=555, y=1472..1474
x=460, y=1119..1121
x=474, y=425..428
x=537, y=1530..1539
x=509, y=156..167
x=464, y=204..207
y=785, x=513..523
x=531, y=1033..1044
y=332, x=496..500
y=1535, x=507..523
x=448, y=1538..1548
y=918, x=476..482
x=520, y=372..374
x=443, y=1219..1220
x=511, y=251..274
x=469, y=302..309
x=425, y=1716..1727
x=549, y=1426..1450
x=488, y=1299..1322
x=529, y=857..870
x=458, y=1827..1840
x=427, y=1591..1601
x=510, y=34..45
x=533, y=581..591
x=495, y=1095..1120
x=430, y=1164..1187
x=473, y=1539..1548
x=477, y=739..755
y=1056, x=482..486
x=531, y=11..24
x=430, y=1046..1073
y=191, x=467..506
x=499, y=140..150
x=545, y=341..345
x=448, y=1609..1632
y=1907, x=526..528
x=422, y=646..654
y=467, x=522..538
x=536, y=900..914
x=456, y=303..309
x=550, y=1504..1517
y=1216, x=545..557
y=1882, x=506..524
x=440, y=1130..1144
x=511, y=95..103
y=749, x=465..470
x=549, y=1524..1527
x=454, y=459..476
y=207, x=464..470
x=524, y=340..345
y=65, x=431..450
x=550, y=1193..1205
x=480, y=1279..1288
x=454, y=1358..1360
x=442, y=995..1002
y=1202, x=492..497
x=509, y=1749..1759
x=504, y=856..868
y=1203, x=436..453
y=504, x=513..515
y=1705, x=460..487
x=519, y=542..544
x=468, y=813..821
x=480, y=284..294
y=557, x=414..431
x=544, y=423..430
x=525, y=372..374
y=570, x=513..531
x=503, y=1746..1754
x=421, y=210..227
y=933, x=475..499
y=1163, x=495..576
x=517, y=826..831
x=556, y=1169..1186
y=1512, x=516..536
x=522, y=706..715
y=1629, x=438..443
x=462, y=895..899
y=1919, x=427..429
y=1225, x=473..516
y=1725, x=557..571
x=460, y=632..657
x=574, y=236..251
x=559, y=1883..1893
x=551, y=437..459
x=466, y=414..439
x=489, y=835..847
x=447, y=1149..1169
y=1577, x=486..505
y=320, x=547..572
x=524, y=1874..1882
y=1449, x=487..508
x=482, y=1373..1375
x=476, y=1627..1640
x=513, y=708..718
y=1144, x=434..440
x=418, y=329..333
x=499, y=299..311
x=460, y=738..755
y=287, x=417..422
x=540, y=1272..1285
x=504, y=1129..1130
x=548, y=724..742
x=470, y=745..749
y=549, x=451..473
x=467, y=778..781
x=548, y=1739..1756
x=513, y=95..103
x=545, y=860..880
y=899, x=462..471
x=507, y=543..567
x=416, y=1805..1813
x=426, y=1890..1902
x=438, y=1610..1629
y=1302, x=525..529
y=720, x=417..439
y=831, x=517..519
y=521, x=482..486
y=251, x=566..574
x=534, y=1095..1105
x=496, y=796..802
y=968, x=418..426
x=466, y=712..718
x=500, y=777..779
x=560, y=982..1000
y=192, x=421..430
x=421, y=584..606
y=930, x=504..522
x=517, y=1705..1713
y=1720, x=509..532
x=449, y=800..803
y=320, x=526..529
x=525, y=820..840
x=497, y=285..287
x=541, y=1673..1699
x=544, y=1310..1333
x=527, y=1051..1058
x=498, y=1716..1726
x=462, y=1465..1475
x=515, y=121..131
x=420, y=1767..1775
x=459, y=1781..1806
y=827, x=461..480
x=486, y=1564..1577
y=1636, x=419..433
y=286, x=530..533
y=886, x=513..516
x=493, y=727..736
x=433, y=771..783
x=444, y=327..328
x=525, y=77..86
y=674, x=541..549
x=439, y=1766..1775
x=435, y=1245..1253
x=557, y=908..918
x=464, y=304..316
x=430, y=1496..1523
y=68, x=497..519
x=522, y=918..930
x=487, y=1438..1449
x=432, y=824..828
x=502, y=1396..1408
x=457, y=1616..1623
x=445, y=631..639
y=271, x=476..479
y=63, x=462..464
y=1466, x=502..504
y=1754, x=497..503
x=454, y=131..145
x=469, y=1391..1409
y=1130, x=487..504
x=518, y=43..50
y=1853, x=566..570
x=552, y=1111..1119
x=511, y=305..308
x=518, y=902..908
x=438, y=146..153
y=167, x=507..509
y=1247, x=442..459
y=446, x=509..528
y=1717, x=535..554
y=1479, x=448..472
x=485, y=1334..1347
y=1344, x=468..474
y=70, x=544..552
x=521, y=965..976
x=477, y=1139..1165
x=452, y=1889..1902
y=765, x=422..442
x=505, y=1564..1577
x=443, y=247..253
y=1664, x=418..421
y=1427, x=571..573
y=1426, x=508..511
y=1443, x=522..528
x=571, y=1475..1491
x=416, y=667..691
x=493, y=771..782
x=545, y=1098..1100
x=573, y=1419..1427
x=426, y=645..654
x=499, y=1729..1738
x=480, y=793..805
x=427, y=1164..1187
x=546, y=821..841
x=558, y=1195..1199
x=540, y=622..636
x=484, y=93..97
x=505, y=1258..1276
x=528, y=1507..1509
x=544, y=1340..1359
x=427, y=1082..1100
x=433, y=833..852
y=1260, x=523..535
x=548, y=964..976
x=543, y=1672..1699
y=802, x=496..498
y=1623, x=457..463
y=499, x=513..515
x=523, y=1072..1084
x=554, y=14..20
x=506, y=305..308
y=1409, x=460..469
x=425, y=493..514
x=519, y=826..831
x=560, y=437..459
y=1640, x=476..503
x=540, y=687..698
x=525, y=995..997
y=606, x=421..435
x=528, y=1235..1248
x=545, y=1213..1216
x=430, y=169..192
x=473, y=1134..1151
x=508, y=1129..1141
x=471, y=1048..1059
x=464, y=59..63
x=518, y=33..40
x=470, y=204..207
y=528, x=522..541
y=1041, x=520..525
x=429, y=369..382
x=480, y=1665..1676
x=532, y=709..718
y=870, x=520..529
y=1100, x=427..454
y=1018, x=418..427
x=557, y=1441..1454
x=449, y=1317..1327
x=443, y=1849..1859
x=474, y=1667..1681
x=528, y=666..677
y=1269, x=447..463
x=545, y=1035..1062
x=442, y=1225..1247
x=496, y=437..444
x=456, y=1255..1265
x=462, y=558..565
x=563, y=494..504
x=515, y=1585..1599
x=468, y=262..275
x=548, y=1710..1713
x=440, y=1672..1699
x=476, y=904..918
x=471, y=326..328
x=495, y=1151..1163
x=500, y=877..890
x=493, y=576..597
x=520, y=578..585
x=495, y=1048..1059
y=828, x=426..432
x=464, y=92..97
x=443, y=1611..1629
x=525, y=1550..1554
x=478, y=426..428
x=464, y=763..781
x=490, y=779..781
y=1774, x=513..517
x=470, y=1318..1327
x=542, y=1584..1604
x=448, y=631..639
x=544, y=490..509
y=434, x=426..441
y=54, x=414..436
x=485, y=107..113
x=560, y=1181..1183
x=530, y=155..177
x=522, y=454..467
y=1100, x=545..553
x=478, y=120..127
x=463, y=1278..1288
x=530, y=1685..1687
x=434, y=1338..1362
y=22, x=491..508
y=1037, x=441..452
x=507, y=1523..1535
x=572, y=319..320
x=555, y=907..918
x=453, y=1255..1265
x=421, y=392..405
x=418, y=1528..1548
x=517, y=1173..1197
x=443, y=304..309
x=448, y=1455..1479
y=374, x=520..525
y=1801, x=443..449
y=1169, x=436..447
x=497, y=1544..1557
x=418, y=93..101
x=537, y=1168..1186
x=472, y=1573..1577
x=438, y=1672..1699
y=40, x=518..539
x=526, y=1899..1907
x=513, y=477..487
x=523, y=542..544
y=1595, x=507..509
x=573, y=520..531
y=1775, x=420..439
x=460, y=1691..1705
y=150, x=475..499
y=1917, x=490..517
x=523, y=747..759
y=1151, x=468..473
y=1443, x=497..499
y=1205, x=550..568
y=631, x=530..534
x=536, y=1501..1512
y=745, x=465..470
y=1500, x=566..574
x=537, y=749..751
y=852, x=433..446
y=1873, x=448..453
x=457, y=1737..1742
y=591, x=515..533
x=545, y=1882..1893
x=464, y=38..41
x=513, y=1550..1554
x=470, y=238..251
y=1208, x=457..462
x=427, y=299..325
y=145, x=454..465
x=455, y=632..657
y=1517, x=550..558
x=535, y=1705..1717
x=476, y=1717..1726
y=694, x=449..462
y=984, x=427..445
y=1687, x=509..530
x=449, y=1355..1365
x=504, y=918..930
x=482, y=1299..1322
x=449, y=1785..1801
y=798, x=553..563
y=628, x=563..573
x=481, y=686..698
x=541, y=1912..1918
x=486, y=1808..1819
y=782, x=493..510
x=495, y=652..656
x=475, y=629..635
x=503, y=1009..1022
y=50, x=518..530
x=556, y=537..558
y=795, x=462..466
y=166, x=550..569
x=526, y=307..320
x=477, y=106..113
x=504, y=1172..1186
x=466, y=339..345
x=497, y=1443..1445
x=415, y=1338..1362
x=532, y=987..1000
y=911, x=490..511
x=472, y=1434..1448
x=506, y=1196..1205
y=1098, x=545..553
y=1632, x=448..470
y=251, x=538..561
y=891, x=504..522
x=477, y=515..525
x=483, y=543..567
x=446, y=1892..1896
x=459, y=1224..1247
x=471, y=1358..1360
y=1191, x=442..444
x=480, y=576..597
x=562, y=822..841
x=571, y=1143..1147
x=569, y=1475..1491
y=805, x=480..506
y=1073, x=430..443
x=441, y=1418..1433
y=113, x=477..485
y=768, x=532..554
y=276, x=559..562
x=430, y=214..224
y=1053, x=482..486
x=531, y=647..660
y=1878, x=545..559
x=442, y=1191..1195
x=559, y=62..74
y=718, x=466..493
x=503, y=124..127
y=460, x=530..532
y=337, x=474..508
x=434, y=1260..1282
y=1632, x=540..548
x=524, y=209..232
x=524, y=1136..1138
x=497, y=1193..1202
x=467, y=1434..1448
x=507, y=1588..1595
x=460, y=1679..1686
y=1327, x=449..470
x=489, y=1765..1774
x=493, y=1647..1657
x=427, y=1015..1018
y=115, x=451..467
y=1742, x=439..457
y=1922, x=427..429
x=563, y=793..798
x=546, y=724..742
x=531, y=556..570
x=471, y=1555..1568
x=426, y=960..968


*/