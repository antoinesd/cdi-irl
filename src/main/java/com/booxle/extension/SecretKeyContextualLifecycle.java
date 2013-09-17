package com.booxle.extension;

import com.booxle.BooxleException;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

import javax.crypto.SecretKey;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import static javax.crypto.KeyGenerator.getInstance;

/**
 * @author Antoine Sabot-Durand
 */
public class SecretKeyContextualLifecycle implements ContextualLifecycle<SecretKey> {

    private final String algo;


    public SecretKeyContextualLifecycle(String algo) {
        this.algo = algo;

    }

    @Override
    public SecretKey create(Bean<SecretKey> bean, CreationalContext<SecretKey> creationalContext) {
        SecretKey instance = null;
        try {
            instance = getInstance(algo).generateKey();
        } catch (Exception e) {
            throw new BooxleException(e);
        }
        return instance;

    }

    @Override
    public void destroy(Bean<SecretKey> bean, SecretKey instance, CreationalContext<SecretKey> creationalContext) {
    }
}
