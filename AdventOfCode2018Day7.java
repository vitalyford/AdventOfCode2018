import java.util.*;

/**
 * Similarly to the topological ordering algorithm,
 * (1) find all verticies with no predecessors and add them to the priority queue called pq,
 * (2) poll a vertex from pq and put it on the output, after that you delete that vertex from the available ones
 * (3) repeat
 * 
 * Also, there will be vertices in the graph that do not have successors, I called them leaves here.
 * Just add those leaves at the end to the output list in the alphabetical order.
 */
 
class Job implements Comparable<Job> {
    Character c;
    int time;
    
    Job() {}
    
    Job(Character c, int time) {
        this.c = c;
        this.time = time;
    }
    
    public int compareTo(Job j) {
        return c.compareTo(j.c);
    }
    
    public String toString() {
        return "" + c;
    }
}

public class AdventOfCode2018Day7 {
    private static void findVerticesWithNoPredecessors(TreeMap<Character, LinkedList<Character>> tm, PriorityQueue<Character> pq) {
        Collection<LinkedList<Character>> values = tm.values();
        ArrayList<Character> withPredecessors = new ArrayList<>();
        for (LinkedList<Character> v : values) {
            for (Character c : v) {
                if (!withPredecessors.contains(c))
                    withPredecessors.add(c);
            }
        }
        // find the vertices without predecessors and push them on the priority queue
        Set<Character> keys = tm.keySet();
        for (Character key : keys) {
            if (!withPredecessors.contains(key) && !pq.contains(key))
                pq.add(key);
        }
    }
    
    public static void main(String args[]) {
        Scanner s = new Scanner(System.in);
        // create a linked list version to store a graph
        TreeMap<Character, LinkedList<Character>> tm = new TreeMap<>();  // for the first part
        TreeMap<Character, LinkedList<Character>> tm2 = new TreeMap<>(); // for the second part
        ArrayList<Character> withPredecessors = new ArrayList<>();
        while (true) {
            String[] line = s.nextLine().split(" ");
            if (line.length < 2) break;
            Character start = line[1].charAt(0);
            Character end   = line[7].charAt(0);
            
            // find all nodes that have predecessors
            if (!withPredecessors.contains(end))
                withPredecessors.add(end);
            
            if (!tm.containsKey(start)) {
                tm.put(start, new LinkedList<Character>());
                tm2.put(start, new LinkedList<Character>());
            }
            tm.get(start).add(end);
            tm2.get(start).add(end);
        }
        s.close();
        
        // find all leaves in this graph (aka tree)
        Collection<LinkedList<Character>> values = tm.values();
        Set<Character> keys = tm.keySet();
        PriorityQueue<Character> leaves = new PriorityQueue<>();  // for the first part
        PriorityQueue<Character> leaves2 = new PriorityQueue<>(); // for the second part
        for (LinkedList<Character> v : values) {
            for (Character c : v) {
                if (!keys.contains(c) && !leaves.contains(c)) {
                    leaves.add(c);
                    leaves2.add(c);
                }
            }
        }
        
        // use topological order algorithm to solve the first part
        ArrayList<Character> topological = new ArrayList<>();
        
        PriorityQueue<Character> pq = new PriorityQueue<>();
        // push all vertices without predecessor on the stack
        findVerticesWithNoPredecessors(tm, pq);
        while (pq.size() != 0) {
            Character c = pq.poll();
            // add c to the topologically ordered list
            topological.add(c);
            // do not forget to remove it from the treemap to recognize that we have already visited this vertex
            tm.remove(c);
            findVerticesWithNoPredecessors(tm, pq);
        }
        
        // add all leaves to the topologically ordered list
        while (leaves.size() != 0) {
            topological.add(leaves.poll());
        }
        
        System.out.print("Answer to the first part: ");
        for (Character t : topological) {
            System.out.print(t);
        }
        
        System.out.println();
        System.out.print("Answer to the second part: ");
        // here comes the second part
        int w = 5;
        int freeWorkers = w;
        int currentTime = 0;
        ArrayList<Job> jobs = new ArrayList<>();
        pq = new PriorityQueue<Character>();
        // push all vertices without predecessor on the stack
        findVerticesWithNoPredecessors(tm2, pq);
        while (pq.size() != 0 || jobs.size() > 0) {
            // add new jobs if we have work to do and free workers
            while (freeWorkers > 0 && pq.size() != 0) {
                Character c = pq.poll();
                jobs.add(new Job(c, 60 + c - 'A' + 1));
                freeWorkers--;
            }
            
            // do the jobs until we have at least one of them done
            boolean done = false;
            ArrayList<Job> doneJobs = new ArrayList<>();
            while (!done) {
                currentTime++;
                for (Job j : jobs) {
                    j.time--;
                    if (j.time == 0) { // the job is done
                        doneJobs.add(j);
                        done = true;
                        System.out.print(j.c);
                        tm2.remove(j.c);
                        freeWorkers++;
                    }
                }
            }
            
            // delete finished jobs from the jobs array list
            for (Job j : doneJobs) {
                jobs.remove(j);
            }
            
            findVerticesWithNoPredecessors(tm2, pq);
            // remove the jobs from pq that are already running
            PriorityQueue<Character> temp = new PriorityQueue<>();
            for (Character c : pq) {
            	boolean running = false;
            	for (Job j : jobs) {
            		if (j.c == c) {
            			running = true;
            			break;
            		}
            	}
            	if (!running) temp.add(c);
            }
            pq = temp;
            
            // maybe it is time to add the leaves
        	// add only those leaves that are not dependent on other jobs
        	for (Character c : leaves2) {
        		Collection<LinkedList<Character>> vals = tm2.values();
        		boolean leafFound = false;
            	for (LinkedList<Character> v : vals) {
            		if (v.contains(c)) {
            			leafFound = true;
            			break;
            		}
            	}
            	if (!leafFound) {
            		pq.add(c);
            	}
        	}
        	// delete the leaves that we have just added to the priority queue
        	for (Character c : pq) {
        		if (leaves2.contains(c)) leaves2.remove(c);
        	}
        }
        System.out.println(" " + (currentTime));
    }
}

