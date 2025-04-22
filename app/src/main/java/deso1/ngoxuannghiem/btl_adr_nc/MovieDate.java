package deso1.ngoxuannghiem.btl_adr_nc;

public class MovieDate {
    private String id;          // ID của ngày chiếu (có thể tự tạo)
    private String date;        // Ngày chiếu, ví dụ: "2025-04-21"

    // Constructor rỗng để dùng với Firebase
    public MovieDate() {
    }

    public MovieDate(String id, String date) {
        this.id = id;
        this.date = date;
    }

    // Getter và Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return date; // Để Spinner hiển thị ngày
    }
}