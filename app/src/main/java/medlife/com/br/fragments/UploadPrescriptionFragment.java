package medlife.com.br.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.firestore.FirebaseFirestore;
import medlife.com.br.R;
import medlife.com.br.helper.UsuarioFirebase;
import java.util.List;

public class UploadPrescriptionFragment extends Fragment {
    private Button btnUploadDigital;
    private Button btnUploadScan;
    private Button btnUploadQr;
    private Button btnUploadPhoto;
    private Button btnBack;
    
    private FirebaseFirestore db;
    private String scannedPrescriptionId = null;
    private boolean isScanning = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_prescription, container, false);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        
        btnUploadDigital = view.findViewById(R.id.btnUploadDigital);
        btnUploadScan = view.findViewById(R.id.btnUploadScan);
        btnUploadQr = view.findViewById(R.id.btnUploadQr);
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto);
        btnBack = view.findViewById(R.id.btnBack);

        btnUploadDigital.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Upload digital - A ser implementado", Toast.LENGTH_SHORT).show();
        });
        
        btnUploadScan.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Upload scan - A ser implementado", Toast.LENGTH_SHORT).show();
        });
        
        btnUploadQr.setOnClickListener(v -> {
            startQRCodeScanner();
        });
        
        btnUploadPhoto.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Upload photo - A ser implementado", Toast.LENGTH_SHORT).show();
        });
        
        btnBack.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        return view;
    }

    private void startQRCodeScanner() {
        if (isScanning) return;
        
        isScanning = true;
        
        try {
            // Create and show QR Scanner Fragment
            QRScannerFragment qrScannerFragment = new QRScannerFragment();
            qrScannerFragment.setOnQRCodeScannedListener(new QRScannerFragment.OnQRCodeScannedListener() {
                @Override
                public void onQRCodeScanned(String qrCodeData) {
                    handleScannedQRCode(qrCodeData);
                }
                
                @Override
                public void onManualInputRequested() {
                    // Go back to this fragment and show manual input
                    requireActivity().onBackPressed();
                    showManualQRInput();
                }
            });
            
            // Replace current fragment with QR scanner
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.contentFrame, qrScannerFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            // Fallback to manual input if camera fails
            Toast.makeText(getContext(), "Erro ao abrir câmera. Usando entrada manual.", Toast.LENGTH_SHORT).show();
            showManualQRInput();
        }
    }

    private void showManualQRInput() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
        builder.setTitle("Inserir Código da Receita");
        builder.setMessage("Digite o código da receita (QR Code):");
        
        final android.widget.EditText input = new android.widget.EditText(requireContext());
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        
        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String qrCodeData = input.getText().toString().trim();
            if (!qrCodeData.isEmpty()) {
                handleScannedQRCode(qrCodeData);
            } else {
                Toast.makeText(getContext(), "Por favor, insira um código válido", Toast.LENGTH_SHORT).show();
                isScanning = false;
            }
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            dialog.cancel();
            isScanning = false;
        });
        
        builder.setOnCancelListener(dialog -> {
            isScanning = false;
        });
        
        builder.show();
    }

    private void handleScannedQRCode(String qrCodeData) {
        isScanning = false;
        
        // Sanitize the QR code data
        String sanitizedData = sanitizeQRCodeData(qrCodeData);
        
        // Validate QR code format
        if (sanitizedData != null && !sanitizedData.isEmpty()) {
            // Check if it's a URL (like the one that caused the error)
            if (sanitizedData.startsWith("http://") || sanitizedData.startsWith("https://")) {
                Toast.makeText(getContext(), "QR Code inválido: URL detectada. Use um código de receita válido.", Toast.LENGTH_LONG).show();
                return;
            }
            
            // Check if it's a valid UUID format (with or without dashes)
            if (isValidUUID(sanitizedData)) {
                scannedPrescriptionId = sanitizedData;
                
                // Save prescription to Firestore
                savePrescriptionToFirestore(sanitizedData);
                
                // Add prescription to CartFragment
                addPrescriptionToCart(sanitizedData);
                
                Toast.makeText(getContext(), "Receita escaneada com sucesso: " + sanitizedData, Toast.LENGTH_LONG).show();
                
                // Go back to cart
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(getContext(), "QR Code inválido. Formato UUID não reconhecido. Exemplo válido: d290f1ee-6c54-4b01-90e6-d701748f0851", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getContext(), "Erro ao ler QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidUUID(String uuid) {
        if (uuid == null) return false;
        
        // Remove dashes for validation
        String cleanUUID = uuid.replace("-", "");
        
        // UUID should be exactly 32 hexadecimal characters
        if (cleanUUID.length() != 32) return false;
        
        // Check if all characters are hexadecimal (0-9, a-f, A-F)
        return cleanUUID.matches("[0-9a-fA-F]{32}");
    }

    private String sanitizeQRCodeData(String qrCodeData) {
        if (qrCodeData == null) return null;
        
        // Remove leading/trailing whitespace
        String sanitized = qrCodeData.trim();
        
        // Remove common URL prefixes if present
        if (sanitized.startsWith("http://")) {
            sanitized = sanitized.substring(7);
        } else if (sanitized.startsWith("https://")) {
            sanitized = sanitized.substring(8);
        }
        
        // Remove www. prefix if present
        if (sanitized.startsWith("www.")) {
            sanitized = sanitized.substring(4);
        }
        
        return sanitized;
    }

    private void addPrescriptionToCart(String prescriptionId) {
        // Find CartFragment in the parent fragment manager
        if (getParentFragmentManager() != null) {
            List<androidx.fragment.app.Fragment> fragments = getParentFragmentManager().getFragments();
            for (androidx.fragment.app.Fragment fragment : fragments) {
                if (fragment instanceof CartFragment) {
                    CartFragment cartFragment = (CartFragment) fragment;
                    cartFragment.addPrescription(prescriptionId);
                    System.out.println("Prescription added to cart: " + prescriptionId);
                    break;
                }
            }
        }
    }

    private void savePrescriptionToFirestore(String prescriptionId) {
        String userId = UsuarioFirebase.getIdUsuario();
        if (userId == null) {
            Toast.makeText(getContext(), "Erro: Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create prescription data
        java.util.Map<String, Object> prescriptionData = new java.util.HashMap<>();
        prescriptionData.put("prescriptionId", prescriptionId);
        prescriptionData.put("userId", userId);
        prescriptionData.put("scanDate", com.google.firebase.Timestamp.now());
        prescriptionData.put("status", "pending");
        prescriptionData.put("type", "qr_code");

        // Generate a proper document ID instead of using the QR code data
        String documentId = db.collection("prescriptions").document().getId();

        // Save to Firestore
        db.collection("prescriptions")
                .document(documentId)
                .set(prescriptionData)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Prescription saved to Firestore: " + prescriptionId + " with document ID: " + documentId);
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error saving prescription: " + e.getMessage());
                    Toast.makeText(getContext(), "Erro ao salvar receita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
} 