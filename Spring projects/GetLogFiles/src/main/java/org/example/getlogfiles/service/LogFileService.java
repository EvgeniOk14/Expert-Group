package org.example.getlogfiles.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class LogFileService
{
    private static final String LOG_DIRECTORY = "C:\\Expert Group\\LoggerLathe\\logfiles";
    private static final Pattern LOG_FILE_PATTERN = Pattern.compile("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt");

    public List<String> getAllLogFiles()
    {
        List<String> logFiles = new ArrayList<>();
        File directory = new File(LOG_DIRECTORY);
        if (directory.exists() && directory.isDirectory())
        {
            File[] files = directory.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    if (file.isFile() && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt")) {
                        logFiles.add(file.getName());
                    }
                }
            }
        }
        return logFiles;
    }

    public String getLogFileContent(String partialFileName) throws IOException
    {
        File directory = new File(LOG_DIRECTORY);
        File[] files = directory.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isFile() && file.getName().contains(partialFileName) && file.getName().matches("\\d{4}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\d{2}_\\w{8}.txt")) {
                    return new String(Files.readAllBytes(file.toPath()));
                }
            }
        }
        return null;
    }

}







//public String getLogFileContentByName(String partialFileName) throws IOException {
//    File directory = new File(LOG_DIRECTORY);
//    if (directory.exists() && directory.isDirectory())
//    {
//        // File[] files = directory.listFiles((dir, name) -> name.contains(partialFileName) && LOG_FILE_PATTERN.matcher(name).matches());
//
//        File[] files = directory.listFiles(new FilenameFilter()
//        {
//            @Override
//            public boolean accept(File dir, String name)
//            {
//                return name.contains(partialFileName) && LOG_FILE_PATTERN.matcher(name).matches();
//            }
//        });
//
//
//        if (files != null && files.length > 0)
//        {
//            File logFile = files[0];
//            if (logFile.exists() && logFile.isFile()) {
//                return new String(Files.readAllBytes(logFile.toPath()));
//            }
//        }
//    }
//    return null;
//}