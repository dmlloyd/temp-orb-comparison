



package com.sun.corba.ee.impl.util;


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
