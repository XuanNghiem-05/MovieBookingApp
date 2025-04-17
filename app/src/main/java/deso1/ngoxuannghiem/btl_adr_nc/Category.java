package deso1.ngoxuannghiem.btl_adr_nc;

public class Category {
    public int id;
    public String name;
    public String imageUrl;

    public Category() {
        // Required for Firebase
    }

    public Category(int id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name; // để Spinner hiển thị tên
    }
}
