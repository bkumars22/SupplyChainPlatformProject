/*
 * Copyright (c) 2026 Supply Chain Platform. All Rights Reserved
 */
package com.scplatform.pcm.user.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.ums.dto.UMSUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.entity.UserDelegate;
import com.scplatform.pcm.user.repository.UsersRepository;
import com.scplatform.pcm.user.repository.UserDelegateRepository;
import com.scplatform.pcm.user.dto.UserProfileForm;
import com.scplatform.pcm.user.util.I18NUtils;
import com.scplatform.pcm.util.stateMachine.StateMachine;
import com.scplatform.pcm.util.stateMachine.StateMachineFactory;
import com.scplatform.pcm.util.stateMachine.StateMachineState;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import static com.scplatform.pcm.util.common.SCPlatformConstant.*;


@Service
@RequiredArgsConstructor
@Log4j2
public class UserService {

	private final UsersRepository usersRepository;
	private final UserDelegateRepository userDelegateRepository;
	private final StateMachineFactory stateMachineFactory;
	private final PcmConfigUtil pcmConfigUtil;

	/**
	 * Find users for a business entity with optional search and active filters.
	 * Maps to: UMSUtil.findUsersForBusinessByKey(Long, String, Boolean, MultiValueMap)
	 *
	 * @param businessEntityKey the business entity key
	 * @param searchText optional search text for userName, userId, or emailAddress
	 * @param activeOnly optional filter to show only enabled users
	 * @param params optional parameters containing offset and limit for pagination
	 * @return Map containing "count" (total results) and "resultList" (paginated results)
	 */
	public Map<String, Object> findUsersForBusinessByKey(Long businessEntityKey, String searchText,
			Boolean activeOnly, MultiValueMap<String, String> params) {
		
		// Query users by business entity key and optional search
		List<Users> users = usersRepository.findUsersByBusinessEntityKeyAndEnabledAndSearchText(businessEntityKey,true, searchText);
		
		// Get count before pagination
		int count = users.size();
		
		// Apply pagination if params are provided
		int offset = 0;
		int limit = users.size();
		
		if (params != null) {
			if (params.getFirst("offset") != null) {
				try {
					offset = Integer.valueOf(params.getFirst("offset"));
				} catch (NumberFormatException e) {
					offset = 0;
				}
			}
			if (params.getFirst("limit") != null) {
				try {
					limit = Integer.valueOf(params.getFirst("limit"));
				} catch (NumberFormatException e) {
					limit = users.size();
				}
			}
		}
		
		// Apply pagination manually
		int endIndex = Math.min(offset + limit, users.size());
		if (offset >= users.size()) {
			users = Collections.emptyList();
		} else {
			users = users.subList(offset, endIndex);
		}
		
		Map<String, Object> results = new HashMap<>();
		results.put("count", count);
		results.put("resultList", users);
		
		return results;
	}

	/**
	 * Find a user by userId (login ID).
	 *
	 * @param userId the user's login ID
	 * @return the user if found, null otherwise
	 */
	public Users findByUserId(String userId) {
		return usersRepository.findByUserId(userId).orElse(null);
	}

	/**
	 * Get a user by their primary key.
	 *
	 * @param userKey the user's primary key
	 * @return the user if found, null otherwise
	 */
	public Users getUser(Long userKey) {
		return usersRepository.findById(userKey).orElse(null);
	}

	/**
	 * Save or update a user.
	 *
	 * @param user the user to save
	 * @return the saved user
	 */
	public Users saveUser(Users user) {
		return usersRepository.save(user);
	}

	/**
	 * Save or update a user with validation.
	 * If user has a userKey (primary key), it will be updated; otherwise, a new user will be created.
	 *
	 * @param user the user to save or update
	 * @throws IllegalArgumentException if user or userId is null/blank
	 */
	@Transactional
	public void saveOrUpdate(Users user) {
		if (user == null) {
			log.error("Cannot save or update a null user");
			throw new IllegalArgumentException("User cannot be null");
		}
		if (StringUtils.isBlank(user.getUserId())) {
			log.error("Cannot save or update user with blank userId");
			throw new IllegalArgumentException("User ID cannot be null or blank");
		}

		if (user.getUserKey() != null) {
			log.info("Updating user with userKey: {}, userId: {}", user.getUserKey(), user.getUserId());
		} else {
			log.info("Creating new user with userId: {}", user.getUserId());
		}

		usersRepository.save(user);
	}

	/**
	 * Find all enabled users.
	 *
	 * @return list of enabled users
	 */
	public List<Users> findAllEnabledUsers() {
		return usersRepository.findByIsEnabledTrue();
	}

	/**
	 * Find users by business entity key.
	 *
	 * @param businessEntityKey the business entity key
	 * @return list of users for the business entity
	 */
	public List<Users> findUsersByBusinessEntity(Long businessEntityKey) {
		return usersRepository.findByBusinessEntityKey(businessEntityKey);
	}

