/*
 * Copyright (c) 2002-2012, Mairie de Paris
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
package fr.paris.lutece.plugins.jpa.modules.hibernate.service;

import fr.paris.lutece.portal.service.jpa.EntityManagerService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.jpa.JPAConstants;

import org.apache.log4j.Logger;

import org.hibernate.SessionFactory;

import org.hibernate.ejb.HibernateEntityManagerFactory;

import org.hibernate.jmx.StatisticsService;

import org.hibernate.stat.Statistics;

import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import javax.persistence.EntityManagerFactory;


/**
 * Provides Hibernate monitoring data, relying on {@link EntityManagerService}.
 *
 */
public class HibernateMonitorService
{
    private static final Logger logger = Logger.getLogger( JPAConstants.JPA_LOGGER );
    private EntityManagerService _service;
    private boolean _bJMXEnabled;
    private boolean _bStatisticsEnabled;

    /**
     * Gets Hibernate statistics for all pools.
     * @return list of {@link HibernateStatistics}
     */
    public List<HibernateStatistics> computeStatistics(  )
    {
        List<HibernateStatistics> listStats = new ArrayList<HibernateStatistics>(  );

        for ( Entry<String, EntityManagerFactory> entry : _service.getEntityManagerFactories(  ).entrySet(  ) )
        {
            Statistics stats = getEntityManagerFactoryImpl( entry.getValue(  ) ).getSessionFactory(  ).getStatistics(  );

            HibernateStatistics hibernateStatistics = new HibernateStatistics(  );
            hibernateStatistics.setPoolName( entry.getKey(  ) );
            hibernateStatistics.setStats( stats );

            listStats.add( hibernateStatistics );
        }

        return listStats;
    }

    /**
     * <code>true</code> if JMX is enabled, <code>false</code> otherwise.
     * @param bJMXEnabled <code>true</code> if JMX is enabled, <code>false</code> otherwise.
     */
    public void setJMXEnabled( boolean bJMXEnabled )
    {
        _bJMXEnabled = bJMXEnabled;
    }

    /**
     * <code>true</code> if JMX is enabled, <code>false</code> otherwise.
     * @return <code>true</code> if JMX is enabled, <code>false</code> otherwise.
     */
    public boolean isJMXEnabled(  )
    {
        return _bJMXEnabled;
    }

    /**
     * <code>true</code> if statistics are enabled, <code>false</code> otherwise.
     * @param bStatisticsEnabled <code>true</code> if statistics are enabled, <code>false</code> otherwise.
     */
    public void setStatisticsEnabled( boolean bStatisticsEnabled )
    {
        _bStatisticsEnabled = bStatisticsEnabled;
    }

    /**
     * <code>true</code> if statistics are enabled, <code>false</code> otherwise.
     * @return <code>true</code> if statistics are enabled, <code>false</code> otherwise.
     */
    public boolean isStatisticsEnabled(  )
    {
        return _bStatisticsEnabled;
    }

    /**
     * Sets the EntityManagerService
     * @param service the EntityManagerService
     */
    public void setEntityManagerService( EntityManagerService service )
    {
        _service = service;
    }

    /**
     * The EntityManagerService
     * @return the EntityManagerService
     */
    public EntityManagerService getEntityManagerService(  )
    {
        return _service;
    }

    /**
     * {@inheritDoc}
     */
    public void init(  )
    {
        // enable statistics
        if ( isStatisticsEnabled(  ) )
        {
            if ( _service.getEntityManagerFactories(  ) != null )
            {
                doEnableStatistics(  );
            }
            else
            {
                logger.error( 
                    "EntityManagerService is not started yet, can't enable hibernate statistics. Manual activation needed." );
                setStatisticsEnabled( false );
            }
        }

        // enable hibernate JMX...
        if ( isJMXEnabled(  ) )
        {
            if ( _service.getEntityManagerFactories(  ) != null )
            {
                setJMXEnabled( registerMBeans( _service.getEntityManagerFactories(  ) ) );
                // also enable statistics...
                setStatisticsEnabled( true );
            }
            else
            {
                logger.error( 
                    "EntityManagerService is not started yet, can't start hibernate JMX beans. Manual activation needed." );
                setJMXEnabled( false );
            }
        }
    }

