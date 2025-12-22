
package com.dresscode.app.ui.weather

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dresscode.app.R
import com.dresscode.app.data.model.Result
import com.dresscode.app.data.model.WeatherData
import com.dresscode.app.databinding.FragmentWeatherBinding
import com.google.android.gms.location.LocationServices

class WeatherFragment : Fragment() {

    private var _binding: FragmentWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: WeatherViewModel

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocationAndFetchWeather()
        } else {
            Toast.makeText(context, "Location permission denied. Cannot fetch weather.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeatherBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        binding.refreshButton.setOnClickListener {
            checkLocationPermission()
        }
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocationAndFetchWeather()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(context, "Location permission is needed to show local weather.", Toast.LENGTH_LONG).show()
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    
    private fun getCurrentLocationAndFetchWeather() {
        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            // Use getCurrentLocation for a fresh, one-time location request.
            fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.fetchWeather(location.latitude, location.longitude)
                    } else {
                        context?.let {
                            Toast.makeText(it, "Could not get current location. Is location enabled on your device?", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                .addOnFailureListener { e ->
                    context?.let {
                        Toast.makeText(it, "Failed to get location: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
        } catch (e: SecurityException) {
             context?.let {
                Toast.makeText(it, "Location permission error: ${e.message}", Toast.LENGTH_LONG).show()
             }
        }
    }

    private fun setupObservers() {
        viewModel.weather.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> binding.loadingProgressBar.isVisible = true
                is Result.Success -> {
                    binding.loadingProgressBar.isVisible = false
                    updateUiWithWeather(result.data)
                }
                is Result.Error -> {
                    binding.loadingProgressBar.isVisible = false
                    Toast.makeText(context, "Error fetching weather: ${result.exception.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateUiWithWeather(weather: WeatherData) {
        binding.cityNameText.text = weather.cityName
        binding.temperatureText.text = "${weather.temperature.toInt()}°C"
        binding.weatherDescriptionText.text = weather.description
        
        val weatherIcon = when(weather.icon.dropLast(1)) { // Drop 'd' or 'n' for day/night
            "01" -> R.drawable.ic_weather_sun
            "02", "03", "04" -> R.drawable.ic_weather_clouds
            "09", "10", "11" -> R.drawable.ic_weather_rain
            else -> R.drawable.ic_nav_weather // A default icon
        }
        binding.weatherIcon.setImageResource(weatherIcon)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
