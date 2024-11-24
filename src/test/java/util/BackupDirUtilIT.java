package util;

import app.util.BackupDirUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;


public class BackupDirUtilIT
{
    String sheetName = "TestData";

    @Test
    void getBackupFiles()
    {
        //given
        File[] files = BackupDirUtil.getBackupFiles(sheetName);
        //then
        //expected
        for (File file : files)
        {
            System.out.println(file.getAbsolutePath());
        }
    }

    @Test
    void deleteOldBackupFiles() throws IOException
    {
        //given
        String[] filesToCreate = {
            BackupDirUtil.createDirBackup(sheetName, LocalDate.of(2024, 2, 1)),
            BackupDirUtil.createDirBackup(sheetName, LocalDate.of(2024, 2, 2)),
            BackupDirUtil.createDirBackup(sheetName, LocalDate.of(2024, 2, 3)),
            BackupDirUtil.createDirBackup(sheetName, LocalDate.of(2024, 2, 4)),
            BackupDirUtil.createDirBackup(sheetName, LocalDate.of(2024, 2, 5)),
            BackupDirUtil.createDirBackup(sheetName, LocalDate.of(2024, 2, 6)),
        };
        for (String fileToCreate : filesToCreate)
        {
            new File(fileToCreate).createNewFile();
        }

        //then
        BackupDirUtil.deleteOldBackups(sheetName);

        //expected
        assertEquals(BackupDirUtil.backupsLimit, BackupDirUtil.getBackupFiles(sheetName).length);
    }
}
