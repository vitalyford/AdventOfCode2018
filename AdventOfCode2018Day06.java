import java.util.*;

class Point {
    int x;
    int y;
    
    Point() {}
    
    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class AdventOfCode2018Day06 {
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        ArrayList<Point> list = new ArrayList<>();
        
        // read the data into the list of Points
        while (true) {
            String line = s.nextLine();
            if (line.equals("")) break;
            
            String[] splitted = line.split(", ");
            list.add(new Point(Integer.parseInt(splitted[0]), Integer.parseInt(splitted[1])));
        }
        
        // find the rectangle within which all points are located
        int topX = list.get(0).x, topY = list.get(0).y, bottomX = list.get(0).x, bottomY = list.get(0).y;
        for (Point p : list) {
            if (p.x < topX) topX = p.x;
            if (p.x > bottomX) bottomX = p.x;
            if (p.y > topY) topY = p.y;
            if (p.y < bottomY) bottomY = p.y;
        }
        
        // check all points within the rectangle for how far away they are from every point
        Point[][] matrix = new Point[topY - bottomY + 1][bottomX - topX + 1];
        for (int row = bottomY; row <= topY; row++) {
            for (int col = topX; col <= bottomX; col++) {
                // we got the point, let's see how far away it is from every other point
                Point minPoint = null;
                int minDist = Integer.MAX_VALUE;
                boolean similarDist = false;
                for (Point p : list) {
                    if (p.x == col && p.y == row) { minPoint = p; break; }
                    int dist = Math.abs(row - p.y) + Math.abs(col - p.x);
                    if (minDist > dist) {
                        minDist = dist;
                        minPoint = p;
                        similarDist = false;
                    }
                    else if (minDist == dist) {
                        similarDist = true;
                    }
                }
                // add the Point to the matrix only if it is unique in terms of the distance
                matrix[row - bottomY][col - topX] = similarDist ? null : minPoint;
            }
        }
        
        // find all points that are not on the edge of the matrix
        ArrayList<Point> cases = new ArrayList<>();
        for (Point p : list) {
            // check top
            boolean found = false;
            for (int i = topX; i <= bottomX; i++) if (matrix[0][i - topX] == p) { found = true; break; } 
            if (found) continue;
            // check right
            for (int i = bottomY; i <= topY; i++) if (matrix[i - bottomY][bottomX - topX] == p) { found = true; break; }
            if (found) continue;
            // check bottom
            for (int i = topX; i <= bottomX; i++) if (matrix[topY - bottomY][i - topX] == p) { found = true; break; } 
            if (found) continue;
            // check left
            for (int i = bottomY; i <= topY; i++) if (matrix[i - bottomY][0] == p) { found = true; break; }
            if (found) continue;
            
            // at this point, the point is not on the edge so we add it to the cases array
            cases.add(p);
        }
        
        // check every case to see if it has the largest area
        HashMap<Point, Integer> hm = new HashMap<>();
        for (int row = bottomY; row <= topY; row++) {
            for (int col = topX; col <= bottomX; col++) {
                for (Point c : cases) {
                    if (matrix[row - bottomY][col - topX] == c) {
                        if (hm.containsKey(c)) hm.replace(c, hm.get(c) + 1);
                        else hm.put(c, 1);
                    }
                }
            }
        }
        
        // find the largest area
        Collection<Integer> areas = hm.values();
        System.out.println("Answer to the first part: " + Collections.max(areas));
        
        // here comes the second part
        matrix = new Point[topY - bottomY + 1][bottomX - topX + 1];
        int maxDistAllowed = 10000;
        int secondAnswer = 0;
        for (int row = bottomY; row <= topY; row++) {
            for (int col = topX; col <= bottomX; col++) {
                int sum = 0;
                for (Point p : list) sum += Math.abs(row - p.y) + Math.abs(col - p.x);
                if (sum < maxDistAllowed) secondAnswer++;
            }
        }
        
        System.out.println("Answer to the second part: " + secondAnswer);
        
        s.close();
    }
}

/* input
336, 308
262, 98
352, 115
225, 205
292, 185
166, 271
251, 67
266, 274
326, 85
191, 256
62, 171
333, 123
160, 131
211, 214
287, 333
231, 288
237, 183
211, 272
116, 153
336, 70
291, 117
156, 105
261, 119
216, 171
59, 343
50, 180
251, 268
169, 258
75, 136
305, 102
154, 327
187, 297
270, 225
190, 185
339, 264
103, 301
90, 92
164, 144
108, 140
189, 211
125, 157
77, 226
177, 168
46, 188
216, 244
346, 348
272, 90
140, 176
109, 324
128, 132

*/
