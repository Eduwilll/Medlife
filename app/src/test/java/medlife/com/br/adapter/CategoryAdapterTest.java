package medlife.com.br.adapter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import medlife.com.br.R;
import medlife.com.br.model.Category;
import java.util.ArrayList;
import java.util.List;

public class CategoryAdapterTest {
    @Mock
    Context mockContext;
    @Mock
    View mockView;
    @Mock
    ViewGroup mockParent;
    @Mock
    ImageView mockImageView;
    @Mock
    TextView mockTextView;

    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private Category category;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        // Setup test data
        category = new Category(123, "Medicamentos");
        category.setName("Medicamentos");
        category.setImageResId(123);
        
        categoryList = new ArrayList<>();
        categoryList.add(category);
        
        categoryAdapter = new CategoryAdapter(mockContext, categoryList);
    }

    @Test
    public void testConstructor() {
        assertNotNull(categoryAdapter);
        assertEquals(1, categoryAdapter.getItemCount());
    }

    @Test
    public void testGetItemCount() {
        assertEquals(1, categoryAdapter.getItemCount());
    }

    @Test
    public void testGetItemCount_EmptyList() {
        CategoryAdapter emptyAdapter = new CategoryAdapter(mockContext, new ArrayList<>());
        assertEquals(0, emptyAdapter.getItemCount());
    }

    @Test
    public void testCategoryViewHolderConstructor() {
        when(mockView.findViewById(R.id.imageSubcategory)).thenReturn(mockImageView);
        when(mockView.findViewById(R.id.textSubcategory)).thenReturn(mockTextView);

        CategoryAdapter.CategoryViewHolder holder = new CategoryAdapter.CategoryViewHolder(mockView);
        
        assertNotNull(holder.imageSubcategory);
        assertNotNull(holder.textSubcategory);
    }
} 