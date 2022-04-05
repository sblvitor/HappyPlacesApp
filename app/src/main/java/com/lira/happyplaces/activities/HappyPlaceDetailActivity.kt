package com.lira.happyplaces.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lira.happyplaces.database.HappyPlaceEntity
import com.lira.happyplaces.databinding.ActivityHappyPlaceDetailBinding

class HappyPlaceDetailActivity : AppCompatActivity() {

    private var binding: ActivityHappyPlaceDetailBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHappyPlaceDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Toolbar
        setSupportActionBar(binding?.toolbarHappyPlaceDetail)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarHappyPlaceDetail?.setNavigationOnClickListener {
            onBackPressed()
        }

        var happyPlaceDetailEntity: HappyPlaceEntity? = null

        if(intent.hasExtra("extra_place_details")){
            happyPlaceDetailEntity = intent.getSerializableExtra("extra_place_details") as HappyPlaceEntity
        }

        if(happyPlaceDetailEntity != null){
            supportActionBar!!.title = happyPlaceDetailEntity.title
            binding?.ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetailEntity.image))
            binding?.tvDescription?.text = happyPlaceDetailEntity.description
            binding?.tvLocation?.text = happyPlaceDetailEntity.location
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}