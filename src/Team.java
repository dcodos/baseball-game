import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Team {

	private final String name;
	private ArrayList<Player> players;
	private ArrayList<Integer> innings;
	private Player dueUp;
	public Player first = null;
	public Player second = null;
	public Player third = null;
	public Player lastFirst = null;
	public Player lastSecond = null;
	public Player lastThird = null;
	private int runs = 0;
	private int lastRuns = 0;
	
	/**
	 * Constructor to take in String array with names
	 */
	public Team(String name, ArrayList<String> names) {
		players = new ArrayList<Player>();
		for (int i = 0; i < names.size(); ++i){
			Player p = new Player(names.get(i));
			this.players.add(p);
		}
		this.innings = new ArrayList<Integer>();
		this.dueUp = players.get(0);
		this.name = name;
	}
	
	// Method to keep track of score for each inning
	public void addInningScore(int inning, int score) {
		if (inning >= this.innings.size()) {
			this.innings.add(score);			
		} else {
			this.innings.add(inning, score);
		}
	}
	
	// Return who is due up next
	public Player dueUp(){
		return this.dueUp;
	}
	
	// Reset all bases
	public void resetBases() {
		this.first = null;
		this.second = null;
		this.third = null;
	}
	
	// Change who is due up to the next batter
	public void nextBatter(Player currentBatter) {
		int index = players.indexOf(currentBatter);
		int limit = players.size() - 2;
		if (index > limit) {
			index = -1;
		}
		this.dueUp = players.get(index + 1);
	}
	
	// Return team name
	public String getName() {
		return this.name;
	}
	
	// Return team score
	public int getRuns() {
		return this.runs;
	}
	
	// Test method to print who is on base
	public void onBase() {
		if (first != null) {
			System.out.append(first + " is on first");
		}
		if (second != null) {
			System.out.append(second + " is on second");
		}
		if (third != null) {
			System.out.append(third + " is on third");
		}
	}
	
	// Run steal method for given base
	public void steal(String base, String result, int i) throws Exception {
		PrintWriter out = new PrintWriter(new FileWriter("plays\\" + this.name + ".csv", true)); 
		
		// Back up team runs and bases
		lastRuns = runs;
		lastFirst = first;
		lastSecond = second;
		lastThird = third;
		
		// Update bases for caught stealing
		if (result.equals("out")) {
			if (base.equals("first")) {
				out.println(i + "," + first + "," + "cs");
				first.addCs();
				first = null;
			}
			if (base.equals("second")) {
				out.println(i + "," + second + "," + "cs");
				second.addCs();
				second = null;
			}
			if (base.equals("third")) {
				out.println(i + "," + third + "," + "cs");
				third.addCs();
				third = null;
			}
		}
		
		// Update bases for safe steal
		if (result.equals("safe")) {
			if (base.equals("first")) {
				out.println(i + "," + first + "," + "sb");
				first.addSb();
				second = first;
				first = null;
			}
			if (base.equals("second")) {
				out.println(i + "," + second + "," + "sb");
				second.addSb();
				third = second;
				second = null;
			}
			if (base.equals("third")) {
				out.println(i + "," + third + "," + "sb");
				third.addSb();
				third.addRunScored();
				third = null;
				++runs;
			}
		}
		out.close();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Team) {
			Team t = (Team) o;
			if (t.getName().equals(this.name)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return this.name;
	}
	
	// Method to restore bases to before last outcome
	public void undoBases() {
		this.first = lastFirst;
		this.second = lastSecond;
		this.third = lastThird;
	}
	
	// Method to restore runs to before last outcome
	public void undoRuns() {
		this.runs = lastRuns;
	}
	
	// Update bases with outcome
	public void updateBases(Player player, String outcome) throws Exception {
		
		// Backup runs and bases
		lastRuns = runs;
		lastFirst = first;
		lastSecond = second;
		lastThird = third;
		
		// Update bases for single
		if(outcome.equals("single")){				
			if(third != null){
				++runs;
				third.addRunScored();
				player.addRbi();
				third = null;
			}
			if(second != null){
				third = second;
				second = null;
			}
			if(first != null){
				second = first;
				first = null;
			}
			first = player;
		}
		
		// Update bases for double
		else if(outcome.equals("double")){
			if(third != null){
				++runs;
				third.addRunScored();
				player.addRbi();
				third = null;
			}
			if(second != null){
				++runs;
				second.addRunScored();
				player.addRbi();
				second = null;
			}
			if(first != null){
				third = first;
				first = null;
			}
			second = player;
		}
		
		// Update bases for triple
		else if(outcome.equals("triple")){
			if(third != null){
				++runs;
				third.addRunScored();
				player.addRbi();
				third = null;
			}
			if(second != null){
				++runs;
				second.addRunScored();
				player.addRbi();
				second = null;
			}
			if(first != null){
				++runs;
				first.addRunScored();
				player.addRbi();
				first = null;
			}
			third = player;
		}
		
		// Update bases for home run
		else if(outcome.equals("hr")){
			if(third != null){
				++runs;
				third.addRunScored();
				player.addRbi();
				third = null;
			}
			if(second != null){
				++runs;
				second.addRunScored();
				player.addRbi();
				second = null;
			}
			if(first != null){
				++runs;
				first.addRunScored();
				player.addRbi();
				first = null;
			}
			player.addRunScored();
			player.addRbi();
			++runs;
		}
	}
	
	// Get img corresponding to base status
	public String getImg() {
		if (first != null && second == null && third == null) {
			return "first.png";
		} else if (first == null && second != null && third == null) {
			return "second.png";
		} else if (first == null && second == null && third != null) {
			return "third.png";
		} else if (first != null && second != null && third == null) {
			return "first_second.png";
		} else if (first == null && second != null && third != null) {
			return "second_third.png";
		} else if (first != null && second == null && third != null) {
			return "corners.png";
		} else if (first != null && second != null && third != null) {
			return "loaded.png";
		} else {
			return "empty_diamond.png";
		}
	}
}
