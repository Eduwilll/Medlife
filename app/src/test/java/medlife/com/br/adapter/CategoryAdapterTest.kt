package medlife.com.br.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import medlife.com.br.R
import medlife.com.br.model.Category
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CategoryAdapterTest {
    @Mock lateinit var mockContext: Context
    @Mock lateinit var mockView: View
    @Mock lateinit var mockParent: ViewGroup
    @Mock lateinit var mockImageView: ImageView
    @Mock lateinit var mockTextView: TextView

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryList: MutableList<Category>
    private lateinit var category: Category

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        category = Category(123, "Medicamentos")
        category.name = "Medicamentos"
        category.imageResId = 123

        categoryList = mutableListOf(category)
        categoryAdapter = CategoryAdapter(mockContext, categoryList)
    }

    @Test
    fun testConstructor() {
        assertNotNull(categoryAdapter)
        assertEquals(1, categoryAdapter.itemCount)
    }

    @Test
    fun testGetItemCount() {
        assertEquals(1, categoryAdapter.itemCount)
    }

    @Test
    fun testGetItemCount_EmptyList() {
        val emptyAdapter = CategoryAdapter(mockContext, mutableListOf())
        assertEquals(0, emptyAdapter.itemCount)
    }

    @Test
    fun testCategoryViewHolderConstructor() {
        `when`(mockView.findViewById<ImageView>(R.id.imageSubcategory)).thenReturn(mockImageView)
        `when`(mockView.findViewById<TextView>(R.id.textSubcategory)).thenReturn(mockTextView)

        val holder = CategoryAdapter.CategoryViewHolder(mockView)

        assertNotNull(holder.imageSubcategory)
        assertNotNull(holder.textSubcategory)
    }
}
