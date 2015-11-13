package com.divisors.projectcuttlefish.httpserver.impl.websocket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WebSocketFrame {
	public static WebSocketFrame readFrom(InputStream is) throws IOException {
		WebSocketFrame result = new WebSocketFrame();
		@SuppressWarnings("unused")
		long read = 0;
		byte[] buffer;
		
		if (is.read(buffer = new byte[2], 0, 2) != 2)
			throw new IOException();
		read+=2;
		
		result.FIN  = (buffer[0] & 0b1000_0000) == 0b1000_0000;
		result.rsv1 = (buffer[0] & 0b0100_0000) == 0b0100_0000;
		result.rsv1 = (buffer[0] & 0b0010_0000) == 0b0010_0000;
		result.rsv1 = (buffer[0] & 0b0001_0000) == 0b0001_0000;
		
		result.opcode = WebSocketFrameOpCode.values()[buffer[0] & 0b1111];
		
		result.isMasked = (buffer[1] & 0b1000_0000) == 0b1000_0000;
		
		long length = buffer[1] & 0b0111_1111;
		if (length == 127) {
			if (is.read(buffer = new byte[8], 0, 8) != 8)
				throw new IOException();
			read+=8;
			length = ((long)(buffer[0] & 0xFF) << 28)
				| ((long)(buffer[1] & 0xFF) << 24)
				| ((long)(buffer[2] & 0xFF) << 20)
				| ((long)(buffer[3] & 0xFF) << 16)
				| ((long)(buffer[4] & 0xFF) << 12)
				| ((long)(buffer[5] & 0xFF) << 8)
				| ((long)(buffer[6] & 0xFF) << 4)
				| ((long)(buffer[7] & 0xFF));
		} else if (length == 126) {
			if (is.read(buffer = new byte[2], 0, 2) != 2)
				throw new IOException();
			read+=2;
			length = ((long)(buffer[0] & 0xFF) << 4) | ((long)(buffer[1] & 0xFF));
		}
		
		if (result.isMasked) {
			if (is.read(buffer = new byte[4], 0, 4) != 4)
				throw new IOException();
			read+=4;
			result.mask = ((int)(buffer[0] & 0xFF) << 12)
					| ((int)(buffer[1] & 0xFF) << 8)
					| ((int)(buffer[2] & 0xFF) << 4)
					| ((int)(buffer[3] & 0xFF));
		}
		
		//TODO add payload reading
		
		return result;
	}
	/**
	 * First bit of frame. Indicates that this is the final fragment in a message. The first fragment MAY also be the final fragment.
	 */
	boolean FIN;
	/**
	 * MUST be 0 unless an extension is negotiated that defines meanings for non-zero values. If a nonzero value is received and none of the negotiated
	 * extensions defines the meaning of such a nonzero value, the receiving endpoint MUST _Fail the WebSocket Connection_.
	 */
	boolean rsv1;
	/**
	 * MUST be 0 unless an extension is negotiated that defines meanings for non-zero values. If a nonzero value is received and none of the negotiated
	 * extensions defines the meaning of such a nonzero value, the receiving endpoint MUST _Fail the WebSocket Connection_.
	 */
	boolean rsv2;
	/**
	 * MUST be 0 unless an extension is negotiated that defines meanings for non-zero values. If a nonzero value is received and none of the negotiated
	 * extensions defines the meaning of such a nonzero value, the receiving endpoint MUST _Fail the WebSocket Connection_.
	 */
	boolean rsv3;
	/**
	 * Defines the interpretation of the "Payload data".  If an unknown opcode is received, the receiving endpoint MUST _Fail the WebSocket Connection_.
	 * @see WebSocketFrameOpCode
	 */
	WebSocketFrameOpCode opcode;
	/**
	 * Defines whether the "Payload data" is masked. If set to 1, a masking key is present in masking-key, and this is used to unmask the "Payload data". 
	 * All frames sent from client to server have this bit set to 1.
	 */
	boolean isMasked;
	/**
	 * All frames sent from the client to the server are masked by a 32-bit value that is contained within the frame. This field is present if the mask
	 * bit is set to 1 and is absent if the mask bit is set to 0. See Section 5.3 for further information on client-to-server masking.
	 */
	int mask;
	WebSocketFrame setFinished(boolean isFinished) {
		this.FIN = isFinished;
		return this;
	}

	protected byte[] getApplicationData() {
		return new byte[0];
	}
	
	/**
	 * The "Extension data" is 0 bytes unless an extension has been negotiated. Any extension MUST specify the length of the "Extension data", or how
	 * that length may be calculated, and how the extension use MUST be negotiated during the opening handshake. If present, the "Extension data" is
	 * included in the total payload length.
	 * @return extension data
	 */
	protected byte[] getExtensionData() {
		return new byte[0];
	}
	
	public long writeTo(OutputStream os) throws IOException {
		//write header byte
		os.write((((FIN?0b1000:0) | (rsv1?0b0100:0) | (rsv2?0b1000:0) | (rsv3?0b0001:0)) << 4) | (opcode.ordinal() & 0x0000_1111));

		//get payload, so we can calculate the length
		byte[] extensionData = getExtensionData();
		byte[] applicationData = getApplicationData();
		
		//calculate the length
		long length = 3 + extensionData.length + applicationData.length;
		if (isMasked)
			length += 4;
		//If larger than 125, we have to use the 16bit extension for the length
		if (length > 125 | length < 0) {
			length += 2;
			if (length > 0xFFFF | length < 0) {//user 64bit extension
				length += 6;
				os.write((isMasked?0b1000_0000:0) | 0b0111_1111);//set length field to 127
				os.write((int) ((length >>> 28) & 0xFF));
				os.write((int) ((length >> 24) & 0xFF));
				os.write((int) ((length >> 20) & 0xFF));
				os.write((int) ((length >> 16) & 0xFF));
				os.write((int) ((length >> 8) & 0xFF));
				os.write((int) ((length >> 4) & 0xFF));
				os.write((int) (length & 0xFF));
			} else {
				os.write((isMasked?0b1000_0000:0) | 0b0111_1110);//set length field to 126
				os.write((int) ((length >> 4) & 0xFF));
				os.write((int) (length & 0xFF));
			}
		} else {
			os.write((isMasked?0b1000_0000:0) | (byte)(length & 0x0111_1111));
		}
		
		//write mask, if exists
		if (isMasked) {
			os.write((mask >> 16) & 0xFF);
			os.write((mask >> 8) & 0xFF);
			os.write((mask >> 4) & 0xFF);
			os.write(mask & 0xFF);
		}
		
		os.write(extensionData);
		os.write(applicationData);
		
		return length;
	}
	//TODO check thread safety
	public byte[] getBytes() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writeTo(baos);
		return baos.toByteArray();
	}
	/**
	 * OpCodes
	 * <br/>
	 * See <a href="https://tools.ietf.org/html/rfc6455">RFC 6455</a> (page 28)
	 * @author mailmindlin
	 */
	public enum WebSocketFrameOpCode {
		/**
		 * Denotes a continuation frame
		 * <br/>
		 * <b>Default value 0x1</b>
		 */
		CONT,
		/**
		 * Denotes a text frame
		 * <br/>
		 * <b>Default value 0x2</b>
		 */
		TEXT,
		/**
		 * Denotes a binary frame
		 * <br/>
		 * <b>Default value 0x3</b>
		 */
		BINARY,
		/**
		 * Reserved for further non-control frames
		 * <br/>
		 * <b>Default value 0x3</b>
		 */
		NC1,
		/**
		 * Reserved for further non-control frames
		 * <br/>
		 * <b>Default value 0x4</b>
		 */
		NC2,
		/**
		 * Reserved for further non-control frames
		 * <br/>
		 * <b>Default value 0x5</b>
		 */
		NC3,
		/**
		 * Reserved for further non-control frames
		 * <br/>
		 * <b>Default value 0x6</b>
		 */
		NC4,
		/**
		 * Reserved for further non-control frames
		 * <br/>
		 * <b>Default value 0x7</b>
		 */
		NC5,
		/**
		 * Denotes a continuation frame
		 * <br/>
		 * <b>Default value 0x8</b>
		 */
		CLOSE,
		/**
		 * Denotes a continuation frame
		 * <br/>
		 * <b>Default value 0x9</b>
		 */
		PING,
		/**
		 * Denotes a continuation frame
		 * <br/>
		 * <b>Default value 0x0A</b>
		 */
		PONG,
		/**
		 * Reserved for further control frames
		 * <br/>
		 * <b>Default value 0xB</b>
		 */
		CTRL1,
		/**
		 * Reserved for further control frames
		 * <br/>
		 * <b>Default value 0xB</b>
		 */
		CTRL2,
		/**
		 * Reserved for further control frames
		 * <br/>
		 * <b>Default value 0xD</b>
		 */
		CTRL3,
		/**
		 * Reserved for further control frames
		 * <br/>
		 * <b>Default value 0xE</b>
		 */
		CTRL4,
		/**
		 * Reserved for further control frames
		 * <br/>
		 * <b>Default value 0xF</b>
		 */
		CRTL5;
	}
}
