/*
* Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.module.vti.web.fp;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.alfresco.module.vti.handler.alfresco.VtiPathHelper;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.springframework.extensions.surf.util.URLDecoder;

/**
 * Implements the WebDAV PROPPATCH method with VTI specific
 * 
 * @author DmitryVas
 */
public class PropPatchMethod extends org.alfresco.repo.webdav.PropPatchMethod
{
    private String alfrescoContext;
    private VtiPathHelper pathHelper;

    public PropPatchMethod(VtiPathHelper pathHelper)
    {
        this.alfrescoContext = pathHelper.getAlfrescoContext();
        this.pathHelper = pathHelper;
    }
    
    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#getNodeForPath(org.alfresco.service.cmr.repository.NodeRef, java.lang.String)
     */
    @Override
    protected FileInfo getNodeForPath(NodeRef rootNodeRef, String path) throws FileNotFoundException
    {
        FileInfo nodeInfo = pathHelper.resolvePathFileInfo(URLDecoder.decode(path));
        if (nodeInfo == null)
        {
        	throw new FileNotFoundException(path);
        }
        FileInfo workingCopy = getWorkingCopy(nodeInfo.getNodeRef());
        return workingCopy != null ? workingCopy : nodeInfo;
    }
    
    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#getURLForPath(javax.servlet.http.HttpServletRequest, java.lang.String, boolean)
     */
    @Override
    protected String getURLForPath(HttpServletRequest request, String path, boolean isFolder)
    {
        return getDAVHelper().getURLForPath(new HttpServletRequestWrapper(m_request)
        {
            public String getServletPath()
            {
                return alfrescoContext.equals("") ? "/" : alfrescoContext;
            }

        }, path, isFolder);
    }    
    
    /**
     * @see org.alfresco.repo.webdav.WebDAVMethod#shouldFlushXMLWriter()
     */
    @Override
    protected boolean shouldFlushXMLWriter()
    {
        // Do not flush, related to specific Office behaviour
    	return false;
    }

    /**
     * @see org.alfresco.repo.webdav.PropPatchMethod#persistDeadProperties(NodeRef, Map)
     */
    @Override
    protected void persistDeadProperties(NodeRef nodeRef, Map<QName, String> deadProperties) 
    {
        //Do nothing
    }
}
