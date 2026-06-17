/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.service;

import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.avl.repository.AvlRepository;
import com.scplatform.pcm.businessEntity.entity.BusinessEntity;
import com.scplatform.pcm.businessEntity.repository.BusinessEntityRepository;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.pcm.item.repository.ItemRepository;
import com.scplatform.pcm.item.service.ItemService;
import com.scplatform.pcm.common.entity.VersionRevision;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemManagementService {

    private final ItemRepository itemRepository;
    private final AvlRepository avlRepository;
    private final BusinessEntityRepository businessEntityRepository;
    private final ItemService itemService;

    @Transactional
    public Item createItem(ItemCreateRequest request) {
        if (request.itemNumber() == null || request.itemNumber().isBlank()) {
            throw new IllegalArgumentException("itemNumber is required");
        }

        BusinessEntity businessEntity = resolveBusinessEntity(
                request.businessEntityIdentifier(),
                request.businessEntityName(),
                request.businessEntityTypeName());
        if (businessEntity == null) {
            throw new IllegalArgumentException("Business entity identifier or name is required");
        }

        Item existing = itemService.findItemByNumber(
                request.itemNumber(),
                request.itemId(),
                request.itemType(),
                businessEntity);
        if (existing != null) {
            return existing;
        }

        Item item = new Item();
        item.setItemNumber(request.itemNumber());
        item.setItemId(request.itemId());
        item.setItemType(request.itemType() != null ? request.itemType() : Item.ITEM);
        item.setDescription(request.description());
        item.setBusinessEntity(businessEntity);
        item.setItemVersion(new VersionRevision(request.revision(), request.version()));
        item.setCurrentFlag(true);
        item.setDataSource(request.dataSource() != null ? request.dataSource() : "REST");
        return itemRepository.save(item);
    }

    @Transactional
    public Avl createAvl(AvlCreateRequest request) {
        Item item = resolveItem(request.itemKey(), request.itemNumber(), request.itemType(), request.itemId());
        if (item == null) {
            throw new IllegalArgumentException("Item key or item natural key is required");
        }

        BusinessEntity supplier = resolveBusinessEntity(
                request.supplierIdentifier(),
                request.supplierName(),
                request.supplierTypeName());
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier identifier or name is required");
        }

        Avl avl = itemService.autoCreateAvl(item, supplier, true);
        if (avl == null) {
            throw new IllegalArgumentException("AVL already exists for this item and supplier");
        }
        if (request.description() != null && !request.description().isBlank()) {
            avl.setDescription(request.description());
            avlRepository.save(avl);
        }
        return avl;
    }

    public Item getItem(Long itemKey) {
        return itemRepository.findById(itemKey).orElse(null);
    }

    public List<Item> searchItems(String itemNumber, Pageable pageable) {
        if (itemNumber != null && !itemNumber.isBlank()) {
            return itemRepository.findByItemNumberContainingIgnoreCase(itemNumber, pageable).getContent();
        }
        return itemRepository.findAll(pageable).getContent();
    }

    public List<Avl> getAvlsForItem(Long itemKey) {
        Item item = itemService.getItem(itemKey);
        if (item == null) {
            return List.of();
        }
        List<Avl> avls = item.getAvls() != null ? new ArrayList<>(item.getAvls()) : List.of();
        return avls;
    }

    @Transactional
    public UploadResponse uploadItemsAndAvls(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Upload file is required");
        }

        Document document = parseXml(file);
        document.getDocumentElement().normalize();

        int itemsProcessed = 0;
        int itemsCreated = 0;
        int avlsProcessed = 0;
        int avlsCreated = 0;
        List<String> errors = new ArrayList<>();

        NodeList itemNodes = document.getElementsByTagName("item");
        for (int i = 0; i < itemNodes.getLength(); i++) {
            Node node = itemNodes.item(i);
            if (!(node instanceof Element element)) {
                continue;
            }
            itemsProcessed++;
            try {
                ItemCreateRequest request = buildItemRequest(element);
                Item item = createItem(request);
                if (item != null) {
                    itemsCreated++;
                }
            } catch (Exception ex) {
                errors.add("Item upload row " + (i + 1) + ": " + ex.getMessage());
            }
        }

        NodeList avlNodes = document.getElementsByTagName("avl");
        for (int i = 0; i < avlNodes.getLength(); i++) {
            Node node = avlNodes.item(i);
            if (!(node instanceof Element element)) {
                continue;
            }
            avlsProcessed++;
            try {
                AvlCreateRequest request = buildAvlRequest(element);
                Avl avl = createAvl(request);
                if (avl != null) {
                    avlsCreated++;
                }
            } catch (Exception ex) {
                errors.add("AVL upload row " + (i + 1) + ": " + ex.getMessage());
            }
        }

        return new UploadResponse(itemsProcessed, itemsCreated, avlsProcessed, avlsCreated, errors);
    }

    private Document parseXml(MultipartFile file) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file.getInputStream());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new IllegalArgumentException("Invalid XML upload", e);
        }
    }

    private ItemCreateRequest buildItemRequest(Element element) {
        return new ItemCreateRequest(
                getChildText(element, "itemNumber"),
                getChildText(element, "itemId"),
                getChildText(element, "itemType"),
                getChildText(element, "description"),
                getChildText(element, "revision"),
                getChildText(element, "version"),
                getChildText(element, "businessEntityIdentifier"),
                getChildText(element, "businessEntityName"),
                getChildText(element, "businessEntityTypeName"),
                getChildText(element, "dataSource")
        );
    }

    private AvlCreateRequest buildAvlRequest(Element element) {
        Long itemKey = null;
        String itemKeyText = getChildText(element, "itemKey");
        if (itemKeyText != null && !itemKeyText.isBlank()) {
            try {
                itemKey = Long.parseLong(itemKeyText.trim());
            } catch (NumberFormatException ignored) {
            }
        }

        return new AvlCreateRequest(
                itemKey,
                getChildText(element, "itemNumber"),
                getChildText(element, "itemType"),
                getChildText(element, "itemId"),
                getChildText(element, "supplierIdentifier"),
                getChildText(element, "supplierName"),
                getChildText(element, "supplierTypeName"),
                getChildText(element, "description")
        );
    }

    private String getChildText(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        String text = nodes.item(0).getTextContent();
        return text != null ? text.trim() : null;
    }

    private Item resolveItem(Long itemKey, String itemNumber, String itemType, String itemId) {
        if (itemKey != null) {
            return itemService.getItem(itemKey);
        }
        if (itemNumber == null || itemNumber.isBlank()) {
            return null;
        }
        List<Item> candidates = itemRepository.findByItemNumberAndType(
                itemNumber,
                itemType != null ? itemType : Item.ITEM);
        if (!candidates.isEmpty()) {
            if (itemId == null || itemId.isBlank()) {
                return candidates.get(0);
            }
            return candidates.stream()
                    .filter(item -> itemId.equals(item.getItemId()))
                    .findFirst()
                    .orElse(candidates.get(0));
        }
        return null;
    }

    private BusinessEntity resolveBusinessEntity(String identifier, String name, String typeName) {
        if (identifier != null && !identifier.isBlank()) {
            if (typeName != null && !typeName.isBlank()) {
                Optional<BusinessEntity> byId = businessEntityRepository.findBusinessById(identifier, typeName)
                        .stream().findFirst();
                if (byId.isPresent()) {
                    return byId.get();
                }
            } else {
                if (identifier.matches("\\d+")) {
                    Optional<BusinessEntity> byPk = businessEntityRepository.findById(Long.parseLong(identifier));
                    if (byPk.isPresent()) {
                        return byPk.get();
                    }
                }
                Optional<BusinessEntity> byExternal = businessEntityRepository.findBusinessByExternalRefId(identifier);
                if (byExternal.isPresent()) {
                    return byExternal.get();
                }
                Optional<BusinessEntity> byId = businessEntityRepository.findBusinessById(identifier, null).stream().findFirst();
                if (byId.isPresent()) {
                    return byId.get();
                }
            }
        }

        if (name != null && !name.isBlank()) {
            if (typeName != null && !typeName.isBlank()) {
                Optional<BusinessEntity> unique = businessEntityRepository.findUniqueBusinessByName(name, typeName);
                if (unique.isPresent()) {
                    return unique.get();
                }
            }
            List<BusinessEntity> byName = businessEntityRepository.findBusinessByName(name, typeName);
            if (!byName.isEmpty()) {
                return byName.get(0);
            }
        }

        return null;
    }

    public record ItemCreateRequest(
            String itemNumber,
            String itemId,
            String itemType,
            String description,
            String revision,
            String version,
            String businessEntityIdentifier,
            String businessEntityName,
            String businessEntityTypeName,
            String dataSource) {
    }

    public record AvlCreateRequest(
            Long itemKey,
            String itemNumber,
            String itemType,
            String itemId,
            String supplierIdentifier,
            String supplierName,
            String supplierTypeName,
            String description) {
    }

    public record UploadResponse(
            int itemsProcessed,
            int itemsCreated,
            int avlsProcessed,
            int avlsCreated,
            List<String> errors) {
    }
}
