/**
 * Copyright (c) 2026 Kumara Swamy — github.com/bkumars22
 * Supply Chain Intelligence Platform
 * Licensed under MIT License — see LICENSE file for details
 */
package com.scplatform.api.controller;

import com.scplatform.pcm.avl.entity.Avl;
import com.scplatform.pcm.item.entity.Item;
import com.scplatform.api.service.ItemManagementService;
import com.scplatform.api.service.ItemManagementService.AvlCreateRequest;
import com.scplatform.api.service.ItemManagementService.ItemCreateRequest;
import com.scplatform.api.service.ItemManagementService.UploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Items", description = "Create and manage items and approved vendor lists")
@SecurityRequirement(name = "Bearer Auth")
public class ItemRestController {

    private final ItemManagementService itemManagementService;

    public ItemRestController(ItemManagementService itemManagementService) {
        this.itemManagementService = itemManagementService;
    }

    @GetMapping
    @Operation(summary = "Search items by number or list all items")
    public ResponseEntity<List<ItemSummary>> listItems(
            @RequestParam(required = false) String itemNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(size, 100);
        List<ItemSummary> result = itemManagementService.searchItems(itemNumber, PageRequest.of(page, safeSize, Sort.by("itemKey").descending()))
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{itemKey}")
    @Operation(summary = "Get a single item by key")
    public ResponseEntity<ItemSummary> getItem(@PathVariable Long itemKey) {
        Item item = itemManagementService.getItem(itemKey);
        if (item == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toSummary(item));
    }

    @PostMapping
    @Operation(summary = "Create an item")
    public ResponseEntity<ItemSummary> createItem(@RequestBody ItemCreateRequest request) {
        Item item = itemManagementService.createItem(request);
        return ResponseEntity.ok(toSummary(item));
    }

    @PostMapping("/avls")
    @Operation(summary = "Create an AVL entry for an item")
    public ResponseEntity<AvlSummary> createAvl(@RequestBody AvlCreateRequest request) {
        Avl avl = itemManagementService.createAvl(request);
        return ResponseEntity.ok(toSummary(avl));
    }

    @GetMapping("/{itemKey}/avls")
    @Operation(summary = "List AVL entries for an item")
    public ResponseEntity<List<AvlSummary>> getAvls(@PathVariable Long itemKey) {
        List<AvlSummary> result = itemManagementService.getAvlsForItem(itemKey)
                .stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an XML file containing items and AVL definitions")
    public ResponseEntity<UploadResponse> uploadXml(@RequestParam("file") MultipartFile file) {
        UploadResponse response = itemManagementService.uploadItemsAndAvls(file);
        return ResponseEntity.ok(response);
    }

    private ItemSummary toSummary(Item item) {
        return new ItemSummary(
                item.getItemKey(),
                item.getItemNumber(),
                item.getItemId(),
                item.getItemType(),
                item.getDescription(),
                item.getItemVersion() != null ? item.getItemVersion().getRevision() : null,
                item.getItemVersion() != null ? item.getItemVersion().getVersion() : null,
                item.getBusinessEntity() != null ? item.getBusinessEntity().getBusinessEntityName() : null,
                item.getDataSource()
        );
    }

    private AvlSummary toSummary(Avl avl) {
        return new AvlSummary(
                avl.getAvlKey(),
                avl.getItem() != null ? avl.getItem().getItemKey() : null,
                avl.getItem() != null ? avl.getItem().getItemNumber() : null,
                avl.getSupplier() != null ? avl.getSupplier().getBusinessEntityKey() : null,
                avl.getSupplier() != null ? avl.getSupplier().getBusinessEntityName() : null,
                avl.getSupplierItem() != null ? avl.getSupplierItem().getItemKey() : null,
                avl.getSupplierItem() != null ? avl.getSupplierItem().getItemNumber() : null,
                avl.getDescription(),
                avl.getCurrentFlag()
        );
    }

    record ItemSummary(
            Long itemKey,
            String itemNumber,
            String itemId,
            String itemType,
            String description,
            String revision,
            String version,
            String businessEntityName,
            String dataSource) {
    }

    record AvlSummary(
            Long avlKey,
            Long itemKey,
            String itemNumber,
            Long supplierKey,
            String supplierName,
            Long supplierItemKey,
            String supplierItemNumber,
            String description,
            Boolean currentFlag) {
    }
}
