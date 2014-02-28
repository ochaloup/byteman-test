package org.jboss.qa;

public class TestProperties {
  public static final String JBOSS_CONTAINER_MANUAL = "jboss_manual";
  public static final String JBOSS_CONTAINER_MANAGED = "jboss_managed";
  
  public static final String JBOSS_ADDRESS = System.getProperty("jboss.address", "localhost");
  
  public static final int BYTEMAN_PORT = Integer.getInteger("byteman.port.number", 9091);
  public static final int BYTEMAN_RMI_REGISTRY_PORT = Integer.getInteger("byteman.rmi.registry.port.number", 1199);
}
