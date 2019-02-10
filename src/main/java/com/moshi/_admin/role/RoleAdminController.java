package com.moshi._admin.role;

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import com.moshi._admin.permission.PermissionAdminService;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.Permission;
import com.moshi.common.model.Role;

import java.util.LinkedHashMap;
import java.util.List;

/** 角色管理控制器 */
public class RoleAdminController extends BaseController {

  @Inject RoleAdminService srv;

  @Inject PermissionAdminService permissionAdminSrv;

  @Before(RoleAdminValidator.class)
  public void save() {
    Role role = getModel(Role.class, "");
    Ret ret = srv.save(role);
    renderJson(ret);
  }

  /** 提交修改 */
  @Before(RoleAdminValidator.class)
  public void update() {
    Role role = getModel(Role.class, "");
    Ret ret = srv.update(role);
    renderJson(ret);
  }

  public void delete() {
    Ret ret = srv.delete(getParaToInt("id"));
    renderJson(ret);
  }

  /** 分配权限 */
  public void assignPermissions() {
    Role role = srv.findById(getParaToInt("id"));
    List<Permission> permissionList = permissionAdminSrv.getAllPermissions();
    srv.markAssignedPermissions(role, permissionList);
    LinkedHashMap<String, List<Permission>> permissionMap = srv.groupByController(permissionList);

    setAttr("role", role);
    setAttr("permissionMap", permissionMap);
    render("assign_permissions.html");
  }

  /** 添加权限 */
  public void addPermission() {
    Ret ret = srv.addPermission(getParaToInt("roleId"), getParaToInt("permissionId"));
    renderJson(ret);
  }

  /** 删除权限 */
  public void deletePermission() {
    Ret ret = srv.deletePermission(getParaToInt("roleId"), getParaToInt("permissionId"));
    renderJson(ret);
  }
}
