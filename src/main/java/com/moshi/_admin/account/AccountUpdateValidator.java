
package com.moshi._admin.account;

import com.moshi.common.kit.SensitiveWordsKit;
import com.moshi.reg.RegValidator;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import com.jfinal.validate.Validator;

/**
 * AccountUpdateValidator 验证账号修改功能表单
 */
public class AccountUpdateValidator extends Validator {
	
	protected void validate(Controller c) {
		setShortCircuit(true);

		/**
		 * 验证 nickName
 		 */
		if (SensitiveWordsKit.checkSensitiveWord(c.getPara("nickName")) != null) {
			addError("msg", "昵称不能包含敏感词");
		}
		validateRequired("nickName", "msg", "昵称不能为空");
		validateString("nickName", 1, 19, "msg", "昵称不能超过19个字");

		String nickName = c.getPara("nickName").trim();
		if (nickName.contains("@") || nickName.contains("＠")) { // 全角半角都要判断
			addError("msg", "昵称不能包含 \"@\" 字符");
		}
		if (nickName.contains(" ") || nickName.contains("　")) {
			addError("msg", "昵称不能包含空格");
		}
		Ret ret = RegValidator.validateNickName(nickName);
		if (ret.isFail()) {
			addError("msg", ret.getStr("msg"));
		}

		/**
		 * 验证 email
		 */
		validateRequired("email", "msg", "邮箱不能为空");
		validateEmail("email", "msg", "邮箱格式不正确");
	}

	protected void handleError(Controller c) {
		c.setAttr("state", "fail");
		c.renderJson();
	}
}

