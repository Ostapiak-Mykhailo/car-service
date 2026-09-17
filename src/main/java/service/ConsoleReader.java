package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader {

    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));

    public String getInput() throws IOException {
        return READER.readLine();
    }

    public void suggestOptions(){
        String string = """
                Please choose action you would like to do:
                Enter '0' to exit the program
                Enter '1' to add new record to the dataBase
                Enter '2' to see record by number
                Enter '3' to see the whole table
                Enter '4' to update record
                Enter '5' to delete record
                """;
        System.out.println(string);
    }

    public int getNumberFromCustomer() throws IOException {
        int number = -1;
        while (number == -1) {
            try {
                number = Integer.parseInt(getInput());
            } catch (NumberFormatException e) {
                System.out.println("Incorrect input");
            }
        }
        return number;
    }

    public void suggestTable(){
        String message = """
                Enter 'cars' if you want to interact with car table
                Enter 'owners' if you want to interact with owner table
                Enter 'back' to get back to previous menu
                """;
        System.out.println(message);
    }
}
