package mock;

import java.util.Random;

public class ExceptionProvider
{
    Random random = new Random();

    void exceptionErrorEvent()
    {
        if (random.nextInt(0, 100) < 10)
        {
            throw  new RuntimeException("MOCK exception");
        }
    }
}
