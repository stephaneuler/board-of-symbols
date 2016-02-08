package jserver;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JTextArea;

public class TOutputStream extends FilterOutputStream {
	private JTextArea messageField;
	
	public TOutputStream(OutputStream out, JTextArea messageField) {
		super(out);
		this.messageField = messageField;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		messageField.append(  new String( b, off, len )  );
		super.write(b, off, len);
	}


}