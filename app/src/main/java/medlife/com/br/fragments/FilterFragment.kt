package medlife.com.br.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import medlife.com.br.R

class FilterFragment : DialogFragment() {

    interface FilterListener {
        fun onFilterApplied(selectedCategories: List<String>, selectedBrands: List<String>)
    }

    private var listener: FilterListener? = null

    fun setFilterListener(listener: FilterListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filter, container, false)

        val closeButton: ImageView = view.findViewById(R.id.closeButton)
        val applyButton: Button = view.findViewById(R.id.applyButton)

        // Category Checkboxes
        val cbFitoterapico: CheckBox = view.findViewById(R.id.cb_fitoterapico)
        val cbAntidepressivos: CheckBox = view.findViewById(R.id.cb_antidepressivos)
        val cbVitaminas: CheckBox = view.findViewById(R.id.cb_vitaminas)
        val cbPerfumes: CheckBox = view.findViewById(R.id.cb_perfumes)

        // Brand Checkboxes
        val cbEms: CheckBox = view.findViewById(R.id.cb_ems)
        val cbPfizer: CheckBox = view.findViewById(R.id.cb_pfizer)
        val cbNovatis: CheckBox = view.findViewById(R.id.cb_novatis)
        val cbEurofarma: CheckBox = view.findViewById(R.id.cb_eurofarma)

        closeButton.setOnClickListener { dismiss() }

        applyButton.setOnClickListener {
            val selectedCategories = mutableListOf<String>()
            if (cbFitoterapico.isChecked) selectedCategories.add("Fitoterápico")
            if (cbAntidepressivos.isChecked) selectedCategories.add("Antidepressivos")
            if (cbVitaminas.isChecked) selectedCategories.add("Vitaminas")
            if (cbPerfumes.isChecked) selectedCategories.add("Perfumes")

            val selectedBrands = mutableListOf<String>()
            if (cbEms.isChecked) selectedBrands.add("EMS")
            if (cbPfizer.isChecked) selectedBrands.add("PFIZER")
            if (cbNovatis.isChecked) selectedBrands.add("NOVATIS")
            if (cbEurofarma.isChecked) selectedBrands.add("EUROFARMA")

            listener?.onFilterApplied(selectedCategories, selectedBrands)
            dismiss()
        }

        return view
    }
}
