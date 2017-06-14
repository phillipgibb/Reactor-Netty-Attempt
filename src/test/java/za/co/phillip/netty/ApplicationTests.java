package za.co.phillip.netty;

import java.time.Duration;
import java.util.function.BiFunction;

import javax.net.ssl.SSLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.http.client.HttpClient;
import reactor.ipc.netty.http.server.HttpServer;
import reactor.ipc.netty.http.server.HttpServerRequest;
import reactor.ipc.netty.http.server.HttpServerResponse;
import reactor.util.Logger;
import reactor.util.Loggers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	final Logger log = Loggers.getLogger(ApplicationTests.class);

	@Autowired
	public WebClient webClient;

	@Before
	public void initialize() {
		startController();
	}

	private void startController() {

		
		
		HttpServer server = HttpServer.create(opts -> opts.listen(8092)
				.option(ChannelOption.SO_RCVBUF, 1024 * 1024)
				.option(ChannelOption.SO_SNDBUF, 1024 * 1024)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.afterChannelInit(channelInit -> {
			ChannelPipeline pipeline = channelInit.pipeline();
//			pipeline.addLast("encoder", new HttpResponseEncoder());
//			pipeline.addLast("decoder", new HttpRequestDecoder());
			pipeline.addLast("aggregator", new HttpObjectAggregator(64 * 1024));
			
//			pipeline.addLast("codec", new HttpClientCodec());

		}));

		Mono<? extends NettyContext> context = server.newRouter(routes -> {
			routes.post("/test", postHandler());
			routes.put("/test", postHandler());
		});

		// Mono<? extends NettyContext> context =
		// server.newHandler(postHandler());

		context.block(Duration.ofSeconds(30));

	}

	BiFunction<? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>> postHandler() {
		return (req, resp) -> {
			req.requestHeaders().entries()
					.forEach(entry -> log.debug(String.format("header [%s=>%s]", entry.getKey(), entry.getValue())));

			return resp/* .chunkedTransfer(true) */
					.sendObject(req.receiveContent().log("received").flatMap(data -> {
						log.debug("Data-----------------" + data);
						final StringBuilder responseContent = new StringBuilder().append(getResponse());// data.content().toString(Charset.defaultCharset())
						log.debug(">" + String.format("%s from thread %s", getResponse(), Thread.currentThread()));
						DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
								HttpResponseStatus.OK, Unpooled.wrappedBuffer(responseContent.toString().getBytes()));
						return Mono.just(response);
					}));
		};
	}

	@Test
	public void testClientMessage() {
		HttpClient httpClient;
		try {
			httpClient = webClient.connect("localhost", 8092, false);
			webClient.send(httpClient, "/test", getTestRequest());

		} catch (SSLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String getResponse() {
		return "Well Done";
	}


	
	private String getTestRequest() {
		return "moreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytesmoreThan1024bytes";
	}

}
