package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    TextView tvMovieName, tvMoviePrice, tvStartDate;
    Spinner spinnerShowtime, spinnerFood, spinnerMovieDate;
    RadioGroup radioGroupPayment;
    Button btnConfirmBooking;
    ImageButton btnBack;
    RecyclerView rvFood;
    String roomId;


    //String selectedSeat = "";
    private List<String> selectedSeats = new ArrayList<>();
    String selectedShowtime = "";
    String selectedMovieDate = "";
    String selectedFood = "";
    String paymentMethod = "";

    private ArrayList<Food> foodList;
    private FoodOrderAdapter foodOrderAdapter;

    private List<String> showtimeList = new ArrayList<>();
    private ArrayAdapter<String> showtimeAdapter;
    private List<Showtime> showtimeObjects = new ArrayList<>();
    private List<MovieDate> movieDateObjects = new ArrayList<>();
    private List<String> movieDates = new ArrayList<>();
    private ArrayAdapter<String> movieDateAdapter;

    RecyclerView rvSeats;
    SeatAdapter seatAdapter;
    List<Seat> seatList;
    List<String> selectedSeatNames = new ArrayList<>();

    GridLayout seatGrid;

    private String selectedMovieId;       // ID bộ phim đã chọn
    private String selectedShowtimeId;
    private String selectedMovieDateId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Ánh xạ view
        tvMovieName = findViewById(R.id.tvMovieName);
        tvMoviePrice = findViewById(R.id.tvMoviePrice);
        tvStartDate = findViewById(R.id.tvStartDate);
        spinnerShowtime = findViewById(R.id.spinnerShowtime);
        spinnerMovieDate = findViewById(R.id.spinnerMovieDate);
        //spinnerFood = findViewById(R.id.spinnerFood);
        rvFood = findViewById(R.id.rvFood);
        rvFood.setLayoutManager(new LinearLayoutManager(this));
        seatGrid = findViewById(R.id.seatGrid);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        btnBack = findViewById(R.id.btnBack);

        generateSeatGrid();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String movieName = intent.getStringExtra("name");
        int price = intent.getIntExtra("price", 0);
        selectedMovieId = intent.getStringExtra("id");
        String startDate = intent.getStringExtra("statedDate");

        tvMovieName.setText("Phim: " + movieName);
        tvMoviePrice.setText("Giá vé: " + price + "đ");
        tvStartDate.setText("Khởi chiếu: " + startDate);
        // Giờ chiếu mẫu

        // KHỞI TẠO ADAPTER TRƯỚC!
        showtimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, showtimeList);
        showtimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerShowtime.setAdapter(showtimeAdapter);
        // Sau đó gọi hàm load dữ liệu
        //loadShowtimesFromFirebase();
        loadMovieDatesFromFirebase();

        // Set adapter với danh sách MovieDate
         movieDateAdapter = new ArrayAdapter<>(BookingActivity.this, android.R.layout.simple_spinner_item, movieDates);
        movieDateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMovieDate.setAdapter(movieDateAdapter);

        // Set listener sau khi adapter được set
        spinnerMovieDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!movieDateObjects.isEmpty()) {
                    MovieDate selectedDate = movieDateObjects.get(position);
                    selectedMovieDateId = selectedDate.getId();
                    String selectedDateString = selectedDate.getDate();

                    Log.e("SelectedDate", "ID: " + selectedMovieDateId + ", Date: " + selectedDateString);
                    loadShowtimesFromFirebase(selectedDateString);
                    // Gọi các hàm cần dùng ID tại đây
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không chọn gì
            }
        });

        spinnerShowtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Lấy đối tượng Showtime từ showtimeObjects
                Showtime selectedShowtime = showtimeObjects.get(position);

                // Lấy ID của giờ chiếu đã chọn
                selectedShowtimeId = selectedShowtime.getId();  // Lấy ID từ đối tượng Showtime
                // Optional: Bạn có thể làm thêm việc gì đó nếu cần với selectedShowtimeId hoặc other properties
                resetSeatGrid();

                // Load ghế đã đặt
                loadBookedSeats(selectedMovieId, selectedShowtimeId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Xử lý nếu không có lựa chọn nào được chọn (thường không cần thiết)
            }
        });
        foodList = new ArrayList<>();
        foodOrderAdapter = new FoodOrderAdapter(foodList);

