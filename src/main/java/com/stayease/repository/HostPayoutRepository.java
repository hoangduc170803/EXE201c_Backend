package com.stayease.repository;

import com.stayease.enums.HostPayoutStatus;
import com.stayease.model.HostPayout;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HostPayoutRepository extends JpaRepository<HostPayout, Long> {

    Page<HostPayout> findByStatus(HostPayoutStatus status, Pageable pageable);

    @Query("select p from HostPayout p where p.host.id = :hostId order by p.createdAt desc")
    Page<HostPayout> findByHostId(@Param("hostId") Long hostId, Pageable pageable);
}

