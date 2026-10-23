package me.aidanbennett.vantailupdateserver.dto.update;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Map;

@Schema(name = "UpdateRequest", description = "A release to publish.")
public record UpdateRequest(
        @NotBlank
        @Schema(example = "1.1.0", requiredMode = Schema.RequiredMode.REQUIRED)
        String version,
        @NotBlank
        @Schema(example = "Fixes the thing", requiredMode = Schema.RequiredMode.REQUIRED)
        String notes,
        @NotNull
        @Schema(example = "2026-08-22T09:00:00Z", requiredMode = Schema.RequiredMode.REQUIRED)
        Instant pubDate,
        @NotEmpty
        @Valid
        @Schema(description = "Map of platform identifier to downloadable artifact.", requiredMode = Schema.RequiredMode.REQUIRED)
        Map<String, PlatformDownloadRequest> platforms
) {
    public record PlatformDownloadRequest(
            @NotBlank
            @Schema(example = "https://downloads.example.com/1.1.0/App-1.1.0-darwin-aarch64.tar.gz", requiredMode = Schema.RequiredMode.REQUIRED)
            String url,
            @NotBlank
            @Schema(example = "B5XlVy0pAcaNt/4GCPqqxQj/...", requiredMode = Schema.RequiredMode.REQUIRED)
            String signature
    ) {
    }
}
