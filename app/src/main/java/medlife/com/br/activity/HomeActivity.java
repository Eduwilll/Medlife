package medlife.com.br.activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import medlife.com.br.R;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout menuItemHome, menuItemSearch, menuItemCart, menuItemFavorite, menuItemProfile;
    private ImageView iconHome, iconSearch, iconCart, iconFavorite, iconProfile;
    private TextView textHome, textSearch, textCart, textFavorite, textProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        initializeViews();
        setupClickListeners();
        setActiveTab(menuItemHome); // Set Home as default active tab
    }

    private void initializeViews() {
        // Initialize menu items
        menuItemHome = findViewById(R.id.menuItemHome);
        menuItemSearch = findViewById(R.id.menuItemSearch);
        menuItemCart = findViewById(R.id.menuItemCart);
        menuItemFavorite = findViewById(R.id.menuItemFavorite);
        menuItemProfile = findViewById(R.id.menuItemProfile);

        // Initialize icons
        iconHome = findViewById(R.id.iconHome);
        iconSearch = findViewById(R.id.iconSearch);
        iconCart = findViewById(R.id.iconCart);
        iconFavorite = findViewById(R.id.iconFavorite);
        iconProfile = findViewById(R.id.iconProfile);

        // Initialize text views
        textHome = findViewById(R.id.textHome);
        textSearch = findViewById(R.id.textSearch);
        textCart = findViewById(R.id.textCart);
        textFavorite = findViewById(R.id.textFavorite);
        textProfile = findViewById(R.id.textProfile);
    }

    private void setupClickListeners() {
        menuItemHome.setOnClickListener(v -> setActiveTab(menuItemHome));
        menuItemSearch.setOnClickListener(v -> setActiveTab(menuItemSearch));
        menuItemCart.setOnClickListener(v -> setActiveTab(menuItemCart));
        menuItemFavorite.setOnClickListener(v -> setActiveTab(menuItemFavorite));
        menuItemProfile.setOnClickListener(v -> setActiveTab(menuItemProfile));
    }

    private void setActiveTab(LinearLayout activeTab) {
        // Reset all tabs to inactive state
        resetTabState(menuItemHome, iconHome, textHome);
        resetTabState(menuItemSearch, iconSearch, textSearch);
        resetTabState(menuItemCart, iconCart, textCart);
        resetTabState(menuItemFavorite, iconFavorite, textFavorite);
        resetTabState(menuItemProfile, iconProfile, textProfile);

        // Set active tab state
        if (activeTab == menuItemHome) {
            setActiveState(iconHome, textHome);
        } else if (activeTab == menuItemSearch) {
            setActiveState(iconSearch, textSearch);
        } else if (activeTab == menuItemCart) {
            setActiveState(iconCart, textCart);
        } else if (activeTab == menuItemFavorite) {
            setActiveState(iconFavorite, textFavorite);
        } else if (activeTab == menuItemProfile) {
            setActiveState(iconProfile, textProfile);
        }

        // Handle navigation logic here
        handleNavigation(activeTab);
    }

    private void resetTabState(LinearLayout tab, ImageView icon, TextView text) {
        icon.setColorFilter(getResources().getColor(R.color.inactive_color));
        text.setTextColor(getResources().getColor(R.color.inactive_color));
    }

    private void setActiveState(ImageView icon, TextView text) {
        icon.setColorFilter(getResources().getColor(R.color.active_color));
        text.setTextColor(getResources().getColor(R.color.active_color));
    }

    private void handleNavigation(LinearLayout activeTab) {
        // Add your navigation logic here
        // For example, you can load different fragments or start different activities
        if (activeTab == menuItemHome) {
            // Handle home navigation
        } else if (activeTab == menuItemSearch) {
            // Handle search navigation
        } else if (activeTab == menuItemCart) {
            // Handle cart navigation
        } else if (activeTab == menuItemFavorite) {
            // Handle favorites navigation
        } else if (activeTab == menuItemProfile) {
            // Handle profile navigation
        }
    }
}