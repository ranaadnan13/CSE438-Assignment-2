package com.example.cse438movieappfall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    PopularMovies movieResponse;

    OkHttpClient client = new OkHttpClient();

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_main);

        RecyclerView rv = findViewById(R.id.movie_list_view);
        rv.setLayoutManager(new GridLayoutManager(this,2));
        //if recycler is horizontal
       // rv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));

        rv.setAdapter(new MyAdapter());

        try {
            String data = run("https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&page=1&api_key=3fa9058382669f72dcb18fb405b7a831&language=en-US");
            movieResponse = new  Gson().fromJson(data, PopularMovies.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    class MyAdapter extends RecyclerView.Adapter<MovieViewHolder> {

        @NonNull
        @Override
        public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.item, parent, false);

            return new MovieViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieViewHolder holder,int position) {
            holder.textView.setText(movieResponse.getResults().get(position).getTitle());
            holder.rating.setText(""+movieResponse.getResults().get(position).getVoteAverage());

            Glide.with(getApplicationContext())
                    .load("https://image.tmdb.org/t/p/w500"+movieResponse.getResults().get(position).getPosterPath())
                    .centerCrop()
                    .into(holder.flagImg);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(MainActivity.this, Activity.class);
                    int k = holder.getAdapterPosition();
                    i.putExtra("id", String.valueOf(movieResponse.getResults().get(k).getId()));
                    i.putExtra("title", movieResponse.getResults().get(k).getTitle());
                    i.putExtra("ov", movieResponse.getResults().get(k).getOverview());
                    i.putExtra("posterPath", movieResponse.getResults().get(k).getPosterPath());
                    i.putExtra("backdropPath", movieResponse.getResults().get(k).getBackdropPath());
                    i.putExtra("rating", String.valueOf(movieResponse.getResults().get(k).getVoteAverage()));
                    i.putExtra("releaseDate", movieResponse.getResults().get(k).getReleaseDate());

                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return movieResponse.getResults().size();
        }
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageView flagImg;
        TextView rating;
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
            flagImg = itemView.findViewById(R.id.poster);
            rating = itemView.findViewById(R.id.rating);
        }
    }
}