package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Recipe_Activity extends AppCompatActivity {

    private TextView title, ready_in, servings, healthy, instructions;
    private ImageView img, vegeterian;
    private JSONArray ingredientsArr;
    private List<Ingredient> ingredientsLst = new ArrayList<Ingredient>();
    private RecyclerView myrv;
    private boolean like = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_);
        final Intent intent = getIntent();
        final String recipeId = Objects.requireNonNull(intent.getExtras()).getString("id");
        img = findViewById(R.id.recipe_img);
        title = findViewById(R.id.recipe_title);
        ready_in = findViewById(R.id.recipe_ready_in);
        servings = findViewById(R.id.recipe_servings);
        healthy = findViewById(R.id.recipe_healthy);
        instructions = findViewById(R.id.recipe_instructions);
        getRecipeData(recipeId);

        myrv = findViewById(R.id.recipe_ingredients_rv);
        myrv.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void getRecipeData(final String recipeId) {
        String URL = " https://api.spoonacular.com/recipes/" + recipeId + "/information?apiKey=0d86a50de7664bb8b6921e39d8d450cf";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            try {
                                Picasso.get().load((String) response.get("image")).into(img);
                            }
                            catch (Exception e){
                               // img.setImageResource(R.drawable.nopicture);
                            }
                            title.setText((String) response.get("title"));
                            ready_in.setText(Integer.toString((Integer) response.get("readyInMinutes")));
                            servings.setText(Integer.toString((Integer) response.get("servings")));
                            if ((boolean) response.get("veryHealthy")) {
                                healthy.setText("Healthy");
                            }
                            try{
                                if(response.get("instructions").equals("")){
                                    throw new Exception("No Instructions");
                                }
                                else
                                    instructions.setText(Html.fromHtml((String) response.get("instructions")));
                            }
                            catch(Exception e){
                                String msg= "Recipe Not Found, to view the original recipe click on the link below:" + "<a href="+response.get("spoonacularSourceUrl")+">"+response.get("spoonacularSourceUrl")+"</a>";
                                instructions.setMovementMethod(LinkMovementMethod.getInstance());
                                instructions.setText(Html.fromHtml(msg));
                            }
                            ingredientsArr = (JSONArray) response.get("extendedIngredients");
                            for (int i = 0; i < ingredientsArr.length(); i++) {
                                JSONObject jsonObject1;
                                jsonObject1 = ingredientsArr.getJSONObject(i);
                                ingredientsLst.add(new Ingredient(jsonObject1.optString("originalString"), jsonObject1.optString("image")));
                            }
                            RecyclerViewAdapterRecipeIngredient myAdapter = new RecyclerViewAdapterRecipeIngredient(getApplicationContext(), ingredientsLst);
                            myrv.setAdapter(myAdapter);
                            myrv.setItemAnimator(new DefaultItemAnimator());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("the res is error:", error.toString());
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}