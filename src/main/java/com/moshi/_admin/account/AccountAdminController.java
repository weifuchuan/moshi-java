package com.moshi._admin.account;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.moshi._admin.role.RoleAdminService;
import com.moshi.account.AccountService;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Account;
import com.moshi.my.setting.MySettingService;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

import java.util.List;

/** 账户管理控制器 */
public class AccountAdminController extends BaseController {

  @Inject AccountAdminService srv;

  @Inject MySettingService mySettingSrv;

  @Inject RoleAdminService roleAdminSrv;

  public void page() {
    int pageNumber = getParaToInt("pageNumber", 1);
    int pageCount = getParaToInt("pageCount", 20);
    Page<Account> page = Account.dao.paginate(pageNumber, pageCount, Db.getSqlPara("account.all"));
    page.getList().forEach(Account::removeSensitiveInfo);
    renderJson(page);
  }

  /** 提交修改 */
  @Before(AccountUpdateValidator.class)
  public void update() {
    Account account = getModel(Account.class, "", true);
    Ret ret = srv.update(account);
    renderJson(ret);
  }

  /** 账户锁定 */
  public void lock() {
    Ret ret = srv.lock(getLoginAccountId(), getParaToInt("id"));
    renderJson(ret);
  }

  /** 账户解锁 */
  public void unlock() {
    Ret ret = srv.unlock(getParaToInt("id"));
    renderJson(ret);
  }

  public void updateStatus(int accountId, int status) {
    Ret ret = srv.updateStatus(accountId, status);
    renderJson(ret);
  }

  /** 添加角色 */
  public void addRole() {
    Ret ret = srv.addRole(getParaToInt("accountId"), getParaToInt("roleId"));
    renderJson(ret);
  }

  /** 删除角色 */
  public void deleteRole() {
    Ret ret = srv.deleteRole(getParaToInt("accountId"), getParaToInt("roleId"));
    renderJson(ret);
  }

  /**
   * 显示 "后台账户/管理员" 列表，在 account_role 表中存在的账户(被分配过角色的账户) 被定义为 "后台账户/管理员"
   *
   * <p>该功能便于查看后台都有哪些账户被分配了角色，在对账户误操作分配了角色时，也便于取消角色分配
   */
  public void showAdminList() {
    List<Record> adminList = srv.getAdminList();
    setAttr("adminList", adminList);
    render("admin_list.html");
  }

  public void avatar() {
    keepPara("p"); // 保持住分页的页号，便于在 ajax 提交后跳转到当前数据所在的页
    Account account = srv.findById(getParaToInt("accountId"));
    setAttr("account", account);
    render("avatar.html");
  }

  /** 上传用户图片，为裁切头像做准备 */
  public void uploadAvatar() {
    UploadFile uf = null;
    try {
      uf = getFile("avatar", mySettingSrv.getAvatarTempDir(), mySettingSrv.getAvatarMaxSize());
      if (uf == null) {
        renderJson(Ret.fail("msg", "请先选择上传文件"));
        return;
      }
    } catch (Exception e) {
      if (e instanceof com.jfinal.upload.ExceededSizeException) {
        renderJson(Ret.fail("msg", "文件大小超出范围"));
      } else {
        if (uf != null) {
          // 只有出现异常时才能删除，不能在 finally 中删，因为后面需要用到上传文件
          uf.getFile().delete();
        }
        renderJson(Ret.fail("msg", e.getMessage()));
      }
      return;
    }

    // 注意这里可以更换任意用户的头像，所以并非 getLoginAccountId()
    int accountId = getParaToInt("accountId");
    Ret ret = mySettingSrv.uploadAvatar(accountId, uf);
    if (ret.isOk()) { // 上传成功则将文件 url 径暂存起来，供下个环节进行裁切
      setSessionAttr("avatarUrl", ret.get("avatarUrl"));
    }
    renderJson(ret);
  }

  /** 保存 jcrop 裁切区域为用户头像 */
  public void saveAvatar() {
    // 注意这里可以更换任意用户的头像，所以并非 getLoginAccountId()
    int accountId = getParaToInt("accountId");
    Account account = AccountService.me.getById(accountId);

    String avatarUrl = getSessionAttr("avatarUrl");
    int x = getParaToInt("x");
    int y = getParaToInt("y");
    int width = getParaToInt("width");
    int height = getParaToInt("height");
    Ret ret = mySettingSrv.saveAvatar(account, avatarUrl, x, y, width, height);
    renderJson(ret);
  }
}
