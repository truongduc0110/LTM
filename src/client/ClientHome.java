/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client;

/**
 *
 * @author TruongDuc
 */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import database.MySQLConnection;
import Game.BattleShip;
import javax.swing.table.TableCellRenderer;

public class ClientHome extends JFrame {

    public ClientHome(String currentUser) {
        setTitle("Danh sách người dùng");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Lấy danh sách người dùng từ CSDL
        List<String[]> users = MySQLConnection.getAllUsers();

        // Tạo bảng để hiển thị thông tin người dùng
        String[] columnNames = {"Username", "Full Name", "Email", "Thách đấu"};
        String[][] data = new String[users.size()][4];

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);

        // Thêm nút "Thách đấu" vào mỗi hàng
        for (int i = 0; i < users.size(); i++) {
            data[i][0] = users.get(i)[0]; // Username
            data[i][1] = users.get(i)[1]; // Full Name
            data[i][2] = users.get(i)[2]; // Email
            JButton challengeButton = new JButton("Thách đấu");
            challengeButton.addActionListener(new ChallengeButtonAction(users.get(i)[0], currentUser));
            table.getColumn("Thách đấu").setCellRenderer((TableCellRenderer) new ButtonRenderer());
            table.getColumn("Thách đấu").setCellEditor(new ButtonEditor(new JCheckBox(), challengeButton));
        }

        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);
        setVisible(true);
    }

    // Hàm xử lý khi nhấn nút "Thách đấu"
    class ChallengeButtonAction implements ActionListener {
        private String opponent;
        private String currentUser;

        public ChallengeButtonAction(String opponent, String currentUser) {
            this.opponent = opponent;
            this.currentUser = currentUser;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int response = JOptionPane.showConfirmDialog(null,
                    "Bạn có muốn thách đấu với " + opponent + " không?",
                    "Thách đấu", JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                // Mở giao diện trò chơi BattleShip
                boolean isHost = JOptionPane.showConfirmDialog(null, "Bạn tạo phòng đúng ko?") == JOptionPane.YES_OPTION;
                SwingUtilities.invokeLater(() -> new BattleShip(isHost, currentUser));
                
                dispose();  // Đóng giao diện danh sách người dùng
                
            }
        }
    }

    // Renderer cho cột "Thách đấu" để hiện nút
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Thách đấu");
            return this;
        }
    }

    // Editor cho cột "Thách đấu" để xử lý nút bấm
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isClicked;

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

        @Override
        public boolean stopCellEditing() {
            isClicked = false;
            return super.stopCellEditing();
        }
    }

    public static void main(String[] args) {
        // Chạy giao diện ClientHome với user hiện tại
        SwingUtilities.invokeLater(() -> new ClientHome("CurrentUser"));
    }
}
