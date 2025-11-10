package svws.gradle.node


import org.gradle.api.tasks.*

abstract class NpmInstall extends AbstractExecTask<NpmInstall> {

	@Internal
	NodePluginConfig cfg;

	NpmInstall() {
		super(NpmInstall.class);
		dependsOn project.rootProject.tasks.getByPath('nodeDownload')
		this.cfg = project.nodeconfig;
	}

	@InputFile
	public String getPackageJson() {
		return "$workingDir/package.json";
	}

	@InputFile
	public String getPackageLockJson() {
		return "$workingDir/package-lock.json";
	}

	@OutputDirectory
	public String getNodeModules() {
		return "$workingDir/node_modules";
	}

	@TaskAction
	@Override
	protected void exec() {
		def cmdLine = this.getCommandLine();
		this.cfg.addEnvironment(this);
		cmdLine.set(0, 'install');
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
