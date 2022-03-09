package com.tigrisdata.db.client.service;

import com.tigrisdata.db.client.model.TigrisCollectionType;

public class C1 implements TigrisCollectionType {
  private String msg;

  public C1() {}

  public C1(String msg) {
    this.msg = msg;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
