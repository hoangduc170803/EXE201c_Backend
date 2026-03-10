package com.stayease.repository;

import com.stayease.model.PostingPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostingPackageRepository extends JpaRepository<PostingPackage, Long> {

    Optional<PostingPackage> findBySlug(String slug);

    List<PostingPackage> findAllByIsActiveTrueOrderByPriorityLevelDesc();

    List<PostingPackage> findAllByOrderByPriorityLevelDesc();
}

