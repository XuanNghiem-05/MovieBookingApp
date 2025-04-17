package deso1.ngoxuannghiem.btl_adr_nc;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;
    private MovieFragment fragment;

    public MovieAdapter(List<Movie> movieList, Context context,MovieFragment fragment) {
        this.movieList = movieList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvName.setText(movie.name);
        holder.tvPrice.setText("Giá vé: " + movie.price + "đ");
        holder.tvStartDate.setText("Khởi chiếu: " + movie.startDate);

        Glide.with(context)
                .load(movie.imageUrl)  // imageUrl lấy từ Realtime Database
//                .placeholder(R.drawable.ic_placeholder_image) // ảnh tạm trong lúc load
//                .error(R.drawable.ic_error_image)             // ảnh nếu bị lỗi
                .into(holder.imgMovie);

        holder.btnEdit.setOnClickListener(v -> fragment.showAddOrEditDialog(movie));

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc muốn xoá phim này?")
                    .setPositiveButton("Xoá", (dialog, which) -> fragment.deleteMovie(movie))
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public void updateList(List<Movie> newList) {
        movieList = newList;
        notifyDataSetChanged();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvStartDate;
        Button btnEdit, btnDelete;
        ImageView imgMovie;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvName = itemView.findViewById(R.id.tvMovieName);
            tvPrice = itemView.findViewById(R.id.tvMoviePrice);
            tvStartDate = itemView.findViewById(R.id.tvMovieStartDate);
            btnEdit = itemView.findViewById(R.id.btnEditMovie);
            btnDelete = itemView.findViewById(R.id.btnDeleteMovie);

        }
    }
}