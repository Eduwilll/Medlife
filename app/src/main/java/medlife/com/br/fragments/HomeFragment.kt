package medlife.com.br.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseUser
import medlife.com.br.R
import medlife.com.br.activity.HomeActivity
import medlife.com.br.adapter.CategoryAdapter
import medlife.com.br.adapter.ProductAdapter
import medlife.com.br.helper.UserLocationManager
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.model.Category
import medlife.com.br.model.Farmacia
import medlife.com.br.model.Product

class HomeFragment : Fragment() {

    private lateinit var locationText: TextView
    private lateinit var searchBar: CardView
    private lateinit var medicamentosRecycler: RecyclerView
    private lateinit var subcategoriesRecycler: RecyclerView
    private lateinit var bestSellingRecycler: RecyclerView
    private lateinit var exclusiveOffersRecycler: RecyclerView
    private var usuarioAtual: FirebaseUser? = null
    private lateinit var locationManager: UserLocationManager

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        locationText = view.findViewById(R.id.locationText)
        searchBar = view.findViewById(R.id.searchBar)
        subcategoriesRecycler = view.findViewById(R.id.subcategoriesRecycler)
        medicamentosRecycler = view.findViewById(R.id.medicamentosRecycler)
        bestSellingRecycler = view.findViewById(R.id.bestSellingRecycler)
        exclusiveOffersRecycler = view.findViewById(R.id.exclusiveOffersRecycler)

        usuarioAtual = UsuarioFirebase.usuarioAtual

        locationManager = if (activity is HomeActivity) {
            (activity as HomeActivity).locationManager
        } else {
            UserLocationManager(requireContext())
        }

        locationManager.setLocationCallback(object : UserLocationManager.LocationCallback {
            override fun onLocationReceived(address: String) {
                if (::locationText.isInitialized) {
                    locationText.text = address
                }
            }

            override fun onLocationError(error: String) {
                if (::locationText.isInitialized) {
                    locationText.text = "Erro ao obter localização"
                }
            }
        })

        setupLocation()
        setupSearchBar()
        setupLocationBar(view)

        setupMedicamentosRecycler()
        setupSubcategoriesRecycler()
        setupBestSellingRecycler()
        setupExclusiveOffersRecycler()

