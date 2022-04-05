package com.lira.happyplaces.activities

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lira.happyplaces.R
import com.lira.happyplaces.database.HappyPlaceDao
import com.lira.happyplaces.database.HappyPlaceEntity
import com.lira.happyplaces.database.HappyPlacesApp
import com.lira.happyplaces.databinding.ActivityAddHappyPlaceBinding
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

open class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {

    private var binding: ActivityAddHappyPlaceBinding? = null

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0

    private var mHappyPlaceUpdate: HappyPlaceEntity? = null

    private val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK && result.data != null){
            try {
                val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, result.data?.data)
                saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                Log.e("Saved image: ", "Path :: $saveImageToInternalStorage")
                binding?.ivPlaceImage?.setImageBitmap(selectedImageBitmap)
            }catch (e: IOException){
                e.printStackTrace()
                Toast.makeText(this, "Failed to load the image from gallery", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val openCameraLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
        if(result.resultCode == RESULT_OK && result.data != null){
            val thumbnail: Bitmap = result.data!!.extras!!.get("data") as Bitmap
            saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
            Log.e("Saved image: ", "Path :: $saveImageToInternalStorage")
            binding?.ivPlaceImage?.setImageBitmap(thumbnail)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Toolbar
        setSupportActionBar(binding?.toolbarAddPlace)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra("happy_place_update")){
            mHappyPlaceUpdate = intent.getSerializableExtra("happy_place_update") as HappyPlaceEntity
        }
        if(mHappyPlaceUpdate != null){
            supportActionBar!!.title = "Edit Happy Place"
            binding?.etTitle?.setText(mHappyPlaceUpdate!!.title)
            binding?.etDescription?.setText(mHappyPlaceUpdate!!.description)
            binding?.etDate?.setText(mHappyPlaceUpdate!!.date)
            binding?.etLocation?.setText(mHappyPlaceUpdate!!.location)
            mLatitude = mHappyPlaceUpdate!!.latitude
            mLongitude = mHappyPlaceUpdate!!.longitude
            saveImageToInternalStorage = Uri.parse(mHappyPlaceUpdate!!.image)
            binding?.ivPlaceImage?.setImageURI(saveImageToInternalStorage)
            binding?.btnSave?.text = "UPDATE"
        }

        // Date dialog
        dateSetListener = DatePickerDialog.OnDateSetListener{ _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()
        binding?.etDate?.setOnClickListener(this)

        // Button add image
        binding?.tvAddImage?.setOnClickListener(this)

        val happyPlaceDao = (application as HappyPlacesApp).db.happyPlaceDao()

        // Button SAVE
        binding?.btnSave?.setOnClickListener{
            if(mHappyPlaceUpdate == null) {
                addHappyPlace(happyPlaceDao)
            }else{
                updateHappyPlace(mHappyPlaceUpdate!!.id, happyPlaceDao)
            }
        }

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
                pictureDialog.setPositiveButton("Select photo from Gallery") { _, _ ->
                    choosePhotoFromGallery()
                }
                pictureDialog.setNegativeButton("Capture photo from camera") { _, _ ->
                    capturePhotoFromCamera()
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
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        openGalleryLauncher.launch(galleryIntent)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>, token: PermissionToken){
                    showRationaleDialogForPermissions()
                }
            }).onSameThread().check()
    }

    private fun capturePhotoFromCamera(){
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .withListener(object: MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?){
                    if(report!!.areAllPermissionsGranted()){
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        openCameraLauncher.launch(cameraIntent)
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

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri{

        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir("HappyPlacesImages", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    private fun addHappyPlace(happyPlaceDao: HappyPlaceDao){
        val title = binding?.etTitle?.text.toString()
        val description = binding?.etDescription?.text.toString()
        val location = binding?.etLocation?.text.toString()
        val date = binding?.etDate?.text.toString()

        if(title.isNotEmpty() && description.isNotEmpty() && location.isNotEmpty() && saveImageToInternalStorage != null){
            lifecycleScope.launch {
                happyPlaceDao.insert(HappyPlaceEntity(title=title, description=description, location=location, image=saveImageToInternalStorage.toString(), date=date,
                    latitude=mLatitude, longitude=mLongitude))
                Toast.makeText(applicationContext, "Record saved", Toast.LENGTH_SHORT).show()
                finish()
            }
        }else{
            Toast.makeText(applicationContext, "Please, fill all the empty spaces", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateHappyPlace(id: Int, happyPlaceDao: HappyPlaceDao){
        val title = binding?.etTitle?.text.toString()
        val description = binding?.etDescription?.text.toString()
        val location = binding?.etLocation?.text.toString()
        val date = binding?.etDate?.text.toString()

        if(title.isNotEmpty() && description.isNotEmpty() && location.isNotEmpty() && saveImageToInternalStorage != null){
            lifecycleScope.launch {
                happyPlaceDao.update(HappyPlaceEntity(id=id, title=title, description=description, location=location, image=saveImageToInternalStorage.toString(), date=date,
                    latitude=mLatitude, longitude=mLongitude))
                Toast.makeText(applicationContext, "Record updated", Toast.LENGTH_SHORT).show()
                finish()
            }
        }else{
            Toast.makeText(applicationContext, "Please, fill all the empty spaces", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        mHappyPlaceUpdate = null
    }

}