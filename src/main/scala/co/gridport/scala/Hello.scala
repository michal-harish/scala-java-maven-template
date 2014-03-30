package co.gridport.scala
import scala.math._

case class Interval(a:Double, b: Double)

case class Number(x : Double) {
    def ≥(y : Double): Boolean = x >= y
    def ≤(y : Double): Boolean = x <= y
    def ≠(y : Double): Boolean = x != y
    def ^(y : Double): Double = pow(x,y)
    def ÷(y: Double): Double = x / y;
    def √(y: Double): Double = {
        def loop(x0: Double) : Double = { 
          val x1 = (1.0d/x * ((x - 1) * x0 + y/pow(x0, x-1)))
          if (x0 <= x1) x0 else loop(x1)
        }
        loop(y/2)
    }
    def ±(y: Double): Interval = Interval(x-y, x+y)
}

object Number {
  implicit def double2num(x : Double): Number = Number(x)
  implicit def double2string(x: Double): String = String.valueOf(x)
  implicit def interval2string(x: Interval): String = "(" + x.a + "," + x.b + ")"
}

import Number._

object √ { def apply(x: Double) = sqrt(x) }
object ∛ { def apply(x: Double):Double = cbrt(x) }
object ∜ { def apply(x: Double):Double = 4√(x) }

object ∑ {
  def apply(x: Double*) = x.reduce { _ + _ }
  def apply(x: List[Int]) = x.reduce {_ + _}
  def apply(x: List[Double]) = x.reduce {_ + _}
  def apply(x: Array[Double]) = x.reduce {_ + _}
}

object print {
  def apply(s: String*) = Console.println( s.reduce { _  + _ })
}

object Hello extends App {

  val π = Pi
  val e = E

  print("π = ", π )
  print("√9 = ", √(9.0))
  if (√(9) ≥ 3) print("√9 ≥ 3")
  print("(√9)² = ", √(9)^2)
  print("e² = ", e^2)
  print("3√8 = " + 3√(8), " = ∛8 = ", ∛(8))
  print("4√16 = " + 4√(16), " = ∜16 = ", ∜(16))
  print("∑(1,2,3,4.5) = " + ∑(1,2,3,4.5) )
  val X = List(1,2,3,4)
  print("X = List(1,2,3,4)")
  print("∑(X) = " + ∑(X) )
  print("5±0.5 = ", 5±0.5)

}
