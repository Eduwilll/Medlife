package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import medlife.com.br.R;
import android.content.SharedPreferences;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import medlife.com.br.adapter.ProductAdapter;
import medlife.com.br.model.Product;
import medlife.com.br.model.Farmacia;
import java.util.ArrayList;
import java.util.List;
import android.widget.LinearLayout;

public class FavoriteFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.favoriteRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Mock pharmacies
        Farmacia farmacia1 = new Farmacia("1", "Drogaria São Paulo", "Cidade Campinas");
        Farmacia farmacia2 = new Farmacia("2", "Drogasil", "Cidade Americana");
        // Mock products (same as HomeFragment)
        List<Product> allProducts = new ArrayList<>();
        allProducts.add(new Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99", "Antidepressivos", "PFIZER", Product.TARJA_PRETA, farmacia1));
        allProducts.add(new Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79", "Fitoterápico", "EMS", Product.TARJA_SEM_TARJA, farmacia2));
        allProducts.add(new Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia1));
        allProducts.add(new Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90", "Fitoterápico", "NOVATIS", Product.TARJA_AMARELA, farmacia2));
        allProducts.add(new Product(R.drawable.mock_medicamentogenerico, "Genérico Dipirona", "500mg, 20 Comprimidos", "R$7.99", "Vitaminas", "EMS", Product.TARJA_AMARELA, farmacia2));
        allProducts.add(new Product(R.drawable.mock_melagriao, "Xarope Melagrião", "120ml", "R$19.90", "Fitoterápico", "PFIZER", Product.TARJA_SEM_TARJA, farmacia1));
        allProducts.add(new Product(R.drawable.mock_protexbaby, "Shampoo Anticaspa", "200ml", "R$22.50", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia2));
        allProducts.add(new Product(R.drawable.mock_banho, "Higiene Pessoal Kit", "Sabonete + Shampoo", "R$29.90", "Perfumes", "NOVATIS", Product.TARJA_SEM_TARJA, farmacia1));
        // Load favorites
        SharedPreferences prefs = getContext().getSharedPreferences("favorites", getContext().MODE_PRIVATE);
        List<Product> favoriteProducts = new ArrayList<>();
        for (Product p : allProducts) {
            if (prefs.getBoolean(p.getName(), false)) {
                favoriteProducts.add(p);
            }
        }
        ProductAdapter adapter = new ProductAdapter(getContext(), favoriteProducts, true);
        recyclerView.setAdapter(adapter);
        // Open product detail on click
        adapter.setOnItemClickListener(product -> {
            android.content.Intent intent = new android.content.Intent(getContext(), medlife.com.br.activity.ProductDetailActivity.class);
            intent.putExtra("product", product);
            startActivity(intent);
        });
        LinearLayout emptyLayout = view.findViewById(R.id.emptyFavoriteLayout);
        if (favoriteProducts.isEmpty()) {
            emptyLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        return view;
    }
}