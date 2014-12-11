import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class Run {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		PrintWriter out;

		ArrayList<String> awayNames = new ArrayList<String>();
		ArrayList<String> homeNames = new ArrayList<String>();
		Player currentBatter;
		Team currentTeam;
		int inningOuts = 0;
		
		// Get names for away team and create team.
		
		System.out.println("Please enter for names for away team: (Enter \"done\" when finished)");
		while (input.hasNextLine()){
			String line = input.nextLine();
			if (line.equals("done")) {
				break;
			}
			awayNames.add(line);
		}
		Team away = new Team("TeamFuckDan", awayNames);

		// Get names for home team and create team.
		System.out.println("Please enter for names for home team: (Enter \"done\" when finished)");
		while (input.hasNextLine()){
			String line = input.nextLine();
			if (line.equals("done")) {
				break;
			}
			homeNames.add(line);
		}
		Team home = new Team("TheSenators", homeNames);
		
		currentTeam = away;
		currentBatter = away.dueUp();
		System.out.println("Play Ball");

		for (int i = 0; i < 18; ++i) {
			while (inningOuts < 3) {
				currentBatter = currentTeam.dueUp();
				out = new PrintWriter(new FileWriter("players\\" + currentBatter + ".csv", true)); 
				System.out.println("Current Score: " + away.getName() + ": " + away.getRuns() + " " + home.getName() + ": " + home.getRuns());
				System.out.println("Team at bat is: " + currentTeam);
				System.out.println("Curent batter is: " + currentBatter);
				System.out.println("Enter outcome: (single, double, triple, hr, out)");
				String line = input.nextLine();
				int inning = i / 2 + 1;
				if (line.equals("out")) {
					++inningOuts;
					currentBatter.addOut();
					out.println(inning + "," + "out" + "," + inningOuts);
				} else if (line.equals("steal")) {
					currentTeam.onBase();
					System.out.println("Please select the base that the base stealer was on");
					String base = input.nextLine();
					System.out.println("Enter \"out\" or \"safe\": ");
					String result = input.nextLine();
					out.close();
					if (result.equals("out")) {
						++inningOuts;
						System.out.println("Who got him out?");
						String defense = input.nextLine();
						out = new PrintWriter(new FileWriter("players\\" + defense + ".csv", true));
						out.println(inning + "," + "CaughtRunner");
						out.close();
					}
					currentTeam.steal(base, result, inning);
					continue;
				} else {
					out = new PrintWriter(new FileWriter("players\\" + currentBatter + ".csv", true));
					currentBatter.addStat(line);
					out.println(inning + "," + line + "," + inningOuts);
				}
				currentTeam.updateBases(currentBatter, line);
				currentTeam.nextBatter(currentBatter);
				out.close();
			}
			out = new PrintWriter(new FileWriter("teams\\" + currentTeam + ".csv", true));
			int inning = i / 2 + 1;
			out.println(inning + "," + currentTeam + "," + currentTeam.getRuns());
			out.close();
			inningOuts = 0;
			currentTeam.resetBases();
			
			// Switch Team at bat
			if (currentTeam.equals(home)) {
				currentTeam = away;
			} else {
				currentTeam = home;
			}
		}
input.close();
	}

}
