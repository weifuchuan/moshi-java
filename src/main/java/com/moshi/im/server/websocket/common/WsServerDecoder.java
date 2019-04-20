package com.moshi.im.server.websocket.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.utils.ByteBufferUtils;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/** @author tanyaowu 2017年7月30日 上午10:10:50 */
public class WsServerDecoder {
  public static WsRequest decode(ByteBuffer buf, ChannelContext channelContext)
    throws AioDecodeException {
    WsSessionContext imSessionContext = (WsSessionContext) channelContext.getAttribute();

    // 第一阶段解析
    int initPosition = buf.position();
    int readableLength = buf.limit() - initPosition;

    int headLength = WsPacket.MINIMUM_HEADER_LENGTH;

    if (readableLength < headLength) {
      return null;
    }

    byte first = buf.get();
    //		int b = first & 0xFF; //转换成32位
    boolean fin = (first & 0x80) > 0; // 得到第8位 10000000>0
    @SuppressWarnings("unused")
    int rsv = (first & 0x70) >>> 4; // 得到5、6、7 为01110000 然后右移四位为00000111
    byte opCodeByte = (byte) (first & 0x0F); // 后四位为opCode 00001111
    Opcode opcode = Opcode.valueOf(opCodeByte);
    if (opcode == Opcode.CLOSE) {
      //      Tio.remove(channelContext, "收到opcode:" + opcode);
      //      return null;
    }

    byte second = buf.get(); // 向后读取一个字节
    boolean hasMask =
      (second & 0xFF) >> 7
        == 1; // 用于标识PayloadData是否经过掩码处理。如果是1，Masking-key域的数据即是掩码密钥，用于解码PayloadData。客户端发出的数据帧需要进行掩码处理，所以此位是1。

    // Client data must be masked
    if (!hasMask) { // 第9为为mask,必须为1
      // throw new AioDecodeException("websocket client data must be masked");
    } else {
      headLength += 4;
    }
    int payloadLength = second & 0x7F; // 读取后7位  Payload legth，如果<126则payloadLength

    byte[] mask = null;
    if (payloadLength == 126) { // 为126读2个字节，后两个字节为payloadLength
      headLength += 2;
      if (readableLength < headLength) {
        return null;
      }
      payloadLength = ByteBufferUtils.readUB2WithBigEdian(buf);

    } else if (payloadLength == 127) { // 127读8个字节,后8个字节为payloadLength
      headLength += 8;
      if (readableLength < headLength) {
        return null;
      }

      payloadLength = (int) buf.getLong();
    }

    if (payloadLength < 0 || payloadLength > WsPacket.MAX_BODY_LENGTH) {
      throw new AioDecodeException("body length(" + payloadLength + ") is not right");
    }

    if (readableLength < headLength + payloadLength) {
      return null;
    }

    if (hasMask) {
      mask = ByteBufferUtils.readBytes(buf, 4);
    }

    // 第二阶段解析
    WsRequest websocketPacket = new WsRequest();
    websocketPacket.setWsEof(fin);
    websocketPacket.setWsHasMask(hasMask);
    websocketPacket.setWsMask(mask);
    websocketPacket.setWsOpcode(opcode);
    websocketPacket.setWsBodyLength(payloadLength);

    if (payloadLength == 0) {
      return websocketPacket;
    }

    byte[] array = ByteBufferUtils.readBytes(buf, payloadLength);
    if (hasMask) {
      for (int i = 0; i < array.length; i++) {
        array[i] = (byte) (array[i] ^ mask[i % 4]);
      }
    }

    websocketPacket.setBody(array);

    if (fin && opcode != null) {
      if (opcode == Opcode.BINARY) {

      } else {
        try {
          String text = new String(array, WsPacket.CHARSET_NAME);
          websocketPacket.setWsBodyText(text);
        } catch (UnsupportedEncodingException e) {
          log.error(e.toString(), e);
        }
      }
    }
    return websocketPacket;
  }

  private static Logger log = LoggerFactory.getLogger(WsServerDecoder.class);


  /** @author tanyaowu 2017年2月22日 下午4:06:42 */
  public WsServerDecoder() {}
}
