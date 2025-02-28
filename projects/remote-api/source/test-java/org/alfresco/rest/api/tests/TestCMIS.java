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
package org.alfresco.rest.api.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.cmis.client.AlfrescoDocument;
import org.alfresco.cmis.client.AlfrescoFolder;
import org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl;
import org.alfresco.cmis.client.type.AlfrescoType;
import org.alfresco.model.ContentModel;
import org.alfresco.opencmis.CMISDispatcherRegistry.Binding;
import org.alfresco.opencmis.dictionary.CMISStrictDictionaryService;
import org.alfresco.opencmis.dictionary.QNameFilter;
import org.alfresco.opencmis.dictionary.QNameFilterImpl;
import org.alfresco.opencmis.mapping.NodeRefProperty;
import org.alfresco.repo.action.ActionModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.filestore.FileContentWriter;
import org.alfresco.repo.dictionary.DictionaryBootstrap;
import org.alfresco.repo.dictionary.DictionaryDAO;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.tenant.TenantService;
import org.alfresco.repo.tenant.TenantUtil;
import org.alfresco.repo.tenant.TenantUtil.TenantRunAsWork;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.repo.version.VersionableAspectTest;
import org.alfresco.rest.api.tests.RepoService.SiteInformation;
import org.alfresco.rest.api.tests.RepoService.TestNetwork;
import org.alfresco.rest.api.tests.RepoService.TestPerson;
import org.alfresco.rest.api.tests.RepoService.TestSite;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.CmisSession;
import org.alfresco.rest.api.tests.client.PublicApiClient.Comments;
import org.alfresco.rest.api.tests.client.PublicApiClient.ListResponse;
import org.alfresco.rest.api.tests.client.PublicApiClient.Nodes;
import org.alfresco.rest.api.tests.client.PublicApiClient.Sites;
import org.alfresco.rest.api.tests.client.PublicApiException;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.api.tests.client.data.CMISNode;
import org.alfresco.rest.api.tests.client.data.Comment;
import org.alfresco.rest.api.tests.client.data.FolderNode;
import org.alfresco.rest.api.tests.client.data.MemberOfSite;
import org.alfresco.rest.api.tests.client.data.NodeRating;
import org.alfresco.rest.api.tests.client.data.NodeRating.Aggregate;
import org.alfresco.rest.api.tests.client.data.Person;
import org.alfresco.rest.api.tests.client.data.SiteRole;
import org.alfresco.rest.api.tests.client.data.Tag;
import org.alfresco.service.cmr.lock.LockService;
import org.alfresco.service.cmr.lock.LockType;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.TempFileProvider;
import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.SecondaryType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.runtime.OperationContextImpl;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisUpdateConflictException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.util.URLEncoder;

public class TestCMIS extends EnterpriseTestApi
{
    private static final String CMIS_VERSION_10 = "1.0";

    private static final String CMIS_VERSION_11 = "1.1";

    private static final String TYPE_CMIS_DOCUMENT = "cmis:document";


    private static final String DOCUMENT_LIBRARY_CONTAINER_NAME = "documentLibrary";

    private static final String TEST_SITE_NAME_PATTERN = "testSite-%d";

    private static final String TEST_USER_NAME_PATTERN = "testUser-%d";

    private static final String TEST_DOCUMENT_NAME_PATTERN = "testDocument-%s.txt";

	private static final String DOCUMENT_LIBRARY_PATH_PATTERN = "/Sites/%s/" + DOCUMENT_LIBRARY_CONTAINER_NAME;

    private static final String TEST_PASSWORD = "password";


    private DictionaryDAO dictionaryDAO;
    private LockService lockService;
    private TenantService tenantService;
    private CMISStrictDictionaryService cmisDictionary;
    private QNameFilter cmisTypeExclusions;
	private NodeService nodeService;
	private Properties globalProperties;

    @Before
    public void before() throws Exception
    {
        ApplicationContext ctx = getTestFixture().getApplicationContext();
        this.dictionaryDAO = (DictionaryDAO)ctx.getBean("dictionaryDAO");
        this.lockService = (LockService) ctx.getBean("lockService");
        this.tenantService = (TenantService)ctx.getBean("tenantService");
        this.cmisDictionary = (CMISStrictDictionaryService)ctx.getBean("OpenCMISDictionaryService");
        this.cmisTypeExclusions = (QNameFilter)ctx.getBean("cmisTypeExclusions");
		this.nodeService = (NodeService) ctx.getBean("NodeService");
        
		this.globalProperties = (Properties) ctx.getBean("global-properties");
		this.globalProperties.setProperty(VersionableAspectTest.AUTO_VERSION_PROPS_KEY, "true");
    }
    
    @After
    public void after()
    {
        this.globalProperties.setProperty(VersionableAspectTest.AUTO_VERSION_PROPS_KEY, "false");
    }

    private void checkSecondaryTypes(Document doc, Set<String> expectedSecondaryTypes, Set<String> expectedMissingSecondaryTypes)
    {
        final List<SecondaryType> secondaryTypesList = doc.getSecondaryTypes();
        assertNotNull(secondaryTypesList);
        List<String> secondaryTypes = new AbstractList<String>()
        {
            @Override
            public String get(int index)
            {
                SecondaryType type = secondaryTypesList.get(index);
                return type.getId();
            }

            @Override
            public int size()
            {
                return secondaryTypesList.size();
            }
        };
        if(expectedSecondaryTypes != null)
        {
            assertTrue("Missing secondary types: " + secondaryTypes, secondaryTypes.containsAll(expectedSecondaryTypes));
        }
        if(expectedMissingSecondaryTypes != null)
        {
            assertTrue("Expected missing secondary types but at least one is still present: " + secondaryTypes, !secondaryTypes.containsAll(expectedMissingSecondaryTypes));
        }
    }
       
    private String getBareObjectId(String objectId)
    {
        int idx = objectId.indexOf(";");
        String bareObjectId = null;
        if(idx != -1)
        {
            bareObjectId = objectId.substring(0, idx);
        }
        else
        {
            bareObjectId = objectId;
        }

        return bareObjectId;
    }

