package org.jboss.qa;

import javax.ejb.Remote;

@Remote
public interface RemoteBean {
  void callVoid();
  String call(String param);
  String callAndReturn();
}
