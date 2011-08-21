<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:useBean id="hibernateMonitor" scope="request" class="fr.paris.lutece.plugins.jpa.modules.hibernate.web.HibernateMonitorJspBean" />
<%
hibernateMonitor.init( request, hibernateMonitor.RIGHT_MONITOR_HIBERNATE );
response.sendRedirect(hibernateMonitor.doActivateJMX( request ) );
%>