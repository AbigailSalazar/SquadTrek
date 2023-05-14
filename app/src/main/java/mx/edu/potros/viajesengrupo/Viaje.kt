package mx.edu.potros.viajesengrupo

data class Viaje(val id:String,val fechaInicio:String,
                 val fechaFinal:String,
                 val amigos:ArrayList<String>,
                 val ubicacion:String,
                 val eventos:ArrayList<Evento>){

    constructor():this("","","", ArrayList(),"",ArrayList())
}
