package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utils.Config;

public class MySQLConnection {

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(Config.DB_URL, Config.DB_USERNAME, Config.DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static boolean loginUser(String username, String password) {
        Connection conn = getConnection();
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<String[]> getAllUsers() {
        List<String[]> users = new ArrayList<>();
        Connection conn = getConnection();
        String query = "SELECT username, fullname, email FROM users";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                users.add(new String[]{rs.getString("username"), rs.getString("fullname"), rs.getString("email")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static String searchUserInfo(String username) {
        Connection conn = getConnection();
        String query = "SELECT fullname, email FROM users WHERE username = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "Tên đầy đủ: " + rs.getString("fullname") + ", Email: " + rs.getString("email");
            } else {
                return "Không tìm thấy người dùng!";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Lỗi trong quá trình tìm kiếm!";
    }
}
