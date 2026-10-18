package me.aidanbennett.vantailupdateserver.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import me.aidanbennett.vantailupdateserver.dto.update.UpdateStatsResponse;
import me.aidanbennett.vantailupdateserver.service.UpdateStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UpdateStatsController {

    private final UpdateStatsService updateStatsService;

    public UpdateStatsController(UpdateStatsService updateStatsService) {
        this.updateStatsService = updateStatsService;
    }

    @GetMapping("/updates/stats")
    @Operation(summary = "Get update request statistics")
    @ApiResponse(responseCode = "200", description = "Aggregated request counts", content = @Content(schema = @Schema(implementation = UpdateStatsResponse.class)))
    public UpdateStatsResponse getStats() {
        return updateStatsService.getStats();
    }

    @GetMapping("/updates/{id}/stats")
    @Operation(summary = "Get request statistics for one update")
    @ApiResponse(responseCode = "200", description = "Request count for the update")
    public long getStatsForUpdate(@PathVariable long id) {
        return updateStatsService.getStatsForUpdate(id);
    }
}
