import java.util.*;

public class AdventOfCode2018Day16 {
	
	private static int countSamples = 0;
	
	private static void initOps(ArrayList<Operation> ops) {
		ops.add(new Addr());
		ops.add(new Addi());
		ops.add(new Mulr());
		ops.add(new Muli());
		ops.add(new Banr());
		ops.add(new Bani());
		ops.add(new Borr());
		ops.add(new Bori());
		ops.add(new Setr());
		ops.add(new Seti());
		ops.add(new Gtir());
		ops.add(new Gtri());
		ops.add(new Gtrr());
		ops.add(new Eqir());
		ops.add(new Eqri());
		ops.add(new Eqrr());
	}
	
	private static boolean matched(ArrayList<Integer> a, ArrayList<Integer> b) {
		for (int i = 0; i < a.size(); i++) {
			if (a.get(i) != b.get(i)) return false;
		}
		return true;
	}
	
	private static void matchOps(ArrayList<Operation> ops, String[] before, 
			String[] input, String[] after, TreeMap<Integer, HashMap<Operation, Integer>> tm) {
		
		ArrayList<Integer> registers = new ArrayList<>();
		ArrayList<Integer> abc = new ArrayList<>();
		ArrayList<Integer> result = new ArrayList<>();
		for (String b : before) registers.add(Integer.parseInt(b));
		for (String i : input)  abc.add(Integer.parseInt(i));
		for (String a : after)  result.add(Integer.parseInt(a));
		int opcode = abc.remove(0);
		
		int counter = 0;
		for (Operation op : ops) {
			// check if there is a match and then add to tm
			if (matched(result, op.process(abc.get(0), abc.get(1), abc.get(2), registers))) {
				counter++;
				if (!tm.containsKey(opcode)) {
					tm.put(opcode, new HashMap<Operation, Integer>());
				}
				if (tm.get(opcode).containsKey(op)) tm.get(opcode).put(op, tm.get(opcode).get(op) + 1);
				else tm.get(opcode).put(op, 1);
			}
		}
		if (counter >= 3) countSamples++;
	}
	
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        ArrayList<Operation> ops = new ArrayList<>();
        // <opcode <list of operations that relate to that opcode, number of those operations>>
        TreeMap<Integer, HashMap<Operation, Integer>> tm = new TreeMap<>();
        initOps(ops);
        // read the before-after data
        while (true) {
        	String firstLine = s.nextLine();
        	if (firstLine.equals("")) break;
        	String[] before = firstLine.replaceFirst("Before: \\[", "").replaceFirst("\\]", "").split(", ");
        	String[] input = s.nextLine().split(" ");
        	String[] after = s.nextLine().replaceFirst("After:  \\[", "").replaceFirst("\\]", "").split(", ");
        	s.nextLine(); // empty separator line
        	// run through the operations and see which ones match
        	matchOps(ops, before, input, after, tm);
        }
        System.out.println("Answer to the first part: " + countSamples);
        
        // here comes the second part
        findOpcodes(tm);
        s.nextLine();
        ArrayList<Integer> registers = new ArrayList<>();
        for (int i = 0; i < 4; i++) registers.add(0);
        while (true) {
        	String line = s.nextLine();
        	if (line.equals("")) break;
        	String[] splitted = line.split(" ");
        	int opcode = Integer.parseInt(splitted[0]);
        	ArrayList<Integer> abc = new ArrayList<>();
        	for (int i = 1; i < splitted.length; i++) abc.add(Integer.parseInt(splitted[i]));
        	Set<Operation> keys = tm.get(opcode).keySet();
        	for (Operation op : keys) {
        		registers = op.process(abc.get(0), abc.get(1), abc.get(2), registers);
        	}
        }
        System.out.println("Answer to the second part: " + registers);
        
        s.close();
    }
    
    private static void findOpcodes(TreeMap<Integer, HashMap<Operation, Integer>> tm) {
    	// if we know an opcode corresponds to a specific operation,
    	// then delete that opcode from all other opcodes
    	while (true) {
    		boolean done = true;
	        for (Map.Entry<Integer, HashMap<Operation, Integer>> entry : tm.entrySet()) {
	        	if (entry.getValue().size() == 1) { // delete this operation from anywhere else
	        		Set<Operation> keys = entry.getValue().keySet();
	        		for (Operation key : keys) {
		        		for (Map.Entry<Integer, HashMap<Operation, Integer>> del : tm.entrySet()) {
		        			if (del != entry) {
		        				if (del.getValue().remove(key) != null) done = false;
		        			}
		        		}
	        		}
	        	}
	        }
	        if (done) break;
    	}
    }
    
    private static void printTM(TreeMap<Integer, HashMap<Operation, Integer>> tm) {
        for (Map.Entry<Integer, HashMap<Operation, Integer>> entry : tm.entrySet()) {
        	System.out.println(entry.getKey() + ": ");
        	for (Map.Entry<Operation, Integer> hm : entry.getValue().entrySet()) {
        		System.out.println(hm.getKey().getName() + " " + hm.getValue());
        	}
        	System.out.println();
        }
    }
}

interface Operation {
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers);
	public String getName();
}

class Addr implements Operation {
	String name = "addr";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) + registers.get(B));
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Addi implements Operation {
	String name = "addi";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) + B);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Mulr implements Operation {
	String name = "mulr";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) * registers.get(B));
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Muli implements Operation {
	String name = "muli";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) * B);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Banr implements Operation {
	String name = "banr";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) & registers.get(B));
		return result;
	}
		
	public String getName() {
		return name;
	}
}

