package client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import database.MySQLConnection;
import Game.BattleShip;

public class ClientForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JTable userTable;

    public ClientForm() {
        setTitle("Đăng nhập");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(10, 10, 80, 25);
        panel.add(userLabel);

        usernameField = new JTextField(20);
        usernameField.setBounds(100, 10, 160, 25);
        panel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(10, 40, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField(20);
        passwordField.setBounds(100, 40, 160, 25);
        panel.add(passwordField);

        loginButton = new JButton("Đăng nhập");
        loginButton.setBounds(100, 70, 160, 25);
        panel.add(loginButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(10, 100, 250, 25);
        panel.add(statusLabel);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // Gửi thông tin đăng nhập đến server qua TCP/IP
                try (Socket socket = new Socket("localhost", 12345);
                     PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    // Gửi username và password đến server
                    out.println(username);
                    out.println(password);

                    // Nhận phản hồi từ server
                    String response = in.readLine();
                    if ("success".equals(response)) {
                        statusLabel.setText("Đăng nhập thành công!");
// Nếu đăng nhập thành công, mở giao diện ClientHome
if ("success".equals(response)) {
    statusLabel.setText("Đăng nhập thành công!");
    SwingUtilities.invokeLater(() -> new ClientHome(username));  // username là tên người dùng hiện tại
    dispose();  // Đóng cửa sổ đăng nhập
} else {
    statusLabel.setText("Đăng nhập thất bại. Vui lòng thử lại.");
}

                        // Sau khi đóng hộp thoại, hiển thị danh sách người dùng
                        dispose();  // Đóng cửa sổ đăng nhập
//                        showUserList();
                    } else {
//                        statusLabel.setText("Đăng nhập thất bại. Vui lòng thử lại.");
                        BattleShip bts=new BattleShip(false, "HIEU");
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Lỗi kết nối đến server.");
                }
            }
        });

        add(panel);
        setVisible(true);
    }

    

    public static void main(String[] args) {
        new ClientForm();
    }
}
