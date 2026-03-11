package medlife.com.br.fragments.profile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import medlife.com.br.R
import medlife.com.br.helper.OrderManager
import medlife.com.br.helper.UsuarioFirebase
import medlife.com.br.model.Order
import java.text.NumberFormat
import java.util.Locale

class ProfileOrdersFragment : Fragment() {
    private lateinit var recyclerOrders: RecyclerView
    private lateinit var textEmptyOrders: TextView
    private lateinit var db: FirebaseFirestore
    private lateinit var orderAdapter: OrderAdapter
    private var ordersList: MutableList<Order> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_orders, container, false)

        db = FirebaseFirestore.getInstance()

        recyclerOrders = view.findViewById(R.id.recyclerOrders)
        textEmptyOrders = view.findViewById(R.id.textEmptyOrders)

        recyclerOrders.layoutManager = LinearLayoutManager(context)
        orderAdapter = OrderAdapter(ordersList)
        recyclerOrders.adapter = orderAdapter

        loadOrders()

        return view
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadOrders() {
        val userId = UsuarioFirebase.idUsuario
        if (userId != null) {
            OrderManager.getUserOrders(userId)
                .addOnSuccessListener { queryDocumentSnapshots ->
                    ordersList.clear()
                    for (document in queryDocumentSnapshots) {
                        val order = document.toObject(Order::class.java)
                        ordersList.add(order)
                    }

                    if (ordersList.isEmpty()) {
                        recyclerOrders.visibility = View.GONE
                        textEmptyOrders.visibility = View.VISIBLE
                    } else {
                        recyclerOrders.visibility = View.VISIBLE
                        textEmptyOrders.visibility = View.GONE
                        orderAdapter.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Erro ao carregar pedidos: ${e.message}", Toast.LENGTH_SHORT).show()
                    recyclerOrders.visibility = View.GONE
                    textEmptyOrders.visibility = View.VISIBLE
                }
        }
    }

    private inner class OrderAdapter(private val orders: List<Order>) :
        RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
            return OrderViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
            val order = orders[position]
            holder.bind(order)
        }

        override fun getItemCount(): Int {
            return orders.size
        }

        inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textOrderId: TextView = itemView.findViewById(R.id.textOrderId)
            private val textOrderDate: TextView = itemView.findViewById(R.id.textOrderDate)
            private val textOrderStatus: TextView = itemView.findViewById(R.id.textOrderStatus)
            private val textOrderTotal: TextView = itemView.findViewById(R.id.textOrderTotal)
            private val textDeliveryOption: TextView = itemView.findViewById(R.id.textDeliveryOption)
            private val textPaymentStatus: TextView = itemView.findViewById(R.id.textPaymentStatus)
            private val textOrderItemsPreview: TextView = itemView.findViewById(R.id.textOrderItemsPreview)

            @SuppressLint("SetTextI18n")
            fun bind(order: Order) {
                val shortOrderId = order.orderId?.take(8)?.uppercase() ?: ""
                textOrderId.text = "Pedido #$shortOrderId"

                if (order.createdAt != null) {
                    val date = order.createdAt!!.toDate().toString()
                    textOrderDate.text = date
                }

                textOrderStatus.text = order.status

                val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                textOrderTotal.text = format.format(order.totalPrice)

                var deliveryText = ""
                when (order.deliveryOption) {
                    "immediate" -> deliveryText = "Entrega Imediata"
                    "pickup" -> deliveryText = "Retirar na Loja"
                    "scheduled" -> deliveryText = "Entrega Agendada"
                }
                textDeliveryOption.text = deliveryText

                textPaymentStatus.text = order.paymentStatus

                val itemCount = order.items?.size ?: 0
                val plural = if (itemCount != 1) "s" else ""
                textOrderItemsPreview.text = "$itemCount item$plural"
            }
        }
    }
}
