package mx.edu.cbta.sistemaescolar;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {

    private static final long START_TIME = System.currentTimeMillis();

    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> healthInfo = new LinkedHashMap<>();

        healthInfo.put("status", "UP");
        healthInfo.put("timestamp", Instant.now().toString());

        RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
        long uptime = rb.getUptime(); // milisegundos desde que inició la JVM

        healthInfo.put("uptime_ms", uptime);
        healthInfo.put("uptime_formatted", formatUptime(uptime));
        healthInfo.put("service", "CBTa97Net-Escolar-API");

        return ResponseEntity.ok(healthInfo);
    }

    private String formatUptime(long uptimeMs) {
        long seconds = uptimeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return String.format("%d days, %d hours, %d minutes, %d seconds",
                days, hours % 24, minutes % 60, seconds % 60);
    }
}