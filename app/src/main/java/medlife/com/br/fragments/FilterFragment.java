package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.ArrayList;
import java.util.List;
import medlife.com.br.R;

public class FilterFragment extends DialogFragment {

    public interface FilterListener {
        void onFilterApplied(List<String> selectedCategories, List<String> selectedBrands);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        ImageView closeButton = view.findViewById(R.id.closeButton);
        Button applyButton = view.findViewById(R.id.applyButton);

        // Category Checkboxes
        CheckBox cbFitoterapico = view.findViewById(R.id.cb_fitoterapico);
        CheckBox cbAntidepressivos = view.findViewById(R.id.cb_antidepressivos);
        CheckBox cbVitaminas = view.findViewById(R.id.cb_vitaminas);
        CheckBox cbPerfumes = view.findViewById(R.id.cb_perfumes);

        // Brand Checkboxes
        CheckBox cbEms = view.findViewById(R.id.cb_ems);
        CheckBox cbPfizer = view.findViewById(R.id.cb_pfizer);
        CheckBox cbNovatis = view.findViewById(R.id.cb_novatis);
        CheckBox cbEurofarma = view.findViewById(R.id.cb_eurofarma);

        closeButton.setOnClickListener(v -> dismiss());
        applyButton.setOnClickListener(v -> {
            List<String> selectedCategories = new ArrayList<>();
            if (cbFitoterapico.isChecked()) selectedCategories.add("Fitoterápico");
            if (cbAntidepressivos.isChecked()) selectedCategories.add("Antidepressivos");
            if (cbVitaminas.isChecked()) selectedCategories.add("Vitaminas");
            if (cbPerfumes.isChecked()) selectedCategories.add("Perfumes");

            List<String> selectedBrands = new ArrayList<>();
            if (cbEms.isChecked()) selectedBrands.add("EMS");
            if (cbPfizer.isChecked()) selectedBrands.add("PFIZER");
            if (cbNovatis.isChecked()) selectedBrands.add("NOVATIS");
            if (cbEurofarma.isChecked()) selectedBrands.add("EUROFARMA");

            if (listener != null) {
                listener.onFilterApplied(selectedCategories, selectedBrands);
            }
            dismiss();
        });

        return view;
    }
} 