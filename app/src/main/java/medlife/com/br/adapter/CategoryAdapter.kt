package medlife.com.br.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import medlife.com.br.R
import medlife.com.br.model.Category

class CategoryAdapter(
    private val context: Context?,
    private val categoryList: List<Category>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_subcategory, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.imageSubcategory.setImageResource(category.imageResId)
        holder.textSubcategory.text = category.name
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageSubcategory: ImageView = itemView.findViewById(R.id.imageSubcategory)
        val textSubcategory: TextView = itemView.findViewById(R.id.textSubcategory)
    }
}
