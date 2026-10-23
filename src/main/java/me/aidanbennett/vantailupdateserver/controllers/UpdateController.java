package me.aidanbennett.vantailupdateserver.controllers;

import me.aidanbennett.vantailupdateserver.dto.update.UpdateResponse;
import me.aidanbennett.vantailupdateserver.dto.update.UpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import me.aidanbennett.vantailupdateserver.service.UpdateService;
import me.aidanbennett.vantailupdateserver.exception.InvalidPageException;
import me.aidanbennett.vantailupdateserver.exception.PublishUnauthorizedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.data.domain.Page;

@RestController
public class UpdateController {

    private final UpdateService updateService;
    private final String publishSecret;

    public UpdateController(
            UpdateService updateService,
            @Value("${update.publish-secret:}") String publishSecret
    ) {
        this.updateService = updateService;
        this.publishSecret = publishSecret;
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

    @GetMapping("/update/latest/{target}/{arch}/{currentVersion}")
    @Operation(summary = "Get the latest update for a platform and current version")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Latest update", content = @Content(schema = @Schema(implementation = UpdateResponse.class))),
            @ApiResponse(responseCode = "404", description = "No updates found")
    })
    public UpdateResponse getLatestUpdateForClient(
            @Parameter(description = "Operating system target", example = "darwin")
            @PathVariable String target,
            @Parameter(description = "CPU architecture", example = "aarch64")
            @PathVariable String arch,
            @Parameter(description = "Currently installed version", example = "1.0.0")
            @PathVariable String currentVersion
    ) {
        return updateService.getLatestUpdateForClient(target, arch, currentVersion);
    }

    @PostMapping("/updates")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Publish an update")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Update published", content = @Content(schema = @Schema(implementation = UpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update manifest"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid publish secret"),
            @ApiResponse(responseCode = "409", description = "Version already exists")
    })
    public UpdateResponse createUpdate(
            @RequestHeader(value = "X-Update-Publish-Secret", required = false) String providedSecret,
            @Valid @RequestBody UpdateRequest request
    ) {
        if (!isValidPublishSecret(providedSecret)) {
            throw new PublishUnauthorizedException();
        }
        return updateService.createUpdate(request);
    }

    private boolean isValidPublishSecret(String providedSecret) {
        if (publishSecret == null || publishSecret.isBlank()
                || providedSecret == null || providedSecret.isBlank()) {
            return false;
        }
        return MessageDigest.isEqual(
                publishSecret.getBytes(StandardCharsets.UTF_8),
                providedSecret.getBytes(StandardCharsets.UTF_8)
        );
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
}
