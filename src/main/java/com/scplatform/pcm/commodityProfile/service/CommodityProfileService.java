/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.commodityProfile.service;

import com.scplatform.pcm.businessEntity.service.BusinessEntityService;
import com.scplatform.pcm.commodityProfile.repository.CommodityProfileRepository;
import com.scplatform.pcm.commodityProfile.repository.RoleCommodityProfileMappingRepository;
import com.scplatform.pcm.config.util.PcmConfigUtil;
import com.scplatform.pcm.cost.entity.PcmCostType;
import com.scplatform.pcm.cost.service.PcmCostTypeService;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.user.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CommodityProfileService {

    private final PcmConfigUtil pcmConfigUtil;
    private final CommodityProfileRepository commodityProfileRepository;
    private final PcmCostTypeService pcmCostTypeService;
    private final BusinessEntityService businessEntityService;
    private final ItemService itemService;
    private final RoleCommodityProfileMappingRepository roleCommodityProfileMappingRepository;

	private static boolean isNull;
	private static boolean isAll;
	private static List<String> allCompanyItemType = null;
	private static List<String> nullCompanyItemType = null;

	private static final String FORECAST_ELEMENT = "Forecast";
	private static final String COST_RECORD_ELEMENT = "SourcingLane";
	private static final String COST_RECORD_COST_TYPE_ELEMENT = "CostRecord";
	private static final String COST_TYPE_ATTRIBUTE = "costType";
	private static final String ITEM_IDENTIFIER_ATTRIBUTE = "itemIdentifier";
	private static final String PARENT_NAME_ATTRIBUTE = "parentName";
	private static final String FG_NAME_ATTRIBUTE = "functionalGroupName";

	private static final String UPLOAD_COST_RECORDS = "CostRecordUI";
	private static final String UPLOAD_COST_RECORDS_ACTIONS = "CostRecordActionUI";
	private static final String UPLOAD_EXPIRE_COST_RECORDS = "CostRecordExpireUI";
	private static final String UPLOAD_CURRENT_FORECAST_RECORDS = "CurrentForecastUI";
	private static final String UPLOAD_ADJUSTABLE_FORECAST_RECORDS = "AdjustableForecastUI";
	private static final String UPLOAD_MASS_UPDATE_COST_RECORD_FG = "MassUpdateCostRecordByFGUI";
	private static final String UPLOAD_MASS_UPDATE_COST_RECORD_PFG = "MassUpdateCostRecordByParentFGUI";
	private static final String UPLOAD_MASS_UPDATE_COST_FORECAST_FG = "MassUpdateCostForecastByFGUI";
	private static final String UPLOAD_MASS_UPDATE_COST_FORECAST_PFG = "MassUpdateCostForecastByParentFGUI";

	public Set<String> getCommoditytProfileFilterList() {
		return new HashSet<>(pcmConfigUtil.getList("pcm.commodityProfile.filter.list", Arrays.asList("")));
	}

	private String getCompanyItemTypeMapping() {
		return pcmConfigUtil.getString("pcm.commodityProfile.companyItemType.field", "DATA_SOURCE");
	}

	private boolean isRoleMapping() {
		String mappingType = pcmConfigUtil.getString("pcm.commodityProfile.user.mapping.type", "user");
		return "role".equalsIgnoreCase(mappingType);
	}

	public String getQuery(String searchReportType, Users user) {
		boolean isRoleMapping = isRoleMapping();
		setCompanyItemType(searchReportType, user,isRoleMapping);
		if (searchReportType.equalsIgnoreCase("SearchDefCostRecord")) {
			return getFinalQueryForCostRecord(searchReportType, user, isRoleMapping);
		} else if (searchReportType.equalsIgnoreCase("SearchDefPriceTAM")) {
			return getFinalQueryForPriceTAMSQL(searchReportType, user, isRoleMapping);
		} else if (searchReportType.equalsIgnoreCase("ExceptionCostRecordUpload")) {
			return getFinalQueryForExceptionCostRecordUpload(searchReportType, user, isRoleMapping);
		} else if (searchReportType.equalsIgnoreCase("SearchDefXLOBDeleteDownload")) {
			return getFinalQueryForXLOBDeleteTAMSQL(searchReportType, user, isRoleMapping);
		} else {
			return getFinalQueryForOtherWith(searchReportType, user, isRoleMapping);
		}
	}

	public String getCommodityFilterSQLQuery(String searchReportType, Users users) {
		boolean isRoleMapping = isRoleMapping();
		setCompanyItemType(searchReportType, users, isRoleMapping);
		if (searchReportType.equalsIgnoreCase("SearchDefRebateProgram")
				|| searchReportType.equalsIgnoreCase("newRebateProgram")) {
			return getFinalQueryForRebate(searchReportType, users, isRoleMapping);
		} else {
			return null;
		}
	}
	private static String getBussinessObjectColumnName(String searchReportType) {
		if (searchReportType.equalsIgnoreCase("SearchDefRebateProgram")
				|| searchReportType.equalsIgnoreCase("newRebateProgram")) {
			return "includeExcludeRebate";
		} else if (searchReportType.equalsIgnoreCase("SearchDefForecast")) {
			return "includeExcludeCostForecast";
		} else if (searchReportType.equalsIgnoreCase("SearchDefItemOnly")
				|| searchReportType.equalsIgnoreCase("SearchDefItem")) {
			return "includeExcludeItem";
		} else if (searchReportType.equalsIgnoreCase("SearchDefBom")
				|| searchReportType.equalsIgnoreCase("SearchDefBomCostRollup")) {
			return "includeExcludeBOM";
		} else if (searchReportType.equalsIgnoreCase("SearchDefSourcingLaneItem")
				|| searchReportType.equalsIgnoreCase("SearchDefSourcingLane")
				|| searchReportType.equalsIgnoreCase("SearchDefSupplyAllocationItem")
				|| searchReportType.equalsIgnoreCase("SearchDefSupplyAllocation")) {
			return "includeExcludeItem";
		} else if (searchReportType.equalsIgnoreCase("SearchDefForecastItem")) {
			return "includeExcludeCostForecast";
		} else if (searchReportType.equalsIgnoreCase("TAMSearch")) {
			return "includeExcludeTAM";
		} else {
			return null;
		}
	}

	private static String getBussinessObjectColumnNameForSQL(String searchReportType) {
		if (searchReportType.equalsIgnoreCase("SearchDefRebateProgram")
				|| searchReportType.equalsIgnoreCase("newRebateProgram")) {
			return "INCLUDE_EXCLUDE_REBATE";
		} else if (searchReportType.equalsIgnoreCase("SearchDefForecast")
				|| searchReportType.equalsIgnoreCase(UPLOAD_CURRENT_FORECAST_RECORDS)
				|| searchReportType.equalsIgnoreCase(UPLOAD_ADJUSTABLE_FORECAST_RECORDS)) {
			return "INCLUDE_EXCLUDE_COST_FORECAST";
		} else if (searchReportType.equalsIgnoreCase("SearchDefItemOnly")
				|| searchReportType.equalsIgnoreCase("SearchDefItem")) {
			return "INCLUDE_EXCLUDE_ITEM";
		} else if (searchReportType.equalsIgnoreCase("SearchDefBom")
				|| searchReportType.equalsIgnoreCase("SearchDefBomCostRollup")) {
			return "INCLUDE_EXCLUDE_BOM";
		} else if (searchReportType.equalsIgnoreCase("SearchDefSourcingLaneItem")
				|| searchReportType.equalsIgnoreCase("SearchDefSourcingLane")
				|| searchReportType.equalsIgnoreCase("SearchDefSupplyAllocationItem")
				|| searchReportType.equalsIgnoreCase("SearchDefSupplyAllocation")) {
			return "INCLUDE_EXCLUDE_ITEM";
		} else if (searchReportType.equalsIgnoreCase("SearchDefForecastItem")) {
			return "INCLUDE_EXCLUDE_COST_FORECAST";
		} else if (searchReportType.equalsIgnoreCase("TAMSearch")) {
			return "INCLUDE_EXCLUDE_TAM";
		} else if (searchReportType.equalsIgnoreCase("SearchDefPriceTAM")) {
			return "INCLUDE_EXCLUDE_PRICE_TAM";
		}else if (searchReportType.equalsIgnoreCase("SearchDefXLOBDeleteDownload")) {
			return "INCLUDE_EXCLUDE_PRICE_TAM";
		} else {
			return null;
		}
	}

	private void updateData(String query) {
		allCompanyItemType = new ArrayList<>();
		nullCompanyItemType = new ArrayList<>();
		isNull = false;
		isAll = false;
		List<Object[]> result = commodityProfileRepository.executeNativeSql(query,null);
		for (Object[] rs : result) {
			if (rs[0] == null) {
				isNull = true;
				nullCompanyItemType.add((String) rs[1]);
			} else if (rs[0].equals(new BigDecimal(-1))) {
				isAll = true;
				allCompanyItemType.add((String) rs[1]);
			}
		}
	}

	private String getFinalQueryForCostRecord(String searchReportType, Users user, boolean isRoleMapping) {
		String mappingField = getCompanyItemTypeMapping().trim();
		if (mappingField.contains("Attribute") || mappingField.contains("dataSource")) {
			mappingField = "im." + mappingField;
		} else if (mappingField.contains("businessEntity")) {
			mappingField = "im.businessEntity.businessEntityName";
		}
		StringBuilder finalQuery = new StringBuilder(" and ");

		String keyMapping = isRoleMapping ? " r.roleKey = " + user.getRole().getRoleKey() + " and rcpm.businessEntity.businessEntityKey = "
				+ (user.getBusinessEntity() == null ? -1 : user.getBusinessEntity().getBusinessEntityKey()) : " us.userKey = " + user.getUserKey();

		String profileJoin = isRoleMapping ? " Role r left join r.roleProfileMapping rcpm left join rcpm.commodityProfile cp "
				: " Users us left join us.userProfileMapping cp ";

		if (isAll) {
			finalQuery.append(" (cat is not null or (cat is null and ");
			finalQuery.append(mappingField);
			finalQuery.append(" not in (");
			finalQuery.append(getCommaSeparatedString(allCompanyItemType));
			finalQuery.append(") ");
			finalQuery.append(" )) and (not exists ");
			finalQuery.append(" (select cp.companyItemType from ");
			finalQuery.append(profileJoin);
			finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
			finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
			finalQuery.append(keyMapping);
			finalQuery.append(" and cp.companyItemType = ");
			finalQuery.append(mappingField);
			finalQuery.append(" and ((ic.categoryKey = -1 ");
			finalQuery.append(" and ct.costType = cr.costType ) ");
			finalQuery.append(" or (ct.costType = cr.costType and ic.categoryKey = cat.categoryKey))) ");
			finalQuery.append("and not exists ");
			if(isRoleMapping){
				finalQuery.append(" (select cp.companyItemType from Role r left join r.roleProfileMapping rcpm left join rcpm.commodityProfile cp ");
				finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
			}else{
				finalQuery.append(" (select cp.companyItemType from Users us ");
				finalQuery.append(" left join us.userProfileMapping cp left join cp.itemCategory ic left join cp.costTypes ct ");
			}
			finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
			finalQuery.append(keyMapping);
			finalQuery.append(" and ic.categoryKey != -1 and cp.companyItemType = ");
			finalQuery.append(mappingField);
			finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) )");
		} else {
			if (isNull) {
				finalQuery.append(" not exists (select cp.companyItemType from ");
				finalQuery.append(profileJoin);
				finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
				finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
				finalQuery.append(keyMapping);
				finalQuery.append(" and ( ");
				finalQuery.append(" (cp.itemCategory is null and cp.companyItemType = ");
				finalQuery.append(mappingField);
				finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) or ");
				finalQuery.append(" (ic.categoryKey != -1 and cp.companyItemType = ");
				finalQuery.append(mappingField);
				finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) ");
				finalQuery.append(" ) ) ");
			} else {
				finalQuery.append(" not exists (select cp.companyItemType from ");
				finalQuery.append(profileJoin);
				finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
				finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
				finalQuery.append(keyMapping);
				finalQuery.append(" and ic.categoryKey != -1 and cp.companyItemType = ");
				finalQuery.append(mappingField);
				finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) ");
			}
		}
		return finalQuery.toString();
	}
	
	private String getFinalQueryForExceptionCostRecordUpload(String searchReportType, Users user, boolean isRoleMapping) {
		String mappingField = getCompanyItemTypeMapping().trim();
		if (mappingField.contains("Attribute") || mappingField.contains("dataSource")) {
			mappingField = "im." + mappingField;
		} else if (mappingField.contains("businessEntity")) {
			mappingField = "im.businessEntity.businessEntityName";
		}

		String keyMapping = isRoleMapping ? " rcpm.roleKey = " + user.getRole().getRoleKey() + " and rcpm.businessEntity.businessEntityKey = "
				+ (user.getBusinessEntity() == null ? -1 : user.getBusinessEntity().getBusinessEntityKey()) : " us.userKey = " + user.getUserKey();

		String profileJoin = isRoleMapping ? " Role r left join r.roleProfileMapping rcpm left join rcpm.commodityProfile cp "
				: " Users us left join us.userProfileMapping cp ";

		StringBuilder finalQuery = new StringBuilder(" and ");
		if (isAll) {
			finalQuery.append(" (cat is not null or (cat is null and ");
			finalQuery.append(mappingField);
			finalQuery.append(" not in (");
			finalQuery.append(getCommaSeparatedString(allCompanyItemType));
			finalQuery.append(") ");
			finalQuery.append(" )) and ( exists (select cp.companyItemType from ");
			finalQuery.append(profileJoin);
			finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
			finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
			finalQuery.append(keyMapping);
			finalQuery.append(" and cp.companyItemType = ");
			finalQuery.append(mappingField);
			finalQuery.append(" and ((ic.categoryKey = -1 ) ");
			// finalQuery.append(" and ct.costType = cr.costType ) ");
			// finalQuery.append(" or (ct.costType = cr.costType and ic.categoryKey = cat.categoryKey))) ");
			finalQuery.append(" or (ic.categoryKey = cat.categoryKey))) ");
			finalQuery.append(" and exists (select cp.companyItemType from ");
			finalQuery.append(" (select cp.companyItemType from Users us ");
			finalQuery.append(profileJoin);
			finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
			finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
			finalQuery.append(keyMapping);
			finalQuery.append(" and ic.categoryKey != -1 and cp.companyItemType = ");
			finalQuery.append(mappingField);
			// finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) )");
			finalQuery.append(" and ic.categoryKey = cat.categoryKey ) )");
		} else {
			if (isNull) {
				finalQuery.append(" exists (select cp.companyItemType from ");
				finalQuery.append(profileJoin);
				finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
				finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
				finalQuery.append(keyMapping);
				finalQuery.append(" and ( ");
				finalQuery.append(" (cp.itemCategory is null and cp.companyItemType = ");
				finalQuery.append(mappingField);
				// finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) or ");
				finalQuery.append(" and ic.categoryKey = cat.categoryKey ) or ");
				finalQuery.append(" (ic.categoryKey != -1 and cp.companyItemType = ");
				finalQuery.append(mappingField);
				// finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) ");
				finalQuery.append(" and ic.categoryKey = cat.categoryKey ) ");
				finalQuery.append(" ) ) ");
			} else {
				finalQuery.append(" exists (select cp.companyItemType from ");
				finalQuery.append(profileJoin);
				finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
				finalQuery.append(" where cp.includeExcludeCostRecord is not null and ");
				finalQuery.append(keyMapping);
				finalQuery.append(" and ic.categoryKey != -1 and cp.companyItemType = ");
				finalQuery.append(mappingField);				
				// finalQuery.append(" and ct.costType = cr.costType and ic.categoryKey = cat.categoryKey ) ");
				finalQuery.append(" and ic.categoryKey = cat.categoryKey ) ");
			}
		}
		return finalQuery.toString();
	}

	private String getFinalQueryForOtherWith(String searchReportType, Users user, boolean isRoleMapping) {
		String mappingField = getCompanyItemTypeMapping().trim();
		if (mappingField.contains("Attribute") || mappingField.contains("dataSource")) {
			mappingField = "im." + mappingField;
		} else if (mappingField.contains("businessEntity")) {
			mappingField = "im.businessEntity.businessEntityName";
		}

		String keyMapping = isRoleMapping ? " rcpm.role.roleKey = " + user.getRole().getRoleKey() +
				" and rcpm.businessEntity.businessEntityKey = " + (user.getBusinessEntity() == null ? -1 : user.getBusinessEntity().getBusinessEntityKey())
				: " us.userKey = " + user.getUserKey();

		String profileJoin = isRoleMapping ? " Role r left join r.roleProfileMapping rcpm left join rcpm.commodityProfile cp " : " Users us left join us.userProfileMapping cp ";

		StringBuilder finalQuery = new StringBuilder(" and ");
		if (isAll) {
			finalQuery.append(" (cat is not null or (cat is null and ");
			finalQuery.append(mappingField);
			finalQuery.append(" not in (");
			finalQuery.append(getCommaSeparatedString(allCompanyItemType));
			finalQuery.append(") ");
			finalQuery.append(") ) and ( not exists (select cp.companyItemType from ");
			finalQuery.append(profileJoin);
			finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
			finalQuery.append(" where cp.");
			finalQuery.append(getBussinessObjectColumnName(searchReportType));
			finalQuery.append(" is not null and ");
			finalQuery.append(keyMapping);
			finalQuery.append(" and ic.categoryKey = -1 and cp.companyItemType = ");
			finalQuery.append(mappingField);
			finalQuery.append(" ) ");
			finalQuery.append("and  not exists (select cp.companyItemType from ");
			finalQuery.append(profileJoin);
			finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
			finalQuery.append(" where cp.");
			finalQuery.append(getBussinessObjectColumnName(searchReportType));
			finalQuery.append(" is not null and ");
			finalQuery.append(keyMapping);
			finalQuery.append(" and ic.categoryKey != -1 and cp.companyItemType = ");
			finalQuery.append(mappingField);
			finalQuery.append(" and ic.categoryKey = cat.categoryKey ) )");
		} else {
			if (isNull) {
				finalQuery.append(" not exists (select cp.companyItemType from ");
				finalQuery.append(profileJoin);
				finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
				finalQuery.append(" where cp.");
				finalQuery.append(getBussinessObjectColumnName(searchReportType));
				finalQuery.append(" is not null and ");
				finalQuery.append(keyMapping);
				finalQuery.append(" and ( ");
				finalQuery.append(" (cp.itemCategory is null and cp.companyItemType = ");
				finalQuery.append(mappingField);
				finalQuery.append(" and ic.categoryKey = cat.categoryKey ) or ");
				finalQuery.append(" (ic.categoryKey != -1 and cp.companyItemType = ");
				finalQuery.append(mappingField);
				finalQuery.append(" and ic.categoryKey = cat.categoryKey ) ");
				finalQuery.append(" ) ) ");
			} else {
				finalQuery.append(" not exists (select cp.companyItemType from ");
				finalQuery.append(profileJoin);
				finalQuery.append(" left join cp.itemCategory ic left join cp.costTypes ct ");
				finalQuery.append(" where cp.");
				finalQuery.append(getBussinessObjectColumnName(searchReportType));
				finalQuery.append(" is not null and ");
				finalQuery.append(keyMapping);
				finalQuery.append(" and ic.categoryKey != -1 and cp.companyItemType = ");
				finalQuery.append(mappingField);
				finalQuery.append(" and ic.categoryKey = cat.categoryKey ) ");
			}
		}
		return finalQuery.toString();
	}
	
	private String getFinalQueryForPriceTAMSQL(String searchReportType, Users user, boolean isRoleMapping) {
		String mappingField = getCompanyItemTypeMapping().trim();
		if (mappingField.contains("dataSource")) {
			mappingField = "z.ITEM_DATA_SOURCE";
		} else if (mappingField.contains("businessEntity")) {
			mappingField = "z.ITEM_BUSINESS_ENTITY";
		}
		StringBuilder finalQuery = new StringBuilder(" AND ");

		String profileMapping = isRoleMapping ? " IS NOT NULL AND rcpm.ROLE_KEY = " + user.getRole().getRoleKey()
				+ " AND rcpm.BUSINESS_ENTITY_KEY = " + (user.getBusinessEntity() == null ? -1 : user.getBusinessEntity().getBusinessEntityKey())
				: " IS NOT NULL AND ucm.USER_KEY = " + user.getUserKey();

		if (isAll) {
			finalQuery.append(" (z.ITEM_CATEGORY_KEY IS NOT NULL OR (z.ITEM_CATEGORY_KEY IS NULL AND ");
			finalQuery.append(mappingField);
			finalQuery.append(" NOT IN (");
			finalQuery.append(getCommaSeparatedString(allCompanyItemType));
			finalQuery.append(") ");
			finalQuery.append(") ) AND NOT EXISTS ");
			finalQuery.append(" (SELECT ifm.FUNCTIONAL_GROUP_ID FROM COMMODITY_PROFILE cp ");
			if(isRoleMapping){
				finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcpm ON rcpm.PROFILE_ID = cp.PROFILE_ID ");
			}else{
				finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME ");
			}
			finalQuery.append(" LEFT JOIN ITEM_ITEM_CATEGORY iic ON iic.ITEM_CATEGORY_KEY = cp.ITEM_CATEGORY_KEY ");
			finalQuery.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = iic.ITEM_KEY ");
			finalQuery.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = im.ITEM_KEY ");
			finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE cpct ON cpct.PROFILE_ID = cp.PROFILE_ID ");
			finalQuery.append(" INNER JOIN BUSINESS_ENTITY be ON be.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY ");
			finalQuery.append(" WHERE cp.");
			finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
			finalQuery.append(profileMapping);
			finalQuery.append(" AND iic.ITEM_CATEGORY_KEY = -1 AND cp.COMPANY_ITEM_TYPE = ");
			finalQuery.append(mappingField);
			finalQuery.append(" AND 1 = (CASE WHEN ifm.FUNCTIONAL_GROUP_ID IS NOT NULL AND ifm.FUNCTIONAL_GROUP_ID   = z.FUNCTIONAL_GROUP_ID THEN 1  ");
			finalQuery.append(" WHEN im.ITEM_IDENTIFIER = z.ITEM_IDENTIFIER AND im.ITEM_KEY = z.ITEM_KEY THEN 1 ELSE 0 END ) )");
		} else {
			if (isNull) {
				finalQuery.append(" NOT EXISTS ");
				finalQuery.append(" (SELECT ifm.FUNCTIONAL_GROUP_ID FROM COMMODITY_PROFILE cp ");
				if(isRoleMapping){
					finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcpm ON rcpm.PROFILE_ID = cp.PROFILE_ID ");
				}else{
					finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME ");
				}
				finalQuery.append(" LEFT JOIN ITEM_ITEM_CATEGORY iic ON iic.ITEM_CATEGORY_KEY = cp.ITEM_CATEGORY_KEY ");
				finalQuery.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = iic.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = im.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE cpct ON cpct.PROFILE_ID = cp.PROFILE_ID ");
				finalQuery.append(" INNER JOIN BUSINESS_ENTITY be ON be.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY ");
				finalQuery.append(" WHERE cp.");
				finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
				finalQuery.append(profileMapping);
				finalQuery.append(" AND ( ");
				finalQuery.append(" (iic.ITEM_CATEGORY_KEY IS NULL AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" ) OR ");
				finalQuery.append(" (iic.ITEM_CATEGORY_KEY != -1 AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" ) ) ");
				finalQuery.append(" AND 1 = (CASE WHEN ifm.FUNCTIONAL_GROUP_ID IS NOT NULL AND ifm.FUNCTIONAL_GROUP_ID   = z.FUNCTIONAL_GROUP_ID THEN 1  ");
				finalQuery.append(" WHEN im.ITEM_IDENTIFIER = z.ITEM_IDENTIFIER AND im.ITEM_KEY = z.ITEM_KEY THEN 1 ELSE 0 END ) )");
			} else {
				finalQuery.append(" NOT EXISTS ");
				finalQuery.append(" (SELECT ifm.FUNCTIONAL_GROUP_ID FROM COMMODITY_PROFILE cp ");
				if(isRoleMapping){
					finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcpm ON rcpm.PROFILE_ID = cp.PROFILE_ID ");
				}else{
					finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME ");
				}
				finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME ");
				finalQuery.append(" LEFT JOIN ITEM_ITEM_CATEGORY iic ON iic.ITEM_CATEGORY_KEY = cp.ITEM_CATEGORY_KEY ");
				finalQuery.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = iic.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = im.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE cpct ON cpct.PROFILE_ID = cp.PROFILE_ID ");
				finalQuery.append(" INNER JOIN BUSINESS_ENTITY be ON be.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY ");
				finalQuery.append(" WHERE cp.");
				finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
				finalQuery.append(profileMapping);
				finalQuery.append(" AND iic.ITEM_CATEGORY_KEY != -1 AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" AND 1 = (CASE WHEN ifm.FUNCTIONAL_GROUP_ID IS NOT NULL AND ifm.FUNCTIONAL_GROUP_ID   = z.FUNCTIONAL_GROUP_ID THEN 1  ");
				finalQuery.append(" WHEN im.ITEM_IDENTIFIER = z.ITEM_IDENTIFIER AND im.ITEM_KEY = z.ITEM_KEY THEN 1 ELSE 0 END ) )");
			}
		}
		return finalQuery.toString();
	}
	
	private String getFinalQueryForXLOBDeleteTAMSQL(String searchReportType, Users user, boolean isRoleMapping) {
		String mappingField = getCompanyItemTypeMapping().trim();
		if (mappingField.contains("dataSource")) {
			mappingField = "IM.DATA_SOURCE";
		} else if (mappingField.contains("businessEntity")) {
			mappingField = "BE.BUSINESS_ENTITY_NAME";
		}
		StringBuilder finalQuery = new StringBuilder(" AND ");
		String profileMapping = isRoleMapping ? " IS NOT NULL AND rcpm.ROLE_KEY = " + user.getRole().getRoleKey()
				+ " AND rcpm.BUSINESS_ENTITY_KEY = " + (user.getBusinessEntity() == null ? -1 : user.getBusinessEntity().getBusinessEntityKey())
				: " IS NOT NULL AND ucm.USER_KEY = " + user.getUserKey();

		if (isAll) {
			finalQuery.append(" (IIC.ITEM_CATEGORY_KEY IS NOT NULL OR (IIC.ITEM_CATEGORY_KEY IS NULL AND ");
			finalQuery.append(mappingField);
			finalQuery.append(" NOT IN (");
			finalQuery.append(getCommaSeparatedString(allCompanyItemType));
			finalQuery.append(") ");
			finalQuery.append(") ) AND NOT EXISTS ");
			finalQuery.append(" (SELECT ifm.FUNCTIONAL_GROUP_ID FROM COMMODITY_PROFILE cp ");
			if(isRoleMapping){
				finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcpm ON rcpm.PROFILE_ID = cp.PROFILE_ID ");
			}else{
				finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME ");
			}
			finalQuery.append(" LEFT JOIN ITEM_ITEM_CATEGORY iic ON iic.ITEM_CATEGORY_KEY = cp.ITEM_CATEGORY_KEY ");
			finalQuery.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = iic.ITEM_KEY ");
			finalQuery.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = im.ITEM_KEY ");
			finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE cpct ON cpct.PROFILE_ID = cp.PROFILE_ID ");
			finalQuery.append(" INNER JOIN BUSINESS_ENTITY be ON be.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY ");
			finalQuery.append(" WHERE cp.");
			finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
			finalQuery.append(profileMapping);
			finalQuery.append(" AND iic.ITEM_CATEGORY_KEY = -1 AND cp.COMPANY_ITEM_TYPE = ");
			finalQuery.append(mappingField);
			finalQuery.append(" AND ifm.FUNCTIONAL_GROUP_ID = FG.FUNCTIONAL_GROUP_ID )");
		} else {
			if (isNull) {
				finalQuery.append(" NOT EXISTS ");
				finalQuery.append(" (SELECT ifm.FUNCTIONAL_GROUP_ID FROM COMMODITY_PROFILE cp ");
				if(isRoleMapping){
					finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcpm ON rcpm.PROFILE_ID = cp.PROFILE_ID ");
				}else{
					finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME ");
				}
				finalQuery.append(" LEFT JOIN ITEM_ITEM_CATEGORY iic ON iic.ITEM_CATEGORY_KEY = cp.ITEM_CATEGORY_KEY ");
				finalQuery.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = iic.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = im.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE cpct ON cpct.PROFILE_ID = cp.PROFILE_ID ");
				finalQuery.append(" INNER JOIN BUSINESS_ENTITY be ON be.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY ");
				finalQuery.append(" WHERE cp.");
				finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
				finalQuery.append(profileMapping);
				finalQuery.append(" AND ( ");
				finalQuery.append(" (iic.ITEM_CATEGORY_KEY IS NULL AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" ) OR ");
				finalQuery.append(" (iic.ITEM_CATEGORY_KEY != -1 AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" ) ) ");
				finalQuery.append(" AND ifm.FUNCTIONAL_GROUP_ID   = FG.FUNCTIONAL_GROUP_ID )");
			} else {
				finalQuery.append(" NOT EXISTS ");
				finalQuery.append(" (SELECT ifm.FUNCTIONAL_GROUP_ID FROM COMMODITY_PROFILE cp ");
				if(isRoleMapping){
					finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcpm ON rcpm.PROFILE_ID = cp.PROFILE_ID ");
				}else{
					finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME ");
				}
				finalQuery.append(" LEFT JOIN ITEM_ITEM_CATEGORY iic ON iic.ITEM_CATEGORY_KEY = cp.ITEM_CATEGORY_KEY ");
				finalQuery.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = iic.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = im.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE cpct ON ");
				finalQuery.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = iic.ITEM_KEY ");
				finalQuery.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = im.ITEM_KEY ");
				finalQuery.append("LEFT JOIN COMMODITY_PROFILE_COST_TYPE cpct ON cpct.PROFILE_ID = cp.PROFILE_ID ");
				finalQuery.append(" INNER JOIN BUSINESS_ENTITY be ON be.BUSINESS_ENTITY_KEY = im.BUSINESS_ENTITY_KEY ");
				finalQuery.append(" WHERE cp.");
				finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
				finalQuery.append(profileMapping);
				finalQuery.append(" AND iic.ITEM_CATEGORY_KEY != -1 AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" AND ifm.FUNCTIONAL_GROUP_ID   = FG.FUNCTIONAL_GROUP_ID )");
			}
		}
		//appendNullXLOBRestrictions(finalQuery, searchReportType, currentUserKey, mappingField);
		return finalQuery.toString();
	}


	/*public void checkExcludedItemExist(XMLStreamReader xmlr, Users user, String uploadType,
			BaseImporter importer) throws MessageLoaderException, XMLStreamException {
		if (uploadType.equals(UPLOAD_COST_RECORDS) || uploadType.equals(UPLOAD_COST_RECORDS_ACTIONS)
				|| uploadType.equals(UPLOAD_EXPIRE_COST_RECORDS) || uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_FG)
				|| uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_PFG)) {

			Map<String,Boolean> validatedFG = null;
			Map<String,Boolean> validatedPFG = null;

			if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_FG)) {
				validatedFG = new HashMap<String, Boolean>();
			} else if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_PFG)) {
				validatedPFG = new HashMap<String, Boolean>();
			}

			while (xmlr.hasNext()) {
				xmlr.next();
				if (xmlr.isStartElement() && xmlr.getLocalName().equals(COST_RECORD_ELEMENT)) {

					String itemIdentifier = null;
					String parentFGName = null;
					String fgName = null;

					if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_FG)) {
						fgName = xmlr.getAttributeValue(null, FG_NAME_ATTRIBUTE);
					} else if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_PFG)) {
						parentFGName = xmlr.getAttributeValue(null, PARENT_NAME_ATTRIBUTE);
					} else {
						itemIdentifier = xmlr.getAttributeValue(null, ITEM_IDENTIFIER_ATTRIBUTE);
					}

					while (xmlr.hasNext()) {
						xmlr.next();
						if (xmlr.isStartElement() && xmlr.getLocalName().equals(COST_RECORD_COST_TYPE_ELEMENT)) {
							String costType = xmlr.getAttributeValue(null, COST_TYPE_ATTRIBUTE);

							if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_FG)) {
								if(validatedFG.containsKey(fgName)) {
									if(!validatedFG.get(fgName)) {
										importer.addError("Functional Group : " + fgName + " is excluded for Cost Type :"
												+ costType + " for current User");
									}
								}else {
									if (isFGExcludedByCostType(fgName, user, costType)) {
										importer.addError("Functional Group : " + fgName + " is excluded for Cost Type :"
												+ costType + " for current User");
										validatedFG.put(fgName, false);
									}else {
										validatedFG.put(fgName, true);
									}
								}
							} else if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_RECORD_PFG)) {
								if(validatedPFG.containsKey(parentFGName)) {
									if(!validatedPFG.get(parentFGName)) {
										importer.addError("Parent Functional Group : " + parentFGName + " is excluded for Cost Type :"
												+ costType + " for current User");
									}
								}else {
									if (isPFGExcludedByCostType(parentFGName, user, costType)) {
										importer.addError("Parent Functional Group : " + parentFGName + " is excluded for Cost Type :"
												+ costType + " for current User");
										validatedPFG.put(fgName, false);
									}else {
										validatedPFG.put(fgName, true);
									}
								}
							} else {
								if (isExcludedItemExistForCostRecord(itemIdentifier, user,
										costType)) {
									importer.addError("Item : " + itemIdentifier + " is excluded for Cost Type :"
											+ costType + " for current User");
								}
							}
						} else if (xmlr.isEndElement() && xmlr.getLocalName().equals(COST_RECORD_ELEMENT)) {
							break;
						}
					}
				}
			}

		} else if (uploadType.equals(UPLOAD_ADJUSTABLE_FORECAST_RECORDS)
				|| uploadType.equals(UPLOAD_CURRENT_FORECAST_RECORDS)) {
			while (xmlr.hasNext()) {
				xmlr.next();
				if (xmlr.isStartElement() && xmlr.getLocalName().equals(FORECAST_ELEMENT)) {
					String itemIdentifier = xmlr.getAttributeValue(null, ITEM_IDENTIFIER_ATTRIBUTE);
					if (isExcludedItemExistForOtherRecord(itemIdentifier, user,
							"INCLUDE_EXCLUDE_COST_FORECAST")) {
						importer.addError("ItemNumber : " + itemIdentifier + " , is excluded for current User.");
					}
				}
			}
		} else if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_FORECAST_FG)) {
			Map<String, Boolean> validatedFG = new HashMap<String, Boolean>();
			while (xmlr.hasNext()) {
				xmlr.next();
				if (xmlr.isStartElement() && xmlr.getLocalName().equals(FORECAST_ELEMENT)) {
					String fgName = xmlr.getAttributeValue(null, FG_NAME_ATTRIBUTE);
					if (validatedFG.containsKey(fgName)) {
						if (!validatedFG.get(fgName)) {
							importer.addError("Functional group "+fgName+" exclude for current user");
						}
					} else {
						if (isFGExcluded(fgName, user)) {
							importer.addError("Functional group "+fgName+" exclude for current user");
							validatedFG.put(fgName, false);
						} else {
							validatedFG.put(fgName, true);
						}
					}
				}
			}
		} else if (uploadType.equals(UPLOAD_MASS_UPDATE_COST_FORECAST_PFG)) {
			Map<String, Boolean> validatedPFG = new HashMap<String, Boolean>();
			while (xmlr.hasNext()) {
				xmlr.next();
				if (xmlr.isStartElement() && xmlr.getLocalName().equals(FORECAST_ELEMENT)) {
					String parentFGName = xmlr.getAttributeValue(null, PARENT_NAME_ATTRIBUTE);

					if (validatedPFG.containsKey(parentFGName)) {
						if (!validatedPFG.get(parentFGName)) {
							importer.addError("Parent functional group "+parentFGName+" exclude for current user");
						}
					} else {
						if (isPFGExcluded(parentFGName, user)) {
							importer.addError("Parent functional group "+parentFGName+" exclude for current user");
							validatedPFG.put(parentFGName, false);
						} else {
							validatedPFG.put(parentFGName, true);
						}
					}
				}
			}
		}
	}*/

	public String getSQLTypePropertyValue() {

		String companyItemTypeField = pcmConfigUtil.getString("pcm.commodityProfile.companyItemType.field",
				"dataSource");

		boolean flag = true;
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < companyItemTypeField.length(); i++) {
			int j = companyItemTypeField.charAt(i);
			if (j < 91 && flag) {
				flag = false;
				result.append("_");
				result.append(companyItemTypeField.charAt(i));
			} else {
				result.append(companyItemTypeField.charAt(i));
			}
		}
		return result.toString();
	}

	public boolean isExcludedItemExistForCostRecord(String itemIdentifier, Users user, String costType) {
		String companyItemTypeField = getSQLTypePropertyValue();

		boolean isRoleMapping = isRoleMapping();
		StringBuilder sql = new StringBuilder(" SELECT DISTINCT 1 FROM USER_COMMODITYPROFILE_MAPPING UCPM ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE CP ON UCPM.PROFILE_NAME = CP.PROFILE_NAME ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE CPCT ON CP.PROFILE_ID = CPCT.PROFILE_ID ");
		sql.append(isRoleMapping ? " WHERE UCPM.ROLE_KEY = :mappingKey " : "  WHERE UCPM.USER_KEY = :mappingKey ");
		sql.append(" AND CPCT.COST_TYPE = :costType ");
		sql.append(" AND CP.INCLUDE_EXCLUDE_COST_RECORD IS NOT NULL ");
		if (companyItemTypeField.equalsIgnoreCase("business_Entity")) {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT BE.BUSINESS_ENTITY_NAME FROM ITEM_MASTER IM ");
			sql.append(
					" LEFT JOIN BUSINESS_ENTITY BE ON IM.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY WHERE IM.ITEM_IDENTIFIER = :identifier) ");
		} else {
			sql.append(
					" AND CP.COMPANY_ITEM_TYPE IN (SELECT UPPER(IM." + companyItemTypeField + ") FROM ITEM_MASTER IM ");
			sql.append(" WHERE IM.ITEM_IDENTIFIER = :identifier) ");
		}
		sql.append(" AND ( EXISTS (SELECT 1 FROM ITEM_MASTER IM ");
		sql.append(" LEFT JOIN ITEM_ITEM_CATEGORY IIC ON IM.ITEM_KEY = IIC.ITEM_KEY ");
		sql.append(" WHERE IIC.ITEM_CATEGORY_KEY = CP.ITEM_CATEGORY_KEY ");
		sql.append(" AND IM.ITEM_IDENTIFIER = :identifier ) ");
		sql.append("  OR CP.ITEM_CATEGORY_KEY = -1 ) ");
		sql.append(" AND ROWNUM <= 1 ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mappingKey", isRoleMapping ? user.getRole().getRoleKey() : user.getUserKey());
        parameters.put("identifier", itemIdentifier);
        parameters.put("costType", costType);
        return !commodityProfileRepository.executeNativeSqlQuery(sql.toString(), parameters).isEmpty();
	}

	public boolean isPFGExcludedByCostType(String parentFunctionalGroupName, Users user, String costType) {
		String companyItemTypeField = getSQLTypePropertyValue();

		boolean isRoleMapping = isRoleMapping();

		StringBuilder sql = new StringBuilder(" SELECT distinct 1  from PARENT_FUNCTIONAL_GROUP pfg ");
		sql.append(" inner join FG_PFG_MAP fpm on pfg.parent_functional_group_id = fpm.PARENT_FUNCTIONAL_GROUP_ID ");
		sql.append(" inner join functional_group fg on fg.FUNCTIONAL_GROUP_ID = fpm.FUNCTIONAL_GROUP_ID ");
		sql.append(" inner join item_fg_map ifm on fg.functional_group_id = ifm.functional_group_id ");
		sql.append(" inner join item_master fim on ifm.item_key = fim.item_key ");
		sql.append(" where UPPER(pfg.name)= UPPER(:pfgName) ");
		sql.append(" and EXISTS ( ");
		sql.append(" SELECT DISTINCT 1 FROM USER_COMMODITYPROFILE_MAPPING UCPM ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE CP ON UCPM.PROFILE_NAME = CP.PROFILE_NAME ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE CPCT ON CP.PROFILE_ID = CPCT.PROFILE_ID ");
		sql.append(isRoleMapping ? " WHERE UCPM.ROLE_KEY = :mappingKey " : " WHERE UCPM.USER_KEY = :mappingKey ");
		sql.append(" AND CPCT.COST_TYPE = :costType ");
		sql.append(" AND CP.INCLUDE_EXCLUDE_COST_RECORD IS NOT NULL ");

		if (companyItemTypeField.equalsIgnoreCase("business_Entity")) {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT BE.BUSINESS_ENTITY_NAME FROM ITEM_MASTER IM ");
			sql.append(
					" LEFT JOIN BUSINESS_ENTITY BE ON IM.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		} else {
			sql.append(
					" AND CP.COMPANY_ITEM_TYPE IN (SELECT UPPER(IM." + companyItemTypeField + ") FROM ITEM_MASTER IM ");
			sql.append(" WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		}

		sql.append(" AND ( EXISTS (SELECT 1 FROM ITEM_MASTER IM ");
		sql.append(" LEFT JOIN ITEM_ITEM_CATEGORY IIC ON IM.ITEM_KEY = IIC.ITEM_KEY ");
		sql.append(" WHERE IIC.ITEM_CATEGORY_KEY = CP.ITEM_CATEGORY_KEY ");
		sql.append(" AND IM.ITEM_KEY = fim.ITEM_KEY ) ");
		sql.append(" OR CP.ITEM_CATEGORY_KEY = -1 ) ");
		sql.append(" AND ROWNUM <= 1) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mappingKey", isRoleMapping ? user.getRole().getRoleKey() : user.getUserKey());
        parameters.put("costType", costType);
        parameters.put("pfgName", parentFunctionalGroupName);

		return !commodityProfileRepository.executeNativeSqlQuery(sql.toString(), parameters).isEmpty();
	}

	public boolean isFGExcludedByCostType(String functionalGroupName, Users user, String costType) {
		String companyItemTypeField = getSQLTypePropertyValue();
		boolean isRoleMapping = isRoleMapping();
		StringBuilder sql = new StringBuilder(" SELECT distinct 1 from FUNCTIONAL_GROUP fg ");
		sql.append(" inner join item_fg_map ifm on fg.functional_group_id = ifm.functional_group_id ");
		sql.append(" inner join item_master fim on ifm.item_key = fim.item_key ");
		sql.append(" where fg.name=:fgName ");
		sql.append(" and EXISTS ( ");
		sql.append(" SELECT DISTINCT 1 FROM USER_COMMODITYPROFILE_MAPPING UCPM ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE CP ON UCPM.PROFILE_NAME = CP.PROFILE_NAME ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE CPCT ON CP.PROFILE_ID = CPCT.PROFILE_ID ");
		sql.append(isRoleMapping ? " WHERE UCPM.ROLE_KEY = :mappingKey " : " WHERE UCPM.USER_KEY = :mappingKey ");
		sql.append(" AND CPCT.COST_TYPE = :costType ");
		sql.append(" AND CP.INCLUDE_EXCLUDE_COST_RECORD IS NOT NULL ");

		if (companyItemTypeField.equalsIgnoreCase("business_Entity")) {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT BE.BUSINESS_ENTITY_NAME FROM ITEM_MASTER IM ");
			sql.append(
					" LEFT JOIN BUSINESS_ENTITY BE ON IM.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		} else {
			sql.append(
					" AND CP.COMPANY_ITEM_TYPE IN (SELECT UPPER(IM." + companyItemTypeField + ") FROM ITEM_MASTER IM ");
			sql.append(" WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		}

		sql.append(" AND ( EXISTS (SELECT 1 FROM ITEM_MASTER IM ");
		sql.append(" LEFT JOIN ITEM_ITEM_CATEGORY IIC ON IM.ITEM_KEY = IIC.ITEM_KEY ");
		sql.append(" WHERE IIC.ITEM_CATEGORY_KEY = CP.ITEM_CATEGORY_KEY ");
		sql.append(" AND IM.ITEM_KEY = fim.ITEM_KEY ) ");
		sql.append(" OR CP.ITEM_CATEGORY_KEY = -1 ) ");
		sql.append(" AND ROWNUM <= 1) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mappingKey", isRoleMapping ? user.getRole().getRoleKey() : user.getUserKey());
        parameters.put("costType", costType);
        parameters.put("fgName", functionalGroupName);

		return !commodityProfileRepository.executeNativeSqlQuery(sql.toString(), parameters).isEmpty();
	}

	public boolean isExcludedItemExistForOtherRecord(String itemIdentifier, Users user, String businessObject) {
		String companyItemTypeField = getSQLTypePropertyValue();
		boolean isRoleMapping = isRoleMapping();
		StringBuilder sql = new StringBuilder(" SELECT DISTINCT 1 FROM USER_COMMODITYPROFILE_MAPPING UCPM ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE CP ON UCPM.PROFILE_NAME = CP.PROFILE_NAME ");
		sql.append(isRoleMapping ? " WHERE UCPM.ROLE_KEY = :mappingKey " : " WHERE UCPM.USER_KEY = :mappingKey ");
		sql.append(" AND CP." + businessObject + " IS NOT NULL ");
		if (companyItemTypeField.equalsIgnoreCase("business_Entity")) {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT UPPER(BE.BUSINESS_ENTITY_NAME) FROM ITEM_MASTER IM ");
			sql.append(
					" LEFT JOIN BUSINESS_ENTITY BE ON IM.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY WHERE IM.ITEM_IDENTIFIER = :identifier) ");
		} else {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT IM." + companyItemTypeField + " FROM ITEM_MASTER IM ");
			sql.append(" WHERE IM.ITEM_IDENTIFIER = :identifier) ");
		}
		sql.append(" AND ( EXISTS (SELECT 1 FROM ITEM_MASTER IM ");
		sql.append(" LEFT JOIN ITEM_ITEM_CATEGORY IIC ON IM.ITEM_KEY = IIC.ITEM_KEY ");
		sql.append(" WHERE IIC.ITEM_CATEGORY_KEY = CP.ITEM_CATEGORY_KEY ");
		sql.append(" AND IM.ITEM_IDENTIFIER = :identifier ) ");
		sql.append("  OR CP.ITEM_CATEGORY_KEY = -1 ) ");
		sql.append(" AND ROWNUM <= 1 ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mappingKey", isRoleMapping ? user.getRole().getRoleKey() : user.getUserKey());
        parameters.put("identifier", itemIdentifier);

		return !commodityProfileRepository.executeNativeSqlQuery(sql.toString(), parameters).isEmpty();
	}

	public Set<String> getCostExcludeListForItem(Users user, String itemIdentifier) {

		String companyItemTypeField = pcmConfigUtil.getString("pcm.commodityProfile.companyItemType.field",
				"dataSource");
		boolean isRoleMapping = isRoleMapping();
		String[] itemTypeList = pcmConfigUtil.getList("pcm.commodityProfile.itemType.field", List.of("I")).toArray(new String[0]);
		StringBuilder sb = new StringBuilder("select include_exclude_cost_record from commodity_profile cp");
        if(isRoleMapping){
            sb.append(" inner join role_commodity_profile_mapping rcpm on rcpm.profile_id = cp.profile_id ");
            sb.append(" and rcpm.role_key = :mappingKey1 and rcpm.business_entity_key = :mappingKey2");
        }else {
            sb.append(" inner join user_commodityprofile_mapping ucpm on ucpm.profile_name = cp.profile_name ");
            sb.append(" and ucpm.user_key = :mappingKey1 ");
        }
		sb.append(" where exists (select 1 from item_master im ");
		sb.append(" left join item_item_category iic on im.item_key = iic.item_key ");
        sb.append(" inner join business_entity be ON be.business_entity_key = im.business_entity_key");
		sb.append(" where iic.item_category_key = cp.item_category_key and im.item_identifier=:itemIdentifier1 ");
		if (companyItemTypeField.equals("businessEntity")) {
			sb.append(" and be.business_Entity_Name = cp.company_item_type ");
		} else {
			sb.append(" and im.data_source = cp.company_item_type ");
		}
		sb.append(")  or (cp.item_category_key=-1 and cp.company_item_type in (select ");
		if (companyItemTypeField.equals("businessEntity")) {
			sb.append(" be.business_entity_name ");
		} else {
			sb.append(" im.data_source ");
		}
        sb.append(" from item_master im JOIN business_entity be ON be.business_entity_key = im.business_entity_key ");
        sb.append(" where im.item_identifier=:itemIdentifier2  and im.item_type in(:itemType)) )");

        Map<String, Object> parameters = new HashMap<>();
        if(isRoleMapping){
            parameters.put("mappingKey1", user.getRole().getRoleKey());
            parameters.put("mappingKey2", user.getBusinessEntity().getBusinessEntityKey());
        }else {
            parameters.put("mappingKey1", user.getUserKey());
        }
        parameters.put("itemIdentifier1", itemIdentifier);
        parameters.put("itemIdentifier2", itemIdentifier);
        parameters.put("itemType", itemTypeList);

		Set<String> costSet = new HashSet<>();
		for (Object costTypes : commodityProfileRepository.executeNativeSqlQueryAsObject(sb.toString(), parameters)) {
			if (costTypes != null) {
				String costTypeString = (String) costTypes;
				if (costTypeString.contains("ALL")) {
					for (PcmCostType result : pcmCostTypeService.getAllCostTypes()) {
						costSet.add(result.getCostTypeKey());
					}
					break;
				} else {
					String[] splits = costTypeString.replace("EXCLUDE", "").split(",");
					for (String cost : splits) {
						costSet.add(cost.trim());
					}
				}
			}
		}
		return costSet;
	}

	private static String getCommaSeparatedString(List<String> value) {
		StringBuilder sb = new StringBuilder();
		for (String part : value) {
			if (!sb.toString().isEmpty()) {
				sb.append(",");
			}
			sb.append("'");
			sb.append(part);
			sb.append("'");
		}
		return sb.toString();
	}

	public boolean isExcludedItemExistForTam(FunctionalGroup functionalGroup, Users user) {

		int listSize = functionalGroup.getFunctionalGroupItems().size();

		long val = checkItemStatusForAll(user, functionalGroup);
		if (val == listSize) {
			return false;
		} else {
			return true;
		}
	}

	public Long checkItemStatusForAll(Users user, FunctionalGroup functionalGroup) {
		String mappingField = getCompanyItemTypeMapping().trim();
		if (mappingField.contains("Attribute") || mappingField.contains("dataSource")) {
			mappingField = "im." + mappingField;
		} else if (mappingField.contains("businessEntity")) {
			mappingField = "im.businessEntity.businessEntityName";
		}

		StringBuilder sbb = new StringBuilder("select count(fg.name) from FunctionalGroup fg ");
		sbb.append(" left join fg.functionalGroupItems im ");
		sbb.append(" left join im.categories cat ");
		sbb.append(" left join im.businessEntity be ");
		sbb.append(" where fg.functionalGroupId = :functionalGroupId ");
		sbb.append(getQuery("TAMSearch", user));

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("functionalGroupId", functionalGroup.getFunctionalGroupId());

        return (Long) commodityProfileRepository.executeNativeSqlQueryAsObject(sbb.toString(), parameters).get(0);
	}

	public boolean isExcludedFunctionalGroupExistForPriceTam(Long userKey, FunctionalGroup functionalGroup) {

		StringBuilder sbb = new StringBuilder("SELECT COUNT(*) FROM COMMODITY_PROFILE cp");
		sbb.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucm ON ucm.PROFILE_NAME = cp.PROFILE_NAME");
		sbb.append(" LEFT JOIN ITEM_ITEM_CATEGORY iic ON iic.ITEM_CATEGORY_KEY = cp.ITEM_CATEGORY_KEY ");
		sbb.append(" LEFT JOIN ITEM_FG_MAP ifm ON ifm.ITEM_KEY = iic.ITEM_KEY ");
		sbb.append(" INNER JOIN ITEM_MASTER im ON im.ITEM_KEY = ifm.ITEM_KEY ");
		sbb.append(" WHERE cp.INCLUDE_EXCLUDE_PRICE_TAM IS NOT NULL ");
		sbb.append(" AND ifm.FUNCTIONAL_GROUP_ID =  :fgId ");
		sbb.append(" AND ucm.USER_KEY = :userKey ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("fgId", functionalGroup.getFunctionalGroupId());
        parameters.put("userKey", userKey);

		BigDecimal count = (BigDecimal)commodityProfileRepository.executeNativeSqlQueryAsObject(sbb.toString(), parameters).get(0);

		if(count.intValue() >=1){
			return true;
		}

		return false;
	}

	public boolean isCompanyItemTypeExist(String companyItemType, String companyItenTypeConfigValue) {
		if (companyItenTypeConfigValue.equals("businessEntity")) {
			return businessEntityService.findDistinctBusinessEntityNames(companyItemType).size() == 0 ? Boolean.FALSE : Boolean.TRUE;
		} else {
            return itemService.countItemByDataSource(companyItemType) == 0 ? Boolean.FALSE : Boolean.TRUE;
		}
	}
	
	public boolean isFGExcluded(String functionalGroupName, Users user) {
		String companyItemTypeField = getSQLTypePropertyValue();
		boolean isRoleMapping = isRoleMapping();
		StringBuilder sql = new StringBuilder(" SELECT distinct 1 from FUNCTIONAL_GROUP fg ");
		sql.append(" inner join item_fg_map ifm on fg.functional_group_id = ifm.functional_group_id ");
		sql.append(" inner join item_master fim on ifm.item_key = fim.item_key ");
		sql.append(" where lower(fg.name) = lower(:fgName) ");
		sql.append(" and EXISTS ( ");
		sql.append(" SELECT DISTINCT 1 FROM USER_COMMODITYPROFILE_MAPPING UCPM ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE CP ON UCPM.PROFILE_NAME = CP.PROFILE_NAME ");
		sql.append(isRoleMapping ? " WHERE UCPM.ROLE_KEY = :mappingKey " : " WHERE UCPM.USER_KEY = :mappingKey ");
		sql.append(" AND CP.INCLUDE_EXCLUDE_COST_FORECAST IS NOT NULL ");

		if (companyItemTypeField.equalsIgnoreCase("business_Entity")) {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT BE.BUSINESS_ENTITY_NAME FROM ITEM_MASTER IM ");
			sql.append(
					" LEFT JOIN BUSINESS_ENTITY BE ON IM.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		} else {
			sql.append(
					" AND CP.COMPANY_ITEM_TYPE IN (SELECT UPPER(IM." + companyItemTypeField + ") FROM ITEM_MASTER IM ");
			sql.append(" WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		}

		sql.append(" AND ( EXISTS (SELECT 1 FROM ITEM_MASTER IM ");
		sql.append(" LEFT JOIN ITEM_ITEM_CATEGORY IIC ON IM.ITEM_KEY = IIC.ITEM_KEY ");
		sql.append(" WHERE IIC.ITEM_CATEGORY_KEY = CP.ITEM_CATEGORY_KEY ");
		sql.append(" AND IM.ITEM_KEY = fim.ITEM_KEY ) ");
		sql.append(" OR CP.ITEM_CATEGORY_KEY = -1 ) ");
		sql.append(" AND ROWNUM <= 1) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mappingKey", isRoleMapping ? user.getRole().getRoleKey() : user.getUserKey());
        parameters.put("fgName", functionalGroupName);

		return !commodityProfileRepository.executeNativeSqlQueryAsObject(sql.toString(), parameters).isEmpty();
	}

	public boolean isPFGExcluded(String parentFunctionalGroupName, Users user) {
		String companyItemTypeField = getSQLTypePropertyValue();
		boolean isRoleMapping = isRoleMapping();
		StringBuilder sql = new StringBuilder(" SELECT distinct 1  from PARENT_FUNCTIONAL_GROUP pfg ");
		sql.append(" inner join FG_PFG_MAP fpm on pfg.parent_functional_group_id = fpm.PARENT_FUNCTIONAL_GROUP_ID ");
		sql.append(" inner join functional_group fg on fg.FUNCTIONAL_GROUP_ID = fpm.FUNCTIONAL_GROUP_ID ");
		sql.append(" inner join item_fg_map ifm on fg.functional_group_id = ifm.functional_group_id ");
		sql.append(" inner join item_master fim on ifm.item_key = fim.item_key ");
		sql.append(" where lower(pfg.name) = lower(:pfgName) ");
		sql.append(" and EXISTS ( ");
		sql.append(" SELECT DISTINCT 1 FROM USER_COMMODITYPROFILE_MAPPING UCPM ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE CP ON UCPM.PROFILE_NAME = CP.PROFILE_NAME ");
		sql.append(isRoleMapping ? " WHERE UCPM.ROLE_KEY = :mappingKey " : " WHERE UCPM.USER_KEY = :mappingKey ");
		sql.append(" AND CP.INCLUDE_EXCLUDE_COST_FORECAST IS NOT NULL ");

		if (companyItemTypeField.equalsIgnoreCase("business_Entity")) {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT BE.BUSINESS_ENTITY_NAME FROM ITEM_MASTER IM ");
			sql.append(
					" LEFT JOIN BUSINESS_ENTITY BE ON IM.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		} else {
			sql.append(
					" AND CP.COMPANY_ITEM_TYPE IN (SELECT UPPER(IM." + companyItemTypeField + ") FROM ITEM_MASTER IM ");
			sql.append(" WHERE IM.ITEM_KEY = fim.ITEM_KEY) ");
		}

		sql.append(" AND ( EXISTS (SELECT 1 FROM ITEM_MASTER IM ");
		sql.append(" LEFT JOIN ITEM_ITEM_CATEGORY IIC ON IM.ITEM_KEY = IIC.ITEM_KEY ");
		sql.append(" WHERE IIC.ITEM_CATEGORY_KEY = CP.ITEM_CATEGORY_KEY ");
		sql.append(" AND IM.ITEM_KEY = fim.ITEM_KEY ) ");
		sql.append(" OR CP.ITEM_CATEGORY_KEY = -1 ) ");
		sql.append(" AND ROWNUM <= 1) ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("mappingKey", isRoleMapping ? user.getRole().getRoleKey() : user.getUserKey());
        parameters.put("pfgName", parentFunctionalGroupName);

		return !commodityProfileRepository.executeNativeSqlQueryAsObject(sql.toString(), parameters).isEmpty();
	}
	
	public boolean isExcludedItemKeyExistForCostRecord(Long itemKey, Long userkey, String costType) {
		String companyItemTypeField = getSQLTypePropertyValue();

		StringBuilder sql = new StringBuilder(" SELECT DISTINCT 1 FROM USER_COMMODITYPROFILE_MAPPING UCPM ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE CP ON UCPM.PROFILE_NAME = CP.PROFILE_NAME ");
		sql.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE CPCT ON CP.PROFILE_ID = CPCT.PROFILE_ID ");
		sql.append("  WHERE UCPM.USER_KEY = :userKey ");
		sql.append(" AND CPCT.COST_TYPE = :costType ");
		sql.append(" AND CP.INCLUDE_EXCLUDE_COST_RECORD IS NOT NULL ");
		if (companyItemTypeField.equalsIgnoreCase("business_Entity")) {
			sql.append(" AND CP.COMPANY_ITEM_TYPE IN (SELECT BE.BUSINESS_ENTITY_NAME FROM ITEM_MASTER IM ");
			sql.append(
					" LEFT JOIN BUSINESS_ENTITY BE ON IM.BUSINESS_ENTITY_KEY = BE.BUSINESS_ENTITY_KEY WHERE IM.ITEM_IDENTIFIER = :identifier) ");
		} else {
			sql.append(
					" AND CP.COMPANY_ITEM_TYPE IN (SELECT UPPER(IM." + companyItemTypeField + ") FROM ITEM_MASTER IM ");
			sql.append(" WHERE IM.ITEM_KEY = :itemKey) ");
		}
		sql.append(" AND ( EXISTS (SELECT 1 FROM ITEM_MASTER IM ");
		sql.append(" LEFT JOIN ITEM_ITEM_CATEGORY IIC ON IM.ITEM_KEY = IIC.ITEM_KEY ");
		sql.append(" WHERE IIC.ITEM_CATEGORY_KEY = CP.ITEM_CATEGORY_KEY ");
		sql.append(" AND IM.ITEM_KEY = :itemKey ) ");
		sql.append("  OR CP.ITEM_CATEGORY_KEY = -1 ) ");
		sql.append(" AND ROWNUM <= 1 ");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userKey", userkey);
        parameters.put("itemKey", itemKey);
        parameters.put("costType", costType);

		return !commodityProfileRepository.executeNativeSqlQueryAsObject(sql.toString(), parameters).isEmpty();
	}
	private String getFinalQueryForRebate(String searchReportType, Users users, boolean isRoleMapping) {
		String mappingField = getCompanyItemTypeMapping().trim();
		if (mappingField.contains("dataSource")) {
			mappingField = "ITEM.DATA_SOURCE";
		} else if (mappingField.contains("businessEntity")) {
			mappingField = "BE.BUSINESS_ENTITY_NAME";
		}
		StringBuilder finalQuery = new StringBuilder(" AND ");
		String profileMapping = "";
		if(isRoleMapping){
			profileMapping = " IS NOT NULL AND rcmp.ROLE_KEY = " + users.getRole().getRoleKey() +" AND rcmp.BUSINESS_ENTITY_KEY = "
					+ users.getBusinessEntity().getBusinessEntityKey();
		}else{
			profileMapping = " IS NOT NULL AND usr.USER_KEY = " + users.getUserKey();
		}
		if (isAll) {
			finalQuery.append(" (IIC.ITEM_CATEGORY_KEY IS NOT NULL OR (IIC.ITEM_CATEGORY_KEY IS NULL AND ");
			finalQuery.append(mappingField);
			finalQuery.append(" NOT IN (");
			finalQuery.append(getCommaSeparatedString(allCompanyItemType));
			finalQuery.append(") ");
			finalQuery.append(") ) AND NOT EXISTS ");
			if(isRoleMapping){
				finalQuery.append(" (SELECT cp.COMPANY_ITEM_TYPE from PCM_ROLE rl ");
				finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcmp on rl.ROLE_KEY=rcmp.ROLE_KEY ");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE cp on rcmp.PROFILE_ID = cp.PROFILE_ID ");
			}else{
				finalQuery.append(" (SELECT cp.COMPANY_ITEM_TYPE from PCM_USER usr ");
				finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucmp on usr.USER_KEY=ucmp.USER_KEY");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE cp on ucmp.PROFILE_NAME=cp.PROFILE_NAME ");
			}
			finalQuery.append(" LEFT JOIN ITEM_CATEGORY itemcat on cp.ITEM_CATEGORY_KEY=itemcat.ITEM_CATEGORY_KEY ");
			finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE costtype on cp.PROFILE_ID=costtype.PROFILE_ID ");
			finalQuery.append(" WHERE cp.");
			finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
			finalQuery.append(profileMapping);
			finalQuery.append(" AND itemcat.ITEM_CATEGORY_KEY = -1 AND cp.COMPANY_ITEM_TYPE = ");
			finalQuery.append(mappingField);
			finalQuery.append(" ) ");
		} else {
			finalQuery.append(" ( NOT EXISTS ");
			if(isRoleMapping){
				finalQuery.append(" (SELECT cp.COMPANY_ITEM_TYPE from PCM_ROLE rl ");
				finalQuery.append(" LEFT JOIN ROLE_COMMODITY_PROFILE_MAPPING rcmp on rl.ROLE_KEY=rcmp.ROLE_KEY ");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE cp on rcmp.PROFILE_ID = cp.PROFILE_ID ");
			}else{
				finalQuery.append(" (SELECT cp.COMPANY_ITEM_TYPE from PCM_USER usr ");
				finalQuery.append(" LEFT JOIN USER_COMMODITYPROFILE_MAPPING ucmp on usr.USER_KEY=ucmp.USER_KEY");
				finalQuery.append(" LEFT JOIN COMMODITY_PROFILE cp on ucmp.PROFILE_NAME=cp.PROFILE_NAME ");
			}
			finalQuery.append(" LEFT JOIN ITEM_CATEGORY itemcat on cp.ITEM_CATEGORY_KEY=itemcat.ITEM_CATEGORY_KEY ");
			finalQuery.append(" LEFT JOIN COMMODITY_PROFILE_COST_TYPE costtype on cp.PROFILE_ID=costtype.PROFILE_ID ");

			finalQuery.append(" WHERE cp.");				
			finalQuery.append(getBussinessObjectColumnNameForSQL(searchReportType));
			finalQuery.append(profileMapping);
			finalQuery.append(" AND ");
			if (isNull) {
				finalQuery.append(" ( (itemcat.ITEM_CATEGORY_KEY IS NULL AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" ) OR ");
				finalQuery.append(" (itemcat.ITEM_CATEGORY_KEY != -1 AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" ) ) ) ) ");
			} else {
				finalQuery.append(" itemcat.ITEM_CATEGORY_KEY != -1");
				finalQuery.append(" AND cp.COMPANY_ITEM_TYPE = ");
				finalQuery.append(mappingField);
				finalQuery.append(" AND itemcat.ITEM_CATEGORY_KEY=itemcategory.ITEM_CATEGORY_KEY ) )");
			}
		}
		return finalQuery.toString();
	}
	
	private void setCompanyItemType(String searchReportType, Users user, boolean isRoleMapping) {
		StringBuilder sb = new StringBuilder();
		if(isRoleMapping){
			sb.append(" select distinct cp.ITEM_CATEGORY_KEY,cp.COMPANY_ITEM_TYPE from COMMODITY_PROFILE cp ");
			sb.append(" inner join ROLE_COMMODITY_PROFILE_MAPPING rcpm on rcpm.PROFILE_ID = cp.PROFILE_ID ");
			sb.append(" and rcpm.ROLE_KEY = ");
			sb.append(user.getRole().getRoleKey());
			sb.append(" and rcpm.BUSINESS_ENTITY_KEY = ");
			sb.append(user.getBusinessEntity() == null ? -1 : user.getBusinessEntity().getBusinessEntityKey());
		}else{
			sb.append(" select distinct cp.ITEM_CATEGORY_KEY,cp.COMPANY_ITEM_TYPE from COMMODITY_PROFILE cp ");
			sb.append(" inner join USER_COMMODITYPROFILE_MAPPING ucpm on ucpm.PROFILE_NAME = cp.PROFILE_NAME ");
			sb.append(" and ucpm.USER_KEY = ");
			sb.append(user.getUserKey());
		}
		sb.append(" where (cp.ITEM_CATEGORY_KEY is null or cp.ITEM_CATEGORY_KEY = -1 )  ");
		sb.append(" and cp.");
		if (searchReportType.equalsIgnoreCase("SearchDefCostRecord")
				|| searchReportType.equalsIgnoreCase("ExceptionCostRecordUpload")) {
			sb.append("INCLUDE_EXCLUDE_COST_RECORD is not null");
			updateData(sb.toString());
		} else if (searchReportType.equalsIgnoreCase("SearchDefPriceTAM")
				|| searchReportType.equalsIgnoreCase("SearchDefXLOBDeleteDownload")) {
			sb.append("INCLUDE_EXCLUDE_PRICE_TAM is not null");
			updateData(sb.toString());
		} else if (searchReportType.equalsIgnoreCase("SearchDefRebateProgram")
				|| searchReportType.equalsIgnoreCase("newRebateProgram")) {
			sb.append("INCLUDE_EXCLUDE_REBATE is not null ");
			updateData(sb.toString());
		} else {
			sb.append(getBussinessObjectColumnNameForSQL(searchReportType));
			sb.append(" is not null");
			updateData(sb.toString());
		}
	}

    @Transactional
    public long deleteCommodityProfileByUserKey(List<String> keys) {
        List<Long> profileIds = new ArrayList<>();
        List<String> profileNames = new ArrayList<>();
        for (String key : keys) {
            profileIds.add(Long.valueOf(key.split("~")[0]));
            if (commodityProfileRepository.getCommodityProfileCountByNameCriteria(key.split("~")[1]) == 0) {
                profileNames.add(key.split("~")[1]);
            }
        }

        if("role".equals(pcmConfigUtil.getString("pcm.commodityProfile.user.mapping.type","user"))) {
            //delete mapped profile from role mapping
            roleCommodityProfileMappingRepository.deleteByCommodityProfile_ProfileIdIn(profileIds);
        }else if(!profileNames.isEmpty()){
            //delete user mapped profile
            commodityProfileRepository.deleteUserCommodityProfileMappingByNames(profileNames);
        }

        if(!profileIds.isEmpty()){
            //delete commodity profile
            return commodityProfileRepository.deleteByProfileIdIn(profileIds);
        }
        return 0;
    }

    @Transactional
    public long deleteCommodityProfileMapping(List<String> userCommodityProfileKeys) {
        String mappingType = pcmConfigUtil.getString("pcm.commodityProfile.user.mapping.type", "user");
        boolean isRoleMapping = "role".equalsIgnoreCase(mappingType);
        if(isRoleMapping){
            return removeRoleProfileMapping(userCommodityProfileKeys);
        }else {
            Map<Long, Set<String>> mapping = buildMapping(userCommodityProfileKeys);
            removeMapping(mapping);
        }
        return 0;
    }

    private long removeRoleProfileMapping(List<String> userCommodityProfileKeys) {
        int count = 0;
        for (String key : userCommodityProfileKeys) {
            String[] parts = key.split("~");
            if (parts.length != 3) continue;
            String roleKey = parts[0];
            String profileName = parts[1];
            String businessEntityKey = parts[2];
            count += roleCommodityProfileMappingRepository.deleteRoleProfileMapping(
                    Long.parseLong(roleKey), profileName, Long.parseLong(businessEntityKey));
        }
        return count;
    }

    private Map<Long, Set<String>> buildMapping(List<String> userCommodityProfileKeys) {
        Collections.sort(userCommodityProfileKeys);
        Map<Long, Set<String>> map = new HashMap<>();
        Long currentKey = null;
        Set<String> profileNames = new HashSet<>();

        for (String key : userCommodityProfileKeys) {
            String[] parts = key.split("~");
            if (parts.length != 2) continue;

            Long keyPart = Long.parseLong(parts[0]);
            String profileName = parts[1];

            if (currentKey != null && currentKey.equals(keyPart)) {
                profileNames.add(profileName);
            } else {
                if (currentKey != null) {
                    map.put(currentKey, profileNames);
                }
                currentKey = keyPart;
                profileNames = new HashSet<>();
                profileNames.add(profileName);
            }
        }

        if (currentKey != null) {
            map.put(currentKey, profileNames);
        }

        return map;
    }

    private long removeMapping(Map<Long, Set<String>> profileMap) {
        long count = 0;
        for (Map.Entry<Long, Set<String>> entry : profileMap.entrySet()) {
            count += commodityProfileRepository.deleteUserCommodityProfileMappingByUserKeyAndProfileNames(entry.getKey(), entry.getValue());
        }
        return count;
    }
}
