package app.exceptions;

public class IllegalInputException extends RuntimeException {
    public IllegalInputException(String message) {
        super(message);
    }
} //egentlig tiltænkt hvis man ikke havde valgt noget i en kategori i customMadeSite
