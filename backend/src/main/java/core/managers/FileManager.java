package core.managers;

import UI.Controllers.MainController;
import core.MyLogger;
import core.Runner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileManager
{
    public static final String logsFolder = "logs/";
    public static final String appiumLogs = logsFolder + "appium_%s.log";
    public static final String nodejsFolder = "node-js/";
    public static final String nodejsEXE = nodejsFolder + "node.exe";
    public static final String nodejsAppium = nodejsFolder + "node_modules/appium/build/lib/main.js";
    public static final String sdkFolder = "sdk/";
    public static final String adbEXE = sdkFolder + "platform-tools/adb.exe";
    public static final String jdkFolder = "jdk/";
    public static final String javaExe = jdkFolder + "bin/java.exe";
    public static final String inputConfigFile = "inputConfig.xml";
    public static final String DataBreakdownEXE = "DataBreakdown.exe";
    public static final String MemInfoEXE = "memInfoTask.exe";
    public static final String eeiDependencies = "C:/EEIDependencies/";
    public static final String scrcpyFolder = "scrcpy/";
    public static final String scrcpyEXE = scrcpyFolder + "scrcpy.exe";


    /**
     * Gets the proper location of the dependencies folder depending on where this jar is ran.
     * @return The dependencies folder, "C:/EEIDependencies/" when running in IDE or the current
     * working directory.
     * @author Victor Dang
     */
    public static String getDependenciesFolder()
    {
        return (MainController.DEBUG) ? eeiDependencies : "";
    }

    /**
     * Gets the path of to the log folder of the specified device ID. This will also create the directory if
     * it does not already exist.
     * @param deviceID The ID of the device to get or create a log folder for.
     * @return The path to the specified device's log folder.
     */
    public static String getLogFolderForDevice(String deviceID)
    {
        String folder = getCurrentWorkingDirectory() + logsFolder + deviceID + "/";
        checkAndCreateDirectory(folder);
        return folder;
    }

    /**
     * Returns the node.exe file location.
     * @return Gets the file of the node.exe
     * @author Victor Dang
     */
    public static File getNodeLocation()
    {
        return FileManager.getPath(FileManager.getDependenciesFolder() + FileManager.nodejsEXE).toFile();
    }

    /**
     * Returns the appium.js file location.
     * @return Gets the file of the appium.js.
     * @author Victor Dang
     */
    public static File getAppiumLocation()
    {
        return FileManager.getPath(FileManager.getDependenciesFolder() + FileManager.nodejsAppium).toFile();
    }

    /**
     * Check if the file or folder at the specified path exists or not.
     * @param path The path to the file or folder
     * @return Returns true if the file exists, false otherwise.
     * @author Victor Dang
     */
    public static boolean checkIfExists(String path)
    {
        return Files.exists(Paths.get(path));
    }

    /**
     * Gets the path object for the specified string.
     * @param path The path to a file or folder as a string.
     * @return The path object.
     * @author Victor Dang
     */
    public static Path getPath(String path)
    {
        return Paths.get(path);
    }

    /**
     * Returns the formatted parent path of the path string specified.
     * @param path The path to the folder.
     * @return The string of the path.
     */
    public static String getFormattedParentPath(String path)
    {
        return new File(path).getParent().replace("%20", " ") + "/";
    }

    /**
     * Gets the current working directory, this will get the correct directory regardless of whether working in
     * the IDE or running the standalone jar.
     * @return Returns the Path object of the current working directory.
     * @author Victor Dang
     */
    public static String getCurrentWorkingDirectory()
    {
        return FileManager.getFormattedParentPath(Runner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    /**
     * Checks to see if a directory at the specified path exists, if not, then one will be created. This method will
     * also create all subdirectories as needed.
     * @param path The path to the folder to check.
     * @author Victor Dang
     */
    public static Path checkAndCreateDirectory(String path)
    {
        return checkAndCreateDirectory(Paths.get(path));
    }

    /**
     * Checks to see if a directory at the specified path exists, if not, then one will be created. This method will
     * also create all subdirectories as needed. Take a Path parameter instead of a string.
     * @param path The path object to the file or folder.
     * @return The same path object.
     */
    public static Path checkAndCreateDirectory(Path path)
    {
        try
        {
            if (Files.notExists(path))
            {
                Files.createDirectories(path);
                MyLogger.log.debug("Creating directory at path " + path.toString());
            }

            return path;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     Checks to see if a file at the specified path exists, if not, then one will be created.
     * @param path The path to the file to check.
     * @return The path object.
     * @author Victor Dang
     */
    public static Path checkAndCreateFile(String path)
    {
        try
        {
            Path p = Paths.get(path);

            // check and create directory if needed first
            checkAndCreateDirectory(p.getParent());

            if (Files.notExists(p))
            {
                Files.createFile(p);
                MyLogger.log.debug("Creating file at path " + path);
            }

            return p;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Zips a file or directory.
     * @param fileToZip the file/folder to zip.
     * @param fileName the name of the folder inside the zip.
     * @param zipOut the outputstream that will write to zip.
     * @throws IOException if an I/O issue occurs.
     */
    public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            assert children != null;
            for (File childFile : children) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }


    /**
     * Deletes the directory using the path given.
     * @param path the path of the directory
     * @throws IOException if deletion is unsuccessful
     * @author Hayden Brehm
     */
    public static void deleteFolder(String path) throws IOException {
        FileUtils.deleteDirectory(new File(path));
    }

}

