package deso1.ngoxuannghiem.btl_adr_nc;

public class Showtime {
    private String id;
    private String time;

    public Showtime() {
        // Constructor rỗng bắt buộc cho Firebase
    }

    public Showtime(String id, String time) {
        this.id = id;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getTime() {
        return time;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTime(String time) {
        this.time = time;
    }

    // Đảm bảo override phương thức toString() để dễ dàng hiển thị thông tin
    @Override
    public String toString() {
        return time;  // Trả về thời gian khi được hiển thị trong Spinner
    }
}