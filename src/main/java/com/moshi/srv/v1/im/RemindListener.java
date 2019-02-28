package com.moshi.srv.v1.im;

import com.jfinal.kit.Kv;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RemindListener extends RedisPubSubAdapter<String, Object> {
  private static final Pattern CHANNEL_PATTERN = Pattern.compile("im:remind:for:room:(.+)");

  private RemindSender sender;

  public RemindListener(RemindSender sender) {
    this.sender = sender;
  }

  @Override
  public void message(String channel, Object message) {
    Matcher m = CHANNEL_PATTERN.matcher(channel);
    if (!m.matches()) {
      return;
    }
    String roomKey = m.group(1);
    sender.send((Kv) message);
  }
}
