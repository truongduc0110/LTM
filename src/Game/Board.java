package Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author TruongDuc
 */
class Board {
    private char[][] grid; // Mảng 2 chiều
    private List<Ship> ships; //danh sách tàu
    private int size; //Khai báo lưới
    private Random random; // biến random để đặt tàu ngẫu nhiên
  // Khởi tạo bảng game
    public Board(int size) {
        this.size = size;
        this.grid = new char[size][size];
        this.ships = new ArrayList<>();
        this.random = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = 'O'; //Tạo bảng full số O( lưới chưa được đặt tàu)
            }
        }
    }
// Kiểm tra xem có thể đặt tàu tại vị trí được chỉ định không
    public boolean canPlaceShip(int row, int col, int length, boolean horizontal) {
        if (horizontal) {
            if (col + length > size) return false;
            for (int i = 0; i < length; i++) {
                if (grid[row][col + i] != 'O') return false;
            }
        } else {
            if (row + length > size) return false;
            for (int i = 0; i < length; i++) {
                if (grid[row + i][col] != 'O') return false;
            }
        }
        return true;
    }
// Đặt tàu tại vị trí được chỉ định
    public void placeShip(int row, int col, int length, boolean horizontal) {
        Ship ship = new Ship(row, col, length, horizontal);
        ships.add(ship);
        for (int i = 0; i < length; i++) {
            if (horizontal) {
                grid[row][col + i] = 'S';
            } else {
                grid[row + i][col] = 'S';
            }
        }
    }
// Đặt tàu ngẫu nhiên trên bảng
    public void placeShipRandomly(int length) {
        while (true) {
            int row = random.nextInt(size);
            int col = random.nextInt(size);
            boolean horizontal = random.nextBoolean();
            
            if (canPlaceShip(row, col, length, horizontal)) {
                placeShip(row, col, length, horizontal);
                break;
            }
        }
    }
// Xử lý khi bảng nhận được một cuộc tấn công
    public boolean receiveAttack(int row, int col) {
        if (grid[row][col] == 'S') {
            grid[row][col] = 'X';
            return true;
        } else if (grid[row][col] == 'O') {
            grid[row][col] = 'M';
        }
        return false;
    }
// Kiểm tra xem tất cả tàu đã bị đánh chìm chưa
    public boolean allShipsSunk() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (grid[i][j] == 'S') return false;
            }
        }
        return true;
    }
 // Trả về lưới của bảng
    public char[][] getGrid() {
        return grid;
    }
}