    /**
     * Tests OpenCMIS api.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCMIS() throws Exception
    {
        // Test Case cloud-2353
        // Test Case cloud-2354
        // Test Case cloud-2356
        // Test Case cloud-2378
        // Test Case cloud-2357
        // Test Case cloud-2358
        // Test Case cloud-2360

        final TestNetwork network1 = getTestFixture().getRandomNetwork();
        Iterator<String> personIt = network1.getPersonIds().iterator();
        final String personId = personIt.next();
        assertNotNull(personId);
        Person person = repoService.getPerson(personId);
        assertNotNull(person);

        // Create a site
        final TestSite site = TenantUtil.runAsUserTenant(new TenantRunAsWork<TestSite>()
        {
            @Override
            public TestSite doWork() throws Exception
            {
                String siteName = "site" + System.currentTimeMillis();
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = network1.createSite(siteInfo);
                return site;
            }
        }, personId, network1.getId());

        publicApiClient.setRequestContext(new RequestContext(network1.getId(), personId));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
        Nodes nodesProxy = publicApiClient.nodes();
        Comments commentsProxy = publicApiClient.comments();

        String expectedContent = "Ipsum and so on";
        Document doc = null;
        Folder documentLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary");
        FolderNode expectedDocumentLibrary = (FolderNode)CMISNode.createNode(documentLibrary);
        Document testDoc = null;
        Folder testFolder = null;
        FolderNode testFolderNode = null;

        // create some sub-folders and documents
        {
            for(int i = 0; i < 3; i++)
            {
                Map<String, String> properties = new HashMap<String, String>();
                {
                    properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
                    properties.put(PropertyIds.NAME, "folder-" + i);
                }

                Folder f = documentLibrary.createFolder(properties);
                FolderNode fn = (FolderNode)CMISNode.createNode(f);
                if(testFolder == null)
                {
                    testFolder = f;
                    testFolderNode = fn;
                }
                expectedDocumentLibrary.addFolder(fn);

                for(int k = 0; k < 3; k++)
                {
                    properties = new HashMap<String, String>();
                    {
                        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
                        properties.put(PropertyIds.NAME, "folder-" + k);
                    }

                    Folder f1 = f.createFolder(properties);
                    FolderNode childFolder = (FolderNode)CMISNode.createNode(f1);
                    fn.addFolder(childFolder);
                }
                
                for(int j = 0; j < 3; j++)
                {
                    properties = new HashMap<String, String>();
                    {
			        	properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
                        properties.put(PropertyIds.NAME, "doc-" + j);
                    }

                    ContentStreamImpl fileContent = new ContentStreamImpl();
                    {
                        ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                        writer.putContent(expectedContent);
                        ContentReader reader = writer.getReader();
                        fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                        fileContent.setStream(reader.getContentInputStream());
                    }

                    Document d = f.createDocument(properties, fileContent, VersioningState.MAJOR);
                    if(testDoc == null)
                    {
                        testDoc = d;
                    }

                    CMISNode childDocument = CMISNode.createNode(d);
                    fn.addNode(childDocument);
                }
            }

            for(int i = 0; i < 10; i++)
            {
                Map<String, String> properties = new HashMap<String, String>();
                {
		        	properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
                    properties.put(PropertyIds.NAME, "doc-" + i);
                }

                ContentStreamImpl fileContent = new ContentStreamImpl();
                {
                    ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                    writer.putContent(expectedContent);
                    ContentReader reader = writer.getReader();
                    fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                    fileContent.setStream(reader.getContentInputStream());
                }

                documentLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
            }
        }

        // try to add and remove ratings, comments, tags to folders created by CMIS
        {
            Aggregate aggregate = new Aggregate(1, null);
            NodeRating expectedNodeRating = new NodeRating("likes", true, aggregate);
            Comment expectedComment = new Comment("commenty", "commenty", false, null, person, person);
            Tag expectedTag = new Tag("taggy");

            NodeRating rating = nodesProxy.createNodeRating(testFolder.getId(), expectedNodeRating);
            expectedNodeRating.expected(rating);
            assertNotNull(rating.getId());
            
            Tag tag = nodesProxy.createNodeTag(testFolder.getId(), expectedTag);
            expectedTag.expected(tag);
            assertNotNull(tag.getId());

            Comment comment = commentsProxy.createNodeComment(testFolder.getId(), expectedComment);
            expectedComment.expected(comment);
            assertNotNull(comment.getId());
        }

        // try to add and remove ratings, comments, tags to documents created by CMIS
        {
            Aggregate aggregate = new Aggregate(1, null);
            NodeRating expectedNodeRating = new NodeRating("likes", true, aggregate);
            Comment expectedComment = new Comment("commenty", "commenty", false, null, person, person);
            Tag expectedTag = new Tag("taggy");

            NodeRating rating = nodesProxy.createNodeRating(testDoc.getId(), expectedNodeRating);
            expectedNodeRating.expected(rating);
            assertNotNull(rating.getId());

            Tag tag = nodesProxy.createNodeTag(testDoc.getId(), expectedTag);
            expectedTag.expected(tag);
            assertNotNull(tag.getId());

            Comment comment = commentsProxy.createNodeComment(testDoc.getId(), expectedComment);
            expectedComment.expected(comment);
            assertNotNull(comment.getId());
        }

        // descendants
        {
            List<Tree<FileableCmisObject>> descendants = documentLibrary.getDescendants(4);
            expectedDocumentLibrary.checkDescendants(descendants);
        }

        // upload/setContent
        {
            Map<String, String> fileProps = new HashMap<String, String>();
            {
	            fileProps.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
                fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
            }
            ContentStreamImpl fileContent = new ContentStreamImpl();
            {
                ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                writer.putContent(expectedContent);
                ContentReader reader = writer.getReader();
                fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                fileContent.setStream(reader.getContentInputStream());
            }
            doc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);

            String nodeId = stripCMISSuffix(doc.getId());
            final NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);
            ContentReader reader = TenantUtil.runAsUserTenant(new TenantRunAsWork<ContentReader>()
            {
                @Override
                public ContentReader doWork() throws Exception
                {
                    ContentReader reader = repoService.getContent(nodeRef, ContentModel.PROP_CONTENT);
                    return reader;
                }
            }, personId, network1.getId());

            String actualContent = reader.getContentString();
            assertEquals(expectedContent, actualContent);
        }

        // get content
        {
            ContentStream stream = doc.getContentStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream.getStream(), writer, "UTF-8");
            String actualContent = writer.toString();
            assertEquals(expectedContent, actualContent);
        }
        
        // get children
        {
            Folder folder = (Folder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary/" + testFolder.getName());

            ItemIterable<CmisObject> children = folder.getChildren();
            testFolderNode.checkChildren(children);
        }
        
        // query
        {
            Folder folder = (Folder)cmisSession.getObjectByPath("/Sites/" + site.getSiteId() + "/documentLibrary/" + testFolder.getName());
            String folderId = folder.getId();

            Set<String> expectedFolderNames = new HashSet<String>();
            for(CMISNode n : testFolderNode.getFolderNodes().values())
            {
                expectedFolderNames.add((String)n.getProperty("cmis:name"));
            }
            int expectedNumFolders = expectedFolderNames.size();
            int numMatchingFoldersFound = 0;
            List<CMISNode> results = cmisSession.query("SELECT * FROM cmis:folder WHERE IN_TREE('" + folderId + "')", false, 0, Integer.MAX_VALUE);
            for(CMISNode node : results)
            {
                String name = (String)node.getProperties().get("cmis:name");
                if(expectedFolderNames.contains(name))
                {
                    numMatchingFoldersFound++;
                }
            }
            assertEquals(expectedNumFolders, numMatchingFoldersFound);

            Set<String> expectedDocNames = new HashSet<String>();
            for(CMISNode n : testFolderNode.getDocumentNodes().values())
            {
                expectedDocNames.add((String)n.getProperty("cmis:name"));
            }
            int expectedNumDocs = expectedDocNames.size();
            int numMatchingDocsFound = 0;
            results = cmisSession.query("SELECT * FROM cmis:document where IN_TREE('" + folderId + "')", false, 0, Integer.MAX_VALUE);
            for(CMISNode node : results)
            {
                String name = (String)node.getProperties().get("cmis:name");
                if(expectedDocNames.contains(name))
                {
                    numMatchingDocsFound++;
                }
            }
            assertEquals(expectedNumDocs, numMatchingDocsFound);
        }

        // versioning
        {
            String nodeId = stripCMISSuffix(doc.getId());
            final NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, nodeId);

            // checkout
            ObjectId pwcId = doc.checkOut();
            Document pwc = (Document)cmisSession.getObject(pwcId.getId());
            Boolean isCheckedOut = TenantUtil.runAsUserTenant(new TenantRunAsWork<Boolean>()
            {
                @Override
                public Boolean doWork() throws Exception
                {
                    Boolean isCheckedOut = repoService.isCheckedOut(nodeRef);
                    return isCheckedOut;
                }
            }, personId, network1.getId());
            assertTrue(isCheckedOut);

            // checkin with new content
            expectedContent = "Big bad wolf";

            ContentStreamImpl fileContent = new ContentStreamImpl();
            {
                ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                writer.putContent(expectedContent);
                ContentReader reader = writer.getReader();
                fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                fileContent.setStream(reader.getContentInputStream());
            }
            ObjectId checkinId = pwc.checkIn(true, Collections.EMPTY_MAP, fileContent, "checkin 1");
            doc = (Document)cmisSession.getObject(checkinId.getId());
            isCheckedOut = TenantUtil.runAsUserTenant(new TenantRunAsWork<Boolean>()
            {
                @Override
                public Boolean doWork() throws Exception
                {
                    Boolean isCheckedOut = repoService.isCheckedOut(nodeRef);
                    return isCheckedOut;
                }
            }, personId, network1.getId());
            assertFalse(isCheckedOut);

            // check that the content has been updated
            ContentStream stream = doc.getContentStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(stream.getStream(), writer, "UTF-8");
            String actualContent = writer.toString();
            assertEquals(expectedContent, actualContent);
            
            List<Document> allVersions = doc.getAllVersions();
            assertEquals(2, allVersions.size());
            assertEquals("2.0", allVersions.get(0).getVersionLabel());
			assertEquals(CMIS_VERSION_10, allVersions.get(1).getVersionLabel());
        }
        
        {
            // https://issues.alfresco.com/jira/browse/PUBLICAPI-95
            // Test that documents are created with autoVersion=true

            Map<String, String> fileProps = new HashMap<String, String>();
            {
	            fileProps.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
                fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
            }
            ContentStreamImpl fileContent = new ContentStreamImpl();
            {
                ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                writer.putContent("Ipsum and so on");
                ContentReader reader = writer.getReader();
                fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                fileContent.setStream(reader.getContentInputStream());
            }

            {
                // a versioned document
                
                Document autoVersionedDoc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);
                String objectId = autoVersionedDoc.getId();
                String bareObjectId = getBareObjectId(objectId);
                final NodeRef nodeRef = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, bareObjectId);
                Boolean autoVersion = TenantUtil.runAsUserTenant(new TenantRunAsWork<Boolean>()
                {
                    @Override
                    public Boolean doWork() throws Exception
                    {
                        Boolean autoVersion = (Boolean)repoService.getProperty(nodeRef, ContentModel.PROP_AUTO_VERSION);
                        return autoVersion;
                    }
                }, personId, network1.getId());
                assertEquals(Boolean.TRUE, autoVersion);
            }

            // https://issues.alfresco.com/jira/browse/PUBLICAPI-92
            // Test that a get on an objectId without a version suffix returns the current version of the document
            {
                // do a few checkout, checkin cycles to create some versions
                fileProps = new HashMap<String, String>();
                {
		            fileProps.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
                    fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
                }

                Document autoVersionedDoc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);
                String objectId = autoVersionedDoc.getId();
                String bareObjectId = getBareObjectId(objectId);

                for(int i = 0; i < 3; i++)
                {
                    Document doc1 = (Document)cmisSession.getObject(bareObjectId);

                    ObjectId pwcId = doc1.checkOut();
                    Document pwc = (Document)cmisSession.getObject(pwcId.getId());
                    
                    ContentStreamImpl contentStream = new ContentStreamImpl();
                    {
                        ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                        expectedContent = GUID.generate();
                        writer.putContent(expectedContent);
                        ContentReader reader = writer.getReader();
                        contentStream.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                        contentStream.setStream(reader.getContentInputStream());
                    }
                    pwc.checkIn(true, Collections.EMPTY_MAP, contentStream, "checkin " + i);
                }
                
                // get the object, supplying an objectId without a version suffix
                Document doc1 = (Document)cmisSession.getObject(bareObjectId);
                String versionLabel = doc1.getVersionLabel();
                ContentStream cs = doc1.getContentStream();
                String content = IOUtils.toString(cs.getStream());
                
                assertEquals("4.0", versionLabel);
                assertEquals(expectedContent, content);
            }
        }
    }

    /**
     * Tests CMIS and non-CMIS public api interactions
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testScenario1() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();
        Iterator<String> personIt = network1.getPersonIds().iterator();
        final String person = personIt.next();
        assertNotNull(person);

        Sites sitesProxy = publicApiClient.sites();
        Comments commentsProxy = publicApiClient.comments();
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
        
        ListResponse<MemberOfSite> sites = sitesProxy.getPersonSites(person, null);
        assertTrue(sites.getList().size() > 0);
        MemberOfSite siteMember = sites.getList().get(0);
        String siteId = siteMember.getSite().getSiteId();

        Folder documentLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + siteId + "/documentLibrary");
        
        System.out.println("documentLibrary id = " + documentLibrary.getId());

        Map<String, String> fileProps = new HashMap<String, String>();
        {
            fileProps.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            fileProps.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
        }
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }
        Document doc = documentLibrary.createDocument(fileProps, fileContent, VersioningState.MAJOR);

        System.out.println("Document id = " + doc.getId());

        Comment c = commentsProxy.createNodeComment(doc.getId(), new Comment("comment title 1", "comment 1"));
        
        System.out.println("Comment = " + c);
        
        // Now lock the document
        String nodeRefStr = (String) doc.getPropertyValue("alfcmis:nodeRef");
        final NodeRef nodeRef = new NodeRef(nodeRefStr);
        final TenantRunAsWork<Void> runAsWork = new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                lockService.lock(nodeRef, LockType.WRITE_LOCK);
                return null;
            }
        };
        RetryingTransactionCallback<Void> txnWork = new RetryingTransactionCallback<Void>()
        {
            @Override
            public Void execute() throws Throwable
            {
                TenantUtil.runAsUserTenant(runAsWork, "bob", network1.getId());
                return null;
            }
        };
        transactionHelper.doInTransaction(txnWork);
        
        // Now attempt to update the document's metadata
        try
        {
            doc.delete();
        }
        catch (CmisUpdateConflictException e)
        {
            // Expected: ACE-762 BM-0012: NodeLockedException not handled by CMIS 
        }
    }

    //@Test
    public void testInvalidMethods() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();
        Iterator<String> personIt = network1.getPersonIds().iterator();
        final String person = personIt.next();
        assertNotNull(person);

        try
        {
            publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));

			publicApiClient.post(Binding.atom, CMIS_VERSION_10, null, null);
            
            fail();
        }
        catch(PublicApiException e)
        {
            assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
        }
        
        try
        {
            publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
            
			publicApiClient.head(Binding.atom, CMIS_VERSION_10, null, null);
            
            fail();
        }
        catch(PublicApiException e)
        {
            assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
        }
        
        try
        {
            publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
            
			publicApiClient.options(Binding.atom, CMIS_VERSION_10, null, null);
            
            fail();
        }
        catch(PublicApiException e)
        {
            assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
        }
        
        try
        {
            publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
            
			publicApiClient.trace(Binding.atom, CMIS_VERSION_10, null, null);
            
            fail();
        }
        catch(PublicApiException e)
        {
            assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
        }
        
        try
        {
            publicApiClient.setRequestContext(new RequestContext(network1.getId(), person));
            
			publicApiClient.patch(Binding.atom, CMIS_VERSION_10, null, null);
            
            fail();
        }
        catch(PublicApiException e)
        {
            assertEquals(HttpStatus.SC_METHOD_NOT_ALLOWED, e.getHttpResponse().getStatusCode());
        }
    }
    
    @Test
    public void testPublicApi110() throws Exception
    {
        Iterator<TestNetwork> networksIt = getTestFixture().networksIterator();
        final TestNetwork network1 = networksIt.next();
        Iterator<String> personIt = network1.getPersonIds().iterator();
        final String person1Id = personIt.next();
        final String person2Id = personIt.next();

        final List<NodeRef> nodes = new ArrayList<NodeRef>(5);
        
        // Create some favourite targets, sites, files and folders
        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                String siteName1 = "site" + GUID.generate();
                SiteInformation siteInfo1 = new SiteInformation(siteName1, siteName1, siteName1, SiteVisibility.PUBLIC);
                TestSite site1 = network1.createSite(siteInfo1);
                
                String siteName2 = "site" + GUID.generate();
                SiteInformation siteInfo2 = new SiteInformation(siteName2, siteName2, siteName2, SiteVisibility.PRIVATE);
                TestSite site2 = network1.createSite(siteInfo2);

				NodeRef nodeRef1 = repoService.createDocument(site1.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), "Test Doc1", "Test Doc1 Title", "Test Doc1 Description", "Test Content");
                nodes.add(nodeRef1);
				NodeRef nodeRef2 = repoService.createDocument(site1.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), "Test Doc2", "Test Doc2 Title", "Test Doc2 Description", "Test Content");
                nodes.add(nodeRef2);
				NodeRef nodeRef3 = repoService.createDocument(site2.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), "Test Doc2", "Test Doc2 Title", "Test Doc2 Description", "Test Content");
                nodes.add(nodeRef3);
                repoService.createAssociation(nodeRef2, nodeRef1, ContentModel.ASSOC_ORIGINAL);
                repoService.createAssociation(nodeRef3, nodeRef1, ContentModel.ASSOC_ORIGINAL);

                site1.inviteToSite(person2Id, SiteRole.SiteCollaborator);

                return null;
            }
        }, person1Id, network1.getId());

        {
            OperationContext cmisOperationCtxOverride = new OperationContextImpl();
            cmisOperationCtxOverride.setIncludeRelationships(IncludeRelationships.BOTH);
            publicApiClient.setRequestContext(new RequestContext(network1.getId(), person2Id, cmisOperationCtxOverride));
    		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());

            CmisObject o1 = cmisSession.getObject(nodes.get(0).getId());
            List<Relationship> relationships = o1.getRelationships();
            assertEquals(1, relationships.size());
            Relationship r = relationships.get(0);
            CmisObject source = r.getSource();
            CmisObject target = r.getTarget();
            String sourceVersionSeriesId = (String)source.getProperty(PropertyIds.VERSION_SERIES_ID).getFirstValue();
            String targetVersionSeriesId = (String)target.getProperty(PropertyIds.VERSION_SERIES_ID).getFirstValue();
            assertEquals(nodes.get(1).getId(), sourceVersionSeriesId);
            assertEquals(nodes.get(0).getId(), targetVersionSeriesId);
        }
    }
    
    @Test
    public void testObjectIds() throws Exception
    {
        String username = "enterpriseuser" + System.currentTimeMillis();
		PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person = repoService.createUser(personInfo, username, null);
        String personId = person.getId();

        final List<NodeRef> folders = new ArrayList<NodeRef>();
        final List<NodeRef> documents = new ArrayList<NodeRef>();

        AuthenticationUtil.runAs(new RunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                String siteName = "site" + System.currentTimeMillis();
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);
                
                String name = GUID.generate();
				NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                folders.add(folderNodeRef);

                name = GUID.generate();
                NodeRef docNodeRef = repoService.createDocument(folderNodeRef, name, "test content");
                documents.add(docNodeRef);

                return null;
            }
        }, personId);
        
        NodeRef folderNodeRef = folders.get(0);
        NodeRef docNodeRef = documents.get(0);

        publicApiClient.setRequestContext(new RequestContext(personId));

        // use cmisatom endpoint
        List<Repository> repositories = publicApiClient.getCMISRepositories();
        CmisSession cmisSession = publicApiClient.getCMISSession(repositories.get(0));

        // test CMIS accepts NodeRefs and guids as input
        // if input is NodeRef, return NodeRef. If input is guid, return guid.
        {
            String nodeRefStr = docNodeRef.toString();
            CmisObject o = cmisSession.getObject(nodeRefStr);
            assertEquals(docNodeRef.toString(), stripCMISSuffix(o.getId()));
    
            nodeRefStr = folderNodeRef.toString();
            o = cmisSession.getObject(nodeRefStr);
            assertEquals(folderNodeRef.toString(), stripCMISSuffix(o.getId()));
            
            String objectId = docNodeRef.getId();
            o = cmisSession.getObject(objectId);
            assertEquals(objectId, stripCMISSuffix(o.getId()));
    
            objectId = folderNodeRef.getId();
            o = cmisSession.getObject(objectId);
            assertEquals(objectId, stripCMISSuffix(o.getId()));
        }

        // query
        {
            // searching by NodeRef, expect result objectIds to be Noderefs
            Set<String> expectedObjectIds = new HashSet<String>();
            expectedObjectIds.add(docNodeRef.toString());
            int numMatchingDocs = 0;

            // NodeRef input
            List<CMISNode> results = cmisSession.query("SELECT * FROM cmis:document WHERE IN_TREE('" + folderNodeRef.toString() + "')", false, 0, Integer.MAX_VALUE);
            assertEquals(expectedObjectIds.size(), results.size());
            for(CMISNode node : results)
            {
                String objectId = stripCMISSuffix((String)node.getProperties().get(PropertyIds.OBJECT_ID));
                if(expectedObjectIds.contains(objectId))
                {
                    numMatchingDocs++;
                }
            }
            assertEquals(expectedObjectIds.size(), numMatchingDocs);

            // searching by guid, expect result objectIds to be NodeRefs
            numMatchingDocs = 0;

            // node guid input
            results = cmisSession.query("SELECT * FROM cmis:document WHERE IN_TREE('" + folderNodeRef.getId() + "')", false, 0, Integer.MAX_VALUE);
            assertEquals(expectedObjectIds.size(), results.size());
            for(CMISNode node : results)
            {
                String objectId = stripCMISSuffix((String)node.getProperties().get(PropertyIds.OBJECT_ID));
                System.out.println("objectId = " + objectId);
                if(expectedObjectIds.contains(objectId))
                {
                    numMatchingDocs++;
                }
            }
            assertEquals(expectedObjectIds.size(), numMatchingDocs);
        }

        // public api
        
        Iterator<TestNetwork> networksIt = getTestFixture().networksIterator();
        final TestNetwork network1 = networksIt.next();
        Iterator<String> personIt = network1.getPersonIds().iterator();
        final String person1Id = personIt.next();

        final List<NodeRef> folders1 = new ArrayList<NodeRef>();
        final List<NodeRef> documents1 = new ArrayList<NodeRef>();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                String siteName = "site" + System.currentTimeMillis();
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);
                
                String name = GUID.generate();
				NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                folders1.add(folderNodeRef);

                name = GUID.generate();
                NodeRef docNodeRef = repoService.createDocument(folderNodeRef, name, "test content");
                documents1.add(docNodeRef);

                return null;
            }
        }, person1Id, network1.getId());
        
        folderNodeRef = folders1.get(0);
        docNodeRef = documents1.get(0);

        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        
		cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());

        // test CMIS accepts NodeRefs and guids as input
        // objectIds returned from public api CMIS are always the guid
        {
            String nodeRefStr = docNodeRef.toString();
            CmisObject o = cmisSession.getObject(nodeRefStr);
            String objectId = docNodeRef.getId();
            assertEquals(objectId, stripCMISSuffix(o.getId()));
    
            nodeRefStr = folderNodeRef.toString();
            o = cmisSession.getObject(nodeRefStr);
            objectId = folderNodeRef.getId();
            assertEquals(objectId, stripCMISSuffix(o.getId()));

            o = cmisSession.getObject(objectId);
            assertEquals(objectId, stripCMISSuffix(o.getId()));
    
            objectId = folderNodeRef.getId();
            o = cmisSession.getObject(objectId);
            assertEquals(objectId, stripCMISSuffix(o.getId()));
        }

        // query
        {
            // searching by NodeRef, expect result objectIds to be objectId
            Set<String> expectedObjectIds = new HashSet<String>();
            expectedObjectIds.add(docNodeRef.getId());
            int numMatchingDocs = 0;

            // NodeRef input
            List<CMISNode> results = cmisSession.query("SELECT * FROM cmis:document WHERE IN_TREE('" + folderNodeRef.toString() + "')", false, 0, Integer.MAX_VALUE);
            assertEquals(expectedObjectIds.size(), results.size());
            for(CMISNode node : results)
            {
                String objectId = stripCMISSuffix((String)node.getProperties().get(PropertyIds.OBJECT_ID));
                if(expectedObjectIds.contains(objectId))
                {
                    numMatchingDocs++;
                }
            }
            assertEquals(expectedObjectIds.size(), numMatchingDocs);

            // searching by guid, expect result objectIds to be objectId
            numMatchingDocs = 0;

            // node guid input
            results = cmisSession.query("SELECT * FROM cmis:document WHERE IN_TREE('" + folderNodeRef.getId() + "')", false, 0, Integer.MAX_VALUE);
            assertEquals(expectedObjectIds.size(), results.size());
            for(CMISNode node : results)
            {
                String objectId = stripCMISSuffix((String)node.getProperties().get(PropertyIds.OBJECT_ID));
                System.out.println("objectId = " + objectId);
                if(expectedObjectIds.contains(objectId))
                {
                    numMatchingDocs++;
                }
            }
            assertEquals(expectedObjectIds.size(), numMatchingDocs);
        }
    }
    
    @Test
    public void testAspects() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
		PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final List<NodeRef> folders = new ArrayList<NodeRef>();
        final List<NodeRef> documents = new ArrayList<NodeRef>();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                String siteName = "site" + System.currentTimeMillis();
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);
                String name = GUID.generate();
				NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                folders.add(folderNodeRef);

                for(int i = 0; i < 3; i++)
                {
                    name = GUID.generate();
                    NodeRef docNodeRef = repoService.createDocument(folderNodeRef, name, "test content");
                    assertFalse(repoService.getAspects(docNodeRef).contains(ContentModel.ASPECT_TITLED));
                    documents.add(docNodeRef);
                }

                return null;
            }
        }, person1Id, network1.getId());

        final NodeRef doc1NodeRef = documents.get(0);
        final NodeRef doc2NodeRef = documents.get(1);
        final NodeRef doc3NodeRef = documents.get(2);

        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
		CmisSession atomCmisSession10 = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
		CmisSession atomCmisSession11 = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
		CmisSession browserCmisSession11 = publicApiClient.createPublicApiCMISSession(Binding.browser, CMIS_VERSION_11);

        // Test that adding aspects works for both 1.0 and 1.1

        // 1.0
        {
            AlfrescoDocument doc = (AlfrescoDocument)atomCmisSession10.getObject(doc1NodeRef.getId());

            doc = (AlfrescoDocument)doc.addAspect("P:cm:titled");
            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc1NodeRef);
                    assertTrue("Missing aspect in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));
    
                    return null;
                }
            }, person1Id, network1.getId());

            doc.removeAspect("P:cm:titled");
            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc1NodeRef);
                    assertFalse("Unexpected aspect in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));
    
                    return null;
                }
            }, person1Id, network1.getId());
        }

        // 1.1 atom (secondary types)
        {
            final Document doc = (Document)atomCmisSession11.getObject(doc2NodeRef.getId());

            final List<SecondaryType> secondaryTypesList = doc.getSecondaryTypes();
            final List<String> secondaryTypes = new ArrayList<String>();
            if (secondaryTypesList != null)
            {
                for(SecondaryType secondaryType : secondaryTypesList)
                {
                    secondaryTypes.add(secondaryType.getId());
                }
            }

            secondaryTypes.add("P:sys:temporary");
            secondaryTypes.add("P:cm:titled");
            Map<String, Object> properties = new HashMap<String, Object>();
            {
                // create a document with 2 secondary types
                properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
            }
            Document doc1 = (Document)doc.updateProperties(properties);
            checkSecondaryTypes(doc1, new HashSet<String>(Arrays.asList(new String[] {"P:sys:temporary", "P:cm:titled"})), null);

            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc2NodeRef);
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TEMPORARY));

                    return null;
                }
            }, person1Id, network1.getId());

            secondaryTypes.add("P:cm:author");
            properties = new HashMap<String, Object>();
            {
                // create a document with 2 secondary types
                properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
            }
            Document doc2 = (Document)doc1.updateProperties(properties);
            checkSecondaryTypes(doc2, new HashSet<String>(Arrays.asList(new String[] {"P:sys:temporary", "P:cm:titled", "P:cm:author"})), null);
            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc2NodeRef);
                    String title = (String)repoService.getProperty(doc2NodeRef, ContentModel.PROP_TITLE);
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_AUTHOR));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TEMPORARY));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));
                    assertEquals(null, title);

                    return null;
                }
            }, person1Id, network1.getId());

            // remove a secondary type
            secondaryTypes.remove("P:cm:titled");
            properties = new HashMap<String, Object>();
            {
                properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
            }
            Document doc3 = (Document)doc2.updateProperties(properties);
            checkSecondaryTypes(doc3, new HashSet<String>(Arrays.asList(new String[] {"P:sys:temporary", "P:cm:author"})),
                    new HashSet<String>(Arrays.asList(new String[] {"P:cm:titled"})));
            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc2NodeRef);
                    String title = (String)repoService.getProperty(doc2NodeRef, ContentModel.PROP_TITLE);
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_AUTHOR));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TEMPORARY));
                    assertFalse("Unexpected aspect in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));
                    assertEquals(null, title);

                    return null;
                }
            }, person1Id, network1.getId());
        }

        // 1.1 browser (secondary types)
        {
            Document doc = (Document)browserCmisSession11.getObject(doc3NodeRef.getId());
            final List<SecondaryType> secondaryTypesList = doc.getSecondaryTypes();
            final List<String> secondaryTypes = new ArrayList<String>();
            if (secondaryTypesList != null)
            {
                for (SecondaryType secondaryType : secondaryTypesList)
                {
                    secondaryTypes.add(secondaryType.getId());
                }
            }

            secondaryTypes.add("P:sys:temporary");
            secondaryTypes.add("P:cm:titled");
            Map<String, Object> properties = new HashMap<String, Object>();
            {
                // create a document with 2 secondary types
                properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
            }
            Document doc1 = (Document)doc.updateProperties(properties);
            checkSecondaryTypes(doc1, new HashSet<String>(Arrays.asList(new String[] {"P:sys:temporary", "P:cm:titled"})), null);

            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc3NodeRef);
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TEMPORARY));
                    return null;
                }
            }, person1Id, network1.getId());

            secondaryTypes.add("P:cm:author");
            properties = new HashMap<String, Object>();
            {
                // create a document with 2 secondary types
                properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
            }
            Document doc2 = (Document)doc1.updateProperties(properties);
            checkSecondaryTypes(doc2, new HashSet<String>(Arrays.asList(new String[] {"P:sys:temporary", "P:cm:titled", "P:cm:author"})), null);
            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc3NodeRef);
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TEMPORARY));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_AUTHOR));

                    return null;
                }
            }, person1Id, network1.getId());

            secondaryTypes.remove("P:cm:titled");
            properties = new HashMap<String, Object>();
            {
                // create a document with 2 secondary types
                properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
            }
            Document doc3 = (Document)doc2.updateProperties(properties);
            checkSecondaryTypes(doc3, new HashSet<String>(Arrays.asList(new String[] {"P:sys:temporary", "P:cm:author"})),
                    new HashSet<String>(Arrays.asList(new String[] {"P:cm:titled"})));
            TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
            {
                @Override
                public Void doWork() throws Exception
                {
                    Set<QName> aspects = repoService.getAspects(doc3NodeRef);
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_AUTHOR));
                    assertTrue("Missing aspects in current set " + aspects, aspects.contains(ContentModel.ASPECT_TEMPORARY));
                    assertFalse("Unexpected aspect in current set " + aspects, aspects.contains(ContentModel.ASPECT_TITLED));

                    return null;
                }
            }, person1Id, network1.getId());
        }
    }

    /**
     * ALF-18968
     * 
     * @see QNameFilterImpl#listOfHardCodedExcludedTypes()
     */
    @Test
    public void testTypeFiltering() throws Exception
    {
        // Force an exclusion in order to test the exclusion inheritance
        cmisTypeExclusions.setExcluded(ActionModel.TYPE_ACTION_BASE, true);
        // Quick check
        assertTrue(cmisTypeExclusions.isExcluded(ActionModel.TYPE_ACTION_BASE));

        // Test that a type defined with this excluded parent type does not break the CMIS dictionary
        DictionaryBootstrap bootstrap = new DictionaryBootstrap();
        List<String> bootstrapModels = new ArrayList<String>();
        bootstrapModels.add("publicapi/test-model.xml");
        bootstrap.setModels(bootstrapModels);
        bootstrap.setDictionaryDAO(dictionaryDAO);
        bootstrap.setTenantService(tenantService);
        bootstrap.bootstrap();
        cmisDictionary.afterDictionaryInit();

        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
		PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        // test that this type is excluded; the 'action' model (act prefix) is in the list of hardcoded exclusions
        QName type = QName.createQName("{http://www.alfresco.org/test/testCMIS}type1");
        assertTrue(cmisTypeExclusions.isExcluded(type));

        // and that we can't get to it through CMIS
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
        try
        {
            cmisSession.getTypeDefinition("D:testCMIS:type1");
            fail("Type should not be available");
        }
        catch(CmisObjectNotFoundException e)
        {
            // ok
        }
    }

