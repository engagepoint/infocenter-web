package org.eclipse.equinox.servletbridge;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by yaroslav on 7/17/14.
 */
@WebFilter(urlPatterns = {"/*"}, description = "Security Filter")
public class SecurityFilter implements Filter {
    private static Set<String> availableRoles;
    private static ThreadLocal<String> role = new ThreadLocal<String>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        readAllRoles(filterConfig.getServletContext());
        availableRoles = new HashSet<String>();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        if(req.getUserPrincipal()!=null)
            System.out.println("Inside SecurityFilter Principal "+req.getUserPrincipal().getName() );
        System.out.println("Inside SecurityFilter isUserInRole onlinehelpadmin"+req.isUserInRole("onlinehelpadmin") );

        String userRole = null;
        for (String availableRole : availableRoles) {
            userRole = req.isUserInRole(availableRole) ? availableRole : null;
        }
        role.set(userRole);
        req = new HttpRequestWrapper(req);
        chain.doFilter(req,response);
    }

    @Override
    public void destroy() {

    }

    private void readAllRoles(ServletContext context) {
        String filepath = "/WEB-INF/roles.properties";
        Properties property = new Properties();
        InputStream input = null;
        try {
            input = context.getResourceAsStream(filepath);
            if(input==null)
                return;
            property.load(input);
        } catch (IOException ex) {
            //throw new IOException(MessageFormat.format("Problem with loading default configurations file {0} ", filename), ex);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    //logger.warn(MessageFormat.format("Failed to close {0} file", filename), e);
                }
            }
        }
        for (Object role : property.values()) {
            availableRoles.addAll(parseRolesValue(role.toString()));
        }
    }

    private List<String> parseRolesValue(String value) {
        return Arrays.asList(value.split(","));
    }
}
