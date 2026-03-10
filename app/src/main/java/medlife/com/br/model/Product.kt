package medlife.com.br.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var image: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var price: String? = null,
    var category: String? = null,
    var brand: String? = null,
    var tarja: String? = null,
    var farmacia: Farmacia? = null
) : Parcelable {
    companion object {
        const val TARJA_SEM_TARJA = "SEM_TARJA"
        const val TARJA_AMARELA = "AMARELA"
        const val TARJA_VERMELHA_SEM_RETENCAO = "VERMELHA_SEM_RETENCAO"
        const val TARJA_VERMELHA_COM_RETENCAO = "VERMELHA_COM_RETENCAO"
        const val TARJA_PRETA = "PRETA"
    }
}
