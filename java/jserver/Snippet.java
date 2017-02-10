package jserver;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

}
