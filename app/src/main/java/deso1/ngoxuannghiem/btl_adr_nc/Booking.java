package deso1.ngoxuannghiem.btl_adr_nc;

import java.util.List;

public class Booking {
    private String id;
    private String movieId;
    private String roomId;
    private String showtimeId;
    private String seat; // Ghế ngăn cách bởi dấu phẩy: "A1,B2,C3"
    private String foodId;
    private int foodQuantity;
    private String paymentMethod;
    private int totalPrice;
    private String userId;
    private List<String> seats; // Thay vì: private String seat;
    private List<Food> foods;
    private String bookingTime;

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public List<Food> getFoods() {
        return foods;
    }

    // Getter & Setter
    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    // Constructor rỗng bắt buộc cho Firebase
    public Booking() {}

    // Constructor đầy đủ
    public Booking(String id, String movieId, String roomId, String showtimeId, String seat,
                   String foodId, int foodQuantity, String paymentMethod, int totalPrice,String userId) {
        this.id = id;
        this.movieId = movieId;
        this.roomId = roomId;
        this.showtimeId = showtimeId;
        this.seat = seat;
        this.foodId = foodId;
        this.foodQuantity = foodQuantity;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

    public int getfoodQuantity() {
        return foodQuantity;
    }

    public void setFoodQuantity(int foodQuantity) {
        this.foodQuantity = foodQuantity;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUserId() {return userId;}

    public void setUserId(String userId) {this.userId = userId;}
}
