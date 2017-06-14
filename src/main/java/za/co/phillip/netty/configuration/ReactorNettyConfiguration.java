package za.co.phillip.netty.configuration;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.NettyContext;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;
import reactor.ipc.netty.NettyPipeline;
import reactor.ipc.netty.tcp.TcpServer;
import reactor.spring.context.config.EnableReactor;
import reactor.util.Logger;
import reactor.util.Loggers;
import za.co.phillip.netty.WebClient;
import za.co.phillip.netty.handlers.CustomInBound;

@Configuration
@EnableReactor
@EnableConfigurationProperties
@EnableAutoConfiguration
@ComponentScan
public class ReactorNettyConfiguration {

	final Logger log = Loggers.getLogger(Configuration.class);

	@Value("${boss.thread.count}")
	private int bossCount;
	@Value("${worker.thread.count}")
	private int workerCount;
	@Value("${tcp.port}")
	private int tcpPort;
	@Value("${so.keepalive}")
	private boolean keepAlive;
	@Value("${so.backlog}")
	private int backlog;

	@Autowired
	@Qualifier("customInBound")
	private CustomInBound customInBound;

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup bossGroup() {
		return new NioEventLoopGroup(bossCount);
	}

	@Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
	public NioEventLoopGroup workerGroup() {
		return new NioEventLoopGroup(workerCount);
	}

	@Bean(name = "tcpSocketAddress")
	public InetSocketAddress tcpPort() {
		return new InetSocketAddress(tcpPort);
	}

	@Bean(name = "tcpChannelOptions")
	public Map<ChannelOption<?>, Object> tcpChannelOptions() {
		Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
		options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
		options.put(ChannelOption.SO_BACKLOG, backlog);
		return options;
	}

	@Bean(name = "customInBound")
	public CustomInBound customInBound() {
		return new CustomInBound();
	}

	@Bean(name = "stringEncoder")
	public StringEncoder stringEncoder() {
		return new StringEncoder();
	}

	@Bean(name = "stringDecoder")
	public StringDecoder stringDecoder() {
		return new StringDecoder();
	}
	
	@Bean
	public WebClient webClient() {
		return new WebClient();
	}

/*	@Bean
	public NettyContext server() throws InterruptedException {
		BiFunction<? super NettyInbound, ? super NettyOutbound, ? extends Publisher<Void>> serverHandler = (in,
				out) -> {
			in.receive().asString().subscribe(data -> {
				log.info("data " + data + " on " + in);
			});
			return Flux.never();
		};

		TcpServer server = TcpServer.create(opts -> opts
				.afterChannelInit(
						c -> c.pipeline().addBefore(NettyPipeline.ReactiveBridge, "customInBound", customInBound()))
				.listen(tcpPort()));

		return server.newHandler(serverHandler).block(Duration.ofSeconds(30));
	}*/

}
