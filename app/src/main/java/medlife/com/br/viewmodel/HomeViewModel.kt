package medlife.com.br.viewmodel

import androidx.lifecycle.ViewModel
import medlife.com.br.helper.CartManager

class HomeViewModel : ViewModel() {

    fun getCartItemCount(): Int {
        return CartManager.getInstance().cartItems.size
    }
}
