package service;

import app.model.AccountingPolicy;
import app.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateBondServiceModule
{
    @Mock
    RusbondsService rusbondsServ;
    @Mock
    BondService bondSheetServ;
    @Mock
    SelRusbondsServiceProxy selRusbondsSer;
    List<AccountingPolicy> accPolices;
    @InjectMocks
    UpdateBondService updateSheetServ;

    @Test
    void UpdateNotNeeded_BondAlreadyModifyToday()
    {
        //given
        doReturn(LocalDate.now())
                .when(bondSheetServ).getModifyDate();

        //then
        boolean isNeedUpdate = updateSheetServ.isNeedUpdate();

        //expected
        assertFalse(isNeedUpdate);
    }

    @Test
    void updateNotStarted_BondAlreadyModifyToday()
    {
        //given
        doReturn(LocalDate.now())
                .when(bondSheetServ)
                .getModifyDate();

        //then
        boolean isNeedUpdate = updateSheetServ.isNeedUpdate();
        updateSheetServ.startUpdateProcess();

        //expected
        assertFalse(isNeedUpdate);
    }
}
