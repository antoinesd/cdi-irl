package com.booxle;

import javax.enterprise.util.AnnotationLiteral;

/**
 * @author Antoine Sabot-Durand
 */
public class ForAlgoLiteral extends AnnotationLiteral<ForAlgo> implements ForAlgo {

    private String value;

    public ForAlgoLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
