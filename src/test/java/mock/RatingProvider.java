package mock;

import java.util.Random;

public class RatingProvider
{
    Random random = new Random();
    String[] ratingTable = {
            "D-",
            "D",
            "D+",
            "DD-",
            "DD",
            "DD+",
            "DDD-",
            "DDD",
            "DDD+",
            "C-",
            "C",
            "C+",
            "CC-",
            "CC",
            "CC+",
            "CCC-",
            "CCC",
            "CCC+",
            null,
            "B-",
            "B",
            "B",
            "B+",
            "BB-",
            "BB",
            "BB+",
            "BBB-",
            "BBB",
            "BBB+",
            "A-",
            "A",
            "A+",
            "AA-",
            "AA",
            "AA+",
            "AAA-",
            "AAA",
            "AAA+"
    };

    String getBondRating()
    {
        String rating = ratingTable[random.nextInt(ratingTable.length)];
        rating = rating == null ? rating : rating.concat("(mock)");
        return rating ;
    }
}
