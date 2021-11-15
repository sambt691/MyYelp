package edu.stanford.smensah.myyelp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "PBDmLZ9odhb32GAsJN7ai90q8mUfQX9nZ5sjDtJItrUvstGCC_yaXGyNR1a0v1WRbPFs9Bx0GuF5Y7K3Zkw8HMuNbuf--aOkCu7wmFTtDCiw6WLz6jSGUtk41JuRYXYx"
class MainActivity : AppCompatActivity() {
    private lateinit var rvRestaurants: RecyclerView
    private lateinit var tvSearch: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvRestaurants = findViewById(R.id.rvRestaurants)
        tvSearch= findViewById(R.id.tvSearch)
        val restaurants = mutableListOf<YelpRestaurant>()
        val adapter = RestaurantsAdapter(this, restaurants)
        rvRestaurants.adapter = adapter
        rvRestaurants.layoutManager = LinearLayoutManager(this)

        val retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()

        val yelpService = retrofit.create(YelpService::class.java)

        tvSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                yelpService.searchRestaurants("Bearer $API_KEY", s.toString(), "location").enqueue(object: Callback<YelpSearchResult> {
                    override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                        Log.i(TAG, "onResponse $response")
                        val body = response.body()
                        if (body == null) {
                            Log.w(TAG, "Did not receive valid response body from Yelp API... exiting")
                            return
                        }
                        restaurants.clear()
                        restaurants.addAll(body.restaurants)
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                        Log.i(TAG, "onFailure $t")
                    }

                })
            }

        })

    }
}