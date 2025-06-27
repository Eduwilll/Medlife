package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import medlife.com.br.R;

public class UploadPrescriptionFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_prescription, container, false);

        Button btnUploadDigital = view.findViewById(R.id.btnUploadDigital);
        Button btnUploadScan = view.findViewById(R.id.btnUploadScan);
        Button btnUploadQr = view.findViewById(R.id.btnUploadQr);
        Button btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        Button btnBack = view.findViewById(R.id.btnBack);

        btnUploadDigital.setOnClickListener(v -> {
            // TODO: Implement upload digital
        });
        btnUploadScan.setOnClickListener(v -> {
            // TODO: Implement upload scan
        });
        btnUploadQr.setOnClickListener(v -> {
            // TODO: Implement upload QR
        });
        btnUploadPhoto.setOnClickListener(v -> {
            // TODO: Implement upload photo
        });
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }
} 