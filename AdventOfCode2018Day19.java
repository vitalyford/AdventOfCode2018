import java.util.*;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class AdventOfCode2018Day19 {
	
	private static int a = 1, b = 0, c = 0, d = 0, e = 0, f = 0;
	
	private static HashMap<Integer, Callable<String>> hm = new HashMap<Integer, Callable<String>>() {
		{
			put(0, () -> {
				f = f + 16;
				return "";
			});
			put(1, () -> {
				c = 1;
				return "";
			});
			put(2, () -> {
				b = 1;
				return "";
			});
			put(3, () -> {
				d = c * b;
				return "";
			});
			put(4, () -> {
				d = (d == e) ? 1 : 0;
				return "";
			});
			put(5, () -> {
				f = d + f;
				return "";
			});
			put(6, () -> {
				f = f + 1;
				return "";
			});
			put(7, () -> {
				a = c + a;
				return "";
			});
			put(8, () -> {
				b = b + 1;
				return "";
			});
			put(9, () -> {
				d = (b > e) ? 1 : 0;
				return "";
			});
			put(10, () -> {
				f = f + d;
				return "";
			});
			put(11, () -> {
				f = 2;
				return "";
			});
			put(12, () -> {
				c = c + 1;
				return "";
			});
			put(13, () -> {
				d = (c > e) ? 1 : 0;
				return "";
			});
			put(14, () -> {
				f = d + f;
				return "";
			});
			put(15, () -> {
				f = 1;
				return "";
			});
			put(16, () -> {
				f = f * f;
				return "";
			});
			put(17, () -> {
				e = e + 2;
				return "";
			});
			put(18, () -> {
				e = e * e;
				return "";
			});
			put(19, () -> {
				e = f * e;
				return "";
			});
			put(20, () -> {
				e = e * 11;
				return "";
			});
			put(21, () -> {
				d = d + 2;
				return "";
			});
			put(22, () -> {
				d = d * f;
				return "";
			});
			put(23, () -> {
				d = d + 13;
				return "";
			});
			put(24, () -> {
				e = e + d;
				return "";
			});
			put(25, () -> {
				f = f + a;
				return "";
			});
			put(26, () -> {
				f = 0;
				return "";
			});
			put(27, () -> {
				d = f;
				return "";
			});
			put(28, () -> {
				d = d * f;
				return "";
			});
			put(29, () -> {
				d = f + d;
				return "";
			});
			put(30, () -> {
				d = f * d;
				return "";
			});
			put(31, () -> {
				d = d * 14;
				return "";
			});
			put(32, () -> {
				d = d * f;
				return "";
			});
			put(33, () -> {
				e = e + d;
				return "";
			});
			put(34, () -> {
				a = 0;
				return "";
			});
			put(35, () -> {
				f = 0;
				return "";
			});
		}
	};
	
	private static String[] r = {"a", "b", "c", "d", "e", "f"}; // registers 0,1,2,3,4,5
	
	private static void initOps(HashMap<String, Operation> ops) {
		ops.put("addr", new Addr());
		ops.put("addi", new Addi());
		ops.put("mulr", new Mulr());
		ops.put("muli", new Muli());
		ops.put("banr", new Banr());
		ops.put("bani", new Bani());
		ops.put("borr", new Borr());
		ops.put("bori", new Bori());
		ops.put("setr", new Setr());
		ops.put("seti", new Seti());
		ops.put("gtir", new Gtir());
		ops.put("gtri", new Gtri());
		ops.put("gtrr", new Gtrr());
		ops.put("eqir", new Eqir());
		ops.put("eqri", new Eqri());
		ops.put("eqrr", new Eqrr());
	}
	
	private static void doOp(HashMap<String, Operation> ops, ArrayList<Long> regs, String instruction) {
		String[] splitted = instruction.split(" ");
    	String name = splitted[0];
    	int A = Integer.parseInt(splitted[1]);
    	int B = Integer.parseInt(splitted[2]);
    	int C = Integer.parseInt(splitted[3]);
    	
    	ops.get(name).process(A, B, C, regs);
	}
	
	private static void recompile(String instruction) {
		String[] splitted = instruction.split(" ");
    	String name = splitted[0];
    	int A = Integer.parseInt(splitted[1]);
    	int B = Integer.parseInt(splitted[2]);
    	int C = Integer.parseInt(splitted[3]);
    	
    	switch (name) {
    	case "addr":
    		System.out.println(r[C] + " = " + r[A] + " + " + r[B]);
    		break;
    	case "addi":
    		System.out.println(r[C] + " = " + r[A] + " + " + B);
    		break;
    	case "mulr":
    		System.out.println(r[C] + " = " + r[A] + " * " + r[B]);
    		break;
    	case "muli":
    		System.out.println(r[C] + " = " + r[A] + " * " + B);
    		break;
    	case "banr":
    		System.out.println(r[C] + " = " + r[A] + " & " + r[B]);
    		break;
    	case "bani":
    		System.out.println(r[C] + " = " + r[A] + " & " + B);
    		break;
    	case "borr":
    		System.out.println(r[C] + " = " + r[A] + " | " + r[B]);
    		break;
    	case "bori":
    		System.out.println(r[C] + " = " + r[A] + " | " + B);
    		break;
    	case "setr":
    		System.out.println(r[C] + " = " + r[A]);
    		break;
    	case "seti":
    		System.out.println(r[C] + " = " + A);
    		break;
    	case "gtir":
    		System.out.println(r[C] + " = (" + A + " > " + r[B] + ") ? 1 : 0");
    		break;
    	case "gtri":
    		System.out.println(r[C] + " = (" + r[A] + " > " + B + ") ? 1 : 0");
    		break;
    	case "gtrr":
    		System.out.println(r[C] + " = (" + r[A] + " > " + r[B] + ") ? 1 : 0");
    		break;
    	case "eqir":
    		System.out.println(r[C] + " = (" + A + " == " + r[B] + ") ? 1 : 0");
    		break;
    	case "eqri":
    		System.out.println(r[C] + " = (" + r[A] + " == " + B + ") ? 1 : 0");
    		break;
    	case "eqrr":
    		System.out.println(r[C] + " = (" + r[A] + " == " + r[B] + ") ? 1 : 0");
    		break;
    	}
	}
	
    public static void main(String args[]) throws Exception {
    	// operation_name, operation
    	HashMap<String, Operation> ops = new HashMap<>();
    	ArrayList<Long> regs = new ArrayList<>();
    	for (int i = 0; i < 6; i++) regs.add(new Long(0));
    	initOps(ops);
        Scanner s = new Scanner(System.in);
        Long ip = Long.parseLong(s.nextLine().split(" ")[1]);
        int bound = ip.intValue();
        ArrayList<String> instructions = new ArrayList<>();
        while (true) {
        	String line = s.nextLine();
        	if (line.equals("")) break;
        	instructions.add(line);
        }
        s.close();
        
        // recompile the instructions
        for (String l : instructions) {
        	recompile(l);
        }
        
        // first part, just a normal run without any hacks
        ip = regs.get(bound);
        while (ip >= 0 && ip < instructions.size()) {
        	regs.set(bound, ip);
        	doOp(ops, regs, instructions.get(ip.intValue()));
        	ip = regs.get(bound) + 1;
        }
        System.out.println("Answer to the first part: " + regs.get(0));
        
        // second part, a few hacks:
        // could have made it more universal without transforming Elf code into
        // java code but realized it too far into it so...
        int nip = 0;
        long count = 0;
        HashMap<Integer, Integer> loop = new HashMap<Integer, Integer>();
        boolean loopFound = false;
        while (nip >= 0 && nip < instructions.size()) {
        	f = nip;
        	hm.get(nip).call();
        	nip = f + 1;
        	if (loop.containsKey(nip)) loopFound = true;
        	else loop.put(nip, 1);
        	// fast-forward if there is a loop
        	if (nip == 3 && loopFound && b < e / c) {
        		if (e % c == 0) b = e / c;
        		else b = e;
        		loopFound = false;
        		loop = new HashMap<Integer, Integer>();
        	}
        	else if (nip == 3 && loopFound) {
        		b = e;
        		loopFound = false;
        		loop = new HashMap<Integer, Integer>();
        	}
        	//if (count++ % 1000000 == 0) System.out.println(nip + ": " + a + ", " + b + ", " + c + ", " + d + ", " + e + ", " + f);
        }
        System.out.println("Answer to the second part: " + a);
    } 
}

