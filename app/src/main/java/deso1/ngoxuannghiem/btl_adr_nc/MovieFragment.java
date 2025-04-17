package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MovieFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText edtSearch;
    private FloatingActionButton btnAdd;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();
    private DatabaseReference movieRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMovie);
        edtSearch = view.findViewById(R.id.edtSearchMovie);
        btnAdd = view.findViewById(R.id.btnAddMovie);

        movieRef = FirebaseDatabase.getInstance().getReference("Movies");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MovieAdapter(movieList, requireContext(), this);
        recyclerView.setAdapter(adapter);

        loadMovies();

        btnAdd.setOnClickListener(v -> showAddOrEditDialog(null));

        edtSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadMovies() {
        movieRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                movieList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Movie movie = item.getValue(Movie.class);
                    movieList.add(movie);
                }
                adapter.notifyDataSetChanged();
            }

            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterMovies(String keyword) {
        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : movieList) {
            if (movie.name.toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(movie);
            }
        }
        adapter.updateList(filtered);
    }

    public void showAddOrEditDialog(Movie movieToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(movieToEdit == null ? "Thêm phim" : "Sửa phim");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_movie, null);
        EditText edtName = view.findViewById(R.id.edtMovieName);
        EditText edtDescription = view.findViewById(R.id.edtMovieDescription);
        EditText edtPrice = view.findViewById(R.id.edtMoviePrice);
        EditText edtStartDate = view.findViewById(R.id.edtMovieStartDate);
        EditText edtImage = view.findViewById(R.id.edtMovieImage);
        EditText edtBanner = view.findViewById(R.id.edtMovieBanner);
        Spinner spnCategory = view.findViewById(R.id.spnMovieCategory);

        List<Category> categoryList = new ArrayList<>();
        ArrayAdapter<Category> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categoryList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategory.setAdapter(spinnerAdapter);

        // Load categories từ Firebase
        FirebaseDatabase.getInstance().getReference("Categories")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        categoryList.clear();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            Category category = item.getValue(Category.class);
                            categoryList.add(category);
                        }
                        spinnerAdapter.notifyDataSetChanged();

                        // Nếu đang sửa phim, set lại vị trí category trong spinner
                        if (movieToEdit != null) {
                            for (int i = 0; i < categoryList.size(); i++) {
                                if (categoryList.get(i).id == movieToEdit.categoryId) {
                                    spnCategory.setSelection(i);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Lỗi tải thể loại", Toast.LENGTH_SHORT).show();
                    }
                });

        if (movieToEdit != null) {
            edtName.setText(movieToEdit.name);
            edtDescription.setText(movieToEdit.description);
            edtPrice.setText(String.valueOf(movieToEdit.price));
            edtStartDate.setText(movieToEdit.startDate);
            edtImage.setText(movieToEdit.imageUrl);
            edtBanner.setText(movieToEdit.bannerUrl);
        }

        builder.setView(view);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = edtName.getText().toString();
            String description = edtDescription.getText().toString();
            int price = Integer.parseInt(edtPrice.getText().toString());
            String startDate = edtStartDate.getText().toString();
            String image = edtImage.getText().toString();
            String banner = edtBanner.getText().toString();

            int selectedIndex = spnCategory.getSelectedItemPosition();
            int categoryId = categoryList.get(selectedIndex).id;

            if (movieToEdit == null) {
                String id = movieRef.push().getKey();
                Movie newMovie = new Movie(id, name, description, price, startDate, image, banner, categoryId);
                movieRef.child(id).setValue(newMovie);
            } else {
                movieToEdit.name = name;
                movieToEdit.description = description;
                movieToEdit.price = price;
                movieToEdit.startDate = startDate;
                movieToEdit.imageUrl = image;
                movieToEdit.bannerUrl = banner;
                movieToEdit.categoryId = categoryId;
                movieRef.child(movieToEdit.id).setValue(movieToEdit);
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    public void deleteMovie(Movie movie) {
        movieRef.child(movie.id).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show());
    }
}
