package com.example.android_movie.ui.movie;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.MainActivity;
import com.example.android_movie.R;
import com.example.android_movie.ui.role.Role;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Movie> movies = new ArrayList<Movie>();

    public MovieRecyclerViewAdapter(Context context){
        this.context = context;
    }

    public void setMovies(ArrayList<Movie> movies){
        this.movies = movies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_movie_item,parent,false);
        return new MovieRecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textTitle.setText(movies.get(position).getTitle());
        holder.textMDesc.setText(movies.get(position).getDescription());
        holder.textRelease.setText(movies.get(position).getRelease());
        holder.textGenName.setText(movies.get(position).getGenre());
        holder.textProName.setText(movies.get(position).getProducer());
        if(movies.get(position).getImgpath() != ""){
            Picasso.get().load("http://192.168.1.15:8000/" + movies.get(position).getImgpath()).into(holder.imgMovie);
        }

        holder.btnEditMovie.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieFormActivity.class);
            intent.putExtra("title", movies.get(position).getTitle());
            intent.putExtra("description", movies.get(position).getDescription());
            intent.putExtra("release", movies.get(position).getRelease());
            intent.putExtra("id", movies.get(position).getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
        });

        holder.btnDelMovie.setOnClickListener(v -> deleteMovie(movies.get(position).getId(), position));

        holder.cardMovie.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity) context);
            View view = LayoutInflater.from(context).inflate(R.layout.activity_movie_show,null);
            TextView txtShowTitle = view.findViewById(R.id.txtShowTitle);
            TextView txtShowDesc = view.findViewById(R.id.txtShowDesc);
            TextView txtShowProd = view.findViewById(R.id.txtShowProd);
            TextView txtShowRelease = view.findViewById(R.id.txtShowRelease);
            TextView txtShowGenre = view.findViewById(R.id.txtShowGenre);
            TextView txtCast = view.findViewById(R.id.txtCast);
            ImageView imgShowMovie = view.findViewById(R.id.imgShowMovie);

            txtShowTitle.setText(movies.get(position).getTitle());
            txtShowDesc.setText(movies.get(position).getDescription());
            txtShowProd.setText(movies.get(position).getProducer());
            txtShowRelease.setText(movies.get(position).getRelease());
            txtShowGenre.setText(movies.get(position).getGenre());
            Picasso.get().load("http://192.168.1.15:8000/" + movies.get(position).getImgpath()).into(imgShowMovie);

            txtCast.setText(movies.get(position).getRoles());

            builder.setView(view);
            builder.setNegativeButton("back", (dialog, which) -> dialog.dismiss());

            builder.create().show();
            Toast.makeText(context, "Show", Toast.LENGTH_SHORT).show();
        });

        }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    private void deleteMovie(String id, int position)   {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.URLProject) + "movie/" + id,
                null,
                response -> {
                    try {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        movies.remove(position);
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e("VolleyError", error.toString());
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textTitle, textMDesc, textRelease, textGenName, textProName;
        ImageView imgMovie;
        CardView cardMovie;
        ImageButton btnEditMovie, btnDelMovie;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            imgMovie = itemView.findViewById(R.id.imgMovie);
            textTitle = itemView.findViewById(R.id.txtTitle);
            textMDesc = itemView.findViewById(R.id.txtMDesc);
            textRelease = itemView.findViewById(R.id.txtRelease);
            textProName = itemView.findViewById(R.id.txtProName);
            textGenName = itemView.findViewById(R.id.txtGenName);
            cardMovie = itemView.findViewById(R.id.cardMovie);
            btnEditMovie = itemView.findViewById(R.id.btnEditMovie);
            btnDelMovie = itemView.findViewById(R.id.btnDelMovie);
        }
    }
}