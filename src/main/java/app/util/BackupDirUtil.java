package app.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class BackupDirUtil
{
    private static final DateTimeFormatter formatter
            = DateTimeFormatter.ofPattern("M_d_yyyy");
    public static final int backupsLimit = 5;

    public static String createBackupDirDeleteOld(String sheetName)
    {
        String dirBackup = createDirBackup(sheetName, LocalDate.now());
        deleteOldBackups(sheetName);
        return dirBackup;
    }

    public static String createDirBackup(String sheetName)
    {
       return createDirBackup(sheetName, LocalDate.now());
    }

    public static String createDirBackup(String sheetName, LocalDate localDate)
    {
        File sheetDumpDir = getSheetDumpDir(sheetName);

        return String.format(
                "%s/%s_%s_backup.csv",
                sheetDumpDir.getAbsolutePath(),
                localDate.format(formatter),
                sheetName
        );
    }

    public static void deleteOldBackups(String sheetName)
    {
        deleteOldBackups(getBackupFiles(sheetName));
    }

    private static void deleteOldBackups(File[] backups)
    {
        if (backups.length <= backupsLimit)
        {
            return;
        }
        sortByCreationDate(backups);
        for (int i = backupsLimit; i < backups.length; i++)
        {
            File backupToDelete = backups[i];
            BasicFileAttributes deleteFileAttr = getFileAttr(backupToDelete);
            if(backupToDelete.delete())
            {
                log.info("Delete old backup: {} create date: {}",
                        backupToDelete.getAbsolutePath(),
                        deleteFileAttr.creationTime()
                );
            }
        }
    }

    private static void sortByCreationDate(File[] files)
    {
        Arrays.sort(files, (file1, file2) ->
        {
                BasicFileAttributes attr1 = getFileAttr(file1);
                BasicFileAttributes attr2 = getFileAttr(file2);

                return attr2.creationTime().compareTo(attr1.creationTime());
        });
    }

    private static BasicFileAttributes getFileAttr(File file)
    {
        try
        {
            return Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static File[] getBackupFiles(String sheetName)
    {
        File sheetDumpDir = getSheetDumpDir(sheetName);
        return sheetDumpDir.listFiles();
    }

    private static File getSheetDumpDir(String sheetName)
    {
        File file = new File("");
        File sheetDumpDir = new File("/%s/dump/%s".formatted(file.getAbsoluteFile(), sheetName));
        if (!sheetDumpDir.exists())
        {
            log.info("Dump dir {} do not exists and will be create",
                    sheetDumpDir
            );
            sheetDumpDir.mkdir();
        }
        return sheetDumpDir;
    }
 }
