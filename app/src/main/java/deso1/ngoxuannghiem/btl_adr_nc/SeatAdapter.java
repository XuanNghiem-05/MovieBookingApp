package deso1.ngoxuannghiem.btl_adr_nc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    private List<Seat> seats;
    private OnSeatSelectedListener listener;


    public interface OnSeatSelectedListener {
        void onSeatSelected(List<Seat> selectedSeats);
    }

    public SeatAdapter(List<Seat> seats, OnSeatSelectedListener listener) {
        this.seats = seats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }
    private List<String> bookedSeats = new ArrayList<>();

    public void setBookedSeats(List<String> bookedSeats) {
        this.bookedSeats = bookedSeats;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seats.get(position);
        holder.tvSeat.setText(seat.name);

        if (bookedSeats.contains(seat.name)) {
            // Ghế đã được đặt
            holder.tvSeat.setBackgroundResource(R.drawable.bg_seat_booked); // bạn tạo drawable màu đỏ
            holder.tvSeat.setEnabled(false); // không cho click
        } else {
            // Ghế có thể chọn
            holder.tvSeat.setBackgroundResource(seat.isSelected ? R.drawable.bg_seat_selected : R.drawable.bg_seat_unselected);
            holder.tvSeat.setEnabled(true); // cho click

            holder.tvSeat.setOnClickListener(v -> {
                seat.isSelected = !seat.isSelected;
                notifyItemChanged(position);
                listener.onSeatSelected(getSelectedSeats());
            });
        }
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    public List<Seat> getSelectedSeats() {
        List<Seat> selected = new ArrayList<>();
        for (Seat s : seats) {
            if (s.isSelected) selected.add(s);
        }
        return selected;
    }

    public static class SeatViewHolder extends RecyclerView.ViewHolder {
        TextView tvSeat;
        public SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeat = itemView.findViewById(R.id.tvSeat);
        }
    }
}