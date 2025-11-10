package svws.gradle.scripts

class FileUtils {

	def static replaceFilePlaceholderWithValues = { File inputFile, File outputFile, LinkedHashMap<String, GString> replacements ->
		def content = inputFile.text

		// Ersetze die Platzhalter mit den entsprechenden Werten
		replacements.each { placeholder, replacement ->
			content = content.replace(placeholder, replacement)
		}

		// Überschreibe den Inhalt der Ausgabedatei
		outputFile.withWriter { writer ->
			writer.write(content)
		}

		println "Replaced placeholder of file: ${outputFile.path}"
	}

	def static writeFile = { File outputFile, String content ->
			outputFile.withWriter { writer ->
				writer.write(content)
			}

		println "File saved: ${outputFile.path}"
	}
}
