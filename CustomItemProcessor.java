package it.gepo.engine.local.processor;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemProcessor;

import com.itextpdf.text.pdf.PdfReader;

import it.gepo.engine.support.utility.GepoUtility;
public class CustomItemProcessor
implements ItemProcessor<List<HashMap<String, String>>, List<HashMap<String, Object>>>, StepExecutionListener {

	private String dirTemp;
	private int progressivo = 1;
	private String zipPrefix;
	private String algoritmo;
	private String istituto;
	private String codDocumento;
	private Logger logger = Logger.getLogger(CustomItemProcessor.class);
	private int idLavoro = 0;
	private DataSource dataSource;

	@Override
	public List<HashMap<String, Object>> process(List<HashMap<String, String>> item) throws Exception {

		List<HashMap<String, Object>> lista = new ArrayList<HashMap<String, Object>>();

		Connection conn = null;

		conn = dataSource.getConnection();

		if (idLavoro == 0) {
			idLavoro = GepoUtility.getSequenceNext(conn, "SEQ_ID_LAVORO_FILENET");
		}
		conn.close();

		for (HashMap<String, String> mappaDiLettura : item) {

			HashMap<String, Object> map = new HashMap<String, Object>();

			logger.info(mappaDiLettura.get("NOMINATIVO"));
			File file = new File(dirTemp + File.separator
					+ StringUtils.leftPad(String.valueOf(mappaDiLettura.get("NOME_PDF")), 7, "0") + ".pdf");       
			String hash = getDigest(file, algoritmo);

			if (file.exists()) {
				PdfReader pdfReader = new PdfReader(FileUtils.readFileToByteArray(file));
				int nPagine = pdfReader.getNumberOfPages();
				file.renameTo(new File(dirTemp + File.separator + hash + ".pdf"));
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
				LocalDateTime now = LocalDateTime.now();

				map.put("ROW", istituto + ";" + hash + ".pdf" + ";" + mappaDiLettura.get("NOMINATIVO") + ";"
						+ mappaDiLettura.get("NDG") + ";" + codDocumento + ";" + dtf.format(now) + ";" + zipPrefix                            
						+ StringUtils.leftPad(String.valueOf(idLavoro), 5, "0") + ";" + progressivo + ";" + nPagine);
				progressivo++;
				lista.add(map);
			} else {
				logger.info("FILE NON TROVATO: " + file);
			}
		}
		File dir = new File(dirTemp);
		for (File file : dir.listFiles())
			if (file.getName().toUpperCase().endsWith(".JRN")) {
				FileUtils.forceDelete(file);
			}
		return lista;
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

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		stepExecution.getJobExecution().getExecutionContext().put("idLavoro",
				StringUtils.leftPad(String.valueOf(idLavoro), 5, "0"));
		return ExitStatus.COMPLETED;
	}

	@Override
	public void beforeStep(StepExecution arg0) {

	}

	public String getDirTemp() {
		return dirTemp;
	}

	public void setDirTemp(String dirTemp) {
		this.dirTemp = dirTemp;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getAlgoritmo() {
		return algoritmo;
	}

	public void setAlgoritmo(String algoritmo) {
		this.algoritmo = algoritmo;
	}

	public String getIstituto() {
		return istituto;
	}

	public void setIstituto(String istituto) {
		this.istituto = istituto;
	}

	public String getCodDocumento() {
		return codDocumento;
	}

	public void setCodDocumento(String codDocumento) {
		this.codDocumento = codDocumento;
	}

	public String getZipPrefix() {
		return zipPrefix;
	}

	public void setZipPrefix(String zipPrefix) {
		this.zipPrefix = zipPrefix;
	}

	public int getProgressivo() {
		return progressivo;
	}

	public void setProgressivo(int progressivo) {
		this.progressivo = progressivo;
	}

}
