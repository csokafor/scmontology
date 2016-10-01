package uk.ac.liv.scm.exception;

public class ServiceException extends Exception {

	public static int NOTSUPPORTED_ERROR_CODE = 400;
	public static String NOTSUPPORTED_ERROR = "NotSupportedException";
	
	public static int MISSINGPARAMETER_ERROR_CODE = 401;
	public static String MISSINGPARAMETER_ERROR = "MissingParameterException";
	
	public static int INVALIDPARAMETER_ERROR_CODE = 402;
	public static String INVALIDPARAMETER_ERROR = "InvalidParameterException";
	
	public static int SERVER_ERROR_CODE = 403;
	public static String SERVER_ERROR = "ServerException";
	
	public ServiceException() {
		super();
	}
	
}
