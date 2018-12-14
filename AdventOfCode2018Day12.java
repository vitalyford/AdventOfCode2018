import java.util.*;


public class AdventOfCode2018Day12 {
	
	private static long saveTheWorld(HashMap<String, String> notes, String initState, long zeroPosition, String end) {
		String prevInitState = "";
        long prevZeroPosition = 0;
        long deviation = 0;
        // iterate over 20 generations and at every generation, apply all notes
        long genNum = new Long(end), i = 0;
        for (i = 0; i < genNum; i++) {
        	// add the necessary additional dots in front and the back of the initState
        	int front = initState.indexOf('#');
        	if (front > 4) {
        		initState = initState.substring(front - 4);
        		zeroPosition -= front - 4;
        	}
        	front = initState.indexOf('#');
        	int back = initState.lastIndexOf('#');
        	String frontFiller = "";
        	for (int k = 0; k < 4 - front; k++)
        		frontFiller += ".";
        	String backFiller  = "";
        	for (int k = 0; k < 5 - (initState.length() - back); k++)
        		backFiller += ".";
        	initState = frontFiller + initState + backFiller;
        	zeroPosition += frontFiller.length();
        	
        	// as soon as we have a repetitive pattern,
        	// there is not reason to continue
        	// so we find the deviation for that pattern
        	// in order to change zeroPosition correctly
        	// and we quit from this loop
        	if (prevInitState.equals(initState)) {
        		deviation = zeroPosition - prevZeroPosition;
        		break;
        	}
        	
        	prevInitState = initState;
        	prevZeroPosition = zeroPosition;
        	
        	// start the process
        	String newInitState = initState;
        	Set<String> keys = notes.keySet();
        	//        index, plant
        	HashMap<Integer, String> createdByNotes = new HashMap<>();
        	for (String key : keys) {
        		int startIndex = 0;
        		int foundIndex = initState.indexOf(key, startIndex);
        		while (true) {
	        		if (foundIndex != -1) { // if we find a match for a note
	        			createdByNotes.put(foundIndex + 2, notes.get(key));
	        			newInitState = newInitState.substring(0, foundIndex) + ".." + notes.get(key) + ".." + newInitState.substring(foundIndex + 5);
	        			startIndex = foundIndex + 1;
	        			foundIndex = initState.indexOf(key, startIndex);
	        		}
	        		else {
	        			break;
	        		}
        		}
        	}
        	// make sure the all updates were made for the newInitState with all plants
        	for (Map.Entry<Integer, String> e : createdByNotes.entrySet()) {
        		if (newInitState.charAt(e.getKey()) != e.getValue().charAt(0))
        			newInitState = newInitState.substring(0, e.getKey()) + e.getValue() + newInitState.substring(e.getKey() + 1);
        	}
        	initState = newInitState;
        }
        // find the real zeroPosition and finish the previous loop
        zeroPosition += deviation * (genNum - i);
        
        // find the sum of the latest generation
        long sum = 0;
        for (int j = 0; j < initState.length(); j++) {
        	if (initState.charAt(j) == '#') sum += (long)j - zeroPosition;
        }
        return sum;
	}
    
    public static void main(String args[]) {
    	HashMap<String, String> notes = new HashMap<>();
        Scanner s = new Scanner(System.in);
        String initState = s.nextLine().replaceFirst("initial state: ", "");
    	long zeroPosition = initState.indexOf('#');
        String line = s.nextLine(); // read the empty line
        while (true) {
        	line = s.nextLine();
        	if (line.equals("")) break;
        	String[] note = line.split(" => ");
        	notes.put(note[0], note[1]);
        }
        s.close();
        System.out.println("Answer to the first part: " + saveTheWorld(notes, initState, zeroPosition, "20"));
        System.out.println("Answer to the second part: " + saveTheWorld(notes, initState, zeroPosition, "50000000000"));
    }
}

/*
initial state: #.#####.#.#.####.####.#.#...#.......##..##.#.#.#.###..#.....#.####..#.#######.#....####.#....##....#

##.## => .
#.#.. => .
..... => .
##..# => #
###.. => #
.##.# => .
..#.. => #
##.#. => #
.##.. => .
#..#. => .
###.# => #
.#### => #
.#.## => .
#.##. => #
.###. => #
##### => .
..##. => .
#.#.# => .
...#. => #
..### => .
.#.#. => #
.#... => #
##... => #
.#..# => #
#.### => #
#..## => #
....# => .
####. => .
#...# => #
#.... => .
...## => .
..#.# => #

*/