package app.exception;

public class InvalidRusbondResponseException extends RuntimeException
{
    public InvalidRusbondResponseException(String message)
    {
        super(message);
    }

    public InvalidRusbondResponseException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
