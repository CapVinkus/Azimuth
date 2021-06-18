package it.gepo.engine.local.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class CustomItemReader implements StepExecutionListener, ItemReader<List<HashMap<String, String>>> {

	private File filePath;
	private String fileNameRegex;
	private String delimiter;
	private String[] columns;

	private Logger logger = Logger.getLogger(CustomItemReader.class);
	boolean readDone = false;

	@Override
	public List<HashMap<String, String>> read()
			throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
		List<HashMap<String, String>> contenutoFile = new ArrayList<HashMap<String, String>>();

		for (File csv : filePath.listFiles()) {

			if (csv.getName().matches(fileNameRegex)) {
				if (!readDone) {
					logger.info("Lettura file indice in corso!");
					List<String> righe = FileUtils.readLines(csv);

					int rowCount = 0;
					for (String riga : righe) {
						
						HashMap<String, String> valoriRiga = new HashMap<String, String>();

						String[] campi = riga.split(delimiter);
						for (int i = 0; i < campi.length; i++) {
							valoriRiga.put(columns[i].toUpperCase(), campi[i].replace("'", "''"));
							
						}
					
						contenutoFile.add(valoriRiga);
						rowCount++;
					}
					readDone = true;
					return contenutoFile;
				} else {
					return null;
				}
			}
			else {
				logger.info("Nessun file indice trovato!");
				
			}
		}
		return null;
	}

	public File getFilePath() {
		return filePath;
	}

	public void setFilePath(File filePath) {
		this.filePath = filePath;
	}

	public String getFileNameRegex() {
		return fileNameRegex;
	}

	public void setFileNameRegex(String fileNameRegex) {
		this.fileNameRegex = fileNameRegex;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	@Override
	public ExitStatus afterStep(StepExecution arg0) {
		return null;
	}

	@Override
	public void beforeStep(StepExecution arg0) {
		
	}


}
