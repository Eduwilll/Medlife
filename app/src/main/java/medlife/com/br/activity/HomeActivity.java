package medlife.com.br.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import medlife.com.br.R;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.navigation_home) {
                // Handle home navigation
                return true;
            } else if (itemId == R.id.navigation_search) {
                // Handle search navigation
                return true;
            } else if (itemId == R.id.navigation_cart) {
                // Handle cart navigation
                return true;
            } else if (itemId == R.id.navigation_favorite) {
                // Handle favorites navigation
                return true;
            } else if (itemId == R.id.navigation_profile) {
                // Handle profile navigation
                return true;
            }
            
            return false;
        });
        
        // Set default selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }
}