package svws.gradle.node

import org.gradle.api.tasks.*

abstract class TsDownload extends Exec {

	@Internal
	NodePluginConfig cfg;

	TsDownload() {
		group = "node"
		dependsOn project.rootProject.tasks.getByPath('nodeDownload')
		this.cfg = project.nodeconfig;
	}

	@OutputFile
	String getTsc() {
		return this.cfg.getTscExecutable();
	}

	@OutputFile
	String getTsserver() {
		return this.cfg.getTsserverExecutable();
	}

	@OutputDirectory
	public String getTSDir() {
		return this.cfg.getNodeDirectory() + "/node_modules/typescript";
 	}

	@OutputDirectory
	public String getTSNodeTypesDir() {
		return this.cfg.getNodeDirectory() + "/node_modules/@types";
 	}

	@Input
	public String getTSVersion() {
		return this.cfg.getTsVersion().get();
	}

	@Input
	public String getTSNodeTypesVersion() {
		return this.cfg.getTsNodeTypesVersion().get();
	}


	@TaskAction
	@Override
	protected void exec() {
		def cmdLine = this.getCommandLine();
		this.cfg.addEnvironment(this);
		cmdLine.set(0, '@types/node@' + this.getTSNodeTypesVersion());
		cmdLine.add(0, 'typescript@' + this.getTSVersion());
		cmdLine.add(0, '--global');
		cmdLine.add(0, 'install');
		cmdLine.add(0, this.cfg.getNpmExecutable());
		if (this.cfg.isWindows()) {
			cmdLine.add(0, '/c');
			cmdLine.add(0, 'cmd');
		} else if (!this.cfg.isLinux() && !this.cfg.isMacOsX()) {
			throw new Exception("Unsupported operating system for the node plugin!");
		}
		this.setCommandLine(cmdLine);
		super.exec();
	}

}
