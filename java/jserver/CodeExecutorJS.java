package jserver;

import java.util.List;
import java.util.ResourceBundle;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Executes JS code
 * 
 * @author Euler
 * 
 */
public class CodeExecutorJS extends CodeExecutor {
	private String fileName = "commands.js";
	private String code ="";
	private String xName = "xSendAdapter";


	private CodeExecutorJS() {
		super();
		// set compile mode for initialization
		setCompileMode(boSLText);
 	}

	public CodeExecutorJS(Board board) {
		this();
		this.board = board;
		xName = "XSend" + board.getMessages().getLocale().getLanguage().toUpperCase();	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see jserver.CodeExecutor#createTmpSourceFile(java.lang.String)
	 */
	@Override
	public String createTmpSourceFile(String codeInput) {
		String language = board.getMessages().getLocale().getLanguage().toUpperCase();
		String className = "jserver.XSend" + language;
		String[] methods = MethodExtractor.getMethodsJS(className, xName);
		
		System.out.println( className );
		
		code = "";
		for (String method : methods) {
			//System.out.println( method );
			code += method + "\n";
		}

		code += codeInput;
		return fileName;
	}

	@Override
	public void stopExecution() {
	}

	@Override
	public String compileAndExecute(String fileName) {
		String result = "";

		ScriptEngineManager factory = new ScriptEngineManager();
		ScriptEngine engine = factory.getEngineByName("JavaScript");
		XSend xsend = new XSendAdapter( board );
		if( xName.endsWith("EN")) {
			xsend = new XSendAdapterEN( board );
		}

		//XSendAdapter x = new XSendAdapter(board);
		CodeDB codeDB = new CodeDB();
		List<String> colors = codeDB.getColorNames();
		
		//engine.setBindings(engine.createBindings(), ScriptContext.ENGINE_SCOPE);
		//ScriptContext sc = new SimpleScriptContext();
		//engine.setContext( sc );
		for( String color : colors ) {
			engine.put( color, codeDB.getColorValue(color).intValue());
		}
		try {
			MessageWriter sw=new MessageWriter(messageField);
			engine.getContext().setWriter(sw);
			engine.put(xName, xsend );
			engine.eval( code );
		} catch (ScriptException e) {
			result = e.getMessage();
			//e.printStackTrace();
		}

		for (ExecutorListener el : listeners) {
			el.endExecution();
		}
		return result;
	}

	@Override
	public void showGeneratedCode(ResourceBundle messages) {
		String numberedCode = "";
		String[] lines = code.split("\n");
			for( int l=0; l<lines.length; l++ ) {
				numberedCode += String.format("%3d", l + 1) + " " + lines[l] + "\n";
			}
		InfoBox info = new InfoBox(board.getGraphic(), "", 400, 400);
		info.setTitle(messages.getString("generatedCode") + " - JS");
		info.getTextArea().setFont( board.getCodeWindow().getNormalFont() );
		info.getTextArea().setText( numberedCode );
		info.setVisible(true);
	}
}
