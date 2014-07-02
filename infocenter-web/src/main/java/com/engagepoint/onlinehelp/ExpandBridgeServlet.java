package com.engagepoint.onlinehelp;

import org.eclipse.equinox.servletbridge.BridgeServlet;
import org.eclipse.equinox.servletbridge.FrameworkLauncher;

import javax.servlet.ServletException;
import java.lang.reflect.Field;

/**
 * Created by ykukharskyi on 01.07.14.
 */
public class ExpandBridgeServlet extends BridgeServlet {
    FrameworkLauncher frameworkLauncher;

    @Override
    public void destroy() {
        if (frameworkLauncher != null) {
            undeploy();
        }
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            Field framework = super.getClass().getDeclaredField("framework");
            framework.setAccessible(true);
            frameworkLauncher = new FrameworkLauncher();
            frameworkLauncher = (FrameworkLauncher) framework.get(frameworkLauncher);
            redeploy();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void undeploy() {
        frameworkLauncher.undeploy();
    }

    private void redeploy() {
        frameworkLauncher.stop();
        undeploy();
        frameworkLauncher.deploy();
        frameworkLauncher.start();
    }
}
