package mx.edu.potros.viajesengrupo

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TuViaje : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    private val listDias=ArrayList<Button>()
    var tuViajes: ArrayList<Viajes> = ArrayList<Viajes>()
    private val viajesRef= FirebaseDatabase.getInstance().getReference("Viajes")
    var viajeKey=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tu_viaje)

        viajeKey= this.intent.getStringExtra("viajeKey").toString()

        /*   agregarViajes()
           var listview: ListView = findViewById(R.id.listview_tu_viaje) as ListView
           var adaptador: TuViaje.AdaptadorViajes = TuViaje.AdaptadorViajes(this, tuViajes)
           listview.adapter = adaptador
   */


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

        var btnAgregarAmigo:ImageButton=findViewById(R.id.btnAddFriends)
        btnAgregarAmigo.setOnClickListener {
            val intent = Intent(this, AgregarAmigoViaje::class.java)
            intent.putExtra("viajeKey", viajeSel.id)
            startActivity(intent)
        }

        val btnTodolist = findViewById<TextView>(R.id.tu_viaje_todolist_tv)
        btnTodolist.setOnClickListener {
            val intent = Intent(this, ToDoList::class.java)
            intent.putExtra("viajeKey", viajeSel.id)
            startActivity(intent)
        }
        val gridBtnEventos=findViewById<GridLayout>(R.id.gridBtnEventos)
        //crear botones de eventos para cada dia

        if(viajeSel!=null){
            var dateFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy");
            var fechaInicial:Date= dateFormat.parse(viajeSel.fechaInicio)
            var fechaFinal:Date= dateFormat.parse(viajeSel.fechaFinal)

            val diff: Long = fechaFinal.time -fechaInicial.time
            val dias=TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
            Log.i("TU_VIAJE","DIAS: $dias")
            //crear botones para cada dia


            for (i in 0..dias){
                val dia= i+1
                var inflador= LayoutInflater.from(this)

                val btnEventos= inflador.inflate(R.layout.button_events_day,null) as Button
                btnEventos.text = "Dia $dia"
                gridBtnEventos.addView(btnEventos)
                btnEventos.setOnClickListener {
                    //dirijirse a los eventos de este dia

                    //Encontrar dia que indica el boton
                    val c = Calendar.getInstance()
                    c.time = fechaInicial
                    c.add(Calendar.DATE, i.toInt())

                    //Se dirije a eventos del dia
                    val intent = Intent(this, Eventos::class.java)
                    intent.putExtra("dia",dateFormat.format(c.time))
                    intent.putExtra("numDia",btnEventos.text)
                    intent.putExtra("viajeKey", viajeSel.id)
                    startActivity(intent)
                }
            }

        }

        //carga el viaje desde firebase
        cargarViaje()

        var lugar=findViewById(R.id.tu_viaje_lugar_tv) as TextView
        var imagen=findViewById(R.id.tu_viaje_imagen_iv) as ImageView
        var fecha=findViewById(R.id.tu_viaje_rango_fechas_tv) as TextView
        var amigos=findViewById(R.id.amigo1_tu_viaje) as ImageView



        if(viajeSel!=null){
            lugar.setText(viajeSel.ubicacion)
            imagen.setImageResource(R.drawable.cdmx)
            fecha.setText(viajeSel.fechaInicio+" a "+ viajeSel.fechaFinal)
        }

        //para editar viaje

        var btnEditarUbicacion:ImageView=findViewById(R.id.btnEditUbicacion)
        btnEditarUbicacion.setOnClickListener {
            var intent=Intent(this,SeleccionarUbicacion::class.java)
            intent.putExtra("ubicacion",lugar.text)
            intent.putExtra("viajeKey",viajeKey)
            startActivity(intent)
        }
        var btnEditarFechas:ImageView=findViewById(R.id.btnEditFechas)
        btnEditarFechas.setOnClickListener {
            var intent=Intent(this,SeleccionarFecha::class.java)
            intent.putExtra("fechaInicio", viajeSel.fechaInicio)
            intent.putExtra("fechaFinal", viajeSel.fechaFinal)
            intent.putExtra("viajeKey",viajeKey)
            startActivity(intent)
        }

    }

    fun cargarViaje(){
        var viajeId = intent.getStringExtra("viajeKey")
        if(viajeId!=null){
            val viajesListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val childData = snapshot.getValue() as Map<String, Any>?
                    if (childData != null) {
                        val viaje = Viaje(
                            snapshot.key!!,
                            (childData["fechaInicio"] as String?)!!,
                            (childData["fechaFinal"] as String?)!!,
                            ArrayList(),
                            (childData["ubicacion"] as String?)!!,
                            ArrayList()

                        )
                        print(viaje.toString())
                        viajeSeleccionado(viaje)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            viajesRef.child(viajeKey!!)
                .addValueEventListener(viajesListener)
        }
    }

    companion object{
        private var viajeSel: Viaje =Viaje("","","", ArrayList(),"", ArrayList())

        fun viajeSeleccionado(viajeObject: Viaje){
            this.viajeSel=viajeObject
        }
    }


}