package org.jboss.qa;

import java.rmi.RemoteException;
import java.util.concurrent.Future;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.TargetsContainer;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.byteman.agent.submit.Submit;
import org.jboss.byteman.contrib.dtest.Instrumentor;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class BytemanTestCase {
  private final Logger log = Logger.getLogger(BytemanTestCase.class);
  private static final String DEPLOYMENT_NAME = "bytemantest";
  
  protected static Instrumentor instrumentor = null;
  protected static Submit instrumentorSubmit = null;
  
  @Inject
  RemoteBeanAsync asyncBean;
  
  @Inject
  RemoteBean syncBean;
  
  @Deployment(name = DEPLOYMENT_NAME)
  @TargetsContainer(TestProperties.JBOSS_CONTAINER_MANAGED)
  public static JavaArchive createDeployment() {
    return ShrinkWrap.create(JavaArchive.class, DEPLOYMENT_NAME + ".jar")
        .addPackage(RemoteBean.class.getPackage());
        // .addAsManifestResource(new StringAsset("Dependencies: org.jboss.jts\n"), "MANIFEST.MF");
  }
  
  @Before
  public void beforeClass() throws RemoteException {
    if(instrumentor == null) {
      instrumentorSubmit = new Submit(TestProperties.JBOSS_ADDRESS, TestProperties.BYTEMAN_PORT);
      instrumentor = new Instrumentor(instrumentorSubmit, TestProperties.BYTEMAN_RMI_REGISTRY_PORT);
    }
  }
  
  @After
  public void tearDown() {
    try {
      instrumentor.removeLocalState();
      instrumentor.removeAllInstrumentation();
    } catch (Exception e) {
      log.info("Instrumentator fails with removing instrumentations", e);
    }
  }
  
  @Test
  public void call() {
    syncBean.call();
    asyncBean.call();
  }
  
  @Test
  @Ignore
  public void bytemanCrash() throws Exception {
    instrumentor.crashAtMethodEntry(SLSBean.class, "call");
    syncBean.call();
  }

  @Test
  public void waitFor() throws Exception {
    String waitForName = "slsbeanwait";
    String waitScriptString = "RULE wait for call \n" +
        "CLASS " + SLSBeanAsync.class.getName() + "\n" +
        "METHOD callAndReturn \n" +
        // "HELPER org.jboss.byteman.contrib.dtest.BytemanTestHelper \n " +
        // "AT ENTRY \n" +
        "BIND NOTHING \n" +
        "IF TRUE \n" +
        "DO waitFor(\"" + waitForName + "\") \n" +
        "ENDRULE";
    instrumentor.installScript("waitForSyncEJBCall", waitScriptString);
    
    String awakeScriptString= "RULE awake sleeping one \n" +
        "CLASS " + SLSBean.class.getName() + "\n" +
        "METHOD call \n" +
        // "HELPER org.jboss.byteman.contrib.dtest.BytemanTestHelper \n " +
        // "AT ENTRY \n" +
        "BIND NOTHING \n" +
        "IF TRUE \n" +
        "DO signalWake(\"" + waitForName + "\", true) \n" +
        "ENDRULE";
    instrumentor.installScript("awakeSleepingAsyncBean", awakeScriptString);
    
    Future<String> future = asyncBean.callAndReturn();
    log.info("Waiting for 3 seconds");
    Thread.sleep(3000);
    Assert.assertFalse("Expecting that future object is not done byteman script stopped it", future.isDone());
    
    syncBean.call();
    Thread.sleep(1000);
    Assert.assertTrue("Expecting that future is returned as byteman script for awaken was already called", future.isDone());
  }
}
