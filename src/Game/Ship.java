package Game;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author TruongDuc
 */
class Ship {
    private int row, col, length;
    private boolean horizontal;
    // Khởi tạo một con tàu
    public Ship(int row, int col, int length, boolean horizontal) {
        this.row = row;//chỉ số hàng
        this.col = col;//chỉ số cột
        this.length = length;//chiều dài tàu
        this.horizontal = horizontal; //có quay ngang không
    }
}