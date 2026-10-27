import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @auther: Samyak Gurung
 * Description: The text UI from project 2.
 * - User Interface and interaction are all done here.
 * - Gets input from user
 * - Sends the data to the shared CryptogramController
 * - Presents the current status of the users interaction
 *
 * */
public class CryptogramTextView {
	private static final int LINE_WIDTH = 80;

	private CryptogramController control;
	private Scanner key;

	public CryptogramTextView() throws FileNotFoundException {
		control = new CryptogramController(new CryptogramModel());
		key = new Scanner(System.in);
	}

	/**
	 * Prints the users progress above the encrypted quote, line by line
	 */
	private void printBoard() {
		List<String> progress = control.splitIntoLines(control.getUsersProgress(), LINE_WIDTH);
		List<String> encrypted = control.splitIntoLines(control.getEncryptedQuote(), LINE_WIDTH);
		System.out.println();
		for (int i = 0; i < encrypted.size(); i++) {
			System.out.println(progress.get(i));
			System.out.println(encrypted.get(i));
			System.out.println();
		}
	}

	/**
	 * Prints the frequency of every encrypted letter, 7 per line
	 */
	private void printFreq() {
		String freq = "";
		for (char c = 'A'; c <= 'Z'; c++) {
			freq += c + ": " + control.getFrequency(c) + " ";
			if ((c - 'A') % 7 == 6) {
				freq += "\n";
			}
		}
		System.out.println(freq);
	}

	/**
	 * Plays one puzzle until it is solved or the user exits
	 */
	private void playPuzzle() {
		while (!control.isGameOver()) {
			printBoard();
			System.out.print("Enter a command (type help to see commands): ");
			if (!key.hasNextLine()) {
				return;
			}
			String comm = key.nextLine().trim();

			// conducts replace X by Y command
			if (comm.length() == 14 && comm.startsWith("replace ") && comm.substring(9, 13).equals(" by ")) {
				control.makeReplacement(comm.charAt(8), comm.charAt(13));
			}
			// conducts X = Y command
			else if (comm.length() == 5 && comm.substring(1, 4).equals(" = ")) {
				control.makeReplacement(comm.charAt(0), comm.charAt(4));
			}
			// conducts the return frequency command
			else if (comm.equals("freq")) {
				printFreq();
			}
			// conducts the hint command
			else if (comm.equals("hint")) {
				char c = control.giveHint();
				if (c != ' ') {
					System.out.println("Hint: " + c + " = " + control.getGuess(c));
				}
			}
			//  exits the game
			else if (comm.equals("exit")) {
				return;
			}
			// returns the list of commands and their use
			else if (comm.equals("help")) {
				System.out.println("replace X by Y  -Replace Character X by Character Y");
				System.out.println("X = Y           -Shortcut to replace X by Y");
				System.out.println("freq            -Displays the letter frequency in the encrypted form");
				System.out.println("hint            -Fill in one correct mapping that has not yet been guessed");
				System.out.println("exit            -Ends the game early");
				System.out.println("help            -List these commands");
			}
			else {
				System.out.println("Unknown command, type help to see commands");
			}
		}
		printBoard();
		System.out.println("You got it!\n");
	}

	/**
	 * Runs the full text game
	 * @throws FileNotFoundException if quotes.txt can't be found
	 */
	public void run() throws FileNotFoundException {
		System.out.println("Hello Gamers, Today we play Cryptograms");

		// Checks and repeats the game until the user wants to stop
		while (true) {
			System.out.println("Would you like to play? (y, n)");
			if (!key.hasNextLine()) {
				break;
			}
			String inp = key.nextLine().trim();
			if (inp.equals("y")) {
				control.newGame();
				playPuzzle();
			} else if (inp.equals("n")) {
				break;
			} else {
				System.out.println("Try Again");
			}
		}
		System.out.println("Ok have a good day");
	}
}
