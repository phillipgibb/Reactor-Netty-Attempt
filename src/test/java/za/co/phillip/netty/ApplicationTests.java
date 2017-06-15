package za.co.phillip.netty;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

import javax.net.ssl.SSLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
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

	private int contentLength = 1024;

//	private String combinedContent = new String();

	@Before
	public void initialize() {
		Hooks.onOperator(providedHook -> providedHook.operatorStacktrace());
		startServer();
	}

	private void startServer() {

		HttpServer server = HttpServer.create(opts -> opts.listen(8092).option(ChannelOption.SO_RCVBUF, 1024 * 1024)
				.option(ChannelOption.SO_SNDBUF, 1024 * 1024).option(ChannelOption.SO_KEEPALIVE, true)
				.afterChannelInit(channelInit -> {
					ChannelPipeline pipeline = channelInit.pipeline();
//					pipeline.addLast("aggregator", new HttpObjectAggregator(64 * 1024));

					// pipeline.addLast("http server codec", new
					// HttpServerCodec());

				}));

		Mono<? extends NettyContext> context = server.newRouter(routes -> {
			routes.post("/test", postHandler());
			routes.put("/test", postHandler());
		});

		context.subscribe().block(Duration.ofSeconds(30));

	}

	BiFunction<? super HttpServerRequest, ? super HttpServerResponse, ? extends Publisher<Void>> postHandler() {
		return (req, resp) -> {
			req.requestHeaders().entries().forEach(entry -> {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key.equalsIgnoreCase("content-length")) {
					contentLength = Integer.parseInt(value);
				}
				log.debug(String.format("header [%s=>%s]", key, value));
			});

			final StringBuilder combinedContent = new StringBuilder();
			req.receiveContent().doOnNext(request -> {
				ByteBuf content = request.content();
				if (!content.hasArray()) {
					int length = content.readableBytes();
					byte[] array = new byte[length];
					content.getBytes(content.readerIndex(),array);
					combinedContent.append(new String(array));
				}
			}).subscribe((what) -> {
				log.debug("Combined Request = " + combinedContent.toString());
			});

			return Mono.empty();

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
