package mx.edu.potros.viajesengrupo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ToDoList : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance().currentUser
    val uid = mAuth?.uid

//    var lista: ArrayList<Listtodo> = ArrayList<Listtodo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_to_do_list)




//        agregarLista()

//        var listview: ListView = findViewById(R.id.to_do_list_lv) as ListView
//
//        var adaptador: AdaptadorLista = AdaptadorLista(this, lista)
//        listview.adapter = adaptador


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

        val btnBack = findViewById<Button>(R.id.btnBack)
        btnBack.setOnClickListener { finish() }

        val btnMessages = findViewById<ImageView>(R.id.btnMessages)
        val btnNotifications = findViewById<ImageView>(R.id.btnNotifications)
        btnMessages.setOnClickListener {
            val intent = Intent(this, Inbox::class.java)
            startActivity(intent)
        }
        btnNotifications.setOnClickListener {
            val intent = Intent(this, Notificaciones::class.java)
            startActivity(intent)
        }
        // termina :D



        val toDoListEd: EditText = findViewById(R.id.toDoListEd)
        val agregarRb: RadioButton = findViewById(R.id.agregarRb)
        agregarRb.setOnClickListener{
            guardarToDoList(toDoListEd.text.toString())
            agregarRb.isChecked = false
            toDoListEd.setText("")
            toDoListEd.setHint("Escribe para añadir algo")
        }

        // Obtener la referencia de la tabla en Firebase
        val toDoListLy: LinearLayout = findViewById(R.id.toDoListLy)
        val userRefToDoList = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid!!).child("toDoList")
        // Leer los datos de Firebase
        userRefToDoList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                toDoListLy.removeAllViews()

                // Iterar a través de los objetos de Firebase
                for (child in dataSnapshot.children) {
                    // Obtener los valores de los objetos y agregarlos al LinearLayout
                    val lista = child.child("lista").getValue(String::class.java)
                    val isChecked = child.child("isChecked").getValue(Boolean::class.java)
                    // Inflar la vista personalizada
                    val itemView = LayoutInflater.from(this@ToDoList).inflate(R.layout.to_do_list_view, toDoListLy, false)
                    // Configurar los valores de los elementos de la vista personalizada según los datos de Firebase
                    itemView.findViewById<TextView>(R.id.to_do_list_tv).text = lista
                    val radioButton = itemView.findViewById<RadioButton>(R.id.marcarRb)
                    radioButton.isChecked = isChecked ?: false

                    // Agregar un listener al RadioButton para actualizar su estado
                    radioButton.setOnClickListener {
                        val isChecked = radioButton.isChecked
                        val childRef = userRefToDoList.child(child.key!!)
                        childRef.child("isChecked").setValue(isChecked)
                    }

                    toDoListLy.addView(itemView)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error", error.toException())
            }
        })




    }
    fun guardarToDoList(texto: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid!!)
        val viajeRef = userRef.child("toDoList").push()
        viajeRef.child("lista").setValue(texto)
    }



//    fun agregarLista(){
//        lista.add(Listtodo("Agregar amigos"))
//        lista.add(Listtodo("Crear eventos de los días de viaje"))
//        lista.add(Listtodo("Asignar eventos"))
//        lista.add(Listtodo("Revisar inbox"))
//        lista.add(Listtodo("Buscar hotel"))
//    }

//    private class AdaptadorLista: BaseAdapter {
//        var listas=ArrayList<Listtodo>()
//        var contexto: Context?=null
//
//        constructor(contexto: Context, listas: ArrayList<Listtodo>){
//            this.listas=listas
//            this.contexto=contexto
//        }
//
//        override fun getCount(): Int {
//            return listas.size
//        }
//
//        override fun getItem(position: Int): Any {
//            return listas[position]
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
//            var lista=listas[position]
//            var inflador= LayoutInflater.from(contexto)
//            var vista=inflador.inflate(R.layout.to_do_list_view, null)
//
//            var text=vista.findViewById(R.id.to_do_list_tv) as TextView
//
//            text.setText(lista.lista)
//
//            return vista
//
//        }
//    }
}