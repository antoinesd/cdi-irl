package com.booxle.security;

import com.booxle.BooxleException;
import com.booxle.ForFile;
import com.booxle.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;

/**
 * @author Antoine Sabot-Durand
 */

@ForFile
public class AccessFileService {

    private final String fileName;

    private OutputStream os;

    private InputStream is;

    @Inject
    private SecretKey key;

    @Inject
    protected AccessFileService(InjectionPoint ip) {
        fileName = extractFileName(ip);

    }

    private String extractFileName(InjectionPoint ip) {
        for (Annotation annotation : ip.getQualifiers()) {
            if (annotation.annotationType().equals(ForFile.class))
                return ((ForFile) annotation).value();
        }
        throw new IllegalStateException("No @ForFile on InjectionPoint");
    }

    @PostConstruct
    protected void init() {
        try {
            Cipher cipherOut = Cipher.getInstance(MyProducers.DEFAULT_CIPHER_TYPE);
            cipherOut.init(Cipher.ENCRYPT_MODE, key);
            os = new CipherOutputStream(new FileOutputStream(fileName), cipherOut);

            Cipher cipherIn = Cipher.getInstance(MyProducers.DEFAULT_CIPHER_TYPE);
            cipherIn.init(Cipher.DECRYPT_MODE, key);

            is = new CipherInputStream(new FileInputStream(fileName), cipherIn);
        } catch (Exception e) {
            throw new BooxleException(e);
        }
    }

    public void write(String txt) {
        try {
            os.write(txt.getBytes());
            os.close();
        } catch (IOException e) {
            throw new BooxleException(e);
        }
    }

    public String read() {
        return StreamUtils.getStreamContents(is);
    }


    @PreDestroy
    protected void destroy() {
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            throw new BooxleException(e);
        }
    }

}
