package br.com.claro.whatsapp.tracking.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String message;

	public TrackingException(String message) {
		super(message);
		this.message = message;
	}

}
