package mx.edu.potros.viajesengrupo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlin.random.Random

class AgregarAmigo : AppCompatActivity() {

    private val mAuth = FirebaseAuth.getInstance().currentUser
    val usuarioId = mAuth?.uid
    private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_amigo)


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

        generarCodigo()

        //si se ingresa el código de un amigo

        var etxtCodigo:EditText=findViewById(R.id.codigoEditText)
        var amigoId=""
        var codigoValido=false
        var cantAmigos = 0

        etxtCodigo.setImeActionLabel("Listo", KeyEvent.KEYCODE_ENTER)
        etxtCodigo.setOnEditorActionListener { p0, p1, p2 ->
            var handled = false
            if(p1== EditorInfo.IME_ACTION_GO){
                if (etxtCodigo.text.isNotBlank()) {
                    //buscar al usuario con ese codigo
                    userRef.orderByChild("codigoAmigo")
                        .equalTo(etxtCodigo.text.toString())
                        .addChildEventListener(object : ChildEventListener {
                            override fun onChildAdded(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                val datosUsuario = snapshot.value as HashMap<*, *>
                                val codigoAmigo = datosUsuario["codigoAmigo"] as String

                                val codigo= etxtCodigo.text.toString()
                                var yaSonAmigos=false
                                //si el codigo del amigo es valido
                                if(codigoAmigo==codigo){
                                    amigoId = snapshot.key.toString()
                                    userRef.child(usuarioId!!)
                                        .child("amigos")
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {

                                                //funciona mientras no elimines amigos
                                                if(snapshot.value!=null){
                                                    cantAmigos = snapshot.childrenCount.toInt()
                                                    val datosAmigos = snapshot.value as ArrayList<String>

                                                    if(datosAmigos.contains(amigoId)){
                                                        yaSonAmigos=true
                                                    }
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {
                                            }

                                        })
                                    if(!yaSonAmigos){
                                        //Se registra el amigo a este usuario
                                        val map: MutableMap<String, Any> = HashMap()
                                        map[(cantAmigos + 1).toString()] = amigoId
                                        userRef.child(usuarioId!!).child("amigos").updateChildren(map)
                                        codigoValido=true
                                        handled = true
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
                }
                if(!codigoValido){
                    //informar que el codigo es invalido
                    val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)
                    builder.setMessage("Código invalido")
                        .setPositiveButton("Aceptar",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                            })
                    builder.create().show()
                }
                else{
                    finish()
                }

        }
            handled
        }



    }

    //Generar el código de amigo
    private fun generarCodigo(){
        val txtCodigo:TextView=findViewById(R.id.txtCodigo)
        val codigo=randomStringByKotlinRandom()
        txtCodigo.text=codigo

        userRef.child(usuarioId!!)
            .child("codigoAmigo")
            .setValue(codigo)

    }

    private val charPool : List<Char> = ('A'..'Z') + ('0'..'9') + ('-')
    private fun randomStringByKotlinRandom() = (1..8)
        .map { Random.nextInt(0, charPool.size).let { charPool[it] } }
        .joinToString("")
}