interface Operation {
	public void process(int A, int B, int C, ArrayList<Long> registers);
	public String getName();
}

class Addr implements Operation {
	String name = "addr";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) + registers.get(B));
	}
	
	public String getName() {
		return name;
	}
}

class Addi implements Operation {
	String name = "addi";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) + B);
	}
	
	public String getName() {
		return name;
	}
}

class Mulr implements Operation {
	String name = "mulr";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) * registers.get(B));
	}
	
	public String getName() {
		return name;
	}
}

class Muli implements Operation {
	String name = "muli";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) * B);
	}
	
	public String getName() {
		return name;
	}
}

class Banr implements Operation {
	String name = "banr";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) & registers.get(B));
	}
		
	public String getName() {
		return name;
	}
}

class Bani implements Operation {
	String name = "bani";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) & B);
	}
	
	public String getName() {
		return name;
	}
}

class Borr implements Operation {
	String name = "borr";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) | registers.get(B));
	}
	
	public String getName() {
		return name;
	}
}

class Bori implements Operation {
	String name = "bori";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A) | B);
	}
	
	public String getName() {
		return name;
	}
}

class Setr implements Operation {
	String name = "setr";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, registers.get(A));
	}
	
	public String getName() {
		return name;
	}
}

class Seti implements Operation {
	String name = "seti";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		registers.set(C, new Long(A));
	}
	
	public String getName() {
		return name;
	}
}

