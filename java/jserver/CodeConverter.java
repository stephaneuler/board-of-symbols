package jserver;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class CodeConverter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		CodeDB cdb = new CodeDB();
		
		cdb.readXML();
		
		cdb.setXmlFile( new File( "cnew.xml"));
		cdb.convertOldCode();
		
		cdb.writeXML();
	}

}
