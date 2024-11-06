case class Employee(sno: Int, name: String, city: String, department: String)

class Tree(val department: String, var employees: List[(Int, String, String)], var subDepartments: List[Tree]) {
  

  def addEmployee(employee: Employee): Unit = {
    if (employee.department == department) {
      employees = employees :+ (employee.sno, employee.name, employee.city)
    } else {
      subDepartments.foreach(_.addEmployee(employee))
    }
  }

  def addSubDepartment(subDepartment: Tree): Unit = {
    subDepartments = subDepartments :+ subDepartment
  }

  def printTree(indent: String = "", isLast: Boolean = true): Unit = {
    val line = if (isLast) "└──" else "├──"
    println(s"$indent$line $department")
    employees.foreach { case (sno, name, city) =>
      println(s"$indent    ├── ($sno, $name, $city)")
    }
    subDepartments.zipWithIndex.foreach { case (subDept, index) =>
      subDept.printTree(indent + (if (isLast) "    " else "│   "), index == subDepartments.length - 1)
    }
  }
}

object OrganizationApp {

  val finance = new Tree("Finance", Nil, Nil)
  val payments = new Tree("Payments", Nil, Nil)
  val sales = new Tree("Sales", Nil, Nil)
  val marketing = new Tree("Marketing", Nil, Nil)
  val advertisements = new Tree("Advertisements", Nil, Nil)
  val salesManagement = new Tree("SalesManagement", Nil, Nil)
  val hr = new Tree("HR", Nil, Nil)
  val tech = new Tree("Tech", Nil, Nil)

  def main(args: Array[String]): Unit = {
    finance.addSubDepartment(payments)
    finance.addSubDepartment(sales)
    sales.addSubDepartment(marketing)
    sales.addSubDepartment(advertisements)
    sales.addSubDepartment(salesManagement)
    finance.addSubDepartment(hr)
    finance.addSubDepartment(tech)
    println("Welcome to the Organization Tree Application!")
    var continue = true

    while (continue) {
      println("\nEnter employee details (sno, name, city, department), or type 'exit' to stop.")
      val input = scala.io.StdIn.readLine()

      if (input.toLowerCase == "exit") {
        continue = false
      } else {
        val details = input.split(",").map(_.trim)

        if (details.length == 4) {
          try {
            val sno = details(0).toInt
            val name = details(1)
            val city = details(2)
            val department = details(3)
            val employee = Employee(sno, name, city, department)
            finance.addEmployee(employee) 

            println(s"Employee added: $name to $department department.")
          } catch {
            case e: Exception => println("Invalid input. Please enter in the format: sno,name,city,department")
          }
        } else {
          println("Invalid input. Please enter exactly 4 comma-separated values: sno,name,city,department.")
        }
      }
    }
    println("\nOrganization")
    finance.printTree()
  }
}
