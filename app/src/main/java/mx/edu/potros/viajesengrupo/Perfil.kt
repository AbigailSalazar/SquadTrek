package mx.edu.potros.viajesengrupo

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.zip.Inflater

class Perfil : AppCompatActivity() {
    private val File = 1
    private val database = Firebase.database
    private lateinit var auth: FirebaseAuth
    //    val myRef = database.getReference("Usuarios")
    lateinit var listViajesRealizados:LinearLayout
    var viajesR = ArrayList<ViajesRealizadosObject>()
    var amigosId=ArrayList<String>()
    private val mAuth = FirebaseAuth.getInstance().currentUser
    var uid = mAuth?.uid
    lateinit var amigosly:LinearLayout
    val userRef = database.getReference("Usuarios").child(uid!!)
    //   private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val btnCerrarSesion = findViewById<Button>(R.id.logoutButton)
        btnCerrarSesion.setOnClickListener {
            // llama a FirebaseAuth para cerrar la sesión actual
            auth.signOut()

            // muestra un mensaje de éxito y envía al usuario a la pantalla de inicio de sesión
            Toast.makeText(baseContext, "Cerraste sesión correctamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)

            finish()
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

        var amigosly=findViewById<LinearLayout>(R.id.amigos_ly)


        //configura el perfil para que no sea editable si es de un amigo
        var amigoId=intent.getStringExtra("idAmigo")
        if(amigoId!=null){
            uid=amigoId
            var imgEditar:ImageView=findViewById(R.id.imgEditar)
            imgEditar.visibility= View.INVISIBLE
            val btnAddAmigos = findViewById<ImageButton>(R.id.btnAddAmigos)
            btnAddAmigos.visibility=View.INVISIBLE
        }

        cargarAmigos()
        var nameTv: TextView = findViewById(R.id.usernameTv)
        val userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid!!)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("username")) {
                    val username = dataSnapshot.child("username").value.toString()
                    nameTv.text = username
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", "Error al leer el nombre de usuario", databaseError.toException())
            }
        })

        if(amigoId==null){
            nameTv.setOnClickListener {
                val input: EditText = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                // establece el valor actual del nombre de usuario en el EditText
                input.setText(nameTv.text.toString())

                val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)

                builder.setMessage("Nombre de usuario")
                    .setPositiveButton(
                        "Aceptar",
                        DialogInterface.OnClickListener { dialog, id ->
                            val newUsername = input.text.toString()

                            if (mAuth != null) {
                                val userID = mAuth.uid
                                val userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(userID)

                                // actualiza el nombre de usuario en la base de datos
                                userRef.child("username").setValue(newUsername)
                                    .addOnSuccessListener {
                                        // actualiza el nombre de usuario en el TextView
                                        nameTv.text = newUsername
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.d("TAG", "Error al actualizar el nombre de usuario", exception)
                                    }
                            }

                            dialog.dismiss()
                        })
                    .setNegativeButton(
                        "Cancelar",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()
                        })
                    .setView(input)
                builder.create()
                builder.show()
            }
        }

        var placeTv: TextView=findViewById(R.id.placeTv)

        if(amigoId==null){
            placeTv.setOnClickListener {
                var input:EditText=EditText(this)
                input.inputType=InputType.TYPE_CLASS_TEXT
                val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

                builder.setMessage("Viaje realizado")
                    .setPositiveButton("Aceptar",
                        DialogInterface.OnClickListener { dialog, id ->
                            val userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(
                                uid!!
                            )
                            val place = input.text.toString()
                            val viajeRef = userRef.child("viajesRealizados").push()
                            viajeRef.child("lugar").setValue(place)
                            // viajeRef.setValue(place)
                            placeTv.setText("Agregar viaje realizado")
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
        }
        else{
            placeTv.setText("")
        }

        // Obtener la referencia de la tabla en Firebase
       val linearLayout: LinearLayout = findViewById(R.id.viajesRealizadosLy)
       val userRefViaRe = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid!!).child("viajesRealizados")
        // Leer los datos de Firebase
        userRefViaRe.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                linearLayout.removeAllViews()
                // Iterar a través de los objetos de Firebase
                for (child in dataSnapshot.children) {
                    // Obtener los valores de los objetos y agregarlos al LinearLayout
                    val lugar = child.child("lugar").getValue(String::class.java)
                    // Inflar la vista personalizada
                    val itemView = LayoutInflater.from(this@Perfil).inflate(R.layout.viajes_perfil_view, linearLayout, false)
                    // Configurar los valores de los elementos de la vista personalizada según los datos de Firebase
                    itemView.findViewById<TextView>(R.id.placeTv).text = lugar

                    linearLayout.addView(itemView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error", error.toException())
            }
        })


        var likePlacesTv: TextView=findViewById(R.id.likePlacesTv)
        if(amigoId==null){//para saber si es el perfil propio o de un amigo
            likePlacesTv.setOnClickListener {
                var input:EditText=EditText(this)
                input.inputType=InputType.TYPE_CLASS_TEXT
                val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

                builder.setMessage("Viaje Deseado")
                    .setPositiveButton("Aceptar",
                        DialogInterface.OnClickListener { dialog, id ->
                            val userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(
                                uid!!
                            )
                            val place = input.text.toString()
                            val viajeRef = userRef.child("viajesDeseados").push()
                            viajeRef.child("lugar").setValue(place)
                            placeTv.setText("Agregar viaje deseado")
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
        }
        else{
            likePlacesTv.setText("")
        }

        // Obtener la referencia de la tabla en Firebase
        val likePlacesLy: LinearLayout = findViewById(R.id.likePlacesLy)
        val userRefViaDe = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid!!).child("viajesDeseados")
        // Leer los datos de Firebase
        userRefViaDe.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                likePlacesLy.removeAllViews()
                // Iterar a través de los objetos de Firebase
                for (child in dataSnapshot.children) {
                    // Obtener los valores de los objetos y agregarlos al LinearLayout
                    val lugar = child.child("lugar").getValue(String::class.java)
                    // Inflar la vista personalizada
                    val itemView = LayoutInflater.from(this@Perfil).inflate(R.layout.viajes_perfil_view, likePlacesLy, false)
                    // Configurar los valores de los elementos de la vista personalizada según los datos de Firebase
                    itemView.findViewById<TextView>(R.id.placeTv).text = lugar

                    likePlacesLy.addView(itemView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error", error.toException())
            }
        })

        val viajesEnProcesoLy: LinearLayout = findViewById(R.id.viajesEnProcesoLy)
        val userRefViaEnProc = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid!!).child("viajesEnProceso")
        userRefViaEnProc.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                viajesEnProcesoLy.removeAllViews()
                for (child in dataSnapshot.children) {
                    val lugar = child.child("ubicacion").getValue(String::class.java)
                    val itemView = LayoutInflater.from(this@Perfil).inflate(R.layout.viajes_proceso_view, viajesEnProcesoLy, false)
                    itemView.findViewById<TextView>(R.id.placeTv).text = lugar

                    viajesEnProcesoLy.addView(itemView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Error", error.toException())
            }
        })


        val uploadImageView: ImageView=findViewById(R.id.photoIv)
        if(amigoId==null){
            uploadImageView.setOnClickListener {
                fileUpload()
            }
        }

        val fotoRef = FirebaseDatabase.getInstance().getReference("Fotos").child(uid!!)
        fotoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("foto")) {
                    val fotoUrl = dataSnapshot.child("foto").value.toString()
                    val profileImageView: ImageView = findViewById(R.id.photoIv)
                    Glide.with(this@Perfil)
                        .load(fotoUrl)
                        .into(profileImageView)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d("TAG", "Error al leer la URL de la foto del usuario", databaseError.toException())
            }
        })



    }

    fun cargarAmigos(){
        val amigosListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val datosAmigos = snapshot.getValue()
                amigosId.add(datosAmigos.toString())
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

        val usuariosListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if(amigosId.contains(snapshot.key)){
                    val fotoRef = FirebaseDatabase.getInstance().getReference("Fotos").child(snapshot.key!!)

                    fotoRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()&& dataSnapshot.hasChild("foto")) {
                                val fotoUrl = dataSnapshot.child("foto").value.toString()
                                cargarFoto(fotoUrl)
                            }
                            else{
                                cargarFoto("")
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.d("TAG", "Error al leer la URL de la foto del usuario", databaseError.toException())
                        }
                    })

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
        userRef.child(uid!!)
            .child("amigos").addChildEventListener(amigosListener)

        userRef.addChildEventListener(usuariosListener)
    }
    fun cargarFoto(fotoUrl: String) {
        var inflater=LayoutInflater.from(this)
        var view=inflater.inflate(R.layout.amigo_view,null)
        var imageView:ImageView=view.findViewById(R.id.amigoImg)
        if(fotoUrl!=""){
            Log.i("PERFIL","CARGANDO_FOTO")
            Glide.with(this@Perfil)
                .load(fotoUrl)
                .error(R.drawable.round_circle)
                .into(imageView)

        }
        amigosly.addView(view)
    }

    fun fileUpload() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, File)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == File) {
            if (resultCode == RESULT_OK) {
                val FileUri = data!!.data
                val Folder: StorageReference =
                    FirebaseStorage.getInstance().getReference().child("Usuarios")
                val file_name: StorageReference = Folder.child("file" + FileUri!!.lastPathSegment)
                file_name.putFile(FileUri).addOnSuccessListener { taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener { uri ->
                        if (mAuth != null) {
                            val userID = mAuth.uid
                            val userRef = FirebaseDatabase.getInstance().getReference("Fotos").child(userID)
                            val hashMap = HashMap<String, String>()
                            hashMap["foto"] = uri.toString()
                            userRef.setValue(hashMap).addOnSuccessListener {
                                Log.d("Mensaje", "Se subió correctamente")
                            }.addOnFailureListener { exception ->
                                Log.d("Mensaje", "Error al subir la imagen", exception)
                            }
                        }
                    }
                }
            }
        }
    }

}