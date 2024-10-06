package Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Random;

public class BattleShip extends JFrame {
    private int CheckWin = 0;
    private Board playerBoard;
    private Board opponentBoard;
    private JButton[][] playerButtons;
    private JButton[][] opponentButtons;
    private JLabel statusLabel;
    private Random random;
    private int shipsToPlace;
    private int[] shipSizes = {5, 4, 3, 3, 2};
    private boolean isPlacingShips;
    private boolean isHorizontal;
    private String playerName;
    private String opponentName;
    private boolean playerTurn;

    // Networking variables
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ServerSocket serverSocket;
    private boolean isHost;

    // Panel to show ships not yet placed
    private JPanel shipsToPlacePanel;
    private DefaultListModel<String> shipsListModel;

    public BattleShip(boolean isHost, String playerName) {
        super("Battleship Game");
        this.isHost = isHost;
        this.playerName = playerName;

        initializeGame();
        if (isHost) {
            createRoom();
        } else {
            joinRoom();
        }
    }

    private void initializeGame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLayout(new BorderLayout());

        playerBoard = new Board(10);
        opponentBoard = new Board(10);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.add(createBoardPanel(true));
        mainPanel.add(createBoardPanel(false));
        statusLabel = new JLabel(playerName + ", place your ships. Press 'R' to rotate.");
        add(statusLabel, BorderLayout.SOUTH);
        add(mainPanel, BorderLayout.CENTER);

        // Create the ships to place panel
        shipsToPlacePanel = new JPanel();
        shipsToPlacePanel.setLayout(new BorderLayout());
        shipsListModel = new DefaultListModel<>();
        JList<String> shipsList = new JList<>(shipsListModel);
        shipsToPlacePanel.add(new JScrollPane(shipsList), BorderLayout.CENTER);
        updateShipsToPlace(); // Update ships to place initially
        add(shipsToPlacePanel, BorderLayout.EAST); // Add it to the right side

        JButton rotateButton = new JButton("Rotate Ship");
        rotateButton.addActionListener(e -> isHorizontal = !isHorizontal);
        add(rotateButton, BorderLayout.NORTH);

        isPlacingShips = true;
        shipsToPlace = 0;
        isHorizontal = true;
        playerTurn = isHost;  // Host goes first by default

