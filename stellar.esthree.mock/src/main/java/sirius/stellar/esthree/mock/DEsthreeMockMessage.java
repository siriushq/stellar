package sirius.stellar.esthree.mock;

import io.avaje.jsonb.Json;
import sirius.stellar.logging.LoggerLevel;

import java.time.Instant;
import java.util.Map;

import static sirius.stellar.logging.LoggerLevel.*;

@Json
record DEsthreeMockMessage(Instant time, String level, String message, String name,
						   @Json.Unmapped Map<String, Object> unmapped) {

	/// Return the level of this message as a [LoggerLevel].
	LoggerLevel mappedLevel() {
		return switch (this.level) {
			case "ERROR", "FATAL" -> ERROR;
			case "WARNING" -> WARNING;
			case "INFO" -> INFORMATION;
			case "DEBUG" -> DEBUGGING;
			default -> INFORMATION;
		};
	}

	/// Return the message string itself, with the unmapped fields
	/// attached to the end of it (given they are non-empty).
	public String message() {
		return this.message + (!this.unmapped.isEmpty() ? "\n" + this.unmapped : "");
	}
}