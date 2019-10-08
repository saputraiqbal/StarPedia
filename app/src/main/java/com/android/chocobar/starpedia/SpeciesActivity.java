package com.android.chocobar.starpedia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.chocobar.starpedia.Model.People;
import com.android.chocobar.starpedia.Model.Species;
import com.android.chocobar.starpedia.Rest.ApiClient;
import com.android.chocobar.starpedia.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SpeciesActivity extends AppCompatActivity {

    TextView species, specs, hair, skin, eye, design, avgHeight, avgLifespan, home, people, film;
    String speciesData = "";
    ApiInterface apiInterface;
    protected List<Species> arrSpecies = new ArrayList<>();
    protected List<String> arrPeople = new ArrayList<>();
    protected List<String> arrFilms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        speciesData = intent.getData().toString();

        species = findViewById(R.id.tv_species_detail);
        specs = findViewById(R.id.tv_specs_species);
        hair = findViewById(R.id.tv_hair_color_species);
        skin = findViewById(R.id.tv_skin_color_species);
        eye = findViewById(R.id.tv_eye_color_species);
        design = findViewById(R.id.tv_designation_species);
        avgHeight = findViewById(R.id.tv_avg_height_species);
        avgLifespan = findViewById(R.id.tv_avg_lifespan_species);
        home = findViewById(R.id.tv_homeworld_species);
        people = findViewById(R.id.tv_people_species);
        film = findViewById(R.id.tv_films_species);

        checkConnection(this);
    }

    private void checkConnection(Context ctx) {
        ConnectivityManager conManager = (ConnectivityManager)ctx.getSystemService(CONNECTIVITY_SERVICE);
        if (conManager != null){
            NetworkInfo netInfo = conManager.getActiveNetworkInfo();
            if((netInfo != null) && (netInfo.isConnected())){
                loadData();
            }else{}
        }
    }

    private String[] splitUrl(String url){
        return url.split("/");
    }

    private void loadData() {
        String[] splitString = splitUrl(speciesData);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Species> getSpeciesCall = apiInterface.getSpecies(splitString[5]);
        getSpeciesCall.enqueue(new Callback<Species>() {
            @Override
            public void onResponse(Call<Species> call, Response<Species> response) {
                arrSpecies.add(response.body());
                arrPeople.addAll(response.body().getPeople());
                arrFilms.addAll(response.body().getFilms());
                updateUI();
            }

            @Override
            public void onFailure(Call<Species> call, Throwable t) {
                AlertDialog.Builder error = new AlertDialog.Builder(SpeciesActivity.this);
                error.setMessage(R.string.ask_back).setPositiveButton(getString(R.string.back_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
            }
        });
    }

    private void updateUI() {
        getSupportActionBar().setTitle(arrSpecies.get(0).getName());
        species.setText(arrSpecies.get(0).getName());
        specs.setText(arrSpecies.get(0).getClassification());
        hair.setText(arrSpecies.get(0).getHairColors());
        skin.setText(arrSpecies.get(0).getSkinColors());
        eye.setText(arrSpecies.get(0).getEyeColors());
        design.setText(arrSpecies.get(0).getDesignation());
        avgHeight.setText(arrSpecies.get(0).getAverageHeight());
        avgLifespan.setText(arrSpecies.get(0).getAverageLifespan());
        home.setText(arrSpecies.get(0).getHomeworld());
        String links = arrSpecies.get(0).getHomeworld();
        home.setMovementMethod(LinkMovementMethod.getInstance());
        Pattern pattern = Pattern.compile(links);
        Linkify.addLinks(home, pattern, "planets:");
        showTextList(arrPeople, people);
        showTextList(arrFilms, film);
    }

    private void showTextList(List<String> object, TextView textView){
        int i = 0;
        String links = "";
        String scheme = "";
        if(!object.isEmpty()){
            do{
                links = object.get(i);
                textView.append(links);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                Pattern pattern = Pattern.compile(links);
                switch (textView.getId()){
                    case R.id.tv_people_species:
                        scheme = "chars:";
                        break;
                    case R.id.tv_films_species:
                        scheme = "films:";
                        break;
                }
                Linkify.addLinks(textView, pattern, scheme);
                i++;
                if (i!= object.size()) {
                    textView.append("\n");
                }
            }while(i < object.size());
        }else textView.setText("n/a");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }
}
