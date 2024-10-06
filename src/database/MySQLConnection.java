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

    public static List<String[]> getFriends(String currentUser) {
    // Thực hiện truy vấn để lấy danh sách bạn bè từ cơ sở dữ liệu
    // Kết quả trả về là danh sách các mảng chứa thông tin của bạn bè
    List<String[]> friendsList = new ArrayList<>();
    Connection conn = getConnection();
    String query = "SELECT friend_display_name FROM friends WHERE user_display_name = ?";
    
    try {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, currentUser);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            String friendName = rs.getString("friend_display_name");
            friendsList.add(new String[]{friendName}); // Thêm tên hiển thị bạn bè vào danh sách
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (conn != null) {
                conn.close(); // Đảm bảo đóng kết nối
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    return friendsList;
}

// Thêm bạn bè vào cơ sở dữ liệu
public static boolean addFriend(String currentUser, String friendDisplayName) {
    Connection conn = getConnection();
    String query = "INSERT INTO friends (user_display_name, friend_display_name) VALUES (?, ?)";
    
    try {
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, currentUser);
        ps.setString(2, friendDisplayName);
        
        int rowsAffected = ps.executeUpdate();
        return rowsAffected > 0; // Trả về true nếu thêm thành công
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try {
            if (conn != null) {
                conn.close(); // Đảm bảo đóng kết nối
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    return false; // Trả về false nếu có lỗi xảy ra
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
