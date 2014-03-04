package org.jboss.qa.manual;

import org.jboss.arquillian.container.test.api.ContainerController;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.qa.RemoteBean;
import org.jboss.qa.common.TestProperties;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BytemanScriptTestCase {
  private static final Logger log = Logger.getLogger(BytemanScriptTestCase.class);
  
  private static final String DEPLOYMENT_NAME = "byteman-script";
  
  @ArquillianResource
  protected ContainerController controller;
  @ArquillianResource
  protected Deployer deployer;

  @Deployment(name = DEPLOYMENT_NAME)
  @TargetsContainer(TestProperties.JBOSS_CONTAINER_MANUAL)
  public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class, DEPLOYMENT_NAME + ".jar")
        .addPackage(RemoteBean.class.getPackage());
  }
  
  @Test
  public void test() {
    log.info("method test");
  }
}
