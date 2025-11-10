package svws.gradle.node


import org.gradle.api.tasks.AbstractExecTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class PnpmTask extends AbstractExecTask<PnpmTask> {

	@Internal
	NodePluginConfig cfg;

	PnpmTask() {
		super(PnpmTask.class);
		dependsOn project.rootProject.tasks.getByPath('pnpmInstall')
		this.cfg = project.nodeconfig;
	}

	@TaskAction
	@Override
	protected void exec() {
		// Make convention mapping work
		def cmdLine = this.getCommandLine();
		this.cfg.addEnvironment(this);
		cmdLine.set(0, this.cfg.getPnpmExecutable());
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
