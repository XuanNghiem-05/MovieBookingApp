package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private Context context;
    private List<Booking> bookingList;
    private Map<String, Movie> movieMap;   // movieId -> movieName
    private Map<String, String> showtimeMap;
    private Map<String, String> roomMap;
    private Map<String, String> foodMap;    // foodId -> foodName

    public BookingHistoryAdapter(Context context, List<Booking> bookingList,
                                 Map<String, Movie> movieMap,
                                 Map<String, String> showtimeMap,
                                 Map<String, String> roomMap,
                                 Map<String, String> foodMap) {
        this.context = context;
        this.bookingList = bookingList;
        this.movieMap = movieMap;
        this.showtimeMap = showtimeMap;
        this.roomMap = roomMap;
        this.foodMap = foodMap;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);


        holder.tvBookingId.setText("Mã vé: " + booking.getId());
        // Lấy tên phim từ map, nếu không có thì "Không rõ"
        Movie movie = movieMap.getOrDefault(booking.getMovieId(), new Movie("Không rõ", "Không rõ"));
        String movieName = movie.getName();
        String startDate = movie.getStartDate();
        holder.tvMovie.setText("Phim: " + movieName);
        holder.tvStartDate.setText("Khởi chiếu: " + startDate);

        // Phòng chiếu - nếu roomId null hoặc rỗng
//        String roomText = (booking.getRoomId() != null && !booking.getRoomId().isEmpty())
//                ? booking.getRoomId()
//                : "Không rõ";
//        holder.tvRoom.setText("Phòng: " + roomText);

        String room = roomMap.getOrDefault(booking.getRoomId(), "Không rõ");
        holder.tvRoom.setText("Phòng chiếu: " + room);

        // Giờ chiếu
        String showtime = showtimeMap.getOrDefault(booking.getShowtimeId(), "Không rõ");
        holder.tvShowtime.setText("Giờ chiếu: " + showtime);

        // Ghế và số lượng vé
        List<String> seatList = booking.getSeats();
        if (seatList != null && !seatList.isEmpty()) {
            String seatsJoined = TextUtils.join(", ", seatList);
            holder.tvSeats.setText("Ghế: " + seatsJoined);
            holder.tvSeatCount.setText("Số lượng vé: " + seatList.size());
        } else {
            holder.tvSeats.setText("Ghế: Không có");
            holder.tvSeatCount.setText("Số lượng vé: 0");
        }

        // Đồ ăn (nếu foodId có trong map và số lượng > 0)
        List<Food> foodList = booking.getFoods();
        StringBuilder foodDisplay = new StringBuilder("Đồ ăn: ");
        if (foodList != null && !foodList.isEmpty()) {

            for (Food food : foodList) {
                foodDisplay.append(food.getName())
                        .append(" x")
                        .append(food.getQuantity())
                        .append(", ");
            }
            foodDisplay.setLength(foodDisplay.length() - 2); // Bỏ dấu phẩy cuối
            holder.tvFoods.setText(foodDisplay.toString());
        } else {
            holder.tvFoods.setText("Đồ ăn: Không có");
        }

        // Thanh toán
        holder.tvPayment.setText("Thanh toán: " + booking.getPaymentMethod());

        // Thời gian đặt vé (dựa theo ID timestamp)
        holder.tvBookingTime.setText("Đặt lúc: " + booking.getBookingTime());

        // Tổng tiền
        holder.tvTotal.setText("Tổng tiền: " + booking.getTotalPrice() + "đ");

        String qrContent = "Mã vé: " + booking.getId() + "\n"
                + "Phim: " + movieName + "\n"
                + "Ghế: " + booking.getSeats().toString() + "\n"
                + "Ngày chiếu: " + startDate + "\n"
                + "Phòng: " + room + "\n"
                + "Giờ chiếu: " + showtime + "\n"
                + "Đồ ăn: " + foodDisplay  + "\n"
                + "Thanh toán: " + booking.getPaymentMethod() + "\n"
                + "Đặt lúc: " + booking.getBookingTime() + "\n"
                + "Tổng tiền: " + booking.getTotalPrice() + "\n";

        Bitmap qrBitmap = generateQRCode(qrContent);
        holder.imgQrCode.setImageBitmap(qrBitmap);
    }
    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvBookingId, tvMovie, tvRoom, tvShowtime, tvSeats, tvSeatCount, tvFoods, tvPayment, tvBookingTime, tvTotal, tvStartDate;
        ImageView imgQrCode;
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tvBookingId);
            tvMovie = itemView.findViewById(R.id.tvMovie);
            tvStartDate = itemView.findViewById(R.id.tvStartDate);
            tvRoom = itemView.findViewById(R.id.tvRoom);
            tvShowtime = itemView.findViewById(R.id.tvShowtime);
            tvSeats = itemView.findViewById(R.id.tvSeats);
            tvSeatCount = itemView.findViewById(R.id.tvTicketCount);
            tvFoods = itemView.findViewById(R.id.tvFoods);
            tvPayment = itemView.findViewById(R.id.tvPayment);
            tvBookingTime = itemView.findViewById(R.id.tvBookingDateTime);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            imgQrCode = itemView.findViewById(R.id.imgQrCode);
        }
    }

    // Lấy thời gian tạo từ ID (nếu ID là timestamp) hoặc có thể thêm createdAt vào Booking nếu cần
    private String getTimeFromId(String id) {
        try {
            long timestamp = Long.parseLong(id); // id = System.currentTimeMillis() khi lưu
            Date date = new Date(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return sdf.format(date);
        } catch (Exception e) {
            return "Không rõ";
        }
    }

    private Bitmap generateQRCode(String content) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    content, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.RGB_565);
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 200; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}