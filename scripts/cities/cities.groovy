File inFile = new File("cities.txt")
File outFile = new File("cities.yml")
outFile.delete()
outFile.createNewFile()
inFile.eachLine() {
outFile << """City($it):
  name: $it
  country: py
"""
}