    /**
     * JMX manual activation.
     * @return {@link #isJMXEnabled()}
     */
    public boolean doActivateJMXBeans(  )
    {
        if ( _service.getEntityManagerFactories(  ) != null )
        {
            setJMXEnabled( registerMBeans( _service.getEntityManagerFactories(  ) ) );
        }

        return isJMXEnabled(  );
    }

    /**
     * Clear pool statistics.
     * @param strPoolName the pool name
     */
    public void doClearStatistics( String strPoolName )
    {
        EntityManagerFactory emf = _service.getEntityManagerFactory( strPoolName );

        if ( emf != null )
        {
            HibernateEntityManagerFactory emfImpl = getEntityManagerFactoryImpl( emf );
            emfImpl.getSessionFactory(  ).getStatistics(  ).clear(  );
        }
    }

    /**
     * Enables statistics (for all pools).
     * @see Statistics#setStatisticsEnabled(boolean)
     */
    public void doEnableStatistics(  )
    {
        setHibernateStatisticsEnabled( true );
    }

    /**
     * Disables statistics (for all pools).
     * @see Statistics#setStatisticsEnabled(boolean)
     */
    public void doDisableStatistics(  )
    {
        setHibernateStatisticsEnabled( false );
    }

    /**
     * Sets statistics status to factories
     * @param bEnabled enabled
     */
    private void setHibernateStatisticsEnabled( boolean bEnabled )
    {
        for ( EntityManagerFactory emf : _service.getEntityManagerFactories(  ).values(  ) )
        {
            HibernateEntityManagerFactory emfImpl = getEntityManagerFactoryImpl( emf );
            emfImpl.getSessionFactory(  ).getStatistics(  ).setStatisticsEnabled( bEnabled );

            if ( !bEnabled )
            {
                emfImpl.getSessionFactory(  ).getStatistics(  ).clear(  );
            }
        }

        setStatisticsEnabled( bEnabled );
    }

    /**
     * Casts the JPA {@link EntityManagerFactory} to Hibernate {@link HibernateEntityManagerFactory}
     * @param emf factory
     * @return the hibernate impl
     */
    private HibernateEntityManagerFactory getEntityManagerFactoryImpl( EntityManagerFactory emf )
    {
        return (HibernateEntityManagerFactory) emf;
    }

    /**
     * Registers JMX Beans for the given factories
     * @param mapFactories the factories
     * @return <code>true</code> if MBeans successfully registered, <code>false</code> otherwise.
     */
    private boolean registerMBeans( Map<String, EntityManagerFactory> mapFactories )
    {
        logger.debug( "Initializing JMX for hibernate" );

        for ( Entry<String, EntityManagerFactory> entry : mapFactories.entrySet(  ) )
        {
            logger.debug( "Registering JMX for hibernate pool : " + entry.getKey(  ) );

            HibernateEntityManagerFactory emf = getEntityManagerFactoryImpl( entry.getValue(  ) );

            if ( !registerMBean( emf.getSessionFactory(  ), entry.getKey(  ) ) )
            {
                return false;
            }
        }

        return true;
    }

    /**
     * Registers factory mbean.
     * @param sessionFactory the factory
     * @param strPoolName the pool name (added to the MBean name)
     * @return <code>true</code> if the mbean is registered, <code>false</code> otherwise.
     */
    private boolean registerMBean( SessionFactory sessionFactory, String strPoolName )
    {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer(  );

        StatisticsService mBean = new StatisticsService(  );
        mBean.setStatisticsEnabled( true );
        mBean.setSessionFactory( sessionFactory );

        ObjectName name;

        try
        {
            name = new ObjectName( "Hibernate:type=statistics-" + strPoolName + ",application=" +
                    AppPropertiesService.getProperty( "lutece.name" ) );
            mbeanServer.registerMBean( mBean, name );
        }
        catch ( Exception e )
        {
            logger.error( e.getMessage(  ), e );

            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getName(  )
    {
        return "HibernateMonitor";
    }
}
