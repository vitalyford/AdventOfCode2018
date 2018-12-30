import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

class Group implements Comparable<Group> {
	String input;
	String name;
	int unitsNum;
	int hitPoints;
	ArrayList<String> weaknesses;
	ArrayList<String> immunities;
	int damage;
	String attackType;
	int initiative;
	
	boolean isTaken;
	boolean isDead;
	boolean isWeak;
	
	boolean sortByInitiative;
	
	Group chosenEnemy;
	
	public Group copyMe() {
		return new Group(input, name);
	}
	
	Group(String input, String name) {
		this.input = input;
		this.name = name;
		String[] temp = input.split(" ");
		attackType = temp[temp.length - 5];
		weaknesses = new ArrayList<>();
		immunities = new ArrayList<>();
		// find all numbers
		Pattern pattern = Pattern.compile("(\\d+)[^0-9]+(\\d+)[^0-9]+(\\d+)[^0-9]+(\\d+)");
		Matcher matcher = pattern.matcher(input);
		matcher.find();
		unitsNum = Integer.parseInt(matcher.group(1));
		hitPoints = Integer.parseInt(matcher.group(2));
		damage = Integer.parseInt(matcher.group(3));
		initiative = Integer.parseInt(matcher.group(4));
		// find weaknesses and immunities
		pattern = Pattern.compile("\\((.+)\\)");
		matcher = pattern.matcher(input);
		if (!matcher.find()) return;
		String[] splitted = matcher.group(1).split("; ");
		for (String s : splitted) {
			s = s.replaceAll(",", "");
			String[] parsed = s.split(" "); // split by space to find
			if (parsed[0].equals("weak")) {
				for (int i = 2; i < parsed.length; i++) {
					weaknesses.add(parsed[i]);
				}
			}
			else { // immune
				for (int i = 2; i < parsed.length; i++) {
					immunities.add(parsed[i]);
				}
			}
		}
		isTaken = isDead = isWeak = sortByInitiative = false;
		chosenEnemy = null;
	}
	
	public int compareTo(Group g) {
		if (sortByInitiative) return g.initiative - initiative;
		if (effectivePower() == g.effectivePower()) {
			return g.initiative - initiative;
		}
		return g.effectivePower() - effectivePower();
	}
	
	public int effectivePower() {
		return unitsNum * damage;
	}
	
	public void attack() {
		int health = chosenEnemy.unitsNum * chosenEnemy.hitPoints;
		int charge = chosenEnemy.isWeak ? effectivePower() * 2 : effectivePower();
		int delta = health - charge;
		if (delta <= 0) {
			chosenEnemy.isDead = true;
			chosenEnemy.unitsNum = 0;
		}
		else chosenEnemy.unitsNum = (int)Math.ceil((double)delta / (double)chosenEnemy.hitPoints);
	}
	
	public String toString() {
		String out = "\n" + name + " " + unitsNum + " " + hitPoints + " " + damage + " " + initiative + "\nWeaknesses: ";
		for (String w : weaknesses) {
			out += w + " ";
		}
		out += "\nImmunities: ";
		for (String i : immunities) {
			out += i + " ";
		}
		out += "\nAttack type: " + attackType + ", damage: " + damage + "\n";
		return out;
	}
}

public class AdventOfCode2018Day24 {
	
	private static void initAll(PriorityQueue<Group> groups, ArrayList<Group> immunes, ArrayList<Group> infections, 
			PriorityQueue<Group> groupsBackup, int boost) {
		groups.clear();
		immunes.clear();
		infections.clear();
    	for (Group g : groupsBackup) {
    		Group t = g.copyMe();
    		if (t.name.equals("immune")) {
    			t.damage += boost;
    			immunes.add(t);
    		}
    		else infections.add(t);
    		groups.add(t);
    	}
	}

