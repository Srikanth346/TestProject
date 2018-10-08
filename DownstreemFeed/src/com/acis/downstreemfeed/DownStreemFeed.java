package com.acis.downstreemfeed;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import com.acis.downstreemfeed.FoldersCreation;
import com.acis.downstreemfeed.FoldersCreation.Enum_Feeds;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DownStreemFeed {
	
	private static String hostServer = "apsrt2150.uhc.com";
	private static String userName = "acisat";
	private static Session serverSession = null;
	private static final String outputFile = new FoldersCreation().getOutputFolderPath(Enum_Feeds.ASOClientPricing)
			+ "rxsolutionpricing09282018" + "_" + getTimeStamp() + ".xml";

	/**
	 * Function Name : verifyFolderPath Description : This function is used to
	 * get Connection to SSHD Server
	 **/
	protected static Session getConnection(String hostServer, String userName, String password) throws JSchException {
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		JSch javasch = new JSch();
		Session sftpsession = null;
		try {
			sftpsession = javasch.getSession(userName, hostServer, 22);
			sftpsession.setPassword(password);
			sftpsession.setConfig(config);
			sftpsession.connect();
			if (sftpsession.isConnected()) {
				System.out.println("Connected to Server " + sftpsession.getHost().toString());
			} else {
				System.out.println("Connected establishemnt failed to Server " + hostServer);
			}
		} catch (JSchException jschException) {
			System.out.println("Unable to Connect to Server ");
		}
		return sftpsession;
	}

	/**
	 * Function Name : verifyFolderPath Description : This function is used to
	 * pretty Print XML Document
	 **/

	public static final void prettyPrintXML(Document xmlDocument) throws Exception {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xmlDocument), new StreamResult(out));
	}

	/**
	 * Function Name : transferFileFromServer Description : This function is
	 * used to transfer a File from SFTP Server
	 **/
	public static void transferFileFromServer(Session session, String serverFilePath, Enum_Feeds feedType) {
		ChannelSftp channel = null;
		try {
			// Create a Channel to SFTP
			channel = (ChannelSftp) session.openChannel("sftp");
			// Connected to SFTP Channel
			channel.connect();
			if (channel.isConnected()) {
				System.out.println("Connected to Sftp Channel");
			} else {
				System.out.println("Not Connected to Sftp Channel");
			}
			InputStream out = channel.get(serverFilePath);
			BufferedReader br = new BufferedReader(new InputStreamReader(out));
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			System.out.println(" Reading File");
			String line;
			while ((line = br.readLine()) != null) {
				bw.write(line);
			}
			// Close Buffered Writer
			bw.close();
			// Close Buffered Reader
			br.close();
			// Close Channel
			channel.disconnect();
			// Pretty Print XML Document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(new FileInputStream(new File(outputFile)));
			prettyPrintXML(document);
			System.out.println("Pretty Print of XML Done.");
			System.out.println("File Saved." + outputFile);
		} catch (JSchException jschException) {
			System.out.println("Unable to create SFTP Channel to Host : " + hostServer);
			System.out.println("File Tranfer from Host :" + hostServer + " got Failed");
			jschException.getStackTrace();
		} catch (Exception exception) {
			exception.getStackTrace();
		} finally {
			if (channel.isClosed())
				channel.disconnect();
		}
	}

	/**
	 * Function Name : getTimeStamp Description : This function is used to get
	 * time stamp
	 **/
	private static String getTimeStamp() {
		SimpleDateFormat dateformatter = new SimpleDateFormat("HHmmssSS");
		Date todaysDate = new Date();
		String str_date = dateformatter.format(todaysDate);
		return str_date.toString();
	}

	/**
	 * Function Name : executeCommand Description : This function is used to
	 * execute command's on connected host
	 **/
	public static String executeCommand(Session cmdSession, String command) {
		StringBuilder outputBuffer = new StringBuilder();
		Channel channel = null;
		try {
			channel = cmdSession.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			InputStream commandOutput = channel.getInputStream();
			channel.connect();
			int readByte = commandOutput.read();
			// Read command output
			while (readByte != 0xffffffff) {
				outputBuffer.append((char) readByte);
				readByte = commandOutput.read();
			}
			channel.disconnect();
		} catch (Exception exception) {
			if (exception instanceof IOException) {
				System.out.println("Unable to Execute Command " + command);
			}
			if (exception instanceof JSchException) {
				System.out.println("Unable to Execute Command " + command);
			}
			return null;
		} finally {
			if (!channel.isClosed())
				channel.disconnect();
		}
		return outputBuffer.toString();
	}

	public static void main(String[] args) throws JSchException {
		String serverPath = "//acis/acisat7/ftp/asoclientpricing/rxsolutionpricing09282018.xml";
		String log;
		try {
			serverSession = DownStreemFeed.getConnection(hostServer, userName, "TUXup@1T");
			log = executeCommand(serverSession,
					"/acis/acisat7/bin/RELaunch_S.sh acisat7 " + args[0] +" acisxml2.0.xml:ASOClientPricing;/acisweb/bin/ASOClientPricing.sh acisat7 Daily runAsDate=TODAY");
			System.out.println(log);
			log = executeCommand(serverSession, "cd /acis/acisat7/ftp/asoclientpricing/;ls");
			System.out.println(log);
			transferFileFromServer(serverSession, serverPath, Enum_Feeds.RXSolution);
			serverSession.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!serverSession.isConnected())
				serverSession.disconnect();
	}
	}
}