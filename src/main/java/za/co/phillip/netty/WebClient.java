package za.co.phillip.netty;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.function.BiFunction;

import javax.net.ssl.SSLException;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.client.HttpClientRequest;
import reactor.ipc.netty.http.client.HttpClientResponse;
import reactor.ipc.netty.resources.PoolResources;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
public class WebClient {

	final static DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("yyyyMMdd");
	final static DateTimeFormatter dtfTime = DateTimeFormatter.ofPattern("HHmmss");

	final Logger log = Loggers.getLogger(WebClient.class);

	@Autowired
	private ServerEndPoint serverEndPoint;

	PoolResources pool = PoolResources.elastic("WebClient");

	public HttpClient connect(String host, int port, boolean useSSL) throws SSLException {

		this.log.error("WebClient Connect");
		
		HttpClient client = null;
		if (useSSL) {

			SslContext clientOptions = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
					.build();
			client =  HttpClient.create(opts -> opts.connect(host, port).sslContext(clientOptions).poolResources(pool)
					.afterChannelInit(serverEndPoint));
		} else {

			client =  HttpClient
					.create(opts -> opts.connect(host, port).poolResources(pool).afterChannelInit(serverEndPoint));
		}
		return client;
	}

	public void send(HttpClient client, String url, String req) {

		ByteBuf reqContent = Unpooled.wrappedBuffer(req.getBytes());
		post(url, reqContent, client);
		this.log.error("sendHttptMessage");

	}

	private void post(String url, ByteBuf reqContent, HttpClient client) {

		DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, url,
				reqContent);
//		client.newHandler(handler(request)).subscribe().block(Duration.ofSeconds(10));
		client.request(HttpMethod.POST, url, clientRequest -> clientRequest.chunkedTransfer(true)
				.addHeader("Content-Length", Integer.toString(reqContent.capacity()))/*.sendHeaders()*/.send(Flux.just(request.content())))
				.subscribe().block(Duration.ofSeconds(60));

		/*
		 * Mono<HttpClientResponse> rsp = client.request(HttpMethod.POST, url,
		 * (req) -> req.chunkedTransfer(true).addHeader("Content-Length",
		 * Integer.toString(reqHeader.content().capacity())).sendHeaders().
		 * sendObject(reqHeaderclientRequest ->
		 * clientRequest.onNext(reqHeader.content()))); rsp.subscribe().block();
		 */

	}

	private void postViaSocket(String path, String data, SocketAddress address) {
		try {
			StringBuilder request = new StringBuilder().append(String.format("POST %s HTTP/1.1\r\n", path))
					.append("Connection: Keep-Alive\r\n");
			request.append(String.format("Content-Length: %s\r\n", data.length())).append("\r\n").append(data)
					.append("\r\n");
			java.nio.channels.SocketChannel channel = java.nio.channels.SocketChannel.open(address);
			System.out.println(String.format("post: request >> [%s]", request.toString()));
			channel.write(ByteBuffer.wrap(request.toString().getBytes()));
			ByteBuffer buf = ByteBuffer.allocate(4 * 1024);
			while (channel.read(buf) > -1) {
			}
			String response = new String(buf.array());
			this.log.debug("post: << " + "Response: %s", response);
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	BiFunction<? super HttpClientResponse, ? super HttpClientRequest, ? extends Publisher<Void>> handler(
			DefaultFullHttpRequest request) {
		return (in, out) -> {
			// in
			in.receive().asString().log("receive").subscribe(data -> {
				log.debug("RECEIVED : " + data);
			});
			;

			// out
			return out.sendObject(request).neverComplete();
			// return Mono.empty();
		};
	}

}
