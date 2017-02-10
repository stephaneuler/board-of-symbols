package jserver;

/**
 * This class implements a simple code layouter. A state machine is used to scan the input.
 *  
 * 
 * @author Euler
 *
 */
public class CodeLayouter {
	private String indentText = "   ";

	/**
	 * The method to perform the layout. 
	 * 
	 * @param code the program code to analyze
	 * @return the newly formatted text
	 */
	public  String autoLayout( String code ) {
		String[] lines = code.split("\n", -1);
		//System.out.println("#lines: " + lines.length);
		
		int indent = 0;
		int state = 0;
		StringBuffer formatedCode = new StringBuffer();
		for (String line : lines) {
			if (line.equals("")) {
				formatedCode.append('\n');
				continue;
			}
			line = line.trim();
			int incIndent = 0;   // additional increment for next lines
			//System.out.println( "<" + line + ">");
			lineLoop: for (int i = 0; i < line.length(); i++) {
				switch (state) {
				case 0:
					if (line.charAt(i) == '{') {
						++incIndent;
					} else if (line.charAt(i) == '}') {
						if( incIndent > 0 ) {
							--incIndent;
						} else {
						--indent;
						}
					} else if (line.charAt(i) == '"') {
						state = 1;
					} else if (line.charAt(i) == '\'') {
						state = 2;
					} else if (line.charAt(i) == '/') {   // comment
						if( i + 1 < line.length() && line.charAt(i+1) == '/' ) {
							// found //, ignore rest of line
							break lineLoop;
						}
						if( i + 1 < line.length() && line.charAt(i+1) == '*' ) {
							// found /*, switch to comment state
							state = 3;
						}
					}
					break;
				case 1: // in string
					if (line.charAt(i) == '"') {
						state = 0;
					}
					break;
				case 2: // in char
					if (line.charAt(i) == '\'') {
						state = 0;
					}
					break;
				case 3: // in comment
					if( i + 1 < line.length() && line.charAt(i) == '*' && line.charAt(i+1) == '/' ) {
						state = 0;
					}
					break;
				}
			}
			for (int t = 0; t < indent; t++) {
				line = indentText + line;
			}
			formatedCode.append(line);
			formatedCode.append('\n');
			indent += incIndent;
		}
		return formatedCode.toString();
	}

}
