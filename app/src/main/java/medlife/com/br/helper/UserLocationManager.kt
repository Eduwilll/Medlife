package medlife.com.br.helper

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserLocationManager(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var locationCallback: LocationCallback? = null

    interface LocationCallback {
        fun onLocationReceived(address: String)
        fun onLocationError(error: String)
    }

    fun setLocationCallback(callback: LocationCallback?) {
        this.locationCallback = callback
    }

    fun saveLastLocationToFirestore(address: String, city: String, state: String, latitude: Double, longitude: Double) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, cannot save location to Firestore")
            return
        }

        val locationData = hashMapOf<String, Any>(
            "address" to address,
            "city" to city,
            "state" to state,
            "latitude" to latitude,
            "longitude" to longitude,
            "timestamp" to System.currentTimeMillis(),
            "fullAddress" to "$address, $city, $state"
        )

        db.collection("usuarios").document(currentUser.uid)
            .collection("lastLocation").document("current")
            .set(locationData)
            .addOnSuccessListener { Log.d(TAG, "Last location saved to Firestore successfully") }
            .addOnFailureListener { e -> Log.e(TAG, "Error saving last location to Firestore", e) }
    }

    fun getLastLocationFromFirestore(listener: OnLastLocationReceivedListener) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, cannot get location from Firestore")
            listener.onLastLocationReceived(null, null, null, 0.0, 0.0)
            return
        }

        db.collection("usuarios").document(currentUser.uid)
            .collection("lastLocation").document("current").get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val address = documentSnapshot.getString("address")
                    val city = documentSnapshot.getString("city")
                    val state = documentSnapshot.getString("state")
                    val latitude = documentSnapshot.getDouble("latitude") ?: 0.0
                    val longitude = documentSnapshot.getDouble("longitude") ?: 0.0

                    if (address != null && city != null && state != null) {
                        listener.onLastLocationReceived("$address, $city, $state", city, state, latitude, longitude)
                    } else {
                        listener.onLastLocationReceived(null, null, null, 0.0, 0.0)
                    }
                } else {
                    listener.onLastLocationReceived(null, null, null, 0.0, 0.0)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting last location from Firestore", e)
                listener.onLastLocationReceived(null, null, null, 0.0, 0.0)
            }
    }

    val savedAddress: String?
        get() {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val address = prefs.getString(KEY_USER_ADDRESS, null)
            val city = prefs.getString(KEY_USER_CITY, null)
            val state = prefs.getString(KEY_USER_STATE, null)

            return if (address != null && city != null && state != null) {
                "$address, $city, $state"
            } else if (city != null && state != null) {
                "$city, $state"
            } else {
                null
            }
        }

    fun saveAddress(address: String, city: String, state: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_USER_ADDRESS, address)
            putString(KEY_USER_CITY, city)
            putString(KEY_USER_STATE, state)
            apply()
        }
        saveLastLocationToFirestore(address, city, state, 0.0, 0.0)
    }

    fun getUserAddressFromFirebase(listener: OnAddressReceivedListener) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            listener.onAddressReceived(null)
            return
        }

        db.collection("usuarios").document(currentUser.uid).get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val address = documentSnapshot.getString("address")
                    val city = documentSnapshot.getString("city")
                    val state = documentSnapshot.getString("state")

                    if (address != null && city != null && state != null) {
                        listener.onAddressReceived("$address, $city, $state")
                    } else if (city != null && state != null) {
                        listener.onAddressReceived("$city, $state")
                    } else {
                        listener.onAddressReceived(null)
                    }
                } else {
                    listener.onAddressReceived(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting user address from Firebase", e)
                listener.onAddressReceived(null)
            }
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            locationCallback?.onLocationError("Location permission not granted")
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val address = reverseGeocode(location)
                    val addressParts = address.split(", ")
                    if (addressParts.size >= 2) {
                        saveLastLocationToFirestore("", addressParts[0], addressParts[1], location.latitude, location.longitude)
                    }
                    locationCallback?.onLocationReceived(address)
                } else {
                    locationCallback?.onLocationError("Unable to get current location")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting location", e)
                locationCallback?.onLocationError("Error getting location: ${e.message}")
            }
    }

    private fun reverseGeocode(location: Location): String {
        val lat = location.latitude
        val lng = location.longitude
        return if (lat in -23.5..-23.4 && lng in -46.7..-46.6) {
            "São Paulo, SP"
        } else if (lat in -22.9..-22.8 && lng in -47.1..-47.0) {
            "Campinas, SP"
        } else {
            "Localização atual"
        }
    }

    fun getBestAvailableLocation() {
        val address = savedAddress
        if (address != null) {
            locationCallback?.onLocationReceived(address)
            return
        }

        getLastLocationFromFirestore(object : OnLastLocationReceivedListener {
            override fun onLastLocationReceived(fullAddress: String?, city: String?, state: String?, latitude: Double, longitude: Double) {
                if (fullAddress != null) {
                    locationCallback?.onLocationReceived(fullAddress)
                } else {
                    getUserAddressFromFirebase(object : OnAddressReceivedListener {
                        override fun onAddressReceived(address: String?) {
                            if (address != null) {
                                locationCallback?.onLocationReceived(address)
                            } else {
                                getCurrentLocation()
                            }
                        }
                    })
                }
            }
        })
    }

    interface OnAddressReceivedListener {
        fun onAddressReceived(address: String?)
    }

    interface OnLastLocationReceivedListener {
        fun onLastLocationReceived(fullAddress: String?, city: String?, state: String?, latitude: Double, longitude: Double)
    }

    companion object {
        private const val TAG = "UserLocationManager"
        private const val PREF_NAME = "location_prefs"
        private const val KEY_USER_ADDRESS = "user_address"
        private const val KEY_USER_CITY = "user_city"
        private const val KEY_USER_STATE = "user_state"
    }
}
