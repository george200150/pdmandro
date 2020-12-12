package com.george200150.uni.pdmandro.todo.plant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.george200150.uni.pdmandro.R
import com.george200150.uni.pdmandro.auth.data.AuthRepository
import com.george200150.uni.pdmandro.core.TAG
import com.george200150.uni.pdmandro.todo.data.Plant
import com.george200150.uni.pdmandro.todo.data.local.LocationHelper
import com.george200150.uni.pdmandro.todo.maps.BasicMapActivity
import com.george200150.uni.pdmandro.todo.maps.EventsActivity
import kotlinx.android.synthetic.main.fragment_item_edit.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

class PlantEditFragment : Fragment() {
    companion object {
        const val ITEM_ID = "ITEM_ID"
    }

    private lateinit var viewModel: PlantEditViewModel
    private var plantId: String? = null
    private var plant: Plant? = null

    private val REQUEST_PERMISSION = 10
    private val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.v(TAG, "onCreate")
        arguments?.let {
            if (it.containsKey(ITEM_ID)) {
                plantId = it.getString(ITEM_ID).toString()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkCameraPermission()
        initPlantLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.fragment_item_edit, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.v(TAG, "onActivityCreated")
        setupViewModel()
        fab.setOnClickListener {
            Log.v(TAG, "save plant")
            val i = plant
            if (i != null) {
                i.name = movie_name.text.toString()
                i.hasFlowers = plant_has_flowers.text.toString().toBoolean()
                i.bloomDate = plant_date.text.toString()
                viewModel.saveOrUpdatePlant(i)
            }
        }

        delete_button.setOnClickListener {
            viewModel.deletePlant(plantId ?: "")
            findNavController().navigate(R.id.fragment_item_list)
        }

        btCapturePhoto.setOnClickListener { openCamera() }

        btnLocation.setOnClickListener {
            LocationHelper.setPinLocation(plant?.latitude!!, plant?.longitude!!)
            val intent = Intent(requireContext(), EventsActivity::class.java)
            startActivity(intent)
        }

        txtLocation.setOnClickListener {
            if (plant != null && plant?.latitude != null && plant?.longitude != null) {
                LocationHelper.setPinLocation(plant?.latitude!!, plant?.longitude!!)
                val intent = Intent(requireContext(), BasicMapActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(requireActivity().packageManager)?.also {
                val photoFile: File? = try {
                    createCapturedPhoto()
                } catch (ex: IOException) {
                    null
                }
                Log.d(TAG, "photofile $photoFile")
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        requireContext(),
                        "com.george200150.uni.pdmandro.fileprovider",
                        it
                    )
                    Log.d(TAG, "photoURI: $photoURI");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createCapturedPhoto(): File {
        val timestamp: String = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        var f = File.createTempFile("PHOTO_${timestamp}", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
        plant?.imageURI = currentPhotoPath
        return f
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                val uri = Uri.parse(currentPhotoPath)
                ivImage.setImageURI(uri)
            }
        }
    }

    private fun initPlantLocation() {
        val location = LocationHelper.getLocationAndClear()
        if (location.first != 0f || location.second != 0f) {
            txtLocation.text = "${location.first} ${location.second}"
            plant?.latitude = location.first
            plant?.longitude = location.second
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(PlantEditViewModel::class.java)
        viewModel.fetching.observe(viewLifecycleOwner, { fetching ->
            Log.v(TAG, "update fetching")
            progress.visibility = if (fetching) View.VISIBLE else View.GONE
        })
        viewModel.fetchingError.observe(viewLifecycleOwner, { exception ->
            if (exception != null) {
                Log.v(TAG, "update fetching error")
                val message = "Fetching exception ${exception.message}"
                val parentActivity = activity?.parent
                if (parentActivity != null) {
                    Toast.makeText(parentActivity, message, Toast.LENGTH_SHORT).show()
                }
            }
        })
        viewModel.completed.observe(viewLifecycleOwner, { completed ->
            if (completed) {
                Log.v(TAG, "completed, navigate back")
                findNavController().popBackStack()
            }
        })
        val id = plantId

        if (id == null) {
            plant = Plant(
                "",
                AuthRepository.getUsername(),
                "name",
                true,
                LocalDate.now().toString(),
                true,
                "",
                "",
                0f,
                0f
            )
        } else {
            viewModel.getPlantById(id).observe(viewLifecycleOwner, {
                Log.v(TAG, "update plants")
                if (it != null) {
                    plant = it
                    plant_name.text = it.name
                    plant_has_flowers.setText(it.hasFlowers.toString())
                    plant_date.setText(it.bloomDate)

                    if (!plant?.imageURI.isNullOrBlank()) {
                        ivImage.setImageURI(Uri.parse(plant?.imageURI))
                        Log.d(TAG, "change image to ${plant?.imageURI}")
                    }
                    txtLocation.text = "${it.latitude} ${it.longitude}"
                }
            })
        }
    }

}
