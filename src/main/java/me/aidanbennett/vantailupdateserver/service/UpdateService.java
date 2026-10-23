package me.aidanbennett.vantailupdateserver.service;

import me.aidanbennett.vantailupdateserver.dto.update.UpdateResponse;
import me.aidanbennett.vantailupdateserver.entity.UpdateEntity;
import me.aidanbennett.vantailupdateserver.repository.UpdateRepository;
import me.aidanbennett.vantailupdateserver.exception.UpdateNotFoundException;
import me.aidanbennett.vantailupdateserver.exception.UpdateAlreadyExistsException;
import me.aidanbennett.vantailupdateserver.dto.update.UpdateRequest;
import me.aidanbennett.vantailupdateserver.entity.PlatformDownload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

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
    public UpdateResponse getLatestUpdateForClient(String target, String arch, String currentVersion) {
        String platform = target + "-" + arch;
        UpdateEntity latestForPlatform = updateRepository.findAll(
                        Sort.by(Sort.Direction.DESC, "pubDate")
                ).stream()
                .filter(update -> update.getPlatforms().containsKey(platform))
                .findFirst()
                .orElseThrow(() -> new UpdateNotFoundException(
                        "No update found for platform: " + platform));

        if (updateRepository.incrementRequestCount(latestForPlatform.getId()) == 0) {
            throw new UpdateNotFoundException("Update not found: " + latestForPlatform.getId());
        }

        UpdateEntity countedUpdate = updateRepository.findById(latestForPlatform.getId())
                .orElseThrow(() -> new UpdateNotFoundException("Update not found: " + latestForPlatform.getId()));
        return toResponse(countedUpdate, platform);
    }

    @Transactional
    public UpdateResponse createUpdate(UpdateRequest request) {
        if (updateRepository.existsByVersion(request.version())) {
            throw new UpdateAlreadyExistsException("Update version already exists: " + request.version());
        }

        UpdateEntity update = new UpdateEntity();

        update.setVersion(request.version());
        update.setNotes(request.notes());
        update.setPubDate(request.pubDate());
        update.setPlatforms(request.platforms().entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey(),
                entry -> new PlatformDownload(entry.getValue().url(), entry.getValue().signature()),
                (left, right) -> left,
                LinkedHashMap::new
        )));

        return toResponse(updateRepository.save(update));
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
        return toResponse(update, null);
    }

    private UpdateResponse toResponse(UpdateEntity update, String platform) {
        return new UpdateResponse(
                update.getVersion(),
                update.getNotes(),
                update.getPubDate(),
                update.getPlatforms().entrySet().stream()
                        .filter(entry -> platform == null || entry.getKey().equals(platform))
                        .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> new UpdateResponse.PlatformDownload(
                                entry.getValue().getUrl(), entry.getValue().getSignature()),
                        (left, right) -> left,
                        java.util.LinkedHashMap::new
                ))
        );
    }
}
