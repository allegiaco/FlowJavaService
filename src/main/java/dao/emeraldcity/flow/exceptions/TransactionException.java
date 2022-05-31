package dao.emeraldcity.flow.exceptions;

public class TransactionException extends Exception {

    public TransactionException (String message, Class<?> clazz) {
        super(String.format(message, clazz.getSimpleName()));
    }

    public TransactionException() {

    }
}
