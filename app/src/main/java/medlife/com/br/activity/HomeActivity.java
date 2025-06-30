package medlife.com.br.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.os.Bundle;
import android.view.View;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import medlife.com.br.R;
import medlife.com.br.fragments.*;
import medlife.com.br.helper.UserLocationManager;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.helper.CartManager;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private UserLocationManager locationManager;
    private BadgeDrawable cartBadge;
    private FloatingActionButton fabQuickCart;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        // Update last login timestamp safely without overwriting other data
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId != null) {
            UsuarioFirebase.atualizarLastLoginSeguro(userId);
        }
        
        // Initialize location manager
        initializeLocationManager();
        
        setupBottomNavigation();
        setupCartBadge();
        setupFloatingActionButton();

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }
    
    private void initializeLocationManager() {
        locationManager = new UserLocationManager(this);
        
        // Set up location callback for the activity
        locationManager.setLocationCallback(new UserLocationManager.LocationCallback() {
            @Override
            public void onLocationReceived(String address) {
                // Location received, can be used for app-wide location features
                // The HomeFragment will handle its own location display
            }

            @Override
            public void onLocationError(String error) {
                // Handle location errors at activity level if needed
            }
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;
            
            if (itemId == R.id.navigation_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.navigation_search) {
                fragment = new SearchFragment();
            } else if (itemId == R.id.navigation_cart) {
                fragment = new CartFragment();
            } else if (itemId == R.id.navigation_favorite) {
                fragment = new FavoriteFragment();
            } else if (itemId == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            }
            
            return loadFragment(fragment);
        });
        
        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, fragment)
                .commit();
            return true;
        }
        return false;
    }
    
    /**
     * Get the location manager instance for use in fragments
     */
    public UserLocationManager getLocationManager() {
        return locationManager;
    }

    private void setupCartBadge() {
        cartBadge = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart);
        updateCartBadge();
    }
    
    public void updateCartBadge() {
        if (cartBadge != null) {
            int cartItemCount = CartManager.getInstance().getCartItems().size();
            if (cartItemCount > 0) {
                cartBadge.setVisible(true);
                cartBadge.setNumber(cartItemCount);
            } else {
                cartBadge.setVisible(false);
            }
        }
        updateFABVisibility();
    }

    private void setupFloatingActionButton() {
        fabQuickCart = findViewById(R.id.fabQuickCart);
        fabQuickCart.setOnClickListener(view -> {
            // Navigate to cart fragment
            loadFragment(new CartFragment());
            bottomNavigationView.setSelectedItemId(R.id.navigation_cart);
        });
        
        // Show FAB only when cart has items
        updateFABVisibility();
    }
    
    private void updateFABVisibility() {
        if (fabQuickCart != null) {
            int cartItemCount = CartManager.getInstance().getCartItems().size();
            fabQuickCart.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
        }
    }
}