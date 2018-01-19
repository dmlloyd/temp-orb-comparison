
package xxxx;






public final class ValueMemberHelper
{
    private static String  _id = "IDL:omg.org/CORBA/ValueMember:1.0";

    public ValueMemberHelper()
    {
    }

    
    
    public static void insert (org.omg.CORBA.Any a, org.omg.CORBA.ValueMember that)
    {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
        a.type (type ());
        write (out, that);
        a.read_value (out.create_input_stream (), type ());
    }

    
    
    public static org.omg.CORBA.ValueMember extract (org.omg.CORBA.Any a)
    {
        return read (a.create_input_stream ());
    }

    private static org.omg.CORBA.TypeCode __typeCode = null;
    private static boolean __active = false;
    synchronized public static org.omg.CORBA.TypeCode type ()
    {
        if (__typeCode == null)
            {
                synchronized (org.omg.CORBA.TypeCode.class)
                    {
                        if (__typeCode == null)
                            {
                                if (__active)
                                    {
                                        return org.omg.CORBA.ORB.init().create_recursive_tc ( _id );
                                    }
                                __active = true;
                                org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [7];
                                org.omg.CORBA.TypeCode _tcOf_members0 = null;
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.IdentifierHelper.id (), "Identifier", _tcOf_members0);
                                _members0[0] = new org.omg.CORBA.StructMember (
                                                                               "name",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.RepositoryIdHelper.id (), "RepositoryId", _tcOf_members0);
                                _members0[1] = new org.omg.CORBA.StructMember (
                                                                               "id",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.RepositoryIdHelper.id (), "RepositoryId", _tcOf_members0);
                                _members0[2] = new org.omg.CORBA.StructMember (
                                                                               "defined_in",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.VersionSpecHelper.id (), "VersionSpec", _tcOf_members0);
                                _members0[3] = new org.omg.CORBA.StructMember (
                                                                               "version",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_TypeCode);
                                _members0[4] = new org.omg.CORBA.StructMember (
                                                                               "type",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = com.sun.org.omg.CORBA.IDLTypeHelper.type ();
                                _members0[5] = new org.omg.CORBA.StructMember (
                                                                               "type_def",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_short);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.VisibilityHelper.id (), "Visibility", _tcOf_members0);
                                _members0[6] = new org.omg.CORBA.StructMember (
                                                                               "access",
                                                                               _tcOf_members0,
                                                                               null);
                                __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (com.sun.org.omg.CORBA.ValueMemberHelper.id (), "ValueMember", _members0);
                                __active = false;
                            }
                    }
            }
        return __typeCode;
    }

    public static String id ()
    {
        return _id;
    }

    
    
    public static org.omg.CORBA.ValueMember read (org.omg.CORBA.portable.InputStream istream)
    {
        
        
        org.omg.CORBA.ValueMember value = new org.omg.CORBA.ValueMember ();
        value.name = istream.read_string ();
        value.id = istream.read_string ();
        value.defined_in = istream.read_string ();
        value.version = istream.read_string ();
        value.type = istream.read_TypeCode ();
        value.type_def = com.sun.org.omg.CORBA.IDLTypeHelper.read (istream);
        value.access = istream.read_short ();
        return value;
    }

    
    
    public static void write (org.omg.CORBA.portable.OutputStream ostream, org.omg.CORBA.ValueMember value)
    {
        ostream.write_string (value.name);
        ostream.write_string (value.id);
        ostream.write_string (value.defined_in);
        ostream.write_string (value.version);
        ostream.write_TypeCode (value.type);
        com.sun.org.omg.CORBA.IDLTypeHelper.write (ostream, value.type_def);
        ostream.write_short (value.access);
    }

}
