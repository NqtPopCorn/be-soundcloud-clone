package com.popcorn.soundcloudclone.features.monitor.repository;

import com.popcorn.soundcloudclone.features.monitor.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    @Query("SELECT s FROM SystemLog s WHERE " +
           "(:level IS NULL OR s.level = :level) AND " +
           "(:keyword IS NULL OR LOWER(s.message) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.exception) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SystemLog> findByFilter(@Param("level") String level, @Param("keyword") String keyword, Pageable pageable);

    void deleteByTimestampBefore(java.time.LocalDateTime timestamp);
}
