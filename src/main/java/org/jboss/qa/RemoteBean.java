package org.jboss.qa;

public interface RemoteBean {
  void call();
  String call(String param);
  String callAndReturn();
}
