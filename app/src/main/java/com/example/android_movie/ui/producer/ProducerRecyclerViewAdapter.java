package com.example.android_movie.ui.producer;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.android_movie.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProducerRecyclerViewAdapter extends RecyclerView.Adapter<ProducerRecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Producer> producers = new ArrayList<Producer>();

    public ProducerRecyclerViewAdapter(Context context) {
        this.context = context;
    }

    public void setProducers(ArrayList<Producer> producers){
        this.producers = producers;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProducerRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_producer_item, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProducerRecyclerViewAdapter.ViewHolder holder, int position) {
        holder.textName.setText(producers.get(position).getName());
        holder.textEmail.setText(producers.get(position).getEmail());

        holder.btnEditProd.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProducerFormActivity.class);
            intent.putExtra("name", producers.get(position).getName());
            intent.putExtra("email", producers.get(position).getEmail());
            intent.putExtra("id", producers.get(position).getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Toast.makeText(context, "Edit", Toast.LENGTH_SHORT).show();
        });
        holder.btnDelProd.setOnClickListener(v -> deleteProducer(producers.get(position).getId(), position));
    }

    private void deleteProducer(String id, int position) {
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                context.getString(R.string.URLProject) + "producer/" + id,
                null,
                response -> {
                    try {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        producers.remove(position);
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
        return producers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textName, textEmail;
        ImageButton btnEditProd, btnDelProd;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textName = itemView.findViewById(R.id.txtProdName);
            textEmail = itemView.findViewById(R.id.txtEmail);
            btnEditProd = itemView.findViewById(R.id.btnEditProd);
            btnDelProd = itemView.findViewById(R.id.btnDelProd);
        }
    }




}