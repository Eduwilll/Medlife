package medlife.com.br.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.activity.AutenticacaoActivity
import medlife.com.br.fragments.profile.*
import medlife.com.br.helper.ConfiguracaoFirebase
import medlife.com.br.helper.UsuarioFirebase

class ProfileFragment : Fragment() {

    private lateinit var imageProfile: ImageView
    private lateinit var textName: TextView
    private lateinit var textEmail: TextView
    private lateinit var menuPedidos: View
    private lateinit var menuMeusDados: View
    private lateinit var menuEnderecos: View
    private lateinit var menuCarteira: View
    private lateinit var menuCupons: View
    private lateinit var menuNotificacoes: View
    private lateinit var menuAjuda: View
    private lateinit var menuSobre: View
    private lateinit var db: FirebaseFirestore
    private var usuarioAtual: FirebaseUser? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        db = FirebaseFirestore.getInstance()

        imageProfile = view.findViewById(R.id.imageProfile)
        textName = view.findViewById(R.id.textName)
        textEmail = view.findViewById(R.id.textEmail)

        menuPedidos = view.findViewById(R.id.menuPedidos)
        menuMeusDados = view.findViewById(R.id.menuMeusDados)
        menuEnderecos = view.findViewById(R.id.menuEnderecos)
        menuCarteira = view.findViewById(R.id.menuCarteira)
        menuCupons = view.findViewById(R.id.menuCupons)
        menuNotificacoes = view.findViewById(R.id.menuNotificacoes)
        menuAjuda = view.findViewById(R.id.menuAjuda)
        menuSobre = view.findViewById(R.id.menuSobre)

        setupClickListeners()

        usuarioAtual = UsuarioFirebase.usuarioAtual

        if (usuarioAtual != null && usuarioAtual?.displayName != null) {
            textName.text = usuarioAtual?.displayName
            textEmail.text = usuarioAtual?.email
        }

        return view
    }

    private fun setupClickListeners() {
        menuPedidos.setOnClickListener { replaceFragment(ProfileOrdersFragment()) }
        menuMeusDados.setOnClickListener { replaceFragment(ProfileUserDataFragment()) }
        menuEnderecos.setOnClickListener { replaceFragment(ProfileAddressesFragment()) }
        menuCarteira.setOnClickListener { replaceFragment(ProfileWalletFragment()) }
        menuCupons.setOnClickListener { replaceFragment(ProfileCouponsFragment()) }
        menuNotificacoes.setOnClickListener { replaceFragment(ProfileNotificationsFragment()) }
        menuAjuda.setOnClickListener { replaceFragment(ProfileHelpFragment()) }
        menuSobre.setOnClickListener { replaceFragment(ProfileAboutFragment()) }
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.contentFrame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sairButton: View? = view.findViewById(R.id.sairButton)
        sairButton?.setOnClickListener {
            ConfiguracaoFirebase.firebaseAutenticacao.signOut()
            val intent = Intent(activity, AutenticacaoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