// setAdapter ở đây
        rvFood.setLayoutManager(new LinearLayoutManager(this));
        rvFood.setAdapter(foodOrderAdapter);

// Load dữ liệu từ Firebase
        loadFoodData1(foodOrderAdapter);
        loadFoodData();

        // Nút quay lại
        btnBack.setOnClickListener(view -> finish());

        // Nút xác nhận đặt vé
        btnConfirmBooking.setOnClickListener(view -> {
            Log.d("DEBUG", "Foodlll: " + foodList);
            selectedShowtime = spinnerShowtime.getSelectedItem().toString();
            selectedMovieDate = spinnerMovieDate.getSelectedItem().toString();
            List<Food> selectedFoods = getSelectedFoods();
            Log.d("DEBUG", "Foodsele: " + selectedFoods);

            int selectedId = radioGroupPayment.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                paymentMethod = rb.getText().toString();
            } else {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedSeats == null || selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một ghế", Toast.LENGTH_SHORT).show();
                return;
            }

// Tạo chuỗi tên món ăn để hiển thị
            String foodName = getSelectedFoodNames();
            Log.d("DEBUG", "Foodname: " + foodName);

            // Gọi hàm hiển thị dialog xác nhận
            showConfirmationDialog(
                    movieName,
                    startDate,
                    "", // roomName sẽ được truy vấn trong showConfirmationDialog
                    selectedShowtimeId, // cần được cập nhật từ spinner
                    selectedSeats,
                    foodName,
                    paymentMethod,
                    price,
                    selectedFoods,
                    selectedMovieDateId
            );
        });
    }

    private void loadShowtimesFromFirebase(String selectedMovieDateString) {
        DatabaseReference showtimeRef = FirebaseDatabase.getInstance().getReference("Showtimes");

        showtimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                showtimeList.clear();
                showtimeObjects.clear();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                String currentTimeStr = sdf.format(new Date());

                for (DataSnapshot showtimeSnap : snapshot.getChildren()) {
                    Showtime showtime = showtimeSnap.getValue(Showtime.class);
                    if (showtime != null && showtime.getTime() != null && selectedMovieDateString != null) {
                        try {
                            // Ghép ngày và giờ lại
                            String fullShowtime = selectedMovieDateString + " " + showtime.getTime();
                            Date showtimeDateTime = sdf.parse(fullShowtime);
                            Date currentDateTime = sdf.parse(currentTimeStr);

                            // So sánh thời gian
                            if (showtimeDateTime != null && showtimeDateTime.after(currentDateTime)) {
                                showtimeObjects.add(showtime);
                                showtimeList.add(showtime.getTime());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                showtimeAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingActivity.this, "Lỗi tải giờ chiếu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMovieDatesFromFirebase() {
        DatabaseReference movieDatesRef = FirebaseDatabase.getInstance().getReference("MovieDates");

        movieDatesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                movieDates.clear();
                movieDateObjects.clear();

                // Lấy ngày hiện tại
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDateStr = sdf.format(new Date()); // Lấy ngày hiện tại dưới dạng chuỗi yyyy-MM-dd

                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    MovieDate movieDate = dateSnap.getValue(MovieDate.class);
                    if (movieDate != null && movieDate.getDate() != null) {
                        // Kiểm tra nếu ngày chiếu không nhỏ hơn ngày hiện tại
                        if (movieDate.getDate().compareTo(currentDateStr) >= 0) {
                            // Thêm thứ vào ngày chiếu
                            String dayOfWeek = getDayOfWeek(movieDate.getDate()); // Lấy thứ trong tuần từ ngày
                            //movieDate.setDayOfWeek(dayOfWeek);  // Lưu lại thứ cho mỗi ngày chiếu

                            movieDateObjects.add(movieDate);
                            movieDates.add(movieDate.getDate() + " (" + dayOfWeek + ")"); // Cập nhật danh sách ngày chiếu với thứ
                        }
                    }
                }
                movieDateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingActivity.this, "Không thể tải ngày chiếu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Hàm để lấy thứ trong tuần từ ngày (yyyy-MM-dd)
    private String getDayOfWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(dateStr);

            // Lấy giá trị ngày trong tuần từ đối tượng Date
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK); // Giá trị ngày trong tuần từ 1 đến 7 (Chủ nhật = 1)

            // Mảng chứa tên thứ trong tuần bằng tiếng Việt
            String[] daysOfWeekInVietnamese = {"Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"};

            // Trả về tên thứ trong tuần bằng tiếng Việt
            return daysOfWeekInVietnamese[dayOfWeek - 1]; // Lấy tên thứ, trừ 1 vì chủ nhật có giá trị là 1
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Trả về chuỗi rỗng nếu có lỗi
        }
    }

    private void generateSeatGrid() {
        char[] rows = {'A', 'B', 'C', 'D'};
        int cols = 5;

        for (char row : rows) {
            for (int col = 1; col <= cols; col++) {
                String seatName = row + String.valueOf(col);

                TextView seatView = new TextView(this);
                seatView.setText(seatName);
                seatView.setGravity(Gravity.CENTER);
                seatView.setTextColor(Color.BLACK);
                seatView.setBackgroundResource(R.drawable.bg_seat_unselected);
                seatView.setPadding(8, 8, 8, 8);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = GridLayout.LayoutParams.WRAP_CONTENT;
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                params.setMargins(8, 8, 8, 8);
                seatView.setLayoutParams(params);

                seatView.setOnClickListener(v -> {
                    if (selectedSeats.contains(seatName)) {
                        selectedSeats.remove(seatName);
                        seatView.setBackgroundResource(R.drawable.bg_seat_unselected);
                    } else {
                        selectedSeats.add(seatName);
                        seatView.setBackgroundResource(R.drawable.bg_seat_selected);
                    }
                });

                seatGrid.addView(seatView);
            }
        }
    }

    private void loadFoodData() {
        // Initialize foodList
        foodList = new ArrayList<>();
        // Initialize the RecyclerView Adapter
        FoodOrderAdapter foodOrderAdapter = new FoodOrderAdapter(foodList); // Use your RecyclerView adapter
        // Set the Layout Manager
        rvFood.setLayoutManager(new LinearLayoutManager(this)); //  Initialize rvFood
        // Set the adapter to the RecyclerView
        rvFood.setAdapter(foodOrderAdapter);
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");
        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot foodSnap : snapshot.getChildren()) {
                    Food food = foodSnap.getValue(Food.class);
                    if (food != null) {
                        foodList.add(food);
                    }
                }
                Log.d("DEBUG", "Foodlllaaa: " + foodList);
                foodOrderAdapter.notifyDataSetChanged(); // Notify the RecyclerView adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingActivity.this, "Lỗi tải danh sách đồ ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public interface OnRoomNameCallback {
        void onRoomNameLoaded(String roomName);
    }

    private void showConfirmationDialog(String movieName, String startDate, String roomName,
                                        String showtime, List<String> selectedSeats,
                                        String foodName, String paymentMethod,double ticketPrice, List<Food> selectedFoods, String movieDate) {

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking_confirmation, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        ((TextView) dialogView.findViewById(R.id.tvMovieName)).setText("Tên phim: " + movieName);
        ((TextView) dialogView.findViewById(R.id.tvStartTime)).setText("Khởi chiếu: " + startDate);
        getRoomNameForMovieAndShowtime(selectedMovieId, selectedShowtimeId, new OnRoomNameCallback() {
            @Override
            public void onRoomNameLoaded(String roomName) {
                ((TextView) dialogView.findViewById(R.id.tvRoom)).setText("Phòng chiếu: " + roomName);
            }
        });
        ((TextView) dialogView.findViewById(R.id.tvShowtime)).setText("Giờ chiếu: " + selectedShowtime);
        ((TextView) dialogView.findViewById(R.id.tvSeats)).setText("Ghế đã chọn: " + String.join(", ", selectedSeats));
        ((TextView) dialogView.findViewById(R.id.tvTicketCount)).setText("Số lượng vé: " + selectedSeats.size());
        ((TextView) dialogView.findViewById(R.id.tvFood)).setText("Đồ ăn/Đồ uống: " + foodName);
        ((TextView) dialogView.findViewById(R.id.tvPayment)).setText("Phương thức thanh toán: " + paymentMethod);
        ((TextView) dialogView.findViewById(R.id.tvMovieDate)).setText("Ngày chiếu: " + selectedMovieDate);

        // Calculate total price
        // Load dữ liệu từ Firebase
        Log.d("DEBUG", "sl: " + foodList);
        double ticketTotal = ticketPrice * selectedSeats.size();
        double foodTotal = 0;

        for (Food food : selectedFoods) {
            int quantity = food.getSelectedQuantity();
            if (quantity > 0) {
                foodTotal += food.getPrice() * quantity;
            }
        }

        final double totalPrice = ticketTotal + foodTotal;

        ((TextView) dialogView.findViewById(R.id.tvTotal)).setText("TỔNG TIỀN THANH TOÁN: " + totalPrice + "đ"); // Display total


        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnConfirm = dialogView.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            // Thực hiện lưu Booking vào Firebase tại đây
            saveBookingToFirebase(selectedMovieId, selectedShowtimeId, selectedMovieDateId ,roomId, selectedSeats, selectedFoods, paymentMethod, totalPrice);
            dialog.dismiss();
        });

        dialog.show();
    }



    private void getRoomNameForMovieAndShowtime(String movieId, String showtimeId, OnRoomNameCallback callback) {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        dbRef.child("MovieShowtimes")
                .orderByChild("movieId")
                .equalTo(movieId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean found = false;
                        for (DataSnapshot item : snapshot.getChildren()) {
                            String sId = item.child("showtimeId").getValue(String.class);
                            if (sId != null && sId.equals(showtimeId)) {
                                roomId = item.child("roomId").getValue(String.class);
                                dbRef.child("Rooms").child(roomId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot roomSnap) {
                                        String roomName = roomSnap.child("name").getValue(String.class);
                                        Log.d("DEBUG", "Found roomId: " + roomId);
                                        if (roomName != null) {
                                            callback.onRoomNameLoaded(roomName);
                                        } else {
                                            callback.onRoomNameLoaded("Không rõ");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        callback.onRoomNameLoaded("Không rõ");
                                    }
                                });

                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            callback.onRoomNameLoaded("Không rõ");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onRoomNameLoaded("Không rõ");
                    }
                });
    }

    public String getSelectedFoodNames() {
        List<String> names = new ArrayList<>();
        for (Food food : foodList) {
            if (food.getSelectedQuantity() > 0) {
                names.add(food.getName() + " x" + food.getSelectedQuantity());
                Log.d("DEBUG", "Foodname: " + food.getName());
            }
        }
        return String.join(", ", names);
    }

    private void loadFoodData1(FoodOrderAdapter foodOrderAdapter) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Foods");

        // Lắng nghe sự thay đổi dữ liệu trong Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Xóa dữ liệu cũ trước khi thêm mới
                foodList.clear();

                // Lặp qua tất cả các món ăn trong Firebase và thêm vào foodList
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Food food = snapshot.getValue(Food.class);  // Chuyển đổi dữ liệu thành đối tượng Food
                    if (food != null) {
                        foodList.add(food);  // Thêm món ăn vào danh sách
                    }
                }

                // Cập nhật lại RecyclerView
                foodOrderAdapter.notifyDataSetChanged();
                Log.d("DEBUG", "Food from Firebase: " + foodList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error loading food data", databaseError.toException());
            }
        });
    }

    public List<Food> getSelectedFoods() {
        Log.d("DEBUG", "foodList size: " + foodList.size());

        List<Food> selected = new ArrayList<>();
        for (Food item : foodList) {
            Log.d("DEBUG", "Checking: " + item.getName() + " - quantity: " + item.getSelectedQuantity());
            if (item.getSelectedQuantity() > 0) {
                selected.add(item);
            }
        }
        return selected;
    }

    // Sau khi xác nhận thì thêm dữ liệu vào bảng booking
    private void saveBookingToFirebase(String movieId, String showtimeId, String movieDateId, String roomId, List<String> selectedSeats, List<Food> selectedFoods, String paymentMethod, double totalPrice) {
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        String bookingId = bookingsRef.push().getKey(); // Tạo id tự động
        if (bookingId == null) return;

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(BookingActivity.this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid(); // Lấy userId hiện tại

        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("movieId", movieId);
        bookingData.put("movieDateId", movieDateId);
        bookingData.put("showtimeId", showtimeId);
        bookingData.put("roomId", roomId);
        bookingData.put("seats", selectedSeats);
        bookingData.put("paymentMethod", paymentMethod);
        bookingData.put("totalPrice", totalPrice);
        bookingData.put("userId", userId); // ✅ Thêm dòng này
        String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        bookingData.put("bookingTime", currentTime);
        String id;
        bookingData.put("id",bookingId);


        // Thêm danh sách món ăn (nếu có)
        List<Map<String, Object>> foods = new ArrayList<>();
        for (Food food : selectedFoods) {
            if (food.getSelectedQuantity() > 0) {
                Map<String, Object> foodMap = new HashMap<>();
                foodMap.put("name", food.getName());
                foodMap.put("price", food.getPrice());
                foodMap.put("quantity", food.getSelectedQuantity());
                foods.add(foodMap);
            }
        }
        bookingData.put("foods", foods);

        bookingsRef.child(bookingId).setValue(bookingData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BookingActivity.this, "Đặt vé thành công!", Toast.LENGTH_SHORT).show();

                    // ✅ Cập nhật số lượng món ăn sau khi đặt vé thành công
                    updateFoodQuantities(selectedFoods);
                    Intent intent = new Intent(BookingActivity.this, UserMainActivity.class);
                    intent.putExtra("navigateTo", "home"); // Truyền dữ liệu để điều hướng đến HomeFragment
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Xoá stack nếu cần
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BookingActivity.this, "Lỗi đặt vé: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

    }

    private void loadBookedSeats(String movieId, String showtimeId) {
        DatabaseReference bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");

        bookingRef.orderByChild("movieId").equalTo(movieId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> bookedSeats = new ArrayList<>();

                for (DataSnapshot bookingSnap : snapshot.getChildren()) {
                    Booking booking = bookingSnap.getValue(Booking.class);
                    if (booking != null && showtimeId.equals(booking.getShowtimeId())) {
                        // Giả sử booking.getSeats() là List<String>
                        bookedSeats.addAll(booking.getSeats());
                    }
                }

                updateSeatGridWithBookedSeats(bookedSeats);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(BookingActivity.this, "Lỗi tải ghế đã đặt", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //cập nhật màu sắc và chặn click những ghế đã được đặt:
    private void updateSeatGridWithBookedSeats(List<String> bookedSeats) {
        for (int i = 0; i < seatGrid.getChildCount(); i++) {
            View child = seatGrid.getChildAt(i);
            if (child instanceof TextView) {
                TextView seatView = (TextView) child;
                String seatName = seatView.getText().toString();

                if (bookedSeats.contains(seatName)) {
                    seatView.setBackgroundResource(R.drawable.bg_seat_booked); // màu khác
                    seatView.setEnabled(false);
                    seatView.setTextColor(Color.GRAY);
                }
            }
        }
    }

    //Reset trạng thái ghế khi đổi giờ chiếu
    private void resetSeatGrid() {
        selectedSeats.clear();
        for (int i = 0; i < seatGrid.getChildCount(); i++) {
            View child = seatGrid.getChildAt(i);
            if (child instanceof TextView) {
                TextView seatView = (TextView) child;
                seatView.setEnabled(true);
                seatView.setBackgroundResource(R.drawable.bg_seat_unselected);
                seatView.setTextColor(Color.BLACK);
            }
        }
    }

    // hàm cập nhật số lượng đồ ăn
    private void updateFoodQuantities(List<Food> selectedFoods) {
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");

        for (Food food : selectedFoods) {
            int selectedQuantity = food.getSelectedQuantity();
            if (selectedQuantity > 0) {
                String foodId = food.getId();
                int currentQuantity = food.getQuantity();

                int newQuantity = currentQuantity - selectedQuantity;

                if (newQuantity < 0) newQuantity = 0; // Không âm

                foodRef.child(foodId).child("quantity").setValue(newQuantity)
                        .addOnSuccessListener(aVoid -> Log.d("UPDATE_FOOD", "Đã cập nhật " + food.getName()))
                        .addOnFailureListener(e -> Log.e("UPDATE_FOOD", "Lỗi khi cập nhật " + food.getName(), e));
            }
        }
    }
}
