package com.moshi.common.model2.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePermission<M extends BasePermission<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setActionKey(java.lang.String actionKey) {
		set("actionKey", actionKey);
	}
	
	public java.lang.String getActionKey() {
		return getStr("actionKey");
	}

	public void setController(java.lang.String controller) {
		set("controller", controller);
	}
	
	public java.lang.String getController() {
		return getStr("controller");
	}

	public void setRemark(java.lang.String remark) {
		set("remark", remark);
	}
	
	public java.lang.String getRemark() {
		return getStr("remark");
	}

}
