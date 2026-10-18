package me.aidanbennett.vantailupdateserver.controllers;

import me.aidanbennett.vantailupdateserver.dto.update.UpdateResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.aidanbennett.vantailupdateserver.service.UpdateService;
import me.aidanbennett.vantailupdateserver.exception.InvalidPageException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;

@RestController
public class UpdateController {

    private final UpdateService updateService;

    public UpdateController(UpdateService updateService) {
        this.updateService = updateService;
    }

    @GetMapping("/update/latest")
    @Operation(summary = "Get the latest update")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Latest update", content = @Content(schema = @Schema(implementation = UpdateResponse.class))),
            @ApiResponse(responseCode = "404", description = "No updates found")
    })
    public UpdateResponse getLatestUpdate() {
        return updateService.getLatestUpdate();
    }

    @GetMapping("/update/{id}")
    @Operation(summary = "Get an update by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Update found", content = @Content(schema = @Schema(implementation = UpdateResponse.class))),
            @ApiResponse(responseCode = "404", description = "Update not found")
    })
    public UpdateResponse getUpdateById(@Parameter(description = "Database ID of the update", example = "1") @PathVariable long id) {
        return updateService.getUpdateById(id);
    }

    @GetMapping("/updates")
    @Operation(summary = "List all updates")
    @ApiResponse(responseCode = "200", description = "A page of updates")
    public Page<UpdateResponse> getUpdates(
            @Parameter(description = "Zero-based page number", example = "0")
            @RequestParam(defaultValue = "0") int page
    ) {
        if (page < 0) {
            throw new InvalidPageException("page must be zero or greater");
        }
        return updateService.getAllUpdates(page);
    }

    // In the future maybe make it so I can
    // create updates via rest api and remove updates etc
}
