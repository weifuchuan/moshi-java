/**
 * 请勿将俱乐部专享资源复制给其他人，保护知识产权即是保护我们所在的行业，进而保护我们自己的利益 即便是公司的同事，也请尊重 JFinal 作者的努力与付出，不要复制给同事
 *
 * <p>如果你尚未加入俱乐部，请立即删除该项目，或者现在加入俱乐部：http://jfinal.com/club
 *
 * <p>俱乐部将提供 jfinal-club 项目文档与设计资源、专用 QQ 群，以及作者在俱乐部定期的分享与答疑， 价值远比仅仅拥有 jfinal club 项目源代码要大得多
 *
 * <p>JFinal 俱乐部是五年以来首次寻求外部资源的尝试，以便于有资源创建更加 高品质的产品与服务，为大家带来更大的价值，所以请大家多多支持，不要将 首次的尝试扼杀在了摇篮之中
 */
package com.moshi._admin.account;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.SqlPara;
import com.moshi.common.model.Account;
import com.moshi.common.model.Role;
import com.moshi.common.model.Session;
import com.moshi.login.LoginService;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import org.apache.zookeeper.Login;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** 账户管理 */
public class AccountAdminService {

  private Account dao = new Account().dao();

  public Page<Account> paginate(int pageNum) {
    return dao.paginate(pageNum, 10, "select *", "from account order by id desc");
  }

  public Account findById(int accountId) {
    return dao.findById(accountId);
  }

  /** 注意要验证 nickName 与 userName 是否存在 */
  public Ret update(Account account) {
    //    Account account1 = Account.dao.findById(account.getId());
    //    if (account1 == null) {
    //      return Ret.fail("msg", "用户不存在");
    //    }
    Kv items =
        Kv.by("nickName", account.getNickName())
            .set("age", account.getAge())
            .set("avatar", account.getAvatar() == null ? "" : account.getAvatar())
            .set("email", account.getEmail())
            .set("birthday", account.getBirthday())
            .set("company", account.getCompany())
            .set("education", account.getEducation())
            .set("personalProfile", account.getPersonalProfile())
            .set("phone", account.getPhone())
            .set("position", account.getPosition())
            .set("profession", account.getProfession())
            .set("realName", account.getRealName())
            .set("sex", account.getSex())
            .set("identityNumber", account.getIdentityNumber());

    //    for (Object key : items.keySet().toArray()) {
    //      if (account1.get((String) key).equals(items.get(key))) {
    //        items.delete(key);
    //      }
    //    }

    SqlPara sqlPara =
        Db.getSqlPara("account.update", Kv.by("id", account.getId()).set("items", items));

    if (Db.update(sqlPara) > 0) {
      return Ret.ok("msg", "账户更新成功");
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  /** 锁定账号 */
  public Ret lock(int loginAccountId, int lockedAccountId) {
    if (loginAccountId == lockedAccountId) {
      return Ret.fail("msg", "不能锁定自己的账号");
    }

    int n =
        Db.update("update account set status = (status | (1 << 0)) where id=?", lockedAccountId);

    // 锁定后，强制退出登录，避免继续搞破坏
    List<Session> sessionList =
        Session.dao.find("select * from session where accountId = ?", lockedAccountId);
    if (sessionList != null) {
      for (Session session : sessionList) { // 处理多客户端同时登录后的多 session 记录
        LoginService.me.logout(session.getId()); // 清除登录 cache，强制退出
      }
    }

    if (n > 0) {
      return Ret.ok("msg", "锁定成功");
    } else {
      return Ret.fail("msg", "锁定失败");
    }
  }

  /** 解锁账号 */
  public Ret unlock(int accountId) {
    // 如果账户未激活，则不能被解锁
    int n =
        Db.update(
            "update account set status = (status & ~(1 << 0)) where status != 0 and id = ?",
            accountId);
    Db.update("delete from session where accountId = ?", accountId);
    if (n > 0) {
      return Ret.ok("msg", "解锁成功");
    } else {
      return Ret.fail("msg", "解锁失败，可能是账户未激活，请查看账户详情");
    }
  }

  /** 添加角色 */
  public Ret addRole(int accountId, int roleId) {
    Record accountRole = new Record().set("accountId", accountId).set("roleId", roleId);
    Db.save("account_role", accountRole);
    return Ret.ok("msg", "添加角色成功");
  }

  /** 删除角色 */
  public Ret deleteRole(int accountId, int roleId) {
    Db.delete("delete from account_role where accountId=? and roleId=?", accountId, roleId);
    return Ret.ok("msg", "删除角色成功");
  }

  public Ret updateStatus(int accountId, int status) {
    Account account = Account.dao.findById(accountId);
    if (account == null) {
      return Ret.fail("msg", "用户不存在");
    }
    account.setStatus(status);
    if (account.update()) {
      return Ret.ok();
    } else {
      return Ret.fail("msg", "更新失败");
    }
  }

  /** 标记出 account 拥有的角色 未来用 role left join account_role 来优化 */
  public void markAssignedRoles(Account account, List<Role> roleList) {
    String sql = "select accountId from account_role where accountId=? and roleId=? limit 1";
    for (Role role : roleList) {
      Integer accountId = Db.queryInt(sql, account.getId(), role.getId());
      if (accountId != null) {
        // 设置 assigned 用于界面输出 checked
        role.put("assigned", true);
      }
    }
  }

  /**
   * 获取 "后台账户/管理员" 列表，在 account_role 表中存在的账户(被分配过角色的账户) 被定义为 "后台账户/管理员"
   *
   * <p>该功能便于查看后台都有哪些账户被分配了角色，在对账户误操作分配了角色时，也便于取消角色分配
   */
  public List<Record> getAdminList() {
    String sql =
        "select a.nickName, a.email, ar.*, r.name from account a, account_role ar, role r "
            + "where a.id = ar.accountId and ar.roleId = r.id "
            + "order by roleId asc";

    return Db.find(sql);
  }
}
