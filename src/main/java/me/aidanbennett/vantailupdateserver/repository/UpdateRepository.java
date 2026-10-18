package me.aidanbennett.vantailupdateserver.repository;

import me.aidanbennett.vantailupdateserver.entity.UpdateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UpdateRepository extends JpaRepository<UpdateEntity, Long> {
    Optional<UpdateEntity> findTopByOrderByPubDateDesc();

    @Modifying
    @Query("update UpdateEntity u set u.requestCount = u.requestCount + 1 where u.id = :id")
    int incrementRequestCount(@Param("id") long id);
}
