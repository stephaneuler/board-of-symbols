package jserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JtoJS implements ExecutorListener {

	public static void main(String[] args) {
		JtoJS j = new JtoJS();
		j.work();

	}

	private void work() {
		//String code = "farbe2(2,3,BLUE);";
		String code;
		try {
			code = new String(Files.readAllBytes(Paths.get("snippet.java")));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		CodeExecutorJavaServer ce = new CodeExecutorJavaServer();
		
		//ce.addListener( this );
		
		ce.createTmpSourceFile(code);
		ce.compileAndExecute("");
	}

	@Override
	public void startCompilation() {
		System.out.println( "startCompilation");
	}

	@Override
	public void failedCompilation() {
		System.out.println( "failedCompilation");
	}

	@Override
	public void endCompilation() {
		System.out.println( "endCompilation");
	}

	@Override
	public void startExecution() {
		System.out.println( "startExecution");
	}

	@Override
	public void endExecution() {
		System.out.println( "endExecution");
	}

}
