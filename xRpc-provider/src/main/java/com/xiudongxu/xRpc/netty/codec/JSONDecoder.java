package com.xiudongxu.xRpc.netty.codec;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author dongxu.xiu
 * @since 2019-03-07 上午11:11
 */
public class JSONDecoder extends LengthFieldBasedFrameDecoder {

    public JSONDecoder(){
        super(65536,0,4,0,4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decode = (ByteBuf)super.decode(ctx, in);
        if(decode == null) return null;
        int data_len = decode.readableBytes();
        byte[] bytes = new byte[data_len];
        decode.readBytes(bytes);
        return JSON.parse(bytes);
    }
}
