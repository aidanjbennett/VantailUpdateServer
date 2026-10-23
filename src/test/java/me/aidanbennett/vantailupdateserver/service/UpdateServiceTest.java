package me.aidanbennett.vantailupdateserver.service;

import me.aidanbennett.vantailupdateserver.dto.update.UpdateRequest;
import me.aidanbennett.vantailupdateserver.dto.update.UpdateResponse;
import me.aidanbennett.vantailupdateserver.entity.PlatformDownload;
import me.aidanbennett.vantailupdateserver.entity.UpdateEntity;
import me.aidanbennett.vantailupdateserver.exception.UpdateAlreadyExistsException;
import me.aidanbennett.vantailupdateserver.exception.UpdateNotFoundException;
import me.aidanbennett.vantailupdateserver.repository.UpdateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.time.Instant;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateServiceTest {

    @Mock
    private UpdateRepository updateRepository;

    private UpdateService service() {
        return new UpdateService(updateRepository);
    }

    @Test
    void createUpdateMapsAndSavesTheManifest() {
        UpdateRequest request = request("1.1.0");
        UpdateEntity saved = entity(1L, "1.1.0", 0L);
        when(updateRepository.existsByVersion("1.1.0")).thenReturn(false);
        when(updateRepository.save(any(UpdateEntity.class))).thenReturn(saved);

        UpdateResponse response = service().createUpdate(request);

        assertThat(response.version()).isEqualTo("1.1.0");
        assertThat(response.platforms()).containsKey("darwin-aarch64");
        ArgumentCaptor<UpdateEntity> captor = ArgumentCaptor.forClass(UpdateEntity.class);
        verify(updateRepository).save(captor.capture());
        assertThat(captor.getValue().getVersion()).isEqualTo("1.1.0");
        assertThat(captor.getValue().getPlatforms().get("darwin-aarch64").getUrl())
                .contains("App-1.1.0");
    }

    @Test
    void createUpdateRejectsDuplicateVersions() {
        when(updateRepository.existsByVersion("1.1.0")).thenReturn(true);

        assertThatThrownBy(() -> service().createUpdate(request("1.1.0")))
                .isInstanceOf(UpdateAlreadyExistsException.class)
                .hasMessageContaining("1.1.0");
    }

    @Test
    void getUpdateByIdIncrementsAndReturnsTheUpdate() {
        UpdateEntity update = entity(7L, "1.1.0", 0L);
        when(updateRepository.incrementRequestCount(7L)).thenReturn(1);
        when(updateRepository.findById(7L)).thenReturn(Optional.of(update));

        UpdateResponse response = service().getUpdateById(7L);

        assertThat(response.version()).isEqualTo("1.1.0");
        verify(updateRepository).incrementRequestCount(7L);
    }

    @Test
    void getUpdateByIdThrowsWhenTheUpdateDoesNotExist() {
        when(updateRepository.incrementRequestCount(99L)).thenReturn(0);

        assertThatThrownBy(() -> service().getUpdateById(99L))
                .isInstanceOf(UpdateNotFoundException.class);
    }

    @Test
    void getLatestUpdateForClientReturnsOnlyTheRequestedPlatform() {
        UpdateEntity update = entity(3L, "1.1.0", 0L);
        when(updateRepository.findAll(any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(update));
        when(updateRepository.incrementRequestCount(3L)).thenReturn(1);
        when(updateRepository.findById(3L)).thenReturn(Optional.of(update));

        UpdateResponse response = service().getLatestUpdateForClient("darwin", "aarch64", "1.0.0");

        assertThat(response.platforms()).containsOnlyKeys("darwin-aarch64");
    }

    @Test
    void getAllUpdatesUsesPagedResults() {
        UpdateEntity update = entity(1L, "1.1.0", 0L);
        when(updateRepository.findAll(any(org.springframework.data.domain.PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(update)));

        assertThat(service().getAllUpdates(2).getContent())
                .extracting(UpdateResponse::version)
                .containsExactly("1.1.0");
    }

    private UpdateRequest request(String version) {
        return new UpdateRequest(
                version,
                "Fixes the thing",
                Instant.parse("2026-08-22T09:00:00Z"),
                Map.of("darwin-aarch64", new UpdateRequest.PlatformDownloadRequest(
                        "https://downloads.example.com/1.1.0/App-1.1.0-darwin-aarch64.tar.gz",
                        "signature"
                ))
        );
    }

    private UpdateEntity entity(long id, String version, long requestCount) {
        UpdateEntity update = org.mockito.Mockito.mock(UpdateEntity.class);
        lenient().when(update.getId()).thenReturn(id);
        lenient().when(update.getVersion()).thenReturn(version);
        lenient().when(update.getNotes()).thenReturn("Fixes the thing");
        lenient().when(update.getPubDate()).thenReturn(Instant.parse("2026-08-22T09:00:00Z"));
        lenient().when(update.getRequestCount()).thenReturn(requestCount);
        lenient().when(update.getPlatforms()).thenReturn(new LinkedHashMap<>(Map.of(
                "darwin-aarch64", new PlatformDownload("https://example.com/update.tar.gz", "signature"),
                "windows-x86_64", new PlatformDownload("https://example.com/update.exe.tar.gz", "signature")
        )));
        return update;
    }
}
