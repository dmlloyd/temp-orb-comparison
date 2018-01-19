


package com.sun.corba.ee.impl.oa.poa;


final class ServantRetentionPolicyImpl
    extends org.omg.CORBA.LocalObject implements ServantRetentionPolicy {

    private static final long serialVersionUID = 469062222833983100L;

    public ServantRetentionPolicyImpl(ServantRetentionPolicyValue value) {
        this.value = value;
    }

    public ServantRetentionPolicyValue value() {
        return value;
    }

    public int policy_type()
    {
        return SERVANT_RETENTION_POLICY_ID.value ;
    }

    public Policy copy() {
        return new ServantRetentionPolicyImpl(value);
    }

    public void destroy() {
        value = null;
    }

    private ServantRetentionPolicyValue value;

    @Override
    public String toString()
    {
        return "ServantRetentionPolicy[" +
            ((value.value() == ServantRetentionPolicyValue._RETAIN) ?
                "RETAIN" : "NON_RETAIN" + "]") ;
    }
}
