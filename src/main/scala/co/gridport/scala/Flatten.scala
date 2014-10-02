package co.gridport.scala

object Flatten extends App {

  val list:List[List[String]] = List(List("a","b","c"),List("1","2","3"))

  println(list.flatMap(_.toList))
}