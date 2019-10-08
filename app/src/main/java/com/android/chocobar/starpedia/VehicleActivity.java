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

import com.android.chocobar.starpedia.Model.Vehicle;
import com.android.chocobar.starpedia.Rest.ApiClient;
import com.android.chocobar.starpedia.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleActivity extends AppCompatActivity {

    TextView vehicle, model, manufacturer, lengthVehicle, costInCredit, maxSpeed, crew, passenger,
            cargoCapacity, consumables, vehicleClass, pilots, films;
    String vehicleData = "";
    ApiInterface apiInterface;
    protected List<Vehicle> vehicles = new ArrayList<>();
    protected List<String> arrPilots = new ArrayList<>();
    protected List<String> arrFilms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        vehicleData = intent.getData().toString();

        vehicle = findViewById(R.id.tv_vehicle_detail);
        model = findViewById(R.id.tv_model_vehicle);
        lengthVehicle = findViewById(R.id.tv_length_vehicle);
        manufacturer = findViewById(R.id.tv_manufacturer_vehicle);
        costInCredit = findViewById(R.id.tv_cost_in_credit_vehicle);
        maxSpeed = findViewById(R.id.tv_max_atmosphere_vehicle);
        crew = findViewById(R.id.tv_crew_vehicle);
        passenger = findViewById(R.id.tv_passengers_vehicle);
        cargoCapacity = findViewById(R.id.tv_cargo_capacity_vehicle);
        consumables = findViewById(R.id.tv_consumables_vehicle);
        vehicleClass = findViewById(R.id.tv_vehicle_class);
        pilots = findViewById(R.id.tv_pilots_vehicle);
        films = findViewById(R.id.tv_films_vehicle);

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
        String[] splitString = splitUrl(vehicleData);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Vehicle> getVehicleCall = apiInterface.getVehicle(splitString[5]);
        getVehicleCall.enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, Response<Vehicle> response) {
                vehicles.add(response.body());
                arrPilots.addAll(response.body().getPilots());
                arrFilms.addAll(response.body().getFilms());
                updateUI();
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                AlertDialog.Builder error = new AlertDialog.Builder(VehicleActivity.this);
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
        getSupportActionBar().setTitle(vehicles.get(0).getName());
        vehicle.setText(vehicles.get(0).getName());
        model.setText(vehicles.get(0).getModel());
        manufacturer.setText(vehicles.get(0).getManufacturer());
        lengthVehicle.setText(vehicles.get(0).getLength());
        costInCredit.setText(vehicles.get(0).getCostInCredits());
        maxSpeed.setText(vehicles.get(0).getMaxAtmospheringSpeed());
        crew.setText(vehicles.get(0).getCrew());
        passenger.setText(vehicles.get(0).getPassengers());
        cargoCapacity.setText(vehicles.get(0).getCargoCapacity());
        consumables.setText(vehicles.get(0).getConsumables());
        vehicleClass.setText(vehicles.get(0).getVehicleClass());
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
                    case R.id.tv_pilots_vehicle:
                        scheme = "chars:";
                        break;
                    case R.id.tv_films_vehicle:
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
