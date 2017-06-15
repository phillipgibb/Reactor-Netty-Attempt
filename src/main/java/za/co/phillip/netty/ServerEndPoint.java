package za.co.phillip.netty;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import reactor.util.Logger;
import reactor.util.Loggers;

@Configurable
@Component
public class ServerEndPoint implements Consumer<Channel> {

	final Logger log = Loggers.getLogger(ServerEndPoint.class);

	private ChannelPipeline pipeline;

		@Override
	public void accept(Channel sc) {
		this.pipeline = sc.pipeline();
//		this.pipeline.addLast(new HttpObjectAggregator(4096));
//		this.pipeline.addLast(new HttpServerCodec());
//		this.pipeline.addLast(new ContentLengthHandler());
	}

}