        return view
    }

    private fun setupLocation() {
        if (checkLocationPermission()) {
            locationText.text = "Carregando localização..."
            locationManager.getBestAvailableLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocation()
            } else {
                locationText.text = "Adicione seu endereço"
            }
        }
    }

    private fun setupSearchBar() {
        searchBar.setOnClickListener {
            val searchFragment = SearchFragment()
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.contentFrame, searchFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    private fun setupLocationBar(view: View) {
        val locationBar: View = view.findViewById(R.id.locationBar)
        locationBar.setOnClickListener {
            showLocationDialog()
        }
    }

    private fun showLocationDialog() {
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Selecionar Localização")

        val locations = arrayOf("São Paulo, SP", "Campinas, SP", "Rio de Janeiro, RJ", "Belo Horizonte, MG", "Salvador, BA")

        builder.setItems(locations) { dialog, which ->
            val selectedLocation = locations[which]
            locationText.text = selectedLocation

            val parts = selectedLocation.split(", ")
            if (parts.size >= 2) {
                val city = parts[0]
                val state = parts[1]

                locationManager.saveAddress("", city, state)
                locationManager.saveLastLocationToFirestore("", city, state, 0.0, 0.0)
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }

    private fun setupSubcategoriesRecycler() {
        val categories = mutableListOf<Category>()
        categories.add(Category(R.drawable.mock_generico, "Antibióticos"))
        categories.add(Category(R.drawable.mock_medicamentogenerico, "Analgésicos"))
        categories.add(Category(R.drawable.mock_remedio, "Vitaminas"))
        categories.add(Category(R.drawable.mock_banho, "Higiene"))
        categories.add(Category(R.drawable.mock_melagriao, "Xaropes"))
        categories.add(Category(R.drawable.mock_medicamentogenerico, "Genéricos"))

        val adapter = CategoryAdapter(context, categories)
        subcategoriesRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        subcategoriesRecycler.adapter = adapter
    }

    private fun setupMedicamentosRecycler() {
        val farmacia1 = Farmacia("1", "Drogaria São Paulo", "Cidade Campinas")
        val farmacia2 = Farmacia("2", "Drogasil", "Cidade Americana")
        setupMedicamentosRecycler(farmacia1, farmacia2)
    }

    private fun setupMedicamentosRecycler(farmacia1: Farmacia, farmacia2: Farmacia) {
        val allSellingProducts = mutableListOf<Product>()
        allSellingProducts.add(Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99", "Antidepressivos", "PFIZER", Product.TARJA_PRETA, farmacia1))
        allSellingProducts.add(Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79", "Fitoterápico", "EMS", Product.TARJA_SEM_TARJA, farmacia2))
        allSellingProducts.add(Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia1))
        allSellingProducts.add(Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90", "Fitoterápico", "NOVATIS", Product.TARJA_AMARELA, farmacia2))

        val adapter = ProductAdapter(context, allSellingProducts)
        medicamentosRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        medicamentosRecycler.adapter = adapter
    }

    private fun setupBestSellingRecycler() {
        val farmacia1 = Farmacia("1", "Drogaria São Paulo", "Cidade Campinas")
        val farmacia2 = Farmacia("2", "Drogasil", "Cidade Americana")
        setupBestSellingRecycler(farmacia1, farmacia2)
    }

    private fun setupBestSellingRecycler(farmacia1: Farmacia, farmacia2: Farmacia) {
        val bestSellingProducts = mutableListOf<Product>()
        bestSellingProducts.add(Product(R.drawable.mock_invegasustena, "INVEGA SUSTENNA", "100mg", "R$1794.99", "Antidepressivos", "PFIZER", Product.TARJA_PRETA, farmacia1))
        bestSellingProducts.add(Product(R.drawable.mock_nervocalm, "NERVOCALM", "250mg, 20 Comprimidos", "R$45.79", "Fitoterápico", "EMS", Product.TARJA_SEM_TARJA, farmacia2))
        bestSellingProducts.add(Product(R.drawable.mock_johnsonssaboneteliquido, "Sabonete Líquido Johnson's", "Hora do Sono Frasco 200 ml", "R$14.90", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia1))
        bestSellingProducts.add(Product(R.drawable.mock_febreedor, "Ácido Acetilsalicílico", "100mg, 30 Comprimidos", "R$5.90", "Fitoterápico", "NOVATIS", Product.TARJA_AMARELA, farmacia2))

        val adapter = ProductAdapter(context, bestSellingProducts)
        bestSellingRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        bestSellingRecycler.adapter = adapter
    }

    private fun setupExclusiveOffersRecycler() {
        val farmacia1 = Farmacia("1", "Drogaria São Paulo", "Cidade Campinas")
        val farmacia2 = Farmacia("2", "Drogasil", "Cidade Americana")
        setupExclusiveOffersRecycler(farmacia1, farmacia2)
    }

    private fun setupExclusiveOffersRecycler(farmacia1: Farmacia, farmacia2: Farmacia) {
        val offers = mutableListOf<Product>()
        offers.add(Product(R.drawable.mock_medicamentogenerico, "Genérico Dipirona", "500mg, 20 Comprimidos", "R$7.99", "Vitaminas", "EMS", Product.TARJA_AMARELA, farmacia2))
        offers.add(Product(R.drawable.mock_melagriao, "Xarope Melagrião", "120ml", "R$19.90", "Fitoterápico", "PFIZER", Product.TARJA_SEM_TARJA, farmacia1))
        offers.add(Product(R.drawable.mock_protexbaby, "Shampoo Anticaspa", "200ml", "R$22.50", "Perfumes", "EUROFARMA", Product.TARJA_SEM_TARJA, farmacia2))
        offers.add(Product(R.drawable.mock_banho, "Higiene Pessoal Kit", "Sabonete + Shampoo", "R$29.90", "Perfumes", "NOVATIS", Product.TARJA_SEM_TARJA, farmacia1))

        val adapter = ProductAdapter(context, offers)
        exclusiveOffersRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        exclusiveOffersRecycler.adapter = adapter
    }
}
