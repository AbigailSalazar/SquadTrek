package mx.edu.potros.viajesengrupo

data class Evento(val icono:Int,val titulo:String,val hora:String,val ubicacion:String,val encargado:AmigoObject){

    constructor() : this(-1,"","","",AmigoObject())
}

