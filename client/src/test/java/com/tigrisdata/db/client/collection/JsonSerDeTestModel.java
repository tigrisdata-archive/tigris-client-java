package com.tigrisdata.db.client.collection;

import java.util.Date;
import java.util.Objects;

public class JsonSerDeTestModel {
  private Date createdAt;

  public JsonSerDeTestModel(Date createdAt) {
    this.createdAt = createdAt;
  }

  public JsonSerDeTestModel() {}

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JsonSerDeTestModel model = (JsonSerDeTestModel) o;
    return Objects.equals(createdAt, model.createdAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(createdAt);
  }
}
