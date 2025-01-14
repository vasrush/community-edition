/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.site.document;

import java.io.File;
import java.util.List;

import org.alfresco.po.share.enums.UserRole;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage.ButtonType;
import org.alfresco.po.share.site.document.ManagePermissionsPage.UserSearchPage;
import org.alfresco.po.share.site.document.UserProfile;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserMembers;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.SiteUtil;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author nshah
 */
@Listeners(FailedTestListener.class)
public class ManagePermissionTest extends AbstractAspectTests
{
    private static Log logger = LogFactory.getLog(ManagePermissionTest.class);

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14128() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Create OP user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // Any user is logged into the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Any site is created
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Any Folder is created
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Any document is uploaded
        ShareUserSitePage.uploadFile(drone, file);

        ShareUser.logout(drone);

    }

    /**
     * Covers 10400 10266 10401
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14128()
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        // login user.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Document Library page is opened.
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Manage permission is opened for the Folder.
        ManagePermissionsPage manaPerPage = documentLibraryPage.getFileDirectoryInfo(folderName).selectManagePermission().render();

        // Click cancel button on the manage permission page.
        manaPerPage.selectCancel().render();

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Manage permission is opened for the document.
        manaPerPage = ShareUser.returnManagePermissionPage(drone, fileName).render();

        // click cancel button on the manage permission page.
        manaPerPage.selectCancel().render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14129() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Create OP user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // Any user is logged into the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Any site is created
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Any Folder is created
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Any document is uploaded
        ShareUserSitePage.uploadFile(drone, file);

        ShareUser.logout(drone);
    }

    @Test(groups = {"AlfrescoOne", "IntermittentBugs"})
    public void AONE_14129()
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        // login user.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Document Library page is opened.
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate to Details page of the Folder.
        ShareUser.openFolderDetailPage(drone, folderName).render();

        // Manage permission is opened for the folder.
        ManagePermissionsPage manaPerPage = ShareUser.returnManagePermissionPage(drone, folderName).render();

        // Click Cancel button on the manage permission page.
        manaPerPage.selectCancel().render();

        ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Navigate to Details page of the document.
        ShareUser.openDocumentDetailPage(drone, fileName);

        // Manage permission is opened for the document.
        manaPerPage = ShareUser.returnManagePermissionPage(drone, fileName).render();

        // Click Cancel button on the manage permission page.
        manaPerPage.selectCancel().render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14130() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");

        // Create user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // Create user2
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // Create user3
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);

        ShareUser.logout(drone);
    }

    /**
     * Covers 10402 10403 10268
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14130() throws Exception
    {

        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Any user is logged into the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Any site is created
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 with site as collaborator.
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // Any Folder is created under the site.
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Any document is uploaded under the site.
        ShareUserSitePage.uploadFile(drone, file);

        // Log out
        ShareUser.logout(drone);

        // login as user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Go to Site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Go to the manage permission of Folder
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user3 with consumer role.
        // click on Save button
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECOLLABORATOR, true);

        ShareUser.returnManagePermissionPage(drone, fileName);

        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECOLLABORATOR, true);

        // log out
        ShareUser.logout(drone);

        // login as user2
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Go to site and than document
        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // verify the user permission for document.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // verify the user permission for Folder.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14131() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");

        // Create user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // Create user2
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // Create user3
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);

        ShareUser.logout(drone);
    }

    /**
     * Covers 10402 10403 10268
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14131() throws Exception
    {

        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");

        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Any user is logged into the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Any site is created
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 with site as collaborator.
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // Any Folder is created under the site.
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Any document is uploaded under the site.
        ShareUserSitePage.uploadFile(drone, file);

        // Log out
        ShareUser.logout(drone);

        // login as user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Go to Site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Go to the manage permission of Folder
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user3 with consumer role.
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(user3);
        managePermissionPage = managePermissionPage.selectAddUser().searchAndSelectUser(userProfile).render();

        // click on Cancel button
        managePermissionPage.selectCancel();

        // Go to the manage permission of Folder
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Verify the user3 is not added as Consumer role.
        Assert.assertFalse(managePermissionPage.isUserExistForPermission(user3));
        managePermissionPage.selectCancel().render();

        // Go to the manage permission of document
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user3 with consumer role.
        managePermissionPage = managePermissionPage.selectAddUser().searchAndSelectUser(userProfile).render();

        // click on Cancel button
        managePermissionPage.selectCancel();

        // Go to the manage permission of Document
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Verify the user3 is not added as Consumer role.
        Assert.assertFalse(managePermissionPage.isUserExistForPermission(user3));
        managePermissionPage.selectCancel().render();

        // log out
        ShareUser.logout(drone);

        // login as user2
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Go to site and than folder
        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // verify the user permission for Folder.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // verify the user permission for document.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14132() throws Exception
    {
        // Create user1, user, user2, user4, user5, user6 on OP and Cloud
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");
        String user6 = getUserNameFreeDomain(testName + "-6");

        // Create user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user6);

        ShareUser.logout(drone);
    }

    /**
     * Covers 10405
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14132() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");
        String user6 = getUserNameFreeDomain(testName + "-6");
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Any user is logged into the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Any site is created
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Roles to site with users user2-manager, user3-collaborator,
        // user4-Contributor, user5-Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Any folder is created under site.
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Any document is uploaded under site.
        ShareUserSitePage.uploadFile(drone, file);

        ShareUser.logout(drone);

        // login as user1.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Go to folder's manage permission
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);
        managePermissionPage.toggleInheritPermission(true, ButtonType.Yes).render();

        // Click on save.
        managePermissionPage.selectSave();

        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);
        managePermissionPage.toggleInheritPermission(true, ButtonType.Yes).render();

        // Click on save.
        managePermissionPage.selectSave();

        // Logout as user1.
        ShareUser.logout(drone);

        // login as user2 - Manager Role
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // verify that folder has "Delete" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // This is needed since in prior step when user checks for delete
        // present More link is kept open till no click happens on page
        // and below step is to check file's more which is covered by previous
        // more.
        drone.refresh();

        // verify that file has "Delete" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // logout of user2.
        ShareUser.logout(drone);

        // login as user3 - Collaborator Role
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // verify that folder has "Edit Offline" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // verify that file has "Edit Offline" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // verify that folder has no "Delete" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // verify that file has no "Delete" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // logout of user3.
        ShareUser.logout(drone);

        // login as user4 - Contributor Role
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // verify that folder has "Add Comment" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // verify that file has "Add comment" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // verify that folder has no "Edit Offline" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // verify that file has no "Edit Offline" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // logout of user4.
        ShareUser.logout(drone);

        // login as user5 - Consumer Role
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // verify that folder has no "Comment" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // verify that file has no "comment" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // logout of user5.
        ShareUser.logout(drone);

        // login as user6 - Non Member
        ShareUser.login(drone, user6, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // verify that folder has no "Comment" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // verify that file has no "Commentk" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // logout of user6.
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14133() throws Exception
    {
        // Create user1, user, user2, user4, user5, user6 on OP and Cloud
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");
        String user6 = getUserNameFreeDomain(testName + "-6");

        // Create user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user6);

        ShareUser.logout(drone);
    }

    /**
     * Covers 10406
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14133() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");
        String user6 = getUserNameFreeDomain(testName + "-6");
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Any user is logged into the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Any site is created
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Roles to site with users user2-manager, user3-collaborator,
        // user4-Contributor, user5-Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Any folder is created under site.
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Any document is uploaded under site.
        ShareUserSitePage.uploadFile(drone, file);

        ShareUser.logout(drone);

        // login as user1.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Go to folder's manage permission
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Turn off permission inheritance.
        managePermissionPage = managePermissionPage.toggleInheritPermission(false, ButtonType.Yes);

        // Click on save.
        managePermissionPage.selectSave().render();

        // open Manage Permissions: {folder_name} page for the folder
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // verify the Locally Set Permissions section
        Assert.assertEquals(managePermissionPage.getExistingPermission("site_" + siteName.toLowerCase() + "_SiteManager"), UserRole.SITEMANAGER);

        managePermissionPage.selectCancel().render();

        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Turn off permission inheritance.
        managePermissionPage = managePermissionPage.toggleInheritPermission(false, ButtonType.Yes);

        // Click on save.
        managePermissionPage.selectSave();

        // open the Manage Permissions page for the file again
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName).render();

        // verify the Locally Set Permissions section
        Assert.assertEquals(managePermissionPage.getExistingPermission("site_" + siteName.toLowerCase() + "_SiteManager"), UserRole.SITEMANAGER);

        // Logout as user1.
        ShareUser.logout(drone);

        // login as user2 - Manager Role
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Assert.assertFalse(docLibPage.isFileVisible(folderName));

        // verify that folder has "Delete" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // This is needed since in prior step when user checks for delete
        // present More link is kept open till no click happens on page
        // and below step is to check file's more which is covered by previous
        // more.
        drone.refresh();

        // Assert.assertFalse(docLibPage.isFileVisible(fileName));

        // verify that file has "Delete" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // logout of user2.
        ShareUser.logout(drone);

        // login as user3 - Collaborator Role
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertFalse(docLibPage.isFileVisible(folderName));

        Assert.assertFalse(docLibPage.isFileVisible(fileName));

        // logout of user3.
        ShareUser.logout(drone);

        // login as user4 - Contributor Role
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertFalse(docLibPage.isFileVisible(folderName));

        Assert.assertFalse(docLibPage.isFileVisible(fileName));

        // logout of user4.
        ShareUser.logout(drone);

        // login as user5 - Consumer Role
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertFalse(docLibPage.isFileVisible(folderName));

        Assert.assertFalse(docLibPage.isFileVisible(fileName));

        // logout of user5.
        ShareUser.logout(drone);

        // login as user6 - Non Member
        ShareUser.login(drone, user6, DEFAULT_PASSWORD);

        // navigate to site where folder is created.
        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        Assert.assertFalse(docLibPage.isFileVisible(folderName));

        Assert.assertFalse(docLibPage.isFileVisible(fileName));

        // logout of user6.
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14134() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Create user1
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // Any user is logged into the Share
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Any site is created
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Any folder is created under site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Any document is uploaded under site.
        ShareUserSitePage.uploadFile(drone, file);

        // logout user.
        ShareUser.logout(drone);
    }

    /**
     * Covers 10407
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14134()
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        // log in as an user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // go to to site.
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // navigate to document and open manage permission.
        ManagePermissionsPage managePermionsPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Verfiy Add user/group button present
        // Click on Add user/group button
        // verify SearcH pop-up comes up.
        // Verify Search Field and Search Button is present.
        ManagePermissionsPage.UserSearchPage userSearchPage = managePermionsPage.selectAddUser().render();
        Assert.assertTrue(userSearchPage instanceof ManagePermissionsPage.UserSearchPage);
        managePermionsPage.selectCancel().render();

        // navigate to folder and open manage permission.
        managePermionsPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Verify Add user/group button present
        // Click on Add user/group button
        // verify Search pop-up comes up.
        // Verify Search Field and Search Button is present.
        userSearchPage = managePermionsPage.selectAddUser().render();
        Assert.assertTrue(userSearchPage instanceof ManagePermissionsPage.UserSearchPage);

        managePermionsPage.selectCancel().render();

        // User logs out
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14135() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);

        // Create a user1, user2. user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // log in as an user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // create a site
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // add user2 with site as consumer.
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.CONSUMER);

        // User logs out
        ShareUser.logout(drone);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14135() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();

        File file = newFile(fileName, "New file");

        // log in as an user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // go to to site#
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder on site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // navigate to document and open manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName).render();

        // Click on Add user/group button
        // verify Search pop-up comes up.
        // Enter search String user2 in the search pop-up.
        // User2 is found in search.
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(user2);
        ManagePermissionsPage.UserSearchPage searchPage = managePermissionPage.selectAddUser().render();
        // e.g. userEnterprise42-5329@freetht1.test LName = user2+" "+"LName"
        searchPage.searchUserAndGroup(user2);

        Assert.assertTrue(searchPage.isUserOrGroupPresentInSearchList(user2 + " " + DEFAULT_LASTNAME));

        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed("(" + user2 + ")"));
        else
            Assert.assertFalse(searchPage.isEveryOneDisplayed((user2 + " " + DEFAULT_LASTNAME)));

        managePermissionPage.selectCancel().render();

        ShareUser.returnManagePermissionPage(drone, folderName).render();

        searchPage = managePermissionPage.selectAddUser().render();

        Assert.assertTrue(searchPage.isUserOrGroupPresentInSearchList(user2 + " " + DEFAULT_LASTNAME));

        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed(user2 + " " + DEFAULT_LASTNAME));
        else
            Assert.assertFalse(searchPage.isEveryOneDisplayed(user2 + " " + DEFAULT_LASTNAME));

        // Come out of ManagePermission page
        managePermissionPage.selectCancel().render();

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14136() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);

        // Create a user1, user2. user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // log in as an user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // create a site
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // add user2 with site as consumer.
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.CONSUMER);

        // User logs out
        ShareUser.logout(drone);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14136() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String warningMsgForUserSearch = "Enter at least 3 character(s)";

        File file = newFile(fileName, "New file");

        // log in as an user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // go to to site#
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder on site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // navigate to document and open manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        UserSearchPage userSearchPage = managePermissionPage.selectAddUser();
        // Enter another search string with "u"
        try
        {
            userSearchPage.render().searchUserAndGroup("u");

        }
        catch (UnsupportedOperationException usoe)
        {
            // Verify dialoug is there "Enter at least 3 character(s)"
            Assert.assertEquals(usoe.getMessage(), warningMsgForUserSearch);
        }

        // Enter another search string with "us"
        try
        {
            userSearchPage.searchUserAndGroup("us");

        }
        catch (UnsupportedOperationException usoe)
        {
            // Verify dialoug is there "Enter at least 3 character(s)"
            Assert.assertEquals(usoe.getMessage(), warningMsgForUserSearch);

        }

        // Enter another search string with "*"

        // Search result with "Everyone" appears

        // Come out of ManagePermissionpage
        managePermissionPage.selectCancel().render();

        // navigate to folder and open manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        userSearchPage = managePermissionPage.selectAddUser();
        // Enter another search string with "u"
        try
        {
            userSearchPage.render().searchUserAndGroup("u");

        }
        catch (UnsupportedOperationException usoe)
        {
            // Verify dialoug is there "Enter at least 3 character(s)"
            Assert.assertEquals(usoe.getMessage(), warningMsgForUserSearch);
        }

        // Enter another search string with "us"
        try
        {
            userSearchPage.searchUserAndGroup("us");

        }
        catch (UnsupportedOperationException usoe)
        {
            // Verify dialoug is there "Enter at least 3 character(s)"
            Assert.assertEquals(usoe.getMessage(), warningMsgForUserSearch);
        }

        // Enter another search string with "*"
        managePermissionPage.selectCancel().render();

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14137() throws Exception
    {
        String testName = getTestName();

        String user = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);

        // Create a user1, user2. user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // log in as an user
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // create a site
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // User logs out
        ShareUser.logout(drone);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14137() throws Exception
    {
        String testName = getTestName();

        String user = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String wildCardStringUser = "<>?:\"|}{+_)(*&^%$#@!~;";

        File file = newFile(fileName, "New file");

        // log in as an user1
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // go to to site#
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder on site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // navigate to document and open manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName).render();

        // Click on Add user/group button
        // verify Search pop-up comes up.
        // Enter search String user2 in the search pop-up.
        // User2 is found in search.

        ManagePermissionsPage.UserSearchPage searchPage = managePermissionPage.selectAddUser().render();

        // Enter another search string with "*"
        // Search result with "Everyone" appears
        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed(wildCardStringUser));
        else
        {
            Assert.assertNull(searchPage.searchUserAndGroup(wildCardStringUser));
        }

        managePermissionPage.selectCancel();

        // ****************************************************** for
        // Folder*********************************************************//

        // navigate to folder and open manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName).render();

        searchPage = managePermissionPage.selectAddUser().render();

        // Search result with "Everyone" appears
        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed(wildCardStringUser));
        else
            Assert.assertNull(searchPage.searchUserAndGroup(wildCardStringUser));
        managePermissionPage.selectCancel();

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14138() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);

        // Create a user1, user2. user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // log in as an user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // create a site
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // User logs out
        ShareUser.logout(drone);
    }

    /**
     * Covers 10273 10274 10275 10276 10408 10409 10410 10411
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14138() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();

        String longNameUSer = getRandomString(257);

        File file = newFile(fileName, "New file");

        // log in as an user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // go to to site#
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder on site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // navigate to document and open manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        ManagePermissionsPage.UserSearchPage searchPage = managePermissionPage.selectAddUser().render();

        // Search result with "Everyone" appears
        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed(longNameUSer));
        else
            Assert.assertNull(searchPage.searchUserAndGroup(longNameUSer));

        managePermissionPage.selectCancel().render();

        // navigate to folder and open manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        searchPage = managePermissionPage.selectAddUser().render();

        // Search result with "Everyone" appears
        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed(longNameUSer));
        else
            Assert.assertNull(searchPage.searchUserAndGroup(longNameUSer));

        managePermissionPage.selectCancel().render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14139() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        String user3 = getUserNameFreeDomain(testName + "-3");
        String siteName = getSiteName(testName);

        // Create a user1, user2. user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);

        // log in as an user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // create a site
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // User logs out
        ShareUser.logout(drone);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14139() throws Exception
    {
        String testName = getTestName();

        String user1 = getUserNameFreeDomain(testName + "-1");

        String user3 = getUserNameFreeDomain(testName + "-3");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();

        File file = newFile(fileName, "New file");

        // log in as an user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // go to to site#
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder on site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // navigate to document and open manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Click on Add user/group button
        // verify Search pop-up comes up.

        ManagePermissionsPage.UserSearchPage searchPage = managePermissionPage.selectAddUser().render();
        // e.g. userEnterprise42-5329@freetht1.test LName = user3+" "+"LName"
        Assert.assertTrue(searchPage.isUserOrGroupPresentInSearchList(user3 + " " + DEFAULT_LASTNAME));

        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed(user3 + " " + DEFAULT_LASTNAME));
        else
            Assert.assertFalse(searchPage.isEveryOneDisplayed(user3 + " " + DEFAULT_LASTNAME));

        managePermissionPage.selectCancel().render();

        // ****************************************************** for
        // Folder*********************************************************//

        // navigate to folder and open manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Click on Add user/group button
        // verify Search pop-up comes up.

        searchPage = managePermissionPage.selectAddUser().render();
        Assert.assertTrue(searchPage.isUserOrGroupPresentInSearchList(user3 + " " + DEFAULT_LASTNAME));
        if (!isAlfrescoVersionCloud(drone))
            Assert.assertTrue(searchPage.isEveryOneDisplayed(user3 + " " + DEFAULT_LASTNAME));
        else
            Assert.assertFalse(searchPage.isEveryOneDisplayed(user3 + " " + DEFAULT_LASTNAME));

        managePermissionPage.selectCancel().render();

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14140() throws Exception
    {
        String testName = getTestName();

        String user = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);

        // Create a user1, user2. user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // log in as an user
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // create a site
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // User logs out
        ShareUser.logout(drone);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14140() throws Exception
    {
        String testName = getTestName();

        String user = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        String nonExistUser = "test123";
        File file = newFile(fileName, "New file");

        // log in as an user1
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // go to to site#
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder on site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // navigate to document and open manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName).render();

        ManagePermissionsPage.UserSearchPage searchPage = managePermissionPage.selectAddUser().render();

        if (!isAlfrescoVersionCloud(drone))
        {
            Assert.assertFalse(searchPage.isUserOrGroupPresentInSearchList(nonExistUser));
            Assert.assertTrue(searchPage.isEveryOneDisplayed(nonExistUser + " " + DEFAULT_LASTNAME));
        }
        else
            Assert.assertNull(searchPage.searchUserAndGroup(nonExistUser + " " + DEFAULT_LASTNAME));

        // Come out of ManagePermissionpage
        managePermissionPage.selectCancel().render();

        // ****************************************************** for
        // Folder*********************************************************//

        // navigate to folder and open manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName).render();

        searchPage = managePermissionPage.selectAddUser().render();

        if (!isAlfrescoVersionCloud(drone))
        {
            Assert.assertFalse(searchPage.isUserOrGroupPresentInSearchList(nonExistUser));
            Assert.assertTrue(searchPage.isEveryOneDisplayed(nonExistUser + " " + DEFAULT_LASTNAME));
        }
        else
            Assert.assertNull(searchPage.searchUserAndGroup(nonExistUser + " " + DEFAULT_LASTNAME));

        // Come out of ManagePermissionpage
        managePermissionPage.selectCancel().render();

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14141() throws Exception
    {

        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Create a user1, user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // log in as an user
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // create a site
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // add user2 with site as collaborator.
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // upload a document on site
        ShareUser.openSitesDocumentLibrary(drone, siteName);
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder on site.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUser.logout(drone);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14141()
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        // log in as an user1
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // go to to site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate to document and open manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Click on Add user/group button
        // search user2
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(user2);
        // add user2 in the permissions.
        managePermissionPage = managePermissionPage.selectAddUser().searchAndSelectUser(userProfile).render();

        // Verify In "Locally set permission" user2 is added with site's role
        // that "COllaborator"
        Assert.assertTrue(managePermissionPage.isUserExistForPermission(user2));
        Assert.assertEquals(managePermissionPage.getExistingPermission(user2), UserRole.SITECOLLABORATOR, "Verify user role is changed");

        // Click Save.
        managePermissionPage.selectSave().render();

        // Navigate to folder and open manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Click on Add user/group button
        // search user2
        userProfile.setUsername(user2);

        // add user2 in the permissions.
        managePermissionPage = managePermissionPage.selectAddUser().searchAndSelectUser(userProfile).render();

        // Verify In "Locally set permission" user2 is added with site's role
        // that "COllaborator"
        Assert.assertTrue(managePermissionPage.isUserExistForPermission(user2));
        Assert.assertEquals(managePermissionPage.getExistingPermission(user2), UserRole.SITECOLLABORATOR, "Verify user role is changed");

        // Click Save.
        managePermissionPage.selectSave().render();

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14142() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Create a user1.
        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.COLLABORATOR);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // User1 logs outs.
        ShareUser.logout(drone);

    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14142()
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate the document's manage permission.
        ManagePermissionsPage managePermisisonPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Add User2 in Locally set permission and Verify that user2 has Site
        // Collaborator role by default selected.
        UserProfile userProfile = new UserProfile();
        userProfile.setUsername(user2);
        managePermisisonPage.selectAddUser().searchAndSelectUser(userProfile);
        Assert.assertEquals(UserRole.SITECOLLABORATOR, managePermisisonPage.getExistingPermission(user2));

        List<String> roles = managePermisisonPage.getListOfUserRoles(user2);
        // [Editor, Consumer, Collaborator, Coordinator, Contributor, Site Consumer, Site Contributor, Site Manager, Site Collaborator]
/*        Assert.assertTrue(roles.contains(UserRole.EDITOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.CONSUMER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.COORDINATOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.CONTRIBUTOR.getRoleName()));*/
        Assert.assertTrue(roles.contains(UserRole.SITECONSUMER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITECONTRIBUTOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITEMANAGER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITECOLLABORATOR.getRoleName()));

        // Cancel the page and come out .
        managePermisisonPage.selectCancel().render();

        // Navigate the folder's manage permission.
        managePermisisonPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Add User2 in Locally set permission and Verify that user2 has Site
        // Collaborator role by default selected.
        managePermisisonPage.selectAddUser().searchAndSelectUser(userProfile);
        Assert.assertEquals(UserRole.SITECOLLABORATOR, managePermisisonPage.getExistingPermission(user2));

        roles = managePermisisonPage.getListOfUserRoles(user2);
        // [Editor, Consumer, Collaborator, Coordinator, Contributor, Site Consumer, Site Contributor, Site Manager, Site Collaborator]
/*        Assert.assertTrue(roles.contains(UserRole.EDITOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.CONSUMER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.COORDINATOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.CONTRIBUTOR.getRoleName()));*/
        Assert.assertTrue(roles.contains(UserRole.SITECONSUMER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITECONTRIBUTOR.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITEMANAGER.getRoleName()));
        Assert.assertTrue(roles.contains(UserRole.SITECOLLABORATOR.getRoleName()));

        // Cancel the page and come out.
        managePermisisonPage.selectCancel().render();

        // User logs out
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14143() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);

        // Create a user1.
        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Consumer"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.CONSUMER);

        // User1 logs outs.
        ShareUser.logout(drone);
    }

    /**
     * Covers 10416
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14143() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user2 in permission.
        // Save.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECOLLABORATOR, true);

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add User2 in permission.
        // Save.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECOLLABORATOR, true);

        // User1 logs outs.
        ShareUser.logout(drone);

        // User2 logs in.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to the site
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User2 has collaborator permission.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditPropertiesLinkPresent());

        // User2 has Collaborator role.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Log out user2.
        ShareUser.logout(drone);

        // User1 logs in.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open site document library.
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate to Document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Set another role to existing user2 which has permission Collaborator
        // to Contributor.
        ShareUserMembers.updateRoleOnContent(drone, user2, fileName, UserRole.SITECONTRIBUTOR, true);

        // Navigate to Folder's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Set another role to existing user2 which has permission Collaborator
        // to Contributor.
        ShareUserMembers.updateRoleOnContent(drone, user2, folderName, UserRole.SITECONTRIBUTOR, true);

        // Log out as user1.
        ShareUser.logout(drone);

        // Login as user 2.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Open Site document library.
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify that user has Contributor permissions.
        // verify that folder has "Add Comment" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // verify that file has "Add comment" button present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // verify that folder has no "Edit Offline" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // verify that file has no "Edit Offline" button present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // user logs out and go home.
        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14144() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");

        String siteName = getSiteName(testName);

        // Create a user1.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        // Create a user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);
        // Create a user4.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);
        // Create a user5.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Manager"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);

        // Add user3 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);

        // Add user4 to the site with Role "Contributor"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);

        // Add user5 to the site with Role "Consumer"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User1 logs outs.
        ShareUser.logout(drone);

    }

    /**
     * Covers 10417, 10397, 10420
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14144() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user2 in permission with "Consumer".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECONSUMER, true);

        // Add user3 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECONSUMER, true);

        // Add user4 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.SITECONSUMER, true);

        // Add user5 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.SITECONSUMER, true);

        // Verify the inherit permission is turned on.
        ManagePermissionsPage managePermisisonPage = ShareUser.returnManagePermissionPage(drone, fileName);
        managePermisisonPage.isInheritPermissionEnabled();

        // Save.
        managePermisisonPage.selectSave();

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user2 in permission with "Consumer".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECONSUMER, true);

        // Add user3 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECONSUMER, true);

        // Add user4 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.SITECONSUMER, true);

        // Add user5 in permission with "Consumer".
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.SITECONSUMER, true);

        // Verify the inherit permission is turned on.
        managePermisisonPage = ShareUser.returnManagePermissionPage(drone, folderName);
        managePermisisonPage.isInheritPermissionEnabled();

        // Save.
        managePermisisonPage.selectSave();

        // User1 logs outs.
        ShareUser.logout(drone);

        // User2 logs in.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to the site
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Log out user2.
        ShareUser.logout(drone);

        // User3 logs in.
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Log out user3.
        ShareUser.logout(drone);

        // User4 logs in.
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment link available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // Verify folder has comment button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // Verify document has no Edit Offline button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has no Edit Properties button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Log out user4.
        ShareUser.logout(drone);

        // User5 logs in.
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has No Comment link available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // Verify folder has No Comment button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // Log out user5.
        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14145() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");

        String siteName = getSiteName(testName);

        // Create a user1.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);
        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);
        // Create a user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);
        // Create a user4.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);
        // Create a user5.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Manager"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);

        // Add user3 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);

        // Add user4 to the site with Role "Contributor"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);

        // Add user5 to the site with Role "Consumer"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User1 logs outs.
        ShareUser.logout(drone);

    }

    /**
     * Covers 10418 , 10398, 10421
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14145() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1");
        String user2 = getUserNameFreeDomain(testName + "-2");
        String user3 = getUserNameFreeDomain(testName + "-3");
        String user4 = getUserNameFreeDomain(testName + "-4");
        String user5 = getUserNameFreeDomain(testName + "-5");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user2 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate to document's manage permission
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user3 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate to document's manage permission
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user4 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate to document's manage permission
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user5 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.SITECONTRIBUTOR, true);

        // Verify the inherit permission is turned on.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);
        managePermissionPage = managePermissionPage.toggleInheritPermission(true, ButtonType.Yes);

        // Save.
        managePermissionPage.selectSave().render();

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user2 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user3 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user4 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user5 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.SITECONTRIBUTOR, true);

        // Verify the inherit permission is turned on.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);
        managePermissionPage = managePermissionPage.toggleInheritPermission(true, ButtonType.Yes);

        // Save.
        managePermissionPage.selectSave().render();

        // User1 logs outs.
        ShareUser.logout(drone);

        // User2 logs in.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to the site
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Log out user2.
        ShareUser.logout(drone);

        // User3 logs in.
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Log out user3.
        ShareUser.logout(drone);

        // User4 logs in.
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment link available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // Verify folder has comment button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // Verify document has no Edit Offline button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has no Edit Properties button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Log out user4.
        ShareUser.logout(drone);

        // User5 logs in.
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment link available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // Verify folder has comment button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // Verify document has no Edit Offline button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has no Edit Properties button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Log out user5.
        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14146() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1A");
        String user2 = getUserNameFreeDomain(testName + "-2A");
        String user3 = getUserNameFreeDomain(testName + "-3A");
        String user4 = getUserNameFreeDomain(testName + "-4A");
        String user5 = getUserNameFreeDomain(testName + "-5A");

        String siteName = getSiteName(testName);

        // Create a user1.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // Create a user2.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user2);

        // Create a user3.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user3);

        // Create a user4.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user4);

        // Create a user5.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user5);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);
        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user2 to the site with Role "Manager"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user2, siteName, UserRole.MANAGER);

        // Add user3 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user3, siteName, UserRole.COLLABORATOR);

        // Add user4 to the site with Role "Contributor"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user4, siteName, UserRole.CONTRIBUTOR);

        // Add user5 to the site with Role "Consumer"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user1, user5, siteName, UserRole.CONSUMER);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User1 logs outs.
        ShareUser.logout(drone);

    }

    /**
     * Covers 10419, 10399, 10422
     * 
     * @throws Exception
     */

    @Test(groups = "AlfrescoOne")
    public void AONE_14146() throws Exception
    {
        String testName = getTestName();
        String user1 = getUserNameFreeDomain(testName + "-1A");
        String user2 = getUserNameFreeDomain(testName + "-2A");
        String user3 = getUserNameFreeDomain(testName + "-3A");
        String user4 = getUserNameFreeDomain(testName + "-4A");
        String user5 = getUserNameFreeDomain(testName + "-5A");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user2 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECOLLABORATOR, true);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user3 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECOLLABORATOR, true);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user4 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.SITECOLLABORATOR, true);

        // Navigate the document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Add user5 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.SITECOLLABORATOR, true);

        // Verify the inherit permission is turned on.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);
        managePermissionPage = managePermissionPage.toggleInheritPermission(true, ButtonType.Yes);

        // Save.
        managePermissionPage.selectSave().render();

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user2 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user2, true, UserRole.SITECOLLABORATOR, true);

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user3 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user3, true, UserRole.SITECOLLABORATOR, true);

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user4 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user4, true, UserRole.SITECOLLABORATOR, true);

        // Navigate the folder's manage permission
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Add user5 in permission with "Contributor".
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user5, true, UserRole.SITECOLLABORATOR, true);

        // Verify the inherit permission is turned on.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);
        managePermissionPage = managePermissionPage.toggleInheritPermission(true, ButtonType.Yes);

        // Save.
        managePermissionPage.selectSave().render();

        // User1 logs outs.
        ShareUser.logout(drone);

        // User2 logs in.
        ShareUser.login(drone, user2, DEFAULT_PASSWORD);

        // Navigate to the site
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has Delete button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Log out user2.
        ShareUser.logout(drone);

        // User3 logs in.
        ShareUser.login(drone, user3, DEFAULT_PASSWORD);

        // Navigate to the site
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Log out user3.
        ShareUser.logout(drone);

        // User4 logs in.
        ShareUser.login(drone, user4, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName).render();

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Log out user4.
        ShareUser.logout(drone);

        // User5 logs in.
        ShareUser.login(drone, user5, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit Off-line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Verify document has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify folder has no Delete button available.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Log out user5.
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14150() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // User logs outs.
        ShareUser.logout(drone);

    }

    /**
     * Covers 10423.
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14150() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        // Create a user1.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Add user1 to the site with Role "Collaborator"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user, user1, siteName, UserRole.COLLABORATOR);

        // Open Site Document library.
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate to manage permission of document.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Assign role Contributor.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate to manage permission of folder.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Assign role Contributor.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONTRIBUTOR, true);

        // User logs out.
        ShareUser.logout(drone);

        // User1 logs in.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Site Document library.
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit off-line button present but no delete
        // button.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // Verify document has Edit properties button present but no delete
        // button.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditPropertiesLinkPresent());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        // User1 logs out.
        ShareUser.logout(drone);

        // User logs in.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Open Site Document Library.
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate to Manage permission of document.
        ManagePermissionsPage managePermisionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Focus cursor on User1 and delete the user1 from permission.
        // Save changes.
        managePermisionPage.deleteUserOrGroupFromPermission(user1, UserRole.SITECONTRIBUTOR);

        // Navigate to Manage permission of document.
        managePermisionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Verify that user1 does not exist.
        Assert.assertFalse(managePermisionPage.isUserExistForPermission(user1));

        // Cancel from page.
        managePermisionPage.selectCancel();

        // Navigate to Manage permission of folder.
        managePermisionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Focus cursor on User1 and delete the user1 from permission.
        // Save changes.
        managePermisionPage.deleteUserOrGroupFromPermission(user1, UserRole.SITECONTRIBUTOR);

        // Navigate to Manage permission of document.
        managePermisionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Verify that user1 does not exist.
        Assert.assertFalse(managePermisionPage.isUserExistForPermission(user1));

        // Cancel from page.
        managePermisionPage.selectCancel();

        // User logs out.
        ShareUser.logout(drone);

        // User1 logs in.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Site document library.
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has comment link available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // Verify folder has comment button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // Verify document has no Edit Off line button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify folder has no Edit Properties button available.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // User1 logs out.
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14151() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName + 10);
        String user1 = getUserNameFreeDomain(testName + "-10");

        String siteName = getSiteName(testName + 0);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);
        File file = newFile(fileName, "New file");

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user1 to the site with Role "Site Manager"
        ShareUserMembers.inviteUserToSiteWithRole(drone, user, user1, siteName, UserRole.MANAGER);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Create a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // User logs outs.
        ShareUser.logout(drone);
    }

    /**
     * Covers 10424.
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14151() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName + 10);

        String siteName = getSiteName(testName + 0);
        String folderName = getFolderName(testName);
        String fileName = getFileName(testName);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Open Site Document library.
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate to manage permission of document.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Turn off inherit permissions.
        managePermissionPage = managePermissionPage.toggleInheritPermission(false, ButtonType.Yes);

        // Save Changes.
        managePermissionPage.selectSave().render();

        // Navigate to manage permission of folder.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Turn off inherit permissions.
        managePermissionPage = managePermissionPage.toggleInheritPermission(false, ButtonType.Yes);

        // Save Changes.
        managePermissionPage.selectSave().render();

        // Navigate to manage permission of folder.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Verify in Locally set permission user/group
        // site_ssiteName_SiteManager with role Manager is present.
        Assert.assertEquals(managePermissionPage.getExistingPermission("site_" + siteName.toLowerCase() + "_SiteManager"), UserRole.SITEMANAGER);

        // Select user/group site_ssiteName_SiteManager with role Manager is
        // present and look for Delete action, which is not present.
        Assert.assertFalse(managePermissionPage.isDeleteActionPresent("site_" + siteName.toLowerCase() + "_SiteManager", UserRole.SITEMANAGER));

        // Turn on inherit permission.
        managePermissionPage = managePermissionPage.toggleInheritPermission(true, ButtonType.Yes);

        // Save changes.
        managePermissionPage.selectSave().render();

        // Navigate to manage permission of document.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Verify in Locally set permission user/group
        // site_ssiteName_SiteManager with role Manager is present.
        Assert.assertEquals(managePermissionPage.getExistingPermission("site_" + siteName.toLowerCase() + "_SiteManager"), UserRole.SITEMANAGER);

        // Select user/group site_ssiteName_SiteManager with role Manager is
        // present and look for Delete action, which is not present.
        Assert.assertFalse(managePermissionPage.isDeleteActionPresent("site_" + siteName.toLowerCase() + "_SiteManager", UserRole.SITEMANAGER));

        // Turn on inherit permission.
        managePermissionPage = managePermissionPage.toggleInheritPermission(true, ButtonType.Yes);

        // Save changes.
        managePermissionPage.selectSave().render();

        // Navigate to manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Verify in Locally set permission user/group
        // site_ssiteName_SiteManager with role Manager is present.
        Assert.assertEquals(managePermissionPage.getExistingPermission("site_" + siteName.toLowerCase() + "_SiteManager"), UserRole.SITEMANAGER);

        // Select user/group site_ssiteName_SiteManager with role Manager is
        // present and look for Delete action, which is present.
        Assert.assertTrue(managePermissionPage.isDeleteActionPresent("site_" + siteName.toLowerCase() + "_SiteManager", UserRole.SITEMANAGER));

        // Delete user from the table.
        managePermissionPage.deleteUserWithPermission("site_" + siteName.toLowerCase() + "_SiteManager", UserRole.SITEMANAGER);

        // save changes.
        managePermissionPage.selectSave().render();

        // Navigate to manage permission.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Verify in Locally set permission user/group
        // site_ssiteName_SiteManager with role Manager is present.
        Assert.assertEquals(managePermissionPage.getExistingPermission("site_" + siteName.toLowerCase() + "_SiteManager"), UserRole.SITEMANAGER);

        // Select user/group site_ssiteName_SiteManager with role Manager is
        // present and look for Delete action, which is present.
        Assert.assertTrue(managePermissionPage.isDeleteActionPresent("site_" + siteName.toLowerCase() + "_SiteManager", UserRole.SITEMANAGER));

        managePermissionPage.deleteUserWithPermission("site_" + siteName.toLowerCase() + "_SiteManager", UserRole.SITEMANAGER);

        // save changes.
        managePermissionPage.selectSave().render();

        // Navigate to manage permission of folder.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        // Role and group is been deleted verify they dont exist in Locally set
        // permission Table.
        Assert.assertFalse(managePermissionPage.isUserExistForPermission("site_" + siteName.toLowerCase() + "_SiteManager"));

        // Save changes.
        managePermissionPage.selectSave().render();

        // Navigate to manage permission of folder.
        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        // Role and group is been deleted verify they dont exist in Locally set
        // permission Table.
        Assert.assertFalse(managePermissionPage.isUserExistForPermission("site_" + siteName.toLowerCase() + "_SiteManager"));

        // Save changes.
        managePermissionPage.selectSave().render();

        // User logs out.
        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14152() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Navigate to the site
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Add user1 to Site as Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, user, user1, siteName, UserRole.CONSUMER);

        // User logs outs.
        ShareUser.logout(drone);
    }

    /**
     * Covers 10792.
     * 
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14152() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Open Site document Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a document.
        ShareUserSitePage.uploadFile(drone, file);

        // Upload a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate to document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Search and add user1 with role Contributor.
        // Save changes.
        // ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.CONTRIBUTOR, true);

        ManagePermissionsPage managePermissionPage = ShareUserMembers.searchAndAddUserOrGroupWithoutSave(drone, user1, true);
        Assert.assertTrue(UserRole.SITECONSUMER.equals(managePermissionPage.getExistingPermission(user1)));
        managePermissionPage.updateUserRole(user1, UserRole.SITECONTRIBUTOR);
        managePermissionPage.selectSave();

        // Navigate to folder's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Search and add user1 with role Contributor.
        // Save changes.
        managePermissionPage = ShareUserMembers.searchAndAddUserOrGroupWithoutSave(drone, user1, true);
        Assert.assertTrue(UserRole.SITECONSUMER.equals(managePermissionPage.getExistingPermission(user1)));
        managePermissionPage.updateUserRole(user1, UserRole.SITECONTRIBUTOR);
        managePermissionPage.selectSave();

        // Navigate to document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Search and add user1 with role Collaborator.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true);

        // Navigate to folder's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Search and add user1 with role Collaborator.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true);

        // user logs out.
        ShareUser.logout(drone);

        // user1 logs in.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Site document Library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // User has collaborator level permission

        // Verify document has Edit off line present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // VErify folder has Edit off line present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14155() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName) + "Sub";

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Add user1 to Site as Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, user, user1, siteName, UserRole.CONSUMER);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a folder.
        DocumentLibraryPage docLibPage = ShareUserSitePage.createFolder(drone, folderName, folderName);

        docLibPage.selectFolder(folderName);

        ShareUserSitePage.createFolder(drone, subFolderName, subFolderName);

        // Navigate to the site document library
        ShareUser.openDocumentLibrary(drone);

        ShareUser.returnManagePermissionPage(drone, folderName);

        docLibPage = ((DocumentLibraryPage) ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true)).render();

        // User logs outs.
        ShareUser.logout(drone);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14155() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName) + "Sub";

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Navigate to the site document library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage.selectFolder(folderName).render();

        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, subFolderName);

        Assert.assertTrue(managePermissionPage.isInheritPermissionEnabled());

        managePermissionPage.selectCancel().render();

        ShareUser.logout(drone);

        // User logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage = ((DocumentLibraryPage) docLibPage.selectFolder(folderName)).render();

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(subFolderName).isEditPropertiesLinkPresent());

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14156() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName + "Sub");

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user1 to Site as Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, user, user1, siteName, UserRole.CONSUMER);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUser.returnManagePermissionPage(drone, folderName);

        DocumentLibraryPage docLibPage = ((DocumentLibraryPage) ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true)).render();

        docLibPage.selectFolder(folderName).render();

        ShareUserSitePage.createFolder(drone, subFolderName, subFolderName);

        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, subFolderName);
        managePermissionPage = managePermissionPage.toggleInheritPermission(false, ButtonType.Yes).render();
        managePermissionPage.selectSave().render();

        // User logs outs.
        ShareUser.logout(drone);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14156() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName + "Sub");

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Navigate to the site document library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage.selectFolder(folderName).render();

        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, subFolderName);

        Assert.assertFalse(managePermissionPage.isInheritPermissionEnabled());

        managePermissionPage.selectCancel().render();

        ShareUser.logout(drone);

        // User logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage = ((DocumentLibraryPage) docLibPage.selectFolder(folderName)).render();

        Assert.assertFalse(docLibPage.isFileVisible(subFolderName));

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14157() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName + "Sub");

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user1 to Site as Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, user, user1, siteName, UserRole.CONSUMER);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a folder.
       ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUser.returnManagePermissionPage(drone, folderName);

        DocumentLibraryPage docLibPage = ((DocumentLibraryPage) ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true)).render();

        docLibPage.selectFolder(folderName);

        ShareUserSitePage.createFolder(drone, subFolderName, subFolderName);

        // User logs outs.
        ShareUser.logout(drone);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14157() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName);
        String subFolderName = getFolderName(testName + "Sub");

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Navigate to the site document library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage.selectFolder(folderName).render();

        ShareUserMembers.managePermissionsOnContent(drone, user1, subFolderName, UserRole.SITECONTRIBUTOR, true);

        ShareUser.logout(drone);

        // User logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        docLibPage = ((DocumentLibraryPage) docLibPage.selectFolder(folderName)).render();

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(subFolderName).isEditPropertiesLinkPresent());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(subFolderName).isDeletePresent());
        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14158() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // User logs outs.
        ShareUser.logout(drone);

        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        ShareUser.logout(drone);
    }

    @Test(groups = "AlfrescoOne")
    public void AONE_14158() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();
        String subFolderName = getFolderName(testName + "sub") + System.currentTimeMillis();

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Navigate to the site document library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        ShareUser.returnManagePermissionPage(drone, folderName);

        DocumentLibraryPage docLibPage = ((DocumentLibraryPage) ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITEMANAGER, true)).render();

        docLibPage.selectFolder(folderName);

        ShareUserSitePage.createFolder(drone, subFolderName, subFolderName);

        ShareUser.returnManagePermissionPage(drone, subFolderName);

        docLibPage = ((DocumentLibraryPage) ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true)).render();

        // Navigate to the site document library
        ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, subFolderName);

        Assert.assertTrue(managePermissionPage.isInheritPermissionEnabled());

        ShareUser.logout(drone);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        docLibPage = ((DocumentLibraryPage) docLibPage.selectFolder(folderName)).render();
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(subFolderName).isDeletePresent());

        ShareUser.logout(drone);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Navigate to the site document library
        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        docLibPage.selectFolder(folderName).render();

        managePermissionPage = ShareUser.returnManagePermissionPage(drone, subFolderName);

        managePermissionPage.toggleInheritPermission(false, ButtonType.Yes);

        Assert.assertFalse(managePermissionPage.isInheritPermissionEnabled());

        managePermissionPage.selectSave().render();

        ShareUser.logout(drone);

        // User1 logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        docLibPage = ((DocumentLibraryPage) docLibPage.selectFolder(folderName)).render();

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(subFolderName).isDeletePresent());

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(subFolderName).isEditPropertiesLinkPresent());

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14153() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // User logs outs.
        ShareUser.logout(drone);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14153() throws Exception
    {
        String testName = getTestName();
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Open Site document Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        ShareUserSitePage.uploadFile(drone, file);

        // Upload a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);

        // Navigate to folder's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Search and add user1 with role Contributor.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate to folder's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Search and add user1 with role Contributor.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONTRIBUTOR, true);

        // Navigate to document's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Search and add user1 with role Consumer.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONSUMER, true);

        // Navigate to document's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Search and add user1 with role Consumer.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONSUMER, true);

        // user logs out.
        ShareUser.logout(drone);

        // user1 logs in.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Site document Library
        DocumentLibraryPage docLibPage = ShareUser.openSiteDocumentLibraryFromSearch(drone, siteName);

        // Verify folder has Comment link present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isCommentLinkPresent());

        // Verify folder has not Edit properties present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // Verify folder has not Delete present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Verify document has Comment link present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isCommentLinkPresent());

        // Verify document has not Edit offline present
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());

        // Verify document has not Delete present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        ShareUser.logout(drone);

    }

    @Test(groups = { "DataPrepAlfrescoOne" })
    public void dataPrep_AONE_14154() throws Exception
    {
        String testName = getTestName() + "AB";
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user);

        // Create a user.
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, user1);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Create a site.
        SiteUtil.createSite(drone, siteName, siteName, SITE_VISIBILITY_PUBLIC, true);

        // Add user1 to Site as Consumer
        ShareUserMembers.inviteUserToSiteWithRole(drone, user, user1, siteName, UserRole.COLLABORATOR);

        // User logs outs.
        ShareUser.logout(drone);
    }

    /**
     * @throws Exception
     */
    @Test(groups = "AlfrescoOne")
    public void AONE_14154() throws Exception
    {
        String testName = getTestName() +"AB";
        String user = getUserNameFreeDomain(testName);
        String user1 = getUserNameFreeDomain(testName + "-1");

        String siteName = getSiteName(testName);
        String folderName = getFolderName(testName) + System.currentTimeMillis();

        String fileName = getFileName(testName) + System.currentTimeMillis();
        File file = newFile(fileName, "New file");

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        // Open Site document Library
        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Upload a folder.
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserSitePage.uploadFile(drone, file);

        // Navigate to folder's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Search and add user1 with role Contributor.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONTRIBUTOR, true);
        ShareUser.returnManagePermissionPage(drone, folderName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true);

        // Navigate to file's manage permission.
        ShareUser.returnManagePermissionPage(drone, fileName);

        // Search and add user1 with role Contributor.
        // Save changes.
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECONTRIBUTOR, true);
        ShareUser.returnManagePermissionPage(drone, fileName);
        ShareUserMembers.addUserOrGroupIntoInheritedPermissions(drone, user1, true, UserRole.SITECOLLABORATOR, true);

        ShareUser.logout(drone);

        // User logins.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Site document Library
        DocumentLibraryPage docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditOfflineLinkPresent());
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        ShareUser.logout(drone);

        // User logins.
        ShareUser.login(drone, user, DEFAULT_PASSWORD);

        ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Navigate to document's manage permission.
        ShareUser.returnManagePermissionPage(drone, folderName);

        // Navigate to document's manage permission.
        ManagePermissionsPage managePermissionPage = ShareUser.returnManagePermissionPage(drone, folderName);

        managePermissionPage = managePermissionPage.deleteUserWithPermission(user1, UserRole.SITECOLLABORATOR);

        Assert.assertTrue(managePermissionPage.isUserExistForPermission(user1));

        managePermissionPage.selectSave().render();

        managePermissionPage = ShareUser.returnManagePermissionPage(drone, fileName);

        managePermissionPage = managePermissionPage.deleteUserWithPermission(user1, UserRole.SITECOLLABORATOR);

        Assert.assertTrue(managePermissionPage.isUserExistForPermission(user1));

        // user logs out.
        ShareUser.logout(drone);

        // user1 logs in.
        ShareUser.login(drone, user1, DEFAULT_PASSWORD);

        // Open Site document Library
        docLibPage = ShareUser.openSitesDocumentLibrary(drone, siteName);

        // Verify document has Edit off line present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(folderName).isEditPropertiesLinkPresent());

        // VErify folder has Edit off line present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(folderName).isDeletePresent());

        // Verify document has Edit off line present.
        Assert.assertTrue(docLibPage.getFileDirectoryInfo(fileName).isEditPropertiesLinkPresent());

        // VErify folder has Edit off line present.
        Assert.assertFalse(docLibPage.getFileDirectoryInfo(fileName).isDeletePresent());

        ShareUser.logout(drone);

    }

}
