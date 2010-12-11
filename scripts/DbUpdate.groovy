String sqlFile = args[0] 

String h2 = getH2Jar()
String cmd = "java -cp ${h2} org.h2.tools.RunScript -showResults -url jdbc:h2:/var/h2/beta -user parapapi -password '9Kb!Snv^aoP[sAs' -script ${sqlFile}"

println cmd
exec(cmd)


void exec(String cmd) { 
	def proc = cmd.execute()                 // Call *execute* on the string
	proc.waitFor()                               // Wait for the command to finish

	// Obtain status and output
	println "return code: ${ proc.exitValue()}"
	println "stderr: ${proc.err.text}"
	println "stdout: ${proc.in.text}" // *out* from the external program is *in* for groovy
}


private String getH2Jar() {
  println "os: " + System.getProperty("os.name")
  String h2
  if("Windows Vista".equals(System.getProperty("os.name"))) {
    h2 = "C:/download/software/database/h2/h2/bin/h2.jar"
  } else {
    h2 = "/opt/h2/bin/h2-1.2.147.jar"
  }
  return h2
}

