package app.services;

public class Parse {

    public static Integer tryParseInt(String value) {  //Overvej at put denne i en anden klasse, hvis vi bruger den i mere end den her
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    public static Boolean tryParseBoolean(String value) {
        if (value == null || value.startsWith("Med/uden")){
            return null;
        }
        return value.equalsIgnoreCase("ja") || value.startsWith("Med"); //Hvis ja eller med, så returneres true, ellers false
    }
}
