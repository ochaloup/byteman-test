package org.jboss.qa;

import java.util.concurrent.Future;

public interface RemoteBeanAsync {
  void call();
  Future<String> call(String param);
  Future<String> callAndReturn();
}
