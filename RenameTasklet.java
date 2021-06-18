package it.gepo.engine.local.tasklet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class RenameTasklet implements Tasklet {

	private File dirTemp;
	private String algoritmo;

	private Logger logger = Logger.getLogger(RenameTasklet.class);

	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext arg1) throws Exception {

		for (File file : dirTemp.listFiles()) {
			if (file.getName().toUpperCase().endsWith(".CSV")) {
				String hash = getDigest(file, algoritmo);
				file.renameTo(new File(dirTemp + File.separator + "indice_" + hash + ".csv"));
				logger.info("FILE INDICE RINOMINATO!");
			}
		}

		return RepeatStatus.FINISHED;
	}

	public static String getDigest(File file, String algorithmName) throws NoSuchAlgorithmException, IOException {

		MessageDigest md = MessageDigest.getInstance(algorithmName);
		FileInputStream is = new FileInputStream(file);
		md.reset();
		byte[] bytes = new byte[2048];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			md.update(bytes, 0, numBytes);
		}
		byte[] digest = md.digest();
		String result = new String(Hex.encodeHex(digest));
		is.close();
		return result;
	}

	public File getDirTemp() {
		return dirTemp;
	}

	public void setDirTemp(File dirTemp) {
		this.dirTemp = dirTemp;
	}

	public String getAlgoritmo() {
		return algoritmo;
	}

	public void setAlgoritmo(String algoritmo) {
		this.algoritmo = algoritmo;
	}

}
