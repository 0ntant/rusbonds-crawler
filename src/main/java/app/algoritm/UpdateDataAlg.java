package app.algoritm;

import app.service.UpdateSheetService;

public class UpdateDataAlg
{
    public static void start()
    {
        UpdateSheetService updateSheetService = new UpdateSheetService();
        updateSheetService.startUpdate();
    }
}
