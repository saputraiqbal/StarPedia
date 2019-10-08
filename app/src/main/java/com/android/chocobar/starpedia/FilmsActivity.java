package com.android.chocobar.starpedia;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.text.HtmlCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.chocobar.starpedia.Model.Film;
import com.android.chocobar.starpedia.Rest.ApiClient;
import com.android.chocobar.starpedia.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilmsActivity extends AppCompatActivity {

    TextView film, director, producer, desc, episode, releaseDate, planet,
            starship, vehicle, character, species;
    String filmData = "";
    ApiInterface apiInterface;
    protected List<Film> films = new ArrayList<>();
    protected List<String> characters = new ArrayList<>();
    protected List<String> planets = new ArrayList<>();
    protected List<String> starships = new ArrayList<>();
    protected List<String> vehicles = new ArrayList<>();
    protected List<String> arrSpecies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_films);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        filmData = intent.getData().toString();

        film = findViewById(R.id.tv_films_detail);
        director = findViewById(R.id.tv_director_films);
        producer = findViewById(R.id.tv_producers_films);
        desc = findViewById(R.id.tv_desc_films);
        episode = findViewById(R.id.tv_episode_films);
        releaseDate = findViewById(R.id.tv_release_date_films);
        planet = findViewById(R.id.tv_planets_films);
        starship = findViewById(R.id.tv_starship_films);
        vehicle = findViewById(R.id.tv_vehicles_films);
        character = findViewById(R.id.tv_character_films);
        species = findViewById(R.id.tv_species_films);

        checkConnection(this);
    }

    private void checkConnection(Context ctx) {
        ConnectivityManager conManager = (ConnectivityManager)ctx.getSystemService(CONNECTIVITY_SERVICE);
        if (conManager != null){
            NetworkInfo netInfo = conManager.getActiveNetworkInfo();
            if ((netInfo != null) && (netInfo.isConnected())){
                loadData();
            }else{}
        }
    }

    private String[] splitUrl (String url){
        return url.split("/");
    }

    private void loadData() {
        String[] splitString = splitUrl(filmData);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Film> getFilmCall = apiInterface.getFilms(splitString[5]);
        getFilmCall.enqueue(new Callback<Film>() {
            @Override
            public void onResponse(Call<Film> call, Response<Film> response) {
                films.add(response.body());
                characters.addAll(response.body().getCharacters());
                arrSpecies.addAll(response.body().getSpecies());
                planets.addAll(response.body().getPlanets());
                starships.addAll(response.body().getStarships());
                vehicles.addAll(response.body().getVehicles());
                updateUI();
            }

            @Override
            public void onFailure(Call<Film> call, Throwable t) {
                AlertDialog.Builder error = new AlertDialog.Builder(FilmsActivity.this);
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
        getSupportActionBar().setTitle(films.get(0).getTitle());
        film.setText(films.get(0).getTitle());
        director.setText(String.format("%s%s", getString(R.string.directed_by), films.get(0).getDirector()));
        producer.setText(String.format("%s%s", getString(R.string.produced_by), films.get(0).getProducer()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            desc.setText(Html.fromHtml(films.get(0).getOpeningCrawl(), Html.FROM_HTML_MODE_COMPACT));
        else desc.setText(Html.fromHtml(films.get(0).getOpeningCrawl()));
        episode.setText(String.format(Locale.US, "%d", films.get(0).getEpisodeId()));
        releaseDate.setText(films.get(0).getReleaseDate());
        showTextList(characters, character);
        showTextList(arrSpecies, species);
        showTextList(planets, planet);
        showTextList(starships, starship);
        showTextList(vehicles, vehicle);
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
                    case R.id.tv_species_films:
                        scheme = "species:";
                        break;
                    case R.id.tv_character_films:
                        scheme = "chars:";
                        break;
                    case R.id.tv_starship_films:
                        scheme = "starship:";
                        break;
                    case R.id.tv_planets_films:
                        scheme = "planets:";
                        break;
                    case R.id.tv_vehicles_films:
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
