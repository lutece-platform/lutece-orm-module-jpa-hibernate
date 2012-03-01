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

import org.hibernate.stat.Statistics;


/**
 * Simple {@link Statistics} wrapper, with plugin name.
 * Used to display data.
 *
 */
public class HibernateStatistics
{
    private Statistics _stats;
    private String _strPoolName;

    /**
     * The pool name
     * @param strPoolName The pool name
     */
    public void setPoolName( String strPoolName )
    {
        _strPoolName = strPoolName;
    }

    /**
     * The pool name
     * @return The pool name
     */
    public String getPoolName(  )
    {
        return _strPoolName;
    }

    /**
     * Hibernate {@link Statistics}
     * @param stats Hibernate {@link Statistics}
     */
    public void setStats( Statistics stats )
    {
        _stats = stats;
    }

    /**
     * Hibernate {@link Statistics}
     * @return Hibernate {@link Statistics}
     */
    public Statistics getStats(  )
    {
        return _stats;
    }
}
