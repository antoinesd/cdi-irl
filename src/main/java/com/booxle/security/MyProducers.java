package com.booxle.security;

import com.booxle.BooxleException;

import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import static javax.crypto.KeyGenerator.getInstance;

/**
 * @author Antoine Sabot-Durand
 */
public class MyProducers {


    public final static String DEFAULT_CIPHER_TYPE = "Blowfish";

    @Produces
    @ApplicationScoped
    protected SecretKey secretKeyProducer() {
        SecretKey key = null;
        try {
            key = getInstance(DEFAULT_CIPHER_TYPE).generateKey();
            return key;
        } catch (Exception e) {
            throw new BooxleException(e);
        }


    }
}
