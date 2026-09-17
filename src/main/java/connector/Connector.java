package connector;

import lombok.extern.slf4j.Slf4j;
import utils.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Connector {

    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.user";
    private static final String PASSWORD_KEY = "db.password";

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(Util.get(URL_KEY), Util.get(USER_KEY), Util.get(PASSWORD_KEY));
        } catch (SQLException e) {
            log.error("Failed to get connection to dataBase");
            throw new RuntimeException(e);
        }
    }

    public void createTableOwners() {
        String sql = """
                CREATE TABLE IF NOT EXISTS owners(
                id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                first_name VARCHAR(64) NOT NULL,
                last_name VARCHAR(64) NOT NULL,
                phone_number VARCHAR(64),
                email VARCHAR(64)
                );
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Failed to create table owners", e);
        }
    }

    public void createTableCars() {
        String sql = """
                CREATE TABLE IF NOT EXISTS cars(
                id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                brand VARCHAR(64) NOT NULL,
                model VARCHAR(64) NOT NULL,
                year INT,
                fuel_type VARCHAR(64),
                owner_id INT,
                constraint fk_car_owner foreign key (owner_id) references owners(id)
                ON DELETE CASCADE
                );
                """;

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Failed to create table cars", e);
        }
    }

    public void insertNewCar(String brand, String model, int year, String fuelType, int ownerId) {
        String sql = """
                INSERT INTO cars (brand, model, year, fuel_type, owner_id)
                VALUES (?,?,?,?,?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, brand);
            preparedStatement.setString(2, model);
            preparedStatement.setInt(3, year);
            preparedStatement.setString(4, fuelType);
            preparedStatement.setInt(5, ownerId);
            preparedStatement.executeUpdate();

            log.info("""
                    The car with the following parameters:
                    Brand: {}; Model: {}; Year: {}; Fuel type: {}; Owner id: {}; was added into the database 
                    successfully
                    """, brand, model, year, fuelType, ownerId);
        } catch (SQLException e) {
            log.error("Failed to add new car record", e);
        }
    }

    public void insertNewOwner(String firstName, String lastName, String phoneNumber, String email) {
        String sql = """
                INSERT INTO owners (first_name, last_name, phone_number, email)
                VALUES (?,?,?,?)
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, phoneNumber);
            preparedStatement.setString(4, email);
            preparedStatement.executeUpdate();
            log.info("""
                    New owner with the following parameters:
                    First name: {}; Last name: {}; Phone number: {};, Email: {} was added successfully
                    """, firstName, lastName, phoneNumber, email);
        } catch (SQLException e) {
            log.error("Failed to add new owner record", e);
        }
    }

    public void printWholeTable(String table) {
        String sql = """
                SELECT * from %s
                """.formatted(table);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                if ("cars".equals(table)) {
                    System.out.println("id = " + result.getInt(1) + "; brand = " + result.getString(2)
                            + "; model = " + result.getString(3) + "; year " + result.getInt(4) +
                            "; fuel type = " + result.getString(5));
                } else {
                    System.out.println("id = " + result.getInt(1) + "; first name = " +
                            result.getString(2) + "; last name = " + result.getString(3));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to get records from table {}", table);
        }
    }

    public void printCarByNumber(int number) {
        String sql = """
                SELECT  cars.*, owners.first_name, owners.last_name
                FROM cars
                JOIN owners ON owners.id = cars.owner_id
                WHERE cars.id = ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, number);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                System.out.println("id = " + result.getInt(1) + "; brand = " +
                        result.getString(2) + "; model = " + result.getString(3) +
                        "; year " + result.getInt(4) + "; fuel type = " +
                        result.getString(5) + "; Owner = " + result.getString("first_name") +
                        " " + result.getString("last_name"));
            }
        } catch (SQLException e) {
            log.error("Failed to get record {} from table cars", number, e);
        }
    }

    public void printOwnerByNumber(int number) {
        String sql = """
                SELECT owners.*, cars.brand, cars.model
                FROM owners
                LEFT JOIN cars ON cars.owner_id = owners.id
                WHERE owners.id = ?
                """;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, number);
            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                System.out.println("id = " + result.getInt(1) + "; first name = " +
                        result.getString(2) + "; last name = " + result.getString(3) +
                        "; phone number " + result.getString(4) + "; email = " +
                        result.getString(5) + "; Cars: " + result.getString("brand") + " "
                        + result.getString("model"));
            }
        } catch (SQLException e) {
            log.error("Failed to get record {} from table owners", number, e);
        }
    }

    public void deleteRecordByNumber(int rowNumber, String table) {
        String sql = """
                DELETE from %s
                WHERE id = ?
                """.formatted(table);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, rowNumber);
            preparedStatement.executeUpdate();

            log.info("record by id {} was deleted successfully from table {}", rowNumber, table);
        } catch (SQLException e) {
            log.error("Failed to delete record {} from table {}", rowNumber, table, e);
        }
    }

    public void updateField(String table, String fieldToUpdate, String newValue, int recordNumber) {
        String sql = """
                UPDATE %s
                SET %s = ?
                WHERE id = ?
                """.formatted(table, fieldToUpdate);
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, newValue);
            preparedStatement.setInt(2, recordNumber);
            preparedStatement.executeUpdate();

            log.info("Table {} was updated. Value of {} in record {} was changed. The new value is: {}", table,
                    fieldToUpdate, recordNumber,
                    newValue);
        } catch (SQLException e) {
            log.error("Failed to update {} in record {} from table {}", fieldToUpdate, recordNumber, table, e);
        }
    }

    public List<Integer> getAllRecordIdAsList(String table) {
        List<Integer> recordNumbers = new ArrayList<>();
        String sql = "SELECT id FROM %s".formatted(table);

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                recordNumbers.add(resultSet.getInt(1));
            }
        } catch (SQLException e) {
            log.error("Failed to get records from table {}", table);
        }
        return recordNumbers;
    }
}
