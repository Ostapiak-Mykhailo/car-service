package validator;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public void validateYearOfCar(int year){
        if (year < 1900 ){
            throw new ValidationException("There were no cars back then.");
        }
        if (year > LocalDate.now().getYear()){
            throw new ValidationException("This year hasn't arrived yet.");
        }
    }

    public void validateFuelType(String fuelType){
        Pattern pattern = Pattern.compile("petrol|diesel|gas|electric");
        Matcher matcher = pattern.matcher(fuelType);
        if (!matcher.matches()){
            throw new ValidationException("Unknown fuel type");
        }
    }

    public void validatePhoneNumber(String string) {
        Pattern pattern = Pattern.compile("^[+]?380\\d{9}$");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            throw new ValidationException("Incorrect input");
        }
    }

    public void validateEmail(String string) {
        Pattern pattern = Pattern.compile("^([\\w-&&[^а-яА-Я]]{3,})@([\\w-&&[^а-яА-Я]]+)\\.([\\w-&&[^а-яА-Я]]{2,})" +
                "(\\.?([\\w-&&[^а-яА-Я]]{2,}))?$");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            throw new ValidationException("Incorrect input");
        }
    }
}
