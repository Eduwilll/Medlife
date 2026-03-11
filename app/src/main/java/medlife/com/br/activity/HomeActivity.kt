package medlife.com.br.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import medlife.com.br.R
import medlife.com.br.fragments.*
import medlife.com.br.helper.CartManager
import medlife.com.br.helper.UserLocationManager
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.viewmodel.HomeViewModel

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    lateinit var locationManager: UserLocationManager
        private set
    private var cartBadge: BadgeDrawable? = null
    private lateinit var fabQuickCart: FloatingActionButton

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val userId = UsuarioFirebase.idUsuario
        userId?.let {
            UsuarioFirebase.atualizarLastLoginSeguro(it)
        }

        initializeLocationManager()
        setupBottomNavigation()
        setupCartBadge()
        setupFloatingActionButton()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun initializeLocationManager() {
        locationManager = UserLocationManager(this)

        locationManager.setLocationCallback(object : UserLocationManager.LocationCallback {
            override fun onLocationReceived(address: String) {
                // Location received
            }

            override fun onLocationError(error: String) {
                // Handle error
            }
        })
    }

    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.navigation_home -> HomeFragment()
                R.id.navigation_search -> SearchFragment()
                R.id.navigation_cart -> CartFragment()
                R.id.navigation_favorite -> FavoriteFragment()
                R.id.navigation_profile -> ProfileFragment()
                else -> return@setOnItemSelectedListener false
            }

            loadFragment(fragment)
        }

        bottomNavigationView.selectedItemId = R.id.navigation_home
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.contentFrame, fragment)
            .commit()
        return true
    }

    private fun setupCartBadge() {
        cartBadge = bottomNavigationView.getOrCreateBadge(R.id.navigation_cart)
        updateCartBadge()
    }

    fun updateCartBadge() {
        val cartItemCount = homeViewModel.getCartItemCount()
        cartBadge?.let {
            if (cartItemCount > 0) {
                it.isVisible = true
                it.number = cartItemCount
            } else {
                it.isVisible = false
            }
        }
        updateFABVisibility()
    }

    private fun setupFloatingActionButton() {
        fabQuickCart = findViewById(R.id.fabQuickCart)
        fabQuickCart.setOnClickListener {
            loadFragment(CartFragment())
            bottomNavigationView.selectedItemId = R.id.navigation_cart
        }
        updateFABVisibility()
    }

    private fun updateFABVisibility() {
        val cartItemCount = homeViewModel.getCartItemCount()
        if (::fabQuickCart.isInitialized) {
            fabQuickCart.visibility = if (cartItemCount > 0) View.VISIBLE else View.GONE
        }
    }
}
