package jserver;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class Snippet {
	Element element;
	private String[] tagNames = { "author", "comment", "language", "tag", "created", "updated" };

	public Snippet(Element element) {
		super();
		this.element = element;
	}

	public Snippet(String xml) throws ParserConfigurationException, SAXException, IOException {
		super();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		Document document = builder.parse(is);
		element = (Element) document.getElementsByTagName("snippet")
				.item(0);
	}

	public Element getElement() {
		return element;
	}

	public String getInfo() {
		String infoText = "";
		NodeList children = element.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			Node c = children.item(j);
			for (String s : tagNames) {
				if (s.equals(c.getNodeName())) {
					Node n = c.getLastChild();
					infoText += s + ":\t " + n.getNodeValue() + "\n";
				}
			}

		}
		return infoText;
	}

	public String getName() {
		return element.getAttribute("name");
	}

	String write() {
		Document document;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		document = builder.newDocument();
		
		document.appendChild(document.importNode(element, true));
		 
		Transformer tf;
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		try {
			tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.transform(new DOMSource(document), result);
		} catch (TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}
}
