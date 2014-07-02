package com.engagepoint.onlinehelp;

import org.eclipse.equinox.servletbridge.BridgeServlet;
import org.eclipse.equinox.servletbridge.FrameworkLauncher;

import javax.servlet.ServletException;
import java.lang.reflect.Field;

/**
 * Created by ykukharskyi on 01.07.14.
 */
public class ExpandBridgeServlet extends BridgeServlet {
    private FrameworkLauncher framework2;

    @Override
    public void destroy() {
        if (framework2 != null) {
            undeploy();
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Field frameworkField = BridgeServlet.class.getDeclaredField("framework");
            frameworkField.setAccessible(true);
            framework2 = new FrameworkLauncher();
            framework2 = (FrameworkLauncher) frameworkField.get(this);
            redeploy();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void undeploy() {
        framework2.undeploy();
    }

    private void redeploy() {
        framework2.stop();
        undeploy();
        framework2.deploy();
        framework2.start();
    }
}
