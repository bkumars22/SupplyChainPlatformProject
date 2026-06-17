/*
 *
 */
/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.test.selenium.scplatform.ui.administration.manageRoles.roleRights.administrator;

import com.test.selenium.common.modelViewController.model.Model;
import com.google.common.base.Preconditions;

/**
 * Navigation: Administration -> Manage Roles
 *
 * @author dgenrich
 */
public class AdministratorModel extends Model {

    private static final long serialVersionUID = 1L;

    private String viewRole;
    private String viewUser;
    private String viewContact;
    private String viewBusiness;
    private String viewAssignmentRules;
    private String viewComplianceRules;
    private String changeDashboardNewsAlertItems;
    private String changeItems;
    private String enterpriseWideVisibility;
    private String changeRole;
    private String changeUser;
    private String changeContact;
    private String changeBusiness;
    private String changeAssignmentRules;
    private String changeComplianceRules;
    private String dataManagementUpload;
    private String createRole;
    private String createContact;
    private String createAssignmentRules;
    private String createComplianceRules;

    public String getViewRole() {
        return viewRole;
    }

    public void setViewRole(String viewRole) {
        checkParameter("viewRole", viewRole);
        this.viewRole = viewRole.toLowerCase();
    }

    public String getViewUser() {
        return viewUser;
    }

    public void setViewUser(String viewUser) {
        checkParameter("viewUser", viewUser);
        this.viewUser = viewUser.toLowerCase();
    }

    public String getViewContact() {
        return viewContact;
    }

    public void setViewContact(String viewContact) {
        checkParameter("viewContact", viewContact);
        this.viewContact = viewContact.toLowerCase();
    }

    public String getViewBusiness() {
        return viewBusiness;
    }

    public void setViewBusiness(String viewBusiness) {
        checkParameter("viewBusiness", viewBusiness);
        this.viewBusiness = viewBusiness.toLowerCase();
    }

    public String getViewAssignmentRules() {
        return viewAssignmentRules;
    }

    public void setViewAssignmentRules(String viewAssignmentRules) {
        checkParameter("viewAssignmentRules", viewAssignmentRules);
        this.viewAssignmentRules = viewAssignmentRules.toLowerCase();
    }

    public String getViewComplianceRules() {
        return viewComplianceRules;
    }

    public void setViewComplianceRules(String viewComplianceRules) {
        checkParameter("viewComplianceRules", viewComplianceRules);
        this.viewComplianceRules = viewComplianceRules.toLowerCase();
    }

    public String getChangeDashboardNewsAlertItems() {
        return changeDashboardNewsAlertItems;
    }

    public void setChangeDashboardNewsAlertItems(String changeDashboardNewsAlertItems) {
        checkParameter("changeDashboardNewsAlertItems", changeDashboardNewsAlertItems);
        this.changeDashboardNewsAlertItems = changeDashboardNewsAlertItems.toLowerCase();
    }

    public String getChangeItems() {
        return changeItems;
    }

    public void setChangeItems(String changeItems) {
        checkParameter("changeItems", changeItems);
        this.changeItems = changeItems.toLowerCase();
    }

    public String getEnterpriseWideVisibility() {
        return enterpriseWideVisibility;
    }

    public void setEnterpriseWideVisibility(String enterpriseWideVisibility) {
        checkParameter("enterpriseWideVisibility", enterpriseWideVisibility);
        this.enterpriseWideVisibility = enterpriseWideVisibility.toLowerCase();
    }

    public String getChangeRole() {
        return changeRole;
    }

    public void setChangeRole(String changeRole) {
        checkParameter("changeRole", changeRole);
        this.changeRole = changeRole.toLowerCase();
    }

    public String getChangeUser() {
        return changeUser;
    }

    public void setChangeUser(String changeUser) {
        checkParameter("changeUser", changeUser);
        this.changeUser = changeUser.toLowerCase();
    }

    public String getChangeContact() {
        return changeContact;
    }

    public void setChangeContact(String changeContact) {
        checkParameter("changeContact", changeContact);
        this.changeContact = changeContact.toLowerCase();
    }

    public String getChangeBusiness() {
        return changeBusiness;
    }

    public void setChangeBusiness(String changeBusiness) {
        checkParameter("changeBusiness", changeBusiness);
        this.changeBusiness = changeBusiness.toLowerCase();
    }

    public String getChangeAssignmentRules() {
        return changeAssignmentRules;
    }

    public void setChangeAssignmentRules(String changeAssignmentRules) {
        checkParameter("changeAssignmentRules", changeAssignmentRules);
        this.changeAssignmentRules = changeAssignmentRules.toLowerCase();
    }

    public String getChangeComplianceRules() {
        return changeComplianceRules;
    }

    public void setChangeComplianceRules(String changeComplianceRules) {
        checkParameter("v", changeComplianceRules);
        this.changeComplianceRules = changeComplianceRules.toLowerCase();
    }

    public String getDataManagementUpload() {
        return dataManagementUpload;
    }

    public void setDataManagementUpload(String dataManagementUpload) {
        checkParameter("dataManagementUpload", dataManagementUpload);
        this.dataManagementUpload = dataManagementUpload.toLowerCase();
    }

    public String getCreateRole() {
        return createRole;
    }

    public void setCreateRole(String createRole) {
        checkParameter("createRole", createRole);
        this.createRole = createRole.toLowerCase();
    }

    public String getCreateContact() {
        return createContact;
    }

    public void setCreateContact(String createContact) {
        checkParameter("createContact", createContact);
        this.createContact = createContact.toLowerCase();
    }

    public String getCreateAssignmentRules() {
        return createAssignmentRules;
    }

    public void setCreateAssignmentRules(String createAssignmentRules) {
        checkParameter("createAssignmentRules", createAssignmentRules);
        this.createAssignmentRules = createAssignmentRules.toLowerCase();
    }

    public String getCreateComplianceRules() {
        return createComplianceRules;
    }

    public void setCreateComplianceRules(String createComplianceRules) {
        checkParameter("createComplianceRules", createComplianceRules);
        this.createComplianceRules = createComplianceRules.toLowerCase();
    }

    private void checkParameter(String subVaraible, String subValue) {
        Preconditions.checkArgument(
                (("true".equals(subValue.toLowerCase())) || ("false".equals(subValue.toLowerCase()))),
                "Invalid value for %s (%s).  %s can only be 'true' or 'false'", subVaraible, subValue, subVaraible);
    }
}
