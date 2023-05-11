package mx.edu.potros.viajesengrupo

data class Usuario(val username:String,
                   val viajesEnProceso:ArrayList<Viaje>,
                   val viajesRealizados:String,
                   val viajesDeseados:String,
                   val amigos:ArrayList<Usuario>)
