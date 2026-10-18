package me.aidanbennett.vantailupdateserver.service;

import me.aidanbennett.vantailupdateserver.dto.update.UpdateStatsResponse;
import me.aidanbennett.vantailupdateserver.entity.UpdateEntity;
import me.aidanbennett.vantailupdateserver.exception.UpdateNotFoundException;
import me.aidanbennett.vantailupdateserver.repository.UpdateRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
public class UpdateStatsService {

    private final UpdateRepository updateRepository;

    public UpdateStatsService(UpdateRepository updateRepository) {
        this.updateRepository = updateRepository;
    }

    public UpdateStatsResponse getStats() {
        LinkedHashMap<String, Long> requestsByVersion = new LinkedHashMap<>();
        long totalRequests = 0;

        for (UpdateEntity update : updateRepository.findAll()) {
            requestsByVersion.put(update.getVersion(), update.getRequestCount());
            totalRequests += update.getRequestCount();
        }

        return new UpdateStatsResponse(totalRequests, requestsByVersion);
    }

    public long getStatsForUpdate(long id) {
        return updateRepository.findById(id)
                .map(UpdateEntity::getRequestCount)
                .orElseThrow(() -> new UpdateNotFoundException("Update not found: " + id));
    }
}
