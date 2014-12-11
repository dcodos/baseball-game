import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.gtranslate.Audio;
import com.gtranslate.Language;

//import com.gtranslate.Audio;
//import com.gtranslate.Language;



public class TeamNames {
static JDialog stealOptions;
	
	// Define text fields for team names and players
	JTextField awayName;
	JTextField homeName;
	JTextField away1;
	JTextField away2;
	JTextField away3;
	JTextField away4;
	JTextField away5;
	JTextField home1;
	JTextField home2;
	JTextField home3;
	JTextField home4;
	JTextField home5;
	JButton submit;
	JPanel top;
	
	Team away;
	Team home;
	
	public void players() {
		
		// Setup panel to show input options
		top = new JPanel(new GridLayout(2,1));
		stealOptions = new JDialog(SwingUtilities.windowForComponent(top), "Enter Players");
		stealOptions.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		stealOptions.setSize(500,200);
		JPanel pane = new JPanel(new GridLayout(8,3));
		
		// Define new labels
		JLabel blank = new JLabel("");
		JLabel blank2 = new JLabel("");
		JLabel awayL = new JLabel("Away");
		JLabel homeL = new JLabel("Home");
		JLabel teamName = new JLabel("Team Name: ");
		JLabel player1 = new JLabel("Player 1:      ");
		JLabel player2 = new JLabel("Player 2:      ");
		JLabel player3 = new JLabel("Player 3:      ");
		JLabel player4 = new JLabel("Player 4:      ");
		JLabel player5 = new JLabel("Player 5:      ");
		
		// Define text fields
		awayName = new JTextField(20);
		homeName = new JTextField(20);
		away1 = new JTextField(20);
		away2 = new JTextField(20);
		away3 = new JTextField(20);
		away4 = new JTextField(20);
		away5 = new JTextField(20);
		home1 = new JTextField(20);
		home2 = new JTextField(20);
		home3 = new JTextField(20);
		home4 = new JTextField(20);
		home5 = new JTextField(20);
		
		// Define submit button and action listener
		submit = new JButton("Submit");
		submit.addActionListener(new submitButtonListener());
		
		// Add elements to the layout
		pane.add(blank);
		pane.add(awayL);
		pane.add(homeL);
		pane.add(teamName);
		pane.add(awayName);
		pane.add(homeName);
		pane.add(player1);
		pane.add(away1);
		pane.add(home1);
		pane.add(player2);
		pane.add(away2);
		pane.add(home2);
		pane.add(player3);
		pane.add(away3);
		pane.add(home3);
		pane.add(player4);
		pane.add(away4);
		pane.add(home4);
		pane.add(player5);
		pane.add(away5);
		pane.add(home5);
		pane.add(blank2);
		pane.add(submit);
		
		stealOptions.add(pane);
		stealOptions.setVisible(true);
		
	}
	
	// Performed when submit button pressed.
	public class submitButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			
			// Create arraylists for names of players
			ArrayList<String> awayNames = new ArrayList<String>();
			ArrayList<String> homeNames = new ArrayList<String>();
			PrintWriter out;
			
			// Get first 4 away names
			awayNames.add(away1.getText());
			awayNames.add(away2.getText());
			awayNames.add(away3.getText());
			// Get 4th away name if it exists
			if (!away4.getText().equals("") && away4.getText() != null) {
				awayNames.add(away4.getText());
			}
			
			// Get 5th away name if it exists
			if (!away5.getText().equals("") && away5.getText() != null) {
				awayNames.add(away5.getText());
			}
			
			// Create undo files for each away player
			for (int i = 0; i < awayNames.size(); ++i) {
				try {
					out = new PrintWriter("undoPlayers\\" + awayNames.get(i) + ".csv");
					out.println("0,0,0,0,0,0,0,0,0,0");
					out.close();
				} catch (FileNotFoundException e1) {
				}
			}
			
			// Get first 4 home names
			homeNames.add(home1.getText());
			homeNames.add(home2.getText());
			homeNames.add(home3.getText());
			// Get 4th home name if it exists
			if (!home4.getText().equals("") && home4.getText() != null) {
				homeNames.add(home4.getText());
			}
			
			// Get 5th home name if it exists
			if (!home5.getText().equals("") && home5.getText() != null) {
				homeNames.add(home5.getText());
			}
			
			// Create undo files for each home player
			for (int i = 0; i < homeNames.size(); ++i) {
				try {
					out = new PrintWriter("undoPlayers\\" + homeNames.get(i) + ".csv");
					out.println("0,0,0,0,0,0,0,0,0,0");
					out.close();
				} catch (FileNotFoundException e1) {
				}
			}
			
			// Define the teams with the player arrays and team names.
			away = new Team(awayName.getText(), awayNames);
			home = new Team(homeName.getText(), homeNames);
			
			// Create undo play files for the teams if none exist.
			try {
				if (!Files.exists(Paths.get("undoPlays\\" + awayName.getText() + ".csv"))){
					Files.createFile(Paths.get("undoPlays\\" + awayName.getText() + ".csv"));
				}
				if (!Files.exists(Paths.get("undoPlays\\" + homeName.getText() + ".csv")))
				Files.createFile(Paths.get("undoPlays\\" + homeName.getText() + ".csv"));
			} catch (Exception e1) {
			}
			stealOptions.setVisible(false);
			stealOptions.dispose();
			
			// Run startMain method of Show
			Show.startMain();
		}

	}
	
	// Return away team
	public Team getAway() {
		return away;
	}
	
	// Return home team
	public Team getHome() {
		return home;
	}
	
	// Read current batter name aloud
	public void readName(Player currentBatter, Team currentTeam) {
		Audio audio = Audio.getInstance();
		InputStream sound;
		try {
			sound = audio.getAudio("Now batting for " + currentTeam + ", " + currentBatter, Language.ENGLISH);
			audio.play(sound);
		} catch (Exception e) {
			// e.printStackTrace(); // DO NOTHING IF COMMENTED OUT
		}
		
	}
	
	// Read inning summary
	public void readSummary(String s) {
		Audio audio = Audio.getInstance();
		InputStream sound;
		try {
			sound = audio.getAudio(s, Language.ENGLISH);
			audio.play(sound);
		} catch (Exception e) {
			// e.printStackTrace(); // DO NOTHING IF COMMENTED OUT
		}
		
	}
}
