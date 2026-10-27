import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
/**
 * @author: Samyak Gurung
 * Description: The main backend of the code that stores and creates the encryptions, answers
 * sends data to Crypto controller and CryptoGram. Checks for users replacement inputs and modifies the decrypted
 * string. Notifies its Observers (the GUI view) whenever a guess changes.
 * */
public class CryptogramModel extends Observable {
	//private variable(s) to store the answer, encryption key, decryption key
	private ArrayMap<Character, Character> encrypt;
	private ArrayMap<Character, Integer> count;

	private String answer;
	private String encrypted_quote;
	private String d_string;
	private ArrayMap<Character, Character> decrypt;

	/**
	 * Creates the model and starts the first puzzle
	 * @throws FileNotFoundException if quotes.txt can't be found
	 */
	public CryptogramModel() throws FileNotFoundException {
		newGame();
	}

	/**
     * Picks a new random quote and creates a ArrayMap encrypt that stores the chars
     * associated with the random string which then the strings are split to their chars
     * and encrypted in a ArrayMap based on their characters.
	 * @throws FileNotFoundException if quotes.txt can't be found
	 */
	public void newGame() throws FileNotFoundException {
        Random rand = new Random();
        encrypt = new ArrayMap<>();
        decrypt = new ArrayMap<>();
        count = new ArrayMap<>();

        File file = new File("quotes.txt");
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<Character> chars = new ArrayList<Character>();
        Scanner quotes = new Scanner(file);

        while (quotes.hasNextLine()) {
            String line = quotes.nextLine().trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        quotes.close();

        String q = lines.get(rand.nextInt(lines.size()));
        answer = q;

        for (char i = 'A'; i <= 'Z'; i++) {
            chars.add(i);
        }

        boolean valid = false;
        while (!valid) {
            Collections.shuffle(chars);
            valid = true;
            for (int i = 0; i < chars.size(); i++) {
                char original = (char) ('A' + i);
                char encrypted = chars.get(i);
                if (original == encrypted) {
                    valid = false;
                    break;
                }
            }
        }

        for (int i = 0; i < chars.size(); i++) {
            char original = (char) ('A' + i);
            char encrypted = chars.get(i);
            encrypt.put(encrypted, original);
        }

        encrypted_quote = "";
        d_string = "";

        for (int i = 0; i < answer.length(); i++) {
            char c = Character.toUpperCase(answer.charAt(i));
            if (c >= 'A' && c <= 'Z') {
                char encryptedChar = ' ';
                for (char encrypted : encrypt.keySet()) {
                    if (encrypt.get(encrypted) == c) {
                        encryptedChar = encrypted;
                        break;
                    }
                }
                encrypted_quote += encryptedChar;
                d_string += " ";

                if (!count.containsKey(encryptedChar)) {
                    count.put(encryptedChar, 1);
                } else {
                    count.put(encryptedChar, count.get(encryptedChar) + 1);
                }
                if (!decrypt.containsKey(encryptedChar)) {
                    decrypt.put(encryptedChar, null);
                }
            } else {
                encrypted_quote += answer.charAt(i);
                d_string += answer.charAt(i);
            }
        }
    }

	/**
	 *
	 * @return decrypt arraymap representation of user decryption
	 */
	public ArrayMap<Character, Character> getDecrypt(){
		return decrypt;
	}

	/**
	 *
	 * @return encrypt ArrayMap representation of user encryptions
	 */
	public ArrayMap<Character, Character> getEncrypt(){
		return encrypt;
	}

	/**
	 * @return Returns frequency of Encrypted characters
	 */
	public ArrayMap<Character, Integer> getFreqy(){
		return count;
	}

	/**
	 * Modifies the decrypt to store users replacement and tells the observers
	 * which encrypted letter changed.
	 * @param encryptedChar Character that is encrypted
	 * @param replacementChar User input for replacement, or ' ' to clear the guess
	 */
	public void setReplacement(char encryptedChar, char replacementChar) {
		if (replacementChar == ' ') {
			decrypt.put(encryptedChar, null);
		} else {
			decrypt.put(encryptedChar, replacementChar);
		}

		for(int i = 0; i < d_string.length(); i++) {
			if (encrypted_quote.charAt(i) == encryptedChar) {
				d_string = d_string.substring(0, i) + replacementChar +  d_string.substring(i+1);
			}
		}

		// Pass the encrypted letter so the view only updates those boxes
		setChanged();
		notifyObservers(encryptedChar);
	}

	/**
	 *
	 * @return return the fully encrypted string
	 */
	public String getEncryptedString() {
		return encrypted_quote;
	}
	/**
	 *
	 * @return the decrypted string with the guessed replacements or spaces
	 */
	public String getDecryptedString() {
		return d_string;
	}

	/**
	 *
	 * @return answer String or main string
	 */
	public String getAnswer() {
		return answer.toUpperCase();
	}
}