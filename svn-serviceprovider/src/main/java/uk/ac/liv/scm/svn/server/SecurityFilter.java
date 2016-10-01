
package uk.ac.liv.scm.svn.server;

import java.io.IOException;
import java.security.Principal;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.server.ContainerRequest;

import uk.ac.liv.scm.exception.AuthenticationException;
import uk.ac.liv.scm.svn.util.Constants;
import uk.ac.liv.scm.svn.util.SVNServiceProperties;

@Provider
@PreMatching
public class SecurityFilter implements ContainerRequestFilter {
	
	private static final Logger logger = Logger.getLogger(SecurityFilter.class);
	
	private static String username = SVNServiceProperties.getProperty(Constants.USERNAME);
	private static String password = SVNServiceProperties.getProperty(Constants.PASSWORD);

    @Inject
    javax.inject.Provider<UriInfo> uriInfo;
    private static final String REALM = "SCM AbstractServiceProvider authentication";

    @Override
    public void filter(ContainerRequestContext filterContext) throws IOException {
        User user = authenticate(filterContext.getRequest());
        filterContext.setSecurityContext(new Authorizer(user));
    }

    private User authenticate(Request request) {
        // Extract authentication credentials
        String authentication = ((ContainerRequest) request).getHeaderString(HttpHeaders.AUTHORIZATION);
        if (authentication == null) {
            throw new AuthenticationException("Authentication credentials are required", REALM);
        }
        if (!authentication.startsWith("Basic ")) {
            return null;
            // additional checks should be done here
            // "Only HTTP Basic authentication is supported"
        }
        authentication = authentication.substring("Basic ".length());
        String[] values = Base64.decodeAsString(authentication).split(":");
        if (values.length < 2) {
            throw new WebApplicationException(400);
            // "Invalid syntax for username and password"
        }
        String username = values[0];
        String password = values[1];
        if ((username == null) || (password == null)) {
            throw new WebApplicationException(400);
            // "Missing username or password"
        }

        // Validate the extracted credentials
        User user;

        if (username.equals(username) && password.equals(password)) {
            user = new User(username, username);
            logger.info("USER AUTHENTICATED");
            //        } else if (username.equals("admin") && password.equals("adminadmin")) {
            //            user = new User("admin", "admin");
            //            System.out.println("ADMIN AUTHENTICATED");
        } else {
        	logger.info("USER NOT AUTHENTICATED");
            throw new AuthenticationException("Invalid username or password\r\n", REALM);
        }
        return user;
    }

    public class Authorizer implements SecurityContext {

        private User user;
        private Principal principal;

        public Authorizer(final User user) {
            this.user = user;
            this.principal = new Principal() {

                public String getName() {
                    return user.username;
                }
            };
        }

        public Principal getUserPrincipal() {
            return this.principal;
        }

        public boolean isUserInRole(String role) {
            return (role.equals(user.role));
        }

        public boolean isSecure() {
            return "https".equals(uriInfo.get().getRequestUri().getScheme());
        }

        public String getAuthenticationScheme() {
            return SecurityContext.BASIC_AUTH;
        }
    }

    public class User {

        public String username;
        public String role;

        public User(String username, String role) {
            this.username = username;
            this.role = role;
        }
    }
}
