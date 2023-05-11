package mx.edu.potros.viajesengrupo

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class Perfil : AppCompatActivity() {
    private val File = 1
    private val database = Firebase.database
//    val myRef = database.getReference("Usuarios")
    lateinit var listViajesRealizados:LinearLayout
    var viajesR = ArrayList<ViajesRealizadosObject>()


    private val mAuth = FirebaseAuth.getInstance().currentUser
    val uid = mAuth?.uid
    val userRef = database.getReference("Usuarios").child(uid!!)
    //   private val userRef= FirebaseDatabase.getInstance().getReference("Usuarios")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
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
                    val intent = Intent(this, Amigo::class.java)
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

        var placeTv: TextView=findViewById(R.id.placeTv)
        placeTv.setOnClickListener {
            var input:EditText=EditText(this)
            input.inputType=InputType.TYPE_CLASS_TEXT
            val builder =  AlertDialog.Builder(this,R.style.MyDialogTheme)

            builder.setMessage("Viaje realizado")
                .setPositiveButton("Aceptar",
                    DialogInterface.OnClickListener { dialog, id ->
                        val userRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)
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

//        val linearLayout: LinearLayout = findViewById(R.id.viajesRealizadosLy)
//        val userRefViaRe = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid).child("viajesRealizados")
//        userRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                linearLayout.removeAllViews()
//                for (snapshot in dataSnapshot.children) {
//                    val viaje = snapshot.getValue(ViajesRealizadosObject::class.java)
//                    val textView = TextView(this@Perfil)
//                    textView.text = "${viaje?.lugar}"
//                    linearLayout.addView(textView)
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Log.d("TAG", "Error", error.toException())
//            }
//        })
        listViajesRealizados= findViewById(R.id.viajesRealizadosLy)
        llenarViajesRealizados(listViajesRealizados)



        val uploadImageView: ImageView=findViewById(R.id.photoIv)
        uploadImageView.setOnClickListener {
            fileUpload()
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

    fun llenarViajesRealizados(ListViajesRealizados: LinearLayout){
        viajesR.forEach{
            var inflador= LayoutInflater.from(this);
            var vista=inflador.inflate(R.layout.viajes_perfil_view,null)

            var lugar=vista.findViewById(R.id.placeTv) as TextView
            lugar.setText(it.lugar)

            ListViajesRealizados.addView(vista)
        }
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