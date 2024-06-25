package com.example.test_rest_api_wrapper.controllers;

import com.example.test_rest_api_wrapper.models.Script;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.test_rest_api_wrapper.services.ScriptExecutorService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ishchenko Danylo
 * @version 0.0.1
 *
 */
@RestController
@RequestMapping("/scripts")
public class ScriptController {
    private final ScriptExecutorService scriptExecutorService;

    public ScriptController(ScriptExecutorService scriptExecutorService) {
        this.scriptExecutorService = scriptExecutorService;
    }

    /**
     * Endpoint for executing a JavaScript script asynchronously.
     * Accepts JavaScript code in the request body.
     *
     * @param code JavaScript code to execute.
     * @return ResponseEntity with Script object representing the execution status.
     */
    @PostMapping("/execute")
    public ResponseEntity<Script> executeScript(@RequestBody String code, @RequestParam(required = false, defaultValue = "false") boolean blocking) {
        String id = java.util.UUID.randomUUID().toString();
        Script script = scriptExecutorService.executeScript(id, code, blocking);
        return ResponseEntity.ok(script);
    }

    /**
     * Endpoint for retrieving a list of all executed or scheduled scripts.
     *
     * @return ResponseEntity with a list of Script objects.
     */
    @GetMapping
    public ResponseEntity<List<Script>> listScripts(@RequestParam(required = false) String status, @RequestParam(required = false) String order) {
        List<Script> scripts = scriptExecutorService.getAllScripts().stream()
                .filter(script -> status == null || script.getStatus().equals(status))
                .sorted((s1, s2) -> "desc".equals(order) ? s2.getStartTime().compareTo(s1.getStartTime()) : s1.getStartTime().compareTo(s2.getStartTime()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(scripts);
    }

    /**
     * Endpoint for retrieving detailed information about a specific script.
     *
     * @param id Unique identifier of the script.
     * @return ResponseEntity with Script object containing detailed information.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Script> getScript(@PathVariable String id) {
        Script script = scriptExecutorService.getScript(id);
        return ResponseEntity.ok(script);
    }

    /**
     * Endpoint for stopping the execution of a running or scheduled script.
     *
     * @param id Unique identifier of the script to stop.
     * @return ResponseEntity with a success message or error response.
     */
    @PostMapping("/{id}/stop")
    public ResponseEntity<HttpStatus> stopScript(@PathVariable String id) {
        scriptExecutorService.stopScript(id);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    /**
     * Endpoint for removing an inactive script from the list by its identifier.
     *
     * @param id Unique identifier of the script to remove.
     * @return ResponseEntity with a success message or error response.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> removeScript(@PathVariable String id) {
        scriptExecutorService.removeScript(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
