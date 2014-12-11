import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.*;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;


public class Show extends JFrame implements NativeKeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static String outcome = null;
	String lastOutcome = null;
	static Show screen;
	static Team away;
	static Team home;
	static Player currentBatter;
	Player lastBatter;
	static Team currentTeam;
	Team lastTeam;
	static int inningOuts = 0;
	int lastOuts = 0;
	static int currentInning = 1;
	int lastInning;
	static boolean isTop = true;
	boolean lastHalf = true;
	static String stealer;
	PrintWriter out;
	boolean canUndo = false;
	
	// Labels
	JPanel box;
	JPanel over;
	JPanel flow;
	static JPanel top;
	JLabel awayScore;
	JLabel homeScore;
	JLabel batterDisplay;
	JLabel teamDisplay;
	JLabel inningDisplay;
	JLabel outDisplay;
	JButton singleBut;
	JButton doubleBut;
	JButton tripleBut;
	JButton hrBut;
	JButton outBut;
	JButton stealBut;
	static TeamNames select1;
	
	static JDialog stealOptions;
	
	JTextField awayName;
	JTextField homeName;
	JTextField away1;
	JTextField away2;
	JTextField away3;
	JTextField away4;
	JTextField home1;
	JTextField home2;
	JTextField home3;
	JTextField home4;

	JLabel bases;

	// Constructor for the main screen
	public Show() {
		
		// Copy existing files to undo Folders
		File playersDir = new File("players\\");
		File playsDir = new File("plays\\");
		File[] allPlayers = playersDir.listFiles();
		File[] allPlays = playsDir.listFiles();
		
		for (File player : allPlayers) {
			try {
				Files.copy(Paths.get("players\\" + player.getName()), Paths.get("undoPlayers\\" + player.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// DO NOTHING ON CATCH
			}
		}
		for (File plays : allPlays) {
			try {
				Files.copy(Paths.get("plays\\" + plays.getName()), Paths.get("undoPlays\\" + plays.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// DO NOTHING ON CATCH
			}
		}
		// End copy process
		
		try {
			GlobalScreen.registerNativeHook();
		}
		catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());

			System.exit(1);
		}
		GlobalScreen.getInstance().addNativeKeyListener(this);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Exit action
		setSize(1000,1000); // Size of window
		setTitle("Play Ball!");
		box = new JPanel();
		BoxLayout myLayout = new BoxLayout(box, BoxLayout.Y_AXIS);
		box.setLayout(myLayout);
		GridLayout overGrid = new GridLayout(6,2);
		over = new JPanel(overGrid);
		overGrid.setHgap(30);
		overGrid.setVgap(10);
		
		// Create info display labels
		awayScore = new JLabel(away.getName() + ": " + away.getRuns());
		homeScore = new JLabel(home.getName() + ": " + home.getRuns());
		batterDisplay = new JLabel("Current Batter: " + currentBatter);
		teamDisplay = new JLabel("At Bat: " + currentTeam);
		inningDisplay = new JLabel("Inning: " + currentInning);
		outDisplay = new JLabel("Outs: " + inningOuts);
		
		// Create buttons for outcomes
		int butTextSize = 30;
		singleBut = new JButton("Single");
		singleBut.setFont(new Font("SansSerif", Font.PLAIN, butTextSize));
		doubleBut = new JButton("Double");
		doubleBut.setFont(new Font("SansSerif", Font.PLAIN, butTextSize));
		tripleBut = new JButton("Triple");
		tripleBut.setFont(new Font("SansSerif", Font.PLAIN, butTextSize));
		hrBut = new JButton("Home Run");
		hrBut.setFont(new Font("SansSerif", Font.PLAIN, butTextSize));
		outBut = new JButton("Out");
		outBut.setFont(new Font("SansSerif", Font.PLAIN, butTextSize));
		stealBut = new JButton("Steal");
		stealBut.setFont(new Font("SansSerif", Font.PLAIN, butTextSize));
		// End buttons
		
		// Create action listeners for buttons
		singleBut.addActionListener(new singleButtonListener());
		doubleBut.addActionListener(new doubleButtonListener());
		tripleBut.addActionListener(new tripleButtonListener());
		hrBut.addActionListener(new hrButtonListener());
		outBut.addActionListener(new outButtonListener());
		stealBut.addActionListener(new stealButtonListener());
		
		// Create image of bases
		bases = new JLabel(new ImageIcon("img\\" + currentTeam.getImg()));
		
		// Add elements to layout
		over.add(awayScore);
		over.add(homeScore);
		over.add(teamDisplay);
		over.add(batterDisplay);
		over.add(inningDisplay);
		over.add(outDisplay);
		over.add(singleBut);
		over.add(doubleBut);
		over.add(tripleBut);
		over.add(hrBut);
		over.add(outBut);
		over.add(stealBut);
		over.setBorder(BorderFactory.createEmptyBorder(10,200,50,200)); 
		box.add(over);
		bases.setAlignmentX(Component.CENTER_ALIGNMENT);
		box.add(bases);
		add(box);
		setVisible(true);
	}	
	
	// Method to update info on display
	private void updateDisplay() {
		
		// Remove everything from display
		over.remove(awayScore);
		over.remove(homeScore);
		over.remove(teamDisplay);
		over.remove(batterDisplay);
		over.remove(inningDisplay);
		over.remove(outDisplay);
		over.remove(singleBut);
		over.remove(doubleBut);
		over.remove(tripleBut);
		over.remove(hrBut);
		over.remove(outBut);
		over.remove(stealBut);
		box.remove(bases);
		
		// Create new score labels
		awayScore = new JLabel(away.getName() + ": " + away.getRuns());
		awayScore.setFont(new Font("SansSerif", Font.BOLD, 60));
		awayScore.setHorizontalAlignment(JLabel.CENTER);
		homeScore = new JLabel(home.getName() + ": " + home.getRuns());
		homeScore.setFont(new Font("SansSerif", Font.BOLD, 60));
		homeScore.setHorizontalAlignment(JLabel.CENTER);
		
		// Set batting team to blue
		if (currentTeam.equals(away)) {
			awayScore.setForeground(Color.BLUE);
			homeScore.setForeground(Color.BLACK);
		} else {
			homeScore.setForeground(Color.BLUE);
			awayScore.setForeground(Color.BLACK);
		}
		
		// Create new labels for batter, team, inning, out, and base image
		batterDisplay = new JLabel("Current Batter: " + currentBatter);
		batterDisplay.setFont(new Font("SansSerif", Font.BOLD, 30));
		batterDisplay.setHorizontalAlignment(JLabel.CENTER);
		teamDisplay = new JLabel("At Bat: " + currentTeam);
		teamDisplay.setFont(new Font("SansSerif", Font.BOLD, 30));
		teamDisplay.setHorizontalAlignment(JLabel.CENTER);
		inningDisplay = new JLabel("Inning: " + currentInning);
		inningDisplay.setFont(new Font("SansSerif", Font.BOLD, 20));
		inningDisplay.setHorizontalAlignment(JLabel.CENTER);
		outDisplay = new JLabel("Outs: " + inningOuts);
		outDisplay.setFont(new Font("SansSerif", Font.BOLD, 20));
		outDisplay.setHorizontalAlignment(JLabel.CENTER);
		bases = new JLabel(new ImageIcon("img\\" + currentTeam.getImg()));
		bases.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Add all of the elements
		over.add(awayScore);
		over.add(homeScore);
		over.add(teamDisplay);
		over.add(batterDisplay);
		over.add(inningDisplay);
		over.add(outDisplay);
		over.add(singleBut);
		over.add(doubleBut);
		over.add(tripleBut);
		over.add(hrBut);
		over.add(outBut);
		over.add(stealBut);
		box.add(bases);
		over.revalidate();
		over.repaint();
		box.revalidate();
		box.repaint();
		
		// Read who is coming to bat
		select1.readName(currentBatter, currentTeam);
		
	}

	// Method run once teams are selected
	public static void startMain() {
		away = select1.getAway();
		home = select1.getHome();
		currentTeam = away;
		currentBatter = away.dueUp();

		screen = new Show();
		screen.updateDisplay();
	}
	
	// Method to select teams
	public static void main(String[] args) {
		select1 = new TeamNames();
		select1.players();
	}

	// Game update after outcome is selected
	private void runOutcome() throws Exception {
		
		// Backup all info to undo folders before changing anything
		File playersDir = new File("players\\");
		File playsDir = new File("plays\\");
		File[] allPlayers = playersDir.listFiles();
		File[] allPlays = playsDir.listFiles();
		for (File player : allPlayers) {
			try {
				Files.copy(Paths.get("players\\" + player.getName()), Paths.get("undoPlayers\\" + player.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// DO NOTHING ON CATCH
			}
		}
		for (File plays : allPlays) {
			try {
				Files.copy(Paths.get("plays\\" + plays.getName()), Paths.get("undoPlays\\" + plays.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// DO NOTHING ON CATCH
			}
		}
		// End undo backup
		
		// Backup last batter, inning, team, half, and outs
		lastBatter = currentBatter;
		lastInning = currentInning;
		lastTeam = currentTeam;
		lastHalf = isTop;
		lastOuts = inningOuts;
		canUndo = true;
		
		// Write outcome to plays file and update accordingly
		out = new PrintWriter(new FileWriter("plays\\" + currentTeam + ".csv", true));
		if (outcome.equals("out")) {
			out.println(currentInning + "," + currentBatter + "," + "out" + "," + inningOuts + "," + currentTeam.getRuns() + "," + currentTeam.first + "," + currentTeam.second + "," + currentTeam.third);
			++inningOuts;
			currentBatter.addOut();
		} else if (outcome.equals("steal")) {
			steal();
		} else {
			currentBatter.addStat(outcome);
			out.println(currentInning + "," + currentBatter + "," + outcome + "," + inningOuts + "," + currentTeam.getRuns() + "," + currentTeam.first + "," + currentTeam.second + "," + currentTeam.third);
		}
		out.close();
		if (!outcome.equals("steal")) {
			currentTeam.updateBases(currentBatter, outcome);
			currentTeam.nextBatter(currentBatter);

			if (inningOuts > 2) {
				switchInning();
			}
			currentBatter = currentTeam.dueUp();
			screen.updateDisplay();
		}
	}
	
	// Method to undo last play
	private void runUndo() {
		
		// Restore last batter, team, half, inning, and outs
		currentBatter = lastBatter;
		currentTeam = lastTeam;
		isTop = lastHalf;
		currentInning = lastInning;
		inningOuts = lastOuts;
		
		// Restore bases and runs
		currentTeam.undoBases();
		currentTeam.undoRuns();
		canUndo = false;		

		// Restore info from undo folders
		File playersDir = new File("undoPlayers\\");
		File playsDir = new File("undoPlays\\");
		File[] allPlayers = playersDir.listFiles();
		File[] allPlays = playsDir.listFiles();
		for (File player : allPlayers) {
			try {
				Files.copy(Paths.get("undoPlayers\\" + player.getName()), Paths.get("players\\" + player.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// DO NOTHING ON CATCH
				e.printStackTrace();
			}
		}
		for (File plays : allPlays) {
			try {
				Files.copy(Paths.get("undoPlays\\" + plays.getName()), Paths.get("plays\\" + plays.getName()), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				// DO NOTHING ON CATCH
				e.printStackTrace();
			}
		}
		// End restore
		
		// Update display with restored info
		updateDisplay();
	}

	// Method to process new half inning
	public void switchInning() {
		
		// Create string to read inning status aloud
		String read = "After the ";
		String inningString;
		if (currentInning == 1) {
			inningString = "1st";
		} else if (currentInning == 2) {
			inningString = "2nd";
		} else if (currentInning == 3) {
			inningString = "3rd";
		} else {
			inningString = currentInning + "th";
		}
		if (isTop) {
			read = read + "top half of the " + inningString + " inning, ";
		} else {
			read = read + "bottom half of the " + inningString + " inning, ";
		}
		if (away.getRuns() > home.getRuns()) {
			read = read + away.getName() + " leads " + home.getName() + ", " + away.getRuns() + " to " + home.getRuns();
		} else if (home.getRuns() > away.getRuns()) {
			read = read + home.getName() + " leads " + away.getName() + ", " + home.getRuns() + " to " + away.getRuns();
		} else {
			read = read + "the game is tied, " + home.getRuns() + " to " + away.getRuns();
		}
		
		select1.readSummary(read);
		// End read aloud process
		
		// Reset inningOuts and bases
		inningOuts = 0;
		currentTeam.resetBases();

		// Switch Team at bat
		if (currentTeam.equals(home)) {
			currentTeam = away;
		} else {
			currentTeam = home;
		}

		if (!isTop) {
			++currentInning;
			isTop = true;
		} else {
			isTop = false;
		}
	}
	
	// Method for processing steals
	private void steal() {
		
		// New dialog window with options for steals
		top = new JPanel(new GridLayout(2,1));
		stealOptions = new JDialog(SwingUtilities.windowForComponent(top), "Steal Options");
		stealOptions.setSize(400,400);
		JPanel pane = new JPanel(new GridLayout(3,2));
		
		// Define buttons
		JButton firstBut;
		JButton secondBut;
		JButton thirdBut;
		JButton firstOutBut;
		JButton secondOutBut;
		JButton thirdOutBut;
		
		// Check if each base is occupied and create steal and caught buttons for each
		if (currentTeam.first != null) {
			firstBut = new JButton(currentTeam.first.getName() + " Stole");
			firstOutBut = new JButton(currentTeam.first.getName() + " Was Caught");
			firstBut.addActionListener(new firstStealListener());
			firstOutBut.addActionListener(new firstCaughtListener());
			pane.add(firstBut);
			pane.add(firstOutBut);
		}
		if (currentTeam.second != null) {
			secondBut = new JButton(currentTeam.second.getName() + " Stole");
			secondOutBut = new JButton(currentTeam.second.getName() + " Was Caught");
			secondBut.addActionListener(new secondStealListener());
			secondOutBut.addActionListener(new secondCaughtListener());
			pane.add(secondBut);
			pane.add(secondOutBut);
		}
		if (currentTeam.third != null) {
			thirdBut = new JButton(currentTeam.third.getName() + " Stole");
			thirdOutBut = new JButton(currentTeam.third.getName() + " Was Caught");
			thirdBut.addActionListener(new thirdStealListener());
			thirdOutBut.addActionListener(new thirdCaughtListener());
			pane.add(thirdBut);
			pane.add(thirdOutBut);
		}
		stealOptions.add(pane);
		stealOptions.setVisible(true);
	}
	
	// Single outcome listener
	public class singleButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			lastOutcome = outcome;
			outcome = "single";
			try {
				runOutcome();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Double outcome listener
	public class doubleButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			lastOutcome = outcome;
			outcome = "double";
			try {
				runOutcome();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Triple outcome listener
	public class tripleButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			lastOutcome = outcome;
			outcome = "triple";
			try {
				runOutcome();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Home run outcome listener
	public class hrButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			lastOutcome = outcome;
			outcome = "hr";
			try {
				runOutcome();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Out outcome listener
	public class outButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			lastOutcome = outcome;
			outcome = "out";
			try {
				runOutcome();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Steal outcome listener
	public class stealButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			lastOutcome = outcome;
			outcome = "steal";
			try {
				runOutcome();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Safe first base steal listener
	public class firstStealListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				currentTeam.steal("first", "safe", currentInning);
				stealOptions.setVisible(false);
				stealOptions.dispose();
				screen.updateDisplay();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Safe second base steal listener
	public class secondStealListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				currentTeam.steal("second", "safe", currentInning);
				stealOptions.setVisible(false);
				stealOptions.dispose();
				screen.updateDisplay();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Safe third base steal listener
	public class thirdStealListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				currentTeam.steal("third", "safe", currentInning);
				stealOptions.setVisible(false);
				stealOptions.dispose();
				screen.updateDisplay();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Caught first base steal listener
	public class firstCaughtListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				currentTeam.steal("first", "out", currentInning);
				++inningOuts;
				stealOptions.setVisible(false);
				stealOptions.dispose();
				if (inningOuts > 2) {
					switchInning();
				}
				currentBatter = currentTeam.dueUp();
				screen.updateDisplay();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}
	
	// Caught second base steal listener
	public class secondCaughtListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				currentTeam.steal("second", "out", currentInning);
				++inningOuts;
				stealOptions.setVisible(false);
				stealOptions.dispose();
				if (inningOuts > 2) {
					switchInning();
				}
				currentBatter = currentTeam.dueUp();
				screen.updateDisplay();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}

	}

	// Caught third base steal listener
	public class thirdCaughtListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			try {
				currentTeam.steal("third", "out", currentInning);
				++inningOuts;
				stealOptions.setVisible(false);
				stealOptions.dispose();
				if (inningOuts > 2) {
					switchInning();
				}
				currentBatter = currentTeam.dueUp();
				screen.updateDisplay();
			} catch (Exception e1) {
				// DO NOTHING ON CATCH
				e1.printStackTrace();
			}
		}
	}
	
	// Methods for key shortcuts
	public void nativeKeyPressed(NativeKeyEvent e) {
		
		// Control must be pressed with other keys
		if ((e.getModifiers() & NativeKeyEvent.CTRL_MASK) != 0) {
			// 1 for single
			if (e.getKeyCode() == NativeKeyEvent.VK_1) {
				lastOutcome = outcome;
				outcome = "single";
				try {
					runOutcome();
				} catch (Exception e1) {
					// DO NOTHING ON CATCH
					e1.printStackTrace();
				}
			}

			// 2 for double
			if (e.getKeyCode() == NativeKeyEvent.VK_2) {
				lastOutcome = outcome;
				outcome = "double";
				try {
					runOutcome();
				} catch (Exception e1) {
					// DO NOTHING ON CATCH
					e1.printStackTrace();
				}
			}

			// 3 for triple
			if (e.getKeyCode() == NativeKeyEvent.VK_3) {
				lastOutcome = outcome;
				outcome = "triple";
				try {
					runOutcome();
				} catch (Exception e1) {
					// DO NOTHING ON CATCH
					e1.printStackTrace();
				}
			}

			// 4 for home run
			if (e.getKeyCode() == NativeKeyEvent.VK_4) {
				lastOutcome = outcome;
				outcome = "hr";
				try {
					runOutcome();
				} catch (Exception e1) {
					// DO NOTHING ON CATCH
					e1.printStackTrace();
				}
			}

			// O for out
			if (e.getKeyCode() == NativeKeyEvent.VK_O) {
				lastOutcome = outcome;
				outcome = "out";
				try {
					runOutcome();
				} catch (Exception e1) {
					// DO NOTHING ON CATCH
					e1.printStackTrace();
				}
			}

			// S for steal
			if (e.getKeyCode() == NativeKeyEvent.VK_S) {
				lastOutcome = outcome;
				outcome = "steal";
				try {
					runOutcome();
				} catch (Exception e1) {
					// DO NOTHING ON CATCH
					e1.printStackTrace();
				}
			}
			
			// Hold shift in addition and press Z for undo
			if ((e.getModifiers() & NativeKeyEvent.SHIFT_MASK) != 0){
				if (e.getKeyCode() == NativeKeyEvent.VK_Z) {	
					if(canUndo){
						runUndo();
					}
				}
			}
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent arg0) {
		// UNIMPLEMENTED
		
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// UNIMPLEMENTED
		
	}

}
