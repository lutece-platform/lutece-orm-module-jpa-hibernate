<%@ page errorPage="../../../../ErrorPage.jsp" %>
<jsp:include page="../../../../AdminHeader.jsp" />
<jsp:useBean id="hibernateMonitor" scope="request" class="fr.paris.lutece.plugins.jpa.modules.hibernate.web.HibernateMonitorJspBean" />
<%
hibernateMonitor.init( request, hibernateMonitor.RIGHT_MONITOR_HIBERNATE );
%>
<%= hibernateMonitor.getHibernateMonitor( request ) %>

<%@ include file="../../../../AdminFooter.jsp" %>