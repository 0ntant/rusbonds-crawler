package service;

import app.model.Bond;
import app.model.DataCell;
import app.service.BondSnapshotService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BondSnapshotServiceIT
{
    BondSnapshotService bondService = new BondSnapshotService("TestData");

    @Test
    void mapDate()
    {
        System.out.println("420/12.5".contains("/"));


    }

    @Test
    void getAndWriteBonds()
    {
        //given

        List<Bond> bonds = bondService.getAll();

        //then
        bondService.writeBonds(bonds);
    }

    @Test
    void getBondValus()
    {
        //given
        List<List<DataCell>> dataCells = bondService.getSnapshot();
        //then
        //expected
        List<DataCell> dataCells1 = dataCells.get(0);
        for (int j = 0; j < dataCells1.size() ; j++)
        {
            System.out.printf("%s %s %s %s",
                    dataCells1.get(j).getValue(),
                    dataCells1.get(j).getNote(),
                    dataCells1.get(j).getCellColor(),
                    dataCells1.get(j).getFormula()
            );
            System.out.println();
        }
    }

    @Test
    void getDataCells()
    {
        //given
        List<List<DataCell>> dataCells = bondService.getSnapshot();

        //then
        //expected
        for(int i = 0; i < 5 ; i++)
        {
            List<DataCell> dataCells1 = dataCells.get(i);
            for (int j = 0; j < 10 ; j++)
            {
                System.out.printf("%s %s %s %s",
                        dataCells1.get(j).getValue(),
                        dataCells1.get(j).getNote(),
                        dataCells1.get(j).getCellColor(),
                        dataCells1.get(j).getFormula()
                );
                System.out.println();
            }
        }
        bondService.writeBonds(null);
    }

    @Test
    void getBondsCount_return29()
    {
        //given
        int count = bondService.getMaxRow();
        //then

        //expect
        assertEquals(count, 29);
    }
}
