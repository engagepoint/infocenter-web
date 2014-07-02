package com.engagepoint.onlinehelp;

import org.eclipse.equinox.servletbridge.BridgeServlet;
import org.eclipse.equinox.servletbridge.FrameworkLauncher;

import javax.servlet.ServletException;
import java.lang.reflect.Field;

/**
 * Created by ykukharskyi on 01.07.14.
 */
public class ExpandBridgeServlet extends BridgeServlet {
    private FrameworkLauncher framework;

    @Override
    public void destroy() {
        if (framework != null) {
            undeploy();
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Field frameworkField = BridgeServlet.class.getDeclaredField("framework");
            frameworkField.setAccessible(true);
            framework = new FrameworkLauncher();
            framework = (FrameworkLauncher) frameworkField.get(this);
            redeploy();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
