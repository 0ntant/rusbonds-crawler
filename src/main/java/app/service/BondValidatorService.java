package app.service;

import app.model.Bond;
import lombok.Getter;

import java.util.*;

public class BondValidatorService
{
    @Getter
    List<Bond> invalidBonds ;
    public BondValidatorService()
    {
        invalidBonds = new ArrayList<>();
    }

    public Set<Bond> getDuplicatesIsins(List<Bond> bondsToCheck)
    {
        Set<Bond> duplicates = new HashSet<>() ;

        for (int i = 0; i < bondsToCheck.size() - 1; i++)
        {
            for(int j = i + 1; j < bondsToCheck.size(); j++)
            {
                if (bondsToCheck.get(i).getIsin().equals(
                        bondsToCheck.get(j).getIsin()) &&
                        !(bondsToCheck.get(i).getIsin().isEmpty() || bondsToCheck.get(i).getIsin().isBlank()))

                {
                    duplicates.add(bondsToCheck.get(i));
                    duplicates.add(bondsToCheck.get(j));
                }
            }
        }

        return duplicates;
    }
}
