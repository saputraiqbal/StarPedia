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
import com.android.chocobar.starpedia.Rest.ApiClient;
import com.android.chocobar.starpedia.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeopleActivity extends AppCompatActivity {

    TextView name, height, mass, hairColor, skinColor, eyeColor, birthYear, gender,
            homeworld, films, species, vehicles, starship;
    String peopleData="";
    ApiInterface apiInterface;
    protected List<People> people = new ArrayList<>();
    protected List<String> listFilms = new ArrayList<>();
    protected List<String> listSpecies = new ArrayList<>();
    protected List<String> listVehicles = new ArrayList<>();
    protected List<String> listStarship = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        if(intent.getStringExtra("_PEOPLE") == null)
            peopleData = intent.getData().toString();
        else
            peopleData = intent.getStringExtra("_PEOPLE");

        name = findViewById(R.id.tv_people_detail);
        height = findViewById(R.id.tv_height);
        mass = findViewById(R.id.tv_mass);
        hairColor = findViewById(R.id.tv_hair_color_people);
        skinColor = findViewById(R.id.tv_skin_color_people);
        eyeColor = findViewById(R.id.tv_eye_color_people);
        birthYear = findViewById(R.id.tv_birth_year_people);
        gender = findViewById(R.id.tv_gender_people);
        homeworld = findViewById(R.id.tv_homeworld_people);
        films = findViewById(R.id.tv_films_people);
        species = findViewById(R.id.tv_species_people);
        vehicles = findViewById(R.id.tv_vehicles_people);
        starship = findViewById(R.id.tv_starship_people);

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
        String[] splitString = splitUrl(peopleData);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<People> getPeopleCall = apiInterface.getPeople(splitString[5]);
        getPeopleCall.enqueue(new Callback<People>() {
            @Override
            public void onResponse(Call<People> call, Response<People> response) {
                people.add(response.body());
                listFilms.addAll(response.body().getFilms());
                listSpecies.addAll(response.body().getSpecies());
                listVehicles.addAll(response.body().getVehicles());
                listStarship.addAll(response.body().getStarships());
                updateUI();
            }

            @Override
            public void onFailure(Call<People> call, Throwable t) {
                AlertDialog.Builder error = new AlertDialog.Builder(PeopleActivity.this);
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
        getSupportActionBar().setTitle(people.get(0).getName());
        name.setText(people.get(0).getName());
        height.setText("Height : " + people.get(0).getHeight());
        mass.setText("Mass : " + people.get(0).getMass());
        hairColor.setText(people.get(0).getHairColor());
        skinColor.setText(people.get(0).getSkinColor());
        eyeColor.setText(people.get(0).getEyeColor());
        birthYear.setText(people.get(0).getBirthYear());
        gender.setText(people.get(0).getGender());
        homeworld.setText(people.get(0).getHomeworld());
        String links = people.get(0).getHomeworld();
        homeworld.setMovementMethod(LinkMovementMethod.getInstance());
        Pattern pattern = Pattern.compile(links);
        Linkify.addLinks(homeworld, pattern, "planets:");
        showTextList(listFilms, films);
        showTextList(listSpecies, species);
        showTextList(listVehicles, vehicles);
        showTextList(listStarship, starship);
    }

    private void showTextList(List<String> object, TextView textView){
        int i = 0;
        if(!object.isEmpty()){
            do{
                String links = object.get(i);
                textView.append(links);
                textView.setMovementMethod(LinkMovementMethod.getInstance());
                Pattern pattern = Pattern.compile(links);
                String scheme = "";
                switch (textView.getId()){
                    case R.id.tv_species_people:
                        scheme = "species:";
                        break;
                    case R.id.tv_films_people:
                        scheme = "films:";
                        break;
                    case R.id.tv_starship_people:
                        scheme = "starship:";
                        break;
                    case R.id.tv_vehicles_people:
                        scheme = "vehicle:";
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
