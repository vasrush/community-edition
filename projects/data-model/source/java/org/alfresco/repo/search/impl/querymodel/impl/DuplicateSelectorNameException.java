/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.repo.search.impl.querymodel.impl;

import org.alfresco.error.AlfrescoRuntimeException;

/**
 * @author andyh
 *
 */
public class DuplicateSelectorNameException extends AlfrescoRuntimeException
{

    /**
     * @param msgId String
     * @param msgParams Object[]
     * @param cause Throwable
     */
    public DuplicateSelectorNameException(String msgId, Object[] msgParams, Throwable cause)
    {
        super(msgId, msgParams, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     * @param msgParams Object[]
     */
    public DuplicateSelectorNameException(String msgId, Object[] msgParams)
    {
        super(msgId, msgParams);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     * @param cause Throwable
     */
    public DuplicateSelectorNameException(String msgId, Throwable cause)
    {
        super(msgId, cause);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param msgId String
     */
    public DuplicateSelectorNameException(String msgId)
    {
        super(msgId);
        // TODO Auto-generated constructor stub
    }

    /**
     * 
     */
    private static final long serialVersionUID = 3163974668059624874L;

}
