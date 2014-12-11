import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;


public class ChangeOrder {
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		System.out.println("Enter player name: ");
		
		while(input.hasNextLine()){
			String name = input.nextLine();
			if(name.equals("done")) {
				break;
			}
			File inFile = new File("players\\" + name + ".csv");
			Scanner fileIn = new Scanner(inFile);

			String line = fileIn.nextLine();
			String[] stats = line.split(",");

			PrintWriter out = new PrintWriter(new FileWriter("players\\" + name + ".csv"));
			out.println(stats[1] + "," + stats[3] + "," + stats[4] + "," + stats[5] + "," + stats[6] + "," + stats[7] + "," + stats[8] + "," + stats[9] + "," + stats[0] + "," + stats[2]);
			out.close();
			fileIn.close();
			System.out.println("Enter player name: ");
		}
		input.close();
	}
}
