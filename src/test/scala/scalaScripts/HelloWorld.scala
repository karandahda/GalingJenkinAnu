package scalaScripts

  object HelloWorld  {
     def main(args: Array[String]) {
      println("Hello\t World!\n\n")
       var myName : String = "Karan"
       var myAge :Int = 37
       val (wifeName: String, wifeAge: Int) = Tuple2("Shaveta", 32)
       println("My Name " + myName + " My Age " + myAge)
       println("My Name " + myName + " My Age " + myAge)

       val epoch = System.currentTimeMillis / 1000L
       val week = 60 * 60 * 24 * 7
       val expiry = (epoch + week).toLong
       //val expiry = Long.toString(epoch + week)
       println("epoch " + epoch + " week " + week + " expiry " + expiry)
    }
  }


