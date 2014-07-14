package com.engagepoint.onlinehelp.servletbridge;

import org.eclipse.equinox.servletbridge.BridgeServlet;
import org.eclipse.equinox.servletbridge.FrameworkLauncher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by ykukharskyi on 01.07.14.
 */
public class ExpandBridgeServlet extends BridgeServlet {
    private FrameworkLauncher framework;
    public static ThreadLocal<String> role = new ThreadLocal<String>();

    @Override
    public void destroy() {
        if (framework != null) {
            undeploy();
        }
        super.destroy();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Field frameworkField = BridgeServlet.class.getDeclaredField("framework");
            frameworkField.setAccessible(true);
            framework = (FrameworkLauncher) frameworkField.get(this);
            redeploy();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        role.set(req.getSession().getId());
        super.service(req, resp);
    }

    private void undeploy() {
        framework.undeploy();
    }

    private void redeploy() {
        framework.stop();
        undeploy();
        framework.deploy();
        framework.start();
    }
}
