package com.umcsuser.carrent.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class VehicleCategoryConfigService {

    private List<Map<String, Object>> categories = new ArrayList<>();

    @PostConstruct
    public void init() {
        Map<String, Object> carCategory = new HashMap<>();
        carCategory.put("category", "Car");

        Map<String, String> carAttributes = new HashMap<>();
        carAttributes.put("fuelType", "string");
        carCategory.put("attributes", carAttributes);

        categories.add(carCategory);
    }

    public Map<String, Object> getByCategory(String category) {
        return categories.stream()
                .filter(c -> c.get("category").equals(category))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nieznana kategoria pojazdu: " + category));
    }
}