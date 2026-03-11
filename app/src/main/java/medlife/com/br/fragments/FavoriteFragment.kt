package medlife.com.br.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import medlife.com.br.R
import medlife.com.br.activity.ProductDetailActivity
import medlife.com.br.adapter.ProductAdapter
import medlife.com.br.model.Farmacia
import medlife.com.br.model.Product

class FavoriteFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.favoriteRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val farmacia1 = Farmacia("1", "Drogaria São Paulo", "Cidade Campinas")
        val farmacia2 = Farmacia("2", "Drogasil", "Cidade Americana")

        val allProducts = mutableListOf<Product>().apply {
            add(Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99", "Antidepressivos", "PFIZER", Product.TARJA_PRETA, farmacia1))
            add(Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79", "Fitoterápico", "EMS", Product.TARJA_SEM_TARJA, farmacia2))
            add(Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia1))
            add(Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90", "Fitoterápico", "NOVATIS", Product.TARJA_AMARELA, farmacia2))
            add(Product(R.drawable.mock_medicamentogenerico, "Genérico Dipirona", "500mg, 20 Comprimidos", "R$7.99", "Vitaminas", "EMS", Product.TARJA_AMARELA, farmacia2))
            add(Product(R.drawable.mock_melagriao, "Xarope Melagrião", "120ml", "R$19.90", "Fitoterápico", "PFIZER", Product.TARJA_SEM_TARJA, farmacia1))
            add(Product(R.drawable.mock_protexbaby, "Shampoo Anticaspa", "200ml", "R$22.50", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia2))
            add(Product(R.drawable.mock_banho, "Higiene Pessoal Kit", "Sabonete + Shampoo", "R$29.90", "Perfumes", "NOVATIS", Product.TARJA_SEM_TARJA, farmacia1))
        }

        val prefs: SharedPreferences? = context?.getSharedPreferences("favorites", Context.MODE_PRIVATE)
        val favoriteProducts = mutableListOf<Product>()
        
        allProducts.forEach { p ->
            if (prefs?.getBoolean(p.name, false) == true) {
                favoriteProducts.add(p)
            }
        }

        val adapter = ProductAdapter(context, favoriteProducts, true)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener {
            override fun onItemClick(product: Product) {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }
        })

        val emptyLayout: LinearLayout = view.findViewById(R.id.emptyFavoriteLayout)
        if (favoriteProducts.isEmpty()) {
            emptyLayout.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyLayout.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
        return view
    }
}
