package medlife.com.br.model

import java.io.Serializable

data class Farmacia(
    var id: String? = null,
    var name: String? = null,
    var location: String? = null
) : Serializable
