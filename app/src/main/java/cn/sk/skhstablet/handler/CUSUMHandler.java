package cn.sk.skhstablet.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
//校验
@Sharable
public class CUSUMHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		//System.out.println(ByteBufUtil.hexDump(buf));
		// int length = buf.getShortLE(4);
		long start = System.currentTimeMillis();
		int sumIndex = buf.writerIndex() - 2;
		int sum = 0;
		for (int i = 4; i < sumIndex; i++) {
			sum += buf.getUnsignedByte(i);
		}
		short sumInBuf = buf.getShortLE(sumIndex);
		long end = System.currentTimeMillis();
//		System.out.println((short)sum);
//		System.out.println(end - start);
		if (sumInBuf == (short)sum) {
			super.channelRead(ctx, msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
	}

}
