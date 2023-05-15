package mx.edu.potros.viajesengrupo

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Response
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.random.Random


class MisViajes : AppCompatActivity() {
    var misViajes= HashMap<ImageView,Viaje>()
    var viajesids=ArrayList<String>()
    lateinit var list:LinearLayout
    var fotosViajes=ArrayList<Int>()
    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    //val usuarioId="-NUileJDCu_cQMfcael9"
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    private val viajesRef= FirebaseDatabase.getInstance().getReference("Viajes")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_viajes)

        list=findViewById(R.id.mis_viajes_ly)
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

        agregarViajes()
        cargarViajes()

        fotosViajes.add(R.drawable.cdmx)
        fotosViajes.add(R.drawable.guadalajara)
        fotosViajes.add(R.drawable.monterrey)

        val btnUnirse=findViewById<Button>(R.id.btnUnirse)
        btnUnirse.setOnClickListener {
            var input:EditText=EditText(this)
            input.inputType= InputType.TYPE_CLASS_TEXT
            val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

            builder.setMessage("Código del viaje")
                .setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        unirseViaje(input.text.toString())
                        dialog.dismiss()
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
                .setView(input)
                .setIcon(R.drawable.airplane)
            builder.create()

            builder.show()
        }
       /* var listview: ListView = findViewById(R.id.mis_viajes_lv) as ListView

        var adaptador: AdaptadorViajes = AdaptadorViajes(this,misViajes)
        listview.adapter = adaptador*/

    }


    fun agregarViajes(){
//        misViajes.add(Viajes("Guadalajara, Jalisco",R.drawable.guadalajara,R.drawable.round_circle,"Fechas", "Del 20 a 25 de marzo."))
//        misViajes.add(Viajes("Monterrey, Nuevo León",R.drawable.monterrey,R.drawable.round_circle,"Fechas", "Del 11 a 15 de junio."))
//        misViajes.add(Viajes("Cd. México, México.",R.drawable.cdmx,R.drawable.round_circle,"Fechas", "Del 11 a 14 de febrero."))

    }


    fun addViaje(viaje:Viaje){
        var inflador=LayoutInflater.from(this);
        var vista=inflador.inflate(R.layout.mis_viajes_view,null)

        var lugar=vista.findViewById(R.id.mis_viajes_lugar_tv) as TextView
        var imagen=vista.findViewById(R.id.mis_viajes_imagen_iv) as ImageView
        var fecha=vista.findViewById(R.id.mis_viajes_rango_fechas_tv) as TextView
        //var amigos=vista.findViewById(R.id.amigo1) as ImageView


        lugar.setText(viaje.ubicacion)

//        var imgRandom=Random.nextInt(0,fotosViajes.size-1)
            imagen.setImageResource(R.drawable.cdmx)

        fecha.setText(viaje.fechaInicio+" a "+ viaje.fechaFinal)
        misViajes.set(imagen,viaje)
        imagen.setOnClickListener {
            val intent = Intent(this, TuViaje::class.java)
            TuViaje.viajeSeleccionado(misViajes.get(imagen)!!)
            //intent.putExtra("viajeKey",viajesids.get(imagen))
            startActivity(intent)
        }
        list.addView(vista)

    }

    fun unirseViaje(codigoViaje:String){
        var cantAmigos=0
        var codigoValido=false
        viajesRef.orderByChild("codigoViaje")
            .equalTo(codigoViaje)
            .addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                    val datosViaje = snapshot.value as HashMap<*, *>
                    val codigo = datosViaje["codigoViaje"] as String

                    var yaSeUnio=false
                    //si el codigo del viaje es valido
                    //añadir amigo a viaje

                    if(codigoViaje==codigo){
                        var viajeId = snapshot.key.toString()
                        viajesRef.child(viajeId!!)
                            .child("amigos")
                            .addValueEventListener(object : ValueEventListener {//revisar si el usuario ya es parte del viaje
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    //funciona mientras no elimines amigos
                                    if(snapshot.value!=null){
                                        cantAmigos = snapshot.childrenCount.toInt()
                                        val datosAmigos = snapshot.value as ArrayList<String>

                                        if(datosAmigos.contains(usuarioId)){
                                            yaSeUnio=true
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                }

                            })
                        if(!yaSeUnio){
                            //Se registra el amigo a este viaje
                            val map: MutableMap<String, Any> = HashMap()
                            map[(cantAmigos + 1).toString()] = usuarioId!!
                            viajesRef.child(viajeId).child("amigos").updateChildren(map)
                            codigoValido=true
                            //finish()
                        }

                    }
                }
                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {

                }
                override fun onChildRemoved(snapshot: DataSnapshot) {

                }
                override fun onChildMoved(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {

                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        if(codigoValido){
            finish()
            startActivity(intent)
        }
        else{
            val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

            builder.setMessage("Código invalido")
                .setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
            builder.create()
            builder.show()
        }
    }


    //cargar viajes desde firebase
    fun cargarViajes() {
        //obtiene las referencias a los viajes del usuario
        val viajesListener = object :  ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val datosAmigos = snapshot.getValue()
                viajesids.add(datosAmigos.toString())
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        //obtiene los objetos viaje de la tabla viajes
        val viajeListener = object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(viajesids.contains(snapshot.key)){
                    val viajeId = snapshot.key
                    val datosViaje = snapshot.value as HashMap<*, *>
                    val ubicacion = datosViaje["ubicacion"] as String
                    val fechaInicio = datosViaje["fechaInicio"] as String
                    val fechaFinal = datosViaje["fechaFinal"] as String

                    val nuevoViaje = Viaje(viajeId!!,fechaInicio, fechaFinal, ArrayList<String>(), ubicacion, ArrayList<Evento>())
                    addViaje(nuevoViaje)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {
            }

        }

        userRef.child(usuarioId!!)
            .child("viajesEnProceso").addChildEventListener(viajesListener)

        viajesRef.addChildEventListener(viajeListener)

    }




}