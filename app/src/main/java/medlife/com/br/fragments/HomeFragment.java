package medlife.com.br.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import medlife.com.br.R;
import medlife.com.br.adapter.ProductAdapter;
import medlife.com.br.adapter.CategoryAdapter;
import medlife.com.br.model.Product;
import medlife.com.br.model.Category;
import medlife.com.br.model.Farmacia;
import medlife.com.br.helper.UsuarioFirebase;
import medlife.com.br.helper.UserLocationManager;
import medlife.com.br.activity.HomeActivity;

public class HomeFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    
    private TextView locationText;
    private CardView searchBar;
    private RecyclerView medicamentosRecycler;
    private RecyclerView subcategoriesRecycler;
    private RecyclerView bestSellingRecycler;
    private RecyclerView exclusiveOffersRecycler;
    private FirebaseUser usuarioAtual;
    private UserLocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize views
        locationText = view.findViewById(R.id.locationText);
        searchBar = view.findViewById(R.id.searchBar);
        subcategoriesRecycler = view.findViewById(R.id.subcategoriesRecycler);
        medicamentosRecycler = view.findViewById(R.id.medicamentosRecycler);
        bestSellingRecycler = view.findViewById(R.id.bestSellingRecycler);
        exclusiveOffersRecycler = view.findViewById(R.id.exclusiveOffersRecycler);

        // Get current user
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        // Get location manager from activity
        if (getActivity() instanceof HomeActivity) {
            locationManager = ((HomeActivity) getActivity()).getLocationManager();
        } else {
            // Fallback: create new instance if not available from activity
            locationManager = new UserLocationManager(requireContext());
        }
        
        // Set up location callback
        locationManager.setLocationCallback(new UserLocationManager.LocationCallback() {
            @Override
            public void onLocationReceived(String address) {
                if (locationText != null) {
                    locationText.setText(address);
                }
            }

            @Override
            public void onLocationError(String error) {
                // Fallback to default location
                if (locationText != null) {
                    locationText.setText("Erro ao obter localização");
                }
            }
        });

        // Setup location
        setupLocation();

        // Setup search bar click listener
        setupSearchBar();

        // Setup location bar click listener
        setupLocationBar(view);

        // Setup RecyclerViews
        setupMedicamentosRecycler();
        setupSubcategoriesRecycler();
        setupBestSellingRecycler();
        setupExclusiveOffersRecycler();
        
        return view;
    }

    private void setupLocation() {
        // Check location permission first
        if (checkLocationPermission()) {
            // Show loading state
            locationText.setText("Carregando localização...");
            
            // Get the best available location
            locationManager.getBestAvailableLocation();
        } else {
            // Request permission
            requestLocationPermission();
        }
    }

    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), 
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(requireActivity(),
            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
            LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get location
                setupLocation();
            } else {
                // Permission denied, show default location
                locationText.setText("Adicione seu endereço");
            }
        }
    }

    private void setupSearchBar() {
        searchBar.setOnClickListener(v -> {
            // Navigate to SearchFragment
            SearchFragment searchFragment = new SearchFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFrame, searchFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
    }

    private void setupLocationBar(View view) {
        View locationBar = view.findViewById(R.id.locationBar);
        locationBar.setOnClickListener(v -> {
            // Show location selection dialog or navigate to location settings
            showLocationDialog();
        });
    }

    private void showLocationDialog() {
        // Create a simple dialog to allow users to select location
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Selecionar Localização");
        
        String[] locations = {"São Paulo, SP", "Campinas, SP", "Rio de Janeiro, RJ", "Belo Horizonte, MG", "Salvador, BA"};
        
        builder.setItems(locations, (dialog, which) -> {
            String selectedLocation = locations[which];
            locationText.setText(selectedLocation);
            
            // Save the selected location to both SharedPreferences and Firestore
            String[] parts = selectedLocation.split(", ");
            if (parts.length >= 2) {
                String city = parts[0];
                String state = parts[1];
                
                // Save to SharedPreferences
                locationManager.saveAddress("", city, state);
                
                // Save to Firestore with coordinates (0,0 for manual selection)
                locationManager.saveLastLocationToFirestore("", city, state, 0, 0);
            }
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setupSubcategoriesRecycler() {

        List<Category> categories = new ArrayList<>();
        categories.add(new Category(R.drawable.mock_generico, "Antibióticos"));
        categories.add(new Category(R.drawable.mock_medicamentogenerico, "Analgésicos"));
        categories.add(new Category(R.drawable.mock_vitaminas, "Vitaminas"));
        categories.add(new Category(R.drawable.mock_banho, "Higiene"));
        categories.add(new Category(R.drawable.mock_melagriao, "Xaropes"));
        categories.add(new Category(R.drawable.mock_medicamentogenerico, "Genéricos"));
        CategoryAdapter adapter = new CategoryAdapter(getContext(), categories);
        subcategoriesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        subcategoriesRecycler.setAdapter(adapter);

    }

    private void setupMedicamentosRecycler() {
        // Mock pharmacies
        Farmacia farmacia1 = new Farmacia("1", "Drogaria São Paulo", "Cidade Campinas");
        Farmacia farmacia2 = new Farmacia("2", "Drogasil", "Cidade Americana");
        setupMedicamentosRecycler(farmacia1, farmacia2);
    }

    private void setupBestSellingRecycler() {
        // Mock pharmacies
        Farmacia farmacia1 = new Farmacia("1", "Drogaria São Paulo", "Cidade Campinas");
        Farmacia farmacia2 = new Farmacia("2", "Drogasil", "Cidade Americana");
        setupBestSellingRecycler(farmacia1, farmacia2);
    }

    private void setupExclusiveOffersRecycler() {
        // Mock pharmacies
        Farmacia farmacia1 = new Farmacia("1", "Drogaria São Paulo", "Cidade Campinas");
        Farmacia farmacia2 = new Farmacia("2", "Drogasil", "Cidade Americana");
        setupExclusiveOffersRecycler(farmacia1, farmacia2);
    }

    private void setupMedicamentosRecycler(Farmacia farmacia1, Farmacia farmacia2) {
        List<Product> allSellingProducts = new ArrayList<>();
        allSellingProducts.add(new Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99", "Antidepressivos", "PFIZER", Product.TARJA_PRETA, farmacia1));
        allSellingProducts.add(new Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79", "Fitoterápico", "EMS", Product.TARJA_SEM_TARJA, farmacia2));
        allSellingProducts.add(new Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia1));
        allSellingProducts.add(new Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90", "Fitoterápico", "NOVATIS", Product.TARJA_AMARELA, farmacia2));
        ProductAdapter adapter = new ProductAdapter(getContext(), allSellingProducts);
        medicamentosRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        medicamentosRecycler.setAdapter(adapter);
    }

    private void setupBestSellingRecycler(Farmacia farmacia1, Farmacia farmacia2) {
        List<Product> bestSellingProducts = new ArrayList<>();
        bestSellingProducts.add(new Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99", "Antidepressivos", "PFIZER", Product.TARJA_PRETA, farmacia1));
        bestSellingProducts.add(new Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79", "Fitoterápico", "EMS", Product.TARJA_SEM_TARJA, farmacia2));
        bestSellingProducts.add(new Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia1));
        bestSellingProducts.add(new Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90", "Fitoterápico", "NOVATIS", Product.TARJA_AMARELA, farmacia2));
        ProductAdapter adapter = new ProductAdapter(getContext(), bestSellingProducts);
        bestSellingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestSellingRecycler.setAdapter(adapter);
    }

    private void setupExclusiveOffersRecycler(Farmacia farmacia1, Farmacia farmacia2) {
        List<Product> offers = new ArrayList<>();
        offers.add(new Product(R.drawable.mock_medicamentogenerico, "Genérico Dipirona", "500mg, 20 Comprimidos", "R$7.99", "Vitaminas", "EMS", Product.TARJA_AMARELA, farmacia2));
        offers.add(new Product(R.drawable.mock_melagriao, "Xarope Melagrião", "120ml", "R$19.90", "Fitoterápico", "PFIZER", Product.TARJA_SEM_TARJA, farmacia1));
        offers.add(new Product(R.drawable.mock_protexbaby, "Shampoo Anticaspa", "200ml", "R$22.50", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia2));
        offers.add(new Product(R.drawable.mock_banho, "Higiene Pessoal Kit", "Sabonete + Shampoo", "R$29.90", "Perfumes", "NOVATIS", Product.TARJA_SEM_TARJA, farmacia1));
        ProductAdapter adapter = new ProductAdapter(getContext(), offers);
        exclusiveOffersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        exclusiveOffersRecycler.setAdapter(adapter);
    }
}