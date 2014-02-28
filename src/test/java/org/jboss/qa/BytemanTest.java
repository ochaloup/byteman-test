package org.jboss.qa;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BytemanTest {
  private static final String DEPLOYMENT_NAME = "bytemantest";
  
  @EJB
  RemoteBeanAsync asyncBean;
  
  @EJB
  RemoteBean syncBean;
  
  @Deployment(name = DEPLOYMENT_NAME)
  @TargetsContainer("jboss_managed")
  public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class, DEPLOYMENT_NAME + ".jar")
        .addPackage(RemoteBean.class.getPackage());
        // .addAsManifestResource(new StringAsset("Dependencies: org.jboss.jts\n"), "MANIFEST.MF");
  }
  
  @Test
  public void waitFor() {
    syncBean.call();
  }

}
