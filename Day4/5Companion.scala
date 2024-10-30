trait Atom {
    def atomicNumber: Int 
    def symbol: String
}

class Nucleus(protons: Int,neutrons: Int){
    def massNumber: Int = protons + neutrons
}

case class element (name: String, atomicNumber: Int, symbol: String) //case class

object element{  //companion object
    def set(name: String, atomicNumber: Int,symbol: String): element = {
        new element(name,atomicNumber,symbol)
    }
    def output(elements: element):String = s"Name: ${elements.name}, AtomicNumber: ${elements.atomicNumber}"
}

@main def main:Unit = {
    val hydrogen = element("Hydrogen",1,"H")
    println(element.output(hydrogen))

    val hydrogenNucleus = new Nucleus(1,0)

    val hydrogenAtom = new Atom { //anonomyous classs
        def atomicNumber: Int = hydrogen.atomicNumber
        def symbol: String = hydrogen.symbol
    }
    println(s"Atom: ${hydrogenAtom.symbol}, Atomic Number: ${hydrogenAtom.atomicNumber}")
    println(s"Nucleus Mass Number: ${hydrogenNucleus.massNumber}")
}