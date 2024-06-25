package com.example.test_rest_api_wrapper.services;

import com.example.test_rest_api_wrapper.models.Script;
import org.springframework.stereotype.Service;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Ishchenko Danylo
 * @version 0.0.1
 * Service class responsible for executing JavaScript scripts using GraalJS interpreter.
 * It handles execution, status tracking, and retrieval of script information.
 */
@Service
public class ScriptExecutorService {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<String, Script> scriptMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Future<Script>> scriptFutures = new ConcurrentHashMap<>();
    /**
     * Executes a JavaScript script asynchronously and returns a Script object
     * representing the execution status.
     *
     * @param id     Unique identifier for the script.
     * @param code   JavaScript code to execute.
     * @param blocking Whether to wait for script execution to complete before returning.
     * @return Script object representing the execution status.
     */
    public Script executeScript(String id, String code, boolean blocking) {
        Script script = new Script(id, code);
        scriptMap.put(id, script);
        Script finalScript = script;
        Callable<Script> task = () -> {
            finalScript.setStartTime(LocalDateTime.now());
            finalScript.setStatus("executing");
            try (Context context = Context.newBuilder("js").out(System.out).err(System.err).build()) {
                context.eval("js", code);
                finalScript.setStatus("completed");
            } catch (PolyglotException e) {
                finalScript.setError(e.getMessage());
                finalScript.setStatus("failed");
            }
            finalScript.setEndTime(LocalDateTime.now());
            return finalScript;
        };

        Future<Script> future = executor.submit(task);
        scriptFutures.put(id, future);

        if (blocking) {
            try {
                script = future.get();
            } catch (InterruptedException | ExecutionException e) {
                script.setError(e.getMessage());
                script.setStatus("failed");
            }
        }

        return script;
    }

    /**
     * Retrieves a list of all executed or scheduled scripts.
     *
     * @return List of Script objects.
     */
    public List<Script> getAllScripts() {
        return new ArrayList<>(scriptMap.values());
    }

    /**
     * Retrieves detailed information about a specific script.
     *
     * @param id Unique identifier of the script.
     * @return Script object with detailed information.
     */
    public Script getScript(String id) {
        return scriptMap.get(id);
    }

    /**
     * Stops the execution of a running or scheduled script.
     *
     * @param id Unique identifier of the script to stop.
     */
    public void stopScript(String id) {
        Future<Script> future = scriptFutures.get(id);
        if (future != null && !future.isDone()) {
            future.cancel(true);
            Script script = scriptMap.get(id);
            script.setStatus("stopped");
            script.setEndTime(LocalDateTime.now());
        }
    }

    /**
     * Removes an inactive script from the list by its identifier.
     *
     * @param id Unique identifier of the script to remove.
     */
    public void removeScript(String id) {
        scriptFutures.remove(id);
        scriptMap.remove(id);
    }
}
