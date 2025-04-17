package deso1.ngoxuannghiem.btl_adr_nc;

import java.util.List;

public class Seat {
    public String name;
    public boolean isSelected;
    private List<String> seats;

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public Seat() {}

    public Seat(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }
}