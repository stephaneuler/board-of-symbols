package jserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Stack;

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

public class XMLProtocol {
	private Document document;
	private Element root;
	private Element topChild;
	private Element currentElement;
	private Element checkSum;
	private File xmlFile;
	private String allData = "";
	private String HTMLFileName = "protocol.html";
	private String topChildName;
	private boolean autoSave = true;
	private Stack<Element> elementStack = new Stack<>();

	public boolean isAutoSave() {
		return autoSave;
	}

	public void setAutoSave(boolean autoSave) {
		this.autoSave = autoSave;
	}

	public XMLProtocol() {
		this("training.xml", "tests", "test");
	}

	public XMLProtocol(String xmlFileName, String rootName, String topChildName) {
		this.topChildName = topChildName;
		xmlFile = new File(xmlFileName);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		document = builder.newDocument();
		root = document.createElement(rootName);
		topChild = root;
		currentElement = topChild;

		checkSum = document.createElement("checksum");
		root.appendChild(checkSum);

		Element date = document.createElement("date");
		date.setTextContent(ZonedDateTime.now().toString());
		root.appendChild(date);

		document.appendChild(root);

	}

	public String getHTMLFileName() {
		return HTMLFileName;
	}

	public void setHTMLFileName(String hTMLFileName) {
		HTMLFileName = hTMLFileName;
	}

	public void nextTopChild() {
		nextTopChild(topChildName);
	}

	public void nextTopChild(String childName) {
		topChild = document.createElement(childName);
		currentElement = topChild;
		root.appendChild(topChild);
		if (autoSave) {
			save();
		}
	}

	public void nextChild(String childName) {
		Element child = document.createElement(childName);
		currentElement.appendChild(child);
		elementStack.push(currentElement);
		currentElement = child;
		if (autoSave) {
			save();
		}
	}

	public void up() {
		if (!elementStack.isEmpty()) {
			//String old = currentElement.getNodeName();
			currentElement = elementStack.pop();
			//System.out.println("Stack: " +  old + " -> " + currentElement.getNodeName());
		} else {
			//System.out.println( "Stack: empty");
		}

	}

	public void writeInfo(String key, String value) {
		Element e = document.createElement(key);
		e.setTextContent(value);
		currentElement.appendChild(e);
		allData += value;
		if (autoSave) {
			save();
		}
	}

	public void writeCData(String key, String value) {
		Element e = document.createElement(key);
		CDATASection cdata = document.createCDATASection(System.lineSeparator() + value + System.lineSeparator());
		e.appendChild(cdata);
		currentElement.appendChild(e);
		allData += value;
		if (autoSave) {
			save();
		}
	}

	void save() {
		save(xmlFile);
	}

	void save(File file) {
		checkSum.setTextContent("" + allData.hashCode());
		Transformer tf;
		try {
			tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(new DOMSource(document), new StreamResult(file));
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}

		saveHTML();
	}

	private void saveHTML() {
		File file = new File(HTMLFileName);
		try {
			Files.write(file.toPath(), getHTMLText().getBytes());
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(), "Protocol", JOptionPane.ERROR_MESSAGE);
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

		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			e.printStackTrace();
			return e.getLocalizedMessage();
		}
		// System.out.println( text );
		return text;
	}

}
