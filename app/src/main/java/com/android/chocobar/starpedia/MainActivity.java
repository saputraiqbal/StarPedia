package com.android.chocobar.starpedia;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.chocobar.starpedia.Adapter.PeopleAdapter;
import com.android.chocobar.starpedia.Model.ListPeople;
import com.android.chocobar.starpedia.Model.People;
import com.android.chocobar.starpedia.Rest.ApiClient;
import com.android.chocobar.starpedia.Rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton nextPageBtn, prevPageBtn;
    TextView tvPage;
    ApiInterface apiInterface;
    private RecyclerView rViewPeople;
    private RecyclerView.LayoutManager rViewManager;
    public List<People> listPeople = new ArrayList<>();
    private PeopleAdapter peopleAdapter;
    private String nextPage = "", prevPage = "";
    protected int page = 1;
    protected String[] splitPages = new String[]{};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nextPageBtn = findViewById(R.id.btn_nextPage);
        prevPageBtn = findViewById(R.id.btn_prevPage);
        tvPage = findViewById(R.id.tv_page);
        
        rViewPeople = findViewById(R.id.rv_people);
        rViewManager = new LinearLayoutManager(this);
        rViewPeople.setLayoutManager(rViewManager);
        checkConnection(this);
    }

    private void checkConnection(Context ctx) {
        ConnectivityManager conManager = (ConnectivityManager)ctx.getSystemService(CONNECTIVITY_SERVICE);
        if (conManager != null){
            NetworkInfo netInfo = conManager.getActiveNetworkInfo();
            if((netInfo != null) && (netInfo.isConnected())){
                loadData();
            }else{

            }
        }
    }

    private void loadData() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ListPeople> callListPeople = apiInterface.getListPeople(page);
        callListPeople.enqueue(new Callback<ListPeople>() {
            @Override
            public void onResponse(Call<ListPeople> call, Response<ListPeople> response) {
                listPeople.addAll(response.body().getResults());
                nextPage = response.body().getNext();
                prevPage = response.body().getPrevious();
                updateUI();
            }

            @Override
            public void onFailure(Call<ListPeople> call, Throwable t) {
                AlertDialog.Builder error = new AlertDialog.Builder(MainActivity.this);
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
        tvPage.setText(getResources().getString(R.string.pages, page));
        checkPageButton(nextPage, nextPageBtn);
        checkPageButton(prevPage, prevPageBtn);
        rViewPeople.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.HORIZONTAL));
        peopleAdapter = new PeopleAdapter(listPeople);
        rViewPeople.setAdapter(peopleAdapter);
        peopleAdapter.notifyDataSetChanged();
        nextPageBtn.setOnClickListener(this);
        prevPageBtn.setOnClickListener(this);
    }

    private void checkPageButton(String pages, ImageButton imgBtn){
        if (pages == null)
            imgBtn.setEnabled(false);
        else
            imgBtn.setEnabled(true);
    }

    private String[] splitUrl (String url){
        return url.split("[/=]");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_nextPage:
                splitPages = splitUrl(nextPage);
                page = Integer.parseInt(splitPages[6]);
                listPeople.clear();
                loadData();
                break;
            case R.id.btn_prevPage:
                splitPages = splitUrl(prevPage);
                page = Integer.parseInt(splitPages[6]);
                listPeople.clear();
                loadData();
                break;
        }
    }
}
