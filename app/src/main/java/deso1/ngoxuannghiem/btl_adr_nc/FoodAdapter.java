package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private List<Food> foodList;
    private Context context;
    private FoodFragment fragment; // Thêm fragment để gọi hàm showAddOrEditDialog và deleteFood

    public FoodAdapter(List<Food> foodList, Context context, FoodFragment fragment) {
        this.foodList = foodList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);
        holder.tvName.setText(food.name);
        holder.tvPrice.setText("Giá: " + food.price + "đ");
        holder.tvQuantity.setText("Số lượng: " + food.quantity);

        holder.btnEdit.setOnClickListener(v -> fragment.showAddOrEditDialog(food));

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc muốn xoá món này?")
                    .setPositiveButton("Xoá", (dialog, which) -> fragment.deleteFood(food))
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateList(List<Food> newList) {
        this.foodList = newList;
        notifyDataSetChanged();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        Button btnEdit, btnDelete;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvFoodPrice);
            tvQuantity = itemView.findViewById(R.id.tvFoodQuantity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}