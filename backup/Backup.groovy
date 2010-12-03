//@Grab(group='org.codehaus.groovy.modules',module='groosh',version='[0.3.6,)')
String backupPathName = "./backup/files"
File backupPath = new File(backupPathName)
if(!backupPath.exists()) {
  backupPath.mkdir()
}
Calendar cal = new GregorianCalendar();
String datePart = String.valueOf(cal.get(Calendar.YEAR));
datePart += String.valueOf(cal.get(Calendar.MONTH));
datePart += String.valueOf(cal.get(Calendar.DATE));
datePart += String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
datePart += String.valueOf(cal.get(Calendar.MINUTE));

String backupFileName = "$backupPathName/${datePart}.parapapi.db.zip"
println backupFileName

String cmd = "java org.h2.tools.Script -url jdbc:h2:/var/h2/beta -user parapapi -pwd XXX -script $backupFileName -options compression zip"
println cmd
exec(cmd)

String zipFileName = "$backupPathName/${datePart}.parapapi.pictures.zip"
println zipFileName
String cmd = "zip -r ${zipFileName} /var/www/parapapi/pictures/"
println cmd
exec(cmd)


void exec(String cmd) { 
	def proc = command.execute()                 // Call *execute* on the string
	proc.waitFor()                               // Wait for the command to finish

	// Obtain status and output
	println "return code: ${ proc.exitValue()}"
	println "stderr: ${proc.err.text}"
	println "stdout: ${proc.in.text}" // *out* from the external program is *in* for groovy
}
