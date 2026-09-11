package service;

import connector.Connector;
import validator.Validator;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Controller {

    private final ConsoleReader reader = new ConsoleReader();
    private final Connector connector = new Connector();
    private final Validator validator = new Validator();

    public void runApp() throws IOException {
        boolean running = true;
        while (running) {
            reader.SuggestOptions();
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
            number = reader.getNumberFromCustomer();
            if (!Set.of(0, 1, 2, 3, 4, 5).contains(number)) {
                System.out.println("Incorrect input, please try again");
            }
        }
        return number;
    }

    private void findOption(Integer number) throws IOException {
        switch (number) {
            case 1 -> insertRecord();
            case 2 -> printRecordByNumber();
            case 3 -> printAllTable();
            case 4 -> updateRecordByNumber();
            case 5 -> deleteRecordByNumber();
        }
    }

    private void insertRecord() throws IOException {
        System.out.println("What is the make of the car?");
        String brand = reader.getInput();

        System.out.println("Model");
        String model = reader.getInput();

        System.out.println("Year");
        int year = reader.getNumberFromCustomer();
        validator.validateYearOfCar(year);

        System.out.println("Fuel type (petrol, diesel, gas, or electric)");
        String fuel = reader.getInput();
        validator.validateFuelType(fuel);
        connector.insertRecord(brand, model, year, fuel);
    }

    private void printAllTable() {
        if (isAnyRecord()) {
            System.out.println("DataBase is empty yet");
            return;
        }
        System.out.println("Now you can see the whole dataBase");
        connector.printAllTable();
    }

    private boolean isAnyRecord() {
        return connector.getAllIdAsList().isEmpty();
    }

    private void printRecordByNumber() throws IOException {
        System.out.println("Please enter the number of record you want to see");
        int recordNumber = reader.getNumberFromCustomer();

        if (!isRecordExists(recordNumber)) {
            System.out.println("There is no record with this number in the dataBase");
            return;
        }

        System.out.println("Now you can see dataBase record by number " + recordNumber);
        connector.printRowByNumber(recordNumber);
    }

    private boolean isRecordExists(Integer number) {
        List<Integer> records = connector.getAllIdAsList();
        return records.contains(number);
    }

    private void deleteRecordByNumber() throws IOException {
        System.out.println("Please enter the number of record you want to delete");
        int recordNumber = reader.getNumberFromCustomer();

        if (!isRecordExists(recordNumber)) {
            System.out.println("There is no record with this number in the dataBase");
            return;
        }
        connector.deleteRowByNumber(recordNumber);
    }

    private void updateRecordByNumber() throws IOException {
        System.out.println("Please enter the number of record you want to update");
        int recordNumber = reader.getNumberFromCustomer();

        if (!isRecordExists(recordNumber)) {
            System.out.println("There is no record with this number in the dataBase");
            return;
        }

        System.out.println("which field would you like to change? select from 'brand', 'model', 'year', or 'fuel type'");
        String fieldToUpdate = defineFieldToUpdate();

        System.out.println("Please enter a new value");
        String newValue = reader.getInput();

        connector.updateField(fieldToUpdate, newValue, recordNumber);
    }

    private String defineFieldToUpdate() throws IOException {
        String field = "";
        while (!Set.of("brand", "model", "year", "fuel type").contains(field)) {
            field = reader.getInput();
            if (!Set.of("brand", "model", "year", "fuel_type").contains(field)) {
                System.out.println("Incorrect input. Please try again");
            }
        }
        return field;
    }
}
