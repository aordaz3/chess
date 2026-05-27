package ui;

import javax.imageio.IIOException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PreloginUI implements UI{
    private final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    @Override
    public UI run() {

        System.out.println("WELCOM TO CHESS. TYPE HELP TO GET STARTED.");

        while (true){
            System.out.println("[LOGGED_OUT]>>>>");
            String input;
            try{
                input = in.readLine();
            }
            catch (IOException e){
                System.out.println("Input Error");
                return this;
            }
            if(input == null){
                return null;
            }
            if(input.isEmpty()){
                continue;
            }
            input.toLowerCase();
            if(input.equals("help")){
                printHelp();
            }

        }
    }

    private void printHelp(){
        System.out.println("register <USERNAME> <PASSWORD> <EMAIL> - to create an account");
        System.out.println("login <USERNAME> <PASSWORD> - to play chess");
        System.out.println("quit - playing chess");
        System.out.println("help - with possible commands");
    }
}
