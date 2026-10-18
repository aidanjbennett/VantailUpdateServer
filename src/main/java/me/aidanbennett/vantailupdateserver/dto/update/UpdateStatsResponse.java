package me.aidanbennett.vantailupdateserver.dto.update;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "UpdateStats", description = "Aggregated update request counts. These are requests, not unique people or confirmed installations.")
public record UpdateStatsResponse(
        @Schema(description = "Total number of requests for individual updates.", example = "1250")
        long totalRequests,
        @Schema(description = "Number of requests grouped by update version.")
        Map<String, Long> requestsByVersion
) {
}
