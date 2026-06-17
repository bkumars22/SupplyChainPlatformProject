/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.pcm.item.service;

import com.scplatform.pcm.aml.entity.Aml;
import com.scplatform.pcm.assignment.entity.ItemAssignment;
import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.avl.repository.AvlRepository;
import com.scplatform.pcm.bom.entity.BomLine;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.common.entity.Attribute;
import com.scplatform.pcm.common.entity.FlexAttributeDefn;
import com.scplatform.pcm.common.entity.FlexAttributeManager;
import com.scplatform.pcm.functionalGroup.entity.FunctionalGroup;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.entity.ItemAlternate;
import com.scplatform.pcm.platform.entity.Platform;
import com.scplatform.pcm.item.repository.ItemRepository;
import com.scplatform.pcm.util.datetime.ISO8601;
import com.scplatform.pcm.util.message.SCPlatformMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

import com.scplatform.pcm.common.entity.VersionRevision;
import com.scplatform.pcm.item.entity.ItemCategory;

/**
 * Service class for Item entity business logic and transformations.
 * Handles non-entity operations like serialization, audit messages, and derived data.
 */
@Service
@RequiredArgsConstructor
public class ItemService {

	private final ItemRepository itemRepository;
	private final AvlRepository avlRepository;

	/**
	 * Repository-backed item load by key.
	 * Maps to: BomUtil.getItem(Long itemKey)
	 */
	public Item getItem(Long itemKey) {
		return itemRepository.findById(itemKey).orElse(null);
	}

	/**
	 * Repository-backed find item by number and business entity.
	 * Maps to: BomUtil.ItemUtil.findItemByNumber(String, String, String, BusinessEntity)
	 */
	public Item findItemByNumber(String itemNumber, String itemId, String itemType, 
			BusinessEntity businessEntity) {
		return itemRepository.findByItemNumberAndBusinessEntity(itemNumber, itemId, itemType, businessEntity)
				.orElse(null);
	}

	/**
	 * Repository-backed find item by natural key.
	 * Maps to: BomUtil.ItemUtil.findItemByNaturalKey(String, String, String, String, String, BusinessEntity)
	 */
	public Item findItemByNaturalKey(String itemNumber, String itemId, String version, String revision,
			String itemType, BusinessEntity businessEntity) {
		return itemRepository.findByItemNumberAndItemIdAndVersionAndRevisionAndItemTypeAndBusinessEntity(
				itemNumber, itemId, version, revision, itemType, businessEntity)
				.orElse(null);
	}

	/**
	 * Migrated from utility logic: creates AVL entry with optional supplier item creation.
	 * Maps to: BomUtil.ItemUtil.autoCreateAvl(Item, BusinessEntity, boolean)
	 */
	@Transactional
	public Avl autoCreateAvl(Item item, BusinessEntity supplier, boolean checkIfSupplierIsOnAvl) {
		if (item == null || supplier == null) {
			return null;
		}

		if (checkIfSupplierIsOnAvl && !avlRepository.findByItemAndSupplier(item, supplier).isEmpty()) {
			return null;
		}

		Item supplierItem = itemRepository
				.findByItemNumberAndBusinessEntity(item.getItemNumber(), null, Item.SUP_ITEM, supplier)
				.orElseGet(() -> {
					Item created = new Item();
					created.setItemNumber(item.getItemNumber());
					created.setDescription(item.getDescription());
					created.setItemType(Item.SUP_ITEM);
					created.setItemId(item.getItemId());
					created.setItemVersion(new VersionRevision(item.getRevision(), item.getVersion()));
					created.setBusinessEntity(supplier);
					created.setCurrentFlag(false);
					created.setDataSource(item.getDataSource() == null ? "B2B" : item.getDataSource());
					return itemRepository.save(created);
				});

		Avl newAvl = new Avl();
		newAvl.setItem(item);
		newAvl.setSupplier(supplier);
		newAvl.setSupplierItem(supplierItem);
		newAvl.setDescription("AutoCreated");
		newAvl.setCurrentFlag(true);
		item.addAvl(newAvl);
		itemRepository.save(item);
		return newAvl;
	}

