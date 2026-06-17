/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.accessControl.service;

import java.io.InputStream;
import java.net.URL;
import java.util.*;

import com.scplatform.pcm.accessControl.dto.ACLType;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.role.entity.Role;
import com.scplatform.pcm.util.common.SCPlatformConstant;
import com.scplatform.pcm.workflow.entity.Workflow;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scplatform.pcm.accessControl.entity.AccessControl;
import com.scplatform.pcm.accessControl.repository.AccessControlRepository;
import com.scplatform.pcm.user.entity.Users;

import lombok.RequiredArgsConstructor;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

@Service
@RequiredArgsConstructor
public class AccessControlService {

    private static final Logger logger = LoggerFactory.getLogger(AccessControlService.class);

    private final AccessControlRepository accessControlRepository;
    private final BusinessEntityService businessEntityService;

    @Transactional(readOnly = true)
    public List<AccessControl> getRoleACLs(Long roleId) {
        logger.debug("Fetching access controls for roleId={}", roleId);
        return accessControlRepository.getRoleACLs(roleId);
    }

    @Transactional(readOnly = true)
    public List<AccessControl> getUserEntityACLs(Long userKey, String entityType, String aclValue) {
        logger.debug("Fetching access controls for userKey={}, entityType={}, aclValue={}", userKey, entityType, aclValue);
        return accessControlRepository.getUserEntityACLs(userKey, entityType, aclValue);
    }

    @Transactional(readOnly = true)
    public List<AccessControl> getRoleEntityACLs(Long roleKey, String entityType, String aclValue) {
        logger.debug("Fetching access controls for roleKey={}, entityType={}, aclValue={}", roleKey, entityType, aclValue);
        return accessControlRepository.getRoleEntityACLs(roleKey, entityType, aclValue);
    }

    @Transactional(readOnly = true)
    public List<AccessControl> getACLsForUser(Users user, String entityType, String aclValue) {
        if (user == null) {
            throw new IllegalArgumentException("User must be specified");
        }

        Long roleKey = user.getRole() == null ? null : user.getRole().getRoleKey();
        logger.debug("Fetching access controls for userKey={}, roleKey={}, entityType={}, aclValue={}",
                user.getUserKey(), roleKey, entityType, aclValue);
        return accessControlRepository.getACLsForUser(user.getUserKey(), roleKey, entityType, aclValue);
    }

    @Transactional(readOnly = true)
    public boolean doesUserHaveACLs(Users user, String entityType, String aclValue) {
        if (user == null) {
            throw new IllegalArgumentException("User must be specified");
        }

        logger.debug("Checking access controls for userKey={}, entityType={}, aclValue={}",
                user.getUserKey(), entityType, aclValue);
        return accessControlRepository.doesUserHaveACLs(user, entityType, aclValue);
    }

    /**
     * Returns true if the current user is not a member of the owning
     * enterprise.  This value is used when determining what access
     * filters need to be enabled.  For instance supplier or mfg filters
     * should be enabled if the user is not part of the enterprise.
     * @return
     */
    public boolean getIsExternalUser(Users user, Long enterpriseBusinessKey)
    {
        if (user != null)
        {
            if (user.getBusinessEntity() != null)
            {
                // Not the enterprise, then must be external
                return !(user.getBusinessEntity().getBusinessEntityKey().equals(enterpriseBusinessKey));
            }
            // No business, assume internal
            return false;
        }
        return true;
    }

    /**
     * Returns true if the user is not a member of the hub company
     * or does not have a delegated admin rights
     * @return
     */
    public boolean getHasRestrictedVisiblity(Users user)
    {
        if (hasAccess(SCPlatformConstant.ADMIN_TYPE,"GlobalVisibility",null,
                user.getRole(),user.getRole().getAcls()))
        {
            return false;
        }
        if (getIsExternalUser(user, businessEntityService.getEnterpriseBusinessEntityKey()))
        {
            return true;
        }
        return false;
    }

