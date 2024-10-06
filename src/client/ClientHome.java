package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import database.MySQLConnection;
import Game.BattleShip;
import client.ClientGage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import javax.swing.table.TableCellRenderer;

public class ClientHome extends JFrame {
    private JTable table;
    private String currentUser;
    
    private void listenForChallenges() {
    new Thread(() -> {
        
        try {
            // Kết nối đến server
            Socket socket = new Socket("localhost", 12345); // Địa chỉ server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("duc");
            String message;
            while (true) {if((message = in.readLine()) != null){
                // Hiển thị thông báo thách đấu
                int response = JOptionPane.showConfirmDialog(null, message + "\nBạn có chấp nhận không?", 
                                                             "Chấp nhận thách đấu", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    // Mở trò chơi nếu chấp nhận
                    SwingUtilities.invokeLater(() -> new BattleShip(false, currentUser));
                }
                System.out.println("duc");
            }
            in.close(); // Đóng BufferedReader khi hoàn tất
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }).start();
}

    public ClientHome(String currentUser) {
        this.currentUser = currentUser; // Lưu tên người dùng hiện tại
        setTitle("Danh sách bạn bè");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Lấy danh sách bạn bè từ CSDL
        List<String[]> friends = MySQLConnection.getFriends(currentUser);

        // Tạo bảng để hiển thị thông tin bạn bè
        String[] columnNames = {"Tên hiển thị", "Thách đấu"};
        String[][] data = new String[friends.size()][2];

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // Thêm nút "Thách đấu" vào mỗi hàng
        for (int i = 0; i < friends.size(); i++) {
            data[i][0] = friends.get(i)[0]; // Tên hiển thị
            JButton challengeButton = new JButton("Thách đấu");
            challengeButton.addActionListener(new ChallengeButtonAction(friends.get(i)[0]));
            table.getColumn("Thách đấu").setCellRenderer(new ButtonRenderer());
            table.getColumn("Thách đấu").setCellEditor(new ButtonEditor(new JCheckBox(), challengeButton));
        }

        panel.add(scrollPane, BorderLayout.CENTER);

        // Nút "Thêm bạn bè"
        JButton addFriendButton = new JButton("Thêm bạn bè");
        addFriendButton.addActionListener(new AddFriendAction());
        panel.add(addFriendButton, BorderLayout.SOUTH);

        // tạo phòng
        JButton createRoomButton = new JButton("Tạo phòng chơi");
        createRoomButton.addActionListener(new ChallengeButtonAction(currentUser));
        panel.add(createRoomButton, BorderLayout.NORTH); // Thêm nút ở phía trên
        add(panel);
        setVisible(true);
        
        // Lắng nghe thách đấu
        listenForChallenges();
    }

    // Hàm xử lý khi nhấn nút "Thách đấu"
    class ChallengeButtonAction implements ActionListener {
        private String opponent;

        public ChallengeButtonAction(String opponent) {
            this.opponent = opponent;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SwingUtilities.invokeLater(() -> new ClientGage(currentUser));
//            int response = JOptionPane.showConfirmDialog(null,
//                    "Bạn có muốn thách đấu với " + opponent + " không?",
//                    "Thách đấu", JOptionPane.YES_NO_OPTION);

//            if (response == JOptionPane.YES_OPTION) {
//                boolean isHost = JOptionPane.showConfirmDialog(null, "Bạn tạo phòng đúng không?") == JOptionPane.YES_OPTION;
//                SwingUtilities.invokeLater(() -> new BattleShip(isHost, currentUser));
//                dispose();  // Đóng giao diện danh sách bạn bè
//            }
        }
    }
    

    // Hàm xử lý khi nhấn nút "Thêm bạn bè"
    class AddFriendAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String friendDisplayName = JOptionPane.showInputDialog(null, "Nhập tên bạn bè:");

            if (friendDisplayName != null && !friendDisplayName.trim().isEmpty()) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Bạn có muốn thêm " + friendDisplayName + " vào danh sách bạn bè không?",
                        "Thêm bạn bè", JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    boolean success = MySQLConnection.addFriend(currentUser, friendDisplayName);
                    if (success) {
                        JOptionPane.showMessageDialog(null, "Đã thêm " + friendDisplayName + " vào danh sách bạn bè.");
                    } else {
                        JOptionPane.showMessageDialog(null, "Không thể thêm bạn. Vui lòng thử lại.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng nhập tên bạn bè hợp lệ!");
            }
        }
    }

    // Renderer cho cột "Thách đấu"
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((String) value);
            return this;
        }
    }

    // Editor cho cột "Thách đấu"
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor(JCheckBox checkBox, JButton button) {
            super(checkBox);
            this.button = button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientHome("User Two"));
    }
}
