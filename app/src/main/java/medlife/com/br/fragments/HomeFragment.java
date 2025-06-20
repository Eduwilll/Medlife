package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.List;
import medlife.com.br.R;
import medlife.com.br.adapter.ProductAdapter;
import medlife.com.br.adapter.CategoryAdapter;
import medlife.com.br.model.Product;
import medlife.com.br.model.Category;
import medlife.com.br.helper.UsuarioFirebase;

public class HomeFragment extends Fragment {
    private TextView locationText;
    private RecyclerView medicamentosRecycler;
    private RecyclerView subcategoriesRecycler;
    private RecyclerView bestSellingRecycler;
    private RecyclerView exclusiveOffersRecycler;
    private FirebaseUser usuarioAtual;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize views
        locationText = view.findViewById(R.id.locationText);
        subcategoriesRecycler = view.findViewById(R.id.subcategoriesRecycler);
        medicamentosRecycler = view.findViewById(R.id.medicamentosRecycler);
        bestSellingRecycler = view.findViewById(R.id.bestSellingRecycler);
        exclusiveOffersRecycler = view.findViewById(R.id.exclusiveOffersRecycler);

        // Get current user
        usuarioAtual = UsuarioFirebase.getUsuarioAtual();

        // Setup location
        setupLocation();

        // Setup RecyclerViews
        setupMedicamentosRecycler();
        setupSubcategoriesRecycler();
        setupBestSellingRecycler();
        setupExclusiveOffersRecycler();
        
        return view;
    }

    private void setupLocation() {
        // TODO: Implement location detection or user's saved location
        locationText.setText("São Paulo, Campinas");
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
        List<Product> allSellingProducts = new ArrayList<>();
        allSellingProducts.add(new Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99"));
        allSellingProducts.add(new Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79"));
        allSellingProducts.add(new Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90"));
        allSellingProducts.add(new Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90"));
        ProductAdapter adapter = new ProductAdapter(getContext(), allSellingProducts);
        medicamentosRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        medicamentosRecycler.setAdapter(adapter);
    }

    private void setupBestSellingRecycler() {
        List<Product> bestSellingProducts = new ArrayList<>();
        bestSellingProducts.add(new Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99"));
        bestSellingProducts.add(new Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79"));
        bestSellingProducts.add(new Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90"));
        bestSellingProducts.add(new Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90"));
        ProductAdapter adapter = new ProductAdapter(getContext(), bestSellingProducts);
        bestSellingRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        bestSellingRecycler.setAdapter(adapter);
    }

    private void setupExclusiveOffersRecycler() {
        List<Product> offers = new ArrayList<>();
        offers.add(new Product(R.drawable.mock_medicamentogenerico, "Genérico Dipirona", "500mg, 20 Comprimidos", "R$7.99"));
        offers.add(new Product(R.drawable.mock_melagriao, "Xarope Melagrião", "120ml", "R$19.90"));
        offers.add(new Product(R.drawable.mock_protexbaby, "Shampoo Anticaspa", "200ml", "R$22.50"));
        offers.add(new Product(R.drawable.mock_banho, "Higiene Pessoal Kit", "Sabonete + Shampoo", "R$29.90"));
        ProductAdapter adapter = new ProductAdapter(getContext(), offers);
        exclusiveOffersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        exclusiveOffersRecycler.setAdapter(adapter);
    }
}