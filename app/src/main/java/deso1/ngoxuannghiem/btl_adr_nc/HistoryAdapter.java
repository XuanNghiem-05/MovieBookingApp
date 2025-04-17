package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<Booking> bookingList;
    private Context context;

    public HistoryAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // Set thông tin vé vào các TextViews
        holder.tvTicketId.setText("Mã vé: " + booking.getId());
        //holder.tvEmail.setText("Email: " + booking.getUserId());
        // Lấy email người dùng từ Firebase
        FirebaseDatabase.getInstance().getReference("Users")
                .child(booking.getUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Lấy email từ thông tin người dùng
                        String email = snapshot.child("email").getValue(String.class);
                        holder.tvEmail.setText("Email: " + email);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý khi truy vấn bị lỗi
                    }
                });

        // Lấy tên phim từ Firebase
        FirebaseDatabase.getInstance().getReference("Movies")
                .child(booking.getMovieId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Lấy tên phim từ dữ liệu phim
                        String movieName = snapshot.child("name").getValue(String.class);
                        holder.tvMovieName.setText("Phim: " + movieName);
                        String startDate = snapshot.child("startDate").getValue(String.class);
                        holder.tvShowDate.setText("Khởi chiếu: " + startDate);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý khi truy vấn bị lỗi
                    }
                });


        FirebaseDatabase.getInstance().getReference("Rooms")
                .child(booking.getRoomId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Lấy tên phim từ dữ liệu phim
                        String roomName = snapshot.child("name").getValue(String.class);
                        holder.tvRoom.setText("Phòng: " + roomName);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý khi truy vấn bị lỗi
                    }
                });


        FirebaseDatabase.getInstance().getReference("Showtimes")
                .child(booking.getShowtimeId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Lấy tên phim từ dữ liệu phim
                        String time = snapshot.child("time").getValue(String.class);
                        holder.tvShowTime.setText("Giờ chiếu: " + time);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý khi truy vấn bị lỗi
                    }
                });

        // Hiển thị danh sách ghế
        StringBuilder seats = new StringBuilder();
        for (String seat : booking.getSeats()) {
            seats.append(seat).append(", ");
        }
        holder.tvSeat.setText("Ghế: " + (seats.length() > 0 ? seats.substring(0, seats.length() - 2) : "Không có ghế"));

        // Hiển thị thông tin đồ ăn
        StringBuilder foodInfo = new StringBuilder();
        for (Food food : booking.getFoods()) {
            foodInfo.append(food.getName()).append(" x").append(food.getQuantity()).append(", ");
        }
        holder.tvFoodInfo.setText("Đồ ăn/đồ uống: " + (foodInfo.length() > 0 ? foodInfo.substring(0, foodInfo.length() - 2) : "Không có đồ ăn"));

        holder.tvPaymentMethod.setText("Phương thức thanh toán: " + booking.getPaymentMethod());
        holder.tvBookingDate.setText("Ngày đặt: " + booking.getBookingTime());
        holder.tvTotalPrice.setText("Tổng tiền: " + booking.getTotalPrice());
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void updateList(List<Booking> newList) {
        bookingList = newList;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTicketId, tvEmail, tvMovieName, tvShowDate, tvRoom, tvShowTime, tvSeat, tvFoodInfo, tvPaymentMethod, tvBookingDate, tvTotalPrice;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketId = itemView.findViewById(R.id.tvTicketId);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvShowDate = itemView.findViewById(R.id.tvShowDate);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvShowTime = itemView.findViewById(R.id.tvShowTime);
            tvSeat = itemView.findViewById(R.id.tvSeat);
            tvFoodInfo = itemView.findViewById(R.id.tvFoodInfo);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }
}