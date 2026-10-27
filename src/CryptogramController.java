import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther: Samyak Gurung
 * Description: The controller section that is reponsible for getting
 * information from provided CryptogramModel and the checks whether the user got the answer or not
 * and gets encrypted quote and user progress. It is also responsible for replacing strings.
 * It is shared by both the text view and the GUI view.
 * */

public class CryptogramController {
	private CryptogramModel backend;
	public CryptogramController (CryptogramModel model) {
		backend = model;
	}

	/**
	 * Returns whether the code is correct
	 * @return status on game boolean
	 */
	public boolean isGameOver() {
		return backend.getDecryptedString().equals(backend.getAnswer());
	}

	/**
	 * Starts a new puzzle with a new random quote
	 * @throws FileNotFoundException if quotes.txt can't be found
	 */
	public void newGame() throws FileNotFoundException {
		backend.newGame();
	}

	/**
	 * Returns how many times an encrypted letter appears in the quote
	 * @param letter encrypted letter
	 * @return count of the letter, 0 if it doesn't appear
	 */
	public int getFrequency(char letter) {
		Integer c = backend.getFreqy().get(letter);
		if (c == null) {
			return 0;
		}
		return c;
	}

	/**
	 * conducts replacement by using CryptogramModel methods
	 * @param letterToReplace encrypted letter
	 * @param replacementLetter users guess, or ' ' to clear it
	 */
	public void makeReplacement(char letterToReplace, char replacementLetter) {
		backend.setReplacement(Character.toUpperCase(letterToReplace),
				Character.toUpperCase(replacementLetter));
	}

	/**
	 * Returns the users current guess for an encrypted letter
	 * @param encryptedLetter encrypted letter
	 * @return guess or null if not guessed yet
	 */
	public Character getGuess(char encryptedLetter) {
		return backend.getDecrypt().get(encryptedLetter);
	}

	/**
	 * Fills in one encrypted letter that is not guessed correctly yet
	 * @return the encrypted letter that was filled in, or ' ' if everything is already correct
	 */
	public char giveHint() {
		String quote = backend.getEncryptedString();
		for (int i = 0; i < quote.length(); i++) {
			char c = quote.charAt(i);
			Character correct = backend.getEncrypt().get(c);
			if (correct != null && !correct.equals(getGuess(c))) {
				makeReplacement(c, correct);
				return c;
			}
		}
		return ' ';
	}

	/**
	 * for the view to display
	 * @return Encrypted String
	 */
	public String getEncryptedQuote() {
		return backend.getEncryptedString();
	}

	/**
	 * for the view to display
	 * @return Decrypted String of Chars
	 */
	public String getUsersProgress() {
		return backend.getDecryptedString();
	}

	/**
	 * Breaks a string the same length as the quote (the encrypted quote or the users progress)
	 * into lines no longer than width. The break points are found from the spaces in the
	 * encrypted quote, so both strings always break at the same places.
	 * @param text the string to split
	 * @param width max characters per line
	 * @return list of lines
	 */
	public List<String> splitIntoLines(String text, int width) {
		String quote = backend.getEncryptedString();
		List<String> lines = new ArrayList<>();
		int start = 0;
		while (start < quote.length()) {
			int end;
			int next;
			if (quote.length() - start <= width) {
				end = quote.length();
				next = end;
			} else {
				// break on the last space that still fits, and drop that space
				int space = quote.lastIndexOf(' ', start + width);
				if (space > start) {
					end = space;
					next = space + 1;
				} else {
					end = start + width;
					next = end;
				}
			}
			lines.add(text.substring(start, end));
			start = next;
		}
		return lines;
	}
}
