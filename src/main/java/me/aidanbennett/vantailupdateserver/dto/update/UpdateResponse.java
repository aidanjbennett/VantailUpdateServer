package me.aidanbennett.vantailupdateserver.dto.update;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(name = "UpdateResponse", description = "A published application update.")
public record UpdateResponse(
        @Schema(example = "1.1.0")
        String version,
        @Schema(example = "Fixes the thing")
        String notes,
        @Schema(example = "2026-08-22T09:00:00Z", type = "string", format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        Instant pubDate,
        @Schema(description = "Map of platform identifier to downloadable artifact.")
        Map<String, PlatformDownload> platforms
) {
    public record PlatformDownload(
            @Schema(example = "https://downloads.example.com/1.1.0/App-1.1.0-darwin-aarch64.tar.gz") String url,
            @Schema(example = "B5XlVy0pAcaNt/4GCPqqxQj/...") String signature
    ) {
    }
}
