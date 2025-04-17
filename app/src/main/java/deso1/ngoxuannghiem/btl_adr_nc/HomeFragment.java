package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private EditText edtSearch;
    private ViewFlipper viewFlipper;
    private RecyclerView rvMovies;
    private MovieAdapter_Home movieAdapter;
    private List<Movie> movieList;
    private List<Movie> filteredList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        edtSearch = view.findViewById(R.id.edtSearch);
        viewFlipper = view.findViewById(R.id.viewFlipper);
        rvMovies = view.findViewById(R.id.rvMovies);
        filteredList = new ArrayList<>();
        movieList = new ArrayList<>();

        setupBanner();
        setupRecyclerView();

        // Xử lý tìm kiếm khi người dùng gõ vào EditText
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void setupBanner() {
        int[] banners = {R.drawable.langman, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4};
        for (int banner : banners) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(banner);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewFlipper.addView(imageView);
        }
        viewFlipper.setAutoStart(true);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.startFlipping();
    }

    private void setupRecyclerView() {
        movieAdapter = new MovieAdapter_Home(filteredList, getContext());
        rvMovies.setLayoutManager(new GridLayoutManager(getContext(), 3));
        rvMovies.setAdapter(movieAdapter);

        DatabaseReference movieRef = FirebaseDatabase.getInstance().getReference("Movies");
        movieRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                filteredList.clear();
                for (DataSnapshot movieSnap : snapshot.getChildren()) {
                    Movie movie = movieSnap.getValue(Movie.class);
                    if (movie != null) {
                        movieList.add(movie);
                        filteredList.add(movie); // ban đầu hiển thị tất cả
                    }
                }
                movieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi tải phim: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMovies(String query) {
        filteredList.clear();
        for (Movie movie : movieList) {
            if (movie.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(movie);
            }
        }
        movieAdapter.notifyDataSetChanged();
    }
}