package org.eclipse.equinox.servletbridge;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private static final String ROLE_FILE_PATH = "/WEB-INF/roles.properties";
    private static Map<String, Set<String>> labelRolesMap = new HashMap<String, Set<String>>();
    private static final String SECURITY_ERROR_PAGE = "/non-existing.jsp";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Properties rolesProperties = readRolesProperties(filterConfig.getServletContext().getResourceAsStream(ROLE_FILE_PATH));
        readAllRoles(rolesProperties);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String userRole = null;
        for (String availableRole : availableRoles) {
            userRole = req.isUserInRole(availableRole) ? availableRole : null;
            if(userRole != null)
                break;
        }
        System.out.println("Set user role "+userRole);
        role.set(userRole);

        if( ! securityConstraint(userRole, req.getRequestURI()) )
            res.sendError(HttpServletResponse.SC_FORBIDDEN);

        req = new HttpRequestWrapper(req);
        chain.doFilter(req,response);
    }

    private boolean securityConstraint(String userRole, String url) {
        boolean result = true;
        for (String label : labelRolesMap.keySet()) {
            String labelWithoutSpaces = label.replaceAll("\\s+","");
            if(url.contains(String.format("topic=/%s",labelWithoutSpaces)) ||
                    url.contains(String.format("topic=%s",labelWithoutSpaces))||
                    url.contains(String.format("topic/%s",labelWithoutSpaces))){
                result = showTopic(label, userRole);
            }
        }
        return result;
    }

    @Override
    public void destroy() {
    }

    public static  String getRole(){
        return role.get();
    }

    public static  Map<String, Set<String>> getLabelRolesMap() {
        return labelRolesMap;
    }

    public static boolean showTopic(String label, String role){
        Set<String> roles = labelRolesMap.get(label);
        if(role != null){
            if(roles != null) {
                return roles.contains(role);
            }else{
                return true;
            }
        }else{
            return roles==null;
        }
    }

    private void readAllRoles(Properties property) {
        for (Object role : property.values()) {
            availableRoles.addAll(parseRolesValue(role.toString()));
        }

        for (Object o : property.keySet()) {
            String key = o.toString();
            labelRolesMap.put(key, parseRolesValue(property.getProperty(key)));
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
