package me.aidanbennett.vantailupdateserver.service;

import me.aidanbennett.vantailupdateserver.entity.UpdateEntity;
import me.aidanbennett.vantailupdateserver.exception.UpdateNotFoundException;
import me.aidanbennett.vantailupdateserver.repository.UpdateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateStatsServiceTest {

    @Mock
    private UpdateRepository updateRepository;

    @Test
    void getStatsAggregatesRequestsByVersion() {
        UpdateEntity first = update("1.1.0", 12L);
        UpdateEntity second = update("1.0.0", 8L);
        when(updateRepository.findAll()).thenReturn(List.of(first, second));

        var stats = new UpdateStatsService(updateRepository).getStats();

        assertThat(stats.totalRequests()).isEqualTo(20L);
        assertThat(stats.requestsByVersion())
                .containsEntry("1.1.0", 12L)
                .containsEntry("1.0.0", 8L);
    }

    @Test
    void getStatsForUpdateReturnsTheStoredCount() {
        UpdateEntity update = update("1.1.0", 42L);
        when(updateRepository.findById(5L)).thenReturn(Optional.of(update));

        assertThat(new UpdateStatsService(updateRepository).getStatsForUpdate(5L))
                .isEqualTo(42L);
    }

    @Test
    void getStatsForUpdateThrowsForMissingUpdate() {
        when(updateRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new UpdateStatsService(updateRepository).getStatsForUpdate(5L))
                .isInstanceOf(UpdateNotFoundException.class);
    }

    private UpdateEntity update(String version, long count) {
        UpdateEntity update = mock(UpdateEntity.class);
        lenient().when(update.getVersion()).thenReturn(version);
        lenient().when(update.getRequestCount()).thenReturn(count);
        return update;
    }
}
