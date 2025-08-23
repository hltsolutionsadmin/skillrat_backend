    package com.hlt.skillrat.repository;

import com.hlt.skillrat.model.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

    @Repository
    public interface NotificationRepository extends JpaRepository<NotificationModel, Long> {
        Page<NotificationModel> findByUserIdOrderByCreationTimeDesc(Long user, Pageable pageable);

        void deleteAllByUserId(Long userId);
    }
