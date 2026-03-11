package medlife.com.br.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import medlife.com.br.R
import medlife.com.br.adapter.ProductAdapter
import medlife.com.br.model.Farmacia
import medlife.com.br.model.Product

class SearchFragment : Fragment(), FilterFragment.FilterListener {
    private lateinit var searchEditText: EditText
    private lateinit var searchResultsRecycler: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val allMedicaments = mutableListOf<Product>()
    private val filteredMedicaments = mutableListOf<Product>()
    private var selectedCategories = listOf<String>()
    private var selectedBrands = listOf<String>()
    private lateinit var clearIcon: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val farmacia1 = Farmacia("1", "Drogaria São Paulo", "Cidade Campinas")
        val farmacia2 = Farmacia("2", "Drogasil", "Cidade Americana")

        searchEditText = view.findViewById(R.id.searchEditText)
        searchResultsRecycler = view.findViewById(R.id.searchResultsRecycler)
        val filterIcon: ImageView = view.findViewById(R.id.filterIcon)
        clearIcon = view.findViewById(R.id.clearIcon)

        allMedicaments.apply {
            add(Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99", "Antidepressivos", "PFIZER", Product.TARJA_PRETA, farmacia1))
            add(Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79", "Fitoterápico", "EMS", Product.TARJA_SEM_TARJA, farmacia2))
            add(Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia1))
            add(Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90", "Fitoterápico", "NOVATIS", Product.TARJA_AMARELA, farmacia2))
            add(Product(R.drawable.mock_medicamentogenerico, "Genérico Dipirona", "500mg, 20 Comprimidos", "R$7.99", "Vitaminas", "EMS", Product.TARJA_AMARELA, farmacia2))
            add(Product(R.drawable.mock_melagriao, "Xarope Melagrião", "120ml", "R$19.90", "Fitoterápico", "PFIZER", Product.TARJA_SEM_TARJA, farmacia1))
            add(Product(R.drawable.mock_protexbaby, "Shampoo Anticaspa", "200ml", "R$22.50", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia2))
            add(Product(R.drawable.mock_banho, "Higiene Pessoal Kit", "Sabonete + Shampoo", "R$29.90", "Perfumes", "NOVATIS", Product.TARJA_SEM_TARJA, farmacia1))
        }

        filteredMedicaments.addAll(allMedicaments)

        searchResultsRecycler.layoutManager = GridLayoutManager(context, 2)
        productAdapter = ProductAdapter(context, filteredMedicaments)
        searchResultsRecycler.adapter = productAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearIcon.visibility = if (!s.isNullOrEmpty()) View.VISIBLE else View.GONE
                filterMedicaments(s?.toString() ?: "", selectedCategories, selectedBrands)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        clearIcon.setOnClickListener { searchEditText.setText("") }

        filterIcon.setOnClickListener {
            val filterFragment = FilterFragment()
            filterFragment.setFilterListener(this)
            filterFragment.show(parentFragmentManager, "FilterFragment")
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchEditText.requestFocus()
        showKeyboard()
    }

    private fun showKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun filterMedicaments(query: String, categories: List<String>, brands: List<String>) {
        filteredMedicaments.clear()

        for (product in allMedicaments) {
            val nameMatches = query.isEmpty() || product.name?.contains(query, ignoreCase = true) == true
            val categoryMatches = categories.isEmpty() || categories.contains(product.category)
            val brandMatches = brands.isEmpty() || brands.contains(product.brand)

            if (nameMatches && categoryMatches && brandMatches) {
                filteredMedicaments.add(product)
            }
        }
        productAdapter.notifyDataSetChanged()
    }

    override fun onFilterApplied(selectedCategories: List<String>, selectedBrands: List<String>) {
        this.selectedCategories = selectedCategories
        this.selectedBrands = selectedBrands
        filterMedicaments(searchEditText.text.toString(), selectedCategories, selectedBrands)
    }
}
