package service;

import app.model.Bond;
import app.service.BondValidatorService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class BondValidatorServiceModule
{
    BondValidatorService bondValidServ = new BondValidatorService();

    @Test
    void checkDuplicates_findOneOrMore()
    {
        //given
        List<Bond> bonds = List.of( Bond.builder().number(1).isin("isin_1").build(),
        Bond.builder().number(2).isin("isin_2").build(),
        Bond.builder().number(3).isin("isin_3").build(),
        Bond.builder().number(4).isin("isin_4").build(),
        Bond.builder().number(5).isin("isin_2").build(),
        Bond.builder().number(6).isin("isin_2").build(),
        Bond.builder().number(7).isin("isin_5").build(),
        Bond.builder().number(8).isin("isin_5").build(),
        Bond.builder().number(9).isin("").build(),
        Bond.builder().number(10).isin("").build()
        );
        //then

        Set<Bond> bondsDuplicates = bondValidServ.getDuplicatesIsins(bonds);

        //expected
        assertEquals(5, bondsDuplicates.size());
    }
}