	/**
	 * Find enabled users by business entity key.
	 *
	 * @param businessEntityKey the business entity key
	 * @return list of enabled users for the business entity
	 */
	public List<Users> findEnabledUsersByBusinessEntity(Long businessEntityKey) {
		return usersRepository.findEnabledUsersByBusinessEntity(businessEntityKey);
	}

    public static void mapUserEntityWithUMSUser(String userName, UMSUser umsUser, Users user, PcmConfigUtil pcmConfigUtil) {
        // set the email
        if (StringUtils.isNotBlank(umsUser.getEmail())) {
            user.setEmailAddress(umsUser.getEmail());
        }

        // set the user name
        if (StringUtils.isNotBlank(umsUser.getFirstName()) || StringUtils.isNotBlank(umsUser.getLastName())) {
            StringBuilder userFullName = new StringBuilder();
            if (StringUtils.trimToNull(umsUser.getFirstName()) != null) {
                userFullName.append(umsUser.getFirstName());
            }
            if (StringUtils.trimToNull(umsUser.getLastName()) != null) {
                if (userFullName.length() > 0) {
                    userFullName.append(" ");
                }
                userFullName.append(umsUser.getLastName());
            }
            if (userFullName.length() > 0) {
                user.setUserName(userFullName.toString());
            }
        }

        // set the user preferences

        // set the page size
        String overridePageSize = pcmConfigUtil.getString("pcm.user.provision.override.pageSize", null);
        String defaultPageSize = pcmConfigUtil.getString("pcm.user.provision.default.pageSize", null);
        if (StringUtils.isNotBlank(overridePageSize)) {
            user.setDefaultPageSize(Integer.parseInt(overridePageSize));
            log.info("setting page size to override " + overridePageSize + " for userId " + userName);
        } else if (StringUtils.isNotBlank(umsUser.getPreferredPagination())) {
            user.setDefaultPageSize(Integer.parseInt(umsUser.getPreferredPagination()));
            log.info("setting page size to ums specified " + umsUser.getPreferredPagination() + " for userId "
                    + userName);
        } else if (StringUtils.isNotBlank(defaultPageSize)) {
            user.setDefaultPageSize(Integer.parseInt(defaultPageSize));
            log.info("setting page size to default " + defaultPageSize + " for userId " + userName);
        }

        // set the timezone
        String overrideTimeZone = pcmConfigUtil.getString("pcm.user.provision.override.timeZone", null);
        String defaultTimeZone = pcmConfigUtil.getString("pcm.user.provision.default.timeZone", null);
        if (StringUtils.isNotBlank(overrideTimeZone)) {
            user.setPreference(TIMEZONE, overrideTimeZone);
            log.info("setting timezone to override " + overrideTimeZone + " for userId " + userName);
        } else if (StringUtils.isNotBlank(umsUser.getPreferredTimezone())) {
            user.setPreference(TIMEZONE, umsUser.getPreferredTimezone());
            log.info("setting timezone to ums specified " + umsUser.getPreferredTimezone() + " for userId "
                    + userName);
        } else if (StringUtils.isNotBlank(defaultTimeZone)) {
            user.setPreference(TIMEZONE, defaultTimeZone);
            log.info("setting timezone to default " + defaultTimeZone + " for userId " + userName);
        }

        // set the date and time format
        String overrideDateFormat = pcmConfigUtil.getString("pcm.user.provision.override.dateFormat", null);
        String overrideTimeFormat = pcmConfigUtil.getString("pcm.user.provision.override.timeFormat", null);
        String defaultDateFormat = pcmConfigUtil.getString("pcm.user.provision.default.dateFormat", null);
        String defaultTimeFormat = pcmConfigUtil.getString("pcm.user.provision.default.timeFormat", null);
        String dateFormat = "";
        String timeFormat = "";
        if (StringUtils.isNotBlank(umsUser.getPreferredLocale())) {
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG,
                    new Locale(umsUser.getPreferredLocale()));
            String pattern = ((SimpleDateFormat) df).toPattern();
            String[] parts = pattern.split(",", 2);
            if (parts != null && parts.length == 2) {
                dateFormat = parts[0];
                timeFormat = parts[1];
                if(dateFormat != null && !dateFormat.isEmpty()) {
                    dateFormat = dateFormat.trim();
                }
                if(timeFormat != null && !timeFormat.isEmpty()) {
                    timeFormat = timeFormat.trim();
                }
            }
        }

        if (StringUtils.isNotBlank(overrideDateFormat)) {
            user.setPreference(DATE_FORMAT, overrideDateFormat);
            log.info("setting date format to override " + overrideDateFormat + " for userId " + userName);
        } else if (StringUtils.isNotBlank(dateFormat)) {
            user.setPreference(DATE_FORMAT, dateFormat);
            log.info("setting date format to ums specified " + dateFormat + " for locale "
                    + umsUser.getPreferredLocale() + " for userId " + userName);
        } else if (StringUtils.isNotBlank(defaultDateFormat)) {
            user.setPreference(DATE_FORMAT, defaultDateFormat);
            log.info("setting date format to default " + defaultDateFormat + " for userId " + userName);
        }

