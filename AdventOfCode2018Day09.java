import java.util.*;

/**
 * Input for the first part:  411 players; last marble is worth 72059 points
 * Input for the second part: 411 players; last marble is worth 7205900 points
 * 
 * The algorithm is based on circular linked lists and using Long instead of Integer to store larger numbers
 */
class Marble {
	long value;
	Marble next;
	Marble prev;
	
	Marble() {
		value = 0;
		next = prev = this;
	}
	
	Marble(int value, Marble next, Marble prev) {
		this.value = value;
		this.next  = next;
		this.prev  = prev;
	}
}

public class AdventOfCode2018Day09 {
    
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        String[] line = s.nextLine().split(" ");
        int elves = Integer.parseInt(line[0]);
        int maxMarble = Integer.parseInt(line[6]);
        s.close();
        
        // create elves in a sorted tree map:
        // elf's index, elf's score
        TreeMap<Integer, Long> tm = new TreeMap<>();
        for (int i = 1; i <= elves; i++) tm.put(i, new Long(0));
        int currPlayer = 1; // the first player is 1
        
        // create the first marble with value 0       
        Marble curr = new Marble();
        
        // add more marbles in the linked list
        for (int m = 1; m <= maxMarble; m++) {
        	if (m % 23 == 0) { // the marble is a multiple of 23
        		tm.replace(currPlayer, tm.get(currPlayer) + m);
        		// remove the 7th marble counter-clockwise and add it to the score
        		for (int i = 0; i < 7; i++)
        			curr = curr.prev;
        		tm.replace(currPlayer, tm.get(currPlayer) + curr.value);
        		curr.prev.next = curr.next;
        		curr.next.prev = curr.prev;
        		curr = curr.next;
        	}
        	else {
	        	// find two marbles to put the current one in between
	        	Marble m1 = curr.next;
	        	Marble m2 = m1.next;
	        	m1.next   = new Marble(m, m2, m1);
	        	m2.prev   = m1.next;
	        	curr      = m1.next;
        	}
        	currPlayer++;
        	if (currPlayer == elves + 1) currPlayer = 1;
        }
        
        // find the highest score
        Set<Integer> players = tm.keySet();
        long highest = Integer.MIN_VALUE;
        for (Integer p : players) {
        	if (tm.get(p) > highest) highest = tm.get(p);
        }
        
        System.out.println("Answer: " + highest);
    }
}