package com.booxle.extension;

import com.booxle.ForAlgo;
import com.booxle.ForAlgoLiteral;
import com.booxle.security.AccessFileService;
import org.apache.deltaspike.core.util.bean.BeanBuilder;
import org.apache.deltaspike.core.util.bean.WrappingBeanBuilder;
import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import javax.crypto.SecretKey;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Antoine Sabot-Durand
 */
public class DynamicAlgoExtension implements Extension {

    private Set<String> allAlgos = new HashSet<String>();

    private Set<String> algosWithExplicitKeys = new HashSet<String>();

    private Bean<?> beanAF;

    void retrieveAllAlgoInjected(@Observes ProcessAnnotatedType<?> pat) {
        AnnotatedType<?> at = pat.getAnnotatedType();
        for (AnnotatedField<?> af : at.getFields()) {
            if (af.isAnnotationPresent(Inject.class) && af.isAnnotationPresent(ForAlgo.class)) {
                allAlgos.add(af.getAnnotation(ForAlgo.class).value());
            }
        }
    }

    void processKeyProducer(@Observes ProcessProducer<?, SecretKey> pp) {
        AnnotatedMember<?> am = pp.getAnnotatedMember();
        if (am.isAnnotationPresent(ForAlgo.class))
            algosWithExplicitKeys.add(am.getAnnotation(ForAlgo.class).value());

    }

    void captureAccessFileServiceBean(@Observes ProcessBean<AccessFileService> pb) {
        if (beanAF == null)
            beanAF = pb.getBean();

    }

    void registerBeansForAlgo(@Observes AfterBeanDiscovery abd, BeanManager bm) {

        for (String a : allAlgos) {
            Annotation qual = new ForAlgoLiteral(a);

            //Registering Keys
            if (!algosWithExplicitKeys.contains(a)) {
                AnnotatedType<SecretKey> at = new AnnotatedTypeBuilder<SecretKey>().setJavaClass(SecretKey.class)
                        .readFromType(SecretKey.class).addToClass(qual).create();

                BeanBuilder<SecretKey> bb = new BeanBuilder<SecretKey>(bm);
                bb.readFromType(at)
                        .scope(ApplicationScoped.class)
                        .beanLifecycle(new SecretKeyContextualLifecycle(a))
                        .passivationCapable(true);

                abd.addBean(bb.create());
            }

            //Registering AccessFileService
            AnnotatedTypeBuilder<AccessFileService> atb = new AnnotatedTypeBuilder<AccessFileService>()
                    .readFromType(AccessFileService.class)

                    .addToClass(qual);

            WrappingBeanBuilder<AccessFileService> wbb = new WrappingBeanBuilder<AccessFileService>((Bean<Object>) beanAF, bm)
                    .readFromType(atb.create());

            abd.addBean(wbb.create());


        }


    }

}
