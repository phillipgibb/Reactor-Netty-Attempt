package za.co.phillip.netty;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.reactivestreams.Publisher;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import io.netty.handler.codec.LineBasedFrameDecoder;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;
import reactor.ipc.netty.NettyPipeline;
import reactor.ipc.netty.tcp.TcpClient;
import reactor.ipc.netty.tcp.TcpServer;
import reactor.util.Logger;
import reactor.util.Loggers;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApolloApplicationTests {

	final Logger log = Loggers.getLogger(ApolloApplicationTests.class);

	@Test
	public void testPineline() {
		final CountDownLatch latch = new CountDownLatch(2);

		final int port = SocketUtils.findAvailableTcpPort();
		try {
			NettyContext connected = startServer(port, latch);
			final TcpClient client = TcpClient.create(port);
			sendMessage(client);

			connected.dispose();

			assertTrue("Latch was counted down", latch.await(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			assertFalse(true);
			log.error("", e);
		}

	}

	private void sendMessage(TcpClient client) {
		client.newHandler((in, out) -> out.send(
				Flux.just("Hello World!\n", "Hello 11!\n").map(b -> out.alloc().buffer().writeBytes(b.getBytes()))))
				.block(Duration.ofSeconds(30));
	}

	public NettyContext startServer(int port, CountDownLatch latch) throws InterruptedException {
		BiFunction<? super NettyInbound, ? super NettyOutbound, ? extends Publisher<Void>> serverHandler = (in,
				out) -> {
			in.receive().asString().subscribe(data -> {
				log.info("data " + data + " on " + in);
				latch.countDown();
			});
			return Flux.never();
		};

		TcpServer server = TcpServer.create(opts -> opts.afterChannelInit(
				c -> c.pipeline().addBefore(NettyPipeline.ReactiveBridge, "codec", new LineBasedFrameDecoder(8 * 1024)))
				.listen(port));

		return server.newHandler(serverHandler).block(Duration.ofSeconds(30));
	}

}
