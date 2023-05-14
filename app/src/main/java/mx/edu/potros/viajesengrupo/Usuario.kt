package mx.edu.potros.viajesengrupo

data class Usuario(val id:String, val username:String,
                   var foto:String,
                   val viajesEnProceso:ArrayList<Viaje>,
                   val viajesRealizados:String,
                   val viajesDeseados:String,
                   val amigos:ArrayList<String>){
    constructor() : this("","","", ArrayList(),"","",ArrayList())
}
