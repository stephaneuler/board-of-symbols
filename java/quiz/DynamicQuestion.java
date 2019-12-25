package quiz;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

import jserver.Utils;

public class DynamicQuestion extends OpenQuestion {
	static Random random = new Random();
	String[] ops = {"&", "|", "^" };
	String[] aOps = {"+", "-", "*" };

	public DynamicQuestion(String typ) {
		super("", "");
		fill(typ);
	}

	private void fill(String typ) {		
		 try {
			Method method = this.getClass().getMethod(typ);
			method.invoke(this);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			//e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Fehler bei Methode: " + e, "DynamicQuestion",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void intDiv() {
		int a = 9 + random.nextInt(20);
		int b;
		do {
			b = 2 + random.nextInt(5);
		} while (a % b == 0);
		question = a + "/" + b;
		answer = "" + a / b;
	}

	public void mod() {
		int a = 12 + random.nextInt(20);
		int b = 2 + random.nextInt(5);
		question = a + "%" + b;
		answer = "" + a % b;
	}

	public void mod10() {
		int a = 1234 + random.nextInt(1000);
		long b = Math.round(Math.pow(10, 1 + random.nextInt(3)));
		question = a + "%" + b;
		answer = "" + a % b;
	}

	public void plusplus() {
		int i = 2 + random.nextInt(3);
		long j = 2 + random.nextInt(3);
		question = "Mit <em>i=" + i +";j=" + j +"</em> was ergibt <em>++i * j</em>" ;
		question = addHtml( question );
		answer = "" +  ++i * j;
	}
	
	public void plusplusPost() {
		int i = 2 + random.nextInt(3);
		long j = 2 + random.nextInt(3);
		question = "Mit <em>i=" + i +";j=" + j +"</em> was ergibt <em>i++ * j</em>" ;
		question = addHtml( question );
		answer = "" +  i++ * j;
	}

	public void minusminus() {
		int i = 2 + random.nextInt(3);
		long j = 2 + random.nextInt(3);
		question = "Mit <em>i=" + i +";j=" + j +"</em> was ergibt <em>--i * j</em>" ;
		question = addHtml( question );
		answer = "" +  --i * j;
	}

	public void minusminusPost() {
		int i = 2 + random.nextInt(3);
		long j = 2 + random.nextInt(3);
		question = "Mit <em>i=" + i +";j=" + j +"</em> was ergibt <em>i-- * j</em>" ;
		question = addHtml( question );
		answer = "" +  i-- * j;
	}

	public void logo() {
		boolean a = random.nextBoolean();
		boolean b = random.nextBoolean();
		String op = Utils.randomFrom( ops );
		question =  a + " " + op + " "  + b;
		boolean value = eval( a, b, op);
		answer = "" +  value;
	}

	public void logo2() {
		boolean a = random.nextBoolean();
		boolean b = random.nextBoolean();
		boolean c = random.nextBoolean();
		String op1 = Utils.randomFrom( ops );
		String op2 = Utils.randomFrom( ops );
		question = "(" + a + " " + op1 + " "  + b + ") " + op2 + " " + c;
		boolean value = eval( eval( a, b, op1), c, op2);
		answer = "" +  value;
	}

	public void exp2() {
		int a = 2 + random.nextInt(5);
		int b = 2 + random.nextInt(5);
		int c =  2 + random.nextInt(5);
		String op1 = Utils.randomFrom( aOps );
		String op2 = Utils.randomFrom( aOps );
		question =  a + " " + op1 + " "  + b + " " + op2 + " " + c;
		ScriptEngineManager mgr = new ScriptEngineManager();
	    ScriptEngine engine = mgr.getEngineByName("JavaScript");
	    try {
			answer =  engine.eval(question).toString();
			System.out.println( answer );
		} catch (ScriptException e) {
			e.printStackTrace();
		}	
	}

	public void array1() {
		int n = 5;
		int[] a = new int[n];
		question = "int[] a= {";
		for( int i=0; i<n; i++ ) {
			int v = 2 + random.nextInt(5);
			question += v + ", ";
			a[i] = v;
		}
		int i1 = random.nextInt(5);
		int i2 = random.nextInt(5);
		question += "}; a[" + i1 + "]*a[" + i2 + "]";
		answer =  "" + a[i1]*a[i2];
		
	}

	public void sIndexOf() {
		String text = "TH-Mittelhessen";
		int i = random.nextInt(text.length() );
		question = "\"" + text + "\".indexOf(" + text.charAt(i) + ")";
		answer = "" + text.indexOf( text.charAt(i) );
	}
	
	private boolean eval(boolean a, boolean b, String op) {
		if( op.equals("&") ) {
			return a & b;
		} else if( op.equals("|") ) {
			return  a | b;
		} else  {
			return  a ^ b;
		}
	}

	private String addHtml(String text) {
		return "<html>" + text + "</html>";
	}

}
