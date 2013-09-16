package com.booxle.security;

import com.booxle.ForFile;
import com.booxle.StreamUtils;
import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Antoine Sabot-Durand
 */
@RunWith(Arquillian.class)
public class CipherTesting {

    @Inject
    @ForFile("testfile.txt")
    OutputStream os;

    @Inject
    @ForFile("testfile.txt")
    InputStream is;

    @Deployment
    public static Archive<?> createTestArchive() throws FileNotFoundException {


        WebArchive ret = ShrinkWrap
                .create(WebArchive.class, "test.war")
                .addClasses(MyProducers.class)
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        return ret;
    }

    @Test
    public void writeTest() throws IOException {

        os.write("hello world".getBytes());
        Assert.assertTrue(Files.exists(Paths.get(MyProducers.LOG_FILE)));

    }

    @Test
    public void writeAndReadTest() throws IOException {

        os.write("hello world".getBytes());
        os.close();

        String readed = StreamUtils.getStreamContents(is);

        Assert.assertTrue("hello world".equals(readed));

    }


}
