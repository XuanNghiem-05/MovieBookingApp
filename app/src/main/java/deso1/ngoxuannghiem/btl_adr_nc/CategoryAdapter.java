package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private List<Category> originalList;
    private Context context;
    private DatabaseReference categoryRef;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.originalList = new ArrayList<>(categoryList);
        this.context = context;
        categoryRef = FirebaseDatabase.getInstance().getReference("Categories");
    }

    public void setOriginalList(List<Category> newList) {
        originalList = new ArrayList<>(newList);
        categoryList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        categoryList.clear();
        if (query.isEmpty()) {
            categoryList.addAll(originalList);
        } else {
            for (Category cat : originalList) {
                if (cat.name.toLowerCase().contains(query.toLowerCase())) {
                    categoryList.add(cat);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.tvName.setText(category.name);

        // Load hình ảnh thể loại (nếu dùng Glide)
        //Glide.with(context).load(category.imageUrl).into(holder.img);
        Glide.with(context)
                .load(category.imageUrl)  // imageUrl lấy từ Realtime Database
//                .placeholder(R.drawable.ic_placeholder_image) // ảnh tạm trong lúc load
//                .error(R.drawable.ic_error_image)             // ảnh nếu bị lỗi
                .into(holder.img);

        // Nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            categoryRef.child(String.valueOf(category.id)).removeValue()
                    .addOnSuccessListener(unused -> Toast.makeText(context, "Đã xóa thể loại", Toast.LENGTH_SHORT).show());
        });

        // Nút sửa
        holder.btnEdit.setOnClickListener(v -> showEditDialog(category));
    }

    private void showEditDialog(Category category) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sửa thể loại");

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_category, null);
        EditText edtName = view.findViewById(R.id.edtCategoryName);
        EditText edtImage = view.findViewById(R.id.edtCategoryImage);
        edtName.setText(category.name);
        edtImage.setText(category.imageUrl);

        builder.setView(view);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newName = edtName.getText().toString().trim();
            String newImage = edtImage.getText().toString().trim();
            if (!newName.isEmpty() && !newImage.isEmpty()) {
                Category updated = new Category(category.id, newName, newImage);
                categoryRef.child(String.valueOf(updated.id)).setValue(updated)
                        .addOnSuccessListener(unused -> Toast.makeText(context, "Đã cập nhật", Toast.LENGTH_SHORT).show());
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;
        Button btnEdit, btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgCategory);
            tvName = itemView.findViewById(R.id.tvCategoryName);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
