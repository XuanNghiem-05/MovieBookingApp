package deso1.ngoxuannghiem.btl_adr_nc;

public class Movie {
    public String id;
    public String name;
    public String description;
    public int price;
    public String startDate;
    public String imageUrl;
    public String bannerUrl;
    public int categoryId;

    public Movie() {
        // Required for Firebase
    }

    public Movie(String id, String name, String description, int price,
                 String startDate, String imageUrl, String bannerUrl, int categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.imageUrl = imageUrl;
        this.bannerUrl = bannerUrl;
        this.categoryId = categoryId;
    }

    // Constructor với tham số name và startDate
    public Movie(String name, String startDate) {
        this.name = name;
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCategory() {
        return categoryId;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Movie{name='" + name + "', startDate='" + startDate + "'}";
    }
}