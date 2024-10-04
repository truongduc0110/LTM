package server;

import java.io.*;
import java.net.*;
import database.MySQLConnection;

public class Server {

    public static void stop() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    private ServerSocket serverSocket;

    private boolean isRunning = false;  // Cờ điều khiển trạng thái server

    // Hàm để bắt đầu server và lắng nghe trên cổng
    public void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            
            isRunning = true;
            System.out.println("Server is running on port " + port + "...");

            while (isRunning) {
                try {
                    // Chấp nhận kết nối từ client
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected successfully!");

                    // Xử lý client
                    handleClient(clientSocket);
                } catch (IOException e) {
                    if (isRunning) {
                        System.out.println("Error accepting client connection.");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server.");
            e.printStackTrace();
        } finally {
            stopServer();  // Đảm bảo server được đóng khi dừng
        }
    }

    // Hàm xử lý client kết nối
    private void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Đọc thông tin từ client
            String username = in.readLine();
            String password = in.readLine();

            // Kiểm tra thông tin đăng nhập từ cơ sở dữ liệu
            if (MySQLConnection.loginUser(username, password)) {
                out.println("success");  // Phản hồi đăng nhập thành công
            } else {
                out.println("fail");  // Phản hồi đăng nhập thất bại
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();  // Đóng kết nối client
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Hàm dừng server
    public void stopServer() {
        isRunning = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();  // Đóng server socket
                System.out.println("Server has stopped.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Kiểm tra trạng thái của server
    public boolean isServerRunning() {
        return isRunning;
    }

    public static void main(String[] args) {
        Server server = new Server();

        // Tạo luồng mới để chạy server
        Thread serverThread = new Thread(() -> server.startServer(12345));
        serverThread.start();

        // Đây là một ví dụ cho cách dừng server sau 60 giây (có thể điều chỉnh)
//        try {
//            Thread.sleep(60000);  // Dừng server sau 60 giây
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Dừng server
//        server.stopServer();
    }
}
