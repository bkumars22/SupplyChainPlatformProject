/*
 * Copyright (c) 2024 E2open Inc. All Rights Reserved
 */
package com.scplatform.pcm.authentication.service;

import com.scplatform.pcm.SpringContextHolder;
import com.scplatform.pcm.accessControl.service.AccessControlService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.dto.InvalidUserContext;
import com.scplatform.pcm.authentication.exception.NotAuthorizedException;
import com.scplatform.pcm.bom.entity.PcmDefectType;
import com.scplatform.pcm.bom.service.PcmDefectService;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.util.common.SCPlatformConstant;
import com.scplatform.pcm.util.jpa.JPAFilterUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static com.scplatform.pcm.util.common.SCPlatformConstant.*;


public final class AppContextHelper {

    private static final Logger logger = LoggerFactory.getLogger(AppContextHelper.class);

    private AppContextHelper() {}

    /**
     * Checks if the user has the specified acl type and specific acl
     *
     * @param request
     *            - gets the ApplicationContext from the session
     * @param type
     *            - Type of ACL to check, REQUEST,WORKFLOW, etc
     * @param acl
     *            Specific operation on the ACL, ie Read, Save, etc
     * @throws NotAuthorizedException
     */
    public static ApplicationContext checkAccess(HttpServletRequest request, String type, String acl)
            throws NotAuthorizedException {
        ApplicationContext ac = (ApplicationContext) request.getSession()
                .getAttribute(ApplicationContext.SESSION_ATTR_NAME);

        if (hasAccess(ac, type, acl) == false) {
            throw new NotAuthorizedException(type, acl);
        }
        return ac;
    }

    /**
     * Retrieves a valid ApplicationContext from the session.
     *
     * @param request the HTTP request
     * @return the ApplicationContext stored in session
     * @throws InvalidUserContext if session is missing, context is null, or user is not set
     */
    public static ApplicationContext getValidContext(HttpServletRequest request) throws InvalidUserContext {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new InvalidUserContext("NoSession");
        }

        ApplicationContext context = (ApplicationContext) session.getAttribute(ApplicationContext.SESSION_ATTR_NAME);

        if (context == null) {
            throw new InvalidUserContext("NoApplicationContext");
        }

        if (context.getCurrentUser() == null) {
            throw new InvalidUserContext("NoAuthenticatedUserFoundInContext");
        }

