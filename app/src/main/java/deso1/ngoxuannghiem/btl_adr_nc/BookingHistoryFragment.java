package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingHistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookingHistoryAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();

    private DatabaseReference bookingRef, movieRef, showtimeRef, roomRef, foodRef;
    private FirebaseAuth auth;

    // Map hỗ trợ hiển thị thông tin đầy đủ
    private Map<String, Movie> movieMap = new HashMap<>();
    private Map<String, String> showtimeMap = new HashMap<>();
    private Map<String, String> roomMap = new HashMap<>();
    private Map<String, String> foodMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_history, container, false);
        recyclerView = view.findViewById(R.id.recyclerBookingHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        auth = FirebaseAuth.getInstance();
        bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        movieRef = FirebaseDatabase.getInstance().getReference("Movies");
        showtimeRef = FirebaseDatabase.getInstance().getReference("Showtimes");
        roomRef = FirebaseDatabase.getInstance().getReference("Rooms");
        foodRef = FirebaseDatabase.getInstance().getReference("Foods");

        // Load các map trước, sau đó load bookings
        loadDataMaps();

        return view;
    }

    private void loadDataMaps() {
        movieRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String id = data.getKey();
                    String name = data.child("name").getValue(String.class);
                    String startDate = data.child("startDate").getValue(String.class);
                    movieMap.put(id, new Movie(name, startDate));
                }

                roomRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String id = data.getKey();
                            String room = data.child("name").getValue(String.class);
                            roomMap.put(id, room);
                        }

                showtimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String id = data.getKey();
                            String time = data.child("time").getValue(String.class);
                            showtimeMap.put(id, time);
                        }

//                        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    String id = data.getKey();
//                                    String name = data.child("name").getValue(String.class);
//                                    foodMap.put(id, name);
//                                }

                                // Sau khi load xong map, load booking
                                loadBookingsFromRealtime();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
                    }

//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {}
//                });
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadBookingsFromRealtime() {
        String userId = auth.getCurrentUser().getUid();

        bookingRef.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookingList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Booking booking = data.getValue(Booking.class);
                            if (booking != null) {
                                bookingList.add(booking);
                            }
                        }

                        adapter = new BookingHistoryAdapter(getContext(), bookingList, movieMap, showtimeMap, roomMap, foodMap);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}