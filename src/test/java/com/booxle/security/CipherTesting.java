package com.booxle.security;

import com.booxle.ForFileLiteral;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Antoine Sabot-Durand
 */
@RunWith(Arquillian.class)
public class CipherTesting {

    @Inject
    @Any
    Instance<AccessFileService> afsInstances;

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {


        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClasses(MyProducers.class, AccessFileService.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return ret;
    }

    public AccessFileService getAccessFileService(String filename) {
        return afsInstances.select(new ForFileLiteral(filename)).get();
    }

    @Test
    public void writeTest() throws IOException {
        AccessFileService afs = getAccessFileService("AnotherTestFile.txt");
        afs.write("hello world");
        Assert.assertTrue(Files.exists(Paths.get("AnotherTestFile.txt")));

    }

    @Test
    public void writeAndReadTest() throws IOException {
        AccessFileService afs = getAccessFileService("AnotherOtherTestFile.txt");


        afs.write("hello world");

        String readed = afs.read();

        Assert.assertTrue("hello world".equals(readed));

    }


}
