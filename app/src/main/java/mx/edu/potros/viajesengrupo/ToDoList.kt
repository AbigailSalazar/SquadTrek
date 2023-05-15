package mx.edu.potros.viajesengrupo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ToDoList : AppCompatActivity() {
    private val userRef = FirebaseDatabase.getInstance().getReference("Usuarios")
    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    private var contador: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)

        var viajeId = intent.getStringExtra("viajeKey")

        // Empieza :D
        val btnNavAdd = findViewById<ImageButton>(R.id.btnNavAdd)
        btnNavAdd.setOnClickListener {
            val intent = Intent(this, SeleccionarUbicacion::class.java)
            startActivity(intent)
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.btnViajes -> {
                    val intent = Intent(this, MisViajes::class.java)
                    startActivity(intent)
                    true
                }
                R.id.btnHome -> {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    true
                }
                R.id.btnUser -> {
                    val intent = Intent(this, Perfil::class.java)
                    startActivity(intent)
                    true
                }
                R.id.btnAmigos -> {
                    val intent = Intent(this, Amigos::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

        var btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        var btnMessages = findViewById<ImageView>(R.id.btnMessages)
        var btnNotifications = findViewById<ImageView>(R.id.btnNotifications)
        btnMessages.setOnClickListener {
            var intent = Intent(this, Inbox::class.java)
            startActivity(intent)
        }
        btnNotifications.setOnClickListener {
            var intent = Intent(this, Notificaciones::class.java)
            startActivity(intent)
        }
        // termina :D

        var toDoListRef = userRef.child(usuarioId!!)
            .child("viajesEnProceso")
            .child(viajeId!!)
            .child("toDoList")

        toDoListRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                contador = snapshot.childrenCount
                val toDoListContainer = findViewById<LinearLayout>(R.id.toDoListLy)
                toDoListContainer.removeAllViews() // Remover vistas anteriores
                for (snapshotChild in snapshot.children) {
                    var toDoListItem = snapshotChild.getValue(Listtodo::class.java) ?: ""
                    Log.d("TAG", "toDoListItem: $toDoListItem")
                    var toDoListItemIsChecked = snapshotChild.child("isChecked").getValue(Boolean::class.java) ?: false

                    // Crear vista personalizada para el elemento de la lista
                    var toDoListItemView = LayoutInflater.from(this@ToDoList).inflate(
                        R.layout.to_do_list_view,
                        toDoListContainer,
                        false
                    )

                    // Configurar la vista personalizada
                    var toDoListItemTextView = toDoListItemView.findViewById<TextView>(R.id.to_do_list_tv)
                    toDoListItemTextView.text = toDoListItem.toString()
                    Log.d("TAG", "toDoListItemTextView.text: ${toDoListItemTextView.text}")

                    var radioButton = toDoListItemView.findViewById<RadioButton>(R.id.marcarRb)
                    radioButton.isChecked = toDoListItemIsChecked
                    // Listener de clic para el botón de radio
                    radioButton.setOnClickListener {
                        var isChecked = radioButton.isChecked
                        snapshotChild.ref.child("isChecked").setValue(isChecked)
                    }

                    // Agregar la vista personalizada al contenedor
                    toDoListContainer.addView(toDoListItemView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error", error.toException())
            }
        })

        var toDoListEd: EditText = findViewById(R.id.toDoListEd)
        var agregarRb: RadioButton = findViewById(R.id.agregarRb)
        agregarRb.setOnClickListener {
            guardarToDoList(toDoListEd.text.toString(), toDoListRef)
            agregarRb.isChecked = false
            toDoListEd.setText("")
            toDoListEd.setHint("Escribe para añadir algo")
        }

    }
    fun guardarToDoList(texto: String, toDoListRef: DatabaseReference) {
        val childRef = toDoListRef.child(contador.toString())
        val toDoListItem = HashMap<String, Any>()
        toDoListItem["texto"] = texto
        toDoListItem["isChecked"] = false
        childRef.setValue(toDoListItem)
        contador++
    }
}