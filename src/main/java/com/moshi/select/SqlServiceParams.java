package com.moshi.select;

public class SqlServiceParams {
  private String sql;
  private Object[] args;

  public SqlServiceParams() {
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public Object[] getArgs() {
    return args;
  }

  public void setArgs(Object[] args) {
    this.args = args;
  }
}