    /**
     * MNT-12680 
     * The creation date of version should be the same as creation date of the original node
     * @throws Exception
     */
    @Test
    public void testCreationDate() throws Exception
    {
        // create a site
        final TestNetwork network1 = getTestFixture().getRandomNetwork();
        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, "password", null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {

            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PUBLIC);
                final TestSite site = network1.createSite(siteInfo);
                final NodeRef resNode = repoService.createDocument(site.getContainerNodeRef("documentLibrary"), "testdoc.txt", "Test Doc1 Title", "Test Doc1 Description",
                        "Test Content");
                return resNode;
            }
        }, person1Id, network1.getId());

        // create a document
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
        AlfrescoFolder docLibrary = (AlfrescoFolder) cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        Map<String, String> properties = new HashMap<String, String>();
        {
            properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            properties.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
        }
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("some content");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        Document autoVersionedDoc = docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
        String objectId = autoVersionedDoc.getId();
        String bareObjectId = getBareObjectId(objectId);
        // create versions
        for (int i = 0; i < 3; i++)
        {
            Document doc1 = (Document) cmisSession.getObject(bareObjectId);

            ObjectId pwcId = doc1.checkOut();
            Document pwc = (Document) cmisSession.getObject(pwcId.getId());

            ContentStreamImpl contentStream = new ContentStreamImpl();
            {
                ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                writer.putContent(GUID.generate());
                ContentReader reader = writer.getReader();
                contentStream.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                contentStream.setStream(reader.getContentInputStream());
            }
            pwc.checkIn(true, Collections.EMPTY_MAP, contentStream, "checkin " + i);
        }
        
        GregorianCalendar cDateFirst = cmisSession.getAllVersions(bareObjectId).get(0).getCreationDate();
        GregorianCalendar cDateSecond = cmisSession.getAllVersions(bareObjectId).get(2).getCreationDate();
        
        if (cDateFirst.before(cDateSecond) || cDateFirst.after(cDateSecond))
        {
            fail("The creation date of version should be the same as creation date of the original node");
        }
    }

    /**
	 * Test that updating properties does not automatically create a new version.
	 * Test that updating content creates a new version automatically.
     * 
     */
    @Test
    public void testVersioning() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
		PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
				NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
        }, person1Id, network1.getId());

        // Create a document...
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
        AlfrescoFolder docLibrary = (AlfrescoFolder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        Map<String, String> properties = new HashMap<String, String>();
        {
        	properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            properties.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
        }
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }
        AlfrescoDocument doc = (AlfrescoDocument)docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
        String versionLabel = doc.getVersionLabel();

		// ...and check that updating its properties does not create a new version 
        properties = new HashMap<String, String>();
        {
            properties.put(PropertyIds.DESCRIPTION, GUID.generate());
        }
        AlfrescoDocument doc1 = (AlfrescoDocument)doc.updateProperties(properties);
        doc1 = (AlfrescoDocument)doc1.getObjectOfLatestVersion(false);
        String versionLabel1 = doc1.getVersionLabel();

        assertTrue(Float.parseFloat(versionLabel) < Float.parseFloat(versionLabel1));

		// ...and check that updating its content creates a new version
        fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        doc1.setContentStream(fileContent, true);
        AlfrescoDocument doc2 = (AlfrescoDocument)doc1.getObjectOfLatestVersion(false);
        @SuppressWarnings("unused")
        String versionLabel2 = doc2.getVersionLabel();

        assertTrue("Set content stream should create a new version automatically", Float.parseFloat(versionLabel1) < Float.parseFloat(versionLabel2));
	}
	
	/**
	 * Test that updating properties does automatically create a new version if
	 * <b>autoVersion</b>, <b>initialVersion</b> and <b>autoVersionOnUpdateProps</b> are TRUE
	 */
	@Test
	public void testVersioningUsingUpdateProperties() throws Exception
	{
		final TestNetwork network1 = getTestFixture().getRandomNetwork();

		String username = "user" + System.currentTimeMillis();
		PersonInfo personInfo = new PersonInfo(username, username, username, "password", null, null, null, null, null, null, null);
		TestPerson person1 = network1.createUser(personInfo);
		String person1Id = person1.getId();

		final String siteName = "site" + System.currentTimeMillis();

		TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
		{
			@Override
			public NodeRef doWork() throws Exception
			{
				SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
				TestSite site = repoService.createSite(null, siteInfo);

				String name = GUID.generate();
				NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef("documentLibrary"), name);
				return folderNodeRef;
			}
		}, person1Id, network1.getId());

		// Create a document...
		publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, "1.0", AlfrescoObjectFactoryImpl.class.getName());
		AlfrescoFolder docLibrary = (AlfrescoFolder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        Map<String, String> properties = new HashMap<String, String>();
        {
        	properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        	properties.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
        }
		ContentStreamImpl fileContent = new ContentStreamImpl();
		{
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
		}
		AlfrescoDocument doc = (AlfrescoDocument)docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
		String versionLabel = doc.getVersionLabel();
		
		String nodeRefStr = doc.getPropertyValue(NodeRefProperty.NodeRefPropertyId).toString();
		final NodeRef nodeRef = new NodeRef(nodeRefStr);
		
		TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
		{
			@Override
			public NodeRef doWork() throws Exception
			{
				// ensure autoversioning is enabled
				assertTrue(nodeService.hasAspect(nodeRef, ContentModel.ASPECT_VERSIONABLE));
				
				Map<QName, Serializable> versionProperties = new HashMap<QName, Serializable>();
				versionProperties.put(ContentModel.PROP_AUTO_VERSION, true);
				versionProperties.put(ContentModel.PROP_INITIAL_VERSION, true);
				versionProperties.put(ContentModel.PROP_AUTO_VERSION_PROPS, true);
				
				nodeService.addProperties(nodeRef, versionProperties);
				
				return null;
			}
		}, person1Id, network1.getId());

		// ...and check that updating its properties creates a new minor version...
		properties = new HashMap<String, String>();
        {
        	properties.put(PropertyIds.DESCRIPTION, GUID.generate());
        }
		AlfrescoDocument doc1 = (AlfrescoDocument)doc.getObjectOfLatestVersion(false).updateProperties(properties);
		doc1 = (AlfrescoDocument)doc.getObjectOfLatestVersion(false);
		String versionLabel1 = doc1.getVersionLabel();
		
        assertTrue(Double.parseDouble(versionLabel) < Double.parseDouble(versionLabel1));

		// ...and check that updating its content creates a new version
		fileContent = new ContentStreamImpl();
		{
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
		}

		doc1.setContentStream(fileContent, true);
		
		AlfrescoDocument doc2 = (AlfrescoDocument)doc1.getObjectOfLatestVersion(false);
		String versionLabel2 = doc2.getVersionLabel();
		
		assertTrue("Set content stream should create a new version automatically", Double.parseDouble(versionLabel1) < Double.parseDouble(versionLabel2));
		assertTrue("It should be latest version : " + versionLabel2, doc2.isLatestVersion());
		
		doc2.deleteContentStream();
		AlfrescoDocument doc3 = (AlfrescoDocument)doc2.getObjectOfLatestVersion(false);
		String versionLabel3 = doc3.getVersionLabel();
		
		assertTrue("Delete content stream should create a new version automatically", Double.parseDouble(versionLabel1) < Double.parseDouble(versionLabel3));
		assertTrue("It should be latest version : " + versionLabel3, doc3.isLatestVersion());
	}
    
	
	/**
    * 1) Creating a file with incorrect char in description property
    * 2) Get xml with incorrect char in description
    * 3) Check xml
    * @throws Exception
    */
   @Test
   public void testGetXmlWithIncorrectDesctiption() throws Exception
   {
       final TestNetwork network1 = getTestFixture().getRandomNetwork();

       String username = "user" + System.currentTimeMillis();
       PersonInfo personInfo = new PersonInfo(username, username, username, "password", null, null, null, null, null, null, null);
       TestPerson person1 = network1.createUser(personInfo);
       String person1Id = person1.getId();

       final String siteName = "site" + System.currentTimeMillis();
       
       NodeRef fileNode = TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
               {
                   @Override
                   public NodeRef doWork() throws Exception
                   {
                       SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PUBLIC);
                       final TestSite site = network1.createSite(siteInfo);
                       
                       NodeRef resNode = repoService.createDocument(site.getContainerNodeRef("documentLibrary"), "testdoc.txt", "Test Doc1 Title", "d\u0010", "Test Content");
                       return resNode;
                   }
               }, person1Id, network1.getId());
       
       String cmisId = fileNode.getId();
       HashMap<String, String> reqParams = new HashMap<String, String>();
       reqParams.put("id", cmisId+";1.0");
       reqParams.put("filter", "*");
       reqParams.put("includeAllowableActions", "true");
       reqParams.put("includeACL", "true");
       reqParams.put("includePolicyIds", "true");
       reqParams.put("includeRelationships", "both");
       reqParams.put("renditionFilter", "*");
       
       publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
       HttpResponse resp = publicApiClient.get(Binding.atom, CMIS_VERSION_11, "id", reqParams);
       String xml = resp.getResponse();
       // Response hasn't full info at error - writer just stops at incorrect character.
       // Therefore we can check end tag of root element
       assertTrue("No end tag was found", xml.endsWith("</atom:entry>"));
   }

    /*
     * Test that creating a document with a number of initial aspects does not create lots of initial versions
     */
    @Test
    public void testALF19320() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
		PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
				NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
        }, person1Id, network1.getId());

        // Create a document...
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
		CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
        AlfrescoFolder docLibrary = (AlfrescoFolder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        Map<String, String> properties = new HashMap<String, String>();
        {
            // create a document with 2 aspects
            properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled,P:cm:author");
            properties.put(PropertyIds.NAME, "mydoc-" + GUID.generate() + ".txt");
        }
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }
        
        AlfrescoDocument doc = (AlfrescoDocument)docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
        String versionLabel = doc.getVersionLabel();
		assertEquals(CMIS_VERSION_10, versionLabel);

        AlfrescoDocument doc1 = (AlfrescoDocument)doc.getObjectOfLatestVersion(false);
        String versionLabel1 = doc1.getVersionLabel();
		assertEquals(CMIS_VERSION_10, versionLabel1);
    }
    
    /* MNT-10175 test */
    @Test
    public void testAppendContentVersioning() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
                NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
        }, person1Id, network1.getId());

        // Create a document
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        // Use CMIS 1.1 to test content appending
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Folder docLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        String name = GUID.generate() + ".txt";
        Map<String, String> properties = new HashMap<String, String>();
        {
            properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            properties.put(PropertyIds.NAME, name);
        }
        // Create content to append
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        Document doc = docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
        String versionLabel1 = doc.getObjectOfLatestVersion(false).getVersionLabel();

        // append a few times
        for(int i = 0; i < 5; i++)
        {
            doc.appendContentStream(fileContent, false);
        }
        
        String versionLabel2 = doc.getObjectOfLatestVersion(false).getVersionLabel();
        
        // Version label should not be incremented by appending with isLustChunk = false
        assertEquals(versionLabel1, versionLabel2);

        doc.appendContentStream(fileContent, true);    
        
        String versionLabel3 = doc.getObjectOfLatestVersion(false).getVersionLabel();
        Integer majorVer1 = Integer.valueOf(versionLabel2.substring(0, 1));
        Integer majorVer2 = Integer.valueOf(versionLabel3.substring(0, 1));
        
        Integer minorVer1 = Integer.valueOf(versionLabel2.substring(2, 3));
        Integer minorVer2 = Integer.valueOf(versionLabel3.substring(2, 3));
        
        // Only one MINOR version should be created
        assertEquals(majorVer1, majorVer2);
        assertEquals(Integer.valueOf(minorVer1 + 1), minorVer2);
    }
    
    @Test
    public void testAppendContentStream() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
                NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
        }, person1Id, network1.getId());

        // Create a document...
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Folder docLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        String name = "mydoc-" + GUID.generate() + ".txt";
        Map<String, String> properties = new HashMap<String, String>();
        {
            // create a document with 2 aspects
            properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            properties.put(PropertyIds.NAME, name);
        }
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        Document doc = docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);

        // append a few times
        for(int i = 0; i < 5; i++)
        {
            fileContent = new ContentStreamImpl();
            {
                ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
                writer.putContent("Ipsum and so on");
                ContentReader reader = writer.getReader();
                fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
                fileContent.setStream(reader.getContentInputStream());
            }
            doc.appendContentStream(fileContent, false);
        }
        fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }
        doc.appendContentStream(fileContent, true);

        // check the appends
        String path = "/Sites/" + siteName + "/documentLibrary/" + name;
        Document doc1 = (Document)cmisSession.getObjectByPath(path);
        ContentStream contentStream = doc1.getContentStream();
        InputStream in = contentStream.getStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, "UTF-8");
        String content = writer.toString();
        assertEquals("Ipsum and so onIpsum and so onIpsum and so onIpsum and so onIpsum and so onIpsum and so onIpsum and so on", content);
    }

    @Test
    public void testSecondaryTypes() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
	    PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
                {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
	            NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
                }, person1Id, network1.getId());

        // Create a document...
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
	    CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Folder docLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        String name = "mydoc-" + GUID.generate() + ".txt";
        final List<String> secondaryTypes = new ArrayList<String>();
        secondaryTypes.add("P:cm:summarizable");
        Map<String, Object> properties = new HashMap<String, Object>();
        {
            // create a document with 2 aspects
	        properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
            properties.put("cm:summary", "My summary");
            properties.put(PropertyIds.NAME, name);
        }
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        Document doc = docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);

        {
            // check that the secondary types and properties are present
            {
                checkSecondaryTypes(doc, Collections.singleton("P:cm:summarizable"), null);
                String summary = (String)doc.getProperty("cm:summary").getFirstValue();
                assertEquals("My summary", summary);
            }
    
            {
                doc = (Document)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary/" + name);
                checkSecondaryTypes(doc, Collections.singleton("P:cm:summarizable"), null);
                String summary = (String)doc.getProperty("cm:summary").getFirstValue();
                assertEquals("My summary", summary);
            }
        }
        
        // update property and check
        {
            properties = new HashMap<String, Object>();
            {
                properties.put("cm:summary", "My updated summary");
            }
            doc.updateProperties(properties);

            {
                doc = (Document)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary/" + name);
                checkSecondaryTypes(doc, Collections.singleton("P:cm:summarizable"), null);
                String summary = (String)doc.getProperty("cm:summary").getFirstValue();
                assertEquals("My updated summary", summary);
            }
    
            {
                checkSecondaryTypes(doc, Collections.singleton("P:cm:summarizable"), null);
                String summary = (String)doc.getProperty("cm:summary").getFirstValue();
                assertEquals("My updated summary", summary);
            }
        }
    }

    /* MNT-10161 related test - mapping of cmis:description for CMIS 1.1 */
    @Test
    public void testMNT_10161() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();
        final String nodeDescription = "Test description";

        final String nodeName = GUID.generate();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), nodeName);
                /* create node with property description */
                return repoService.createDocument(folderNodeRef, nodeName, "title", nodeDescription, "content");
            }
        }, person1Id, network1.getId());

        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Document doc = 
                (Document)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary/" + nodeName + "/" + nodeName);

        /* ensure we got the node */
        assertNotNull(doc);

        /* get mapped cmis:description */
        Property<?> descrProperty = doc.getProperty(PropertyIds.DESCRIPTION);

        /* ensure that cmis:description is set properly */
        assertTrue(nodeDescription.equals(descrProperty.getValue()));
    }
    
    /* MNT-10687 related test - appendContent to PWC CMIS 1.1 */
    @Test
    public void testMNT_10687() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
                NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
        }, person1Id, network1.getId());

        // Create a document...
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Folder docLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        String name = "mydoc-" + GUID.generate() + ".txt";
        Map<String, Object> properties = new HashMap<String, Object>();
        {
            properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            properties.put(PropertyIds.NAME, name);
        }
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        /* Create document */
        Document doc = docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
        
        /* Checkout document */
        ObjectId pwcId = doc.checkOut();
        Document pwc = (Document)cmisSession.getObject(pwcId.getId());
        
        /* append content to PWC */
        fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent(" and so on");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }
        pwc.appendContentStream(fileContent, true);
        
        pwc.checkIn(false, null, null, "Check In");

        ContentStream contentStream = doc.getObjectOfLatestVersion(false).getContentStream();
        InputStream in = contentStream.getStream();
        StringWriter writer = new StringWriter();
        IOUtils.copy(in, writer, "UTF-8");
        String content = writer.toString();
        assertEquals("Ipsum and so on", content);
    }
    
    @Test
    public void testMNT_13057() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();
        
        String guid = GUID.generate();
        String name = guid + "_KRUIS_LOGO_100%_PMS.txt";
        String urlFileName = guid + "_KRUIS_LOGO_100%25_PMS.txt";
        
        final String siteName = "site" + System.currentTimeMillis();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
                NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
        }, person1Id, network1.getId());
        
        // Create a document...
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Folder docLibrary = (Folder)cmisSession.getObjectByPath("/Sites/" + siteName + "/documentLibrary");
        
        Map<String, Object> properties = new HashMap<String, Object>();
        {
            properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
            properties.put(PropertyIds.NAME, name);
        }
        
        ContentStreamImpl fileContent = new ContentStreamImpl();
        {
            ContentWriter writer = new FileContentWriter(TempFileProvider.createTempFile(GUID.generate(), ".txt"));
            writer.putContent("Ipsum");
            ContentReader reader = writer.getReader();
            fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
            fileContent.setStream(reader.getContentInputStream());
        }

        /* Create document */
        Document doc = docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);
        
        String id = doc.getId();
        assertNotNull(id);
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", URLEncoder.encode(id));
        
        urlFileName += "?id=" + URLEncoder.encode(id);

        HttpResponse response = publicApiClient.get(network1.getId() + "/public/cmis/versions/1.1/atom/content/" + urlFileName, null);
        assertEquals(200, response.getStatusCode());
    }
    
    @Test
    public void testMNT10430() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();

        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null,
        		null, null, null, null, null);
        TestPerson person1 = network1.createUser(personInfo);
        String person1Id = person1.getId();

        publicApiClient.setRequestContext(new RequestContext(network1.getId(), person1Id));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.browser, CMIS_VERSION_11,
        		"org.alfresco.cmis.client.impl.AlfrescoObjectFactoryImpl");

		ObjectType objectType = cmisSession.getTypeDefinition("D:testcmis:maDoc");

		// try and get the mandatory aspects
		List<String> mandatoryAspects = ((AlfrescoType)objectType).getMandatoryAspects();
		System.out.println("Mandatory Aspects");
		for(String mandatoryAspect : mandatoryAspects)
		{ 
			System.out.println(mandatoryAspect); 
		}
		assertTrue("The aspects should have P:cm:generalclassifiable", mandatoryAspects.contains("P:cm:generalclassifiable"));
    }

    @Test
    public void testMnt11631() throws Exception
    {
        final TestNetwork network = getTestFixture().getRandomNetwork();

        String username = String.format(TEST_USER_NAME_PATTERN, System.currentTimeMillis());
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person = network.createUser(personInfo);
        String personId = person.getId();

        final String siteName = String.format(TEST_SITE_NAME_PATTERN, System.currentTimeMillis());

        TenantUtil.runAsUserTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);

                String name = GUID.generate();
                NodeRef folderNodeRef = repoService.createFolder(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), name);
                return folderNodeRef;
            }
        }, personId, network.getId());

        publicApiClient.setRequestContext(new RequestContext(network.getId(), personId));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Folder docLibrary = (Folder) cmisSession.getObjectByPath(String.format(DOCUMENT_LIBRARY_PATH_PATTERN, siteName));
        String name = String.format(TEST_DOCUMENT_NAME_PATTERN, GUID.generate());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
        properties.put(PropertyIds.NAME, name);

        ContentStreamImpl fileContent = new ContentStreamImpl();
        ByteArrayInputStream stream = new ByteArrayInputStream(GUID.generate().getBytes());
        fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        fileContent.setStream(stream);

        Document doc = docLibrary.createDocument(properties, fileContent, VersioningState.MAJOR);

        ObjectId pwcId = doc.checkOut();
        Document pwc = (Document) cmisSession.getObject(pwcId.getId());

        assertIsPwcProperty(pwc, false);

        cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10);
        CmisObject pwc10 = cmisSession.getObject(pwc.getId());
        assertIsPwcProperty(pwc10, true);
    }

    private void assertIsPwcProperty(CmisObject pwc, boolean nullExpected)
    {
        boolean isPwcFound = false;
        Boolean isPwcValueTrue = null;
        for (Property<?> property : pwc.getProperties())
        {
            if ((null != property) && PropertyIds.IS_PRIVATE_WORKING_COPY.equals(property.getId()))
            {
                isPwcFound = true;
                isPwcValueTrue = property.getValue();
                break;
            }
        }

        if (nullExpected)
        {
            assertTrue(("'" + PropertyIds.IS_PRIVATE_WORKING_COPY + "' property is not null!"), !isPwcFound || (null == isPwcValueTrue));
            return;
        }

        assertTrue(("'" + PropertyIds.IS_PRIVATE_WORKING_COPY + "' property has not been found!"), isPwcFound);
        assertNotNull(("'" + PropertyIds.IS_PRIVATE_WORKING_COPY + "' property value must not be null!"), isPwcValueTrue);
        assertTrue(("'" + PropertyIds.IS_PRIVATE_WORKING_COPY + "' property value must be equal to 'true'!"), isPwcValueTrue);
    }
    
    @Test
    public void testCanConnectCMISUsingDefaultTenant() throws Exception
    {
        testCanConnectCMISUsingDefaultTenantImpl(Binding.atom, CMIS_VERSION_11);
        testCanConnectCMISUsingDefaultTenantImpl(Binding.atom, CMIS_VERSION_10);
        testCanConnectCMISUsingDefaultTenantImpl(Binding.browser, CMIS_VERSION_11);
    }
    
    private void testCanConnectCMISUsingDefaultTenantImpl(Binding binding, String cmisVersion)
    {
        String url = httpClient.getPublicApiCmisUrl(TenantUtil.DEFAULT_TENANT, binding, cmisVersion, null);
        
        Map<String, String> parameters = new HashMap<String, String>();
        
        // user credentials
        parameters.put(SessionParameter.USER, "admin");
        parameters.put(SessionParameter.PASSWORD, "admin");
        
        parameters.put(SessionParameter.ATOMPUB_URL, url);
        parameters.put(SessionParameter.BROWSER_URL, url);
        parameters.put(SessionParameter.BINDING_TYPE, binding.getOpenCmisBinding().value());
        
        SessionFactory factory = SessionFactoryImpl.newInstance();
        // perform request : http://host:port/alfresco/api/-default-/public/cmis/versions/${cmisVersion}/${binding}
        List<Repository> repositories = factory.getRepositories(parameters);
        
        assertTrue(repositories.size() > 0);
        
        parameters.put(SessionParameter.REPOSITORY_ID, TenantUtil.DEFAULT_TENANT);
        Session session = factory.createSession(parameters);
        // perform request : http://host:port/alfresco/api/-default-/public/cmis/versions/${cmisVersion}/${binding}/type?id=cmis:document
        ObjectType objectType = session.getTypeDefinition("cmis:document");
        
        assertNotNull(objectType);
    }

    @Test
    public void testACE3433() throws Exception
    {
        final TestNetwork network = getTestFixture().getRandomNetwork();

        NodeRef rootNodeRef = TenantUtil.runAsSystemTenant(new TenantRunAsWork<NodeRef>()
        {
            @Override
            public NodeRef doWork() throws Exception
            {
                NodeRef rootNodeRef = repoService.getNodeService().getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
                List<ChildAssociationRef> childAssocs = repoService.getNodeService().getChildAssocsByPropertyValue(rootNodeRef,
                        ContentModel.PROP_TITLE, "Company Home");
                assertEquals(1, childAssocs.size());
                NodeRef companyHomeNodeRef = childAssocs.get(0).getChildRef();
                return companyHomeNodeRef;
            }

        }, network.getId());
        assertNotNull(rootNodeRef);

        // atom
        {
            Binding binding = Binding.atom;
            String url = httpClient.getPublicApiCmisUrl(TenantUtil.DEFAULT_TENANT, binding, "1.1", null);
    
            Map<String, String> parameters = new HashMap<String, String>();
    
            // user credentials
            parameters.put(SessionParameter.USER, "admin@" + network.getId());
            parameters.put(SessionParameter.PASSWORD, "admin");
            parameters.put(SessionParameter.ATOMPUB_URL, url);
            parameters.put(SessionParameter.BINDING_TYPE, binding.getOpenCmisBinding().value());
    
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Repository repository = factory.getRepositories(parameters).get(0);
            String rootFolderId = repository.getRootFolderId();
    
            assertEquals(rootNodeRef.getId(), rootFolderId);
        }

        {
            Binding binding = Binding.browser;
            String url = httpClient.getPublicApiCmisUrl(TenantUtil.DEFAULT_TENANT, binding, "1.1", null);
    
            Map<String, String> parameters = new HashMap<String, String>();
    
            // user credentials
            parameters.put(SessionParameter.USER, "admin@" + network.getId());
            parameters.put(SessionParameter.PASSWORD, "admin");
            parameters.put(SessionParameter.BROWSER_URL, url);
            parameters.put(SessionParameter.BINDING_TYPE, binding.getOpenCmisBinding().value());
    
            SessionFactory factory = SessionFactoryImpl.newInstance();
            Repository repository = factory.getRepositories(parameters).get(0);
            String rootFolderId = repository.getRootFolderId();
    
            assertEquals(rootNodeRef.getId(), rootFolderId);
        }
    }

    @Test
    public void testMNT12956QueryingCMIS11UsesDictionary11() throws Exception
    {
        final TestNetwork network1 = getTestFixture().getRandomNetwork();
        
        String username = "user" + System.currentTimeMillis();
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person = network1.createUser(personInfo);
        String personId = person.getId();

        final List<NodeRef> documents = new ArrayList<NodeRef>();
        final String filename = GUID.generate();

        TenantUtil.runAsUserTenant(new TenantRunAsWork<Void>()
        {
            @Override
            public Void doWork() throws Exception
            {
                String siteName = "site" + System.currentTimeMillis();
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                TestSite site = repoService.createSite(null, siteInfo);
                NodeRef docNodeRef = repoService.createDocument(site.getContainerNodeRef(DOCUMENT_LIBRARY_CONTAINER_NAME), filename, "test content");
                documents.add(docNodeRef);

                return null;
            }
        }, personId, network1.getId());

        NodeRef docNodeRef = documents.get(0);
        publicApiClient.setRequestContext(new RequestContext(network1.getId(), personId));
        CmisSession atomCmisSession10 = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_10, AlfrescoObjectFactoryImpl.class.getName());
        CmisSession atomCmisSession11 = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);

        // query
        {
            // searching by NodeRef, expect result objectIds to be objectId
            Set<String> expectedObjectIds = new HashSet<String>();
            expectedObjectIds.add(docNodeRef.getId());
            int numMatchingDocs = 0;

            // NodeRef input
            List<CMISNode> results = atomCmisSession11.query("SELECT cmis:objectId,cmis:name,cmis:secondaryObjectTypeIds FROM cmis:document WHERE cmis:name LIKE '" + filename + "'", false, 0, Integer.MAX_VALUE);
            assertEquals(expectedObjectIds.size(), results.size());
            for(CMISNode node : results)
            {
                String objectId = stripCMISSuffix((String)node.getProperties().get(PropertyIds.OBJECT_ID));
                if(expectedObjectIds.contains(objectId))
                {
                    numMatchingDocs++;
                }
            }
            assertEquals(expectedObjectIds.size(), numMatchingDocs);

            try
            {
                results = atomCmisSession10.query("SELECT cmis:objectId,cmis:name,cmis:secondaryObjectTypeIds FROM cmis:document WHERE cmis:name LIKE '" + filename + "'", false, 0, Integer.MAX_VALUE);
                fail("OpenCMIS 1.0 knows nothing about cmis:secondaryObjectTypeIds");
            }
            catch (CmisInvalidArgumentException expectedException)
            {
                // ok
            }
        }
    }
    
    @Test
    public void testDoNotShowCheckedOutedNodeInFolder() throws Exception
    {
        final TestNetwork network = getTestFixture().getRandomNetwork();

        String username = String.format(TEST_USER_NAME_PATTERN, System.currentTimeMillis());
        PersonInfo personInfo = new PersonInfo(username, username, username, TEST_PASSWORD, null, null, null, null, null, null, null);
        TestPerson person = network.createUser(personInfo);
        String personId = person.getId();

        final String siteName = String.format(TEST_SITE_NAME_PATTERN, System.currentTimeMillis());

        TenantUtil.runAsUserTenant(new TenantRunAsWork<TestSite>()
        {
            @Override
            public TestSite doWork() throws Exception
            {
                SiteInformation siteInfo = new SiteInformation(siteName, siteName, siteName, SiteVisibility.PRIVATE);
                return repoService.createSite(null, siteInfo);
            }
        }, personId, network.getId());

        publicApiClient.setRequestContext(new RequestContext(network.getId(), personId));
        CmisSession cmisSession = publicApiClient.createPublicApiCMISSession(Binding.atom, CMIS_VERSION_11);
        Folder docLibrary = (Folder) cmisSession.getObjectByPath(String.format(DOCUMENT_LIBRARY_PATH_PATTERN, siteName));
        
        assertEquals(0, docLibrary.getChildren().getTotalNumItems());
        
        String name = String.format(TEST_DOCUMENT_NAME_PATTERN, GUID.generate());

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, TYPE_CMIS_DOCUMENT);
        properties.put(PropertyIds.NAME, name);

        ContentStreamImpl fileContent = new ContentStreamImpl();
        ByteArrayInputStream stream = new ByteArrayInputStream(GUID.generate().getBytes());
        fileContent.setMimeType(MimetypeMap.MIMETYPE_TEXT_PLAIN);
        fileContent.setStream(stream);

        docLibrary.createDocument(properties, fileContent, VersioningState.CHECKEDOUT);
        // there should be only one document
        assertEquals(1, docLibrary.getChildren().getTotalNumItems());
        
        Document obj = (Document)docLibrary.getChildren().iterator().next();
        // and it should be a PWC
        assertTrue(obj.isPrivateWorkingCopy());
    }
}
