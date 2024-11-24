package app.algoritm;

import app.service.UpdateManagerService;

public class UpdateDataAlg
{
    public static void start()
    {
        UpdateManagerService updateServ = new UpdateManagerService();
        updateServ.updateOldestSheet();
    }
}
