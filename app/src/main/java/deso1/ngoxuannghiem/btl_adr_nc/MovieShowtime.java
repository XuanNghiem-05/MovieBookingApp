package deso1.ngoxuannghiem.btl_adr_nc;

public class MovieShowtime {
    public String movieId;
    public String roomId;
    public String showtimeId;

    public MovieShowtime() {}

    public MovieShowtime(String movieId, String roomId, String showtimeId) {
        this.movieId = movieId;
        this.roomId = roomId;
        this.showtimeId = showtimeId;
    }
}