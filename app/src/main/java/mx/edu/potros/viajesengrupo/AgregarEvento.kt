package mx.edu.potros.viajesengrupo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.lang.ref.WeakReference


class AgregarEvento : AppCompatActivity() {


    val LAUNCH_SEL_AMIGOS= 1

    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    private val viajesRef= FirebaseDatabase.getInstance().getReference("Viajes")
    lateinit var txtEncargado:TextView
    private lateinit var btnAddEncargado:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_evento)

        var agregarEventoTxt:TextView=findViewById(R.id.agregarEventoTxt)
        //viaje de prueba
        val editar = intent.getStringExtra("editar")
        if (editar==editar){
            agregarEventoTxt.text="Editar evento"
        }


        txtEncargado=findViewById(R.id.txtEncargado)

        txtWeakReference = WeakReference(txtEncargado)
        var fechaEvento= this.intent.getStringExtra("dia")
        val btnSelectAmigo = findViewById<Button>(R.id.btnSelectAmigo)
        btnSelectAmigo.setOnClickListener {
            val intent = Intent(this, SeleccionAmigos::class.java)
            startActivity(intent)
        }




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

        //Editar nombre del evento

        var txtTitulo:TextView= findViewById(R.id.txtTitulo)
        txtTitulo.setOnClickListener {
            var input:EditText=EditText(this)
            input.inputType=InputType.TYPE_CLASS_TEXT
            val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

            builder.setMessage("Nombre del evento")
                .setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        txtTitulo.setText(input.text)
                        dialog.dismiss()
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
                .setView(input)
            builder.create()
            builder.show()
        }

        //Editar hora del evento
        var txtHora:TextView= findViewById(R.id.txtHora)
        txtHora.setOnClickListener {
            var input:TimePicker= TimePicker(this)
            val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

            builder.setMessage("Hora del evento")
                .setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        txtHora.setText(input.hour.toString()+":"+input.minute.toString())
                        dialog.dismiss()
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
                .setView(input)
            builder.create()
            builder.show()
        }

        //Editar ubicacion del evento
        var txtUbicacion:TextView= findViewById(R.id.txtUbicacion)
        txtUbicacion.setOnClickListener {
            var input:EditText=EditText(this)
            input.inputType=InputType.TYPE_CLASS_TEXT
            val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

            builder.setMessage("Ubicación del evento")
                .setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        txtUbicacion.setText(input.text)
                        dialog.dismiss()
                    })
                .setNegativeButton("Cancelar",
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
                .setView(input)
            builder.create()
            builder.show()
        }

        //agregar encargado
        var amigo=AmigoObject()
        btnAddEncargado= findViewById(R.id.btnSelectAmigo)
        btnAddEncargado.setOnClickListener {
            val i = Intent(this, Amigos::class.java)
            i.putExtra("pagAnterior","AGREGAR_EVENTO")
            startActivity(i)
        }



        //guardar evento en la bd
        var btnAceptar:Button= findViewById(R.id.btnAceptar)
        btnAceptar.setOnClickListener {
            val nombre=txtTitulo.text.toString()
            val ubicacion=txtUbicacion.text.toString()
            val hora=txtHora.text.toString()

            if(amigoSel.id!=""&&nombre.isNotBlank()&&ubicacion.isNotBlank()&&hora.isNotBlank()){
                //cambiar dependiendo de cual viaje sea y dia
                var viajeId=this.intent.getStringExtra("viajeKey")
                var dia=this.intent.getStringExtra("dia")
                val evento=Evento(R.drawable.house,nombre,hora,ubicacion, amigoSel)

                //guarda un nuevo evento con el id del usuario
                if(viajeId!=null){
                    if (usuarioId != null) {
                        viajesRef.child(viajeId)
                            .child("eventos")
                            .child(dia!!.replace("/","-"))
                            .push()
                            .setValue(evento)

                    }
                    finish()
                }
            }
            else{
                val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)
                builder.setMessage("Informacion incompleta")
                    .setPositiveButton("Aceptar",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                        })
                builder.create()
                builder.show()
            }

        }

    }

    companion object{
        lateinit var txtWeakReference:WeakReference<TextView>//wrapper para el txtview
        private var amigoSel: Usuario = Usuario()
        fun amigoSeleccionado(amigo: Usuario){
            amigoSel=amigo
            txtWeakReference.get()?.setText("Encargado: "+amigoSel.username)
        }
    }
}
