package org.eclipse.equinox.servletbridge;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

/**
 * Created by yaroslav on 7/14/14.
 */
public class HttpRequestWrapper extends HttpServletRequestWrapper {
    private static final String FILTER = "filter";
    private static final String SCOPE = "(security)";


    public HttpRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public Cookie[] getCookies() {
        Cookie[] array = super.getCookies();
        if(array==null)
            return null;
        List<Cookie> cookies =  new LinkedList<Cookie>(Arrays.asList(array));
        boolean containFilterName = false;
        ListIterator<Cookie> iterator = cookies.listIterator();
        while(iterator.hasNext()){
            Cookie current = iterator.next();
            if(FILTER.equals(current.getName())) {
                if( containFilterName ) {
                    iterator.remove();
                }else{
                    current.setValue(SCOPE);
                    containFilterName = true;
                }

            }
        }
        if ( ! containFilterName)
            cookies.add(new Cookie(FILTER, SCOPE));

        return cookies.toArray(new Cookie[cookies.size()]);
    }

}
