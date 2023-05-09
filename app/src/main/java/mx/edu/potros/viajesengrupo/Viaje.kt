package mx.edu.potros.viajesengrupo

data class Viaje(val fechaInicio:String, val fechaFinal:String, val amigos:ArrayList<Usuario>, val ubicacion:String, val eventos:ArrayList<Evento>){

    constructor():this("","", ArrayList(),"",ArrayList())
}
