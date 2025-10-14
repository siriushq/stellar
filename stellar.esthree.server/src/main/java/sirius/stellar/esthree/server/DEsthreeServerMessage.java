package sirius.stellar.esthree.server;

import io.avaje.jsonb.Json;
import sirius.stellar.logging.LoggerLevel;

import java.time.Instant;
import java.util.Map;

import static sirius.stellar.logging.LoggerLevel.*;

@Json
record DEsthreeServerMessage(Instant time, String level, String message, String name,
							 @Json.Unmapped Map<String, Object> unmapped) {

	LoggerLevel loggerLevel() {
		return switch (this.level) {
			case "ERROR", "FATAL" -> ERROR;
			case "WARNING" -> WARNING;
			case "INFO" -> INFORMATION;
			case "DEBUG" -> DEBUGGING;
			default -> INFORMATION;
		};
	}

	public String message() {
		return this.message + this.unmapped;
	}
}