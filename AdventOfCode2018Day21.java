import java.util.*;

/*
 * Reverse engineer the input line by line
 * Yes, it does take time but when it is all set and done, it flies
 */
public class AdventOfCode2018Day21 {

    public static void main(String args[]) throws Exception {  
    	System.out.println("Wait a little bit (~20s :-D)...");
        int val = 0;
        boolean foundFirst = false, part1done = false, part2done = false;
        HashMap<Integer, Boolean> visited = new HashMap<>();
        int lastVisited = -1;
        while (true) {
	        int a = val, b = 0, c = 0, d = 0, e = 0, f = 0;
	        b = 123;
	        boolean done = false;
	        int steps = 1;
	        while (!done) {
	            b &= 456;
	            b = (b == 72) ? 1 : 0;
	            steps += 3;
	            if (b == 0) {
	            	steps++;
	                continue;
	            }
	            steps++;
	            b = 0;
	            while (!done) {
	                e = b | 65536;
	                b = 3798839;
	                steps += 2;
	                while (!done) {
	                    f = e & 255;
	                    b += f;
	                    b &= 16777215;
	                    b *= 65899;
	                    steps += 4;
	                    if (b < 0 && !foundFirst) {
	                    	//if (val % 1000000 == 0) System.out.println("Overflow on steps " + steps + " and reg[0] = " + val);
	                    	done = true;
	                    	break;
	                    }
	                    b &= 16777215;
	                    f = (256 > e) ? 1 : 0;
	                    steps += 3;
	                    if (f == 1) { // jump to d = 27
	                    	if (b != 1797184) { // magic number that answers the first part
		                    	if (visited.containsKey(b) && part1done) {
			                    	part2done = true;
			                    	done = true;
			                    	System.out.println("Answer to the second part: " + lastVisited);
			                    	break;
			                    }
			                    lastVisited = b;
			                    if (part1done) visited.put(b, true);
	                    	}
	                        f = (b == a) ? 1 : 0;
	                        steps++;
	                        if (f == 1) {
	                            System.out.println("Haulted!");
	                            if (!foundFirst) part1done = true;
	                            if (foundFirst) part2done = true;
	                            done = true;
	                        }
	                        steps++;
	                        break;
	                    }
	                    steps++;
	                    f = 0;
	                    while (true) {
	                        c = f + 1;
	                        c *= 256;
	                        c = (c > e) ? 1 : 0;
	                        steps += 3;
	                        if (c == 0) {
	                        	steps += 3;
	                            f++;
	                        }
	                        else {
	                        	steps += 2;
	                        	break;
	                        }
	                    }
	                    steps += 2;
	                    e = f;
	                }
	            }
	        }
	        if (part1done && !foundFirst) {
	        	System.out.println("Answer to the first part: " + val);
	        	foundFirst = true;
	        }
	        if (part1done && part2done) break;
	        val++;
	        if (val < 0) {
	        	System.out.println("Darn it, did not work");
	        	break;
	        }
        }
    } 
}

/*
 * input has already been parsed into Java code
 * but in any case, here it is
 *
#ip 3
seti 123 0 1
bani 1 456 1
eqri 1 72 1
addr 1 3 3
seti 0 0 3
seti 0 7 1
bori 1 65536 4
seti 3798839 3 1
bani 4 255 5
addr 1 5 1
bani 1 16777215 1
muli 1 65899 1
bani 1 16777215 1
gtir 256 4 5
addr 5 3 3
addi 3 1 3
seti 27 6 3
seti 0 2 5
addi 5 1 2
muli 2 256 2
gtrr 2 4 2
addr 2 3 3
addi 3 1 3
seti 25 3 3
addi 5 1 5
seti 17 1 3
setr 5 6 4
seti 7 8 3
eqrr 1 0 5
addr 5 3 3
seti 5 6 3


*/