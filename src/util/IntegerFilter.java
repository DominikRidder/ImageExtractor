package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class IntegerFilter extends DocumentFilter {

	int min, max;
	
	public IntegerFilter() {
		super();
		min = Integer.MIN_VALUE;
		max = Integer.MAX_VALUE;
	}
	
	public IntegerFilter(int min, int max) {
		this();
		
		this.min = min;
		this.max = max;
	}
	
	public void setRange(int min, int max) {
		this.min = min;
		this.max = max;
	}
	
	 @Override
	   public void insertString(FilterBypass fb, int offset, String string,
	         AttributeSet attr) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.insert(offset, string);
	      String sbtext = sb.toString();
	      
	      if (isInteger(sbtext)) {
	    	 int number = Integer.parseInt(sbtext);
	    	 if (number >= min && number <= max) {
	    		 super.insertString(fb, offset, string, attr);
	    	 }
	      } else if (sbtext.length() == 0) {
	    	  super.insertString(fb, offset, string, attr);
	      }
	   }

	   private static boolean isInteger(String text) {
	      try {
	         Integer.parseInt(text);
	         return true;
	      } catch (NumberFormatException e) {
	         return false;
	      }
	   }

	   @Override
	   public void replace(FilterBypass fb, int offset, int length, String text,
	         AttributeSet attrs) throws BadLocationException {

	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.replace(offset, offset + length, text);
	      String sbtext = sb.toString();

	      if (isInteger(sbtext)) {
	    	 int number = Integer.parseInt(sbtext);
	    	 if (number >= min && number <= max) {
	    		 super.replace(fb, offset, length, text, attrs);
	    	 }
	      } else if (sbtext.length() == 0) {
	    	  super.replace(fb, offset, length, text, attrs);
	      }
	   }

	   @Override
	   public void remove(FilterBypass fb, int offset, int length)
	         throws BadLocationException {
	      Document doc = fb.getDocument();
	      StringBuilder sb = new StringBuilder();
	      sb.append(doc.getText(0, doc.getLength()));
	      sb.delete(offset, offset + length);
	      String sbtext = sb.toString();
	      
	      if (isInteger(sbtext)) {
	    	 int number = Integer.parseInt(sbtext);
	    	 if (number >= min && number <= max) {
	    		 super.remove(fb, offset, length);
	    	 }
	      } else if (sbtext.length() == 0) {
	    	  super.remove(fb, offset, length);
	      }
	   }
}
