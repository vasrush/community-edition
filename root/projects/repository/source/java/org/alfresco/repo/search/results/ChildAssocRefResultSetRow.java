/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */
package org.alfresco.repo.search.results;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.search.AbstractResultSetRow;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

/**
 * Row in child assoc ref result set.
 * @author andyh
 *
 */
public class ChildAssocRefResultSetRow extends AbstractResultSetRow
{
    /**
     * Row in child assoc ref result set
     * @param resultSet
     * @param index
     */
    public ChildAssocRefResultSetRow(ChildAssocRefResultSet resultSet, int index)
    {
        super(resultSet, index);
    }

    public QName getQName()
    {
        return ((ChildAssocRefResultSet)getResultSet()).getChildAssocRef(getIndex()).getQName();
    }

    @Override
    protected Map<QName, Serializable> getDirectProperties()
    {
        return ((ChildAssocRefResultSet)getResultSet()).getNodeService().getProperties(getNodeRef());
    }

    public ChildAssociationRef getChildAssocRef()
    {
        return ((ChildAssocRefResultSet)getResultSet()).getChildAssocRef(getIndex());
    }

    public NodeRef getNodeRef(String selectorName)
    {
        throw new UnsupportedOperationException();
    }

    public Map<String, NodeRef> getNodeRefs()
    {
        throw new UnsupportedOperationException();
    }

    public float getScore(String selectorName)
    {
        throw new UnsupportedOperationException();
    }

    public Map<String, Float> getScores()
    {
        throw new UnsupportedOperationException();
    }

}
