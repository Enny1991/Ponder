package com.eneaceolini.ponder;

import java.io.Serializable;

/**
 * Simple container object for contact data
 *
 * Created by mgod on 9/12/13.
 * @author mgod
 */
public class Person implements Serializable{
    private String tag;

    public Person(String t) {
        tag = t;
    }

    public String getTag() { return tag; }

    @Override
    public String toString() { return tag; }
}
