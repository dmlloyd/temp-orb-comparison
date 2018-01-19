package org.jboss.javax.rmi;

import java.security.Permission;


public class RemoteObjectSubstitutionManager {


    private static final Permission REMOTE_OBJECT_SUBSTITUTION = new RuntimePermission("remoteObjectSubstitution");

    private static volatile RemoteObjectSubstitution remoteObjectSubstitution;


    public static Object writeReplaceRemote(Object remote) {
        RemoteObjectSubstitution replace = remoteObjectSubstitution;
        if (replace == null) {
            return remote;
        }
        return replace.writeReplaceRemote(remote);
    }


    public static RemoteObjectSubstitution getRemoteObjectSubstitution() {
        return remoteObjectSubstitution;
    }

    public static void setRemoteObjectSubstitution(final RemoteObjectSubstitution remoteObjectSubstitution) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(REMOTE_OBJECT_SUBSTITUTION);
        }
        RemoteObjectSubstitutionManager.remoteObjectSubstitution = remoteObjectSubstitution;
    }
}
