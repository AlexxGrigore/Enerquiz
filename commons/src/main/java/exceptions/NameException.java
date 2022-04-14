package exceptions;

public class NameException extends Exception {
    /**
     * Creates new name exception.
     * @param errorMessage error message.
     */
    public NameException(String errorMessage) {
        super(errorMessage);
    }
}
