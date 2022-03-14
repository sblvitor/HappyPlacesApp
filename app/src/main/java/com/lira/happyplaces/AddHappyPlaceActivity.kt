package com.lira.happyplaces

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lira.happyplaces.databinding.ActivityAddHappyPlaceBinding
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlaceBinding? = null

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarAddPlace)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        binding?.etDate?.setOnClickListener(this)

        binding?.tvAddImage?.setOnClickListener(this)

    }

    private fun updateDateInView(){
        val myFormat = "dd/MM/yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDate?.setText(sdf.format(cal.time).toString())
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.etDate -> {
                DatePickerDialog(this@AddHappyPlaceActivity, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
            }
            R.id.tvAddImage -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select action")
                pictureDialog.setPositiveButton("Select photo from Gallery") { dialog, _ ->
                    choosePhotoFromGallery()
                }
                pictureDialog.setNegativeButton("Capture photo from camera") { dialog, _ ->
                    Toast.makeText(this, "Camera selection coming soon", Toast.LENGTH_SHORT).show()
                }
                pictureDialog.create().show()
            }
        }
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?){
                    if(report!!.areAllPermissionsGranted()){
                        Toast.makeText(this@AddHappyPlaceActivity, "Storage READ/WRITE permission are granted. Now u can select an image from Gallery", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken){
                    showRationaleDialogForPermissions()
                }
            }).onSameThread().check()
    }

    private fun showRationaleDialogForPermissions(){
        AlertDialog.Builder(this).setMessage("It looks like you have turned off permission required for this feature. It can be enabled under the Application Settings")
            .setPositiveButton("Go to SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") {dialog, _ ->
                dialog.dismiss()
            }.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

}