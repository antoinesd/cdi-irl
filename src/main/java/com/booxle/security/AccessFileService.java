package com.booxle.security;

import com.booxle.BooxleException;
import com.booxle.ForAlgo;
import com.booxle.ForAlgoLiteral;
import com.booxle.ForFile;
import com.booxle.StreamUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    private final String algo;

    @Inject
    @Any
    Instance<SecretKey> keys;

    private OutputStream os;

    private InputStream is;

    @Inject
    protected AccessFileService(InjectionPoint ip) {
        fileName = extractFileName(ip);
        algo = extractAlgo(ip);

    }

    private String extractFileName(InjectionPoint ip) {
        for (Annotation annotation : ip.getQualifiers()) {
            if (annotation.annotationType().equals(ForFile.class))
                return ((ForFile) annotation).value();
        }
        throw new IllegalStateException("No @ForFile on InjectionPoint");
    }

    private String extractAlgo(InjectionPoint ip) {
        String res = "";
        for (Annotation annotation : ip.getQualifiers()) {
            if (annotation.annotationType().equals(ForAlgo.class))
                res = ((ForAlgo) annotation).value();
        }
        return res;
    }

    @PostConstruct
    protected void init() {
        if ("".equals(algo)) {
            try {
                os = new FileOutputStream(fileName);
                is = new FileInputStream(fileName);
            } catch (FileNotFoundException e) {
                throw new BooxleException(e);
            }
        } else {
            SecretKey key = keys.select(new ForAlgoLiteral(algo)).get();
            String name = algo + "_" + fileName;
            try {
                Cipher cipherOut = Cipher.getInstance(algo);
                cipherOut.init(Cipher.ENCRYPT_MODE, key);
                os = new CipherOutputStream(new FileOutputStream(name), cipherOut);

                Cipher cipherIn = Cipher.getInstance(algo);
                cipherIn.init(Cipher.DECRYPT_MODE, key);

                is = new CipherInputStream(new FileInputStream(name), cipherIn);
            } catch (Exception e) {
                throw new BooxleException(e);
            }
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
