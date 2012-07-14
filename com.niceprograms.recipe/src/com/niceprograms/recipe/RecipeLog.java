package com.niceprograms.recipe;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * Class for logging.
 */
public class RecipeLog {

	/**
	 * Logs at the info level.
	 * 
	 * @param message the message to log.
	 */
	public static void logInfo(String message) {
		log(IStatus.INFO, IStatus.OK, message, null);
	}

	/**
	 * Logs at the error level.
	 * 
	 * @param exception the exception to log.
	 */
	public static void logError(Throwable exception) {
		logError("Unexpected Exception", exception);
	}

	/**
	 * Logs at the error level.
	 * 
	 * @param message the message to log.
	 * @param exception the exception to log.
	 */
	public static void logError(String message, Throwable exception) {
		log(IStatus.ERROR, IStatus.OK, message, exception);
	}

	/**
	 * Logs a message.
	 * 
	 * @param severity the severity.
	 * @param code the code.
	 * @param message the message to log.
	 * @param exception the exception to log.
	 */
	public static void log(int severity, int code, String message,
			Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}

	/**
	 * Creates a status.
	 * 
	 * @param severity the severity.
	 * @param code the code.
	 * @param message the message.
	 * @param exception the exception.
	 * @return a status based on the provided data.
	 */
	public static IStatus createStatus(int severity, int code, String message,
			Throwable exception) {
		return new Status(severity, RecipeActivator.ID, code, message,
				exception);
	}

	/**
	 * Performs a log.
	 * 
	 * @param status the status to log.
	 */
	public static void log(IStatus status) {
		RecipeActivator.getDefault().getLog().log(status);
	}
}
