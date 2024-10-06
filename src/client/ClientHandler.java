/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;


import database.MySQLConnection;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author TruongDuc
 */
public class ClientHandler implements Runnable{
    
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private static List<ClientHandler> clients = new ArrayList<>(); // Danh sách các client đang kết nối

    public ClientHandler(Socket socket) {
        this.socket = socket;
        setupStreams();
        clients.add(this); // Thêm client vào danh sách khi kết nối
    }

    private void setupStreams() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Đọc thông tin từ client
            username = in.readLine(); // Nhận tên người dùng
            String password = in.readLine(); // Nhận mật khẩu

            // Kiểm tra thông tin đăng nhập
            if (MySQLConnection.loginUser(username, password)) {
                out.println("success"); // Phản hồi thành công
            } else {
                out.println("fail"); // Phản hồi thất bại
                socket.close(); // Đóng kết nối nếu đăng nhập thất bại
                return;
            }

            // Lắng nghe yêu cầu từ client
            String request;
            while ((request = in.readLine()) != null) {
                // Xử lý yêu cầu thách đấu
                if (request.startsWith("challenge")) {
                    String opponent = request.split(" ")[1]; // Lấy tên đối thủ
                    notifyOpponent(opponent, username);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close(); // Đóng kết nối khi client rời
            } catch (IOException e) {
                e.printStackTrace();
            }
            clients.remove(this); // Xóa client khỏi danh sách
        }
    }
    
    private void notifyOpponent(String opponent, String challenger) {
    for (ClientHandler client : clients) {
        if (client.username.equals(opponent)) {
            client.out.println(challenger + " đã thách đấu bạn! Chấp nhận không?"); // Gửi thông báo
        }
    }
}

}
