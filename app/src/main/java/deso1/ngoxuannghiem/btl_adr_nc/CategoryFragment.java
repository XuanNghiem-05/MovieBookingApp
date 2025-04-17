package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

public class CategoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private EditText edtSearch;
    private FloatingActionButton btnAdd;
    private CategoryAdapter adapter;
    private List<Category> categoryList = new ArrayList<>();
    private DatabaseReference categoryRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCategory);
        edtSearch = view.findViewById(R.id.edtSearchCategory);
        btnAdd = view.findViewById(R.id.btnAddCategory);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CategoryAdapter(categoryList, getContext());
        recyclerView.setAdapter(adapter);

        categoryRef = FirebaseDatabase.getInstance().getReference("Categories");

        loadCategories();

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
        });

        btnAdd.setOnClickListener(v -> showAddCategoryDialog());

        return view;
    }

    private void loadCategories() {
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Category category = ds.getValue(Category.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                adapter.setOriginalList(categoryList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Thêm thể loại");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_category, null);
        EditText edtName = view.findViewById(R.id.edtCategoryName);
        EditText edtImage = view.findViewById(R.id.edtCategoryImage);

        builder.setView(view);
        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String name = edtName.getText().toString().trim();
            String imageUrl = edtImage.getText().toString().trim();
            if (!name.isEmpty() && !imageUrl.isEmpty()) {
                long newId = System.currentTimeMillis();
                Category category = new Category((int) newId, name, imageUrl);
                categoryRef.child(String.valueOf(newId)).setValue(category);
            } else {
                Toast.makeText(getContext(), "Không được để trống", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
}