class Bani implements Operation {
	String name = "bani";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) & B);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Borr implements Operation {
	String name = "borr";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) | registers.get(B));
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Bori implements Operation {
	String name = "bori";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A) | B);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Setr implements Operation {
	String name = "setr";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, registers.get(A));
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Seti implements Operation {
	String name = "seti";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		result.set(C, A);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Gtir implements Operation {
	String name = "gtir";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		if (A > registers.get(B)) result.set(C, 1);
		else result.set(C, 0);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Gtri implements Operation {
	String name = "gtri";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		if (registers.get(A) > B) result.set(C, 1);
		else result.set(C, 0);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Gtrr implements Operation {
	String name = "gtrr";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		if (registers.get(A) > registers.get(B)) result.set(C, 1);
		else result.set(C, 0);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Eqir implements Operation {
	String name = "eqir";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		if (A == registers.get(B)) result.set(C, 1);
		else result.set(C, 0);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Eqri implements Operation {
	String name = "eqri";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		if (registers.get(A) == B) result.set(C, 1);
		else result.set(C, 0);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

class Eqrr implements Operation {
	String name = "eqrr";
	public ArrayList<Integer> process(int A, int B, int C, ArrayList<Integer> registers) {
		ArrayList<Integer> result = new ArrayList<>();
		for (Integer i : registers) result.add(i);
		if (registers.get(A) == registers.get(B)) result.set(C, 1);
		else result.set(C, 0);
		return result;
	}
	
	public String getName() {
		return name;
	}
}

/*
Before: [2, 3, 2, 2]
0 3 3 0
After:  [0, 3, 2, 2]

Before: [1, 1, 2, 3]
6 0 2 0
After:  [0, 1, 2, 3]

Before: [1, 0, 2, 2]
6 0 2 0
After:  [0, 0, 2, 2]

Before: [1, 1, 1, 1]
11 2 1 0
After:  [2, 1, 1, 1]

Before: [3, 0, 0, 2]
0 3 3 2
After:  [3, 0, 0, 2]

Before: [1, 1, 2, 2]
9 1 0 2
After:  [1, 1, 1, 2]

Before: [3, 2, 1, 1]
5 2 1 1
After:  [3, 2, 1, 1]

Before: [1, 1, 0, 3]
7 1 3 0
After:  [0, 1, 0, 3]

Before: [1, 2, 1, 3]
5 2 1 0
After:  [2, 2, 1, 3]

Before: [0, 2, 2, 0]
8 0 0 0
After:  [0, 2, 2, 0]

Before: [2, 0, 0, 1]
3 0 3 0
After:  [1, 0, 0, 1]

Before: [3, 1, 2, 2]
4 1 3 1
After:  [3, 0, 2, 2]

Before: [2, 2, 1, 1]
5 2 1 1
After:  [2, 2, 1, 1]

Before: [1, 1, 2, 2]
6 0 2 2
After:  [1, 1, 0, 2]

Before: [1, 1, 1, 2]
4 1 3 0
After:  [0, 1, 1, 2]

Before: [2, 1, 3, 1]
13 1 3 0
After:  [1, 1, 3, 1]

Before: [0, 1, 2, 1]
13 1 3 1
After:  [0, 1, 2, 1]

Before: [2, 1, 0, 2]
4 1 3 1
After:  [2, 0, 0, 2]

Before: [2, 1, 0, 1]
2 0 1 3
After:  [2, 1, 0, 1]

Before: [3, 1, 2, 1]
12 1 2 2
After:  [3, 1, 0, 1]

Before: [1, 1, 3, 2]
4 1 3 3
After:  [1, 1, 3, 0]

Before: [2, 2, 1, 3]
7 1 3 0
After:  [0, 2, 1, 3]

Before: [1, 3, 2, 1]
6 0 2 1
After:  [1, 0, 2, 1]

Before: [2, 1, 2, 1]
13 1 3 1
After:  [2, 1, 2, 1]

Before: [2, 1, 3, 0]
14 2 0 3
After:  [2, 1, 3, 1]

Before: [1, 1, 2, 3]
6 0 2 3
After:  [1, 1, 2, 0]

Before: [1, 1, 1, 3]
11 2 1 2
After:  [1, 1, 2, 3]

Before: [2, 2, 3, 2]
0 3 3 0
After:  [0, 2, 3, 2]

Before: [1, 2, 0, 2]
1 0 2 3
After:  [1, 2, 0, 0]

Before: [2, 1, 0, 0]
2 0 1 3
After:  [2, 1, 0, 1]

Before: [0, 2, 1, 1]
5 2 1 3
After:  [0, 2, 1, 2]

Before: [0, 3, 2, 1]
10 3 2 3
After:  [0, 3, 2, 1]

Before: [3, 3, 2, 2]
0 3 3 0
After:  [0, 3, 2, 2]

Before: [1, 1, 2, 0]
12 1 2 0
After:  [0, 1, 2, 0]

Before: [0, 2, 1, 3]
5 2 1 0
After:  [2, 2, 1, 3]

Before: [0, 3, 2, 1]
8 0 0 0
After:  [0, 3, 2, 1]

Before: [1, 1, 1, 3]
11 2 1 1
After:  [1, 2, 1, 3]

Before: [0, 1, 1, 2]
11 2 1 2
After:  [0, 1, 2, 2]

Before: [1, 1, 1, 1]
13 1 3 1
After:  [1, 1, 1, 1]

Before: [1, 3, 0, 0]
1 0 2 1
After:  [1, 0, 0, 0]

Before: [2, 2, 3, 1]
14 2 0 1
After:  [2, 1, 3, 1]

Before: [0, 3, 0, 3]
8 0 0 3
After:  [0, 3, 0, 0]

Before: [0, 0, 1, 1]
8 0 0 2
After:  [0, 0, 0, 1]

Before: [0, 3, 2, 1]
8 0 0 2
After:  [0, 3, 0, 1]

Before: [2, 1, 2, 3]
12 1 2 1
After:  [2, 0, 2, 3]

Before: [3, 2, 2, 3]
14 2 1 2
After:  [3, 2, 1, 3]

Before: [2, 2, 3, 0]
15 2 2 2
After:  [2, 2, 1, 0]

Before: [2, 3, 3, 2]
15 2 2 0
After:  [1, 3, 3, 2]

Before: [1, 1, 0, 0]
1 0 2 3
After:  [1, 1, 0, 0]

Before: [3, 2, 2, 2]
0 3 3 3
After:  [3, 2, 2, 0]

Before: [1, 3, 2, 2]
6 0 2 3
After:  [1, 3, 2, 0]

Before: [2, 1, 0, 1]
3 0 3 1
After:  [2, 1, 0, 1]

Before: [3, 3, 1, 3]
7 2 3 0
After:  [0, 3, 1, 3]

Before: [0, 2, 1, 0]
5 2 1 3
After:  [0, 2, 1, 2]

Before: [1, 1, 1, 2]
4 1 3 2
After:  [1, 1, 0, 2]

Before: [0, 3, 1, 2]
8 0 0 1
After:  [0, 0, 1, 2]

Before: [2, 1, 3, 3]
7 1 3 0
After:  [0, 1, 3, 3]

Before: [3, 2, 2, 1]
10 3 2 0
After:  [1, 2, 2, 1]

Before: [2, 1, 0, 1]
3 0 3 3
After:  [2, 1, 0, 1]

Before: [2, 1, 1, 1]
13 1 3 2
After:  [2, 1, 1, 1]

Before: [2, 2, 0, 3]
7 1 3 1
After:  [2, 0, 0, 3]

Before: [2, 2, 0, 1]
3 0 3 0
After:  [1, 2, 0, 1]

Before: [2, 2, 3, 1]
3 0 3 3
After:  [2, 2, 3, 1]

Before: [1, 2, 0, 0]
1 0 2 1
After:  [1, 0, 0, 0]

Before: [2, 2, 2, 2]
14 3 2 1
After:  [2, 0, 2, 2]

Before: [3, 1, 1, 2]
4 1 3 1
After:  [3, 0, 1, 2]

Before: [2, 1, 1, 1]
2 0 1 3
After:  [2, 1, 1, 1]

Before: [1, 1, 0, 0]
1 0 2 1
After:  [1, 0, 0, 0]

Before: [1, 3, 0, 2]
1 0 2 1
After:  [1, 0, 0, 2]

Before: [1, 1, 1, 3]
9 1 0 2
After:  [1, 1, 1, 3]

Before: [3, 1, 2, 2]
12 1 2 2
After:  [3, 1, 0, 2]

Before: [0, 1, 2, 1]
12 1 2 2
After:  [0, 1, 0, 1]

Before: [3, 2, 0, 3]
7 1 3 3
After:  [3, 2, 0, 0]

Before: [2, 1, 2, 3]
7 2 3 2
After:  [2, 1, 0, 3]

Before: [3, 1, 3, 1]
13 1 3 0
After:  [1, 1, 3, 1]

Before: [2, 1, 1, 1]
11 2 1 0
After:  [2, 1, 1, 1]

Before: [0, 1, 1, 0]
11 2 1 3
After:  [0, 1, 1, 2]

Before: [2, 1, 3, 3]
7 1 3 2
After:  [2, 1, 0, 3]

Before: [2, 3, 2, 1]
10 3 2 1
After:  [2, 1, 2, 1]

Before: [1, 1, 2, 2]
4 1 3 1
After:  [1, 0, 2, 2]

Before: [1, 3, 0, 1]
1 0 2 0
After:  [0, 3, 0, 1]

Before: [1, 3, 0, 3]
1 0 2 3
After:  [1, 3, 0, 0]

Before: [2, 3, 3, 1]
3 0 3 1
After:  [2, 1, 3, 1]

Before: [2, 1, 1, 2]
11 2 1 3
After:  [2, 1, 1, 2]

Before: [2, 1, 1, 1]
2 0 1 1
After:  [2, 1, 1, 1]

Before: [3, 1, 2, 2]
4 1 3 0
After:  [0, 1, 2, 2]

Before: [2, 0, 2, 1]
10 3 2 1
After:  [2, 1, 2, 1]

Before: [1, 3, 0, 1]
1 0 2 1
After:  [1, 0, 0, 1]

Before: [1, 1, 0, 2]
9 1 0 0
After:  [1, 1, 0, 2]

Before: [2, 3, 2, 1]
3 0 3 2
After:  [2, 3, 1, 1]

Before: [1, 2, 2, 1]
0 3 3 3
After:  [1, 2, 2, 0]

Before: [3, 1, 2, 2]
12 1 2 1
After:  [3, 0, 2, 2]

Before: [0, 2, 3, 1]
8 0 0 1
After:  [0, 0, 3, 1]

Before: [0, 0, 2, 1]
10 3 2 2
After:  [0, 0, 1, 1]

Before: [3, 2, 1, 3]
15 0 0 3
After:  [3, 2, 1, 1]

Before: [1, 3, 2, 2]
6 0 2 2
After:  [1, 3, 0, 2]

Before: [1, 2, 2, 3]
6 0 2 3
After:  [1, 2, 2, 0]

Before: [1, 1, 3, 2]
4 1 3 2
After:  [1, 1, 0, 2]

Before: [1, 2, 2, 1]
10 3 2 3
After:  [1, 2, 2, 1]

Before: [1, 2, 2, 1]
6 0 2 2
After:  [1, 2, 0, 1]

Before: [1, 2, 1, 3]
7 2 3 1
After:  [1, 0, 1, 3]

Before: [1, 2, 2, 1]
10 3 2 0
After:  [1, 2, 2, 1]

Before: [2, 3, 3, 1]
3 0 3 3
After:  [2, 3, 3, 1]

Before: [2, 3, 2, 3]
14 2 0 2
After:  [2, 3, 1, 3]

Before: [2, 1, 3, 1]
2 0 1 3
After:  [2, 1, 3, 1]

Before: [0, 3, 3, 0]
8 0 0 1
After:  [0, 0, 3, 0]

Before: [2, 1, 1, 3]
7 2 3 2
After:  [2, 1, 0, 3]

Before: [0, 2, 2, 1]
10 3 2 3
After:  [0, 2, 2, 1]

Before: [3, 2, 1, 3]
5 2 1 3
After:  [3, 2, 1, 2]

Before: [3, 1, 1, 2]
0 3 3 2
After:  [3, 1, 0, 2]

Before: [0, 3, 1, 3]
7 2 3 3
After:  [0, 3, 1, 0]

Before: [2, 0, 2, 1]
10 3 2 3
After:  [2, 0, 2, 1]

Before: [2, 2, 1, 0]
5 2 1 2
After:  [2, 2, 2, 0]

Before: [2, 1, 2, 2]
4 1 3 3
After:  [2, 1, 2, 0]

Before: [1, 3, 1, 1]
0 2 3 2
After:  [1, 3, 0, 1]

Before: [1, 1, 0, 3]
1 0 2 3
After:  [1, 1, 0, 0]

Before: [1, 0, 0, 3]
1 0 2 2
After:  [1, 0, 0, 3]

Before: [2, 1, 1, 0]
11 2 1 0
After:  [2, 1, 1, 0]

Before: [2, 0, 0, 1]
3 0 3 3
After:  [2, 0, 0, 1]

Before: [3, 3, 0, 1]
14 0 2 2
After:  [3, 3, 1, 1]

Before: [0, 1, 2, 0]
8 0 0 1
After:  [0, 0, 2, 0]

Before: [2, 0, 1, 1]
3 0 3 2
After:  [2, 0, 1, 1]

Before: [1, 3, 2, 0]
6 0 2 1
After:  [1, 0, 2, 0]

Before: [3, 3, 2, 0]
2 0 2 3
After:  [3, 3, 2, 1]

Before: [2, 1, 0, 1]
13 1 3 2
After:  [2, 1, 1, 1]

Before: [1, 1, 2, 1]
13 1 3 2
After:  [1, 1, 1, 1]

Before: [1, 3, 2, 0]
6 0 2 2
After:  [1, 3, 0, 0]

Before: [3, 1, 3, 2]
4 1 3 1
After:  [3, 0, 3, 2]

Before: [2, 3, 2, 2]
15 0 0 3
After:  [2, 3, 2, 1]

Before: [2, 3, 2, 1]
3 0 3 3
After:  [2, 3, 2, 1]

Before: [2, 1, 1, 2]
4 1 3 0
After:  [0, 1, 1, 2]

Before: [1, 1, 1, 1]
13 1 3 0
After:  [1, 1, 1, 1]

Before: [3, 1, 1, 0]
11 2 1 2
After:  [3, 1, 2, 0]

Before: [3, 1, 1, 1]
11 2 1 0
After:  [2, 1, 1, 1]

Before: [3, 1, 0, 2]
4 1 3 0
After:  [0, 1, 0, 2]

Before: [3, 3, 1, 3]
15 0 0 3
After:  [3, 3, 1, 1]

Before: [1, 2, 2, 1]
10 3 2 1
After:  [1, 1, 2, 1]

Before: [1, 1, 1, 0]
11 2 1 3
After:  [1, 1, 1, 2]

Before: [1, 1, 1, 2]
11 2 1 0
After:  [2, 1, 1, 2]

Before: [3, 2, 2, 2]
14 2 1 2
After:  [3, 2, 1, 2]

Before: [0, 0, 3, 3]
15 2 2 3
After:  [0, 0, 3, 1]

Before: [0, 3, 2, 2]
0 3 3 0
After:  [0, 3, 2, 2]

Before: [1, 0, 2, 1]
10 3 2 1
After:  [1, 1, 2, 1]

Before: [2, 1, 2, 2]
14 3 2 1
After:  [2, 0, 2, 2]

Before: [1, 0, 0, 3]
1 0 2 1
After:  [1, 0, 0, 3]

Before: [3, 2, 1, 3]
7 2 3 1
After:  [3, 0, 1, 3]

Before: [3, 1, 1, 2]
11 2 1 0
After:  [2, 1, 1, 2]

Before: [1, 3, 2, 1]
6 0 2 0
After:  [0, 3, 2, 1]

Before: [2, 0, 3, 1]
3 0 3 0
After:  [1, 0, 3, 1]

Before: [3, 1, 2, 2]
12 1 2 0
After:  [0, 1, 2, 2]

Before: [3, 1, 2, 0]
12 1 2 3
After:  [3, 1, 2, 0]

Before: [2, 1, 2, 0]
2 0 1 3
After:  [2, 1, 2, 1]

Before: [1, 1, 3, 1]
14 2 3 2
After:  [1, 1, 0, 1]

Before: [1, 3, 2, 3]
6 0 2 0
After:  [0, 3, 2, 3]

Before: [1, 1, 2, 3]
12 1 2 0
After:  [0, 1, 2, 3]

Before: [3, 0, 2, 1]
10 3 2 1
After:  [3, 1, 2, 1]

Before: [1, 0, 2, 0]
6 0 2 1
After:  [1, 0, 2, 0]

Before: [2, 3, 1, 3]
7 2 3 2
After:  [2, 3, 0, 3]

Before: [1, 1, 1, 1]
11 2 1 3
After:  [1, 1, 1, 2]

Before: [2, 1, 2, 2]
2 0 1 0
After:  [1, 1, 2, 2]

Before: [1, 2, 1, 3]
7 2 3 3
After:  [1, 2, 1, 0]

Before: [1, 1, 2, 2]
12 1 2 0
After:  [0, 1, 2, 2]

Before: [2, 0, 2, 1]
10 3 2 2
After:  [2, 0, 1, 1]

Before: [0, 1, 2, 3]
12 1 2 2
After:  [0, 1, 0, 3]

Before: [2, 1, 1, 3]
11 2 1 0
After:  [2, 1, 1, 3]

Before: [2, 1, 3, 1]
13 1 3 3
After:  [2, 1, 3, 1]

Before: [0, 2, 1, 1]
8 0 0 1
After:  [0, 0, 1, 1]

Before: [1, 0, 0, 2]
1 0 2 1
After:  [1, 0, 0, 2]

Before: [2, 1, 3, 3]
2 0 1 1
After:  [2, 1, 3, 3]

Before: [0, 1, 2, 2]
4 1 3 2
After:  [0, 1, 0, 2]

Before: [1, 1, 2, 1]
13 1 3 0
After:  [1, 1, 2, 1]

Before: [1, 1, 3, 0]
9 1 0 1
After:  [1, 1, 3, 0]

Before: [1, 1, 0, 1]
1 0 2 1
After:  [1, 0, 0, 1]

Before: [2, 2, 3, 1]
3 0 3 1
After:  [2, 1, 3, 1]

Before: [3, 2, 1, 2]
5 2 1 0
After:  [2, 2, 1, 2]

Before: [1, 1, 2, 0]
12 1 2 1
After:  [1, 0, 2, 0]

Before: [3, 0, 2, 3]
2 0 2 3
After:  [3, 0, 2, 1]

Before: [2, 1, 3, 3]
2 0 1 2
After:  [2, 1, 1, 3]

Before: [3, 1, 3, 1]
15 0 0 0
After:  [1, 1, 3, 1]

Before: [0, 1, 3, 2]
4 1 3 1
After:  [0, 0, 3, 2]

Before: [3, 2, 3, 3]
15 2 0 0
After:  [1, 2, 3, 3]

Before: [1, 3, 3, 1]
0 3 3 0
After:  [0, 3, 3, 1]

Before: [0, 0, 2, 3]
7 2 3 0
After:  [0, 0, 2, 3]

Before: [0, 2, 1, 3]
7 2 3 2
After:  [0, 2, 0, 3]

Before: [3, 0, 2, 1]
2 0 2 0
After:  [1, 0, 2, 1]

Before: [2, 2, 2, 1]
10 3 2 2
After:  [2, 2, 1, 1]

Before: [1, 2, 0, 1]
1 0 2 0
After:  [0, 2, 0, 1]

Before: [1, 2, 0, 0]
1 0 2 2
After:  [1, 2, 0, 0]

Before: [3, 1, 2, 1]
2 0 2 1
After:  [3, 1, 2, 1]

Before: [0, 0, 3, 1]
8 0 0 1
After:  [0, 0, 3, 1]

Before: [0, 1, 1, 2]
11 2 1 3
After:  [0, 1, 1, 2]

Before: [0, 1, 3, 1]
13 1 3 2
After:  [0, 1, 1, 1]

Before: [1, 1, 1, 2]
11 2 1 2
After:  [1, 1, 2, 2]

Before: [2, 0, 3, 1]
3 0 3 3
After:  [2, 0, 3, 1]

Before: [0, 2, 1, 2]
8 0 0 0
After:  [0, 2, 1, 2]

Before: [1, 0, 2, 1]
6 0 2 1
After:  [1, 0, 2, 1]

Before: [1, 1, 0, 2]
4 1 3 3
After:  [1, 1, 0, 0]

Before: [2, 2, 1, 1]
3 0 3 2
After:  [2, 2, 1, 1]

Before: [1, 2, 1, 2]
5 2 1 2
After:  [1, 2, 2, 2]

Before: [2, 0, 2, 1]
3 0 3 3
After:  [2, 0, 2, 1]

Before: [2, 1, 0, 1]
3 0 3 2
After:  [2, 1, 1, 1]

Before: [2, 2, 1, 2]
5 2 1 1
After:  [2, 2, 1, 2]

Before: [1, 1, 2, 2]
9 1 0 3
After:  [1, 1, 2, 1]

Before: [2, 2, 1, 3]
15 0 0 3
After:  [2, 2, 1, 1]

Before: [3, 1, 0, 1]
13 1 3 3
After:  [3, 1, 0, 1]

Before: [3, 3, 2, 1]
10 3 2 2
After:  [3, 3, 1, 1]

Before: [0, 1, 3, 2]
4 1 3 3
After:  [0, 1, 3, 0]

Before: [0, 1, 1, 0]
11 2 1 2
After:  [0, 1, 2, 0]

Before: [3, 1, 3, 1]
14 3 1 0
After:  [0, 1, 3, 1]

Before: [0, 1, 3, 3]
8 0 0 3
After:  [0, 1, 3, 0]

Before: [0, 1, 2, 1]
10 3 2 0
After:  [1, 1, 2, 1]

Before: [2, 1, 2, 1]
3 0 3 2
After:  [2, 1, 1, 1]

Before: [0, 2, 1, 3]
5 2 1 3
After:  [0, 2, 1, 2]

Before: [1, 0, 0, 3]
1 0 2 0
After:  [0, 0, 0, 3]

Before: [2, 3, 0, 1]
3 0 3 0
After:  [1, 3, 0, 1]

Before: [2, 1, 2, 1]
12 1 2 1
After:  [2, 0, 2, 1]

Before: [2, 1, 3, 2]
4 1 3 0
After:  [0, 1, 3, 2]

Before: [1, 2, 1, 0]
5 2 1 3
After:  [1, 2, 1, 2]

Before: [3, 1, 3, 1]
13 1 3 1
After:  [3, 1, 3, 1]

Before: [1, 2, 1, 0]
5 2 1 1
After:  [1, 2, 1, 0]

Before: [3, 1, 2, 1]
10 3 2 1
After:  [3, 1, 2, 1]

Before: [1, 1, 1, 1]
13 1 3 2
After:  [1, 1, 1, 1]

Before: [2, 1, 2, 1]
13 1 3 2
After:  [2, 1, 1, 1]

Before: [1, 2, 1, 3]
7 1 3 1
After:  [1, 0, 1, 3]

Before: [0, 0, 2, 2]
14 3 2 3
After:  [0, 0, 2, 0]

Before: [2, 2, 1, 3]
15 0 0 1
After:  [2, 1, 1, 3]

Before: [2, 1, 3, 2]
4 1 3 1
After:  [2, 0, 3, 2]

Before: [1, 2, 1, 3]
5 2 1 2
After:  [1, 2, 2, 3]

Before: [2, 2, 1, 0]
5 2 1 3
After:  [2, 2, 1, 2]

Before: [2, 0, 2, 1]
3 0 3 2
After:  [2, 0, 1, 1]

Before: [1, 0, 0, 1]
1 0 2 0
After:  [0, 0, 0, 1]

Before: [2, 1, 1, 0]
15 0 0 0
After:  [1, 1, 1, 0]

Before: [0, 0, 3, 3]
8 0 0 0
After:  [0, 0, 3, 3]

Before: [1, 1, 1, 2]
4 1 3 3
After:  [1, 1, 1, 0]

Before: [1, 2, 0, 3]
1 0 2 1
After:  [1, 0, 0, 3]

Before: [1, 1, 0, 2]
9 1 0 1
After:  [1, 1, 0, 2]

Before: [3, 1, 1, 1]
11 2 1 3
After:  [3, 1, 1, 2]

Before: [1, 1, 0, 3]
7 1 3 1
After:  [1, 0, 0, 3]

Before: [1, 1, 1, 3]
7 1 3 2
After:  [1, 1, 0, 3]

Before: [1, 1, 2, 3]
6 0 2 1
After:  [1, 0, 2, 3]

Before: [2, 1, 1, 2]
4 1 3 3
After:  [2, 1, 1, 0]

Before: [2, 2, 2, 3]
7 1 3 2
After:  [2, 2, 0, 3]

Before: [1, 3, 2, 1]
0 3 3 3
After:  [1, 3, 2, 0]

Before: [0, 0, 3, 3]
8 0 0 3
After:  [0, 0, 3, 0]

Before: [3, 1, 3, 1]
15 0 0 1
After:  [3, 1, 3, 1]

Before: [1, 0, 0, 2]
1 0 2 2
After:  [1, 0, 0, 2]

Before: [0, 0, 0, 1]
0 3 3 1
After:  [0, 0, 0, 1]

Before: [1, 1, 1, 2]
9 1 0 0
After:  [1, 1, 1, 2]

Before: [1, 3, 0, 1]
1 0 2 2
After:  [1, 3, 0, 1]

Before: [1, 1, 3, 3]
9 1 0 0
After:  [1, 1, 3, 3]

Before: [2, 1, 3, 1]
13 1 3 2
After:  [2, 1, 1, 1]

Before: [2, 1, 3, 2]
4 1 3 3
After:  [2, 1, 3, 0]

Before: [2, 1, 2, 1]
13 1 3 3
After:  [2, 1, 2, 1]

Before: [1, 0, 2, 2]
6 0 2 1
After:  [1, 0, 2, 2]

Before: [1, 1, 2, 1]
10 3 2 2
After:  [1, 1, 1, 1]

Before: [3, 2, 1, 3]
5 2 1 2
After:  [3, 2, 2, 3]

Before: [0, 1, 2, 0]
12 1 2 2
After:  [0, 1, 0, 0]

Before: [2, 1, 1, 3]
2 0 1 0
After:  [1, 1, 1, 3]

Before: [1, 2, 2, 3]
14 2 1 2
After:  [1, 2, 1, 3]

Before: [1, 2, 0, 3]
1 0 2 0
After:  [0, 2, 0, 3]

Before: [0, 1, 2, 2]
8 0 0 2
After:  [0, 1, 0, 2]

Before: [0, 2, 1, 0]
5 2 1 1
After:  [0, 2, 1, 0]

Before: [2, 0, 0, 1]
15 0 0 2
After:  [2, 0, 1, 1]

Before: [2, 2, 1, 3]
5 2 1 0
After:  [2, 2, 1, 3]

Before: [3, 2, 2, 1]
10 3 2 2
After:  [3, 2, 1, 1]

Before: [0, 3, 2, 2]
14 3 2 2
After:  [0, 3, 0, 2]

Before: [1, 2, 0, 1]
1 0 2 2
After:  [1, 2, 0, 1]

Before: [0, 1, 1, 0]
11 2 1 0
After:  [2, 1, 1, 0]

Before: [1, 2, 2, 3]
14 2 1 3
After:  [1, 2, 2, 1]

Before: [2, 1, 3, 1]
3 0 3 3
After:  [2, 1, 3, 1]

Before: [0, 1, 2, 3]
7 1 3 3
After:  [0, 1, 2, 0]

Before: [2, 1, 2, 2]
2 0 1 1
After:  [2, 1, 2, 2]

Before: [2, 2, 1, 0]
5 2 1 1
After:  [2, 2, 1, 0]

Before: [3, 2, 1, 3]
5 2 1 0
After:  [2, 2, 1, 3]

Before: [1, 1, 2, 1]
0 3 3 1
After:  [1, 0, 2, 1]

Before: [1, 0, 2, 1]
6 0 2 3
After:  [1, 0, 2, 0]

Before: [1, 3, 0, 2]
1 0 2 0
After:  [0, 3, 0, 2]

Before: [0, 1, 1, 3]
11 2 1 2
After:  [0, 1, 2, 3]

Before: [1, 1, 3, 3]
9 1 0 1
After:  [1, 1, 3, 3]

Before: [3, 1, 2, 3]
12 1 2 1
After:  [3, 0, 2, 3]

Before: [0, 1, 1, 1]
13 1 3 0
After:  [1, 1, 1, 1]

Before: [1, 1, 2, 3]
9 1 0 1
After:  [1, 1, 2, 3]

Before: [0, 3, 1, 3]
7 2 3 0
After:  [0, 3, 1, 3]

Before: [3, 1, 2, 1]
13 1 3 2
After:  [3, 1, 1, 1]

Before: [1, 0, 1, 3]
7 2 3 1
After:  [1, 0, 1, 3]

Before: [1, 1, 0, 3]
1 0 2 0
After:  [0, 1, 0, 3]

Before: [2, 1, 2, 2]
12 1 2 2
After:  [2, 1, 0, 2]

Before: [3, 0, 1, 3]
14 3 0 0
After:  [1, 0, 1, 3]

Before: [3, 1, 3, 3]
7 1 3 3
After:  [3, 1, 3, 0]

Before: [1, 1, 0, 0]
1 0 2 0
After:  [0, 1, 0, 0]

Before: [1, 1, 1, 1]
0 2 3 2
After:  [1, 1, 0, 1]

Before: [2, 1, 0, 1]
2 0 1 2
After:  [2, 1, 1, 1]

Before: [1, 1, 2, 1]
14 3 1 1
After:  [1, 0, 2, 1]

Before: [0, 0, 2, 3]
7 2 3 3
After:  [0, 0, 2, 0]

Before: [3, 2, 0, 0]
14 0 2 1
After:  [3, 1, 0, 0]

Before: [0, 0, 2, 3]
8 0 0 0
After:  [0, 0, 2, 3]

Before: [3, 1, 1, 0]
11 2 1 1
After:  [3, 2, 1, 0]

Before: [1, 2, 1, 1]
5 2 1 2
After:  [1, 2, 2, 1]

Before: [0, 2, 1, 3]
7 2 3 3
After:  [0, 2, 1, 0]

Before: [3, 1, 2, 2]
15 0 0 3
After:  [3, 1, 2, 1]

Before: [0, 0, 0, 2]
8 0 0 2
After:  [0, 0, 0, 2]

Before: [3, 1, 3, 1]
13 1 3 2
After:  [3, 1, 1, 1]

Before: [1, 1, 2, 3]
9 1 0 2
After:  [1, 1, 1, 3]

Before: [1, 2, 0, 2]
1 0 2 2
After:  [1, 2, 0, 2]

Before: [2, 1, 2, 3]
2 0 1 3
After:  [2, 1, 2, 1]

Before: [1, 2, 0, 3]
1 0 2 2
After:  [1, 2, 0, 3]

Before: [1, 0, 2, 0]
6 0 2 3
After:  [1, 0, 2, 0]

Before: [1, 0, 3, 1]
0 3 3 2
After:  [1, 0, 0, 1]

Before: [1, 3, 2, 1]
6 0 2 3
After:  [1, 3, 2, 0]

Before: [1, 1, 1, 1]
9 1 0 3
After:  [1, 1, 1, 1]

Before: [0, 3, 2, 1]
0 3 3 1
After:  [0, 0, 2, 1]

Before: [1, 1, 3, 1]
13 1 3 3
After:  [1, 1, 3, 1]

Before: [2, 2, 0, 3]
7 1 3 0
After:  [0, 2, 0, 3]

Before: [0, 3, 2, 1]
0 3 3 0
After:  [0, 3, 2, 1]

Before: [1, 0, 0, 1]
1 0 2 2
After:  [1, 0, 0, 1]

Before: [2, 1, 2, 1]
2 0 1 2
After:  [2, 1, 1, 1]

Before: [1, 2, 2, 2]
6 0 2 2
After:  [1, 2, 0, 2]

Before: [0, 1, 1, 1]
13 1 3 3
After:  [0, 1, 1, 1]

Before: [2, 1, 1, 0]
11 2 1 2
After:  [2, 1, 2, 0]

Before: [0, 1, 3, 1]
13 1 3 1
After:  [0, 1, 3, 1]

Before: [3, 2, 0, 2]
0 3 3 1
After:  [3, 0, 0, 2]

Before: [1, 1, 2, 1]
10 3 2 3
After:  [1, 1, 2, 1]

Before: [2, 1, 2, 1]
13 1 3 0
After:  [1, 1, 2, 1]

Before: [2, 1, 0, 1]
13 1 3 1
After:  [2, 1, 0, 1]

Before: [2, 1, 2, 2]
12 1 2 3
After:  [2, 1, 2, 0]

Before: [0, 1, 2, 0]
12 1 2 1
After:  [0, 0, 2, 0]

Before: [3, 1, 2, 2]
4 1 3 2
After:  [3, 1, 0, 2]

Before: [1, 1, 0, 2]
1 0 2 1
After:  [1, 0, 0, 2]

Before: [0, 2, 1, 1]
0 2 3 2
After:  [0, 2, 0, 1]

Before: [1, 1, 2, 0]
6 0 2 0
After:  [0, 1, 2, 0]

Before: [0, 3, 1, 2]
8 0 0 3
After:  [0, 3, 1, 0]

Before: [1, 3, 0, 0]
1 0 2 2
After:  [1, 3, 0, 0]

Before: [1, 1, 2, 0]
12 1 2 2
After:  [1, 1, 0, 0]

Before: [2, 1, 0, 2]
0 3 3 1
After:  [2, 0, 0, 2]

Before: [0, 3, 3, 3]
8 0 0 1
After:  [0, 0, 3, 3]

Before: [3, 3, 0, 1]
0 3 3 0
After:  [0, 3, 0, 1]

Before: [3, 1, 1, 2]
4 1 3 3
After:  [3, 1, 1, 0]

Before: [2, 1, 2, 3]
12 1 2 3
After:  [2, 1, 2, 0]

Before: [3, 1, 2, 1]
12 1 2 3
After:  [3, 1, 2, 0]

Before: [1, 0, 2, 2]
6 0 2 3
After:  [1, 0, 2, 0]

Before: [1, 1, 0, 1]
0 3 3 1
After:  [1, 0, 0, 1]

Before: [1, 1, 0, 3]
9 1 0 2
After:  [1, 1, 1, 3]

Before: [3, 0, 2, 1]
10 3 2 3
After:  [3, 0, 2, 1]

Before: [2, 2, 3, 3]
14 3 2 3
After:  [2, 2, 3, 1]

Before: [3, 1, 2, 2]
12 1 2 3
After:  [3, 1, 2, 0]

Before: [0, 1, 2, 1]
10 3 2 1
After:  [0, 1, 2, 1]

Before: [0, 1, 3, 0]
8 0 0 2
After:  [0, 1, 0, 0]

Before: [3, 1, 2, 0]
12 1 2 1
After:  [3, 0, 2, 0]

Before: [1, 3, 2, 0]
6 0 2 3
After:  [1, 3, 2, 0]

Before: [2, 0, 1, 3]
7 2 3 3
After:  [2, 0, 1, 0]

Before: [3, 2, 2, 1]
10 3 2 3
After:  [3, 2, 2, 1]

Before: [1, 2, 0, 0]
1 0 2 3
After:  [1, 2, 0, 0]

Before: [2, 1, 1, 1]
0 2 3 0
After:  [0, 1, 1, 1]

Before: [3, 2, 1, 1]
5 2 1 3
After:  [3, 2, 1, 2]

Before: [3, 1, 3, 1]
14 2 3 0
After:  [0, 1, 3, 1]

Before: [2, 1, 1, 3]
14 2 1 1
After:  [2, 0, 1, 3]

Before: [0, 1, 1, 2]
8 0 0 0
After:  [0, 1, 1, 2]

Before: [2, 3, 3, 2]
15 2 2 2
After:  [2, 3, 1, 2]

Before: [0, 1, 2, 3]
7 2 3 1
After:  [0, 0, 2, 3]

Before: [1, 1, 0, 2]
4 1 3 2
After:  [1, 1, 0, 2]

Before: [0, 2, 3, 0]
8 0 0 2
After:  [0, 2, 0, 0]

Before: [0, 1, 1, 1]
11 2 1 1
After:  [0, 2, 1, 1]

Before: [2, 1, 1, 1]
13 1 3 0
After:  [1, 1, 1, 1]

Before: [2, 3, 1, 3]
7 2 3 0
After:  [0, 3, 1, 3]

Before: [2, 1, 2, 3]
12 1 2 2
After:  [2, 1, 0, 3]

Before: [2, 2, 1, 3]
5 2 1 3
After:  [2, 2, 1, 2]

Before: [3, 1, 1, 3]
11 2 1 0
After:  [2, 1, 1, 3]

Before: [0, 0, 1, 3]
7 2 3 1
After:  [0, 0, 1, 3]

Before: [1, 3, 2, 1]
10 3 2 2
After:  [1, 3, 1, 1]

Before: [3, 2, 1, 2]
15 0 0 2
After:  [3, 2, 1, 2]

Before: [1, 2, 1, 1]
0 2 3 1
After:  [1, 0, 1, 1]

Before: [1, 1, 1, 3]
9 1 0 3
After:  [1, 1, 1, 1]

Before: [1, 1, 0, 3]
9 1 0 3
After:  [1, 1, 0, 1]

Before: [0, 1, 1, 1]
11 2 1 2
After:  [0, 1, 2, 1]

Before: [0, 1, 2, 1]
13 1 3 2
After:  [0, 1, 1, 1]

Before: [1, 1, 2, 2]
4 1 3 2
After:  [1, 1, 0, 2]

Before: [3, 1, 1, 2]
11 2 1 3
After:  [3, 1, 1, 2]

Before: [2, 2, 3, 2]
0 3 3 3
After:  [2, 2, 3, 0]

Before: [0, 0, 1, 1]
0 2 3 1
After:  [0, 0, 1, 1]

Before: [0, 1, 2, 2]
12 1 2 1
After:  [0, 0, 2, 2]

Before: [2, 0, 3, 1]
3 0 3 2
After:  [2, 0, 1, 1]

Before: [1, 0, 2, 0]
6 0 2 0
After:  [0, 0, 2, 0]

Before: [0, 2, 1, 1]
5 2 1 0
After:  [2, 2, 1, 1]

Before: [1, 3, 3, 0]
15 2 2 0
After:  [1, 3, 3, 0]

Before: [0, 3, 2, 0]
8 0 0 2
After:  [0, 3, 0, 0]

Before: [2, 2, 2, 1]
0 3 3 1
After:  [2, 0, 2, 1]

Before: [3, 1, 1, 2]
4 1 3 0
After:  [0, 1, 1, 2]

Before: [1, 2, 1, 0]
5 2 1 0
After:  [2, 2, 1, 0]

Before: [2, 2, 3, 3]
15 0 0 0
After:  [1, 2, 3, 3]

Before: [2, 1, 0, 0]
2 0 1 1
After:  [2, 1, 0, 0]

Before: [1, 2, 2, 3]
6 0 2 2
After:  [1, 2, 0, 3]

Before: [1, 0, 0, 1]
1 0 2 1
After:  [1, 0, 0, 1]

Before: [2, 2, 0, 1]
3 0 3 1
After:  [2, 1, 0, 1]

Before: [3, 2, 1, 2]
5 2 1 1
After:  [3, 2, 1, 2]

Before: [2, 1, 3, 2]
14 2 0 1
After:  [2, 1, 3, 2]

Before: [1, 1, 0, 0]
9 1 0 2
After:  [1, 1, 1, 0]

Before: [2, 2, 3, 3]
15 2 2 2
After:  [2, 2, 1, 3]

Before: [0, 2, 1, 0]
8 0 0 2
After:  [0, 2, 0, 0]

Before: [1, 1, 0, 1]
9 1 0 0
After:  [1, 1, 0, 1]

Before: [0, 1, 2, 2]
4 1 3 0
After:  [0, 1, 2, 2]

Before: [1, 1, 0, 0]
9 1 0 0
After:  [1, 1, 0, 0]

Before: [2, 3, 2, 1]
3 0 3 1
After:  [2, 1, 2, 1]

Before: [1, 2, 1, 3]
5 2 1 3
After:  [1, 2, 1, 2]

Before: [2, 1, 1, 3]
11 2 1 2
After:  [2, 1, 2, 3]

Before: [1, 1, 3, 0]
9 1 0 2
After:  [1, 1, 1, 0]

Before: [2, 1, 1, 3]
11 2 1 1
After:  [2, 2, 1, 3]

Before: [2, 1, 3, 2]
2 0 1 2
After:  [2, 1, 1, 2]

Before: [0, 2, 1, 3]
5 2 1 2
After:  [0, 2, 2, 3]

Before: [1, 0, 0, 2]
1 0 2 3
After:  [1, 0, 0, 0]

Before: [1, 1, 1, 2]
9 1 0 3
After:  [1, 1, 1, 1]

Before: [2, 1, 3, 2]
4 1 3 2
After:  [2, 1, 0, 2]

Before: [1, 0, 2, 2]
6 0 2 2
After:  [1, 0, 0, 2]

Before: [3, 1, 1, 3]
11 2 1 1
After:  [3, 2, 1, 3]

Before: [3, 1, 2, 3]
2 0 2 0
After:  [1, 1, 2, 3]

Before: [1, 2, 0, 2]
1 0 2 0
After:  [0, 2, 0, 2]

Before: [3, 1, 2, 1]
10 3 2 2
After:  [3, 1, 1, 1]

Before: [1, 0, 2, 3]
7 2 3 0
After:  [0, 0, 2, 3]

Before: [3, 1, 2, 3]
12 1 2 0
After:  [0, 1, 2, 3]

Before: [2, 1, 1, 3]
7 2 3 1
After:  [2, 0, 1, 3]

Before: [0, 2, 1, 2]
5 2 1 3
After:  [0, 2, 1, 2]

Before: [3, 1, 1, 0]
11 2 1 0
After:  [2, 1, 1, 0]

Before: [1, 1, 3, 1]
9 1 0 0
After:  [1, 1, 3, 1]

Before: [1, 1, 2, 2]
9 1 0 1
After:  [1, 1, 2, 2]

Before: [2, 1, 1, 3]
11 2 1 3
After:  [2, 1, 1, 2]

Before: [1, 1, 1, 2]
4 1 3 1
After:  [1, 0, 1, 2]

Before: [3, 1, 0, 1]
13 1 3 0
After:  [1, 1, 0, 1]

Before: [1, 2, 2, 3]
6 0 2 0
After:  [0, 2, 2, 3]

Before: [1, 3, 0, 3]
1 0 2 0
After:  [0, 3, 0, 3]

Before: [2, 1, 1, 0]
2 0 1 2
After:  [2, 1, 1, 0]

Before: [0, 1, 2, 1]
12 1 2 3
After:  [0, 1, 2, 0]

Before: [2, 3, 1, 1]
3 0 3 3
After:  [2, 3, 1, 1]

Before: [2, 1, 3, 3]
2 0 1 3
After:  [2, 1, 3, 1]

Before: [1, 3, 2, 1]
10 3 2 3
After:  [1, 3, 2, 1]

Before: [1, 1, 3, 3]
9 1 0 3
After:  [1, 1, 3, 1]

Before: [1, 1, 3, 2]
9 1 0 1
After:  [1, 1, 3, 2]

Before: [1, 1, 0, 1]
13 1 3 2
After:  [1, 1, 1, 1]

Before: [3, 0, 2, 0]
2 0 2 1
After:  [3, 1, 2, 0]

Before: [2, 0, 0, 0]
14 0 1 2
After:  [2, 0, 1, 0]

Before: [0, 1, 2, 1]
13 1 3 3
After:  [0, 1, 2, 1]

Before: [2, 1, 3, 0]
14 2 0 1
After:  [2, 1, 3, 0]

Before: [2, 1, 0, 1]
13 1 3 0
After:  [1, 1, 0, 1]

Before: [2, 1, 0, 1]
2 0 1 1
After:  [2, 1, 0, 1]

Before: [0, 3, 2, 1]
10 3 2 0
After:  [1, 3, 2, 1]

Before: [0, 1, 3, 1]
0 3 3 2
After:  [0, 1, 0, 1]

Before: [0, 2, 1, 1]
5 2 1 1
After:  [0, 2, 1, 1]

Before: [2, 1, 1, 2]
15 0 0 3
After:  [2, 1, 1, 1]

Before: [1, 1, 2, 0]
6 0 2 2
After:  [1, 1, 0, 0]

Before: [1, 1, 2, 1]
6 0 2 0
After:  [0, 1, 2, 1]

Before: [0, 2, 1, 3]
7 1 3 1
After:  [0, 0, 1, 3]

Before: [1, 0, 0, 0]
1 0 2 3
After:  [1, 0, 0, 0]

Before: [2, 1, 2, 3]
2 0 1 2
After:  [2, 1, 1, 3]

Before: [0, 2, 0, 2]
0 3 3 1
After:  [0, 0, 0, 2]

Before: [0, 2, 3, 0]
15 2 2 2
After:  [0, 2, 1, 0]

Before: [1, 2, 2, 2]
14 2 1 3
After:  [1, 2, 2, 1]

Before: [0, 1, 3, 1]
8 0 0 2
After:  [0, 1, 0, 1]

Before: [3, 3, 3, 2]
15 0 0 3
After:  [3, 3, 3, 1]

Before: [3, 3, 0, 2]
14 0 2 1
After:  [3, 1, 0, 2]

Before: [0, 1, 1, 3]
11 2 1 0
After:  [2, 1, 1, 3]

Before: [1, 1, 0, 1]
9 1 0 2
After:  [1, 1, 1, 1]

Before: [0, 1, 2, 1]
10 3 2 3
After:  [0, 1, 2, 1]

Before: [2, 2, 2, 1]
10 3 2 1
After:  [2, 1, 2, 1]

Before: [0, 1, 2, 2]
4 1 3 3
After:  [0, 1, 2, 0]

Before: [1, 2, 2, 1]
10 3 2 2
After:  [1, 2, 1, 1]

Before: [2, 1, 1, 2]
11 2 1 1
After:  [2, 2, 1, 2]

Before: [1, 1, 2, 1]
12 1 2 3
After:  [1, 1, 2, 0]

Before: [3, 3, 1, 1]
0 2 3 1
After:  [3, 0, 1, 1]

Before: [0, 1, 2, 2]
4 1 3 1
After:  [0, 0, 2, 2]

Before: [0, 3, 2, 2]
8 0 0 3
After:  [0, 3, 2, 0]

Before: [2, 1, 2, 1]
2 0 1 0
After:  [1, 1, 2, 1]

Before: [1, 1, 0, 3]
1 0 2 1
After:  [1, 0, 0, 3]

Before: [3, 3, 3, 2]
15 0 0 0
After:  [1, 3, 3, 2]

Before: [0, 1, 1, 2]
4 1 3 2
After:  [0, 1, 0, 2]

Before: [1, 3, 0, 3]
1 0 2 1
After:  [1, 0, 0, 3]

Before: [1, 1, 0, 1]
1 0 2 2
After:  [1, 1, 0, 1]

Before: [2, 1, 0, 2]
4 1 3 0
After:  [0, 1, 0, 2]

Before: [3, 2, 2, 2]
2 0 2 2
After:  [3, 2, 1, 2]

Before: [0, 2, 2, 1]
10 3 2 1
After:  [0, 1, 2, 1]

Before: [0, 1, 0, 2]
4 1 3 2
After:  [0, 1, 0, 2]

Before: [0, 1, 0, 2]
4 1 3 3
After:  [0, 1, 0, 0]

Before: [1, 1, 2, 1]
10 3 2 1
After:  [1, 1, 2, 1]

Before: [1, 1, 0, 1]
13 1 3 0
After:  [1, 1, 0, 1]

Before: [1, 3, 2, 2]
6 0 2 1
After:  [1, 0, 2, 2]

Before: [0, 1, 2, 1]
13 1 3 0
After:  [1, 1, 2, 1]

Before: [0, 1, 1, 3]
11 2 1 1
After:  [0, 2, 1, 3]

Before: [3, 2, 1, 0]
5 2 1 3
After:  [3, 2, 1, 2]

Before: [2, 1, 2, 3]
7 2 3 3
After:  [2, 1, 2, 0]

Before: [1, 1, 1, 1]
11 2 1 2
After:  [1, 1, 2, 1]

Before: [2, 1, 1, 1]
3 0 3 2
After:  [2, 1, 1, 1]

Before: [0, 1, 1, 3]
8 0 0 1
After:  [0, 0, 1, 3]

Before: [3, 2, 3, 3]
7 1 3 3
After:  [3, 2, 3, 0]

Before: [0, 3, 0, 0]
8 0 0 2
After:  [0, 3, 0, 0]

Before: [1, 1, 2, 1]
6 0 2 1
After:  [1, 0, 2, 1]

Before: [0, 1, 1, 2]
4 1 3 0
After:  [0, 1, 1, 2]

Before: [1, 1, 2, 1]
9 1 0 1
After:  [1, 1, 2, 1]

Before: [3, 1, 2, 0]
12 1 2 0
After:  [0, 1, 2, 0]

Before: [1, 3, 0, 3]
1 0 2 2
After:  [1, 3, 0, 3]

Before: [1, 1, 0, 3]
9 1 0 1
After:  [1, 1, 0, 3]

Before: [0, 2, 2, 2]
8 0 0 1
After:  [0, 0, 2, 2]

Before: [0, 1, 1, 1]
13 1 3 1
After:  [0, 1, 1, 1]

Before: [1, 1, 3, 1]
13 1 3 0
After:  [1, 1, 3, 1]

Before: [0, 1, 2, 1]
8 0 0 0
After:  [0, 1, 2, 1]

Before: [2, 1, 2, 1]
12 1 2 2
After:  [2, 1, 0, 1]

Before: [1, 0, 2, 3]
6 0 2 1
After:  [1, 0, 2, 3]

Before: [3, 0, 3, 1]
15 2 0 2
After:  [3, 0, 1, 1]

Before: [0, 1, 1, 1]
0 2 3 0
After:  [0, 1, 1, 1]

Before: [3, 0, 0, 3]
14 0 2 1
After:  [3, 1, 0, 3]

Before: [3, 1, 1, 1]
0 2 3 1
After:  [3, 0, 1, 1]

Before: [0, 1, 2, 3]
7 2 3 3
After:  [0, 1, 2, 0]

Before: [3, 1, 0, 1]
13 1 3 1
After:  [3, 1, 0, 1]

Before: [0, 0, 3, 0]
8 0 0 1
After:  [0, 0, 3, 0]

Before: [1, 1, 0, 2]
1 0 2 3
After:  [1, 1, 0, 0]

Before: [2, 1, 1, 2]
4 1 3 1
After:  [2, 0, 1, 2]

Before: [3, 2, 3, 0]
15 2 2 3
After:  [3, 2, 3, 1]

Before: [0, 2, 0, 3]
7 1 3 0
After:  [0, 2, 0, 3]

Before: [1, 1, 3, 2]
9 1 0 2
After:  [1, 1, 1, 2]

Before: [0, 3, 1, 3]
8 0 0 1
After:  [0, 0, 1, 3]

Before: [3, 1, 2, 1]
2 0 2 0
After:  [1, 1, 2, 1]

Before: [1, 1, 3, 1]
9 1 0 2
After:  [1, 1, 1, 1]

Before: [2, 1, 3, 0]
2 0 1 3
After:  [2, 1, 3, 1]

Before: [2, 1, 1, 0]
11 2 1 1
After:  [2, 2, 1, 0]

Before: [3, 1, 1, 1]
13 1 3 0
After:  [1, 1, 1, 1]

Before: [2, 2, 1, 3]
5 2 1 1
After:  [2, 2, 1, 3]

Before: [0, 0, 2, 1]
10 3 2 3
After:  [0, 0, 2, 1]

Before: [3, 3, 0, 2]
0 3 3 1
After:  [3, 0, 0, 2]

Before: [0, 2, 1, 0]
8 0 0 0
After:  [0, 2, 1, 0]

Before: [3, 3, 0, 2]
15 0 0 3
After:  [3, 3, 0, 1]

Before: [1, 0, 2, 3]
6 0 2 0
After:  [0, 0, 2, 3]

Before: [0, 0, 1, 1]
8 0 0 1
After:  [0, 0, 1, 1]

Before: [1, 0, 2, 1]
10 3 2 0
After:  [1, 0, 2, 1]

Before: [1, 2, 1, 2]
5 2 1 1
After:  [1, 2, 1, 2]

Before: [2, 1, 3, 1]
14 2 0 1
After:  [2, 1, 3, 1]

Before: [2, 1, 2, 0]
2 0 1 0
After:  [1, 1, 2, 0]

Before: [1, 1, 2, 2]
6 0 2 3
After:  [1, 1, 2, 0]

Before: [2, 1, 1, 3]
2 0 1 2
After:  [2, 1, 1, 3]

Before: [2, 3, 3, 2]
14 2 0 2
After:  [2, 3, 1, 2]

Before: [1, 0, 0, 2]
1 0 2 0
After:  [0, 0, 0, 2]

Before: [3, 3, 2, 2]
15 0 0 0
After:  [1, 3, 2, 2]

Before: [0, 1, 1, 2]
4 1 3 3
After:  [0, 1, 1, 0]

Before: [2, 2, 1, 2]
5 2 1 3
After:  [2, 2, 1, 2]

Before: [2, 1, 2, 0]
12 1 2 0
After:  [0, 1, 2, 0]

Before: [3, 1, 0, 1]
13 1 3 2
After:  [3, 1, 1, 1]

Before: [1, 2, 1, 1]
5 2 1 1
After:  [1, 2, 1, 1]

Before: [2, 1, 2, 2]
4 1 3 2
After:  [2, 1, 0, 2]

Before: [0, 1, 0, 2]
4 1 3 0
After:  [0, 1, 0, 2]

Before: [3, 1, 0, 2]
4 1 3 2
After:  [3, 1, 0, 2]

Before: [1, 1, 3, 2]
4 1 3 1
After:  [1, 0, 3, 2]

Before: [3, 1, 1, 1]
13 1 3 2
After:  [3, 1, 1, 1]

Before: [0, 0, 2, 0]
8 0 0 3
After:  [0, 0, 2, 0]

Before: [1, 1, 3, 2]
9 1 0 0
After:  [1, 1, 3, 2]

Before: [3, 2, 1, 0]
5 2 1 1
After:  [3, 2, 1, 0]

Before: [1, 1, 0, 2]
1 0 2 0
After:  [0, 1, 0, 2]

Before: [2, 1, 0, 1]
13 1 3 3
After:  [2, 1, 0, 1]

Before: [3, 1, 2, 0]
12 1 2 2
After:  [3, 1, 0, 0]

Before: [3, 2, 2, 3]
2 0 2 0
After:  [1, 2, 2, 3]

Before: [1, 1, 1, 0]
11 2 1 1
After:  [1, 2, 1, 0]

Before: [0, 0, 1, 2]
8 0 0 3
After:  [0, 0, 1, 0]

Before: [1, 1, 0, 0]
9 1 0 3
After:  [1, 1, 0, 1]

Before: [1, 1, 3, 0]
9 1 0 3
After:  [1, 1, 3, 1]

Before: [1, 1, 1, 1]
11 2 1 1
After:  [1, 2, 1, 1]

Before: [3, 0, 0, 0]
14 0 2 3
After:  [3, 0, 0, 1]

Before: [2, 1, 1, 3]
7 1 3 3
After:  [2, 1, 1, 0]

Before: [0, 3, 3, 2]
8 0 0 2
After:  [0, 3, 0, 2]

Before: [3, 1, 2, 1]
12 1 2 1
After:  [3, 0, 2, 1]

Before: [3, 0, 2, 3]
7 2 3 0
After:  [0, 0, 2, 3]

Before: [3, 1, 1, 1]
14 3 1 1
After:  [3, 0, 1, 1]

Before: [1, 1, 1, 3]
9 1 0 0
After:  [1, 1, 1, 3]

Before: [0, 0, 3, 3]
8 0 0 2
After:  [0, 0, 0, 3]

Before: [3, 1, 3, 3]
7 1 3 1
After:  [3, 0, 3, 3]

Before: [1, 1, 2, 2]
12 1 2 1
After:  [1, 0, 2, 2]

Before: [1, 1, 0, 1]
1 0 2 3
After:  [1, 1, 0, 0]

Before: [2, 2, 2, 1]
3 0 3 2
After:  [2, 2, 1, 1]

Before: [2, 0, 3, 0]
14 0 1 1
After:  [2, 1, 3, 0]

Before: [1, 1, 2, 2]
4 1 3 3
After:  [1, 1, 2, 0]

Before: [1, 1, 2, 3]
12 1 2 2
After:  [1, 1, 0, 3]

Before: [1, 2, 1, 3]
7 2 3 2
After:  [1, 2, 0, 3]

Before: [3, 0, 0, 1]
14 0 2 2
After:  [3, 0, 1, 1]

Before: [3, 2, 1, 0]
5 2 1 0
After:  [2, 2, 1, 0]

Before: [2, 3, 2, 1]
3 0 3 0
After:  [1, 3, 2, 1]

Before: [0, 1, 3, 2]
8 0 0 3
After:  [0, 1, 3, 0]

Before: [2, 2, 1, 1]
3 0 3 3
After:  [2, 2, 1, 1]

Before: [3, 2, 3, 1]
0 3 3 3
After:  [3, 2, 3, 0]

Before: [2, 1, 1, 0]
14 2 1 3
After:  [2, 1, 1, 0]

Before: [2, 2, 1, 3]
7 2 3 1
After:  [2, 0, 1, 3]

Before: [2, 3, 3, 1]
3 0 3 2
After:  [2, 3, 1, 1]

Before: [1, 1, 2, 1]
9 1 0 2
After:  [1, 1, 1, 1]

Before: [0, 3, 2, 1]
10 3 2 1
After:  [0, 1, 2, 1]

Before: [0, 1, 0, 1]
13 1 3 3
After:  [0, 1, 0, 1]

Before: [1, 1, 1, 3]
11 2 1 3
After:  [1, 1, 1, 2]

Before: [3, 1, 1, 2]
11 2 1 2
After:  [3, 1, 2, 2]

Before: [1, 3, 2, 3]
6 0 2 3
After:  [1, 3, 2, 0]

Before: [0, 1, 2, 3]
8 0 0 2
After:  [0, 1, 0, 3]

Before: [3, 0, 1, 3]
14 3 0 2
After:  [3, 0, 1, 3]

Before: [2, 1, 2, 0]
12 1 2 3
After:  [2, 1, 2, 0]

Before: [0, 1, 1, 1]
11 2 1 0
After:  [2, 1, 1, 1]

Before: [2, 3, 2, 1]
0 3 3 2
After:  [2, 3, 0, 1]

Before: [1, 1, 0, 2]
0 3 3 3
After:  [1, 1, 0, 0]

Before: [1, 0, 0, 1]
1 0 2 3
After:  [1, 0, 0, 0]

Before: [3, 2, 1, 3]
7 2 3 3
After:  [3, 2, 1, 0]

Before: [3, 1, 1, 3]
11 2 1 2
After:  [3, 1, 2, 3]

Before: [0, 1, 2, 2]
12 1 2 3
After:  [0, 1, 2, 0]

Before: [3, 3, 2, 1]
10 3 2 0
After:  [1, 3, 2, 1]

Before: [1, 1, 3, 1]
13 1 3 1
After:  [1, 1, 3, 1]

Before: [2, 2, 1, 1]
3 0 3 1
After:  [2, 1, 1, 1]

Before: [2, 1, 2, 2]
4 1 3 0
After:  [0, 1, 2, 2]

Before: [1, 1, 1, 1]
9 1 0 2
After:  [1, 1, 1, 1]

Before: [1, 3, 2, 1]
10 3 2 0
After:  [1, 3, 2, 1]

Before: [2, 0, 2, 1]
10 3 2 0
After:  [1, 0, 2, 1]

Before: [1, 1, 0, 3]
1 0 2 2
After:  [1, 1, 0, 3]

Before: [1, 2, 0, 1]
1 0 2 3
After:  [1, 2, 0, 0]

Before: [1, 3, 0, 0]
1 0 2 0
After:  [0, 3, 0, 0]

Before: [2, 1, 1, 3]
14 2 1 0
After:  [0, 1, 1, 3]

Before: [1, 1, 1, 2]
9 1 0 1
After:  [1, 1, 1, 2]

Before: [1, 1, 0, 1]
13 1 3 1
After:  [1, 1, 0, 1]

Before: [2, 0, 0, 2]
15 0 0 0
After:  [1, 0, 0, 2]

Before: [2, 3, 1, 1]
3 0 3 0
After:  [1, 3, 1, 1]

Before: [0, 1, 2, 0]
12 1 2 3
After:  [0, 1, 2, 0]

Before: [1, 2, 1, 2]
5 2 1 0
After:  [2, 2, 1, 2]

Before: [2, 0, 2, 2]
14 3 2 2
After:  [2, 0, 0, 2]

Before: [0, 2, 2, 1]
10 3 2 0
After:  [1, 2, 2, 1]

Before: [2, 1, 0, 2]
4 1 3 3
After:  [2, 1, 0, 0]

Before: [1, 3, 0, 2]
1 0 2 2
After:  [1, 3, 0, 2]

Before: [0, 0, 2, 3]
8 0 0 1
After:  [0, 0, 2, 3]

Before: [2, 1, 1, 3]
7 1 3 0
After:  [0, 1, 1, 3]

Before: [3, 1, 2, 1]
13 1 3 0
After:  [1, 1, 2, 1]

Before: [2, 0, 1, 1]
3 0 3 1
After:  [2, 1, 1, 1]

Before: [1, 1, 2, 1]
13 1 3 1
After:  [1, 1, 2, 1]

Before: [0, 1, 2, 1]
12 1 2 1
After:  [0, 0, 2, 1]

Before: [2, 2, 3, 3]
14 3 2 2
After:  [2, 2, 1, 3]

Before: [3, 1, 1, 1]
13 1 3 3
After:  [3, 1, 1, 1]

Before: [3, 3, 3, 2]
15 2 0 1
After:  [3, 1, 3, 2]

Before: [2, 1, 2, 1]
3 0 3 1
After:  [2, 1, 2, 1]

Before: [3, 1, 2, 0]
2 0 2 3
After:  [3, 1, 2, 1]

Before: [1, 2, 1, 2]
5 2 1 3
After:  [1, 2, 1, 2]

Before: [3, 2, 1, 1]
5 2 1 0
After:  [2, 2, 1, 1]

Before: [0, 1, 2, 1]
12 1 2 0
After:  [0, 1, 2, 1]

Before: [2, 1, 1, 1]
3 0 3 0
After:  [1, 1, 1, 1]

Before: [3, 1, 1, 2]
11 2 1 1
After:  [3, 2, 1, 2]

Before: [1, 1, 1, 3]
11 2 1 0
After:  [2, 1, 1, 3]

Before: [1, 1, 2, 0]
9 1 0 3
After:  [1, 1, 2, 1]

Before: [0, 2, 2, 3]
8 0 0 3
After:  [0, 2, 2, 0]

Before: [0, 0, 2, 1]
10 3 2 1
After:  [0, 1, 2, 1]

Before: [0, 2, 3, 3]
14 3 2 0
After:  [1, 2, 3, 3]

Before: [2, 1, 0, 3]
2 0 1 2
After:  [2, 1, 1, 3]

Before: [3, 1, 2, 0]
2 0 2 0
After:  [1, 1, 2, 0]

Before: [3, 1, 0, 2]
14 0 2 0
After:  [1, 1, 0, 2]

Before: [2, 1, 3, 0]
2 0 1 1
After:  [2, 1, 3, 0]

Before: [1, 1, 1, 0]
9 1 0 3
After:  [1, 1, 1, 1]

Before: [1, 0, 0, 0]
1 0 2 1
After:  [1, 0, 0, 0]

Before: [0, 3, 2, 2]
8 0 0 2
After:  [0, 3, 0, 2]

Before: [3, 3, 2, 2]
2 0 2 0
After:  [1, 3, 2, 2]

Before: [0, 2, 1, 2]
5 2 1 1
After:  [0, 2, 1, 2]

Before: [3, 3, 2, 2]
2 0 2 3
After:  [3, 3, 2, 1]

Before: [0, 2, 1, 2]
5 2 1 0
After:  [2, 2, 1, 2]

Before: [1, 0, 2, 1]
10 3 2 2
After:  [1, 0, 1, 1]

Before: [0, 1, 0, 1]
13 1 3 2
After:  [0, 1, 1, 1]

Before: [3, 1, 1, 1]
14 2 1 1
After:  [3, 0, 1, 1]

Before: [0, 1, 0, 1]
13 1 3 1
After:  [0, 1, 0, 1]

Before: [2, 2, 0, 1]
3 0 3 2
After:  [2, 2, 1, 1]

Before: [3, 2, 1, 3]
14 3 0 0
After:  [1, 2, 1, 3]

Before: [1, 1, 2, 2]
4 1 3 0
After:  [0, 1, 2, 2]

Before: [3, 1, 2, 3]
7 1 3 1
After:  [3, 0, 2, 3]

Before: [3, 0, 3, 0]
15 2 2 1
After:  [3, 1, 3, 0]

Before: [0, 2, 2, 2]
14 2 1 3
After:  [0, 2, 2, 1]

Before: [1, 1, 2, 3]
12 1 2 1
After:  [1, 0, 2, 3]

Before: [3, 1, 1, 1]
13 1 3 1
After:  [3, 1, 1, 1]

Before: [2, 1, 1, 1]
13 1 3 3
After:  [2, 1, 1, 1]

Before: [2, 2, 2, 3]
7 2 3 3
After:  [2, 2, 2, 0]

Before: [2, 3, 3, 3]
15 0 0 2
After:  [2, 3, 1, 3]

Before: [3, 1, 2, 1]
13 1 3 3
After:  [3, 1, 2, 1]

Before: [3, 3, 3, 2]
15 0 2 0
After:  [1, 3, 3, 2]

Before: [3, 1, 0, 2]
0 3 3 0
After:  [0, 1, 0, 2]

Before: [2, 0, 3, 2]
14 0 1 1
After:  [2, 1, 3, 2]

Before: [1, 0, 2, 1]
10 3 2 3
After:  [1, 0, 2, 1]

Before: [1, 3, 3, 1]
0 3 3 2
After:  [1, 3, 0, 1]

Before: [0, 2, 2, 1]
10 3 2 2
After:  [0, 2, 1, 1]

Before: [2, 2, 1, 0]
5 2 1 0
After:  [2, 2, 1, 0]

Before: [2, 3, 0, 1]
3 0 3 2
After:  [2, 3, 1, 1]

Before: [1, 2, 2, 2]
6 0 2 1
After:  [1, 0, 2, 2]

Before: [0, 1, 2, 2]
12 1 2 0
After:  [0, 1, 2, 2]

Before: [1, 1, 0, 2]
9 1 0 2
After:  [1, 1, 1, 2]

Before: [0, 1, 2, 2]
12 1 2 2
After:  [0, 1, 0, 2]

Before: [2, 1, 2, 0]
12 1 2 2
After:  [2, 1, 0, 0]

Before: [2, 3, 3, 0]
15 0 0 3
After:  [2, 3, 3, 1]

Before: [2, 2, 0, 1]
15 0 0 0
After:  [1, 2, 0, 1]

Before: [2, 0, 3, 2]
0 3 3 2
After:  [2, 0, 0, 2]

Before: [3, 0, 3, 2]
15 2 2 3
After:  [3, 0, 3, 1]

Before: [2, 3, 2, 1]
10 3 2 3
After:  [2, 3, 2, 1]

Before: [2, 1, 2, 1]
3 0 3 3
After:  [2, 1, 2, 1]

Before: [1, 3, 0, 0]
1 0 2 3
After:  [1, 3, 0, 0]

Before: [3, 1, 2, 3]
2 0 2 3
After:  [3, 1, 2, 1]

Before: [2, 1, 1, 2]
11 2 1 2
After:  [2, 1, 2, 2]

Before: [1, 3, 2, 3]
7 2 3 1
After:  [1, 0, 2, 3]

Before: [0, 0, 0, 0]
8 0 0 3
After:  [0, 0, 0, 0]

Before: [1, 0, 3, 1]
14 2 3 2
After:  [1, 0, 0, 1]

Before: [3, 2, 0, 3]
14 0 2 3
After:  [3, 2, 0, 1]

Before: [3, 2, 2, 1]
2 0 2 1
After:  [3, 1, 2, 1]

Before: [2, 1, 2, 1]
3 0 3 0
After:  [1, 1, 2, 1]

Before: [2, 2, 0, 1]
3 0 3 3
After:  [2, 2, 0, 1]

Before: [0, 3, 3, 2]
8 0 0 0
After:  [0, 3, 3, 2]

Before: [3, 2, 0, 1]
14 0 2 1
After:  [3, 1, 0, 1]

Before: [1, 1, 1, 3]
9 1 0 1
After:  [1, 1, 1, 3]

Before: [0, 1, 0, 1]
13 1 3 0
After:  [1, 1, 0, 1]

Before: [1, 1, 1, 0]
9 1 0 1
After:  [1, 1, 1, 0]

Before: [1, 3, 2, 2]
6 0 2 0
After:  [0, 3, 2, 2]

Before: [2, 1, 1, 1]
14 3 1 0
After:  [0, 1, 1, 1]

Before: [1, 1, 3, 0]
9 1 0 0
After:  [1, 1, 3, 0]

Before: [2, 1, 3, 1]
3 0 3 2
After:  [2, 1, 1, 1]

Before: [2, 1, 1, 1]
3 0 3 1
After:  [2, 1, 1, 1]

Before: [3, 2, 1, 3]
7 1 3 3
After:  [3, 2, 1, 0]

Before: [2, 0, 3, 3]
15 0 0 2
After:  [2, 0, 1, 3]

Before: [3, 0, 2, 1]
10 3 2 2
After:  [3, 0, 1, 1]

Before: [1, 1, 2, 3]
9 1 0 0
After:  [1, 1, 2, 3]

Before: [1, 2, 1, 1]
5 2 1 0
After:  [2, 2, 1, 1]

Before: [0, 1, 2, 3]
12 1 2 1
After:  [0, 0, 2, 3]

Before: [1, 3, 0, 1]
1 0 2 3
After:  [1, 3, 0, 0]

Before: [2, 1, 0, 1]
2 0 1 0
After:  [1, 1, 0, 1]

Before: [3, 2, 2, 3]
2 0 2 1
After:  [3, 1, 2, 3]

Before: [1, 2, 0, 1]
1 0 2 1
After:  [1, 0, 0, 1]

Before: [1, 2, 2, 0]
6 0 2 0
After:  [0, 2, 2, 0]

Before: [2, 1, 1, 2]
11 2 1 0
After:  [2, 1, 1, 2]

Before: [3, 1, 1, 3]
7 1 3 2
After:  [3, 1, 0, 3]

Before: [2, 2, 1, 3]
5 2 1 2
After:  [2, 2, 2, 3]

Before: [3, 1, 1, 1]
11 2 1 1
After:  [3, 2, 1, 1]

Before: [2, 1, 2, 2]
4 1 3 1
After:  [2, 0, 2, 2]

Before: [1, 1, 2, 1]
12 1 2 0
After:  [0, 1, 2, 1]

Before: [1, 1, 0, 2]
9 1 0 3
After:  [1, 1, 0, 1]

Before: [3, 3, 2, 3]
2 0 2 0
After:  [1, 3, 2, 3]

Before: [1, 1, 2, 3]
9 1 0 3
After:  [1, 1, 2, 1]

Before: [2, 1, 2, 1]
12 1 2 0
After:  [0, 1, 2, 1]

Before: [1, 1, 0, 2]
4 1 3 1
After:  [1, 0, 0, 2]

Before: [1, 2, 2, 0]
6 0 2 3
After:  [1, 2, 2, 0]

Before: [2, 1, 1, 0]
11 2 1 3
After:  [2, 1, 1, 2]

Before: [1, 1, 0, 1]
13 1 3 3
After:  [1, 1, 0, 1]

Before: [3, 1, 2, 3]
7 1 3 3
After:  [3, 1, 2, 0]

Before: [0, 2, 1, 3]
8 0 0 0
After:  [0, 2, 1, 3]

Before: [3, 2, 1, 3]
7 1 3 0
After:  [0, 2, 1, 3]

Before: [1, 2, 2, 2]
6 0 2 3
After:  [1, 2, 2, 0]

Before: [1, 1, 1, 1]
13 1 3 3
After:  [1, 1, 1, 1]

Before: [2, 1, 3, 2]
15 2 2 1
After:  [2, 1, 3, 2]

Before: [2, 1, 0, 3]
2 0 1 0
After:  [1, 1, 0, 3]

Before: [1, 1, 2, 1]
12 1 2 2
After:  [1, 1, 0, 1]

Before: [1, 1, 3, 2]
4 1 3 0
After:  [0, 1, 3, 2]

Before: [2, 3, 2, 3]
7 2 3 2
After:  [2, 3, 0, 3]

Before: [2, 2, 1, 1]
5 2 1 3
After:  [2, 2, 1, 2]

Before: [0, 0, 2, 1]
10 3 2 0
After:  [1, 0, 2, 1]

Before: [3, 1, 0, 3]
7 1 3 2
After:  [3, 1, 0, 3]

Before: [2, 1, 3, 2]
2 0 1 1
After:  [2, 1, 3, 2]

Before: [2, 3, 1, 1]
3 0 3 1
After:  [2, 1, 1, 1]

Before: [2, 2, 1, 3]
7 1 3 3
After:  [2, 2, 1, 0]

Before: [3, 3, 3, 1]
15 0 2 1
After:  [3, 1, 3, 1]

Before: [0, 1, 1, 0]
11 2 1 1
After:  [0, 2, 1, 0]

Before: [1, 1, 1, 0]
11 2 1 0
After:  [2, 1, 1, 0]

Before: [3, 1, 3, 1]
13 1 3 3
After:  [3, 1, 3, 1]

Before: [0, 1, 3, 2]
8 0 0 1
After:  [0, 0, 3, 2]

Before: [2, 2, 0, 3]
7 1 3 3
After:  [2, 2, 0, 0]

Before: [1, 0, 2, 1]
6 0 2 2
After:  [1, 0, 0, 1]

Before: [1, 3, 0, 2]
0 3 3 3
After:  [1, 3, 0, 0]

Before: [1, 1, 0, 1]
9 1 0 3
After:  [1, 1, 0, 1]

Before: [1, 2, 2, 3]
7 1 3 1
After:  [1, 0, 2, 3]

Before: [1, 1, 2, 2]
12 1 2 3
After:  [1, 1, 2, 0]

Before: [1, 1, 2, 0]
12 1 2 3
After:  [1, 1, 2, 0]

Before: [0, 1, 0, 2]
4 1 3 1
After:  [0, 0, 0, 2]

Before: [1, 1, 1, 0]
9 1 0 0
After:  [1, 1, 1, 0]

Before: [1, 1, 2, 0]
9 1 0 0
After:  [1, 1, 2, 0]

Before: [1, 2, 1, 1]
5 2 1 3
After:  [1, 2, 1, 2]

Before: [3, 0, 3, 2]
15 2 2 0
After:  [1, 0, 3, 2]

Before: [2, 2, 1, 3]
7 2 3 3
After:  [2, 2, 1, 0]

Before: [3, 1, 2, 2]
4 1 3 3
After:  [3, 1, 2, 0]

Before: [3, 1, 2, 1]
15 0 0 1
After:  [3, 1, 2, 1]

Before: [2, 3, 2, 1]
10 3 2 0
After:  [1, 3, 2, 1]

Before: [2, 1, 2, 2]
0 3 3 1
After:  [2, 0, 2, 2]

Before: [1, 2, 0, 2]
1 0 2 1
After:  [1, 0, 0, 2]

Before: [3, 3, 2, 0]
2 0 2 0
After:  [1, 3, 2, 0]

Before: [0, 1, 1, 2]
11 2 1 1
After:  [0, 2, 1, 2]

Before: [3, 1, 2, 1]
13 1 3 1
After:  [3, 1, 2, 1]

Before: [3, 1, 3, 3]
15 2 0 3
After:  [3, 1, 3, 1]

Before: [0, 1, 0, 1]
8 0 0 3
After:  [0, 1, 0, 0]

Before: [2, 3, 2, 1]
0 3 3 3
After:  [2, 3, 2, 0]

Before: [2, 1, 1, 2]
4 1 3 2
After:  [2, 1, 0, 2]

Before: [0, 1, 3, 1]
13 1 3 0
After:  [1, 1, 3, 1]

Before: [2, 2, 1, 1]
5 2 1 0
After:  [2, 2, 1, 1]

Before: [3, 1, 2, 0]
15 0 0 0
After:  [1, 1, 2, 0]

Before: [1, 1, 1, 1]
9 1 0 0
After:  [1, 1, 1, 1]

Before: [1, 1, 2, 2]
12 1 2 2
After:  [1, 1, 0, 2]

Before: [1, 1, 2, 1]
10 3 2 0
After:  [1, 1, 2, 1]

Before: [2, 0, 1, 1]
3 0 3 3
After:  [2, 0, 1, 1]



8 0 0 2
5 2 2 2
6 3 1 1
8 0 0 3
5 3 0 3
9 2 3 1
8 1 3 1
8 1 2 1
11 0 1 0
10 0 0 1
8 0 0 2
5 2 3 2
6 3 0 3
8 2 0 0
5 0 1 0
12 3 2 0
8 0 3 0
11 1 0 1
10 1 1 3
6 1 2 0
8 1 0 1
5 1 0 1
6 0 0 2
5 0 1 1
8 1 3 1
11 3 1 3
10 3 3 2
6 3 0 3
6 2 1 1
4 3 1 0
8 0 3 0
11 0 2 2
10 2 0 1
8 1 0 0
5 0 1 0
8 3 0 2
5 2 0 2
6 2 1 3
8 0 2 3
8 3 3 3
8 3 3 3
11 3 1 1
10 1 1 3
6 3 3 2
6 0 0 1
8 0 2 0
8 0 1 0
8 0 1 0
11 3 0 3
10 3 0 0
6 2 2 1
6 1 0 3
13 1 2 1
8 1 2 1
11 1 0 0
10 0 3 2
6 2 0 0
6 3 1 1
6 2 0 3
9 0 3 1
8 1 1 1
8 1 3 1
11 1 2 2
10 2 0 0
6 3 1 1
6 2 1 2
6 0 2 3
7 3 2 1
8 1 2 1
8 1 2 1
11 1 0 0
10 0 3 3
6 3 1 2
6 1 2 0
6 0 1 1
6 2 1 1
8 1 1 1
11 3 1 3
10 3 3 1
6 2 0 3
8 0 0 2
5 2 0 2
6 2 2 0
15 0 3 0
8 0 1 0
8 0 2 0
11 0 1 1
10 1 0 0
6 3 1 1
6 0 0 3
6 2 1 2
7 3 2 2
8 2 2 2
11 2 0 0
10 0 1 1
6 1 0 3
8 0 0 0
5 0 2 0
8 0 0 2
5 2 0 2
3 0 3 2
8 2 3 2
8 2 2 2
11 1 2 1
10 1 1 3
8 2 0 2
5 2 3 2
6 3 2 0
6 1 3 1
8 1 2 2
8 2 1 2
11 2 3 3
6 1 1 0
8 1 0 2
5 2 0 2
6 2 0 0
8 0 3 0
11 3 0 3
10 3 3 2
6 2 1 0
6 2 2 3
6 0 1 1
9 0 3 0
8 0 1 0
8 0 1 0
11 0 2 2
10 2 3 3
6 3 1 1
8 3 0 2
5 2 1 2
6 1 3 0
5 0 1 0
8 0 2 0
11 0 3 3
10 3 0 0
6 1 3 3
8 0 0 2
5 2 0 2
6 0 1 1
5 3 1 2
8 2 2 2
11 0 2 0
10 0 2 3
6 2 1 1
6 2 0 2
6 3 3 0
13 1 0 1
8 1 2 1
11 1 3 3
10 3 2 2
6 1 1 3
6 0 2 1
6 0 2 0
5 3 1 3
8 3 2 3
11 2 3 2
10 2 3 3
6 1 3 0
6 0 0 2
6 3 1 1
8 0 2 1
8 1 3 1
11 3 1 3
10 3 0 1
6 2 2 0
6 2 1 3
6 3 3 2
9 0 3 2
8 2 2 2
8 2 3 2
11 2 1 1
10 1 3 3
6 0 3 2
8 2 0 1
5 1 3 1
2 0 1 1
8 1 1 1
11 1 3 3
10 3 3 2
6 3 0 1
6 1 1 3
3 0 3 1
8 1 3 1
11 2 1 2
10 2 1 0
6 2 1 1
6 1 3 2
6 2 0 3
9 1 3 3
8 3 3 3
11 3 0 0
10 0 3 2
6 2 3 0
6 1 0 3
3 0 3 3
8 3 2 3
11 2 3 2
10 2 2 1
6 1 2 3
6 2 3 2
3 0 3 2
8 2 1 2
8 2 2 2
11 2 1 1
10 1 0 2
6 3 2 1
6 3 3 0
11 3 3 1
8 1 2 1
8 1 2 1
11 2 1 2
6 2 1 1
13 1 0 0
8 0 3 0
8 0 1 0
11 0 2 2
10 2 1 3
8 1 0 0
5 0 1 0
6 0 0 2
8 0 2 0
8 0 3 0
8 0 2 0
11 3 0 3
10 3 3 1
6 3 3 2
6 2 1 0
6 2 2 3
15 0 3 3
8 3 2 3
11 3 1 1
10 1 3 2
6 3 1 1
8 2 0 3
5 3 0 3
4 1 0 1
8 1 2 1
11 2 1 2
10 2 0 1
6 3 1 2
6 1 1 3
6 1 0 0
8 3 2 3
8 3 2 3
11 1 3 1
10 1 2 3
6 2 1 1
6 2 1 2
10 0 2 2
8 2 1 2
11 3 2 3
6 0 0 1
6 2 1 2
10 0 2 0
8 0 3 0
11 3 0 3
10 3 1 2
8 2 0 3
5 3 0 3
6 1 1 0
11 0 0 3
8 3 2 3
11 3 2 2
10 2 3 3
6 2 1 1
6 3 2 2
6 2 2 0
0 0 2 0
8 0 1 0
11 0 3 3
10 3 3 2
6 2 1 3
6 2 0 0
6 3 0 1
15 0 3 3
8 3 1 3
8 3 3 3
11 3 2 2
10 2 3 1
8 1 0 0
5 0 1 0
6 3 3 2
8 0 0 3
5 3 1 3
8 0 2 3
8 3 1 3
11 3 1 1
10 1 1 3
6 0 0 1
6 2 1 2
10 0 2 1
8 1 2 1
11 1 3 3
10 3 2 0
8 0 0 2
5 2 0 2
6 3 2 3
6 3 3 1
12 3 2 1
8 1 1 1
11 1 0 0
10 0 0 3
6 3 2 1
6 2 2 0
6 3 3 2
0 0 2 0
8 0 3 0
8 0 1 0
11 0 3 3
10 3 3 2
6 1 3 1
8 1 0 3
5 3 2 3
8 0 0 0
5 0 2 0
1 1 3 0
8 0 1 0
11 2 0 2
6 3 1 1
6 2 0 0
4 1 0 1
8 1 2 1
11 2 1 2
10 2 1 1
6 3 0 2
6 1 2 3
3 0 3 0
8 0 1 0
11 0 1 1
8 3 0 2
5 2 2 2
6 2 0 3
8 2 0 0
5 0 3 0
2 2 0 2
8 2 1 2
11 1 2 1
10 1 2 0
6 3 0 1
8 0 0 2
5 2 0 2
14 2 3 1
8 1 2 1
11 1 0 0
10 0 2 3
6 3 3 2
6 1 3 0
8 3 0 1
5 1 1 1
11 1 0 2
8 2 3 2
8 2 3 2
11 3 2 3
10 3 3 1
6 2 2 2
6 0 2 3
7 3 2 0
8 0 2 0
8 0 2 0
11 1 0 1
10 1 2 3
6 3 2 0
8 2 0 2
5 2 0 2
8 1 0 1
5 1 3 1
0 2 0 0
8 0 3 0
11 0 3 3
10 3 2 1
6 1 1 0
8 3 0 3
5 3 0 3
6 1 1 2
6 3 0 2
8 2 2 2
11 2 1 1
10 1 3 2
8 3 0 1
5 1 1 1
6 3 1 3
11 0 0 1
8 1 1 1
11 1 2 2
10 2 1 1
6 1 2 2
6 3 2 0
6 2 1 3
12 0 2 0
8 0 3 0
11 1 0 1
10 1 1 2
6 2 3 0
8 2 0 3
5 3 1 3
6 3 1 1
5 3 1 3
8 3 2 3
11 2 3 2
6 0 3 3
6 3 0 0
8 2 0 1
5 1 2 1
13 1 0 0
8 0 1 0
11 0 2 2
10 2 3 1
6 2 1 2
6 1 2 3
6 3 0 0
2 2 0 3
8 3 3 3
11 1 3 1
10 1 2 3
6 1 3 0
6 3 0 1
5 0 1 1
8 1 2 1
11 1 3 3
10 3 3 2
6 1 3 3
8 0 0 1
5 1 0 1
8 1 0 0
5 0 2 0
3 0 3 1
8 1 2 1
11 2 1 2
6 1 1 0
6 0 2 1
8 3 0 3
5 3 2 3
5 0 1 0
8 0 3 0
8 0 2 0
11 0 2 2
6 1 0 1
6 2 1 0
15 0 3 3
8 3 3 3
11 3 2 2
6 0 2 1
6 2 3 3
6 3 3 0
4 0 3 1
8 1 2 1
11 2 1 2
6 0 1 3
8 0 0 0
5 0 2 0
8 3 0 1
5 1 2 1
6 3 0 0
8 0 2 0
8 0 3 0
11 0 2 2
10 2 1 3
8 2 0 2
5 2 3 2
6 1 1 1
6 2 3 0
1 1 0 2
8 2 3 2
11 3 2 3
10 3 0 0
6 0 3 3
8 3 0 1
5 1 0 1
6 2 0 2
7 3 2 2
8 2 2 2
11 0 2 0
10 0 0 3
6 2 3 1
8 3 0 2
5 2 0 2
6 3 3 0
13 1 0 2
8 2 3 2
8 2 3 2
11 3 2 3
8 3 0 2
5 2 2 2
2 2 0 1
8 1 3 1
11 3 1 3
10 3 2 2
6 1 3 3
6 3 3 1
6 2 0 0
3 0 3 3
8 3 3 3
11 3 2 2
10 2 2 0
6 1 1 1
6 2 1 3
8 2 0 2
5 2 0 2
14 2 3 2
8 2 2 2
8 2 3 2
11 2 0 0
10 0 1 3
6 3 0 1
6 3 2 2
6 2 0 0
6 2 1 0
8 0 2 0
11 3 0 3
10 3 0 1
6 0 1 3
6 2 3 2
8 3 0 0
5 0 0 0
7 3 2 3
8 3 3 3
8 3 2 3
11 1 3 1
10 1 1 2
6 2 0 0
8 0 0 3
5 3 1 3
6 1 1 1
1 3 0 1
8 1 2 1
11 1 2 2
10 2 2 3
6 0 2 1
6 3 1 0
6 2 2 2
2 2 0 1
8 1 3 1
8 1 1 1
11 1 3 3
10 3 1 2
6 1 0 1
6 2 2 3
1 1 3 0
8 0 2 0
8 0 3 0
11 2 0 2
10 2 3 1
6 2 0 0
6 2 0 2
15 0 3 3
8 3 1 3
11 3 1 1
10 1 0 3
6 3 0 2
6 1 3 1
0 0 2 2
8 2 3 2
11 3 2 3
10 3 3 0
6 1 2 3
6 3 1 1
6 0 0 2
12 1 2 3
8 3 3 3
8 3 3 3
11 3 0 0
10 0 3 1
8 1 0 0
5 0 2 0
8 1 0 2
5 2 2 2
6 0 2 3
7 3 2 2
8 2 2 2
11 2 1 1
10 1 2 3
6 2 3 1
6 3 1 2
13 1 2 0
8 0 3 0
11 0 3 3
10 3 1 1
6 1 2 3
6 3 1 0
8 3 2 0
8 0 1 0
11 1 0 1
6 1 3 0
6 2 2 2
6 3 0 3
10 0 2 0
8 0 1 0
11 0 1 1
6 3 0 0
2 2 0 3
8 3 1 3
11 3 1 1
10 1 2 3
6 1 2 2
8 1 0 1
5 1 1 1
6 2 2 0
1 1 0 0
8 0 3 0
11 3 0 3
10 3 1 0
6 2 0 3
6 2 0 2
1 1 3 3
8 3 1 3
11 3 0 0
10 0 0 3
6 3 2 1
8 3 0 0
5 0 2 0
6 3 2 2
0 0 2 2
8 2 1 2
11 3 2 3
10 3 3 0
8 0 0 3
5 3 0 3
6 1 3 1
6 2 2 2
7 3 2 3
8 3 1 3
8 3 3 3
11 0 3 0
10 0 0 3
6 3 2 2
6 2 1 0
0 0 2 1
8 1 3 1
8 1 2 1
11 1 3 3
8 1 0 2
5 2 2 2
6 3 1 0
6 3 3 1
2 2 1 1
8 1 1 1
11 3 1 3
10 3 1 1
6 3 2 3
6 2 0 0
6 3 0 2
13 0 2 3
8 3 3 3
8 3 1 3
11 3 1 1
10 1 2 3
8 2 0 0
5 0 1 0
8 3 0 2
5 2 2 2
8 0 0 1
5 1 0 1
11 0 0 1
8 1 2 1
11 3 1 3
10 3 3 0
6 0 1 3
6 3 3 1
6 3 1 2
14 3 2 3
8 3 3 3
11 0 3 0
10 0 2 3
6 1 0 1
6 1 0 0
6 2 3 2
10 0 2 1
8 1 1 1
8 1 2 1
11 1 3 3
6 1 1 2
6 0 3 1
5 0 1 2
8 2 3 2
11 3 2 3
10 3 2 1
8 3 0 3
5 3 2 3
8 1 0 0
5 0 2 0
6 3 3 2
0 0 2 3
8 3 2 3
8 3 1 3
11 3 1 1
10 1 2 0
6 1 3 3
8 3 0 2
5 2 1 2
6 0 1 1
5 3 1 3
8 3 1 3
11 0 3 0
10 0 3 1
6 1 3 0
6 0 3 3
8 1 0 2
5 2 2 2
7 3 2 3
8 3 2 3
11 3 1 1
6 0 0 2
6 2 3 3
6 0 3 0
14 2 3 0
8 0 1 0
8 0 1 0
11 0 1 1
10 1 0 3
6 3 0 2
6 2 2 0
6 2 0 1
13 0 2 1
8 1 3 1
8 1 1 1
11 3 1 3
10 3 2 1
6 3 1 3
6 1 2 0
8 0 2 2
8 2 3 2
11 1 2 1
6 1 0 3
6 3 3 2
11 0 0 2
8 2 2 2
11 1 2 1
10 1 2 3
6 0 3 0
8 1 0 2
5 2 2 2
6 3 2 1
2 2 1 2
8 2 1 2
11 3 2 3
6 0 0 2
6 1 3 1
8 1 2 0
8 0 3 0
8 0 2 0
11 0 3 3
10 3 2 2
6 2 2 0
6 1 2 3
3 0 3 0
8 0 2 0
11 2 0 2
10 2 3 3
6 0 0 1
6 2 0 0
6 3 3 2
0 0 2 2
8 2 1 2
8 2 1 2
11 2 3 3
10 3 1 0
6 2 3 1
6 2 3 3
8 3 0 2
5 2 0 2
14 2 3 2
8 2 3 2
11 2 0 0
10 0 0 1
8 2 0 2
5 2 0 2
6 1 0 0
1 0 3 3
8 3 2 3
8 3 2 3
11 1 3 1
6 2 2 0
6 2 0 3
6 1 3 2
15 0 3 0
8 0 2 0
11 1 0 1
10 1 2 0
6 3 0 1
6 0 3 2
6 1 1 3
12 1 2 1
8 1 1 1
11 1 0 0
10 0 1 2
6 3 1 1
6 3 1 0
5 3 1 3
8 3 3 3
11 2 3 2
10 2 1 0
6 0 2 3
6 2 3 2
8 1 0 1
5 1 0 1
7 3 2 3
8 3 1 3
8 3 1 3
11 3 0 0
6 2 1 1
6 3 0 2
6 0 1 3
14 3 2 2
8 2 2 2
11 2 0 0
8 2 0 2
5 2 0 2
6 1 1 3
6 2 3 1
8 1 3 1
11 0 1 0
10 0 0 1
6 2 1 0
6 3 1 2
1 3 0 0
8 0 2 0
11 0 1 1
10 1 3 0
6 0 3 3
6 1 1 1
14 3 2 1
8 1 2 1
11 0 1 0
10 0 3 2
6 2 3 1
6 2 0 0
6 1 1 3
1 3 0 0
8 0 1 0
11 0 2 2
10 2 0 1
6 0 2 2
6 2 0 3
6 2 1 0
15 0 3 3
8 3 2 3
8 3 3 3
11 1 3 1
6 3 0 0
8 0 0 3
5 3 1 3
11 3 3 3
8 3 1 3
8 3 1 3
11 3 1 1
10 1 3 3
6 2 0 0
6 3 0 2
8 0 0 1
5 1 2 1
0 0 2 1
8 1 1 1
8 1 1 1
11 1 3 3
10 3 1 2
6 2 0 1
6 2 3 3
15 0 3 1
8 1 1 1
8 1 1 1
11 1 2 2
10 2 1 0
6 0 2 3
6 3 3 1
6 2 1 2
7 3 2 3
8 3 3 3
11 3 0 0
10 0 0 2
6 2 3 0
6 0 0 3
2 0 1 3
8 3 3 3
11 2 3 2
10 2 2 3
6 1 0 2
6 1 0 1
1 1 0 1
8 1 1 1
11 3 1 3
8 1 0 1
5 1 3 1
2 0 1 1
8 1 3 1
11 3 1 3
10 3 0 2
6 2 2 3
8 2 0 1
5 1 3 1
15 0 3 1
8 1 1 1
8 1 1 1
11 2 1 2
10 2 3 0
6 3 0 1
6 1 0 3
6 2 1 2
2 2 1 2
8 2 2 2
8 2 3 2
11 0 2 0
10 0 0 1
6 1 0 0
6 2 2 2
10 0 2 2
8 2 1 2
11 1 2 1
10 1 2 0
6 3 1 1
6 2 0 3
6 2 0 2
9 2 3 2
8 2 1 2
11 0 2 0
10 0 2 2
6 1 1 3
6 0 2 1
8 0 0 0
5 0 1 0
5 3 1 3
8 3 2 3
11 3 2 2
10 2 0 1
6 1 2 2
6 2 1 3
1 0 3 2
8 2 2 2
11 2 1 1
6 3 3 2
6 2 3 0
15 0 3 2
8 2 2 2
11 2 1 1
10 1 2 0
6 2 1 1
6 3 3 2
9 1 3 3
8 3 3 3
11 0 3 0
10 0 2 1
6 0 0 2
6 2 0 0
6 1 1 3
1 3 0 2
8 2 1 2
8 2 1 2
11 2 1 1
10 1 0 0
6 2 2 1
6 0 0 3
6 3 2 2
13 1 2 2
8 2 3 2
11 0 2 0
10 0 0 2
6 2 2 3
8 2 0 0
5 0 0 0
6 3 1 1
6 3 0 0
8 0 3 0
11 0 2 2
10 2 0 1
6 0 1 2
6 3 2 0
6 0 1 3
12 0 2 3
8 3 1 3
8 3 2 3
11 3 1 1
10 1 3 2
6 2 2 1
6 2 3 0
6 3 2 3
4 3 1 1
8 1 2 1
11 2 1 2
10 2 0 0
8 3 0 3
5 3 1 3
6 1 1 1
6 3 3 2
8 3 2 3
8 3 2 3
11 0 3 0
6 2 3 2
6 2 0 1
6 2 0 3
9 1 3 3
8 3 1 3
11 0 3 0
10 0 1 2
6 1 3 3
6 2 2 0
11 3 3 3
8 3 2 3
11 2 3 2
6 2 0 3
6 3 1 1
15 0 3 0
8 0 2 0
8 0 2 0
11 2 0 2
10 2 2 3
8 2 0 0
5 0 1 0
8 3 0 1
5 1 1 1
6 0 0 2
8 1 2 2
8 2 2 2
8 2 1 2
11 3 2 3
10 3 2 0


*/