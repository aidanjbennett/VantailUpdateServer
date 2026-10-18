package me.aidanbennett.vantailupdateserver.service;

import me.aidanbennett.vantailupdateserver.dto.update.UpdateResponse;
import me.aidanbennett.vantailupdateserver.entity.UpdateEntity;
import me.aidanbennett.vantailupdateserver.repository.UpdateRepository;
import me.aidanbennett.vantailupdateserver.exception.UpdateNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;

@Service
public class UpdateService {

    private final UpdateRepository updateRepository;

    public UpdateService(UpdateRepository updateRepository) {
        this.updateRepository = updateRepository;
    }

    @Transactional
    public UpdateResponse getLatestUpdate() {
        UpdateEntity latest = updateRepository.findTopByOrderByPubDateDesc()
                .orElseThrow(() -> new UpdateNotFoundException("No updates found"));
        return getAndCount(latest.getId());
    }

    @Transactional
    public UpdateResponse getUpdateById(long id) {
        return getAndCount(id);
    }

    public Page<UpdateResponse> getAllUpdates(int page) {
        return updateRepository.findAll(PageRequest.of(
                        page,
                        30,
                        Sort.by(Sort.Direction.DESC, "pubDate")
                ))
                .map(this::toResponse);
    }

    private UpdateResponse getAndCount(long id) {
        if (updateRepository.incrementRequestCount(id) == 0) {
            throw new UpdateNotFoundException("Update not found: " + id);
        }
        return updateRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new UpdateNotFoundException("Update not found: " + id));
    }

    private UpdateResponse toResponse(UpdateEntity update) {
        return new UpdateResponse(
                update.getVersion(),
                update.getNotes(),
                update.getPubDate(),
                update.getPlatforms().entrySet().stream().collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> new UpdateResponse.PlatformDownload(
                                entry.getValue().getUrl(), entry.getValue().getSignature()),
                        (left, right) -> left,
                        java.util.LinkedHashMap::new
                ))
        );
    }
}
