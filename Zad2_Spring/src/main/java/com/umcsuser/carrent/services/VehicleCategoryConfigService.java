package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.VehicleCategoryConfig;
import com.umcsuser.carrent.repositories.VehicleCategoryConfigRepository;

import java.util.List;

public class VehicleCategoryConfigService implements IVehicleCategoryConfigService {
    private final VehicleCategoryConfigRepository configRepository;

    public VehicleCategoryConfigService(VehicleCategoryConfigRepository configRepository) {
        this.configRepository = configRepository;
    }

    @Override
    public List<VehicleCategoryConfig> findAllCategories() {
        return configRepository.findAll();
    }

    @Override
    public VehicleCategoryConfig getByCategory(String category) {
        return configRepository.findByCategory(category)
                .orElseThrow(() -> new IllegalArgumentException("Nieznana kategoria pojazdu: " + category));
    }
}