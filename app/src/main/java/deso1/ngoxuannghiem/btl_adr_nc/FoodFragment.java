package deso1.ngoxuannghiem.btl_adr_nc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class FoodFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText edtSearch;
    private FloatingActionButton btnAdd;
    private FoodAdapter adapter;
    private List<Food> foodList = new ArrayList<>();
    private DatabaseReference foodRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFood);
        edtSearch = view.findViewById(R.id.edtSearchFood);
        btnAdd = view.findViewById(R.id.btnAddFood);

        foodRef = FirebaseDatabase.getInstance().getReference("Foods");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FoodAdapter(foodList, requireContext(), this); // Truyền fragment nếu cần gọi lại delete
        recyclerView.setAdapter(adapter);

        loadFood();

        btnAdd.setOnClickListener(v -> showAddOrEditDialog(null));

        edtSearch.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterFoods(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void loadFood() {
        foodRef.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                foodList.clear();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Food food = item.getValue(Food.class);
                    foodList.add(food);
                }
                adapter.notifyDataSetChanged();
            }

            public void onCancelled(DatabaseError error) {
                Toast.makeText(getContext(), "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterFoods(String keyword) {
        List<Food> filtered = new ArrayList<>();
        for (Food food : foodList) {
            if (food.name.toLowerCase().contains(keyword.toLowerCase())) {
                filtered.add(food);
            }
        }
        adapter.updateList(filtered);
    }

    public void showAddOrEditDialog(Food foodToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(foodToEdit == null ? "Thêm món ăn" : "Sửa món ăn");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_food, null);
        EditText edtName = view.findViewById(R.id.edtFoodName);
        EditText edtPrice = view.findViewById(R.id.edtFoodPrice);
        EditText edtQuantity = view.findViewById(R.id.edtFoodQuantity);

        if (foodToEdit != null) {
            edtName.setText(foodToEdit.name);
            edtPrice.setText(String.valueOf(foodToEdit.price));
            edtQuantity.setText(String.valueOf(foodToEdit.quantity));
        }

        builder.setView(view);

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String name = edtName.getText().toString();
            int price = Integer.parseInt(edtPrice.getText().toString());
            int quantity = Integer.parseInt(edtQuantity.getText().toString());

            if (foodToEdit == null) {
                String id = foodRef.push().getKey();
                Food newFood = new Food(id, name, price, quantity,0);
                foodRef.child(id).setValue(newFood);
            } else {
                foodToEdit.name = name;
                foodToEdit.price = price;
                foodToEdit.quantity = quantity;
                foodRef.child(foodToEdit.id).setValue(foodToEdit);
            }
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    public void deleteFood(Food food) {
        foodRef.child(food.id).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show());
    }
}