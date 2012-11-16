/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadgtsf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 * @author azabeo
 */
public class Utility {
    
    private static final boolean DEBUG = true;

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public static void unZip(String zipFile, String outputFolder) {

        byte[] buffer = new byte[1024];
        
        long startTime = System.currentTimeMillis();

        try {

            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }
            
            

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                Utility.log("file unzip : " + newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        Utility.log("Done. " + (new Long(System.currentTimeMillis() - startTime).toString()) + " millseconds).\n");
    }

    /**
     * Remove a directory and all of its contents.
     *
     * The results of executing File.delete() on a File object that represents a
     * directory seems to be platform dependent. This method removes the
     * directory and all of its contents.
     *
     * @return true if the complete directory was removed, false if it could not
     * be. If false is returned then some of the files in the directory may have
     * been removed.
     *
     */
    public static boolean rmDir(File directory) {

        // Utility.log("removeDirectory " + directory);

        if (directory == null) {
            return false;
        }
        if (!directory.exists()) {
            return true;
        }
        if (!directory.isDirectory()) {
            return false;
        }

        String[] list = directory.list();

        // Some JVMs return null for File.list() when the
        // directory is empty.
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                File entry = new File(directory, list[i]);

                Utility.log("Removing entry " + entry);

                if (entry.isDirectory()) {
                    if (!rmDir(entry)) {
                        return false;
                    }
                } else {
                    if (!entry.delete()) {
                        return false;
                    }
                }
            }
        }

        return directory.delete();
    }

    public static boolean mkDir(String dirName) {
        File theDir = new File(dirName);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            Utility.log("creating directory: " + dirName);
            boolean result = theDir.mkdir();
            if (result) {
                Utility.log("DIR created");
                return true;
            }else{
                return false;
            }

        }
        
        return true;
    }
    
    public static void del(String file){
        new File(file).delete();
        Utility.log("File " + file + " deleted");
    }
    
    public static void log(String text){
        if (DEBUG) {
            System.out.println(text);
        }
    }
}
