package com.example.android_movie.ui.actor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.MainActivity;
import com.example.android_movie.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ActorRecyclerViewAdapter extends RecyclerView.Adapter<ActorRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Actor> actors = new ArrayList<Actor>();

    public ActorRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setActors(ArrayList<Actor> actors) {
        this.actors = actors;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ActorRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_actor_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorRecyclerViewAdapter.ViewHolder holder, int position) {
        if(actors.get(position).getImgpath() != ""){
            Picasso.get().load("http://192.168.1.15:8000/" + actors.get(position).getImgpath()).into(holder.imgActor);
        }
        holder.textFirstname.setText(actors.get(position).getFirstname());
        holder.textLastname.setText(actors.get(position).getLastname());
        holder.textNote.setText(actors.get(position).getNote());

        holder.btnEditActor.setOnClickListener(v -> {
            Intent intent = new Intent(context, ActorFormActivity.class);
            intent.putExtra("imgpath", actors.get(position).getImgpath());
            intent.putExtra("firstname", actors.get(position).getFirstname());
            intent.putExtra("lastname", actors.get(position).getLastname());
            intent.putExtra("note", actors.get(position).getNote());
            intent.putExtra("id", actors.get(position).getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
        });

        holder.btnDelActor.setOnClickListener(v -> deleteActor(actors.get(position).getId(), position));

        holder.cardActor.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity) context);
            View view = LayoutInflater.from(context).inflate(R.layout.activity_actor_view,null);
            TextView txtShowName = view.findViewById(R.id.txtShowName);
            TextView txtShowNote = view.findViewById(R.id.txtShowNote);
            TextView txtResume = view.findViewById(R.id.txtResume);

            ImageView imgShowActor = view.findViewById(R.id.imgShowActor);


            txtShowName.setText(actors.get(position).getFirstname() + " " + actors.get(position).getLastname());
            txtShowNote.setText(actors.get(position).getNote());
            txtResume.setText(actors.get(position).getRole());

            Picasso.get().load("http://192.168.1.15:8000/" + actors.get(position).getImgpath()).into(imgShowActor);
            builder.setView(view);

            builder.setNegativeButton("back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            Toast.makeText(context, "Show", Toast.LENGTH_SHORT).show();
        });

    }

    private void deleteActor(String id, int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.URLProject) + "actor/" + id,
                null,
                response -> {
                    try {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        actors.remove(position);
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

    @Override
    public int getItemCount() {
        return actors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textFirstname, textLastname, textNote, textResume;
        ImageButton btnEditActor, btnDelActor;
        ImageView imgActor;
        CardView cardActor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgActor = itemView.findViewById(R.id.imgActor);
            textFirstname = itemView.findViewById(R.id.txtFirstname);
            textLastname = itemView.findViewById(R.id.txtLastname);
            textNote = itemView.findViewById(R.id.txtNote);
            textResume = itemView.findViewById(R.id.txtResume);
            btnEditActor = itemView.findViewById(R.id.btnEditActor);
            btnDelActor = itemView.findViewById(R.id.btnDelActor);
            cardActor = itemView.findViewById(R.id.cardActor);
        }
    }

}

