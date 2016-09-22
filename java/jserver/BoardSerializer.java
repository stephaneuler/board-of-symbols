package jserver;

import java.io.StringWriter;
import java.util.List;

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

public class BoardSerializer {
	private Document document;

	public static void main(String[] args) {
		Board b = new Board();
		BoardSerializer bs = new BoardSerializer();

		b.receiveMessage("2 0xff");
		bs.serialize(b);
		String s = bs.write();
		System.out.println( s );
		System.out.println( "Length: " + s.length() );
		System.out.println( "Hash: " + s.hashCode() );

	}

	String write() {
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

	public BoardSerializer() {
		super();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return;
		}
		document = builder.newDocument();

	}

	void serialize(Board b) {
		Node root = document.appendChild(document.createElement("board"));
		addChild(root, "rows", "" + b.getRows());
		addChild(root, "columns", "" + b.getColumns());
		List<Symbol> symbols = b.getSymbols();
		for (Symbol s : symbols) {
			Node sn = addChild(root, "symbol");
			addChild(sn, "pos", s.getPos().toString() ) ;
			addChild(sn, "type", s.getType().toString() ) ;
			addChild(sn, "color", ""+s.getFarbe());
		}

	}

	private Node addChild(Node root, String name, String value) {
		Element created = document.createElement(name);
		created.setTextContent(value);
		root.appendChild(created);
		return created;
	}

	private Node addChild(Node root, String name) {
		Element created = document.createElement(name);
		root.appendChild(created);
		return created;
	}

}
