package com.example.android_movie.ui.actor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.android_movie.ui.SessionClass;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ActorFormActivity extends AppCompatActivity {

    ImageView imgActorSelect;
    EditText tboxFirstname, tboxLastname, tboxNote;
    Button btnSaveActor, btnSelectImg;
    String id, firstname, lastname, note, imgpath;
    private static final int REQUEST_IMAGE = 2;
    Bitmap img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_form);

        imgActorSelect = findViewById(R.id.imgActorSelect);
        tboxFirstname = findViewById(R.id.tboxFirstname);
        tboxLastname = findViewById(R.id.tboxLastname);
        tboxNote = findViewById(R.id.tboxNote);
        btnSaveActor = findViewById(R.id.btnSaveActor);
        btnSelectImg = findViewById(R.id.btnSelectImg);

        if(getIntent().hasExtra("id")) {
            firstname = getIntent().getStringExtra("firstname");
            lastname = getIntent().getStringExtra("lastname");
            note = getIntent().getStringExtra("note");
            id = getIntent().getStringExtra("id");
            tboxFirstname.setText(firstname);
            tboxLastname.setText(lastname);
            tboxNote.setText(note);
        }
        btnSaveActor.setOnClickListener(v -> storeActorData());

        btnSelectImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"imgpath"),REQUEST_IMAGE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri imageActors = data.getData();
            try{
                img = MediaStore.Images.Media.getBitmap(getContentResolver(),imageActors);
                Picasso.get().load(imageActors).into(imgActorSelect);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SessionClass.FRAGMENT = new ActorFragment();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    private String imgToString(Bitmap imgs){
        ByteArrayOutputStream arrayActors = new ByteArrayOutputStream();
        imgs.compress(Bitmap.CompressFormat.JPEG,100,arrayActors);
        byte[] byteActors = arrayActors.toByteArray();
        String imgpath = Base64.encodeToString(byteActors, Base64.DEFAULT);

        return imgpath;
    }

    private void storeActorData() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imgpath", imgToString(img));
            jsonObject.put("firstname", tboxFirstname.getText().toString());
            jsonObject.put("lastname", tboxLastname.getText().toString());
            jsonObject.put("note", tboxNote.getText().toString());
        }catch (JSONException e){
            e.printStackTrace();
        }

        String url = getString(R.string.URLProject) + "actor";
        if(id != null) {
            url += "/" + id;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                (id != null) ? Request.Method.PUT:Request.Method.POST,
                url,
                jsonObject,
                response -> {
                    try {
                        Toast.makeText(ActorFormActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        SessionClass.FRAGMENT = new ActorFragment();
                        Intent intent = new Intent(ActorFormActivity.this, ActorFragment.class);
                        startActivity(intent);
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
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                        "loginCredential", Context.MODE_PRIVATE
                );
                headers.put("Authorization", "Bearer "+ sharedPreferences.getString("access_token", null));
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }
}
