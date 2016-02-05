package com.divisors.projectcuttlefish.httpserver.api.error;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.divisors.projectcuttlefish.httpserver.api.ServiceState;

/**
 * Exception for ServiceState's
 * @see ServiceState
 * @author mailmindlin
 */
public class ServiceStateException extends IllegalStateException implements Externalizable {
	private static final long serialVersionUID = 1L;
	public static ServiceStateException expect(String message, ServiceState expect) {
		return new ServiceStateException(expect, null);
	}
	
	protected ServiceState expect, actual;
	public ServiceStateException() {
		super();
	}
	public ServiceStateException(String message, ServiceState expect, ServiceState actual) {
		super(message);
		this.expect = expect;
		this.actual = actual;
	}
	public ServiceStateException(String message) {
		this(message, null, null);
	}
	public ServiceStateException(ServiceState expect, ServiceState actual) {
		this(String.format("EXPECT: %s; ACTUAL: %s", expect.name(), actual.name()), expect, actual);
	}
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		expect = (ServiceState) in.readObject();
		actual = (ServiceState) in.readObject();
	}
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(expect);
		out.writeObject(actual);
	}
}
