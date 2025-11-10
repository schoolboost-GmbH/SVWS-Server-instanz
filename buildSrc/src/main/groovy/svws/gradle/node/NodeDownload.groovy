package svws.gradle.node

import groovy.ant.AntBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Paths

abstract class NodeDownload extends DefaultTask {

	@Internal
	NodePluginConfig cfg

	NodeDownload() {
		group = 'node'
		cfg = project.nodeconfig
		if (!cfg.useSystemNode()){
			outputs.dir(cfg.getNodeDirectory())
		}
	}

	@TaskAction
	def download() {
		if (cfg.useSystemNode()) {
			println "Der Node Download wird ausgesetzt, da Node vom Hostsystem genutzt wird!"
			return
		}

		def downloadPath = cfg.getDownloadDirectory()
		def nodeFilename = cfg.getCompressedFilenameExt()
		def compressedNodeFile = new File("${downloadPath}/${nodeFilename}")
		if (compressedNodeFile.exists()) {
			println "Datei ${nodeFilename} wurde bereits heruntergeladen, skipping download"
		} else {
			Files.createDirectories(Paths.get(downloadPath))
			def downloadUrl = cfg.getDownloadURL()
			println "Downloading ${nodeFilename} from ${downloadUrl}..."
			def username = cfg.getDownloadUser()
			def password = cfg.getDownloadPasswd()
			if ((username == null) || (password == null)) {
				ant.get(src: downloadUrl, dest: compressedNodeFile)
			} else {
				ant.get(src: downloadUrl, dest: compressedNodeFile, username: username, password: password)
			}
		}

		def ant = new AntBuilder()
		if ("zip" == cfg.getCompressedFileType()) {
			ant.unzip(src: "${compressedNodeFile.getAbsolutePath()}", dest: cfg.getNodeDirectory()) { cutdirsmapper(dirs: 1) }
		} else if ("tar.gz" == cfg.getCompressedFileType()) {
			def process = [ 'tar', '-xzf', "${compressedNodeFile.getAbsolutePath()}", '--strip-components=1', '-C', "${cfg.getNodeDirectory()}/"].execute()
			process.waitFor()
		} else if ("tar.xz" == cfg.getCompressedFileType()) {
			throw new Exception("Archive type tar.xz not yet supported by this gradle node plugin!")
		} else {
			throw new Exception("Archive type ${cfg.getCompressedFileType()} not supported by this gradle node plugin!")
		}
	}

}
