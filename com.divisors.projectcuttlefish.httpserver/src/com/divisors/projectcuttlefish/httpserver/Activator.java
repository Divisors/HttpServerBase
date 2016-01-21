package com.divisors.projectcuttlefish.httpserver;

import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.divisors.projectcuttlefish.httpserver.api.StandardChannelOptions;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpServer;
import com.divisors.projectcuttlefish.httpserver.api.http.HttpServerImpl;
import com.divisors.projectcuttlefish.httpserver.api.request.HttpRequest;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponse;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseImpl;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponseLineImpl;
import com.divisors.projectcuttlefish.httpserver.api.response.HttpResponsePayload;
import com.divisors.projectcuttlefish.httpserver.api.response.StandardHttpResponseSerializer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServer;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerFactory;
import com.divisors.projectcuttlefish.httpserver.api.tcp.TcpServerImpl;
import com.divisors.projectcuttlefish.httpserver.util.FormatUtils;

import reactor.core.processor.RingBufferProcessor;

/**
 * Currently a working echo server, it binds to a random port between 1000 and 65536, and opens the default webbrowser.
 * 
 * This supports restarting and stuff.
 * 
 * @author mailmindlin
 *
 */
public class Activator implements BundleActivator {

	ServiceRegistration<?> httpServerFactoryServiceRegistration;
	ServiceRegistration<?> serverFactoryServiceRegistration;
	TcpServer tcp;
	/**
	 * Standard htttp header text
	 */
	final String toWrite = 
			  "HTTP/1.1 200 OK\r\n"
			+ "Server: PC-0.0.1\r\n"
			+ "Content-Type: text/plain; charset=utf-8\r\n"
			+ "Content-Length: ";
	@Override
	public void start(BundleContext context) throws Exception {
		try {
			System.out.println("Initializing: ProjectCuttlefish|HttpServer");
			//just random testing -- binds to a random local port
			tcp = TcpServerFactory.getInstance().createServer(new InetSocketAddress("localhost", (int)(Math.random() * 55536) + 1000))
					.start(server-> {
						((TcpServerImpl)server)
							.dispatchOn(RingBufferProcessor.create("test1", 32))
							.runOn(Executors.newCachedThreadPool())
							.onConnect((channel)-> {
								System.out.println("Connected to channel @ " + channel.getRemoteAddress() + ", id=" + channel.getConnectionID());
								channel.onRead(data->{
									//print out the data sent to this
									System.out.println("Recieved request from " + channel.getRemoteAddress());
									{
										byte[] inBytes = data.array();
										HttpRequest request = HttpRequest.parse(inBytes);
//										ByteBufferTokenizer tokenizer = new ByteBufferTokenizer(new byte[]{'\r','\n'},inBytes.length);
//										tokenizer.put(inBytes);
//										ByteBuffer token;
//										while ((token = tokenizer.next()) != null) {
//											byte[] bytes = ByteUtils.toArray(token);
//											System.out.println("TOKEN: " + FormatUtils.bytesToHex(bytes, true, 0));
//											System.out.println("STRING: " + new String(bytes));
//										}
									}
									String text = new String(data.array());
									byte[] output = new StringBuilder(toWrite)
											.append(text.getBytes().length * 4 + 17)
											.append("\r\n\r\n")
											.append(text)
											.append("\n=====BYTES=====\n")
											.append(FormatUtils.bytesToHex(data.array(), true, 0))
											.toString()
											.getBytes();
									ByteBuffer bytes = ByteBuffer.allocate(output.length).put(output);
									bytes.flip();
									channel.write(bytes);
								});
							});
						//try to open the default browser to send a request to this server
						if (Desktop.isDesktopSupported())
							try {
								Desktop.getDesktop().browse(new URI("http://localhost:" + ((InetSocketAddress)server.getAddress()).getPort()));
							} catch (URISyntaxException | IOException e) {
								e.printStackTrace();
							}
					});
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		System.out.println("Goodbye!");
		try {
			tcp.shutdownNow();
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
