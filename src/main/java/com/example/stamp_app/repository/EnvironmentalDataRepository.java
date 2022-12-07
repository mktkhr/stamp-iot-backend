package com.example.stamp_app.repository;

import com.example.stamp_app.dummyData.EnvironmentalData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EnvironmentalDataRepository extends JpaRepository<EnvironmentalData, UUID> {
}
