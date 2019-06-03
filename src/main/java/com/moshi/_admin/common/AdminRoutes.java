package com.moshi._admin.common;

import com.jfinal.config.Routes;
import com.moshi._admin.account.AccountAdminController;
import com.moshi._admin.apply.ApplyAdminController;
import com.moshi._admin.article.ArticleAdminController;
import com.moshi._admin.auth.AdminAuthInterceptor;
import com.moshi._admin.course.CourseAdminController;
import com.moshi._admin.news.NewsAdminController;
import com.moshi._admin.permission.PermissionAdminController;
import com.moshi._admin.preset_text.PresetTextAdminController;
import com.moshi._admin.role.RoleAdminController;
import com.moshi._admin.subscription.SubscriptionController;

public class AdminRoutes extends Routes {

  @Override
  public void config() {
    addInterceptor(new AdminAuthInterceptor());

    add("/admin/account", AccountAdminController.class);
    add("/admin/role", RoleAdminController.class);
    add("/admin/permission", PermissionAdminController.class);
    add("/admin/apply", ApplyAdminController.class);
    add("/admin/course", CourseAdminController.class);
    add("/admin/preset-text", PresetTextAdminController.class);
    add("/admin/subscription", SubscriptionController.class);
    add("/admin/article", ArticleAdminController.class);
    add("/admin/news", NewsAdminController.class);
  }
}
