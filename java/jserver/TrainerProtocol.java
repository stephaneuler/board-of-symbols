package jserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class TrainerProtocol {
	private Document document;
	private Element root;
	private Element test;
	private Element checkSum;
	private File xmlFile = new File("training.xml");
	private String allData = "";
	private String HTMLFileName = "protocol.html";

	public TrainerProtocol() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		document = builder.newDocument();
		root = document.createElement("tests");
		checkSum = document.createElement("checksum");
		root.appendChild(checkSum);
		document.appendChild(root);

	}

	public String getHTMLFileName() {
		return HTMLFileName;
	}

	public void setHTMLFileName(String hTMLFileName) {
		HTMLFileName = hTMLFileName;
	}

	public void nextImage() {
		test = document.createElement("test");
		root.appendChild(test);
		save();
	}

	public void writeInfo(String key, String value) {
		Element e = document.createElement(key);
		e.setTextContent(value);
		test.appendChild(e);
		allData += value;
		save();
	}

	public void writeCData(String key, String value) {
		Element e = document.createElement(key);
		CDATASection cdata = document.createCDATASection(System.lineSeparator() + value + System.lineSeparator());
		e.appendChild(cdata);
		test.appendChild(e);
		allData += value;
		save();
	}

	private void save() {
		checkSum.setTextContent("" + allData.hashCode());
		Transformer tf;
		try {
			tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(new DOMSource(document), new StreamResult(xmlFile));
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
		
		saveHTML();
	}

	private void saveHTML() {
		File file = new File(HTMLFileName);
		try {
		    Files.write(file.toPath(), getHTMLText().getBytes() );
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(),
					"Protocol", JOptionPane.ERROR_MESSAGE);
		}		
	}

	public String getHTMLText() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
        String text = "";

        try {
			builder = factory.newDocumentBuilder();
			Document document = builder.parse(xmlFile);

			TransformerFactory tFactory = TransformerFactory.newInstance();
			InputStream xsl = this.getClass().getResourceAsStream("images/protocol.xsl");
			StreamSource stylesource = new StreamSource(xsl);
			Transformer transformer = tFactory.newTransformer(stylesource);

			DOMSource source = new DOMSource(document);
			StringWriter writer = new StringWriter();
			transformer.transform(source, new StreamResult(writer));
			text = writer.toString().trim();
			writer.close();

		} catch (ParserConfigurationException | SAXException | IOException |  TransformerException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
        //System.out.println( text );
		return text;
	}

}
