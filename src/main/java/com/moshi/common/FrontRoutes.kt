package com.moshi.common

import com.jfinal.config.Routes
import com.moshi.apply.ApplyController
import com.moshi.article.ArticleController
import com.moshi.audio.AudioController
import com.moshi.avatar.AvatarController
import com.moshi.course.CourseController
import com.moshi.easyrec.EasyrecController
import com.moshi.file.FileController
import com.moshi.im.ImController
import com.moshi.index.IndexController
import com.moshi.issue.IssueController
import com.moshi.login.LoginController
import com.moshi.reg.RegController
import com.moshi.section.SectionController
import com.moshi.select.SqlServiceController
import com.moshi.video.VideoController

class FrontRoutes: Routes() {
  override fun config() {
    add("/apply", ApplyController::class.java)
    add("/article",ArticleController::class.java)
    add("/audio",AudioController::class.java)
    add("/avatar",AvatarController::class.java)
    add("/course",CourseController::class.java)
    add("/easyrec",EasyrecController::class.java)
    add("/file",FileController::class.java)
    add("/im",ImController::class.java)
    add("/",IndexController::class.java)
    add("/issue",IssueController::class.java)
    add("/login",LoginController::class.java)
    add("/reg",RegController::class.java)
    add("/section",SectionController::class.java)
    add("/select",SqlServiceController::class.java)
    add("/video",VideoController::class.java)
  }
}