/*
 * Copyright (c) 2006 Supply Chain Platform. All Rights Reserved
 * 
 * THIS IS PROPRIETARY SOURCE CODE OF Supply Chain Platform. The copyright notice
 * above does not evidence any actual or intended publication of such source
 * code.
 * 
 * Copyright (c) 2006, by Supply Chain Platform. All rights reserved.
 */
package com.scplatform.pcm.dashboard.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.scplatform.pcm.alert.service.AlertSearchService;
import com.scplatform.pcm.authentication.dto.ApplicationContext;
import com.scplatform.pcm.authentication.service.AppContextHelper;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.dashboard.dto.DashboardForm;
import com.scplatform.pcm.dashboard.service.DashboardQueryService;
import com.scplatform.pcm.user.entity.Users;
import com.scplatform.pcm.user.service.UserService;
import com.scplatform.pcm.userAlert.entity.UserAlert;
import com.scplatform.pcm.userAlert.repository.UserAlertRepository;
import com.scplatform.pcm.util.stateMachine.StateMachine;
import com.scplatform.pcm.util.stateMachine.StateMachineFactory;
import com.scplatform.pcm.util.stateMachine.StateMachineState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

	private final static Logger logger = LogManager.getLogger(DashboardController.class);
	private final static Logger perfLogger = LogManager.getLogger("com.scplatform.pcm.PERFORMANCETRACE.Dashboard");

	private final UserService userService;
	private final PcmConfigUtil pcmConfigUtil;
	private final StateMachineFactory stateMachineFactory;
	private final UserAlertRepository userAlertRepository;
	private final DashboardQueryService dashboardQueryService;
	private final AlertSearchService alertSearchService;

	public DashboardController(UserService userService, PcmConfigUtil pcmConfigUtil,
			StateMachineFactory stateMachineFactory, UserAlertRepository userAlertRepository,
			DashboardQueryService dashboardQueryService, AlertSearchService alertSearchService) {
		this.userService = userService;
		this.pcmConfigUtil = pcmConfigUtil;
		this.stateMachineFactory = stateMachineFactory;
		this.userAlertRepository = userAlertRepository;
		this.dashboardQueryService = dashboardQueryService;
		this.alertSearchService = alertSearchService;
	}

	private List<UserAlert> getAllUserAlerts(Users user) {
		try {
			List<String> filters = new ArrayList<>();
			if (user != null) {
				if (StringUtils.isNotBlank(user.getUserId())) {
					filters.add(user.getUserId());
				}
				if (user.getRole() != null && StringUtils.isNotBlank(user.getRole().getRoleName())) {
					filters.add(user.getRole().getRoleName());
				}
			}
			if (filters.isEmpty()) {
				return userAlertRepository.findAllByOrderByAlertDateDesc();
			}
			return userAlertRepository.findByAlertFilters(filters);
		} catch (Exception e) {
			logger.warn("Unable to load user alerts", e);
			return new ArrayList<>();
		}
	}

	@GetMapping
	public String init(@ModelAttribute("dashboardForm") DashboardForm df, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ApplicationContext ac = AppContextHelper.getValidContext(request);
		Users user = ac.getCurrentUser();
		df.clearRecordStatus();
		getUserPrefresnces(user, df);
		long refresh = NumberUtils.toLong(user.getPreference("DB_REFRESH_SECS"), -1);
		if (refresh > 0) {
			long age = ((System.currentTimeMillis() - df.getLastLoaded()) / 1000);
			if (age > refresh) {
				df.setRefresh(true);
			}
		}
		Map<String, Collection<StateMachineState>> stateMachineStates = new HashMap<String, Collection<StateMachineState>>();
		Set<String> smList = stateMachineFactory.getAllStateMachineTypes();
		if (smList != null) {
			for (String smName : smList) {
				StateMachine sm = stateMachineFactory.getStateMachine(smName);
				stateMachineStates.put(smName, new ArrayList<StateMachineState>(sm.getAllStates()));
			}
			df.setAvailableStates(stateMachineStates);
		}

		// return mapping.findForward("success");
		return refresh(df, model, request, response);
	}

	public void getUserPrefresnces(Users user, DashboardForm df) {
		df.setDashboardCards(null);
		df.setInactiveDashboardCards(null);
		List<String> dashboardStaticCards = pcmConfigUtil.getList("pcm.dashboard.cards", new ArrayList<String>());
		String dashboardCards = user.getPreference("DASHBOARD_CARDS");
		if (user.getPreference("DB_STATUS_forecast_ADJ") != null)
			df.setAdjustableforecastStatPref(user.getPreference("DB_STATUS_forecast_ADJ").split(","));
		if (user.getPreference("DB_STATUS_costRecord") != null)
			df.setCostRecordStatPref(user.getPreference("DB_STATUS_costRecord").split(","));
		if (user.getPreference("DB_STATUS_bom") != null)
			df.setBomStatPref(user.getPreference("DB_STATUS_bom").split(","));
		if (user.getPreference("DB_STATUS_forecast") != null)
			df.setForecastStatPref(user.getPreference("DB_STATUS_forecast").split(","));
		if (user.getPreference("DB_STATUS_rebateProgram") != null)
			df.setRebateStatPref(user.getPreference("DB_STATUS_rebateProgram").split(","));
		if (user.getPreference("DB_STATUS_sourcingLane") != null)
			df.setSourcingLaneStatPref(user.getPreference("DB_STATUS_sourcingLane").split(","));
		df.setUserPreferences(user.getPreferences());
		if (StringUtils.isNotBlank(dashboardCards)) {
			Set<String> dashboardCard = new HashSet<String>(
					(Arrays.asList(dashboardCards.split(","))).stream().map(String::trim).collect(Collectors.toList()));
			Set<String> activeCards = dashboardStaticCards.stream().filter(m -> dashboardCard.contains(m))
					.collect(Collectors.toSet());
			dashboardStaticCards.removeAll(activeCards);
			df.setInactiveDashboardCards(new HashSet<>(dashboardStaticCards));
			df.setDashboardCards(activeCards);
		} else {
			df.setInactiveDashboardCards(null);
			df.setDashboardCards(new HashSet<String>(dashboardStaticCards));
		}

		df.setUserAlerts(getAllUserAlerts(user));
		df.setReviwAlert(getalerts(user));
		String dashboardLayout = "col-1:costRecord,col-1:sourcingLane,col-1:forecast,col-1:forecast_ADJ,col-2:rebateProgram,col-2:bom";
		df.setDashboardLayout(dashboardLayout);
	}

	@PostMapping("/saveLayout")
	@Transactional
	public String saveLayout(@ModelAttribute("dashboardForm") DashboardForm df, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		String layoutString = df.getDashboardLayout();
		if (StringUtils.isNotBlank(layoutString)) {
			ApplicationContext ac = AppContextHelper.getValidContext(request);
			Users user = ac.getCurrentUser();
			if (layoutString.equals(user.getPreference("DB_LAYOUT")) == false) {
				user.setPreference("DB_LAYOUT", layoutString);
				userService.saveOrUpdate(user);
			}
		}
		return "dashboard";
	}

	@PostMapping("/editCards")
	@Transactional
	public String editCards(@ModelAttribute("dashboardForm") DashboardForm df, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ApplicationContext ac = AppContextHelper.getValidContext(request);
		Users user = userService.getUser(ac.getCurrentUser().getUserKey());
		String dashboardCards = user.getPreference("DASHBOARD_CARDS");
		String dCards = String.join(",", df.getDashboardCard());
		Set<String> removedCard = new HashSet<String>(new HashSet<String>(
				(Arrays.asList(dCards.split(","))).stream().map(String::trim).collect(Collectors.toList())));
		removedCard.addAll(new HashSet<>(
				(Arrays.asList(dashboardCards.split(","))).stream().map(String::trim).collect(Collectors.toList())));
		user.setPreference("DASHBOARD_CARDS", String.join(",", removedCard));
		userService.saveOrUpdate(user);
		ac.setCurrentUser(user);
		getUserPrefresnces(user, df);
		return "dashboard";
	}

	@PostMapping("/saveCards")
	@Transactional
	public String saveCards(@ModelAttribute("dashboardForm") DashboardForm df, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ApplicationContext ac = AppContextHelper.getValidContext(request);
		Users user = userService.getUser(ac.getCurrentUser().getUserKey());
		String dashboardCards = user.getPreference("DASHBOARD_CARDS");
		List<String> dashboardStaticCards = pcmConfigUtil.getList("pcm.dashboard.cards", new ArrayList<String>());
		String dCards = String.join(",", df.getDashboardCard());
		Set<String> removedCard = new HashSet<String>(new HashSet<String>(
				(Arrays.asList(dCards.split(","))).stream().map(String::trim).collect(Collectors.toList())));
		if (dashboardCards == null) {
			if (removedCard != null && removedCard.size() > 0) {
				Set<String> dashboardActiveCard = new HashSet<String>(
						dashboardStaticCards.stream().map(String::trim).collect(Collectors.toList()));
				Set<String> activeCards = removedCard.stream().filter(m -> dashboardActiveCard.contains(m.trim()))
						.collect(Collectors.toSet());
				dashboardActiveCard.removeAll(activeCards);
				user.setPreference("DASHBOARD_CARDS", String.join(",", dashboardActiveCard));
				df.setInactiveDashboardCards(activeCards);
				df.setDashboardCards(dashboardActiveCard);
			}
		} else {
			if (removedCard != null && removedCard.size() > 0) {
				Set<String> dashboardActiveCard = new HashSet<>(Arrays.asList(dashboardCards.split(",")));
				Set<String> activeCards = removedCard.stream().filter(m -> dashboardActiveCard.contains(m.trim()))
						.collect(Collectors.toSet());
				dashboardActiveCard.removeAll(activeCards);
				user.setPreference("DASHBOARD_CARDS", String.join(",", dashboardActiveCard));
			}
		}
		userService.saveOrUpdate(user);
		ac.setCurrentUser(user);
		getUserPrefresnces(user, df);
		return "dashboard";
	}

	@PostMapping("/saveCardsStatus")
	@Transactional
	public String saveCardsStatus(@ModelAttribute("dashboardForm") DashboardForm df, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		ApplicationContext ac = AppContextHelper.getValidContext(request);
		Users user = ac.getCurrentUser();
		Iterator itr = df.getUserPreferences().entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry entry = (Map.Entry) itr.next();
			user.setPreference((String) entry.getKey(), (String) entry.getValue());
		}
		user.setPreference(df.getCardType(), df.getCardsPreferences());
		userService.saveOrUpdate(user);
		getUserPrefresnces(user, df);
		return refresh(df, model, request, response);
	}

	@PostMapping("/refresh")
	public String refresh(@ModelAttribute("dashboardForm") DashboardForm df, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		df.clearRecordStatus();
		ApplicationContext ac = AppContextHelper.getValidContext(request);
		Users user = ac.getCurrentUser();

		// saveLayout(mapping, form, request, response);

		StopWatch timer = new StopWatch();
		// AppContextHelper.enableAccessFilter(ac);
		df.setUserAlerts(getAllUserAlerts(user));

		Map<String, String> errors = new HashMap<String, String>();

		try {
			if (AppContextHelper.hasAccess(ac, "COST_RECORD", "Read")) {
				loadStatus("costRecord", user, df);
			}
		} catch (Exception e) {
			logger.warn("Unable to execute dashboard queries", e);
			errors.put("costRecord", "errors.dashboard.alert: Cost Records");
		}

		try {
			if (AppContextHelper.hasAccess(ac, "SOURCING_LANE", "Read")) {
				loadStatus("sourcingLane", user, df);
			}
		} catch (Exception e) {
			logger.warn("Unable to execute dashboard queries", e);
			errors.put("sourcingLane", "errors.dashboard.alert: Sourcing Records");
		}

		try {
			if (AppContextHelper.hasAccess(ac, "REBATE", "Read")) {
				loadStatus("rebateProgram", user, df);
			}
		} catch (Exception e) {
			logger.warn("Unable to execute dashboard queries", e);
			errors.put("rebateProgram", "errors.dashboard.alert: Rebate Records");
		}

		try {
			if (AppContextHelper.hasAccess(ac, "BOM", "Read")) {
				loadStatus("bom", user, df);
			}
		} catch (Exception e) {
			logger.warn("Unable to execute dashboard queries", e);
			errors.put("bom", "errors.dashboard.alert: Rebate Records");
		}

		try {
			if (AppContextHelper.hasAccess(ac, "FORECAST", "Read")) {
				loadStatus("forecast", user, df);
			}
		} catch (Exception e) {
			logger.warn("Unable to execute dashboard queries", e);
			errors.put("forecast", "errors.dashboard.alert: Forecast");
		}

		try {
			if (AppContextHelper.hasAccess(ac, "FORECAST", "Read")) {
				loadStatus("forecast_ADJ", user, df);
			}
		} catch (Exception e) {
			logger.warn("Unable to execute dashboard queries", e);
			errors.put("forecast_ADJ", "errors.dashboard.alert: Forecast_ADJ");
		}

		try {
			String age = user.getPreference("DB_ITEMS_DAYSOLD");
			if (age != null) {
				df.setNewItemAge(Integer.parseInt(age));
			}

			if (perfLogger.isDebugEnabled()) {
				timer.reset();
				timer.start();
			}
			df.setNewItemStatus(dashboardQueryService.getNewUnassignedItems(df.getNewItemAge()));
			if (perfLogger.isDebugEnabled()) {
				timer.stop();
				perfLogger.debug("Executed in: " + timer);
			}

		} catch (Throwable e) {
			logger.warn("Unable to execute dashboard queries (newUnassignedItems)", e);
			errors.put("unassignedItems", "errors.dashboard.alert: Unassigned Items");
		}
		model.addAttribute("errors", errors);

		df.setLastLoaded(System.currentTimeMillis());
		df.setReviwAlert(getalerts(user));
		Map<String, Collection<StateMachineState>> stateMachineStates = new HashMap<String, Collection<StateMachineState>>();
		Set<String> smList = stateMachineFactory.getAllStateMachineTypes();
		if (smList != null) {
			for (String smName : smList) {
				StateMachine sm = stateMachineFactory.getStateMachine(smName);
				stateMachineStates.put(smName, new ArrayList<StateMachineState>(sm.getAllStates()));
			}
			df.setAvailableStates(stateMachineStates);
		}
		getUserPrefresnces(user, df);
		model.addAttribute("dashboardForm", df);
		return "dashboard";
	}

	protected void loadStatus(String recordType, Users user, DashboardForm df) throws Exception {
		StopWatch timer = new StopWatch();
		try {
			String[] stats = user.getPreferenceAsArray("DB_STATUS_" + recordType);
			boolean ownerOnly = BooleanUtils.toBoolean(user.getPreference("DB_STATUS_" + recordType + "_OWNER_ONLY"));
			int daysOld = NumberUtils.toInt(user.getPreference("DB_STATUS_" + recordType + "_DAYSOLD"), 365);

			if (perfLogger.isDebugEnabled()) {
				timer.reset();
				timer.start();
			}
			List<?> list = dashboardQueryService.getStatusCounts(
					recordType, ownerOnly, stats, daysOld, user.getUserKey(), user.getUserId());
			df.setRecordStatus(recordType, list, ownerOnly, daysOld);
			if (perfLogger.isDebugEnabled()) {
				timer.stop();
				perfLogger.debug("Executed in: " + timer);
			}

		} catch (Throwable e) {
			throw new Exception("Unable to execute dashboard queries", e);
		}
	}

	/**
	 * Returns the {@code alertType -> count} map used by the dashboard's
	 * "Review Alerts" tile (e.g. {@code ItemAssignment}, {@code ItemUnassignment},
	 * {@code CostChange}, etc.).
	 */
	public Map<String, Integer> getalerts(Users user) {
		if (user == null || user.getUserId() == null) {
			Map<String, Integer> empty = new HashMap<>();
			empty.put("", 1);
			return empty;
		}
		try {
			return alertSearchService.getReviewAlertCounts(user.getUserId());
		} catch (Exception e) {
			logger.warn("Unable to load review alert counts for user '{}'", user.getUserId(), e);
			Map<String, Integer> fallback = new HashMap<>();
			fallback.put("", 1);
			return fallback;
		}
	}
}