        return context;
    }

    /**
     * Retrieves ApplicationContext from session without throwing exception.
     *
     * @param request the HTTP request
     * @return ApplicationContext or {@code null} if not found
     */
    public static ApplicationContext getContextOrNull(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (ApplicationContext) session.getAttribute(ApplicationContext.SESSION_ATTR_NAME);
    }

    /**
     * Checks whether a valid authenticated context exists in the session.
     *
     * @param request the HTTP request
     * @return {@code true} if a valid context with authenticated user exists
     */
    public static boolean hasValidContext(HttpServletRequest request) {
        try {
            getValidContext(request);
            return true;
        } catch (InvalidUserContext e) {
            return false;
        }
    }

    /**
     * Creates a new ApplicationContext, populates it from the user's preferences,
     * and stores it in the HTTP session.
     *
     * @param user    the authenticated user
     * @param request the HTTP request
     * @return the newly created ApplicationContext
     */
    public static ApplicationContext setupSessionContext(Users user, HttpServletRequest request) throws Exception {
        ApplicationContext context = new ApplicationContext();
        context.setCurrentRole(user.getRole());
        context.setCurrentUser(user);
        Locale userLocale = user.getPreferedLocale();
        if (userLocale == null) {
            userLocale = resolveLocale(user, request);
        }
        context.setCurrentLocale(userLocale);
        String tz = user.getPreference(TIMEZONE);
        if (StringUtils.isBlank(tz) == false) {
            context.setCurrentTimezone(TimeZone.getTimeZone(tz));
        }
        String df = user.getPreference(DATE_FORMAT);
        if (df != null) {
            int i = df.indexOf(' ');
            if (i > -1) {
                df = df.substring(0, i);
            }
        }
        context.setCurrentDateFormat((df != null) ? df : DEFAULT_DATE_FORMAT);

        String tf = user.getPreference(TIME_FORMAT);
        if (StringUtils.isBlank(tf)) {
            context.setCurrentTimeFormat("hh:mm:ss a");
        }
        else{
            context.setCurrentTimeFormat(tf);
        }

        // Setup visibility
        Set altBusinessKeys = new HashSet();

        // Get specific agents
        List keys = accessControlService().getResponderBusinessEntityKeys(user, SCPlatformConstant.AGENT_BE_ACL);
        altBusinessKeys.addAll(keys);

        // And the primary
        if (AppContextHelper.getActiveBusinessEntityKey(user) != null) {
            altBusinessKeys.add(AppContextHelper.getActiveBusinessEntityKey(user));
        }
        context.setValidBusinessEntityKeys(altBusinessKeys);

        BusinessEntity enterprise = businessEntityService().getEnterpriseBusinessEntity(BusinessEntity.ENTERPRISE_TYPE);
        if (enterprise == null) {
            throw new Exception("Bootstrap must have failed, no enterprise BusinessEntity found");
        } else {
            context.setEnterpriseKey(enterprise.getBusinessEntityKey());
        }

        // Determining if Attrition rate should be displayed or not.
        List<PcmDefectType> defectTypes = getPcmDefectService().getDefectTypes();
        if (defectTypes != null && defectTypes.size() > 0) {
            context.setAttritionRateAllowed(true);
        }


        AppContextHelper.setDataFilterKeys(context,SCPlatformConstant.CATEGORY, accessControlService().getDataFilterKeys(user, SCPlatformConstant.CATEGORY));
        AppContextHelper.setDataFilterKeys(context,SCPlatformConstant.PLATFORM, accessControlService().getDataFilterKeys(user, SCPlatformConstant.PLATFORM));
        AppContextHelper.setDataFilterKeys(context,SCPlatformConstant.SITE, accessControlService().getDataFilterKeys(user, SCPlatformConstant.SITE));
        AppContextHelper.setDataFilterKeys(context,SCPlatformConstant.COSTTYPE, accessControlService().getDataFilterKeys(user, SCPlatformConstant.COSTTYPE));
        HttpSession session = request.getSession();
        session.setAttribute(ApplicationContext.SESSION_ATTR_NAME, context);
        return context;
    }

    /**
     * Removes the ApplicationContext attribute from the session.
     *
     * @param request the HTTP request
     */
    public static void clearContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(ApplicationContext.SESSION_ATTR_NAME);
        }
    }

    /**
     * Invalidates the entire HTTP session.
     *
     * @param request the HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // ======================== User info helpers ========================

    /**
     * Safe getter for current user ID.
     *
     * @param context the application context
     * @return user ID or {@code null}
     */
    public static String getUserId(ApplicationContext context) {
        return (context != null && context.getCurrentUser() != null)
                ? context.getCurrentUser().getUserId()
                : null;
    }

    /**
     * Checks whether the context holds an authenticated user.
     *
     * @param context the application context
     * @return {@code true} if a user is present
     */
    public static boolean isAuthenticated(ApplicationContext context) {
        return context != null && context.getCurrentUser() != null;
    }

    // ======================== Taglib / EL static wrappers ========================
    // These are called statically from JSP EL function libraries.
    // They delegate to AppContextService (retrieved via SpringContextHolder) so
    // the actual business logic stays inside the Spring-managed service.

    /**
     * EL helper: checks entity-level access for the current user.
     *
     * @param app        the application context (from session)
     * @param entityType the entity type key
     * @param op         the operation (e.g. "Read", "Write")
     * @return {@code true} if access is granted
     */
    public static boolean hasAccess(ApplicationContext app, String entityType, String op) {
        return appContextService().hasAccess(app, entityType, op);
    }

    /**
     * EL helper: checks entity-level access with an entity-type key.
     *
     * @param app           the application context
     * @param entityType    the entity type
     * @param op            the operation
     * @param entityTypeKey specific entity key
     * @return {@code true} if access is granted
     */
    public static boolean hasAccess(ApplicationContext app, String entityType, String op, String entityTypeKey) {
        return appContextService().hasAccess(app, entityType, op, entityTypeKey);
    }

    /**
     * EL helper: checks cost-type-aware entity access.
     * Uses pcm config to determine allowed roles for specific cost types.
     *
     * @param ac         the application context
     * @param entityType the entity type
     * @param op         the operation
     * @param costType   the cost type (e.g. "WAP", "XWAP")
     * @return {@code true} if access is granted
     */
    public static boolean hasAccessForCostType(ApplicationContext ac, String entityType, String op, String costType) {
        PcmConfigUtil configUtil = SpringContextHolder.getBean(PcmConfigUtil.class);
        List<String> accessibleCostTypes = configUtil.getList(
                "pcm.allow.costTypes.access", Arrays.asList("WAP", "XWAP"));
        if (accessibleCostTypes.contains(costType)) {
            String property = "pcm.costRecord." + costType + "." + op + ".allowedRoles";
            List<String> allowedRolesList = configUtil.getList(property, Arrays.asList("ADMIN"));
            return allowedRolesList.contains(ac.getCurrentRole().getRoleId().toUpperCase());
        }
        return appContextService().hasAccess(ac, entityType, op);
    }

    /**
     * EL helper: checks state-model event access.
     *
     * @param app         the application context
     * @param stateModel  the state model name
     * @param op          the operation/event name
     * @return {@code true} if access is granted
     */
    public static boolean hasEventAccess(ApplicationContext app, String stateModel, String op) {
        return appContextService().hasEventAccess(app, stateModel, op);
    }

    /**
     * EL helper: checks cost-type-aware state-model event access.
     *
     * @param ac         the application context
     * @param requestType the state model / request type
     * @param op         the operation
     * @param costType   the cost type (e.g. "WAP", "XWAP")
     * @return {@code true} if access is granted
     */
    public static boolean hasEventAccessForCostType(ApplicationContext ac, String requestType, String op, String costType) {
        if ("WAP".equals(costType) || "XWAP".equals(costType)) {
            PcmConfigUtil configUtil = SpringContextHolder.getBean(PcmConfigUtil.class);
            String property = "pcm.costRecord." + costType + ".Save.allowedRoles";
            List<String> allowedRolesList = configUtil.getList(property, Arrays.asList("ADMIN"));
            if (!allowedRolesList.contains(ac.getCurrentRole().getRoleId().toUpperCase())) {
                return false;
            }
        }
        return appContextService().hasEventAccess(ac, requestType, op);
    }

    /**
     * EL helper: returns the number of days old for a dashboard card record type,
     * read from user preferences.
     *
     * @param app        the application context
     * @param recordType the dashboard record type key (e.g. "COST")
     * @return number of days, defaulting to 365 if preference is absent
     */
    public static int getDayOldDashBoardCard(ApplicationContext app, String recordType) {
        return NumberUtils.toInt(
                app.getCurrentUser().getPreference("DB_STATUS_" + recordType + "_DAYSOLD"), 365);
    }

    // ======================== Private helpers ========================

    /**
     * Resolves locale from user preferences, falling back to request locale.
     */
    private static Locale getUserLocale(Users user, HttpServletRequest request) {
        Locale userLocale = user.getPreferedLocale();
        return (userLocale != null) ? userLocale : request.getLocale();
    }

    public static String getSystemProperty(String key) {
        return System.getProperty(key);
    }

    public static String getAccessableWorkflowsMenuStr(ApplicationContext applicationContext) throws IOException {
        return appContextService().getAccessableWorkflowsMenuStr(applicationContext);
    }

    /**
     * Resolves user locale from multiple sources in priority order:
     * 1. User preferences (stored in application context)
     * 2. Session attribute (if previously stored)
     * 3. Request locale (browser preference)
     *
     * @param user    the authenticated user
     * @param request the HTTP request
     * @return resolved Locale
     */
    private static Locale resolveLocale(Users user, HttpServletRequest request) {
        Locale userLocale = user.getPreferedLocale();
        if (userLocale != null) {
            return userLocale;
        }

        HttpSession session = request.getSession(false);
        if (session != null) {
            userLocale = (Locale) session.getAttribute("SPRING_SECURITY_LAST_LOCALE");
            if (userLocale != null) {
                return userLocale;
            }
        }

        return request.getLocale();
    }

    public static void setDataFilterKeys(ApplicationContext applicationContext,String dataType, Set<String> dataFilterKeys)
    {
        applicationContext.getDataFilterKeys().put(dataType,dataFilterKeys);
    }

    public static Long getActiveBusinessEntityKey(Users currentUser)
    {
        if (currentUser.getBusinessEntity() != null)
        {
            return currentUser.getBusinessEntity().getBusinessEntityKey();
        }
        return null;
    }

    /**
     * Returns true if the user is not a member of the hub company
     * or does not have a delegated admin rights
     * @return
     */
    public static boolean getHasRestrictedVisiblity(ApplicationContext applicationContext)
    {

        if (hasAccess(applicationContext,SCPlatformConstant.ADMIN_TYPE,"GlobalVisibility"))
        {
            return false;
        }
        if (getIsExternalUser(applicationContext))
        {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the current user is not a member of the owning
     * enterprise.  This value is used when determining what access
     * filters need to be enabled.  For instance supplier or mfg filters
     * should be enabled if the user is not part of the enterprise.
     * @return
     */
    public static boolean getIsExternalUser(ApplicationContext applicationContext)
    {
        return accessControlService().getIsExternalUser(applicationContext.getCurrentUser(), applicationContext.getEnterpriseKey());
    }

    /**
     * Modify the business filter for the application context
     *
     * @param ac
     * @param customerCode
     * @param params
     * @throws InvalidUserContext
     */
    public static void modifyBusinessFilter(JPAFilterUtil jpaFilterUtil, ApplicationContext ac, String customerCode, Map<String, ?> params)
            throws InvalidUserContext {
        if (getHasRestrictedVisiblity(ac)) {
            Set keys = ac.getValidBusinessEntityKeys();
            if (keys == null || keys.size() == 0) {
                throw new InvalidUserContext("User not assigned to any valid business");
            }
            jpaFilterUtil.disableFilter("businessFilter");
            jpaFilterUtil.enableFilter("businessFilter" + customerCode, params);
        }
    }

    /**
     * Setup the data level fiters based on the context passed in and the setting
     * for the filters
     *
     * @param ac
     * @throws Exception
     */
    public static void enableAccessFilter(ApplicationContext ac, JPAFilterUtil jpaFilterUtil, PcmConfigUtil pcmConfigUtil) throws InvalidUserContext {
        enableBusinessFilter(ac,jpaFilterUtil);
        enableCategoryFilter(ac,jpaFilterUtil,pcmConfigUtil);
        enablePlatformFilter(ac,jpaFilterUtil,pcmConfigUtil);
        enableSiteFilter(ac, jpaFilterUtil,pcmConfigUtil);
        enableCostTypeFilter(ac,jpaFilterUtil,pcmConfigUtil);
    }

    /**
     * Enable business filter if user has restricted visibility.  This will ensure that the user only sees data for the business entities they have access to.
     * @param ac
     * @param jpaFilterUtil
     * @throws InvalidUserContext
     */
    public static void enableBusinessFilter(ApplicationContext ac, JPAFilterUtil jpaFilterUtil) throws InvalidUserContext {
        if (AppContextHelper.getHasRestrictedVisiblity(ac)) {
            Set keys = ac.getValidBusinessEntityKeys();
            if (keys == null || keys.size() == 0) {
                throw new InvalidUserContext("User not assigned to any valid business");
            }
            jpaFilterUtil.enableFilter("businessFilter", "businessEntity", keys);
        }
    }

    /**
     * Enable category filter if enabled in config and user has data filter for category
     * @param ac
     * @param jpaFilterUtil
     * @param pcmConfigUtil
     */
    public static void enableCategoryFilter(ApplicationContext ac, JPAFilterUtil jpaFilterUtil, PcmConfigUtil pcmConfigUtil) {
        if (pcmConfigUtil.getBoolean("pcm.common.enableDataFilter.CATEGORY", false)
                && getHasDataFilter(SCPlatformConstant.CATEGORY, ac)) {
            Map<String, Object> params = new HashMap<String, Object>();
            Set<Long> filterKeys = new HashSet<Long>();
            for (String key : ac.getDataFilterKeys().get(SCPlatformConstant.CATEGORY)) {
                filterKeys.add(Long.valueOf(key));
            }
            params.put("category", filterKeys);
            params.put("user", ac.getCurrentUser().getUserKey());
            params.put("role", ac.getCurrentRole().getRoleKey());
            jpaFilterUtil.enableFilter("categoryFilter", params);
        }
    }

    /**
     * Enable platform filter if enabled in config and user has data filter for platform
     * @param ac
     * @param jpaFilterUtil
     * @param pcmConfigUtil
     */
    public static void enablePlatformFilter(ApplicationContext ac, JPAFilterUtil jpaFilterUtil, PcmConfigUtil pcmConfigUtil) {
        if (pcmConfigUtil.getBoolean("pcm.common.enableDataFilter.PLATFORM", false)
                && getHasDataFilter(SCPlatformConstant.PLATFORM, ac)) {
            Map<String, Object> params = new HashMap<String, Object>();
            Set<Long> filterKeys = new HashSet<Long>();
            for (String key : ac.getDataFilterKeys().get(SCPlatformConstant.PLATFORM)) {
                filterKeys.add(Long.valueOf(key));
            }
            params.put("platform", filterKeys);
            params.put("user", ac.getCurrentUser().getUserKey());
            params.put("role", ac.getCurrentRole().getRoleKey());
            jpaFilterUtil.enableFilter("platformFilter", params);
        }
    }

    /**
     * Enable site filter if enabled in config and user has data filter for site
     * @param ac
     * @param jpaFilterUtil
     * @param pcmConfigUtil
     */
    public static void enableSiteFilter(ApplicationContext ac, JPAFilterUtil jpaFilterUtil, PcmConfigUtil pcmConfigUtil) {
        if (pcmConfigUtil.getBoolean("pcm.common.enableDataFilter.SITE", false)
                && getHasDataFilter(SCPlatformConstant.SITE, ac)) {
            Map<String, Object> params = new HashMap<String, Object>();
            Set<Long> filterKeys = new HashSet<Long>();
            for (String key : ac.getDataFilterKeys().get(SCPlatformConstant.SITE)) {
                filterKeys.add(Long.valueOf(key));
            }
            params.put("site", filterKeys);
            params.put("user", ac.getCurrentUser().getUserKey());
            params.put("role", ac.getCurrentRole().getRoleKey());
            jpaFilterUtil.enableFilter("siteFilter", params);
        }
    }

    /**
     * Enable cost type filter if enabled in config and user has data filter for cost type
     * @param ac
     * @param jpaFilterUtil
     * @param pcmConfigUtil
     */
    public static void enableCostTypeFilter(ApplicationContext ac, JPAFilterUtil jpaFilterUtil, PcmConfigUtil pcmConfigUtil) {
        if (pcmConfigUtil.getBoolean("pcm.common.enableDataFilter.COSTTYPE", false)
                && getHasDataFilter(SCPlatformConstant.COSTTYPE, ac)) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("costType", ac.getDataFilterKeys().get(SCPlatformConstant.COSTTYPE));
            params.put("user", ac.getCurrentUser().getUserKey());
            params.put("role", ac.getCurrentRole().getRoleKey());
            jpaFilterUtil.enableFilter("costTypeFilter", params);
        }
    }

    public static boolean getHasDataFilter(String dataType, ApplicationContext applicationContext)
    {
        return accessControlService().doesUserHaveACLs(applicationContext.getCurrentUser(), dataType, READ);
    }

    private static AppContextService appContextService() {
        return SpringContextHolder.getBean(AppContextService.class);
    }

    private static BusinessEntityService businessEntityService() {
        return SpringContextHolder.getBean(BusinessEntityService.class);
    }

    private static AccessControlService accessControlService() {
        return SpringContextHolder.getBean(AccessControlService.class);
    }

    private static PcmDefectService getPcmDefectService() {
        return SpringContextHolder.getBean(PcmDefectService.class);
    }
}


