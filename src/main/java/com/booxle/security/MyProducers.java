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

import static javax.crypto.KeyGenerator.getInstance;

/**
 * @author Antoine Sabot-Durand
 */
public class MyProducers {


    final static String LOG_FILE = "./logsecret.txt";

    private final static String CIPHER_TYPE = "Blowfish";

    @Produces
    @ForFile
    protected CipherOutputStream cipherOutStreamProducer(SecretKey key, InjectionPoint ip) {

        String fileName = ip.getAnnotated().getAnnotation(ForFile.class).value();

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
        String fileName = ip.getAnnotated().getAnnotation(ForFile.class).value();

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
