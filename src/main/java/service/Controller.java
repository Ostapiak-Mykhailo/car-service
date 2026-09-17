package service;

import connector.Connector;
import validator.ValidationException;
import validator.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Controller {

    private final ConsoleReader reader = new ConsoleReader();
    private final Connector connector = new Connector();
    private final Validator validator = new Validator();

    public void runApp() throws IOException {
        connector.createTableOwners();
        connector.createTableCars();
        boolean running = true;
        while (running) {
            reader.suggestOptions();
            int number = getOptionFromCustomer();
            if (number == 0) {
                running = false;
            }
            findOption(number);
        }
    }

    private Integer getOptionFromCustomer() throws IOException {
        int number = reader.getNumberFromCustomer();
        while (!Set.of(0, 1, 2, 3, 4, 5).contains(number)) {
            System.out.println("Incorrect input, please try again");
            number = reader.getNumberFromCustomer();
        }
        return number;
    }

    private void findOption(Integer number) throws IOException {
        switch (number) {
            case 1 -> addNewRecordToDataBase();
            case 2 -> printRecordByNumber();
            case 3 -> printWholeTable();
            case 4 -> updateRecordByNumber();
            case 5 -> deleteRecordByNumber();
        }
    }

    private void updateRecordByNumber() throws IOException {
        reader.suggestTable();
        String table = chooseTable();
        if ("back".equals(table)) {
            return;
        }
        if (isTableEmpty(table)) {
            System.out.println("This Table is empty yet");
            return;
        }
       connector.printWholeTable(table);
        System.out.println("Please enter the number of record you want to update");
        int recordNumber = reader.getNumberFromCustomer();

        if (!isRecordExists(table, recordNumber)) {
            System.out.println("There is no record with this number in the Table");
            return;
        }

        switch (table) {
            case "cars" -> updateCarRecordByNumber(table, recordNumber);
            case "owners" -> updateOwnerRecordByNumber(table, recordNumber);
        }
    }

    private void updateCarRecordByNumber(String table, Integer recordNumber) throws IOException {

        System.out.println("which field would you like to change? select from 'brand', 'model', 'year', or 'fuel_type'");
        String fieldToUpdate = defineCarFieldToUpdate();

        System.out.println("Please enter a new value");
        String newValue = reader.getInput();

        connector.updateField(table, fieldToUpdate, newValue, recordNumber);
    }

    private void updateOwnerRecordByNumber(String table, Integer recordNumber) throws IOException {

        System.out.println("which field would you like to change? select from 'first_name', 'last_name', " +
                "'phone_number', or 'email'");
        String fieldToUpdate = defineOwnerFieldToUpdate();

        System.out.println("Please enter a new value");
        String newValue = reader.getInput();

        connector.updateField(table, fieldToUpdate, newValue, recordNumber);
    }

    private void addNewRecordToDataBase() throws IOException {
        reader.suggestTable();
        String table = chooseTable();

        if ("back".equals(table)) {
            return;
        }
        switch (table) {
            case "cars" -> addNewCarToDataBase();
            case "owners" -> addNewOwnerToDataBase();
        }
    }

    private void printRecordByNumber() throws IOException {
        reader.suggestTable();
        String table = chooseTable();
        if ("back".equals(table)) {
            return;
        }
        printAllRecordId(table);
        System.out.println("Please enter the number of record you want to see");
        int recordNumber = reader.getNumberFromCustomer();

        if (!isRecordExists(table, recordNumber)) {
            System.out.println("There is no record by this number in this table");
            return;
        }
        switch (table){
            case "owners" -> connector.printOwnerByNumber(recordNumber);
            case "cars" -> connector.printCarByNumber(recordNumber);
        }
    }

    private void addNewCarToDataBase() throws IOException {
        System.out.println("What is the brand of the car?");
        String brand = reader.getInput();

        System.out.println("Model");
        String model = reader.getInput();

        System.out.println("Year");
        int year = reader.getNumberFromCustomer();
        validator.validateYearOfCar(year);

        System.out.println("Fuel type (petrol, diesel, gas, or electric)");
        String fuel = reader.getInput();
        validator.validateFuelType(fuel);

        System.out.println("Who owns this car? Enter the person`s id");
        connector.printWholeTable("owners");

        int ownerId = reader.getNumberFromCustomer();
        if (!isRecordExists("owners", ownerId)) {
            throw new ValidationException("There`s no owner by this id number");
        }
        connector.insertNewCar(brand, model, year, fuel, ownerId);
    }

    private void addNewOwnerToDataBase() throws IOException {
        System.out.println("Enter first name");
        String firstName = reader.getInput();

        System.out.println("Enter last name");
        String lastName = reader.getInput();

        System.out.println("Enter phone number name");
        String phoneNumber = reader.getInput();
        validator.validatePhoneNumber(phoneNumber);

        System.out.println("Enter email");
        String email = reader.getInput();
        validator.validateEmail(email);

        connector.insertNewOwner(firstName, lastName, phoneNumber, email);
    }

    private void printWholeTable() throws IOException {
        reader.suggestTable();
        String table = chooseTable();
        if ("back".equals(table)) {
            return;
        }
        if (isTableEmpty(table)) {
            System.out.println("This Table is empty yet");
            return;
        }
        System.out.println("Now you can see all records in " + table + " table");
        connector.printWholeTable(table);
    }

    private boolean isTableEmpty(String table) {
        return connector.getAllRecordIdAsList(table).isEmpty();
    }

    private boolean isRecordExists(String table, Integer number) {
        List<Integer> records = connector.getAllRecordIdAsList(table);
        return records.contains(number);
    }

    private void deleteRecordByNumber() throws IOException {
        reader.suggestTable();
        String table = chooseTable();
        if ("back".equals(table)) {
            return;
        }
        printAllRecordId(table);
        System.out.println("Please enter the number of record you want to delete");
        int recordNumber = reader.getNumberFromCustomer();

        if (!isRecordExists(table, recordNumber)) {
            System.out.println("There is no record with this number in the dataBase");
            return;
        }
        connector.deleteRecordByNumber(recordNumber, table);
    }


    private String defineCarFieldToUpdate() throws IOException {
        String field = reader.getInput();
        while (!Set.of("brand", "model", "year", "fuel_type").contains(field)) {
            System.out.println("Incorrect input. Please try again");
            field = reader.getInput();
        }
        return field;
    }

    private String defineOwnerFieldToUpdate() throws IOException {
        String field = reader.getInput();
        while (!Set.of("first_name", "last_name", "phone_number", "email").contains(field)) {
            System.out.println("Incorrect input. Please try again");
            field = reader.getInput();
        }
        return field;
    }

    private String chooseTable() throws IOException {
        String table = reader.getInput();
        while (!Set.of("cars", "owners", "back").contains(table)) {
            System.out.println("Incorrect input. Please try again");
            table = reader.getInput();
        }
        return table;
    }

    private void printAllRecordId(String table) {
        List<Integer> recordId = connector.getAllRecordIdAsList(table);
        String message = """
                Records for the following ID numbers are available in this Table:
                %s
                """.formatted(recordId);
        System.out.println(message);
    }
}
