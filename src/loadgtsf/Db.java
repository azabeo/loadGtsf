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

/**
 *
 * @author azabeo
 */
public class Db {

    private static final String URL = "jdbc:mysql://localhost:3306/";
    final static Charset ENCODING = StandardCharsets.UTF_8;

    public static void main(String[] args) {
        //Db.execute("ipmobman", "root", "root", null, null);
        Db.importCsv("", "", ",", "\"", "\\r\\n");
    }

    private static Connection getCon() {

        String name = "ipmobman";
        String user = "root";
        String password = "root";

        Connection con = null;

        try {
            con = DriverManager.getConnection(URL + name, user, password);
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

        while (redo) {

            if (noCol.length() != 0) {
                colNames = colNames.replaceAll(noCol, "@var" + varNum);
            }

            String query = "LOAD DATA INFILE '" + name + "' "
                    + "INTO TABLE " + tableName
                    + " FIELDS TERMINATED BY '" + field_term + "' OPTIONALLY ENCLOSED BY '" + encloser
                    //+ "' LINES TERMINATED BY '" + line_term 
                    + "' IGNORE 1 LINES (" + colNames + ") SET agency_global_id = '" + agency_global_id + "';";

            try {
                con.setAutoCommit(false);

                st = con.createStatement();
                int res = st.executeUpdate("DELETE FROM " + tableName + " WHERE agency_global_id = '" + agency_global_id + "';");
                Utility.log("  Deleted " + res + " lines from " + tableName);
                res = st.executeUpdate(query);
                Utility.log("  Inserted " + res + " lines from " + tableName);

                con.commit();
                redo = false;
            } catch (SQLException ex) {
                String msg = ex.getMessage();
                Utility.log(msg);

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
                        Utility.log("Transaction is being rolled back");
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
    
    private static void createShapes(){
        Connection con = getCon();
        
        String query = "SELECT r.*, t.*, s.* " +
                "FROM ipmobman.trips t " + 
                "INNER JOIN ipmobman.shapes s ON t.shape_id = s.shape_id " +
                "INNER JOIN ipmobman.routes r ON t.route_id = r.route_id " +
                "ORDER BY t.trip_id, s.shape_pt_sequence;
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