/*
Step A must be finished before step R can begin.
Step J must be finished before step B can begin.
Step D must be finished before step B can begin.
Step X must be finished before step Z can begin.
Step H must be finished before step M can begin.
Step B must be finished before step F can begin.
Step Q must be finished before step I can begin.
Step U must be finished before step O can begin.
Step T must be finished before step W can begin.
Step V must be finished before step S can begin.
Step N must be finished before step P can begin.
Step P must be finished before step O can begin.
Step E must be finished before step C can begin.
Step F must be finished before step O can begin.
Step G must be finished before step I can begin.
Step Y must be finished before step Z can begin.
Step M must be finished before step K can begin.
Step C must be finished before step W can begin.
Step L must be finished before step W can begin.
Step W must be finished before step S can begin.
Step Z must be finished before step O can begin.
Step K must be finished before step S can begin.
Step S must be finished before step R can begin.
Step R must be finished before step I can begin.
Step O must be finished before step I can begin.
Step A must be finished before step Q can begin.
Step Z must be finished before step R can begin.
Step T must be finished before step R can begin.
Step M must be finished before step O can begin.
Step Q must be finished before step Z can begin.
Step V must be finished before step C can begin.
Step Y must be finished before step W can begin.
Step N must be finished before step F can begin.
Step J must be finished before step D can begin.
Step D must be finished before step N can begin.
Step B must be finished before step M can begin.
Step P must be finished before step I can begin.
Step W must be finished before step Z can begin.
Step Q must be finished before step V can begin.
Step V must be finished before step K can begin.
Step B must be finished before step Z can begin.
Step M must be finished before step I can begin.
Step G must be finished before step C can begin.
Step K must be finished before step O can begin.
Step E must be finished before step O can begin.
Step C must be finished before step I can begin.
Step X must be finished before step G can begin.
Step B must be finished before step T can begin.
Step B must be finished before step I can begin.
Step E must be finished before step F can begin.
Step N must be finished before step K can begin.
Step D must be finished before step W can begin.
Step R must be finished before step O can begin.
Step V must be finished before step I can begin.
Step T must be finished before step O can begin.
Step B must be finished before step Q can begin.
Step T must be finished before step L can begin.
Step M must be finished before step C can begin.
Step A must be finished before step M can begin.
Step F must be finished before step L can begin.
Step X must be finished before step T can begin.
Step G must be finished before step K can begin.
Step C must be finished before step L can begin.
Step D must be finished before step Z can begin.
Step H must be finished before step L can begin.
Step P must be finished before step Z can begin.
Step A must be finished before step V can begin.
Step G must be finished before step R can begin.
Step E must be finished before step G can begin.
Step D must be finished before step P can begin.
Step X must be finished before step L can begin.
Step U must be finished before step C can begin.
Step Z must be finished before step K can begin.
Step E must be finished before step W can begin.
Step B must be finished before step Y can begin.
Step J must be finished before step I can begin.
Step U must be finished before step P can begin.
Step Y must be finished before step L can begin.
Step N must be finished before step L can begin.
Step L must be finished before step S can begin.
Step H must be finished before step P can begin.
Step P must be finished before step S can begin.
Step J must be finished before step S can begin.
Step J must be finished before step U can begin.
Step H must be finished before step T can begin.
Step L must be finished before step I can begin.
Step N must be finished before step Z can begin.
Step A must be finished before step G can begin.
Step H must be finished before step S can begin.
Step S must be finished before step I can begin.
Step H must be finished before step E can begin.
Step W must be finished before step R can begin.
Step B must be finished before step G can begin.
Step U must be finished before step Y can begin.
Step J must be finished before step G can begin.
Step M must be finished before step L can begin.
Step G must be finished before step Z can begin.
Step N must be finished before step W can begin.
Step D must be finished before step E can begin.
Step A must be finished before step W can begin.
Step G must be finished before step Y can begin.

*/