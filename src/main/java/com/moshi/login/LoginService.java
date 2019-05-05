package com.moshi.login;

import java.util.List;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;
import com.moshi.common.account.AccountService;
import com.moshi.common.authcode.AuthCodeService;
import com.moshi.common.kit.EmailKit;
import com.moshi.common.model.Account;
import com.moshi.common.model.Session;

public class LoginService {
  public static final LoginService me = new LoginService();

  // 存放登录用户的 cacheName
  public static final String loginAccountCacheName = "account";

  // "moshiId" 仅用于 cookie 名称，其它地方如 cache 中全部用的 "sessionId" 来做 key
  public static final String sessionIdName = "moshiId";

  private Account accountDao = Account.dao;

  public Ret loginByEmail(String email, String password) {
    email = email.trim();
    password = HashKit.sha256(password.trim());
    Account account = accountDao.findFirst("select * from account where email = ? ", email);

    if (account == null) {
      return Ret.fail("msg", "用户不存在");
    } else if (!account.getPassword().equals(password)) {
      return Ret.fail("msg", "密码错误");
    }
    account.removeSensitiveInfo();

    // 暂定过期时间为 3 年
    long liveSeconds  = 3 * 365 * 24 * 60 * 60;
    // 传递给控制层的 cookie
    int maxAgeInSeconds = (int) (liveSeconds);
    // expireAt 用于设置 session 的过期时间点，需要转换成毫秒
    long expireAt = System.currentTimeMillis() + (liveSeconds * 1000);
    // 保存登录 session 到数据库
    Session session = new Session();
    String sessionId = StrKit.getRandomUUID();
    session.setId(sessionId);
    session.setAccountId(account.getId());
    session.setExpireAt(expireAt);
    if (!session.save()) {
      return Ret.fail("msg", "保存 session 到数据库失败，请联系管理员");
    }

    account.put("sessionId", sessionId); // 保存一份 sessionId 到 loginAccount 备用
    CacheKit.put(loginAccountCacheName, sessionId, account);

    return Ret.ok(sessionIdName, sessionId)
        .set(loginAccountCacheName, account)
        .set("maxAgeInSeconds", maxAgeInSeconds); // 用于设置cookie的最大存活时间
  }

  public Account getLoginAccountWithSessionIdFromCache(String sessionId) {
    return CacheKit.get(loginAccountCacheName, sessionId);
  }

  public void removeLoginAccountWithSessionIdFromCache(String sessionId) {
    CacheKit.remove(loginAccountCacheName, sessionId);
  }

  /**
   * 通过 sessionId 获取登录用户信息 sessoin表结构：session(id, accountId, expireAt)
   *
   * <p>1：先从缓存里面取，如果取到则返回该值，如果没取到则从数据库里面取 2：在数据库里面取，如果取到了，则检测是否已过期，如果过期则清除记录， 如果没过期则先放缓存一份，然后再返回
   */
  public Account loginWithSessionId(String sessionId) {
    Session session = Session.dao.findById(sessionId);
    if (session == null) { // session 不存在
      return null;
    }
    if (session.isExpired()) { // session 已过期
      session.delete(); // 被动式删除过期数据，此外还需要定时线程来主动清除过期数据
      return null;
    }

    Account loginAccount = accountDao.findById(session.getAccountId());
    // 找到 loginAccount 并且 是正常状态 才允许登录
    if (loginAccount != null && !loginAccount.isStatusLock()) {
      loginAccount.removeSensitiveInfo(); // 移除 password 与 salt 属性值
      loginAccount.put("sessionId", sessionId); // 保存一份 sessionId 到 loginAccount 备用
      CacheKit.put(loginAccountCacheName, sessionId, loginAccount);

      return loginAccount;
    }
    return null;
  }

  /** 发送密码找回授权邮件 */
  public Ret sendRetrievePasswordAuthEmail(String email) {
    if (StrKit.isBlank(email) || email.indexOf('@') == -1) {
      return Ret.fail("msg", "email 格式不正确，请重新输入");
    }
    email = email.toLowerCase().trim(); // email 转成小写
    Account account = accountDao.findFirst("select * from account where userName=? limit 1", email);
    if (account == null) {
      return Ret.fail("msg", "您输入的 email 还没注册，无法找回密码");
    }

    String authCode = AuthCodeService.me.createRetrievePasswordAuthCode(account.getId());

    String title = "默识 密码找回邮件";
    String content = "您的密码找回授权码为：\n" + authCode;
    String toEmail = account.getStr("email");
    try {
      EmailKit.sendEmail(toEmail, title, content);
      return Ret.ok("msg", "密码找回邮件已发送至邮箱，请收取邮件并重置密码");
    } catch (Exception e) {
      return Ret.fail("msg", "密码找回邮件发送失败，可能是邮件服务器出现故障，请去JFinal官方QQ群留言给群主");
    }
  }

  /** 退出登录 */
  public void logout(String sessionId) {
    if (sessionId != null) {
      CacheKit.remove(loginAccountCacheName, sessionId);
      Session.dao.deleteById(sessionId);
    }
  }

  /** 从数据库重新加载登录账户信息 */
  public void reloadLoginAccount(Account loginAccountOld) {
    String sessionId = loginAccountOld.get("sessionId");
    Account loginAccount =
        accountDao.findFirst("select * from account where id=? limit 1", loginAccountOld.getId());
    loginAccount.removeSensitiveInfo(); // 移除 password 与 salt 属性值
    loginAccount.put("sessionId", sessionId); // 保存一份 sessionId 到 loginAccount 备用

    // 集群方式下，要做一通知其它节点的机制，让其它节点使用缓存更新后的数据，
    // 将来可能把 account 用 id : obj 的形式放缓存，
    // 更新缓存只需要 cache.remove("account", id) 就可以了，
    // 其它节点发现数据不存在会自动去数据库读取，
    // 所以未来可能就是在 AccountService.getById(int id)的方法引入缓存就好
    // 所有用到 account 对象的地方都从这里去取
    CacheKit.put(loginAccountCacheName, sessionId, loginAccount);
  }

}
