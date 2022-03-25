package com.tigrisdata.db.client.model;

import org.junit.Assert;
import org.junit.Test;

public class FiltersTest {

  @Test
  public void equalFilterTest() {
    Assert.assertEquals("{\"$eq\":{\"k1\":123}}", Filters.eq("k1", 123).toString());
    Assert.assertEquals("{\"$eq\":{\"k2\":false}}", Filters.eq("k2", false).toString());
    Assert.assertEquals("{\"$eq\":{\"k3\":true}}", Filters.eq("k3", true).toString());
    Assert.assertEquals("{\"$eq\":{\"k4\":\"val1\"}}", Filters.eq("k4", "val1").toString());
  }

  @Test
  public void orFilterTest() {
    Assert.assertEquals(
        "{\"$or\":[{\"$eq\":{\"k1\":123}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val3\"}}]",
        Filters.or(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"))
            .toString());
  }

  @Test
  public void andFilterTest() {
    Assert.assertEquals(
        "{\"$and\":[{\"$eq\":{\"k1\":123}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val3\"}}]",
        Filters.and(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"))
            .toString());
  }

  @Test
  public void nestedFilterTest1() {
    TigrisFilter filter1 =
        Filters.and(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.and(Filters.eq("k1", 456), Filters.eq("k2", false), Filters.eq("k3", "val4"));

    Assert.assertEquals(
        "{\"$or\":[{\"$and\":[{\"$eq\":{\"k1\":123}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val3\"}}],"
            + "{\"$and\":[{\"$eq\":{\"k1\":456}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val4\"}}]]",
        Filters.or(filter1, filter2).toString());
  }

  @Test
  public void nestedFilterTest2() {
    TigrisFilter filter1 =
        Filters.and(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.and(Filters.eq("k4", 456), Filters.eq("k5", false), Filters.eq("k6", "val4"));

    Assert.assertEquals(
        "{\"$and\":[{\"$and\":[{\"$eq\":{\"k1\":123}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val3\"}}],"
            + "{\"$and\":[{\"$eq\":{\"k4\":456}},{\"$eq\":{\"k5\":false}},{\"$eq\":{\"k6\":\"val4\"}}]]",
        Filters.and(filter1, filter2).toString());
  }

  @Test
  public void nestedFilterTest3() {
    TigrisFilter filter1 =
        Filters.or(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.or(Filters.eq("k1", 456), Filters.eq("k2", false), Filters.eq("k3", "val4"));

    Assert.assertEquals(
        "{\"$and\":[{\"$or\":[{\"$eq\":{\"k1\":123}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val3\"}}],"
            + "{\"$or\":[{\"$eq\":{\"k1\":456}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val4\"}}]]",
        Filters.and(filter1, filter2).toString());
  }

  @Test
  public void nestedFilterTest4() {
    TigrisFilter filter1 =
        Filters.or(Filters.eq("k1", 123), Filters.eq("k2", false), Filters.eq("k3", "val3"));
    TigrisFilter filter2 =
        Filters.or(Filters.eq("k4", 456), Filters.eq("k5", false), Filters.eq("k6", "val4"));

    Assert.assertEquals(
        "{\"$and\":[{\"$or\":[{\"$eq\":{\"k1\":123}},{\"$eq\":{\"k2\":false}},{\"$eq\":{\"k3\":\"val3\"}}],"
            + "{\"$or\":[{\"$eq\":{\"k4\":456}},{\"$eq\":{\"k5\":false}},{\"$eq\":{\"k6\":\"val4\"}}]]",
        Filters.and(filter1, filter2).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCompositeFilterTest1() {
    //noinspection ResultOfMethodCallIgnored
    Filters.or(Filters.eq("k1", 123));
  }

  @Test(expected = IllegalArgumentException.class)
  public void invalidCompositeFilterTest2() {
    //noinspection ResultOfMethodCallIgnored
    Filters.and(Filters.eq("k1", 123));
  }
}
