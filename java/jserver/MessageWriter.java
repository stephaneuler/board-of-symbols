package jserver;

import java.io.IOException;
import java.io.Writer;

import javax.swing.JTextArea;

public class MessageWriter extends Writer {

	private JTextArea messageField;

	public MessageWriter(JTextArea messageField) {
		super();
		this.messageField = messageField;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		messageField.append(  new String( cbuf, off, len )  );
		
	}

	@Override
	public void flush() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