	/**
	 * Get audit title for item from messages property file
	 * 
	 * @param item the item entity
	 * @return formatted audit title
	 */
	public String getAuditTitle(Item item) {
		if (item == null) {
			return "";
		}
		List<Object> args = new ArrayList<>();
		args.add(item.getItemNumber());
		if (item.getBusinessEntity() != null) {
			args.add(item.getBusinessEntity().getBusinessEntityName());
		}
		return SCPlatformMessages.INSTANCE.getAuditMessage("audit.itemTitle", args.toArray(), null);
	}

	/**
	 * Get natural key for item as JSON
	 * 
	 * @param item the item entity
	 * @return ObjectNode with natural key fields
	 */
	public ObjectNode getItemNaturalKeyAsJSON(Item item) {
		if (item == null) {
			return null;
		}
		ObjectMapper om = new ObjectMapper();
		ObjectNode o = om.createObjectNode();
		o.put("itemKey", item.getItemKey());
		o.put("itemIdentifier", item.getItemNumber());
		o.put("description", item.getDescription());
		if (item.getBusinessEntity() != null) {
			ObjectNode businessEntityAsJSON = item.getBusinessEntity().getNaturalKeyAsJSON();
			o.set("business", businessEntityAsJSON);
		} else {
			o.putNull("business");
		}
		o.put("revision", item.getItemVersion().getRevision());
		o.put("version", item.getItemVersion().getVersion());
		return o;
	}

	/**
	 * Get complete inline item natural key as JSON with all details
	 * 
	 * @param item the item entity
	 * @return ObjectNode with all item details
	 * @throws ParseException if date parsing fails
	 */
	public ObjectNode getInlineItemNaturalKeyAsJSON(Item item) throws ParseException {
		if (item == null) {
			return null;
		}
		ObjectMapper om = new ObjectMapper();
		ObjectNode o = om.createObjectNode();
		o.put("itemKey", item.getItemKey());
		o.put("itemIdentifier", item.getItemNumber());
		o.put("description", item.getDescription());
		o.put("itemType", item.getItemType());
		if (item.getBusinessEntity() != null) {
			ObjectNode businessEntityAsJSON = item.getBusinessEntity().getNaturalKeyAsJSON();
			o.set("business", businessEntityAsJSON);
		} else {
			o.putNull("business");
		}
		o.put("managedFlag", item.getManagedFlag());
		o.put("revision", item.getItemVersion().getRevision());
		o.put("version", item.getItemVersion().getVersion());
		o.put("isTopLevel", item.getIsTopLevel());
		o.put("itemClassification", item.getItemClassification());
		o.put("productFamily", item.getProductFamily());
		o.put("lifeCycleTypeCode", item.getLifeCycleTypeCode());
		o.put("lifeCycleTypeCodeOther", item.getLifeCycleTypeCodeOther());
		o.put("productUomCode", item.getProductUomCode());
		o.put("inventory", item.getInventory());
		String status = "ACTIVE";
		if(item.getEol() != null) {
			if(item.getEol()) {
				if(item.getEolType() != null && !item.getEolType().isEmpty()) {
					status = item.getEolType();
				}else {
					status = "INACTIVE";
				}
			}
		}
		o.put("eolState", status);
		o.put("insertDate", ISO8601.safeFormat(item.getInsertDate()));
		o.put("updateDate", ISO8601.safeFormat(item.getUpdateDate()));
		o.put("dataSource", om.getNodeFactory().pojoNode(item.getDataSource()));
		o.put("groupMembers", om.getNodeFactory().pojoNode(item.getGroupMembers()));
		ArrayNode an = o.putArray("flex");
		getFlexAttributeAsJson(item, an);
		ArrayNode ct = o.putArray("categories");
		getCategoriesAsJson(item, ct);
		ArrayNode fgs = o.putArray("functionalGroups");
		getFgAsJson(item, fgs);
		ArrayNode itemAssignments = o.putArray("assignment");
		getAssignmentsAsJson(item, itemAssignments);
		ArrayNode platforms = o.putArray("platforms");
		getPlatformsAsJson(item, platforms);
		ArrayNode attributes = o.putArray("attributes");
		getAttributesAsJson(item, attributes);
		ArrayNode amls = o.putArray("amls");
		getAmlsAsJson(item, amls);
		ArrayNode avls = o.putArray("avls");
		getAvlsAsJson(item, avls);
		ArrayNode alternates = o.putArray("alternates");
		getAlternatesAsJson(item, alternates);
		ArrayNode wu = o.putArray("whereUsed");
		getWhereUsedAsJson(item, wu);
		return o;
	}

