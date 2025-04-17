package deso1.ngoxuannghiem.btl_adr_nc;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

    public class FoodOrderAdapter extends RecyclerView.Adapter<FoodOrderAdapter.FoodViewHolder> {

        private List<Food> foodList;

        public FoodOrderAdapter(List<Food> foodList) {
            this.foodList = foodList;
        }

        @NonNull
        @Override
        public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food_order, parent, false);
            return new FoodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
            Food food = foodList.get(position);
            holder.tvName.setText(food.getName());
            holder.tvPrice.setText(food.getPrice() + "đ");

            // Hiển thị số lượng người dùng đã chọn (cập nhật từ selectedQuantity)
            holder.etQuantity.setText(String.valueOf(food.getSelectedQuantity()));

            // Cập nhật selectedQuantity khi người dùng thay đổi số lượng trong EditText
            holder.etQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    int qty = 0;
                    try {
                        qty = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        qty = 0; // Mặc định là 0 nếu không thể chuyển đổi
                    }
                    food.setSelectedQuantity(qty); // Cập nhật số lượng người dùng chọn vào selectedQuantity
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void afterTextChanged(Editable s) {}
            });


            // Các hành động + và -
            holder.btnPlus.setOnClickListener(v -> {
                int qty = food.getSelectedQuantity();
                try {
                    qty = Integer.parseInt(holder.etQuantity.getText().toString());
                } catch (NumberFormatException e) {
                    qty = 0;
                }
                qty++;
                food.setSelectedQuantity(qty);  // Cập nhật số lượng người dùng chọn
                holder.etQuantity.setText(String.valueOf(qty));
                getSelectedFoodNames();
                Log.d("DEBUG", "Food: " + food.getSelectedQuantity());
            });

            holder.btnMinus.setOnClickListener(v -> {
                int qty = food.getSelectedQuantity();
                try {
                    qty = Integer.parseInt(holder.etQuantity.getText().toString());
                } catch (NumberFormatException e) {
                    qty = 0;
                }
                if (qty > 0) {
                    qty--;
                    food.setSelectedQuantity(qty);  // Cập nhật số lượng người dùng chọn
                    holder.etQuantity.setText(String.valueOf(qty));
                }
            });

        }

        @Override
        public int getItemCount() {
            return foodList.size();
        }
         static class FoodViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice;
            EditText etQuantity;
            Button btnPlus, btnMinus; // Declare the buttons

            public FoodViewHolder(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tvFoodName);
                tvPrice = itemView.findViewById(R.id.tvFoodPrice);
                etQuantity = itemView.findViewById(R.id.etQuantity);
                btnPlus = itemView.findViewById(R.id.btnPlus);   // Find the plus button
                btnMinus = itemView.findViewById(R.id.btnMinus); // Find the minus button
            }
        }

        public String getSelectedFoodNames() {
            List<String> names = new ArrayList<>();
            for (Food food : foodList) {
                if (food.getSelectedQuantity() > 0) {
                    names.add(food.getName() + " x" + food.getSelectedQuantity());
                    Log.d("DEBUG", "Food: " + food.getName());
                }
            }
            return String.join(", ", names);
        }

        private void loadFoodData() {
            // Initialize foodList
            foodList = new ArrayList<>();
            // Initialize the RecyclerView Adapter
            FoodOrderAdapter foodOrderAdapter = new FoodOrderAdapter(foodList); // Use your RecyclerView adapter
            // Set the Layout Manager
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

                }
            });
        }

    }