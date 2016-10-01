package uk.ac.liv.scm.exception;

public class SVNServiceException extends ServiceException {
	
	public static int PATHNOTFOUND_ERROR_CODE = 500;
	public static String PATHNOTFOUND_ERROR = "PathNotFoundException";
	
	public static int REVISIONNOTFOUND_ERROR_CODE = 501;
	public static String REVISIONNOTFOUND_ERROR = "RevisionNotFoundException";

}
