import java.io.FileNotFoundException;
import javafx.application.Application;

/**
 * @auther: Samyak Gurung
 * Description: 
 * */
public class Cryptogram {

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0 && args[0].equals("-text")) {
            new CryptogramTextView().run();
        } else {
            Application.launch(CryptogramGUIView.class, args);
        }
    }
}