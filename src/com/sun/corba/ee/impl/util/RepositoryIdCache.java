



package com.sun.corba.ee.impl.util;

import java.util.Stack;
import java.util.Hashtable;
import java.util.EmptyStackException;
import java.util.Enumeration;

public class RepositoryIdCache extends Hashtable {
    public final synchronized RepositoryId getId(String key) {
        RepositoryId repId = (RepositoryId)super.get(key);

        if (repId != null) {
            return repId;
        } else {
            repId = new RepositoryId(key);
            put(key, repId);
            return repId;
        }
    }
}
