package com.moshi.im.server.handler

import com.moshi.im.common.Command
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
@Retention(RetentionPolicy.RUNTIME)
annotation class HandlerForCommand(val value: Command)
