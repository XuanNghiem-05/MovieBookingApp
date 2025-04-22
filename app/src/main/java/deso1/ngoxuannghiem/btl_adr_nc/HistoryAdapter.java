package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<Booking> bookingList;
    private Context context;

    public HistoryAdapter(List<Booking> bookingList, Context context) {
        this.bookingList = bookingList;
        this.context = context;
        // Sắp xếp danh sách bookingList theo thời gian đặt vé giảm dần (mới nhất lên đầu)
        this.bookingList = sortBookingsByTime(bookingList);
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
        if (booking.getUserId() != null) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(booking.getUserId())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String email = snapshot.child("email").getValue(String.class);
                            holder.tvEmail.setText("Email: " + email);
                            Log.e("BookingAdapter", "Lỗi: userId null với booking id = " + booking.getUserId());
                            if (booking.getUserId() == null) {

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
        } else {
            holder.tvEmail.setText("Email: Không rõ");
        }
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
                        Log.e("BookingAdapter", "Lỗi: userId null với booking id = " + booking.getRoomId());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý khi truy vấn bị lỗi
                    }
                });

        // Biến tạm để lưu ngày và giờ trước khi kiểm tra
        final String[] movieDate = {""};
        final String[] showtime = {""};

        FirebaseDatabase.getInstance().getReference("MovieDates")
                .child(booking.getMovieDateId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        movieDate[0] = snapshot.child("date").getValue(String.class);
                        holder.tvMovieDate.setText("Ngày chiếu: " + movieDate[0]);
                        // Nếu showtime đã có thì kiểm tra luôn
                        if (!showtime[0].isEmpty()) {
                            updateItemBackground(holder, movieDate[0], showtime[0]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });

        FirebaseDatabase.getInstance().getReference("Showtimes")
                .child(booking.getShowtimeId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showtime[0] = snapshot.child("time").getValue(String.class);
                        holder.tvShowTime.setText("Giờ chiếu: " + showtime[0]);
                        // Nếu movieDate đã có thì kiểm tra luôn
                        if (!movieDate[0].isEmpty()) {
                            updateItemBackground(holder, movieDate[0], showtime[0]);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
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
        TextView tvTicketId, tvEmail, tvMovieName, tvShowDate, tvRoom, tvShowTime, tvSeat, tvFoodInfo, tvPaymentMethod, tvBookingDate, tvTotalPrice, tvMovieDate;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTicketId = itemView.findViewById(R.id.tvTicketId);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvShowDate = itemView.findViewById(R.id.tvShowDate);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvMovieDate = itemView.findViewById(R.id.tvMovieDate);
            tvShowTime = itemView.findViewById(R.id.tvShowTime);
            tvSeat = itemView.findViewById(R.id.tvSeat);
            tvFoodInfo = itemView.findViewById(R.id.tvFoodInfo);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvBookingDate = itemView.findViewById(R.id.tvBookingDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
        }
    }

    private List<Booking> sortBookingsByTime(List<Booking> bookings) {
        Collections.sort(bookings, new Comparator<Booking>() {
            @Override
            public int compare(Booking booking1, Booking booking2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                try {
                    // Chuyển đổi chuỗi ngày giờ thành đối tượng Date
                    Date date1 = dateFormat.parse(booking1.getBookingTime());
                    Date date2 = dateFormat.parse(booking2.getBookingTime());

                    // So sánh các thời gian này theo thứ tự giảm dần (mới nhất lên đầu)
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                    return 0; // Nếu không thể phân tích ngày, sẽ không thay đổi thứ tự
                }
            }
        });
        return bookings;
    }

    // phương thức check thời gian chiếu
    private void updateItemBackground(HistoryViewHolder holder, String movieDate, String showtime) {
        try {
            // Chuyển từ yyyy-MM-dd (Firebase format) sang dd/MM/yyyy
            SimpleDateFormat firebaseFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date originalDate = firebaseFormat.parse(movieDate);
            String formattedDate = targetFormat.format(originalDate);

            // Ghép ngày + giờ lại thành chuỗi đầy đủ "dd/MM/yyyy HH:mm"
            String dateTimeStr = formattedDate + " " + showtime; // Ví dụ: 20/04/2025 18:30

            // Parse ngày giờ vào đối tượng Date
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date showDateTime = sdf.parse(dateTimeStr);
            Date now = new Date(); // Lấy thời gian hiện tại

            // So sánh thời gian chiếu với thời gian hiện tại
            if (showDateTime != null && showDateTime.before(now)) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_200)); // Đã chiếu
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white)); // Chưa chiếu
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white)); // Mặc định trắng nếu lỗi
        }
    }
}