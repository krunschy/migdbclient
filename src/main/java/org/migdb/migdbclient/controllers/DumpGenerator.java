package org.migdb.migdbclient.controllers;

import org.migdb.migdbclient.config.FilePath;
import org.migdb.migdbclient.config.fromResources.ConnectionParameters;
import org.migdb.migdbclient.config.fromResources.Session;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class DumpGenerator {

	public void generateDump() {
		String dirPath  = FilePath.DOCUMENT.getPath();
		String xmlPath  = dirPath + FilePath.XMLPATH.getPath();
		String database = Session.INSTANCE.getActiveDB();
		String host     = ConnectionParameters.SESSION.getMysqlHostName();
		String user     = ConnectionParameters.SESSION.getUserName();
		String pass     = ConnectionParameters.SESSION.getPassword();

		try {
			// 1) Ensure output directory exists
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			System.out.println("Dump path: " + xmlPath);

			// 2) Build mysqldump command
			String dumpExe = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe";
			List<String> cmd = new ArrayList<>();
			cmd.add(dumpExe);
			cmd.add("-h");   cmd.add(host);
			cmd.add("-u");   cmd.add(user);
			if (!pass.isEmpty()) {
				cmd.add("-p" + pass);
			}
			cmd.add("--no-create-db");
			cmd.add("--no-create-info");
			cmd.add("--skip-triggers");
			cmd.add("--xml");
			cmd.add(database);

			// 3) Run the process and redirect its output to Xmldump.xml
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectOutput(new File(xmlPath));
			pb.redirectError(ProcessBuilder.Redirect.INHERIT);
			Process p = pb.start();
			System.out.println("Running mysqldump...");
			int exitCode = p.waitFor();
			System.out.println("mysqldump exit code: " + exitCode);

			// 4) If successful, sanitize by streaming
			File xmlFile = new File(xmlPath);
			if (exitCode == 0 && xmlFile.exists()) {
				System.out.println("XML dump file created, now sanitizing...");
				sanitizeXml(xmlFile);
				System.out.println("Sanitization complete.");
			} else {
				System.out.println("Failed to create XML dump file.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error executing the mysqldump process.");
		}
	}

	/**
	 * Reads xmlFile character by character, writes only valid XML codepoints
	 * into a temp file, then atomically replaces the original.
	 */
	private void sanitizeXml(File xmlFile) throws Exception {
		File temp = new File(xmlFile.getParentFile(), xmlFile.getName() + ".tmp");

		CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder()
				.onMalformedInput(CodingErrorAction.REPLACE)
				.onUnmappableCharacter(CodingErrorAction.REPLACE);

		try (
				InputStreamReader isr = new InputStreamReader(Files.newInputStream(xmlFile.toPath()), decoder);
				BufferedReader reader = new BufferedReader(isr);
				BufferedWriter writer = Files.newBufferedWriter(temp.toPath(), StandardCharsets.UTF_8)
		) {
			int c;
			while ((c = reader.read()) != -1) {
				// Allow: tab, LF, CR, and everything from 0x20 up to 0xFFFD
				if (c == 0x9 || c == 0xA || c == 0xD || (c >= 0x20 && c <= 0xFFFD)) {
					writer.write(c);
				}
			}
		}

		Files.move(temp.toPath(), xmlFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
}
