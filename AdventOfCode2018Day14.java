import java.util.*;

public class AdventOfCode2018Day14 {
	
	// answer the second part
	private static int findPattern(ArrayList<Integer> recipes, int startSearch, String in) {
		while (startSearch <= recipes.size() - in.length()) {
			// get the string to match with in
			boolean matches = true;
			for (int i = startSearch; i < startSearch + in.length(); i++) {
				if (recipes.get(i) != (in.charAt(i - startSearch) - '0')) {
					matches = false;
					break;
				}
			}
			if (matches) {
				System.out.println("Answer to the second part: " + startSearch);
				return -1;
			}
			startSearch++;
		}
		return startSearch;
	}
	
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        String in = s.nextLine();
        // uncomment for answering the first part
        //int input = Integer.parseInt(in);
        s.close();
        
        ArrayList<Integer> recipes = new ArrayList<>();
        recipes.add(3);
        recipes.add(7);
        int curr1 = 0, curr2 = 1;
        int startSearch = 0;
        
        // uncomment for answering the first part
        while (true/*recipes.size() < input + 10*/) {
        	int sum = recipes.get(curr1) + recipes.get(curr2);
        	if (sum < 10) recipes.add(sum);
        	else {
        		recipes.add(1);
        		recipes.add(sum % 10);
        	}
        	// step forward to update curr1 and curr2
        	int step1 = 1 + recipes.get(curr1);
        	int step2 = 1 + recipes.get(curr2);
        	curr1 = (curr1 + (step1) % recipes.size()) % recipes.size();
        	curr2 = (curr2 + (step2) % recipes.size()) % recipes.size();
        	// search for the pattern match to answer the second part
        	startSearch = findPattern(recipes, startSearch, in);
        	if (startSearch == -1) break;
        }
        
        // print the scores of the ten recipes for the first part
        /*System.out.print("Answer to the first part: ");
        for (int i = input; i < input + 10; i++) {
        	System.out.print(recipes.get(i));
        }
        System.out.println();*/
    }
}

/*
765071

*/