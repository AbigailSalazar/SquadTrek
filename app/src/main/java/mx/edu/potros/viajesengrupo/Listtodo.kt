package mx.edu.potros.viajesengrupo

data class Listtodo(var texto: String){
    constructor() : this("")

    override fun toString(): String {
        return texto
    }
}

