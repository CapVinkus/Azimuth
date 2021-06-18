package it.gepo.engine.local.tasklet;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class DeleteDirTempTasklet implements Tasklet {
	private File dirTemp;
	private Logger logger = Logger.getLogger(DeleteDirTempTasklet.class);

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		FileUtils.cleanDirectory(dirTemp);
		logger.info("dir temporanea ripulita!");
		return RepeatStatus.FINISHED;
	}

	public File getDirTemp() {
		return dirTemp;
	}

	public void setDirTemp(File dirTemp) {
		this.dirTemp = dirTemp;
	}

}
