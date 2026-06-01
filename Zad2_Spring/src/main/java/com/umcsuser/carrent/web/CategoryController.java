package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.services.IVehicleCategoryConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final IVehicleCategoryConfigService categoryService;

    public CategoryController(IVehicleCategoryConfigService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<VehicleCategoryConfig> list() {
        return categoryService.findAllCategories();
    }

    @GetMapping("/{category}")
    public VehicleCategoryConfig get(@PathVariable String category) {
        return categoryService.getByCategory(category);
    }
}