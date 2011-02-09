//java -cp /Applications/h2/bin/h2*.jar org.h2.tools.Server -baseDir ./repo/var/h2
var h2 = getH2Jar()
println("hello " + h2)
//String cmd = "java -cp ${h2} org.h2.tools.RunScript -showResults -url jdbc:h2:/var/h2/beta -user parapapi -password '9Kb!Snv^aoP[sAs' -script ${sqlFile}"
if(args.length == 0) {
	println("no sql file")
	exit(1)
} 
val sqlFile = args(0)
println("sqlFile:  " + sqlFile)
val cmd = "java -cp " + h2 + " org.h2.tools.RunScript -showResults -url jdbc:h2:tcp://127.0.0.1/beta -user parapapi -password '9Kb!Snv^aoP[sAm' -script " + sqlFile
println("cmd: " + cmd)

val p = scala.tools.nsc.io.Process(cmd)
p.waitFor();
val buf = if(p.exitValue() == 0) p.stdout else p.stderr 
buf foreach println


def getH2Jar() = {
  println("os: " + System.getProperty("os.name"))
  val os = System.getProperty("os.name")
  var h2 = ""
  if("Windows Vista" == os) {
    h2 = "C:/download/software/database/h2/h2/bin/h2.jar"
  } else if("Mac OS X" == os) {
    h2 = "/Applications/h2/bin/h2*.jar"
  } else {
    h2 = "/opt/h2/bin/h2-1.2.147.jar"
  }
  h2
}