	/**
	 * Get unique set of manufacturers from item's AMLs
	 * 
	 * @param item the item entity
	 * @return unmodifiable set of manufacturers
	 */
	public Set<BusinessEntity> getManufacturers(Item item) {
		if (item == null) {
			return Collections.emptySet();
		}
		HashSet<BusinessEntity> result = new HashSet<>();
		for (Aml aml : item.getAmls()) {
			result.add(aml.getMfg());
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Get unique set of suppliers from item's AVLs
	 * 
	 * @param item the item entity
	 * @return unmodifiable sorted set of suppliers
	 */
	public Set<BusinessEntity> getSuppliers(Item item) {
		if (item == null) {
			return Collections.emptySet();
		}
		Set<BusinessEntity> result = new TreeSet<>(new BusinessEntity.NameSorter());
		for (Avl avl : item.getAvls()) {
			result.add(avl.getSupplier());
		}
		return Collections.unmodifiableSet(result);
	}

	/**
	 * Get derived managed flag from item or its categories
	 * 
	 * @param item the item entity
	 * @return managed flag value or null
	 */
	public String getDerivedManagedFlag(Item item) {
		if (item == null) {
			return null;
		}
		if (item.getManagedFlag() != null) {
			return item.getManagedFlag();
		}
		for (ItemCategory ic : item.getCategories()) {
			if (ic.getManagedFlag() != null) {
				return ic.getManagedFlag();
			}
		}
		return null;
	}

	/**
	 * Get comma-separated item category names
	 * 
	 * @param item the item entity
	 * @return category names formatted as comma-separated string
	 */
	public String getItemCategoryNames(Item item) {
		if (item == null) {
			return "";
		}
		StringBuilder icName = new StringBuilder();
		for (ItemCategory ic : item.getCategories()) {
			if (icName.length() > 0) {
				icName.append(", ");
			}
			icName.append(ic.getCategoryName());
		}
		return icName.toString();
	}

	/**
	 * Determine item type based on business entity type
	 * 
	 * @param be the business entity
	 * @param item the item entity
	 * @return item type code (ITEM, MFG_ITEM, SUP_ITEM, or PHANTOM_ITEM)
	 */
	public static String determineItemType(BusinessEntity be, Item item) {
		if (be.getBusinessEntityTypeKey() == BusinessEntity.MFG_TYPE) {
			return Item.MFG_ITEM;
		} else if (be.getBusinessEntityTypeKey() == BusinessEntity.SUPPLIER_TYPE) {
			if(item.getItemType().equalsIgnoreCase(Item.PHANTOM_ITEM))
				return Item.PHANTOM_ITEM;
			else
				return Item.SUP_ITEM;
		} else {
			return Item.ITEM;
		}
	}

	/**
	 * Get flex attribute definitions for item
	 * 
	 * @return list of FlexAttributeDefn for items
	 */
	public List<FlexAttributeDefn> getFlexAttributeDefinitions() {
		return FlexAttributeManager.ITEM.getFlexAttributeDefinitionList();
	}

	// Private helper methods for JSON serialization
	private void getWhereUsedAsJson(Item item, ArrayNode wu) {
		for (Object fx : item.getWhereUsedSet()) {
			BomLine bomLine = (BomLine)fx;
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode on = mapper.createObjectNode();
			on.put("businessEntityName", bomLine.getItem().getBusinessEntity().getBusinessEntityName());
			on.put("name", bomLine.getBom().getBomName());
			on.put("status", bomLine.getBom().getStatus());
		    on.put("description", bomLine.getBom().getBomDesc());
			wu.add(on);
		}
	}
	
	private void getFlexAttributeAsJson(Item item, ArrayNode an) {
		for (FlexAttributeDefn fx : this.getFlexAttributeDefinitions()) {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode on = mapper.createObjectNode();
			on.put(fx.getName(),
					mapper.getNodeFactory().pojoNode(item.getFlexAttribute(fx.getAssociatedAttribute())));
			an.add(on);
		}
	}

	private void getCategoriesAsJson(Item item, ArrayNode ct) {
		for (ItemCategory category : item.getCategories()) {
			ct.add(category.getCategoriesNaturalKeyAsJSON());
		}
	}

	private void getFgAsJson(Item item, ArrayNode functionalGroups) {
		for (FunctionalGroup fg : item.getFunctionalGroups()) {
			functionalGroups.add(fg.getFgNaturalKeyAsJSON());
		}
	}

	private void getAssignmentsAsJson(Item item, ArrayNode itemAssignments) {
		for (ItemAssignment is : item.getAssignments()) {
			itemAssignments.add(is.getAssignmentsNaturalKeyAsJSON());
		}
	}

	private void getPlatformsAsJson(Item item, ArrayNode platforms) {
		for (Platform pf : item.getPlatforms()) {
			platforms.add(pf.getPlatformsNaturalKeyAsJSON());
		}
	}

	private void getAttributesAsJson(Item item, ArrayNode attributes) {
		List<Attribute> attributeList = new ArrayList<>(item.getAttributes());
		attributeList.sort(Comparator.comparing(Attribute::getAttrName));
		for (Attribute ab : attributeList) {
		    attributes.add(ab.getAttributesNaturalKeyAsJSON());
		}
	}
	
	private void getAmlsAsJson(Item item, ArrayNode amls) {
		for (Aml aml : item.getAmls()) {
			amls.add(aml.getAmlsNaturalKeyAsJSON());
		}
	}

	private void getAvlsAsJson(Item item, ArrayNode avls) {
		for (Avl avl : item.getAvls()) {
			avls.add(avl.getAvlsNaturalKeyAsJSON());
		}
	}

	private void getAlternatesAsJson(Item item, ArrayNode alternates) {
		for (ItemAlternate alt : item.getAlternates()) {
			alternates.add(alt.getAlternatesNaturalKeyAsJSON());
		}
	}

	/**
	 * Count distinct data sources matching the given value.
	 * Maps to Hibernate Criteria API code with distinct projection and rowCount.
	 * 
	 * @param companyItemType the data source value to search for
	 * @return count of distinct data sources
	 */
	public long countDistinctDataSourceByType(String companyItemType) {
		if (companyItemType == null) {
			return 0L;
		}
		return itemRepository.countDistinctDataSourceByType(companyItemType);
	}

	/**
	 * Count total items by data source.
	 * Alternative method that returns the actual count of matching records.
	 * 
	 * @param companyItemType the data source value to search for
	 * @return count of rows matching the criteria
	 */
	public long countItemByDataSource(String companyItemType) {
		if (companyItemType == null) {
			return 0L;
		}
		return itemRepository.countItemByDataSource(companyItemType);
	}

	/**
	 * Find distinct data sources matching the given value.
	 * Maps to Hibernate Criteria API code with distinct projection.
	 * 
	 * @param companyItemType the data source value to search for
	 * @return list of distinct data sources
	 */
	public List<String> findDistinctDataSources(String companyItemType) {
		if (companyItemType == null) {
			return Collections.emptyList();
		}
		return itemRepository.findDistinctDataSources(companyItemType);
	}

	@Transactional(readOnly = true)
	public List<Long> getItemKeysByItemNumbers(List<String> itemNumbers) {
		if (itemNumbers == null || itemNumbers.isEmpty()) {
			return Collections.emptyList();
		}
		return itemRepository.findItemKeysByItemNumbers(itemNumbers);
	}

	@Transactional(readOnly = true)
	public List<Object[]> getNewUnassignedItems(java.util.Date cutoffDate) {
		return itemRepository.findNewUnassignedItems(cutoffDate);
	}

	@Transactional(readOnly = true)
	public List<Object[]> getForecastStatus(List<String> status, java.util.Date cutoffDate) {
		return itemRepository.findForecastStatus(status, cutoffDate);
	}

	@Transactional(readOnly = true)
	public List<Object[]> getForecastAdjStatus(List<String> status, java.util.Date cutoffDate) {
		return itemRepository.findForecastAdjStatus(status, cutoffDate);
	}

    public void updateCost(String itemCode, java.math.BigDecimal newCost, String reason, String updatedBy) {
        // TODO: implement cost update
    }
}