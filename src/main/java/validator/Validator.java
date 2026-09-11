package validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public void validateYearOfCar(int year){
        if (year < 1900 ){
            throw new ValidationException("There were no cars back then.");
        }
        if (year > 2026){
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
}
