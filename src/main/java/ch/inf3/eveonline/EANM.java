package ch.inf3.eveonline;

import ch.inf3.eveonline.gui.MainFrame;
import javax.swing.*;
import java.io.File;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.commons.io.FileUtils;

public class EANM {
    public static void main(String[] args) {
        initializeLogging();
        
        MainFrame mf = new MainFrame();
        mf.setTitle("Eve Settings Manager");
        mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mf.setLocationRelativeTo(null);

        mf.setSettingsFolder(getSettingsFolder());

        mf.setVisible(true);
    }

    public static File getSettingsFolder() {
        File systemUserFolder = new File(System.getProperty("user.home"));

        // get files
        File userFolder;
        if (System.getProperty("os.name").equals("Linux")) {
            File usersFolders = FileUtils.getFile(systemUserFolder, ".eve", "wineenv", "drive_c", "users");
            File[] possibleUserFolders = usersFolders.listFiles(f -> !"Public".equalsIgnoreCase(f.getName()));
            
            if (possibleUserFolders == null || possibleUserFolders.length == 0) {
                return null;
            }

            userFolder = possibleUserFolders[0];
        } else {
            userFolder = systemUserFolder;
        }

        File eveFolder = FileUtils.getFile(userFolder, "Local Settings", "Application Data", "CCP", "EVE");

        if (!eveFolder.isDirectory()) {
            return null;
        }

        return eveFolder;
    }

    private static void initializeLogging() {
        Logger globalLogger = Logger.getLogger("");
        globalLogger.setLevel(Level.WARNING);
        for(Handler handler : globalLogger.getHandlers()) {
            handler.setLevel(Level.ALL);
            handler.setFormatter(new SimpleFormatter());
        }

        Logger myLogger = Logger.getLogger("ch.inf3");
        myLogger.setLevel(Level.INFO);
    }
}
