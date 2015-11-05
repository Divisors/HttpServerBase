package com.divisors.projectcuttlefish.httpserver.api.websocket;

public enum WebSocketCloseStatusCode {
	// 0-999 RESERVED not used
	/**
	 * Normal closure; the connection successfully completed whatever purpose
	 * for which it was created. {@value 1000}
	 */
	CLOSE_NORMAL(1000),
	/**
	 * The endpoint is going away, either because of a server failure or because
	 * the browser is navigating away from the page that opened the connection.
	 * {@value 1001}
	 */
	CLOSE_GOING_AWAY(1001),
	/**
	 * The endpoint is terminating the connection due to a protocol error.
	 */
	CLOSE_PROTOCOL_ERROR(1002),
	/**
	 * The connection is being terminated because the endpoint received data of
	 * a type it cannot accept (for example, a text-only endpoint received
	 * binary data).
	 */
	CLOSE_UNSUPPORTED(1003),
	// 1004: Reserved. A meaning might be defined in the future.
	/**
	 * <b>Reserved.</b> Must be set as a status code in a Close control frame by
	 * an endpoint. It is designated for use in applications expecting a status
	 * code to indicate that no status code was actually present.
	 */
	CLOSE_NO_STATUS(1005),
	/**
	 * <b>Reserved.</b> Used to indicate that a connection was closed abnormally
	 * (that is, with no close frame being sent) when a status code is expected.
	 * <br/>
	 * Must not be set as a status code in a Close control frame by an endpoint.
	 * It is designated for use in applications expecting a status code to
	 * indicate that the connection was closed abnormally, e.g., without sending
	 * or receiving a Close control frame.
	 */
	CLOSE_ABNORMAL(1006),
	/**
	 * Indicates that an endpoint is terminating the connection because it has
	 * received data within a message that was not consistent with the type of
	 * the message (e.g., non-UTF-8 [RFC3629] data within a text message).
	 */
	CLOSE_INVALID_PAYLOAD(1007),
	/**
	 * Indicates that an endpoint is terminating the connection because it has
	 * received a message that violates its policy. This is a generic status
	 * code that can be returned when there is no other more suitable status
	 * code (e.g., 1003 or 1009) or if there is a need to hide specific details
	 * about the policy.
	 */
	CLOSE_POLICY_VIOLATION(1008),
	/**
	 * Indicates that an endpoint is terminating the connection because it has
	 * received a message that is too big for it to process.
	 */
	CLOSE_TOO_LARGE(1009),
	/**
	 * Indicates that an endpoint (client) is terminating the connection because
	 * it has expected the server to negotiate one or more extension, but the
	 * server didn't return them in the response message of the WebSocket
	 * handshake. The list of extensions that are needed SHOULD appear in the
	 * {@code /reason/} part of the Close frame. Note that this status code is
	 * not used by the server, because it can fail the WebSocket handshake
	 * instead.
	 */
	CLOSE_MANDATORY_EXTENSION(1010),
	/**
	 * Indicates that a server is terminating the connection because it
	 * encountered an unexpected condition that prevented it from fulfilling the
	 * request.
	 */
	CLOSE_SERVER_ERROR(1011),
	/**
	 * <b>Reserved</b> This must not be set as a status code in a Close control
	 * frame by an endpoint. It is designated for use in applications expecting
	 * a status code to indicate that the connection was closed due to a failure
	 * to perform a TLS handshake (e.g., the server certificate can't be
	 * verified).
	 */
	CLOSE_TLS_HANDSHAKE(1015),
	;
	protected final int code;
	WebSocketCloseStatusCode(int code) {
		this.code = code;
	}
	public int getCode() {
		return code;
	}
}
