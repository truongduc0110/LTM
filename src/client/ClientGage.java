package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import database.MySQLConnection;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientGage extends JFrame {
    private String currentUser;

    public ClientGage(String currentUser) {
        this.currentUser = currentUser; // Lưu tên người dùng hiện tại
        setTitle("Thách đấu với bạn bè");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Lấy danh sách bạn bè từ CSDL
        List<String[]> friends = MySQLConnection.getFriends(currentUser);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(friends.size() + 1, 1)); // +1 để chứa tiêu đề

        // Tiêu đề
        panel.add(new JLabel("Danh sách bạn bè:"));

        // Thêm nút "Thách đấu" cho mỗi bạn bè
        for (String[] friend : friends) {
            String friendName = friend[0]; // Giả sử tên bạn bè ở vị trí 0
            JButton challengeButton = new JButton("Thách đấu với " + friendName);
            challengeButton.addActionListener(new ChallengeButtonAction(friendName));
            panel.add(challengeButton);
        }

        add(panel);
        setVisible(true);
    }

    // Hàm xử lý khi nhấn nút "Thách đấu"
    class ChallengeButtonAction implements ActionListener {
        private String opponent;

        public ChallengeButtonAction(String opponent) {
            this.opponent = opponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Gửi yêu cầu thách đấu đến server
            try {
                // Kết nối đến server
                Socket socket = new Socket("localhost", 12345); // Địa chỉ server
                OutputStream out = socket.getOutputStream();

                // Gửi yêu cầu thách đấu
                System.out.println("challenge " + opponent);
                out.write(("challenge " + opponent).getBytes());
                out.flush();
                JOptionPane.showMessageDialog(null, "Yêu cầu thách đấu đã gửi đến " + opponent);
                
                // Đóng kết nối sau khi gửi
//                socket.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Lỗi khi gửi yêu cầu thách đấu.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGage("User Two"));
    }
}
