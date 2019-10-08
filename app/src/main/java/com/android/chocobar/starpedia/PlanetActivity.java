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
import android.view.MenuItem;
import android.widget.TextView;

import com.android.chocobar.starpedia.Model.People;
import com.android.chocobar.starpedia.Model.Planet;
import com.android.chocobar.starpedia.Rest.ApiClient;
import com.android.chocobar.starpedia.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanetActivity extends AppCompatActivity {

    TextView planet, rotation, orbital, diameter, climate, gravity, terrain, surfaceWater,
            population, residents, films;
    String planetData = "";
    ApiInterface apiInterface;
    protected List<Planet> planets = new ArrayList<>();
    protected List<String> arrFilms = new ArrayList<>();
    protected List<String> arrResidents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planet);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        planetData = intent.getData().toString();

        planet = findViewById(R.id.tv_planet_detail);
        rotation = findViewById(R.id.tv_rotation_planet);
        orbital = findViewById(R.id.tv_orbital_planet);
        diameter = findViewById(R.id.tv_diameter_planet);
        climate = findViewById(R.id.tv_climate_planet);
        gravity = findViewById(R.id.tv_gravity_planet);
        terrain = findViewById(R.id.tv_terrain_planet);
        surfaceWater = findViewById(R.id.tv_surface_water_planet);
        population = findViewById(R.id.tv_population_planet);
        residents = findViewById(R.id.tv_resident_planet);
        films = findViewById(R.id.tv_films_planet);

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

    private String[] splitUrl (String url){
        return url.split("/");
    }

    private void loadData() {
        String[] splitString = splitUrl(planetData);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Planet> getPlanetCall = apiInterface.getPlanet(splitString[5]);
        getPlanetCall.enqueue(new Callback<Planet>() {
            @Override
            public void onResponse(Call<Planet> call, Response<Planet> response) {
                planets.add(response.body());
                arrFilms.addAll(response.body().getFilms());
                arrResidents.addAll(response.body().getResidents());
                updateUI();
            }

            @Override
            public void onFailure(Call<Planet> call, Throwable t) {
                AlertDialog.Builder error = new AlertDialog.Builder(PlanetActivity.this);
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
        getSupportActionBar().setTitle(planets.get(0).getName());
        planet.setText(planets.get(0).getName());
        rotation.setText(planets.get(0).getRotationPeriod());
        orbital.setText(planets.get(0).getOrbitalPeriod());
        diameter.setText(planets.get(0).getDiameter());
        climate.setText(planets.get(0).getClimate());
        gravity.setText(planets.get(0).getGravity());
        terrain.setText(planets.get(0).getTerrain());
        surfaceWater.setText(planets.get(0).getSurfaceWater());
        population.setText(planets.get(0).getPopulation());
        showTextList(arrResidents, residents);
        showTextList(arrFilms, films);
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
                    case R.id.tv_resident_planet:
                        scheme = "chars:";
                        break;
                    case R.id.tv_films_planet:
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
