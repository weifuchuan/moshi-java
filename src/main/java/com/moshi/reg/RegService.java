package com.moshi.reg;

import com.jfinal.kit.*;
import com.jfinal.plugin.activerecord.Db;
import com.moshi.common.authcode.AuthCodeService;
import com.moshi.common.kit.EmailKit;
import com.moshi.common.model.Account;
import com.moshi.common.model.AuthCode;

import java.util.Date;

public class RegService {

  public static final RegService me = new RegService();

  private Account accountDao = new Account().dao();

  /**
   * 邮箱是否已被注册
   */
  public boolean isEmailExists(String email) {
    email = email.toLowerCase().trim();
    return Db.queryInt("select id from account where email = ? limit 1", email) != null;
  }

  /**
   * 手机是否已被注册
   */
  public boolean isPhoneExists(String phone) {
    phone = phone.toLowerCase().trim();
    return Db.queryInt("select id from account where phone = ? limit 1", phone) != null;
  }

  /**
   * 昵称是否已被注册，昵称不区分大小写，以免存在多个用户昵称看起来一个样的情况
   * <p>
   * mysql 的 where 字句与 order by 子句默认不区分大小写，区分大小写需要在 字段名或字段值前面使用 binary 关键字例如：
   * where nickName = binary "jfinal" 或者 where binary nickName = "jfinal"，前者性能要高
   * <p>
   * 为了避免不同的 mysql 配置破坏掉 mysql 的 where 不区分大小写的行为，这里在 sql 中使用 lower(...) 来处理，参数
   * nickName 也用 toLowerCase() 方法来处理，再次确保不区分大小写
   */
  public boolean isNickNameExists(String nickName) {
    nickName = nickName.toLowerCase().trim();
    return Db.queryInt("select id from account where lower(nickName) = ? limit 1",
        nickName) != null;
  }

  /**
   * 账户注册，hashedPass = sha256(pass)
   */
  public Ret regByEmail(String email, String password, String nickName) {
    if (StrKit.isBlank(email) || StrKit.isBlank(password) || StrKit.isBlank(nickName)) {
      return Ret.fail("msg", "邮箱、密码或昵称不能为空");
    }

    email = email.toLowerCase().trim(); // 邮件全部存为小写
    password = password.trim();
    nickName = nickName.trim();

    if (nickName.contains("@") || nickName.contains("＠")) { // 全角半角都要判断
      return Ret.fail("msg", "昵称不能包含 \"@\" 字符");
    }
    if (nickName.contains(" ") || nickName.contains("　")) { // 检测是否包含半角或全角空格
      return Ret.fail("msg", "昵称不能包含空格");
    }
    if (isNickNameExists(nickName)) {
      return Ret.fail("msg", "昵称已被注册，请换一个昵称");
    }
    if (isEmailExists(email)) {
      return Ret.fail("msg", "邮箱已被注册，如果忘记密码，可以使用密码找回功能");
    }

    password = HashKit.sha256(password);

    // 创建账户
    Account account = new Account();
    account.setEmail(email);
    account.setPassword(password);
    account.setNickName(nickName);
    account.setStatus(Account.STATUS_REG);
    account.setCreateAt(new Date().getTime());
    account.setAvatar(Account.AVATAR_NO_AVATAR); // 注册时设置默认头像

    if (account.save()) {
      String authCode = AuthCodeService.me.createRegActivateAuthCode(account.getInt("id"));
      if (sendRegActivateAuthEmail(authCode, account)) {
        return Ret.ok("msg", "注册成功，激活邮件已发送，请查收并激活账号：" + email);
      } else {
        return Ret.fail("msg", "注册成功，但是激活邮件发送失败");
      }
    } else {
      return Ret.fail("msg", "注册失败，account 保存失败，请告知管理员");
    }
  }

  /**
   * 发送账号激活授权邮件
   */
  private boolean sendRegActivateAuthEmail(String authCode, Account reg) {
    String title = "默识 | 用户激活邮件";
    String content = "激活码：\n\n" + authCode;

    String toEmail = reg.getStr("email");
    try {
      EmailKit.sendEmail(toEmail, title, content);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 激活账号，返回 false 表示激活码已过期或者不存在 
   * 激活账号不要去自动登录，激活邮件如果发错到了别人的邮箱，会有别人冒用的可能
   * 并且登录功能还有额外有选择过期时间的功能
   */
  public Ret activate(String authCodeId) {
    AuthCode authCode = AuthCodeService.me.getAuthCode(authCodeId);
    if (authCode != null && authCode.isValidRegActivateAuthCode()) {
      // 更新账户状态为已激活， status 的 where 条件必须为 reg，以防被锁定账户重新激活
      int n = Db.update("update account set status = ? where id = ? and status = ?",
          Account.STATUS_LEANER, authCode.get("accountId"), Account.STATUS_REG);
      if (n > 0) {
        return Ret.ok("msg", "账号激活成功");
      } else {
        return Ret.fail("msg", "未找到需要激活的账号，可能是账号已经激活或已经被锁定，请联系管理员");
      }
    } else {
      return Ret.fail("msg", "激活码 不存在或已经失效，可以尝试在登录页再次发送激活邮件");
    }
  }

  public Ret reSendActivateEmail(String email) {
    if (StrKit.isBlank(email) || email.indexOf('@') == -1) {
      return Ret.fail("msg", "email 格式不正确，请重新输入");
    }
    email = email.toLowerCase().trim(); // email 转成小写
    if (!isEmailExists(email)) {
      return Ret.fail("msg", "email 没有被注册，无法收取激活邮件，请先去注册");
    }

    // 根据 email 查找未激活的账户：Account.STATUS_REG
    Account account = accountDao.findFirst(
        "select * from account where email=? and status = ? limit 1", email, Account.STATUS_REG);
    if (account == null) {
      return Ret.fail("msg", "该账户已经激活，可以直接登录");
    }

    String authCode = AuthCodeService.me.createRegActivateAuthCode(account.getId());
    if (sendRegActivateAuthEmail(authCode, account)) {
      return Ret.ok("msg", "激活码已发送至邮箱，请收取激活邮件并进行激活");
    } else {
      return Ret.fail("msg", "激活邮件发送失败，可能是邮件服务器出现故障");
    }
  }
}
