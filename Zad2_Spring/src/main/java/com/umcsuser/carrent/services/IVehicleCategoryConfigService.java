package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.VehicleCategoryConfig;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IVehicleCategoryConfigService {
    List<VehicleCategoryConfig> findAllCategories();

    VehicleCategoryConfig getByCategory(String category);
}
