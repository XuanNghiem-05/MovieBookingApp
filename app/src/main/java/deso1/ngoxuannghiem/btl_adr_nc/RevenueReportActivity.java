package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RevenueReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvTotalRevenue;
    private RevenueReportAdapter adapter;
    private List<RevenueItem> reportList = new ArrayList<>();
    private DatabaseReference bookingRef, movieRef;
    private Map<String, RevenueItem> revenueMap = new HashMap<>();
    private int totalAllRevenue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_report);

        recyclerView = findViewById(R.id.recyclerViewReport);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RevenueReportAdapter(reportList);
        recyclerView.setAdapter(adapter);

        bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        movieRef = FirebaseDatabase.getInstance().getReference("Movies");

        loadRevenueReport();
    }

    private void loadRevenueReport() {
        bookingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalAllRevenue = 0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking == null) continue;

                    String movieId = booking.getMovieId();
                    int quantity = booking.getSeats() != null ? booking.getSeats().size() : 0;
                    int total = booking.getTotalPrice();

                    totalAllRevenue += total;

                    if (!revenueMap.containsKey(movieId)) {
                        revenueMap.put(movieId, new RevenueItem(movieId, "", quantity, total));
                    } else {
                        RevenueItem item = revenueMap.get(movieId);
                        item.setTicketCount(item.getTicketCount() + quantity);
                        item.setRevenue(item.getRevenue() + total);
                    }
                }

                fetchMovieNamesAndUpdateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void fetchMovieNamesAndUpdateUI() {
        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int stt = 1;
                reportList.clear();
                for (RevenueItem item : revenueMap.values()) {
                    String movieName = snapshot.child(item.getMovieId()).child("name").getValue(String.class);
                    item.setMovieName(movieName != null ? movieName : "Không rõ");
                    item.setIndex(stt++);
                    reportList.add(item);
                }

                adapter.notifyDataSetChanged();
                tvTotalRevenue.setText("Tổng doanh thu: " + totalAllRevenue + " VND");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
