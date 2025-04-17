package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private List<Booking> bookingList = new ArrayList<>();
    private DatabaseReference bookingRef; // Firebase reference (hoặc nơi bạn lấy dữ liệu)

    private EditText edtSearch; // Thanh tìm kiếm

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        // Khởi tạo RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter
        historyAdapter = new HistoryAdapter(bookingList, getContext());
        recyclerView.setAdapter(historyAdapter);

        // Khởi tạo thanh tìm kiếm
        edtSearch = view.findViewById(R.id.edtSearch);

        // Lắng nghe sự kiện tìm kiếm
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterBookings(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Lấy dữ liệu từ Firebase hoặc nguồn dữ liệu khác
        fetchBookingData();

        return view;
    }

    private void fetchBookingData() {
        // Giả sử bạn sử dụng Firebase Realtime Database, thay đổi theo nguồn dữ liệu của bạn
        bookingRef = FirebaseDatabase.getInstance().getReference("Bookings");
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bookingList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Booking booking = snapshot.getValue(Booking.class);
                    if (booking != null) {
                        bookingList.add(booking);
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("HistoryFragment", "Failed to fetch booking data: " + databaseError.getMessage());
            }
        });
    }

    private void filterBookings(String query) {
        List<Booking> filteredList = new ArrayList<>();
        for (Booking booking : bookingList) {
            // Lọc theo mã vé (id)
            if (booking.getId().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(booking);
            }
        }
        historyAdapter.updateList(filteredList);
    }
}