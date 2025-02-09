package com.moshi.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAuthCode<M extends BaseAuthCode<M>> extends Model<M> implements IBean {

	public void setId(java.lang.String id) {
		set("id", id);
	}
	
	public java.lang.String getId() {
		return getStr("id");
	}

	public void setAccountId(java.lang.Integer accountId) {
		set("accountId", accountId);
	}
	
	public java.lang.Integer getAccountId() {
		return getInt("accountId");
	}

	public void setExpireAt(java.lang.Long expireAt) {
		set("expireAt", expireAt);
	}
	
	public java.lang.Long getExpireAt() {
		return getLong("expireAt");
	}

	public void setType(java.lang.Integer type) {
		set("type", type);
	}
	
	public java.lang.Integer getType() {
		return getInt("type");
	}

}
