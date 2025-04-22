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
    public String videoUrl;
    public String director;       // Đạo diễn
    public String actors;         // Diễn viên (dạng chuỗi, có thể phân cách bằng dấu phẩy)
    public int duration;          // Thời lượng phim (phút)
    public String language;

    public Movie() {
        // Required for Firebase
    }

    public Movie(String id, String name, String description, int price, String startDate,
                 String imageUrl, String bannerUrl, int categoryId, String videoUrl,
                 String director, String actors, int duration, String language) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.startDate = startDate;
        this.imageUrl = imageUrl;
        this.bannerUrl = bannerUrl;
        this.categoryId = categoryId;
        this.videoUrl = videoUrl;
        this.director = director;
        this.actors = actors;
        this.duration = duration;
        this.language = language;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getActors() { return actors; }
    public void setActors(String actors) { this.actors = actors; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    @Override
    public String toString() {
        return "Movie{name='" + name + "', startDate='" + startDate + "'}";
    }
}