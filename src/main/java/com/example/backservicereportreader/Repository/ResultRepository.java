package com.example.backservicereportreader.Repository;

import com.example.backservicereportreader.entity.AiResult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface ResultRepository extends JpaRepository<AiResult, UUID> {

    List<AiResult> findAllByUserId(UUID userId);
}
