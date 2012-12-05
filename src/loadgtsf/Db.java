/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package loadgtsf;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author azabeo
 */
public class Db {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String ENC = "?useUnicode=true&characterEncoding=utf-8";
    final static Charset ENCODING = StandardCharsets.UTF_8;

    public static void main(String[] args) {
        Db.importCsv("", "", ",", "\"", "\\r\\n");
    }

    private static Connection getCon() {

        String name = "ipmobman2";
        String user = "root";
        String password = "root";

        Connection con = null;

        try {
            con = DriverManager.getConnection(URL + name + ENC, user, password);
        } catch (SQLException ex) {
            Utility.log(ex.getMessage());
        } finally {
            return con;
        }
    }

    public static void closeCon(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                Utility.log(ex.getMessage());
            }
        }
    }

    public static void connect(String name, String user, String password) {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;

        try {
            con = DriverManager.getConnection(URL + name, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {
                Utility.log("Connected to '" + name + "' MySQL Version: " + rs.getString(1));
            }

        } catch (SQLException ex) {
            Utility.log(ex.getMessage());

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
                Utility.log(ex.getMessage());
            }
        }
    }

    public static void importCsv(String name, String agency_global_id, String field_term, String encloser, String line_term) {

        String headers = readLine(name);
        if (headers == null) {
            return;
        }

        Scanner scanner = new Scanner(headers);
        scanner.useDelimiter(field_term);

        String colNames = "";

        while (scanner.hasNext()) {
            String head = scanner.next();
            if (encloser != null && head.startsWith(encloser)) {
                head = head.replaceAll(encloser, "");
            }
            colNames += (", " + head);
        }
        colNames = colNames.substring(2);
        String tableName = Paths.get(name).getFileName().toString();
        tableName = tableName.substring(0, tableName.length() - 4);

        boolean redo = true;
        Connection con = getCon();
        String noCol = "";
        int varNum = 0;
        Statement st = null;
        long startTime;

        while (redo) {

            if (noCol.length() != 0) {
                colNames = colNames.replaceAll(noCol, "@var" + varNum);
            }

            String query = "LOAD DATA INFILE '" + name + "' "
                    + "INTO TABLE " + tableName
                    + " CHARACTER SET utf8"
                    + " FIELDS TERMINATED BY '" + field_term + "' OPTIONALLY ENCLOSED BY '" + encloser
                    //+ "' LINES TERMINATED BY '" + line_term 
                    + "' IGNORE 1 LINES (" + colNames + ") SET agency_global_id = '" + agency_global_id + "';";

            try {
                con.setAutoCommit(false);

                startTime = System.currentTimeMillis();

                st = con.createStatement();
                int res = st.executeUpdate("DELETE FROM " + tableName + " WHERE agency_global_id = '" + agency_global_id + "';");
                Utility.log("  Deleted " + res + " lines from " + tableName);
                res = st.executeUpdate(query);
                Utility.log("  Inserted " + res + " lines from " + tableName);
                Utility.log("  " + (new Long(System.currentTimeMillis() - startTime).toString()) + " millseconds");

                con.commit();
                redo = false;
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                Utility.log("  --" + msg);

                if (msg.startsWith("Unknown column")) {
                    Scanner s = new Scanner(msg);
                    s.useDelimiter("'");
                    s.next();
                    noCol = s.next();
                    varNum++;
                } else {
                    redo = false;
                }

                if (con != null) {
                    try {
                        Utility.log("  -- Transaction is being rolled back");
                        con.rollback();
                    } catch (SQLException excep) {
                        Utility.log(excep.getMessage());
                    }
                }
            } finally {
                try {
                    if (st != null) {
                        st.close();
                    }

                    con.setAutoCommit(true);
                } catch (SQLException excep) {
                    Utility.log(excep.getMessage());
                }
            }
        }
        closeCon(con);
    }

    public static void createShapes(String agency_global_id) {
        
        Timer t = new Timer();
        
        Connection con = getCon();
        
        String delete = "DELETE FROM paths WHERE agency_global_id ='" + agency_global_id + "';";

        String query = "SELECT shape_id ,shape_pt_lat,shape_pt_lon,shape_pt_sequence,shape_dist_traveled "
                + "FROM shapes "
                + "WHERE agency_global_id = '" + agency_global_id + "' "
                + "ORDER BY shape_id, shape_pt_sequence;";

        String insert = "INSERT INTO paths (agency_global_id, shape_id, path, num_points, tot_dist) VALUES('" + agency_global_id + "', '";

        Statement stmt = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            int res;
            stmt = con.createStatement();
            res = stmt.executeUpdate(delete);
            
            st = con.createStatement();
            rs = st.executeQuery(query);

            String id = "";
            String old_id = "";
            int nump = 0;
            double totd = 0;
            String path = "";
            
            boolean isFirst = true;

            while (rs.next()) {
                id = rs.getString(1);
                if (!id.equals(old_id) && !isFirst) {
                    String q1 = insert + old_id + "', '" + path + "', " + nump + ", " + totd + ");";
                    res = stmt.executeUpdate(q1);
                    
                    //Utility.log("- paths: " + old_id + " " + nump);

                    path = "";
                    nump = 0;
                    totd = 0;
                }
                path += rs.getString(2) + "," + rs.getString(3) + "|";
                nump++;
                totd += rs.getFloat(5);

                old_id = id;
                isFirst = false;
            }

            String q1 = insert + old_id + "', '" + path + "', " + nump + ", " + totd + ");";
            res = stmt.executeUpdate(q1);
            
            Utility.log("createShapes done: " + t.stop());

        } catch (SQLException ex) {
            Utility.log(ex.getLocalizedMessage());
        }finally{
            try {
                st.close();
                stmt.close();
                rs.close();
            } catch (SQLException ex) {
                Utility.log(ex.getLocalizedMessage());
            }
        }
    }
    
    public static void createRouteShapes(String agency_global_id){
        
    }

    public static String readLine(String fileName) {
        BufferedReader reader = null;
        String line = null;

        try {
            Path path = Paths.get(fileName);
            reader = Files.newBufferedReader(path, ENCODING);

            line = reader.readLine();
            reader.close();

        } catch (IOException ex) {
            Utility.log(ex.getLocalizedMessage());
        } finally {
            return line;
        }
    }
}
