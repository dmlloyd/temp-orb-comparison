


package com.sun.corba.ee.impl.encoding;






public interface TypeCodeReader extends MarshalInputStream {
    public void addTypeCodeAtPosition(TypeCodeImpl tc, int position);
    public TypeCodeImpl getTypeCodeAtPosition(int position);
    public void setEnclosingInputStream(InputStream enclosure);
    public TypeCodeReader getTopLevelStream();
    public int getTopLevelPosition();
    public int getPosition();
    public void printTypeMap();
}
