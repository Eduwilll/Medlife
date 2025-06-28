package medlife.com.br.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import java.util.HashMap;
import java.util.Map;

public class UserLocationManager {
    private static final String TAG = "UserLocationManager";
    private static final String PREF_NAME = "location_prefs";
    private static final String KEY_USER_ADDRESS = "user_address";
    private static final String KEY_USER_CITY = "user_city";
    private static final String KEY_USER_STATE = "user_state";
    
    private Context context;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private LocationCallback locationCallback;
    
    public interface LocationCallback {
        void onLocationReceived(String address);
        void onLocationError(String error);
    }
    
    public UserLocationManager(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }
    
    public void setLocationCallback(LocationCallback callback) {
        this.locationCallback = callback;
    }
    
    /**
     * Save user's last location to Firestore
     */
    public void saveLastLocationToFirestore(String address, String city, String state, double latitude, double longitude) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, cannot save location to Firestore");
            return;
        }
        
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("address", address);
        locationData.put("city", city);
        locationData.put("state", state);
        locationData.put("latitude", latitude);
        locationData.put("longitude", longitude);
        locationData.put("timestamp", System.currentTimeMillis());
        locationData.put("fullAddress", address + ", " + city + ", " + state);
        
        // Save to Firestore in usuarios/{userId}/lastLocation/{locationId}
        db.collection("usuarios")
            .document(currentUser.getUid())
            .collection("lastLocation")
            .document("current")
            .set(locationData)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Last location saved to Firestore successfully");
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error saving last location to Firestore", e);
            });
    }
    
    /**
     * Get user's last location from Firestore
     */
    public void getLastLocationFromFirestore(OnLastLocationReceivedListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Log.w(TAG, "User not authenticated, cannot get location from Firestore");
            listener.onLastLocationReceived(null, null, null, 0, 0);
            return;
        }
        
        db.collection("usuarios")
            .document(currentUser.getUid())
            .collection("lastLocation")
            .document("current")
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String address = documentSnapshot.getString("address");
                    String city = documentSnapshot.getString("city");
                    String state = documentSnapshot.getString("state");
                    Double latitude = documentSnapshot.getDouble("latitude");
                    Double longitude = documentSnapshot.getDouble("longitude");
                    
                    if (address != null && city != null && state != null) {
                        String fullAddress = address + ", " + city + ", " + state;
                        listener.onLastLocationReceived(fullAddress, city, state, 
                            latitude != null ? latitude : 0, 
                            longitude != null ? longitude : 0);
                    } else {
                        listener.onLastLocationReceived(null, null, null, 0, 0);
                    }
                } else {
                    listener.onLastLocationReceived(null, null, null, 0, 0);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting last location from Firestore", e);
                listener.onLastLocationReceived(null, null, null, 0, 0);
            });
    }
    
    /**
     * Get user's saved address from SharedPreferences
     */
    public String getSavedAddress() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String address = prefs.getString(KEY_USER_ADDRESS, null);
        String city = prefs.getString(KEY_USER_CITY, null);
        String state = prefs.getString(KEY_USER_STATE, null);
        
        if (address != null && city != null && state != null) {
            return address + ", " + city + ", " + state;
        } else if (city != null && state != null) {
            return city + ", " + state;
        }
        
        return null;
    }
    
    /**
     * Save user's address to SharedPreferences
     */
    public void saveAddress(String address, String city, String state) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_USER_ADDRESS, address);
        editor.putString(KEY_USER_CITY, city);
        editor.putString(KEY_USER_STATE, state);
        editor.apply();
        
        // Also save to Firestore
        saveLastLocationToFirestore(address, city, state, 0, 0);
    }
    
    /**
     * Get user's address from Firebase (if available)
     */
    public void getUserAddressFromFirebase(OnAddressReceivedListener listener) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            listener.onAddressReceived(null);
            return;
        }
        
        db.collection("usuarios").document(currentUser.getUid())
            .get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String address = documentSnapshot.getString("address");
                    String city = documentSnapshot.getString("city");
                    String state = documentSnapshot.getString("state");
                    
                    if (address != null && city != null && state != null) {
                        String fullAddress = address + ", " + city + ", " + state;
                        listener.onAddressReceived(fullAddress);
                    } else if (city != null && state != null) {
                        String fullAddress = city + ", " + state;
                        listener.onAddressReceived(fullAddress);
                    } else {
                        listener.onAddressReceived(null);
                    }
                } else {
                    listener.onAddressReceived(null);
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting user address from Firebase", e);
                listener.onAddressReceived(null);
            });
    }
    
    /**
     * Get current location using GPS
     */
    public void getCurrentLocation() {
        if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) 
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            if (locationCallback != null) {
                locationCallback.onLocationError("Location permission not granted");
            }
            return;
        }
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener(location -> {
                if (location != null) {
                    // In a real app, you would use reverse geocoding here
                    // to convert coordinates to address
                    String address = reverseGeocode(location);
                    
                    // Save the current location to Firestore
                    String[] addressParts = address.split(", ");
                    if (addressParts.length >= 2) {
                        String city = addressParts[0];
                        String state = addressParts[1];
                        saveLastLocationToFirestore("", city, state, location.getLatitude(), location.getLongitude());
                    }
                    
                    if (locationCallback != null) {
                        locationCallback.onLocationReceived(address);
                    }
                } else {
                    if (locationCallback != null) {
                        locationCallback.onLocationError("Unable to get current location");
                    }
                }
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "Error getting location", e);
                if (locationCallback != null) {
                    locationCallback.onLocationError("Error getting location: " + e.getMessage());
                }
            });
    }
    
    /**
     * Simple reverse geocoding (in a real app, use Google Geocoding API)
     */
    private String reverseGeocode(Location location) {
        // This is a simplified version. In a real app, you would use:
        // Google Geocoding API or other geocoding services
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        
        // For now, return a default address based on coordinates
        // In a real implementation, you would make an API call to get the actual address
        if (lat > -23.5 && lat < -23.4 && lng > -46.7 && lng < -46.6) {
            return "São Paulo, SP";
        } else if (lat > -22.9 && lat < -22.8 && lng > -47.1 && lng < -47.0) {
            return "Campinas, SP";
        } else {
            return "Localização atual";
        }
    }
    
    /**
     * Get the best available location (saved address, Firebase, or current location)
     */
    public void getBestAvailableLocation() {
        // First, try to get saved address
        String savedAddress = getSavedAddress();
        if (savedAddress != null) {
            if (locationCallback != null) {
                locationCallback.onLocationReceived(savedAddress);
            }
            return;
        }
        
        // Then, try to get from Firestore lastLocation
        getLastLocationFromFirestore(new OnLastLocationReceivedListener() {
            @Override
            public void onLastLocationReceived(String fullAddress, String city, String state, double latitude, double longitude) {
                if (fullAddress != null) {
                    if (locationCallback != null) {
                        locationCallback.onLocationReceived(fullAddress);
                    }
                } else {
                    // Try to get from Firebase users collection
                    getUserAddressFromFirebase(new OnAddressReceivedListener() {
                        @Override
                        public void onAddressReceived(String address) {
                            if (address != null) {
                                if (locationCallback != null) {
                                    locationCallback.onLocationReceived(address);
                                }
                            } else {
                                // Finally, try to get current location
                                getCurrentLocation();
                            }
                        }
                    });
                }
            }
        });
    }
    
    public interface OnAddressReceivedListener {
        void onAddressReceived(String address);
    }
    
    public interface OnLastLocationReceivedListener {
        void onLastLocationReceived(String fullAddress, String city, String state, double latitude, double longitude);
    }
} 