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
import medlife.com.br.R;

public class FilterFragment extends DialogFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        ImageView closeButton = view.findViewById(R.id.closeButton);
        Button applyButton = view.findViewById(R.id.applyButton);

        closeButton.setOnClickListener(v -> dismiss());
        applyButton.setOnClickListener(v -> dismiss());

        // You can collect selected filters here and return to SearchFragment if needed
        // For now, just dismiss on apply

        return view;
    }
} 