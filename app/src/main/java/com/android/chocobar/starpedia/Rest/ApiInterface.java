package com.android.chocobar.starpedia.Rest;

import com.android.chocobar.starpedia.Model.Film;
import com.android.chocobar.starpedia.Model.ListPeople;
import com.android.chocobar.starpedia.Model.People;
import com.android.chocobar.starpedia.Model.Planet;
import com.android.chocobar.starpedia.Model.Species;
import com.android.chocobar.starpedia.Model.Starship;
import com.android.chocobar.starpedia.Model.Vehicle;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("people")
    Call<ListPeople> getListPeople(@Query("page") int page);

    @GET("people/{id}")
    Call<People> getPeople(@Path("id") String id);

    @GET("films/{id}")
    Call<Film> getFilms(@Path("id") String id);

    @GET("species/{id}")
    Call<Species> getSpecies(@Path("id") String id);

    @GET("starships/{id}")
    Call<Starship> getStarship(@Path("id") String id);

    @GET("vehicles/{id}")
    Call<Vehicle> getVehicle(@Path("id") String id);

    @GET("planets/{id}")
    Call<Planet> getPlanet(@Path("id") String id);
}
