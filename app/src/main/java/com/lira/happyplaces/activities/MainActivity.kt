package com.lira.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lira.happyplaces.adapters.ItemAdapter
import com.lira.happyplaces.database.HappyPlaceDao
import com.lira.happyplaces.database.HappyPlaceEntity
import com.lira.happyplaces.database.HappyPlacesApp
import com.lira.happyplaces.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.fabAddHappyPlace?.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }

        val happyPlaceDao = (application as HappyPlacesApp).db.happyPlaceDao()

        lifecycleScope.launch {
            happyPlaceDao.fetchAllHappyPlaces().collect {
                val list = ArrayList(it)
                setupListOfHappyPlacesIntoRecyclerView(list, happyPlaceDao)
            }
        }
    }

    private fun setupListOfHappyPlacesIntoRecyclerView(happyPlacesList: ArrayList<HappyPlaceEntity>, happyPlaceDao: HappyPlaceDao){

        if(happyPlacesList.isNotEmpty()){
            val itemAdapter = ItemAdapter(happyPlacesList)
            binding?.rvHappyPlacesList?.adapter = itemAdapter
            binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
            binding?.rvHappyPlacesList?.visibility = View.VISIBLE
            binding?.tvNoRecords?.visibility = View.GONE
        }else{
            binding?.rvHappyPlacesList?.visibility = View.GONE
            binding?.tvNoRecords?.visibility = View.VISIBLE
        }

    }

}