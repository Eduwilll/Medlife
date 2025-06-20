package medlife.com.br.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import medlife.com.br.R;
import medlife.com.br.adapter.ProductAdapter;
import medlife.com.br.model.Product;
import androidx.recyclerview.widget.GridLayoutManager;
import android.widget.ImageView;

public class SearchFragment extends Fragment {
    private EditText searchEditText;
    private RecyclerView searchResultsRecycler;
    private ProductAdapter productAdapter;
    private List<Product> allMedicaments;
    private List<Product> filteredMedicaments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchEditText = view.findViewById(R.id.searchEditText);
        searchResultsRecycler = view.findViewById(R.id.searchResultsRecycler);
        ImageView filterIcon = view.findViewById(R.id.filterIcon);

        // Initialize medicaments list (copy from HomeFragment)
        allMedicaments = new ArrayList<>();
        allMedicaments.add(new Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99"));
        allMedicaments.add(new Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79"));
        allMedicaments.add(new Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90"));
        allMedicaments.add(new Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90"));
        allMedicaments.add(new Product(R.drawable.mock_medicamentogenerico, "Genérico Dipirona", "500mg, 20 Comprimidos", "R$7.99"));
        allMedicaments.add(new Product(R.drawable.mock_melagriao, "Xarope Melagrião", "120ml", "R$19.90"));
        allMedicaments.add(new Product(R.drawable.mock_protexbaby, "Shampoo Anticaspa", "200ml", "R$22.50"));
        allMedicaments.add(new Product(R.drawable.mock_banho, "Higiene Pessoal Kit", "Sabonete + Shampoo", "R$29.90"));
        // Add more products as needed

        filteredMedicaments = new ArrayList<>(allMedicaments);

        // Set up RecyclerView with a 2-column grid
        searchResultsRecycler.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new ProductAdapter(getContext(), filteredMedicaments);
        searchResultsRecycler.setAdapter(productAdapter);

        // Add TextWatcher to filter products as user types
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMedicaments(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        filterIcon.setOnClickListener(v -> {
            FilterFragment filterFragment = new FilterFragment();
            filterFragment.show(getParentFragmentManager(), "FilterFragment");
        });

        return view;
    }

    private void filterMedicaments(String query) {
        filteredMedicaments.clear();
        if (query.isEmpty()) {
            filteredMedicaments.addAll(allMedicaments);
        } else {
            for (Product product : allMedicaments) {
                if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredMedicaments.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }
}