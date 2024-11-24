package service;

import app.model.GoogleSheet;
import app.service.UpdateManagerService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateManagerServiceIT
{
    UpdateManagerService updateManagerServ
            = new UpdateManagerService(
                    List.of(new GoogleSheet("TestData"),
                            new GoogleSheet("TestDataCopy")
                    )
    );

    @Test
    void getOldestSheetToUpdate_TestDateSheet()
    {
        //given
        GoogleSheet oldestSheetToUpdate = updateManagerServ.getOldest();

        //then
        //expected
        assertEquals("TestDataCopy", oldestSheetToUpdate.getName());
    }
}
