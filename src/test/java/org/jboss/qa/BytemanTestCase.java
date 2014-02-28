package org.jboss.qa;

import java.rmi.RemoteException;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.byteman.agent.submit.Submit;
import org.jboss.byteman.contrib.dtest.Instrumentor;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BytemanTestCase {
  private final Logger log = Logger.getLogger(BytemanTestCase.class);
  private static final String DEPLOYMENT_NAME = "bytemantest";
  
  protected static Instrumentor instrumentor = null;
  protected static Submit instrumentorSubmit = null;
  
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
  
  @BeforeClass
  public static void beforeClass() throws RemoteException {
    instrumentorSubmit = new Submit(TestProperties.JBOSS_ADDRESS, TestProperties.BYTEMAN_PORT);
    instrumentor = new Instrumentor(instrumentorSubmit, TestProperties.BYTEMAN_RMI_REGISTRY_PORT);
  }
  
  @After
  public void tearDown() {
    try {
      instrumentor.removeLocalState();
      instrumentor.removeAllInstrumentation();
    } catch (Exception e) {
      log.debug("Instrumentator fails with removing instrumentations", e);
    }
  }
  
  @Test
  public void call() {
    syncBean.call();
    asyncBean.call();
  }
  
  @Test
  public void bytemanCall() {
    
  }

}
