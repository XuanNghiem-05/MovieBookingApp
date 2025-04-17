package deso1.ngoxuannghiem.btl_adr_nc;

public class RevenueItem {
    private int index;
    private String movieId;
    private String movieName;
    private int ticketCount;
    private int revenue;

    public RevenueItem(String movieId, String movieName, int ticketCount, int revenue) {
        this.movieId = movieId;
        this.movieName = movieName;
        this.ticketCount = ticketCount;
        this.revenue = revenue;
    }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public String getMovieId() { return movieId; }
    public String getMovieName() { return movieName; }
    public void setMovieName(String name) { this.movieName = name; }
    public int getTicketCount() { return ticketCount; }
    public void setTicketCount(int count) { this.ticketCount = count; }
    public int getRevenue() { return revenue; }
    public void setRevenue(int revenue) { this.revenue = revenue; }
}