        if (StringUtils.isNotBlank(overrideTimeFormat)) {
            user.setPreference(TIME_FORMAT, overrideTimeFormat);
            log.info("setting time format to override " + overrideTimeFormat + " for userId " + userName);
        } else if (StringUtils.isNotBlank(timeFormat)) {
            user.setPreference(TIME_FORMAT, timeFormat);
            log.info("setting time format to ums specified " + timeFormat + " for locale "
                    + umsUser.getPreferredLocale() + " for userId " + userName);
        } else if (StringUtils.isNotBlank(defaultTimeFormat)) {
            user.setPreference(TIME_FORMAT, defaultTimeFormat);
            log.info("setting time format to default " + defaultTimeFormat + " for userId " + userName);
        }
    }

    /**
     * Populate UserProfileForm from Users entity.
     * 
     * @param upf the UserProfileForm to populate
     * @param user the Users entity
     * @throws Exception if an error occurs
     */
    public void populateUserProfileForm(UserProfileForm upf, Users user, Locale currentLocale) throws Exception {
        if (user == null) {
            return;
        }

        // Populate state machines
        Map<String, Collection<StateMachineState>> stateMachineStates = new HashMap<>();
        Set<String> smList = stateMachineFactory.getAllStateMachineTypes();
        if (smList != null) {
            for (String smName : smList) {
                StateMachine sm = stateMachineFactory.getStateMachine(smName);
                if (sm != null) {
                    stateMachineStates.put(smName, new ArrayList<>(sm.getAllStates()));
                }
            }
        }
        upf.setAvailableStates(stateMachineStates);

        // Populate available languages
        List<String> availLangs = pcmConfigUtil.getList("pcm.user.language", new ArrayList<>());
        upf.clearAvailableLanguages();
        for (String langCode : availLangs) {
            Locale locale = I18NUtils.findLocale(langCode);
            if (locale != null) {
                upf.addAvailableLanguage(new I18NUtils.LocaleLabel(
                        locale.getDisplayLanguage(currentLocale),
                        locale.getLanguage()));
            } else {
                log.error("Invalid language specified in pcm.user.language:{}", langCode);
            }
        }

        // Set user delegates
        upf.setDelegates(findDelegatesForUser(user));
        upf.setWhereDelegateFor(findDelegatorOfUser(user.getUserId()));

        // Set user information
        upf.setUser(user);

        // Set locale information
        if (user.getPreferedLocale() != null) {
            upf.setLocaleKey(user.getPreferedLocale().toString());
            upf.setLanguage(user.getPreferedLocale().getLanguage());
            upf.setCountry(user.getPreferedLocale().getCountry());
        }

        // Set user key and preferences
        upf.setCurrentUserKey(String.valueOf(user.getUserKey()));
        upf.setUserPreferences(user.getPreferences());

        // Set user role
        upf.setRole(user.getRole());
    }

    /**
     * Find all user delegates for a given user.
     * 
     * @param user the Users entity whose delegates to find
     * @return list of UserDelegate records where user is the delegator
     */
    public List<UserDelegate> findDelegatesForUser(Users user) {
        if (user == null) {
            return Collections.emptyList();
        }
        return userDelegateRepository.findByDelegator(user);
    }

    /**
     * Find all users who have delegated to a specific user ID.
     * 
     * @param delegateUserId the delegate user ID
     * @return list of UserDelegate records where delegateUserId matches
     */
    public List<UserDelegate> findDelegatorOfUser(String delegateUserId) {
        if (StringUtils.isBlank(delegateUserId)) {
            return Collections.emptyList();
        }
        return userDelegateRepository.findByDelegateUserId(delegateUserId);
    }

    /**
     * Saves user profile preferences and locale for the current user context,
     * then repopulates the form from the managed user entity.
     *
     * @param upf user profile form from request (nullable)
     * @param appContext current application context
     * @return populated form when successful, null if no current managed user is found
     * @throws Exception when save or form population fails
     */
    public UserProfileForm saveUserProfile(UserProfileForm upf, ApplicationContext appContext) throws Exception {
        if (upf == null) {
            upf = new UserProfileForm();
        }

        if (appContext == null || appContext.getCurrentUser() == null) {
            return null;
        }

        Users user = getUser(appContext.getCurrentUser().getUserKey());
        if (user == null) {
            return null;
        }

        if (upf.getUserPreferences() != null) {
            for (Object entryObj : upf.getUserPreferences().entrySet()) {
                if (entryObj instanceof Map.Entry<?, ?> entry) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        user.setPreference(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                    }
                }
            }
        }

        String lang = upf.getLanguage();
        String country = upf.getCountry();
        if ((country == null || country.isBlank()) && user.getPreferedLocale() != null) {
            country = user.getPreferedLocale().getCountry();
        }
        if (lang != null && !lang.isBlank()) {
            user.setPreferedLocale(lang, country);
        }

        saveUser(user);
        populateUserProfileForm(upf, user, appContext.getCurrentLocale());
        return upf;
    }
}

