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
import android.webkit.WebView;
import android.widget.TextView;

import com.android.chocobar.starpedia.Model.People;
import com.android.chocobar.starpedia.Model.Starship;
import com.android.chocobar.starpedia.Rest.ApiClient;
import com.android.chocobar.starpedia.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StarshipActivity extends AppCompatActivity {

    TextView starship, model, lengthStarship, manufacturer, costInCredit, maxSpeed, crew, passenger,
            cargoCapacity, consumables, hyperdrive, mglt, starshipClass, pilots, films;
    String starshipData = "";
    ApiInterface apiInterface;
    protected List<Starship> starships = new ArrayList<>();
    protected List<String> arrPilots = new ArrayList<>();
    protected List<String> arrFilms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starship);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        starshipData = intent.getData().toString();

        starship = findViewById(R.id.tv_starship_detail);
        model = findViewById(R.id.tv_model_starship);
        lengthStarship = findViewById(R.id.tv_length_starship);
        manufacturer = findViewById(R.id.tv_manufacturer_starship);
        costInCredit = findViewById(R.id.tv_cost_in_credit_starship);
        maxSpeed = findViewById(R.id.tv_max_atmosphere_starship);
        crew = findViewById(R.id.tv_crew_starship);
        passenger = findViewById(R.id.tv_passengers_starship);
        cargoCapacity = findViewById(R.id.tv_cargo_capacity_starship);
        consumables = findViewById(R.id.tv_consumables_starship);
        hyperdrive = findViewById(R.id.tv_hyperdrive_rating_starship);
        mglt = findViewById(R.id.tv_mglt_starship);
        starshipClass = findViewById(R.id.tv_starship_class);
        pilots = findViewById(R.id.tv_pilots_starship);
        films = findViewById(R.id.tv_films_starship);

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
        String[] splitString = splitUrl(starshipData);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Starship> getStarshipCall = apiInterface.getStarship(splitString[5]);
        getStarshipCall.enqueue(new Callback<Starship>() {
            @Override
            public void onResponse(Call<Starship> call, Response<Starship> response) {
                starships.add(response.body());
                arrPilots.addAll(response.body().getPilots());
                arrFilms.addAll(response.body().getFilms());
                updateUI();
            }

            @Override
            public void onFailure(Call<Starship> call, Throwable t) {
                AlertDialog.Builder error = new AlertDialog.Builder(StarshipActivity.this);
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
        getSupportActionBar().setTitle(starships.get(0).getName());
        starship.setText(starships.get(0).getName());
        manufacturer.setText(starships.get(0).getManufacturer());
        lengthStarship.setText(starships.get(0).getLength());
        model.setText(starships.get(0).getModel());
        costInCredit.setText(starships.get(0).getCostInCredits());
        maxSpeed.setText(starships.get(0).getMaxAtmospheringSpeed());
        crew.setText(starships.get(0).getCrew());
        passenger.setText(starships.get(0).getPassengers());
        cargoCapacity.setText(starships.get(0).getCargoCapacity());
        consumables.setText(starships.get(0).getConsumables());
        hyperdrive.setText(starships.get(0).getHyperdriveRating());
        mglt.setText(starships.get(0).getMGLT());
        starshipClass.setText(starships.get(0).getStarshipClass());
        showTextList(arrPilots, pilots);
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
                    case R.id.tv_pilots_starship:
                        scheme = "chars:";
                        break;
                    case R.id.tv_films_starship:
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
