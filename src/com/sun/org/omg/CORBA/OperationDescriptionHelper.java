
package com.sun.org.omg.CORBA;




public final class OperationDescriptionHelper
{
    private static String  _id = "IDL:omg.org/CORBA/OperationDescription:1.0";

    public OperationDescriptionHelper()
    {
    }

    public static void insert (org.omg.CORBA.Any a, com.sun.org.omg.CORBA.OperationDescription that)
    {
        org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
        a.type (type ());
        write (out, that);
        a.read_value (out.create_input_stream (), type ());
    }

    public static com.sun.org.omg.CORBA.OperationDescription extract (org.omg.CORBA.Any a)
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
                                org.omg.CORBA.StructMember[] _members0 = new org.omg.CORBA.StructMember [9];
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
                                                                               "result",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = com.sun.org.omg.CORBA.OperationModeHelper.type ();
                                _members0[5] = new org.omg.CORBA.StructMember (
                                                                               "mode",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_string_tc (0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.IdentifierHelper.id (), "Identifier", _tcOf_members0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.ContextIdentifierHelper.id (), "ContextIdentifier", _tcOf_members0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.ContextIdSeqHelper.id (), "ContextIdSeq", _tcOf_members0);
                                _members0[6] = new org.omg.CORBA.StructMember (
                                                                               "contexts",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = com.sun.org.omg.CORBA.ParameterDescriptionHelper.type ();
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.ParDescriptionSeqHelper.id (), "ParDescriptionSeq", _tcOf_members0);
                                _members0[7] = new org.omg.CORBA.StructMember (
                                                                               "parameters",
                                                                               _tcOf_members0,
                                                                               null);
                                _tcOf_members0 = com.sun.org.omg.CORBA.ExceptionDescriptionHelper.type ();
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
                                _tcOf_members0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.org.omg.CORBA.ExcDescriptionSeqHelper.id (), "ExcDescriptionSeq", _tcOf_members0);
                                _members0[8] = new org.omg.CORBA.StructMember (
                                                                               "exceptions",
                                                                               _tcOf_members0,
                                                                               null);
                                __typeCode = org.omg.CORBA.ORB.init ().create_struct_tc (com.sun.org.omg.CORBA.OperationDescriptionHelper.id (), "OperationDescription", _members0);
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

    public static com.sun.org.omg.CORBA.OperationDescription read (org.omg.CORBA.portable.InputStream istream)
    {
        com.sun.org.omg.CORBA.OperationDescription value = new com.sun.org.omg.CORBA.OperationDescription ();
        value.name = istream.read_string ();
        value.id = istream.read_string ();
        value.defined_in = istream.read_string ();
        value.version = istream.read_string ();
        value.result = istream.read_TypeCode ();
        value.mode = com.sun.org.omg.CORBA.OperationModeHelper.read (istream);
        value.contexts = com.sun.org.omg.CORBA.ContextIdSeqHelper.read (istream);
        value.parameters = com.sun.org.omg.CORBA.ParDescriptionSeqHelper.read (istream);
        value.exceptions = com.sun.org.omg.CORBA.ExcDescriptionSeqHelper.read (istream);
        return value;
    }

    public static void write (org.omg.CORBA.portable.OutputStream ostream, com.sun.org.omg.CORBA.OperationDescription value)
    {
        ostream.write_string (value.name);
        ostream.write_string (value.id);
        ostream.write_string (value.defined_in);
        ostream.write_string (value.version);
        ostream.write_TypeCode (value.result);
        com.sun.org.omg.CORBA.OperationModeHelper.write (ostream, value.mode);
        com.sun.org.omg.CORBA.ContextIdSeqHelper.write (ostream, value.contexts);
        com.sun.org.omg.CORBA.ParDescriptionSeqHelper.write (ostream, value.parameters);
        com.sun.org.omg.CORBA.ExcDescriptionSeqHelper.write (ostream, value.exceptions);
    }

}
