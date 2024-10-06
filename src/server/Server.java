package server;

import client.ClientHandler;
import java.io.*;
import java.net.*;
import database.MySQLConnection;

public class Server {
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
        ClientHandler clientHandler = new ClientHandler(clientSocket); // Tạo client handler mới cho mỗi client
        new Thread(clientHandler).start(); // Chạy client handler trong một luồng riêng
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
    }
}
