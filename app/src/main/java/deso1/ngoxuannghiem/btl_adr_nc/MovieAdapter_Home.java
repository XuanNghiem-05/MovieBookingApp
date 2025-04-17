package deso1.ngoxuannghiem.btl_adr_nc;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter_Home extends RecyclerView.Adapter<MovieAdapter_Home.MovieViewHolder> {

    private List<Movie> movieList;
    private Context context;

    public MovieAdapter_Home(List<Movie> movieList, Context context) {
        this.movieList = movieList;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_home, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvMovieName.setText(movie.name);
        Glide.with(context).load(movie.imageUrl).into(holder.imgMovie); // Cần thêm Glide vào build.gradle

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("name", movie.getName());
            intent.putExtra("category", movie.getCategory());
            intent.putExtra("price", movie.getPrice());
            intent.putExtra("description", movie.getDescription());
            intent.putExtra("id", movie.getId());
            intent.putExtra("startDate", movie.getStartDate());
            intent.putExtra("description", movie.getDescription());
            intent.putExtra("imageUrl", movie.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgMovie;
        TextView tvMovieName;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
        }
    }
}