    public List<Workflow> getAccessableWorkflows(List<Workflow> workflows, Role role)
    {
        List<Workflow> results = new ArrayList<Workflow>();
        Workflow wf;
        List<AccessControl> roleACLs = getRoleACLs(role.getRoleKey());

        for (int idx = 0; idx < workflows.size(); idx++) {
            wf = (Workflow) workflows.get(idx);
            if (logger.isDebugEnabled()) {
                logger.debug("Looking for WF " + wf.getWorkflowKey());
            }

            if (!hasAccess("WORKFLOW", "ExecuteAll", wf.getAcls(), role, roleACLs)) {
                if (wf.getNestedWorkflows() != null) {
                    HashSet<Workflow> remove = new HashSet<Workflow>();
                    Iterator<?> itr = wf.getNestedWorkflows().iterator();
                    while (itr.hasNext()) {
                        Workflow child = (Workflow) itr.next();
                        if (hasAccess("WORKFLOW", "Execute", child.getAcls(), role, roleACLs) == false) {
                            if (logger.isDebugEnabled()) {
                                logger.debug("Role '" + role.getRoleName() + "' , access denied to workflow '"
                                        + child.getWorkflowName() + "'");
                            }
                            remove.add(child);
                        }
                    }
                    wf.getNestedWorkflows().removeAll(remove);
                }
                if (wf.getNestedWorkflows() != null && !wf.getNestedWorkflows().isEmpty())
                    results.add(wf);
            } else {
                results.add(wf);
            }
        }
        return results;
    }

    public boolean hasGlobalAccess(Collection<AccessControl> roleACLs, String entityType, String op, String entityKey)
    {
        if (roleACLs != null)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("GB Looking for entityType:" + entityType + " op:" + op);
            }

