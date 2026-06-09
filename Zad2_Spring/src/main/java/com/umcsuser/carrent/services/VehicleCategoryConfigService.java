package com.umcsuser.carrent.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umcsuser.carrent.models.VehicleCategoryConfig;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VehicleCategoryConfigService implements IVehicleCategoryConfigService {

    private List<Map<String, Object>> categories = new ArrayList<>();
    private ObjectMapper objectMapper = new ObjectMapper(); // Magiczne narzędzie do konwersji

    @PostConstruct
    public void init() {
        Map<String, Object> carCategory = new HashMap<>();
        carCategory.put("category", "Car");

        Map<String, String> carAttributes = new HashMap<>();
        carAttributes.put("fuelType", "string");
        carCategory.put("attributes", carAttributes);

        categories.add(carCategory);
    }

    @Override
    public List<VehicleCategoryConfig> findAllCategories() {
        List<VehicleCategoryConfig> resultList = new ArrayList<>();
        for (Map<String, Object> map : categories) {
            resultList.add(objectMapper.convertValue(map, VehicleCategoryConfig.class));
        }
        return resultList;
    }

    @Override
    public VehicleCategoryConfig getByCategory(String category) {
        Map<String, Object> foundMap = categories.stream()
                .filter(c -> c.get("category").equals(category))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nieznana kategoria pojazdu: " + category));

        return objectMapper.convertValue(foundMap, VehicleCategoryConfig.class);
    }
}