import scala.io.Source

case class employee(sno: Int,name: String,salary: Int, department: String)

object employee{
    def set(sno: Int,name: String,salary: Int, department: String): employee = {
        new employee(sno,name,salary,department)
    }
}

class employeeOps(employeeList: List[employee]) {
    def filterSalary(salary: Int): List[employee] = {

    }

    def filterDepartment(department: Int): List[employee] = {

    }

    def averageSalary: Int = {

    }

    def totalSalary: Int = {

    }

    def noofemp: List[(String,Int)] = {
        
    }

}

@main def main: Unit = {

    def readCSV(filename: String): List[List[String]] = {
        val lines = Source.fromFile(filename).getLines().toList
        lines.map(line => line.split(",").toList)
    }
    // val employe = new employee()
    val filename = "data.csv" 
    val data = readCSV(filename)
    data.foreach(println)
    val header = data.head 
    val rows = data.tail 
    println(s"Header: $header")
    println(s"First row: ${rows.head}")
}

