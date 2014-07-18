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
    private static Set<String> availableRoles = new HashSet<String>();
    private static ThreadLocal<String> role = new ThreadLocal<String>();
    private static InputStream rolesStream;
    private static final String ROLE_FILE_PATH = "/WEB-INF/roles.properties";
    private static Properties rolesProperties;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //if(rolesStream == null)
        //    rolesStream = filterConfig.getServletContext().getResourceAsStream(ROLE_FILE_PATH);
        rolesProperties = readRolesProperties(filterConfig.getServletContext().getResourceAsStream(ROLE_FILE_PATH));
        readAllRoles(rolesProperties);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String userRole = null;
        if(req.getUserPrincipal()!=null)
            System.out.println("Inside SecurityFilter Principal "+req.getUserPrincipal().getName() );
        System.out.println("Inside SecurityFilter isUserInRole onlinehelpadmin  ->"+req.isUserInRole("onlinehelpadmin") );
        for (String availableRole : availableRoles) {
            userRole = req.isUserInRole(availableRole) ? availableRole : null;
            if(userRole != null)
                break;
        }
        System.out.println("Set user role "+userRole);
        role.set(userRole);
        req = new HttpRequestWrapper(req);
        chain.doFilter(req,response);
    }

    @Override
    public void destroy() {
        if(rolesStream != null){
            try {
                rolesStream.close();
            } catch (IOException e) {
                //logger.warn(MessageFormat.format("Failed to close {0} file", filename), e);
            }
        }
    }

    public static String getRole(){
        return role.get();
    }

    public static Map<String, Set<String>> getLabelRolesMap() {
        Map<String, Set<String>> labelRolesMap = new HashMap<String, Set<String>>();

        for (Object o : rolesProperties.keySet()) {
            String key = o.toString();
            labelRolesMap.put(key, parseRolesValue(rolesProperties.getProperty(key)));
        }
        return labelRolesMap;
    }

    private void readAllRoles(Properties property) {
        for (Object role : property.values()) {
            availableRoles.addAll(parseRolesValue(role.toString()));
        }
    }

    private static Set<String> parseRolesValue(String value) {
        return new HashSet<String>(Arrays.asList(value.split(",")));
    }

    private static Properties readRolesProperties(InputStream input){
        Properties property = new Properties();
        if(input==null)
            return property;
        try {
            property.load(input);
        } catch (IOException ex) {
            //throw new IOException(MessageFormat.format("Problem with loading default configurations file {0} ", filename), ex);
        }
        return property;
    }
}
