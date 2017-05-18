package za.co.phillip.netty.handlers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
@Qualifier("CustomInBound")
@Sharable
public class CustomInBound extends SimpleChannelInboundHandler<String> {

	final Logger log = Loggers.getLogger(CustomInBound.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		log.debug(msg);
		ctx.channel().writeAndFlush(msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("Channel is active\n");
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("\nChannel is disconnected");
		super.channelInactive(ctx);
	}

}
