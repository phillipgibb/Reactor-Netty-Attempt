package za.co.phillip.netty.handlers;

import java.nio.charset.Charset;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import reactor.util.Logger;
import reactor.util.Loggers;

@Component
@Qualifier("CustomInBound")
@Sharable
public class CustomInBound extends SimpleChannelInboundHandler<ByteBuf> {

	final Logger log = Loggers.getLogger(CustomInBound.class);

	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
	try{
		log.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + msg.toString(Charset.forName("US-ASCII")) + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		 // (4)
//		if (ctx.channel().isActive()) {
//			ctx.channel().writeAndFlush("Hi");
//			ctx.channel().close();
//	    }
	    ctx.fireChannelRead(msg);
	} catch (Exception e) {
		log.error(e.getMessage(), e);
	}
	}

}
