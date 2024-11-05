import scala.collection.mutable.ListBuffer

case class student(sno: Int, name: String, score: Int)

object student{
    def set(sno: Int, name: String, score: Int): student = {
        new student(sno,name,score)
    }
}


implicit class EnhancesList(students: ListBuffer[student]) {
    def %>(value: Int): ListBuffer[student] = {
        var newlist: ListBuffer[student] = ListBuffer()
        for(i <- students){
            if(i.score > value){
                newlist += i
            }
        }
        return newlist
    }

    def %<(value: Int): ListBuffer[student] = {
        var newlist2: ListBuffer[student] =  ListBuffer()
        for(i <- students){
            if(i.score < value){
                newlist2 += i
            }
        }
        return newlist2
        
    }
}

implicit def arrayToList[T](array: Array[T]): List[T] = {
    def convert(index: Int, acc: List[T]): List[T] = {
      if (index < 0) acc       // Base case: return accumulated list when index is out of bounds
      else convert(index - 1, array(index) :: acc)  // Prepend element at current index and decrement
    }
    convert(array.length - 1, Nil)  // Start converting from the last element to the first
}



implicit def tupletostudent(t: (Int, String, Int)): student = {
    new student(t._1,t._2,t._3)
}



class studentops(var studentlist: ListBuffer[student]) {
    
    def add(s: student): ListBuffer[student] = {
        studentlist += s
        studentlist
    }

    def add(s: (Int,String,Int)): ListBuffer[student] = {
        val newstudent:student = s  
        studentlist += newstudent
        studentlist
    }

    def filterastudent: ListBuffer[student] => Boolean = listbuf => {
        var result: Boolean = false
        for(s <- listbuf){
            if(s.score > 50)
                result = true
            else
                result = false
        }
        result
    }
}


@main def main: Unit = {
    var students: ListBuffer[student] = ListBuffer(student(1,"sri",20), student(2,"srinija",40),student(3,"x",50))
    val studentArray: Array[Student] = Array(
  (1, "Alice", 85), (2, "Bob", 92), (3, "Charlie", 78), (4, "David", 66), (5, "Eve", 90),
  (6, "Frank", 73), (7, "Grace", 88), (8, "Hannah", 91), (9, "Isaac", 84), (10, "Judy", 76),
  (11, "Kevin", 82), (12, "Laura", 79), (13, "Mike", 95), (14, "Nina", 70), (15, "Oscar", 89),
  (16, "Paul", 80), (17, "Quinn", 77), (18, "Rachel", 93), (19, "Sam", 85), (20, "Tina", 74),
  (21, "Uma", 69), (22, "Victor", 96), (23, "Wendy", 87), (24, "Xander", 68), (25, "Yara", 94),
  (26, "Zane", 81), (27, "Oliver", 78), (28, "Sophia", 85), (29, "Liam", 90), (30, "Mia", 83),
  (31, "Noah", 88), (32, "Emma", 75), (33, "Ava", 92), (34, "William", 86), (35, "James", 91),
  (36, "Lucas", 72), (37, "Amelia", 79), (38, "Ella", 89), (39, "Mason", 76), (40, "Logan", 95),
  (41, "Ethan", 84), (42, "Charlotte", 82), (43, "Benjamin", 80), (44, "Alexander", 71),
  (45, "Michael", 88), (46, "Isabella", 73), (47, "Daniel", 86), (48, "Elijah", 81),
  (49, "Matthew", 79), (50, "Jackson", 92)
)


  val studentList: List[Student] = studentArray  // Implicitly converted to List[Student]
  println(studentList)
    // println(students %< 50)
    // println(students %> 30)
    // var obj = new studentops(students)
    
    // var obj2 = new studentops(studentarray)
    // val recordsListBuffer: ListBuffer[(Int,String,Int)] = records
    // println(obj.add(recordsListBuffer)
    // obj.add(student(3,"ninja",70))
    // println(students)
    // obj.add((4,"tuple",50))
    // println(obj.filterastudent(students))
}

