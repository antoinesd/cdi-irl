package com.booxle.security;

import com.booxle.ForAlgo;
import com.booxle.ForFileLiteral;
import com.booxle.extension.DynamicAlgoExtension;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    @ForAlgo("AES")
    Instance<AccessFileService> afsAESInstances;

    @Inject
    @ForAlgo
    Instance<AccessFileService> afsDefaultInstances;

    @Inject
    @ForAlgo("")
    Instance<AccessFileService> afsInstances;

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {

        JavaArchive[] libs = Maven.resolver()
                .loadPomFromFile("pom.xml")
                .resolve("org.apache.deltaspike.core:deltaspike-core-impl")
                .withTransitivity().as(JavaArchive.class);

        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addAsLibraries(libs)
                .addAsResource("META-INF/services/javax.enterprise.inject.spi.Extension")
                .addClasses(DynamicAlgoExtension.class, AccessFileService.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return ret;
    }

    public AccessFileService getAccessFileService(Instance<AccessFileService> iafs, String filename) {
        return iafs.select(new ForFileLiteral(filename)).get();
    }

    @Test
    public void writeTest() throws IOException {
        AccessFileService afs = getAccessFileService(afsInstances, "AnotherTestFile.txt");
        afs.write("hello world");
        Assert.assertTrue(Files.exists(Paths.get("AnotherTestFile.txt")));

    }

    @Test
    public void writeAndReadTestAES() throws IOException {
        AccessFileService afs = getAccessFileService(afsAESInstances, "AnotherOtherTestFile.txt");


        afs.write("hello world");

        String readed = afs.read();

        Assert.assertTrue("hello world".equals(readed));

    }

    @Test
    public void writeAndReadTestDefault() throws IOException {
        AccessFileService afs = getAccessFileService(afsDefaultInstances, "AnotherOtherTestFile.txt");


        afs.write("hello world");

        String readed = afs.read();

        Assert.assertTrue("hello world".equals(readed));

    }

    @Test
    public void writeAndReadTest() throws IOException {
        AccessFileService afs = getAccessFileService(afsInstances, "AnotherOtherTestFile.txt");


        afs.write("hello world");

        String readed = afs.read();

        Assert.assertTrue("hello world".equals(readed));

    }


}