        setFocusable(true);
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyChar() == 'r' || evt.getKeyChar() == 'R') {
                    isHorizontal = !isHorizontal;
                }
            }
        });
        setVisible(true);
    }

    private void updateShipsToPlace() {
        shipsListModel.clear(); // Clear existing list
        for (int i = shipsToPlace; i < shipSizes.length; i++) {
            shipsListModel.addElement("Ship Size: " + shipSizes[i]);
        }
    }

    // Create room (host)
    private void createRoom() {
        statusLabel.setText("Waiting for opponent to join...");

        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(551);  // Server listens on port 551
                System.out.println("Server started. Waiting for opponent to connect...");

                socket = serverSocket.accept();  // Accept connection from opponent
                System.out.println("Opponent connected!");

                setupStreams();  // Set up input/output streams for communication
                statusLabel.setText("Opponent joined. Place your ships.");

            } catch (IOException e) {
                e.printStackTrace();
                statusLabel.setText("Error while waiting for opponent.");
            } finally {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();  // Start thread
    }

    // Join room (opponent)
    private void joinRoom() {
        try {
            String hostAddress = "localhost";  // Localhost for same machine
            socket = new Socket(hostAddress, 551);  // Connect to the host at port 551
            setupStreams();  // Set up input/output streams
            statusLabel.setText("Connected to host. Place your ships.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupStreams() throws IOException {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Start a thread to listen for opponent's moves
        new Thread(() -> {
            while (true) {
                try {
                    String message = in.readLine();
                    if (message.startsWith("MOVE")) {
                        // Nhận kết quả tấn công từ đối thủ
                        String[] parts = message.split(":");
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        opponentTurn(row, col);
                    } else if (message.equals("GAME_OVER")) {
                        endGame(opponentName + " wins!");  // Đối thủ thắng
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private JPanel createBoardPanel(boolean isPlayerBoard) {
        JPanel panel = new JPanel(new GridLayout(10, 10));
        JButton[][] buttons = new JButton[10][10];

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(30, 30));
                int row = i;
                int col = j;

                if (isPlayerBoard) {
                    button.addActionListener(e -> {
                        if (isPlacingShips) {
                            placePlayerShip(row, col);
                        }
                    });
                } else {
                    button.addActionListener(e -> {
                        if (!isPlacingShips && playerTurn) {
                            playerTurn(row, col);
                        }
                    });
                }

                panel.add(button);
                buttons[i][j] = button;
            }
        }

        if (isPlayerBoard) {
            playerButtons = buttons;
        } else {
            opponentButtons = buttons;
        }

        return panel;
    }

    private void placePlayerShip(int row, int col) {
        if (shipsToPlace < shipSizes.length) {
            int size = shipSizes[shipsToPlace];
            if (playerBoard.canPlaceShip(row, col, size, isHorizontal)) {
                playerBoard.placeShip(row, col, size, isHorizontal);
                updatePlayerBoard();
                shipsToPlace++;
                updateShipsToPlace(); // Update the ships to place list
                if (shipsToPlace == shipSizes.length) {
                    isPlacingShips = false;
                    statusLabel.setText("All ships placed. Click on the opponent board to attack.");
                    if (!isHost) {
                        playerTurn = false;  // If opponent, wait for host to go first
                    }
                } else {
                    statusLabel.setText("Place your " + shipSizes[shipsToPlace] + "-unit ship. Press 'R' to rotate.");
                }
            }
        }
    }

    private void updatePlayerBoard() {
    for (int i = 0; i < 10; i++) {
        for (int j = 0; j < 10; j++) {
            if (playerBoard.getGrid()[i][j] == 'S') {
                playerButtons[i][j].setText("🚢"); // Thay thế màu bằng biểu tượng tàu
                playerButtons[i][j].setEnabled(false); // Để nút không thể nhấn
            } else {
                playerButtons[i][j].setText(""); // Nếu không có tàu, đặt lại thành rỗng
            }
        }
    }
}


    private void playerTurn(int row, int col) {
        if (!playerTurn || opponentBoard.getGrid()[row][col] == 'X' || opponentBoard.getGrid()[row][col] == 'M') {
            return;
        }

        // Gửi yêu cầu tấn công đến đối thủ
        out.println("MOVE:" + row + ":" + col);  // Gửi thông tin tấn công cho đối thủ

        boolean hit = opponentBoard.receiveAttack(row, col);
        updateButton(opponentButtons[row][col], hit);  // Cập nhật bảng giao diện của người chơi dựa trên kết quả

        // Kiểm tra xem người chơi đã thắng chưa bằng cách kiểm tra bảng của chính họ
        if (playerBoard.allShipsSunk()) {
            endGame(playerName + " wins!");  // Người chơi chiến thắng
            return;
        }

        // Chuyển lượt cho đối thủ
        playerTurn = false;
        statusLabel.setText("Opponent's turn");
    }

    public void opponentTurn(int row, int col) {
        if (playerTurn || playerBoard.getGrid()[row][col] == 'X' || playerBoard.getGrid()[row][col] == 'M') {
            return;
        }

        // Đối thủ tấn công người chơi
        boolean hit = playerBoard.receiveAttack(row, col);
        updateButton(playerButtons[row][col], hit);

        // Kiểm tra nếu tất cả tàu của người chơi đã bị đánh chìm
        if (playerBoard.allShipsSunk()) {
            out.println("GAME_OVER");  // Thông báo cho đối thủ rằng trò chơi đã kết thúc
            endGame(opponentName + " wins!");  // Đối thủ chiến thắng
            return;
        }

        // Chuyển lượt cho người chơi
        playerTurn = true;
        statusLabel.setText(playerName + "'s turn");
    }

    private void updateButton(JButton button, boolean hit) {
        button.setBackground(hit ? Color.RED : Color.BLUE);
        button.setEnabled(false);
    }

    private void endGame(String message) {
        statusLabel.setText(message);
        for (JButton[] row : opponentButtons) {
            for (JButton button : row) {
                button.setEnabled(false);
            }
        }
    }

    public static void main(String[] args) {
        boolean isHost = JOptionPane.showConfirmDialog(null, "Are you the host?") == JOptionPane.YES_OPTION;
        String playerName = JOptionPane.showInputDialog("Enter your name:");
        SwingUtilities.invokeLater(() -> new BattleShip(isHost, playerName));
    }
}
