/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.jpa.modules.hibernate.web;

import fr.paris.lutece.plugins.jpa.modules.hibernate.service.HibernateMonitorService;
import fr.paris.lutece.plugins.jpa.modules.hibernate.service.HibernateStatistics;
import fr.paris.lutece.plugins.jpa.modules.hibernate.util.HibernateConstants;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * HibernateMonitorJspBean
 *
 */
public class HibernateMonitorJspBean extends PluginAdminPageJspBean
{
    public static final String RIGHT_MONITOR_HIBERNATE = "HIBERNATE_MONITOR";

    // parameters
    private static final String PARAMETER_POOL_NAME = "pool_name";

    // markers
    private static final String MARK_LIST_STATS = "list_stats";
    private static final String MARK_JMX_STATE = "jmx_state";
    private static final String MARK_STATS_STATE = "stats_state";

    // templates
    private static final String TEMPLATE_HIBERNATE_MONITOR = "admin/plugins/jpa/modules/hibernate/monitor_hibernate.html";

    // jsp
    private static final String JSP_HIBERNATE_MONITOR = "jsp/admin/plugins/jpa/modules/hibernate/Monitor.jsp";

    /**
     * HibernateMonitorService.
     * @return {@link HibernateMonitorService}
     */
    private HibernateMonitorService getHibernateMonitorService(  )
    {
        return (HibernateMonitorService) SpringContextService.getBean( HibernateConstants.BEAN_HIBERNATE_MONITOR );
    }

    /**
     * Displays hibernate statistics, if activated.
     * @param request the request
     * @return html page
     */
    public String getHibernateMonitor( HttpServletRequest request )
    {
        HibernateMonitorService monitorService = getHibernateMonitorService(  );

        Map<String, Object> model = new HashMap<String, Object>(  );

        List<HibernateStatistics> listStats = monitorService.computeStatistics(  );

        model.put( MARK_LIST_STATS, listStats );
        model.put( MARK_JMX_STATE, monitorService.isJMXEnabled(  ) );
        model.put( MARK_STATS_STATE, monitorService.isStatisticsEnabled(  ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_HIBERNATE_MONITOR, getUser(  ).getLocale(  ),
                model );

        return getAdminPage( template.getHtml(  ) );
    }

    /**
     * Tries to active JMX beans, and then returns the {@link #JSP_HIBERNATE_MONITOR}.
     * @param request the request
     * @return {@link #JSP_HIBERNATE_MONITOR}
     */
    public String doActivateJMX( HttpServletRequest request )
    {
        HibernateMonitorService monitorService = getHibernateMonitorService(  );

        if ( !monitorService.doActivateJMXBeans(  ) )
        {
            AppLogService.error( "Failed to activate hibernate JMX beans" );
        }

        return AppPathService.getBaseUrl( request ) + JSP_HIBERNATE_MONITOR;
    }

    /**
     * Clears a pool statistics.
     * @param request the request
     * @return {@link #JSP_HIBERNATE_MONITOR}
     */
    public String doClearStatistics( HttpServletRequest request )
    {
        String strPoolName = request.getParameter( PARAMETER_POOL_NAME );

        if ( StringUtils.isNotBlank( strPoolName ) )
        {
            HibernateMonitorService monitorService = getHibernateMonitorService(  );
            monitorService.doClearStatistics( strPoolName );
        }

        return AppPathService.getBaseUrl( request ) + JSP_HIBERNATE_MONITOR;
    }

    /**
     * Enables statistics for all pools.
     * @param request request
     * @return {@link #JSP_HIBERNATE_MONITOR}
     */
    public String doEnableStatistics( HttpServletRequest request )
    {
        getHibernateMonitorService(  ).doEnableStatistics(  );

        return AppPathService.getBaseUrl( request ) + JSP_HIBERNATE_MONITOR;
    }

    /**
     * Disables statistics for all pools.
     * @param request request
     * @return {@link #JSP_HIBERNATE_MONITOR}
     */
    public String doDisableStatistics( HttpServletRequest request )
    {
        getHibernateMonitorService(  ).doDisableStatistics(  );

        return AppPathService.getBaseUrl( request ) + JSP_HIBERNATE_MONITOR;
    }
}
