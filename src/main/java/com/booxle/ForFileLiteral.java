package com.booxle;

import javax.enterprise.util.AnnotationLiteral;

/**
 * @author Antoine Sabot-Durand
 */
public class ForFileLiteral extends AnnotationLiteral<ForFile> implements ForFile {

    private String value;

    public ForFileLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }
}
