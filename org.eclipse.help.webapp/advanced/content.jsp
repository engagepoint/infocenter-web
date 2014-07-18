<%--
 Copyright (c) 2000, 2010 IBM Corporation and others.
 All rights reserved. This program and the accompanying materials 
 are made available under the terms of the Eclipse Public License v1.0
 which accompanies this distribution, and is available at
 http://www.eclipse.org/legal/epl-v10.html
 
 Contributors:
     IBM Corporation - initial API and implementation
--%>
<%@ include file="fheader.jsp" %>

<%
    LayoutData data = new LayoutData(application, request, response);
    FrameData frameData = new FrameData(application, request, response);
    WebappPreferences prefs = data.getPrefs();
%>

<html lang="<%=ServletResources.getString("locale", request)%>">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%=ServletResources.getString("Help", request)%>
    </title>

    <style type="text/css">
        <%
        if (data.isMozilla()) {
        %>
        HTML {
            border- <%=isRTL?"left":"right"%>: 1px solid ThreeDShadow;
        }

        <%
        } else {
        %>
        FRAMESET {
            border-top: 1px solid ThreeDShadow;
            border-left: 1px solid ThreeDShadow;
            border-right: 1px solid ThreeDShadow;
            border-bottom: 1px solid ThreeDShadow;
        }

        <%
        }
        %>
    </style>

    <script>
        function changeRootHREF() {
            var contentHREF = window.frames[1].window.location.href;
            console.log("contentURI = " + contentHREF);
            if (contentHREF) {
                var rootHREF = parent.parent.window.location.href;
                console.log("rootURI = " + rootHREF);
                if (!contentHREF.contains('/topic/org.eclipse.help.base/') && rootHREF) {
                    var indexOfCp = contentHREF.indexOf('?cp=');
                    if (indexOfCp != -1) {
                        contentHREF = contentHREF.substring(0, indexOfCp);
                    }
                    parent.parent.window.history.pushState('', '', contentHREF);
                }
            }
        }
    </script>

</head>
<frameset id="contentFrameset" rows="<%=frameData.getContentAreaFrameSizes()%>" frameborder=0" framespacing="0"
          border="0" spacing="0">
    <frame name="ContentToolbarFrame" title="<%=ServletResources.getString("topicViewToolbar", request)%>"
           src='<%="contentToolbar.jsp"+UrlUtil.htmlEncode(data.getQuery())%>' marginwidth="0" marginheight="0"
           scrolling="no" frameborder="0">
    <frame ACCESSKEY="K" name="ContentViewFrame" title="<%=ServletResources.getString("topicView", request)%>"
           src='<%=UrlUtil.htmlEncode(data.getContentURL())%>' onload="return changeRootHREF()"
           marginwidth="10"<%=(data.isIE() && "6.0".compareTo(data.getIEVersion()) <=0)?"scrolling=\"yes\"":""%>
           marginheight="0" frameborder="0">
    <%
        AbstractFrame[] frames = frameData.getFrames(AbstractFrame.BELOW_CONTENT);
        for (int f = 0; f < frames.length; f++) {
            AbstractFrame frame = frames[f];
            String url = frameData.getUrl(frame);
    %>
    <frame name="<%=frame.getName()%>" src="<%=url %>" <%=frame.getFrameAttributes()%> >
    <%
        }
    %>
</frameset>

</html>

