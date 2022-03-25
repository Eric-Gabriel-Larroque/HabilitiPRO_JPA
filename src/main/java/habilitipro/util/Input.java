package habilitipro.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Input {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Logger LOG = LogManager.getLogger(Input.class);

    public static String validateString(String message){
        boolean repeat = true;
        String input = null;
        while(repeat) {
            System.out.print(message);
            input = SCANNER.nextLine();
            if(input==null||input.isBlank()||input.isEmpty()) {
                LOG.info("Entrada de dados inválida. Tente novamente");
            }else {
                repeat = false;
            }
        }
        return input;
    }
}