class Gtir implements Operation {
	String name = "gtir";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		if (A > registers.get(B).intValue()) registers.set(C, new Long(1));
		else registers.set(C, new Long(0));
	}
	
	public String getName() {
		return name;
	}
}

class Gtri implements Operation {
	String name = "gtri";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		if (registers.get(A).intValue() > B) registers.set(C, new Long(1));
		else registers.set(C, new Long(0));
	}
	
	public String getName() {
		return name;
	}
}

class Gtrr implements Operation {
	String name = "gtrr";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		if (registers.get(A).intValue() > registers.get(B).intValue()) registers.set(C, new Long(1));
		else registers.set(C, new Long(0));
	}
	
	public String getName() {
		return name;
	}
}

class Eqir implements Operation {
	String name = "eqir";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		if (A == registers.get(B).intValue()) registers.set(C, new Long(1));
		else registers.set(C, new Long(0));
	}
	
	public String getName() {
		return name;
	}
}

class Eqri implements Operation {
	String name = "eqri";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		if (registers.get(A).intValue() == B) registers.set(C, new Long(1));
		else registers.set(C, new Long(0));
	}
	
	public String getName() {
		return name;
	}
}

class Eqrr implements Operation {
	String name = "eqrr";
	public void process(int A, int B, int C, ArrayList<Long> registers) {
		if (registers.get(A).intValue() == registers.get(B).intValue()) registers.set(C, new Long(1));
		else registers.set(C, new Long(0));
	}
	
	public String getName() {
		return name;
	}
}
/*
#ip 5
addi 5 16 5
seti 1 1 2
seti 1 8 1
mulr 2 1 3
eqrr 3 4 3
addr 3 5 5
addi 5 1 5
addr 2 0 0
addi 1 1 1
gtrr 1 4 3
addr 5 3 5
seti 2 6 5
addi 2 1 2
gtrr 2 4 3
addr 3 5 5
seti 1 2 5
mulr 5 5 5
addi 4 2 4
mulr 4 4 4
mulr 5 4 4
muli 4 11 4
addi 3 2 3
mulr 3 5 3
addi 3 13 3
addr 4 3 4
addr 5 0 5
seti 0 8 5
setr 5 5 3
mulr 3 5 3
addr 5 3 3
mulr 5 3 3
muli 3 14 3
mulr 3 5 3
addr 4 3 4
seti 0 9 0
seti 0 9 5


*/