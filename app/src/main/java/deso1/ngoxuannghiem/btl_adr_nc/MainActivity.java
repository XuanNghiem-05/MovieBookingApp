package deso1.ngoxuannghiem.btl_adr_nc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //loadAndInsertShowtimes();
    }

    private void insertAllShowtimesForEachMovie(List<String> movieIds, List<String> roomIds, List<String> showtimeIds) {
        DatabaseReference movieShowtimesRef = FirebaseDatabase.getInstance().getReference("MovieShowtimes");
        Random random = new Random();

        for (String movieId : movieIds) {
            for (String showtimeId : showtimeIds) {
                String randomRoomId = roomIds.get(random.nextInt(roomIds.size()));
                String key = movieId + "_" + showtimeId;

                MovieShowtime showtime = new MovieShowtime(movieId, randomRoomId, showtimeId);
                movieShowtimesRef.child(key).setValue(showtime)
                        .addOnSuccessListener(aVoid -> Log.d("Insert", "Thêm thành công: " + key))
                        .addOnFailureListener(e -> Log.e("Insert", "Thêm thất bại: " + key, e));
            }
        }
    }

    private void checkAndInsertShowtime(String movieId, List<String> roomIds, String showtimeId, DatabaseReference movieShowtimesRef) {
        for (String roomId : roomIds) {
            String key = roomId + "_" + showtimeId;

            movieShowtimesRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        MovieShowtime showtime = new MovieShowtime(movieId, roomId, showtimeId);
                        movieShowtimesRef.child(key).setValue(showtime);
                        Log.d("InsertShowtime", "Gán: " + movieId + " -> " + key);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("FirebaseError", "Lỗi đọc dữ liệu: " + error.getMessage());
                }
            });

            // Dừng vòng lặp sau khi chọn phòng đầu tiên chưa bị trùng (chỉ chèn 1 room/showtime cho mỗi phim)
            break;
        }
    }
    private void loadAndInsertShowtimes() {
        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movies");
        DatabaseReference roomRef = FirebaseDatabase.getInstance().getReference("Rooms");
        DatabaseReference showtimeRef = FirebaseDatabase.getInstance().getReference("Showtimes");

        movieRef.get().addOnSuccessListener(movieSnap -> {
            roomRef.get().addOnSuccessListener(roomSnap -> {
                showtimeRef.get().addOnSuccessListener(showtimeSnap -> {
                    List<String> movieIds = new ArrayList<>();
                    for (DataSnapshot m : movieSnap.getChildren()) {
                        movieIds.add(m.getKey());
                    }

                    List<String> roomIds = new ArrayList<>();
                    for (DataSnapshot r : roomSnap.getChildren()) {
                        roomIds.add(r.getKey());
                    }

                    List<String> showtimeIds = new ArrayList<>();
                    for (DataSnapshot s : showtimeSnap.getChildren()) {
                        showtimeIds.add(s.getKey());
                    }

                    insertAllShowtimesForEachMovie(movieIds, roomIds, showtimeIds);
                });
            });
        });
    }
}