//scala -cp mysql-connector-j-8.0.33.jar 3Question.scala
import java.sql.{Connection, DriverManager, ResultSet, PreparedStatement}

case class candidate(sno: Int,name: String, city: String)

object candidate{
    def apply(sno: Int,name: String, city: String): candidate = {
        new candidate(sno,name,city)
    }
}

implicit def tupletocandidate(tuple: (Int, String, String)): candidate = {
  candidate(tuple._1, tuple._2, tuple._3)
}

class candidateOps{
    def insertMethod(candi: candidate, connection: Connection): Unit = {
        try {
            val sql = "INSERT INTO candidates (sno, name, city) VALUES (?, ?, ?)"
            val preparedStatement: PreparedStatement = connection.prepareStatement(sql)
            preparedStatement.setInt(1, candi.sno)
            preparedStatement.setString(2, candi.name)
            preparedStatement.setString(3, candi.city)
            preparedStatement.executeUpdate()
            println(s"Inserted: ${candi.sno}, ${candi.name}, ${candi.city}")
        }
        catch {
            case e: Exception => e.printStackTrace()
        }
    }

    def insertCandidates(candidates: Array[candidate],connection: Connection): Unit = {
        candidates.foreach(candidate => insertMethod(candidate,connection))
    }


    def verifyInsertion(connection: Connection): Unit = {
        try {
            val query = "SELECT * FROM candidates"
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery(query)
            println("Candidates in the database:")
            while (resultSet.next()) {
                val sno = resultSet.getInt("sno")
                val name = resultSet.getString("name")
                val city = resultSet.getString("city")
                println(s"ID: $sno, Name: $name, City: $city")
            }
        } catch {
            case e: Exception => e.printStackTrace()
        }
    }
}


@main def main: Unit = {
    val url = "jdbc:mysql://scaladb.mysql.database.azure.com:3306/srinija_db"
    val username = "mysqladmin"
    val password = "Password@12345"
    var connection: Connection = null

    try{
        Class.forName("com.mysql.cj.jdbc.Driver")
        connection = DriverManager.getConnection(url, username, password)
        val candidateData: Array[(Int, String, String)] = Array(
      (1, "Alice", "New York"),
      (2, "Bob", "Los Angeles"),
      (3, "Charlie", "Chicago"),
      (4, "Diana", "Houston"),
      (5, "Eve", "Phoenix"),
      (6, "Frank", "Philadelphia"),
      (7, "Grace", "San Antonio"),
      (8, "Hank", "San Diego"),
      (9, "Ivy", "Dallas"),
      (10, "Jack", "San Jose"),
      (11, "Kathy", "Austin"),
      (12, "Leo", "Jacksonville"),
      (13, "Mona", "Fort Worth"),
      (14, "Nina", "Columbus"),
      (15, "Oscar", "Charlotte"),
      (16, "Paul", "San Francisco"),
      (17, "Quinn", "Indianapolis"),
      (18, "Rita", "Seattle"),
      (19, "Steve", "Denver"),
      (20, "Tina", "Washington"),
      (21, "Uma", "Boston"),
      (22, "Vince", "El Paso"),
      (23, "Wendy", "Detroit"),
      (24, "Xander", "Nashville"),
      (25, "Yara", "Portland"),
      (26, "Zane", "Oklahoma City"),
      (27, "Aiden", "Las Vegas"),
      (28, "Bella", "Louisville"),
      (29, "Caleb", "Baltimore"),
      (30, "Daisy", "Milwaukee"),
      (31, "Ethan", "Albuquerque"),
      (32, "Fiona", "Tucson"),
      (33, "George", "Fresno"),
      (34, "Hazel", "Mesa"),
      (35, "Ian", "Sacramento"),
      (36, "Jill", "Atlanta"),
      (37, "Kyle", "Kansas City"),
      (38, "Luna", "Colorado Springs"),
      (39, "Mason", "Miami"),
      (40, "Nora", "Raleigh"),
      (41, "Owen", "Omaha"),
      (42, "Piper", "Long Beach"),
      (43, "Quincy", "Virginia Beach"),
      (44, "Ruby", "Oakland"),
      (45, "Sam", "Minneapolis"),
      (46, "Tara", "Tulsa"),
      (47, "Ursula", "Arlington"),
      (48, "Victor", "New Orleans"),
      (49, "Wade", "Wichita"),
      (50, "Xena", "Cleveland")
    )
        val candidates = candidateData.map(tuple => tuple: candidate)
        val ops = new candidateOps()
        ops.insertCandidates(candidates, connection)
        ops.verifyInsertion(connection)
    } catch {
        case e: Exception => e.printStackTrace()
    } finally {
        if (connection != null) connection.close()
    }
}