package medlife.com.br.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.helper.UsuarioFirebase

class UploadPrescriptionFragment : Fragment() {
    private lateinit var btnUploadDigital: Button
    private lateinit var btnUploadScan: Button
    private lateinit var btnUploadQr: Button
    private lateinit var btnUploadPhoto: Button
    private lateinit var btnBack: Button

    private lateinit var db: FirebaseFirestore
    private var scannedPrescriptionId: String? = null
    private var isScanning = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upload_prescription, container, false)

        db = FirebaseFirestore.getInstance()

        btnUploadDigital = view.findViewById(R.id.btnUploadDigital)
        btnUploadScan = view.findViewById(R.id.btnUploadScan)
        btnUploadQr = view.findViewById(R.id.btnUploadQr)
        btnUploadPhoto = view.findViewById(R.id.btnUploadPhoto)
        btnBack = view.findViewById(R.id.btnBack)

        btnUploadDigital.setOnClickListener {
            Toast.makeText(context, "Upload digital - A ser implementado", Toast.LENGTH_SHORT).show()
        }

        btnUploadScan.setOnClickListener {
            Toast.makeText(context, "Upload scan - A ser implementado", Toast.LENGTH_SHORT).show()
        }

        btnUploadQr.setOnClickListener {
            startQRCodeScanner()
        }

        btnUploadPhoto.setOnClickListener {
            Toast.makeText(context, "Upload photo - A ser implementado", Toast.LENGTH_SHORT).show()
        }

        btnBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun startQRCodeScanner() {
        if (isScanning) return

        isScanning = true

        try {
            val qrScannerFragment = QRScannerFragment()
            qrScannerFragment.setOnQRCodeScannedListener(object : QRScannerFragment.OnQRCodeScannedListener {
                override fun onQRCodeScanned(qrCodeData: String) {
                    handleScannedQRCode(qrCodeData)
                }

                override fun onManualInputRequested() {
                    requireActivity().onBackPressed()
                    showManualQRInput()
                }
            })

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.contentFrame, qrScannerFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao abrir câmera. Usando entrada manual.", Toast.LENGTH_SHORT).show()
            showManualQRInput()
        }
    }

    private fun showManualQRInput() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Inserir Código da Receita")
        builder.setMessage("Digite o código da receita (QR Code):")

        val input = EditText(requireContext())
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("Confirmar") { dialog, _ ->
            val qrCodeData = input.text.toString().trim()
            if (qrCodeData.isNotEmpty()) {
                handleScannedQRCode(qrCodeData)
            } else {
                Toast.makeText(context, "Por favor, insira um código válido", Toast.LENGTH_SHORT).show()
                isScanning = false
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
            isScanning = false
        }

        builder.setOnCancelListener {
            isScanning = false
        }

        builder.show()
    }

    private fun handleScannedQRCode(qrCodeData: String) {
        isScanning = false

        val sanitizedData = sanitizeQRCodeData(qrCodeData)

        if (!sanitizedData.isNullOrEmpty()) {
            if (sanitizedData.startsWith("http://") || sanitizedData.startsWith("https://")) {
                Toast.makeText(context, "QR Code inválido: URL detectada. Use um código de receita válido.", Toast.LENGTH_LONG).show()
                return
            }

            if (isValidUUID(sanitizedData)) {
                scannedPrescriptionId = sanitizedData

                savePrescriptionToFirestore(sanitizedData)
                addPrescriptionToCart(sanitizedData)

                Toast.makeText(context, "Receita escaneada com sucesso: $sanitizedData", Toast.LENGTH_LONG).show()
                requireActivity().onBackPressed()
            } else {
                Toast.makeText(
                    context,
                    "QR Code inválido. Formato UUID não reconhecido. Exemplo válido: d290f1ee-6c54-4b01-90e6-d701748f0851",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(context, "Erro ao ler QR Code", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidUUID(uuid: String?): Boolean {
        if (uuid == null) return false

        val cleanUUID = uuid.replace("-", "")

        if (cleanUUID.length != 32) return false

        return cleanUUID.matches("[0-9a-fA-F]{32}".toRegex())
    }

    private fun sanitizeQRCodeData(qrCodeData: String?): String? {
        if (qrCodeData == null) return null

        var sanitized = qrCodeData.trim()

        if (sanitized.startsWith("http://")) {
            sanitized = sanitized.substring(7)
        } else if (sanitized.startsWith("https://")) {
            sanitized = sanitized.substring(8)
        }

        if (sanitized.startsWith("www.")) {
            sanitized = sanitized.substring(4)
        }

        return sanitized
    }

    private fun addPrescriptionToCart(prescriptionId: String) {
        val parentFragmentManager = parentFragmentManager
        val fragments = parentFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is CartFragment) {
                fragment.addPrescription(prescriptionId)
                println("Prescription added to cart: $prescriptionId")
                break
            }
        }
    }

    private fun savePrescriptionToFirestore(prescriptionId: String) {
        val userId = UsuarioFirebase.idUsuario
        if (userId == null) {
            Toast.makeText(context, "Erro: Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val prescriptionData = hashMapOf<String, Any>(
            "prescriptionId" to prescriptionId,
            "userId" to userId,
            "scanDate" to Timestamp.now(),
            "status" to "pending",
            "type" to "qr_code"
        )

        val documentId = db.collection("prescriptions").document().id

        db.collection("prescriptions")
            .document(documentId)
            .set(prescriptionData)
            .addOnSuccessListener {
                println("Prescription saved to Firestore: $prescriptionId with document ID: $documentId")
            }
            .addOnFailureListener { e ->
                println("Error saving prescription: ${e.message}")
                Toast.makeText(context, "Erro ao salvar receita: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