    public static void main(String args[]) throws Exception {  
    	boolean part1done = false;
    	
    	PriorityQueue<Group> groups = new PriorityQueue<>();
    	ArrayList<Group> immunes = new ArrayList<>();
    	ArrayList<Group> infections = new ArrayList<>();
    	PriorityQueue<Group> groupsBackup = new PriorityQueue<>();
    	ArrayList<Group> immunesBackup = new ArrayList<>();
    	ArrayList<Group> infectionsBackup = new ArrayList<>();
    	
    	Scanner s = new Scanner(System.in);
    	s.nextLine(); // Immune System:
    	while (true) {
    		String line = s.nextLine();
    		if (line.equals("")) break;
    		Group g = new Group(line, "immune");
    		groups.add(g);
    		immunes.add(g);
    	}
    	s.nextLine(); // Infection:
    	while (true) {
    		String line = s.nextLine();
    		if (line.equals("")) break;
    		Group g = new Group(line, "infection");
    		groups.add(g);
    		infections.add(g);
    	}
    	s.close();
    	
    	initAll(groupsBackup, immunesBackup, infectionsBackup, groups, 0);
    	int boost = 0;
    	while (true) {
    		initAll(groups, immunes, infections, groupsBackup, boost);
    		//print(groups);
	    	boolean containsImmunes = true, containsInfections = true;
	    	int previousUnitNums = -1;
	    	
	    	while (containsImmunes && containsInfections) {
		    	// select the targets
	    		PriorityQueue<Group> sortedByInitiative = new PriorityQueue<>();
		    	while (!groups.isEmpty()) {
		    		Group attacker = groups.poll();
		    		attacker.sortByInitiative = true;
		    		attacker.chosenEnemy = attacker.name.equals("immune") ? findEnemy(attacker, infections) : findEnemy(attacker, immunes);
		    		if (attacker.chosenEnemy != null) sortedByInitiative.add(attacker);
		    	}
		    	if (sortedByInitiative.isEmpty()) System.out.println("Stalemate!");
		    	while (!sortedByInitiative.isEmpty()) {
		    		Group attacker = sortedByInitiative.poll(); 
		    		if (attacker.chosenEnemy != null && !attacker.isDead) {
		    			attacker.attack();
		    		}
		    	}

		    	containsImmunes = containsInfections = false;
		    	// add leftover players back in the priority queue
		    	int currUnitsNum = 0;
		    	for (Group i : immunes) {
		    		if (!i.isDead) {
		    			i.isTaken = i.isWeak = i.sortByInitiative = false;
		    			groups.add(i);
		    			containsImmunes = true;
		    			currUnitsNum += i.unitsNum;
		    		}
		    	}
		    	for (Group i : infections) {
		    		if (!i.isDead) {
		    			i.isTaken = i.isWeak = i.sortByInitiative = false;
		    			groups.add(i);
		    			containsInfections = true;
		    			currUnitsNum += i.unitsNum;
		    		}
		    	}
		    	if (currUnitsNum == previousUnitNums) break; // everything is stalled
		    	else previousUnitNums = currUnitsNum;
	    	}
	    	boost++;
	    	if (!part1done) {
		    	System.out.print("Answer to the first part: ");
		    	int sum = 0;
		    	if (containsImmunes) {
		    		for (Group i : immunes) sum += i.unitsNum;
		    		System.out.println(sum);
		    	}
		    	else {
		    		for (Group i : infections) sum += i.unitsNum;
		    		System.out.println(sum);
		    	}
		    	part1done = true;
	    	}
	    	if (containsImmunes && !containsInfections) {
		    	int sum = 0;
	    		for (Group i : immunes) sum += i.unitsNum;
	    		System.out.print("Answer to the second part: " + sum);
	    		break;
	    	}
    	}
    }
    
    private static void print(PriorityQueue<Group> groups) {
    	for (Group g : groups) {
    		System.out.println(g.name + " contains " + g.unitsNum + " units with damage " + g.damage);
    	}
    	System.out.println();
    }
    
