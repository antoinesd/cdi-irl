package com.booxle.security;

import com.booxle.BooxleException;
import com.booxle.ForFile;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Annotation;

import static javax.crypto.KeyGenerator.getInstance;

/**
 * @author Antoine Sabot-Durand
 */
public class MyProducers {

    private final static String CIPHER_TYPE = "Blowfish";


    private String extractFileName(InjectionPoint ip) {
        for (Annotation annotation : ip.getQualifiers()) {
            if (annotation.annotationType().equals(ForFile.class))
                return ((ForFile) annotation).value();
        }
        throw new IllegalStateException("No @ForFile on InjectionPoint");
    }

    @Produces
    @ForFile
    protected CipherOutputStream cipherOutStreamProducer(SecretKey key, InjectionPoint ip) {
        String fileName = extractFileName(ip);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return new CipherOutputStream(new FileOutputStream(fileName), cipher);
        } catch (Exception e) {
            throw new BooxleException(e);
        }
    }

    @Produces
    @ForFile
    protected CipherInputStream cipherInStreamProducer(SecretKey key, InjectionPoint ip) {
        String fileName = extractFileName(ip);
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_TYPE);
            cipher.init(Cipher.DECRYPT_MODE, key);

            return new CipherInputStream(new FileInputStream(fileName), cipher);
        } catch (Exception e) {
            throw new BooxleException(e);
        }
    }

    @Produces
    @ApplicationScoped
    protected SecretKey secretKeyProducer() {
        SecretKey key = null;
        try {
            key = getInstance(CIPHER_TYPE).generateKey();
            return key;
        } catch (Exception e) {
            throw new BooxleException(e);
        }


    }
}
