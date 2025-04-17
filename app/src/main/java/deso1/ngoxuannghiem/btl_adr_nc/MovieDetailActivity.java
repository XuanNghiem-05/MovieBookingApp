package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView imgMovie;
    private TextView tvName, tvCategory, tvPrice, tvDescription, tvStartDate, tvMovieId;
    private Button btnBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Ánh xạ
        imgMovie = findViewById(R.id.imgMovie);
        tvName = findViewById(R.id.tvName);
        tvCategory = findViewById(R.id.tvCategory);
        tvPrice = findViewById(R.id.tvPrice);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvMovieId = findViewById(R.id.tvMovieId);
        tvDescription = findViewById(R.id.tvDescription);
        btnBook = findViewById(R.id.btnBook);

// Nhận dữ liệu từ intent
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        int categoryId = intent.getIntExtra("category", -1); // Đổi tên cho rõ nghĩa
        int price = intent.getIntExtra("price", 0);
        String description = intent.getStringExtra("description");
        String imageUrl = intent.getStringExtra("imageUrl");
        String startDate = intent.getStringExtra("startDate");

// Gán dữ liệu
        tvName.setText(name);
        tvPrice.setText("Giá vé: " + price + "đ");
        tvDescription.setText(description);
        tvStartDate.setText("Khởi chiếu: " + startDate);
        tvMovieId.setText(id);
        Glide.with(this).load(imageUrl).into(imgMovie);

// Lấy tên thể loại từ Firebase
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference("Categories");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot child : snapshot.getChildren()) {
                    Category category = child.getValue(Category.class);
                    if (category != null && category.getId() == categoryId) {
                        tvCategory.setText("Thể loại: " + category.getName());
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    tvCategory.setText("Thể loại: Không xác định");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvCategory.setText("Lỗi tải thể loại");
            }
        });

        // Nút đặt vé (có thể chuyển sang trang chọn suất chiếu, ghế,...)
        btnBook.setOnClickListener(v -> {
//            Toast.makeText(this, "Đi đến trang đặt vé", Toast.LENGTH_SHORT).show();
//            startActivity(new Intent(this, BookingActivity.class));

            Intent intent1 = new Intent(MovieDetailActivity.this, BookingActivity.class);
            intent1.putExtra("name", name); // String
            intent1.putExtra("price", price); // int
            intent1.putExtra("id" , id);
            intent1.putExtra("statedDate", startDate);
            startActivity(intent1);
        });

        // Xử lý nút quay lại
        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }
}