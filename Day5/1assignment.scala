import scala.io.Source

case class employee(sno: Int,name: String,city: String,salary: Int, department: String)

object employee{
    def set(sno: Int,name: String,city: String,salary: Int, department: String): employee = {
       new employee(sno,name,city,salary,department)
    }
}

class employeeOps(employeeList: List[employee]) {
    def filterSalary(salary: Int): List[employee] = {
        employeeList.filter(_.salary >= salary) 
    }

    def filterDepartment(depart: String): List[employee] = {
        employeeList.filter(_.department == depart)
    }

    def averageSalary: Int = {
        var avgsalary: Int = 0
        for(i <- employeeList){
            avgsalary = avgsalary + i.salary
        }
        avgsalary/(employeeList.length)
    }

    def totalSalary: Int = {
        var totalSalary: Int = 0
        for(i <- employeeList){
            totalSalary = totalSalary + i.salary
        }
        totalSalary
    }

    def noofemp: Map[String, Int] = {
        employeeList.groupBy(_.department).map { case (dept, empList) => dept -> empList.length }
    }

    // Generate a formatted report with the details of employees
  def generateReport: String = {
    val reportHeader = f"Employee Report - Total Employees: ${employeeList.length}\n"
    val reportBody = employeeList.map { emp =>
      f"ID: ${emp.sno}%-5d | Name: ${emp.name}%-15s | City: ${emp.city}%-15s | Salary: ${emp.salary}%-8d | Department: ${emp.department}"
    }.mkString("\n")

    // Combine the header and body
    reportHeader + reportBody
  }
}



@main def main: Unit = {
    def readCSV(filename: String): List[employee] = {
        val lines = Source.fromFile(filename).getLines().toList
        val rows = lines.tail
        var employeeList: List[employee] = List()
        rows.map { line =>
        val columns = line.split(",").map(_.trim)
        employeeList = employee(
            sno = columns(0).toInt,
            name = columns(1),
            city = columns(2),
            salary = columns(3).toInt,
            department = columns(4)
        ) ::  employeeList 
    }
        employeeList
    }
    // val employe = new employee()
    val filename = "data.csv" 
    val data = readCSV(filename)
    //data.foreach(println)
    var obj = new employeeOps(data)
    println(obj.averageSalary)
    println(obj.totalSalary)
    //println(obj.filterSalary(50000))
    // println(obj.filterDepartment("Engineering"))
    // println(obj.noofemp)
    println(obj.generateReport)
    // println(obj)
}