    private static Group findEnemy(Group attacker, ArrayList<Group> enemies) {
    	Group weakestEnemy = null;
    	int mostDamage = -1, highestInitiative = -1;
    	for (Group e : enemies) {
    		if (e.isTaken || e.isDead) continue;
    		boolean enemyIsImmune = false;
    		boolean enemyIsWeak = false;
    		// check for immunities
    		for (String immunity : e.immunities) {
    			if (attacker.attackType.equals(immunity)) {
    				enemyIsImmune = true;
    				break;
    			}
    		}
    		if (enemyIsImmune) continue; // can't fight this enemy
    		// check for weaknesses
    		for (String weakness : e.weaknesses) {
    			if (attacker.attackType.equals(weakness)) {
    				enemyIsWeak = true;
    				break;
    			}
    		}
    		int damage = enemyIsWeak ? attacker.effectivePower() * 2 : attacker.effectivePower();
    		if (mostDamage < damage) {
    			mostDamage = damage;
    			weakestEnemy = e;
    			highestInitiative = e.initiative;
    		}
    		else if (mostDamage == damage) {
    			if (weakestEnemy.effectivePower() < e.effectivePower()) {
    				weakestEnemy = e;
        			highestInitiative = e.initiative;
    			}
    			else if (weakestEnemy.effectivePower() == e.effectivePower()) {
    				if (highestInitiative < e.initiative) {
    					weakestEnemy = e;
            			highestInitiative = e.initiative;
    				}
    			}
    		}
    	}
    	if (weakestEnemy != null) {
    		weakestEnemy.isTaken = true;
    		if (mostDamage > attacker.effectivePower()) weakestEnemy.isWeak = true;
    	}
    	return weakestEnemy;
    }
}

/*
Immune System:
84 units each with 9798 hit points (immune to bludgeoning) with an attack that does 1151 fire damage at initiative 9
255 units each with 9756 hit points (weak to cold, radiation) with an attack that does 382 slashing damage at initiative 17
4943 units each with 6022 hit points (weak to bludgeoning) with an attack that does 11 bludgeoning damage at initiative 4
305 units each with 3683 hit points (weak to bludgeoning, slashing) with an attack that does 107 cold damage at initiative 5
1724 units each with 6584 hit points (weak to radiation) with an attack that does 30 cold damage at initiative 6
2758 units each with 5199 hit points (immune to slashing, bludgeoning, cold; weak to fire) with an attack that does 18 bludgeoning damage at initiative 15
643 units each with 9928 hit points (immune to fire; weak to slashing, bludgeoning) with an attack that does 149 fire damage at initiative 14
219 units each with 8810 hit points with an attack that does 368 cold damage at initiative 3
9826 units each with 10288 hit points (weak to bludgeoning; immune to cold) with an attack that does 8 cold damage at initiative 18
2417 units each with 9613 hit points (weak to fire, cold) with an attack that does 36 cold damage at initiative 19

Infection:
1379 units each with 46709 hit points with an attack that does 66 slashing damage at initiative 16
1766 units each with 15378 hit points (weak to bludgeoning) with an attack that does 12 radiation damage at initiative 10
7691 units each with 33066 hit points (weak to bludgeoning) with an attack that does 7 slashing damage at initiative 12
6941 units each with 43373 hit points (weak to cold) with an attack that does 12 fire damage at initiative 7
5526 units each with 28081 hit points (weak to fire, slashing) with an attack that does 7 bludgeoning damage at initiative 11
5844 units each with 41829 hit points with an attack that does 11 bludgeoning damage at initiative 20
370 units each with 25050 hit points (immune to radiation; weak to fire) with an attack that does 120 radiation damage at initiative 2
164 units each with 42669 hit points with an attack that does 481 fire damage at initiative 13
3956 units each with 30426 hit points (weak to radiation) with an attack that does 13 cold damage at initiative 8
2816 units each with 35467 hit points (immune to slashing, radiation, fire; weak to cold) with an attack that does 24 slashing damage at initiative 1


*/