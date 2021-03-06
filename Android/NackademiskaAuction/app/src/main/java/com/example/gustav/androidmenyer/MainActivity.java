package com.example.gustav.androidmenyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String AUCTION = "AUCTION";
    private ArrayList<com.example.gustav.androidmenyer.Auction> auctions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.category_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);*/

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest("http://nackademiska-api.azurewebsites.net/api/auction",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject auction = (JSONObject) response.get(i);
                                auctions.add(new Auction(auction.getString("name"),
                                        auction.getDouble("buyNowPrice"),
                                        auction.getString("imageUrl"), auction.getString("description"),
                                        auction.getString("startTime"), auction.getString("endTime"),
                                        auction.getString("categoryId"), auction.getString("supplierId"),
                                        auction.getString("id")));
                            }
                            for (int i = 0; i < auctions.size(); i++) {
                                getBids(requestQueue, auctions.get(i).getId());
                            }
                            setupAuctionList();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print("Failed response on auction");
            }
        });
        requestQueue.add(request);

    }

    private void setupAuctionList() {


        AuctionListAdapter auctionAdapter = new AuctionListAdapter(this, R.layout.auction_list_item, auctions);
        ListView auctionListView = (ListView) findViewById(R.id.auctionListView);
        auctionListView.setAdapter(auctionAdapter);


        auctionListView.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra(AUCTION, auctions.get(position));
                startActivity(intent);
            }
        });
    }

    public void getBids(RequestQueue requestQueue, final String ID) {

        JsonArrayRequest requestBid = new JsonArrayRequest("http://nackademiska-api.azurewebsites.net/api/bid/" + ID,
                new Response.Listener<JSONArray>() {

                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject bid = (JSONObject)response.get(response.length()-1);
                            for (int i = 0; i < auctions.size(); i++) {
                                if (auctions.get(i).getId() == ID) {
                                    auctions.get(i).setHighestBid(bid.getDouble("bidPrice"));
                                    break;
                                }
                            }
                        } catch (
                                JSONException e)

                        {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.print("Failed response on bid");
            }
        });

        requestQueue.add(requestBid);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //spinner on selected.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
