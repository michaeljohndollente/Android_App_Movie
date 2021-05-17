package com.example.android_movie.ui.genre;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenreRecyclerViewAdapter extends RecyclerView.Adapter<GenreRecyclerViewAdapter.ViewHolder>{

    Context context;
    ArrayList<Genre> genres = new ArrayList<Genre>();

    public GenreRecyclerViewAdapter(Context context){
        this.context = context;
    }

    public void setGenres(ArrayList<Genre> genres){
        this.genres = genres;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenreRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_genre_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textName.setText(genres.get(position).getName());
        
        holder.btnEditGenre.setOnClickListener(v -> {
            Intent intent = new Intent(context, GenreFormActivity.class);
            intent.putExtra("id", genres.get(position).getId());
            intent.putExtra("name", genres.get(position).getName());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
        });

        holder.btnDelGenre.setOnClickListener(v -> deleteGenre(genres.get(position).getId(), position));
    }

    private void deleteGenre(String id, int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.URLProject) + "genre/" + id,
                null,
                response -> {
                    try {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        genres.remove(position);
                        notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("volleyError", error.toString()))
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

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textName;
        ImageButton btnEditGenre, btnDelGenre;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textName = itemView.findViewById(R.id.txtGName);
            btnEditGenre = itemView.findViewById(R.id.btnEditGenre);
            btnDelGenre = itemView.findViewById(R.id.btnDelGenre);

        }
    }
}
