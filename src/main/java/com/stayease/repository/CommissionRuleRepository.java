package com.stayease.repository;

import com.stayease.model.CommissionRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRuleRepository extends JpaRepository<CommissionRule, Long> {

    List<CommissionRule> findAllByOrderByEffectiveFromDesc();

    @Query("""
            select r
            from CommissionRule r
            where r.isDeleted = false
              and r.isActive = true
              and r.effectiveFrom <= :at
              and (r.effectiveTo is null or r.effectiveTo > :at)
            order by r.effectiveFrom desc
            """)
    Optional<CommissionRule> findActiveAt(@Param("at") LocalDateTime at);
}

