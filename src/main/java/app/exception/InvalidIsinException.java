package app.exception;

public class InvalidIsinException extends RuntimeException
{
    public InvalidIsinException(String message)
    {
        super(message);
    }

    public InvalidIsinException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
