/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadgtsf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 *
 * @author azabeo
 */
public class Loader {
    
    private static final String FNAME = "tmpGtsf_";
    private static final String FEXT = ".zip";
    private static final String DIR = "/Applications/MAMP/htdocs/PhpProject2/";
    private static final String TMP = "tmp";
    private static final int BUFFER = 153600;
    
    public static void main(String[] args) {
        
        Loader.importa("http://localhost/gtsf/torino_it.zip","id1",",", "\"", "\\r\\n");
        
    }
    
    public static void importa(String urlString, String agency_global_id, String field_term, String encloser, String line_term){
        String destDir = DIR + TMP + "/";
        String fileName = FNAME + agency_global_id + FEXT;
        
        /*
        Utility.rmDir(new File(destDir));
        Utility.mkDir(destDir);
        download(urlString,fileName,destDir);
        Utility.unZip(destDir + fileName, destDir);
        Utility.del(destDir + fileName);
        * */
        importCsvsFromDir(destDir, agency_global_id, field_term, encloser, line_term);
    }
    
    //download zip file
    private static void download(String urlString, String fileName, String destDir){
        try {
            /*
             * Get a connection to the URL and start up
             * a buffered reader.
             */
            long startTime = System.currentTimeMillis();

            Utility.log("Connecting...\n");

            URL url = new URL(urlString);
            url.openConnection();
            InputStream reader = url.openStream();

            /*
             * Setup a buffered file writer to write
             * out what we read from the website.
             */
            
            
            FileOutputStream writer = new FileOutputStream(destDir + fileName);
            byte[] buffer = new byte[BUFFER];
            int totalBytesRead = 0;
            int bytesRead = 0;

            Utility.log("Downloading " + fileName + " 150KB blocks at a time.\n");

            while ((bytesRead = reader.read(buffer)) > 0) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[BUFFER];
                totalBytesRead += bytesRead;
            }

            long endTime = System.currentTimeMillis();

            Utility.log("Done. " + (new Integer(totalBytesRead).toString()) + " bytes read (" + (new Long(endTime - startTime).toString()) + " millseconds).\n");
            writer.close();
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    //unzip to local directory
    private static void unzip(String file, String dir){
        Utility.unZip(file, dir);
    }
    
    //load into database
    private static void importCsvsFromDir(String path, String agency_global_id, String field_term, String encloser, String line_term){
        
        File dir = new File(path);
        
        String[] list = dir.list();

        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                Utility.log("Importing " + list[i]);
                Db.importCsv(path+list[i],agency_global_id, field_term, encloser, line_term);
            }
        }
    }
}
