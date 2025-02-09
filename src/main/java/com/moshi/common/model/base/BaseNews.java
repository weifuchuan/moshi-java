package com.moshi.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseNews<M extends BaseNews<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setTitle(java.lang.String title) {
		set("title", title);
	}
	
	public java.lang.String getTitle() {
		return getStr("title");
	}

	public void setContent(java.lang.String content) {
		set("content", content);
	}
	
	public java.lang.String getContent() {
		return getStr("content");
	}

	public void setCreateAt(java.lang.Long createAt) {
		set("createAt", createAt);
	}
	
	public java.lang.Long getCreateAt() {
		return getLong("createAt");
	}

	public void setPublishAt(java.lang.Long publishAt) {
		set("publishAt", publishAt);
	}
	
	public java.lang.Long getPublishAt() {
		return getLong("publishAt");
	}

	public void setStatus(java.lang.Integer status) {
		set("status", status);
	}
	
	public java.lang.Integer getStatus() {
		return getInt("status");
	}

	public void setAuthor(java.lang.String author) {
		set("author", author);
	}
	
	public java.lang.String getAuthor() {
		return getStr("author");
	}

	public void setAudioId(java.lang.Integer audioId) {
		set("audioId", audioId);
	}
	
	public java.lang.Integer getAudioId() {
		return getInt("audioId");
	}

	public void setContentType(java.lang.String contentType) {
		set("contentType", contentType);
	}
	
	public java.lang.String getContentType() {
		return getStr("contentType");
	}

}
