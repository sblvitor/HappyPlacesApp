package com.lira.happyplaces.activities

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
            val itemAdapter = ItemAdapter(happyPlacesList,
                { detailId ->
                    happyPlaceDetail(detailId, happyPlaceDao)
                },
                { updateId ->
                    setupUpdateHappyPlace(updateId, happyPlaceDao)
                },
                { deleteId ->
                    deleteRecordAlertDialog(deleteId, happyPlaceDao)
                })
            binding?.rvHappyPlacesList?.adapter = itemAdapter
            binding?.rvHappyPlacesList?.layoutManager = LinearLayoutManager(this)
            binding?.rvHappyPlacesList?.visibility = View.VISIBLE
            binding?.tvNoRecords?.visibility = View.GONE
        }else{
            binding?.rvHappyPlacesList?.visibility = View.GONE
            binding?.tvNoRecords?.visibility = View.VISIBLE
        }

    }

    private fun happyPlaceDetail(id: Int, happyPlaceDao: HappyPlaceDao){
        lifecycleScope.launch {
            happyPlaceDao.fetchHappyPlaceById(id).collect {
                if(it != null){
                    val intent = Intent(this@MainActivity, HappyPlaceDetailActivity::class.java)
                    intent.putExtra("extra_place_details", it)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupUpdateHappyPlace(id: Int, happyPlaceDao: HappyPlaceDao){

        lifecycleScope.launch {
            happyPlaceDao.fetchHappyPlaceById(id).collect {
                if(it != null){
                    val intent = Intent(this@MainActivity, AddHappyPlaceActivity::class.java)
                    intent.putExtra("happy_place_update", it)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            }
        }
    }

    private fun deleteRecordAlertDialog(id: Int, happyPlaceDao: HappyPlaceDao){

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record?")

        builder.setPositiveButton("Yes"){ dialog, _ ->
            lifecycleScope.launch {
                happyPlaceDao.delete(HappyPlaceEntity(id))
                Toast.makeText(applicationContext, "Record deleted!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        builder.setNegativeButton("No"){ dialog, _ ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}