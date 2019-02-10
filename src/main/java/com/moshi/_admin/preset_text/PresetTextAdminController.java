package com.moshi._admin.preset_text;

import com.jfinal.kit.Ret;
import com.moshi.common.controller.BaseController;
import com.moshi.common.model.PresetText;

public class PresetTextAdminController extends BaseController {
  private PresetText dao = new PresetText().dao();

  public void saveOrUpdate(String key, String value, String type) {
    PresetText presetText = dao.findById(key);
    boolean ok = false;
    if (presetText == null) {
      presetText = new PresetText();
      presetText.setKey(key);
      presetText.setValue(value);
      presetText.setType(type);
      ok = presetText.save();
    } else {
      presetText.setValue(value);
      presetText.setType(type);
      ok = presetText.update();
    }
    renderJson(ok ? Ret.ok() : Ret.fail("msg", "保存/更新失败"));
  }

  public void delete(String key) {
    boolean ok = dao.deleteById(key);
    renderJson(ok ? Ret.ok() : Ret.fail("msg", "删除失败"));
  }
}
