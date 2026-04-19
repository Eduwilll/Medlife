package medlife.com.br.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import medlife.com.br.R
import medlife.com.br.model.Product
import medlife.com.br.model.Category
import medlife.com.br.model.Farmacia

class ProductRepository {

    // Mock data for initial implementation, will be replaced with Firebase/API calls
    private val mockProducts = listOf(
        Product(R.drawable.mock_generico, "Aspirina", "ácido acetilsalicílico 500 mg", "16.97", "Medicamentos", "Bayer", Product.TARJA_SEM_TARJA, Farmacia("f1", "Farmácia 1", "Cidade")),
        Product(R.drawable.mock_medicamentogenerico, "Amoxicilina", "Antibiótico 500mg", "45.50", "Antibióticos", "Genérico", Product.TARJA_VERMELHA_SEM_RETENCAO, Farmacia("f1", "Farmácia 1", "Cidade")),
        Product(R.drawable.mock_remedio, "Vitamina C", "Suplemento vitamínico", "12.90", "Suplementos", "Sundown", Product.TARJA_SEM_TARJA, Farmacia("f2", "Farmácia 2", "Cidade")),
        Product(R.drawable.mock_febreedor, "Doralgina", "Analgésico e antitérmico", "22.30", "Medicamentos", "Neo Química", Product.TARJA_SEM_TARJA, Farmacia("f2", "Farmácia 2", "Cidade"))
    )

    private val mockCategories = listOf(
        Category(R.drawable.mock_generico, "Medicamentos"),
        Category(R.drawable.mock_medicamentogenerico, "Antibióticos"),
        Category(R.drawable.mock_remedio, "Suplementos"),
        Category(R.drawable.mock_banho, "Higiene"),
        Category(R.drawable.mock_melagriao, "Beleza")
    )

    fun getProducts(): Flow<List<Product>> = flow {
        // Simulate network delay
        kotlinx.coroutines.delay(1000)
        emit(mockProducts)
    }

    fun getCategories(): Flow<List<Category>> = flow {
        emit(mockCategories)
    }

    fun getProductsByCategory(categoryName: String): Flow<List<Product>> = flow {
        emit(mockProducts.filter { it.category == categoryName })
    }
}
