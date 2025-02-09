/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益
 * 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 * <p>
 * 如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 * <p>
 * 俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑，
 * 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 * <p>
 * JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加
 * 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将
 * 首次的尝试扼杀在了摇篮之中
 */

package com.moshi.common.kit;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;

import com.moshi.common.MainConfig;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * 邮件发送工具类
 */
public class EmailKit {
  private static final Log log = Log.getLog(EmailKit.class);

  public static String sendEmail(String toEmail, String title, String content) {
    String emailServer = MainConfig.Companion.getP().get("email.server");
    String fromEmail = MainConfig.Companion.getP().get("email.from");
    String password = MainConfig.Companion.getP().get("email.password");

    SimpleEmail email = new SimpleEmail();
    if (StrKit.notBlank(emailServer)) {
      email.setHostName(emailServer);
    } else {
      // 默认使用本地 postfix 发送，这样就可以将postfix 的 mynetworks 配置为 127.0.0.1 或 127.0.0.0/8 了
      email.setHostName("127.0.0.1");
    }

    // 如果密码为空，则不进行认证
    if (StrKit.notBlank(password)) {
      email.setAuthentication(fromEmail, password);
    }

    email.setCharset("utf-8");
    try {
      email.addTo(toEmail);
      email.setFrom(fromEmail);
      email.setSubject(title);
      email.setMsg(content);
      return email.send();
    } catch (EmailException e) {
      log.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args){
    EmailKit.sendEmail("url9777@qq.com", "test from moshi", "test from moshi");
  }

}
		
		
	
	


