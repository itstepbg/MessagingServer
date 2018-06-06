package exceptions;

public class WrongMenuInputException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7271520584142462374L;
	
	public WrongMenuInputException(String s) {
		super(s);
	}
	
	@Override
	public String getMessage() {
		return "The chosen menu option is not valid. Please enter an existing option from the menu.";
	}
}
