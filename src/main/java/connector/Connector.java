package connector;

import lombok.extern.slf4j.Slf4j;
import utils.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Connector {

    private final String URL_Key = "db.url";
    private final String User_Key = "db.user";
    private final String Password_Key = "db.password";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(Util.get(URL_Key), Util.get(User_Key), Util.get(Password_Key));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insertRecord(String brand, String model, int year, String fuelType) {
        String sql = """
                INSERT into cars (brand, model, year, fuel_type)
                VALUES (?,?,?,?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, brand);
            preparedStatement.setString(2, model);
            preparedStatement.setInt(3, year);
            preparedStatement.setString(4, fuelType);
            preparedStatement.executeUpdate();

            log.info("""
                The car with the following parameters:
                Brand: {}; Model: {}; Year: {}; Fuel type: {}; was added into the database successfully
                """, brand, model, year, fuelType);
        } catch (SQLException e) {
            System.out.println("Failed to get connection to dataBase");
        }
    }

    public void printAllTable() {
        String sql = """
                SELECT * from cars
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                System.out.println("id = " + result.getInt(1) + "; brand = " + result.getString(2)
                        + "; model = " + result.getString(3) + "; year " + result.getInt(4) +
                        "; fuel type = " + result.getString(5));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get connection to dataBase");
        }
    }

    public void printRowByNumber(int number) {
        String sql = """
                SELECT * from cars
                WHERE car_id = ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, number);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                System.out.println("id = " + result.getInt(1) + "; brand = " + result.getString(2)
                        + "; model = " + result.getString(3) + "; year " + result.getInt(4) +
                        "; fuel type = " + result.getString(5));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get connection to dataBase");
        }
    }

    public void deleteRowByNumber(int rowNumber) {
        String sql = """
                DELETE from cars
                WHERE car_id = ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, rowNumber);
            preparedStatement.executeUpdate();

            log.info("record by id {} was deleted successfully", rowNumber);
        } catch (SQLException e) {
            System.out.println("Failed to get connection to dataBase");
        }
    }

    public void updateField(String fieldToUpdate, String newValue, int recordNumber) {
        String sql = """
                UPDATE cars
                SET %s = ?
                WHERE car_id = ?
                """.formatted(fieldToUpdate);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newValue);
            preparedStatement.setInt(2, recordNumber);
            preparedStatement.executeUpdate();

            log.info("Value of {} in record {} was changed. The new value is: {}", fieldToUpdate, recordNumber, newValue);
        } catch (SQLException e) {
            System.out.println("Failed to get connection to dataBase");
        }
    }

    public List<Integer> getAllIdAsList() {
        List<Integer> RecordsNumbers = new ArrayList<>();
        String sql = "SELECT car_id FROM cars";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                RecordsNumbers.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Failed to get connection to dataBase");
        }
        return RecordsNumbers;
    }
}
