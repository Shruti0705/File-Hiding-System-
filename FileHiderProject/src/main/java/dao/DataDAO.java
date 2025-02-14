package dao;

import db.MyConnection;
import model.Data;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DataDAO {
    private static final Logger LOGGER = Logger.getLogger(DataDAO.class.getName());

    public static List<Data> getAllFiles(String email) throws SQLException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM data WHERE email = ?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        List<Data> files = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(1);
            String name = rs.getString(2);
            String path = rs.getString(3);
            files.add(new Data(id, name, path));
        }
        return files;
    }

    public static int hideFile(Data file) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement(
                "insert into data(name, path, email, bin_data) values(?,?,?,?)");
        ps.setString(1, file.getFileName());
        ps.setString(2, file.getPath());
        ps.setString(3, file.getEmail());

        File f = new File(file.getPath());

        if (!f.exists()) {
            System.err.println("File does not exist: " + file.getPath());
            return 0;
        }

        try (FileReader fr = new FileReader(f)) {  // Ensuring FileReader is closed properly
            ps.setCharacterStream(4, fr, f.length());
            int ans = ps.executeUpdate();

            if (ans > 0) {
                boolean deleted = f.delete();
                if (!deleted) {
                    System.err.println("WARNING: Failed to delete file: " + file.getPath());
                }
            }
            return ans;
        }
    }


    public static void unhide(int id) throws SQLException, IOException {
        Connection connection = MyConnection.getConnection();
        PreparedStatement ps = connection.prepareStatement("SELECT path, bin_data FROM data WHERE id = ?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            String path = rs.getString("path");
            Clob c = rs.getClob("bin_data");

            try (Reader r = c.getCharacterStream();
                 FileWriter fw = new FileWriter(path)) {
                int i;
                while ((i = r.read()) != -1) {
                    fw.write((char) i);
                }
            }

            ps = connection.prepareStatement("DELETE FROM data WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            LOGGER.info("File successfully unhidden: " + path);
        } else {
            LOGGER.log(Level.WARNING, "File ID not found in database: " + id);
        }
    }
}