            AccessControl acl;
            Iterator<AccessControl> itr = roleACLs.iterator();
            while (itr.hasNext())
            {
                acl = (AccessControl)itr.next();
                if (logger.isDebugEnabled())
                {
                    logger.debug("GB Checking acl:" + acl.getAcl() + " aclKey:" + acl.getEntityKey()
                            + " type:" + acl.getEntityType() + " roleKey:" + acl.getRole().getRoleKey());
                }

                if ((SCPlatformConstant.ANY_ENTITY.equals(acl.getEntityKey()) || acl.getEntityKey() == null)
                        && entityType.equals(acl.getEntityType()))
                {
                    if (acl.getAcl().equals(SCPlatformConstant.ALL_OP) || acl.getAcl().equals(op))
                    {
                        return true;
                    }

                }
                else if(acl.getEntityKey()!=null && entityType.equals(acl.getEntityType()) )
                {
                    if(entityKey!=null)
                    {
                        if (acl.getAcl().equals(SCPlatformConstant.ALL_OP) || (acl.getAcl().equals(op) && acl.getEntityKey().equals(entityKey)))
                        {
                            return true;
                        }
                    }

                }

            }
        }
        return false;
    }
    public boolean hasAccess(String type, String op,
                                    Collection<AccessControl> entityACLs,
                                    Role role, Collection<AccessControl> roleACLs)
    {
        return hasAccess(type, op, null, entityACLs,role,roleACLs);
    }

    public boolean hasAccess(String type, String op, String entityTypeKey,
                                    Collection<AccessControl> entityACLs,
                                    Role role, Collection<AccessControl> roleACLs)
    {
        if (hasGlobalAccess(roleACLs,type,op,entityTypeKey))
        {
            return true;
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("EA looking for op:" + op + " roleKey:" + role.getRoleKey());
        }

        if (entityACLs != null)
        {
            Iterator<AccessControl> itr = entityACLs.iterator();
            while (itr.hasNext())
            {
                AccessControl acl = (AccessControl)itr.next();

                if (logger.isDebugEnabled())
                {
                    logger.debug("EA checking acl:" + acl.getAcl() + " entityKey:" + acl.getEntityKey()
                            + " entityType:" + acl.getEntityType() + " roleKey:" + acl.getRole().getRoleKey());
                }
                if (acl.getRole().getRoleKey().equals(role.getRoleKey()))
                {
                    if (acl.getAcl().equals(SCPlatformConstant.ALL_OP) || acl.getAcl().equals(op))
                    {
                        return true;
                    }

                }
            }
        }
        return false;
    }

    public Set<String> getDataFilterKeys(Users user, String dataType)
    {
        Set<String> keys = new HashSet<String>();
        List<AccessControl> acls = getUserEntityACLs(user.getUserKey(),dataType,"Read");
        for (int idx=0; idx < acls.size(); idx++)
        {
            keys.add(acls.get(idx).getEntityKey());
        }
        acls = getRoleEntityACLs(user.getRole().getRoleKey(),dataType,"Read");
        for (int idx=0; idx < acls.size(); idx++)
        {
            keys.add(acls.get(idx).getEntityKey());
        }
        return keys;
    }

    public List<Long> getResponderBusinessEntityKeys(Users user, String aclType)
    {
        List<AccessControl> acls = getUserEntityACLs(user.getUserKey(),"BUSINESS_ENTITY",aclType);
        List<Long> keys = new ArrayList<Long>(acls.size());
        for (int idx=0; idx < acls.size(); idx++)
        {
            Long key = Long.valueOf(acls.get(idx).getEntityKey());
            keys.add(key);
        }
        return keys;
    }

    /**
     * Updates the ALCs for when a user is an agent of another business
     * @param user
     * @param businessEntityKeys list of the BusinessEntityKeys
     */
    @Transactional
    public void setupBusinessEntityAgents(Users user, Long[] businessEntityKeys)
    {
        List<AccessControl> orgacls = getUserEntityACLs(user.getUserKey(),"BUSINESS_ENTITY",SCPlatformConstant.AGENT_BE_ACL);
        List<AccessControl> newAcls = new ArrayList<AccessControl>();
        List<AccessControl> removeAcls = new ArrayList<AccessControl>(orgacls);
        if (businessEntityKeys != null)
        {
            for (int idx=0; idx < businessEntityKeys.length; idx++)
            {
                BusinessEntity be = businessEntityService.getBusinessEntity(businessEntityKeys[idx]);
                AccessControl acl = new AccessControl();
                acl.setUser(user);
                acl.setEntityType("BUSINESS_ENTITY");
                acl.setAcl(SCPlatformConstant.AGENT_BE_ACL);
                acl.setEntityKey(be.getBusinessEntityKey().toString());
                newAcls.add(acl);
            }
            // Remove the ones that are no longer in the list;
            removeAcls.removeAll(newAcls);

            // Add the difference
            for (int idx=0; idx < newAcls.size(); idx++)
            {
                AccessControl acl = (AccessControl)newAcls.get(idx);
                if (orgacls.contains(acl) == false)
                {
                    accessControlRepository.save(acl);
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Adding BusinessEntity Agent ACL to user " + user.getUserId()
                                + " on BusinessEntityKey=" + acl.getEntityKey());
                    }

                }
            }

        }
        // Anything to remove
        for (int idx=0; idx < removeAcls.size(); idx++)
        {
            AccessControl acl = (AccessControl)removeAcls.get(idx);
            accessControlRepository.delete(acl);
            if (logger.isDebugEnabled())
            {
                logger.debug("Removing BusinessEntity Agent ACL from user "
                        + user.getUserId() + " on BusinessEntityKey=" + acl.getEntityKey());
            }
        }
    }


    public Map<String,List<ACLType>> loadACLDefinitions(URL aclModel)
    {
        Map<String,List<ACLType>> results = new LinkedHashMap<String,List<ACLType>>();
        XMLStreamReader xmlr = null;
        InputStream fis = null;
        try
        {
            fis = aclModel.openStream();
            XMLInputFactory xmlif = XMLInputFactory.newInstance();
            xmlif.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES,Boolean.TRUE);
            xmlif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES,Boolean.FALSE);
            xmlif.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE , Boolean.TRUE);
            xmlif.setProperty(XMLInputFactory.IS_COALESCING , Boolean.TRUE);
            xmlr = xmlif.createXMLStreamReader(fis);
            List<ACLType> activeDocACLs = null;
            String activeDocType = null;
            while (xmlr.hasNext())
            {
                xmlr.next();
                if (xmlr.isStartElement() == false && xmlr.isEndElement() == false)
                {
                    continue;
                }
                if ("BusinessObject".equals(xmlr.getLocalName()))
                {
                    if (xmlr.isStartElement())
                    {
                        activeDocType = xmlr.getAttributeValue(null, "name");
                        activeDocACLs = results.get(activeDocType);
                        if (activeDocACLs == null)
                        {
                            activeDocACLs = new ArrayList<ACLType>();
                            results.put(activeDocType, activeDocACLs);
                        }
                    }
                    else if (xmlr.isEndElement())
                    {
                        activeDocACLs = null;
                        activeDocType = null;
                    }
                }
                else if ("ACL".equals(xmlr.getLocalName()))
                {
                    if (xmlr.isStartElement() && activeDocACLs != null)
                    {
                        ACLType type = new ACLType(xmlr.getAttributeValue(null, "action"),
                                StringUtils.trimToNull(xmlr.getAttributeValue(null, "requiredBy")),
                                StringUtils.trimToNull(xmlr.getAttributeValue(null, "requires")),
                                StringUtils.trimToNull(xmlr.getAttributeValue(null, "excludes")));
                        type.setBusinessObjectType(activeDocType);
                        activeDocACLs.add(type);
                    }
                }
            }


        }
        catch(Exception e)
        {
            logger.error("Loading ACL Definitions",e);
        }
        finally
        {
            if (xmlr != null)
            {
                try
                {
                    xmlr.close();
                }
                catch (XMLStreamException e)
                {
                    logger.error("Loading ACL Definitions",e);
                }
            }
            IOUtils.closeQuietly(fis);
        }
        return results;
    }
}
