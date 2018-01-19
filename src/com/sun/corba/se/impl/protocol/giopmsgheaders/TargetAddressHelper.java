

package xxxx;




abstract public class TargetAddressHelper
{
  private static String  _id = "IDL:messages/TargetAddress:1.0";

  public static void insert (org.omg.CORBA.Any a, com.sun.corba.se.impl.protocol.giopmsgheaders.TargetAddress that)
  {
    org.omg.CORBA.portable.OutputStream out = a.create_output_stream ();
    a.type (type ());
    write (out, that);
    a.read_value (out.create_input_stream (), type ());
  }

  public static com.sun.corba.se.impl.protocol.giopmsgheaders.TargetAddress extract (org.omg.CORBA.Any a)
  {
    return read (a.create_input_stream ());
  }

  private static org.omg.CORBA.TypeCode __typeCode = null;
  synchronized public static org.omg.CORBA.TypeCode type ()
  {
    if (__typeCode == null)
    {
      org.omg.CORBA.TypeCode _disTypeCode0;
      _disTypeCode0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_short);
      _disTypeCode0 = org.omg.CORBA.ORB.init ().create_alias_tc (com.sun.corba.se.impl.protocol.giopmsgheaders.AddressingDispositionHelper.id (), "AddressingDisposition", _disTypeCode0);
      org.omg.CORBA.UnionMember[] _members0 = new org.omg.CORBA.UnionMember [3];
      org.omg.CORBA.TypeCode _tcOf_members0;
      org.omg.CORBA.Any _anyOf_members0;

      
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_short ((short)com.sun.corba.se.impl.protocol.giopmsgheaders.KeyAddr.value);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().get_primitive_tc (org.omg.CORBA.TCKind.tk_octet);
      _tcOf_members0 = org.omg.CORBA.ORB.init ().create_sequence_tc (0, _tcOf_members0);
      _members0[0] = new org.omg.CORBA.UnionMember (
        "object_key",
        _anyOf_members0,
        _tcOf_members0,
        null);

      
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_short ((short)com.sun.corba.se.impl.protocol.giopmsgheaders.ProfileAddr.value);
      _tcOf_members0 = org.omg.IOP.TaggedProfileHelper.type ();
      _members0[1] = new org.omg.CORBA.UnionMember (
        "profile",
        _anyOf_members0,
        _tcOf_members0,
        null);

      
      _anyOf_members0 = org.omg.CORBA.ORB.init ().create_any ();
      _anyOf_members0.insert_short ((short)com.sun.corba.se.impl.protocol.giopmsgheaders.ReferenceAddr.value);
      _tcOf_members0 = com.sun.corba.se.impl.protocol.giopmsgheaders.IORAddressingInfoHelper.type ();
      _members0[2] = new org.omg.CORBA.UnionMember (
        "ior",
        _anyOf_members0,
        _tcOf_members0,
        null);
      __typeCode = org.omg.CORBA.ORB.init ().create_union_tc (com.sun.corba.se.impl.protocol.giopmsgheaders.TargetAddressHelper.id (), "TargetAddress", _disTypeCode0, _members0);
    }
    return __typeCode;
  }

  public static String id ()
  {
    return _id;
  }

  public static com.sun.corba.se.impl.protocol.giopmsgheaders.TargetAddress read (org.omg.CORBA.portable.InputStream istream)
  {
    com.sun.corba.se.impl.protocol.giopmsgheaders.TargetAddress value = new com.sun.corba.se.impl.protocol.giopmsgheaders.TargetAddress ();
    short _dis0 = (short)0;
    _dis0 = istream.read_short ();
    switch (_dis0)
    {
      case com.sun.corba.se.impl.protocol.giopmsgheaders.KeyAddr.value:
        byte _object_key[] = null;
        int _len1 = istream.read_long ();
        _object_key = new byte[_len1];
        istream.read_octet_array (_object_key, 0, _len1);
        value.object_key (_object_key);
        break;
      case com.sun.corba.se.impl.protocol.giopmsgheaders.ProfileAddr.value:
        org.omg.IOP.TaggedProfile _profile = null;
        _profile = org.omg.IOP.TaggedProfileHelper.read (istream);
        value.profile (_profile);
        break;
      case com.sun.corba.se.impl.protocol.giopmsgheaders.ReferenceAddr.value:
        com.sun.corba.se.impl.protocol.giopmsgheaders.IORAddressingInfo _ior = null;
        _ior = com.sun.corba.se.impl.protocol.giopmsgheaders.IORAddressingInfoHelper.read (istream);
        value.ior (_ior);
        break;
      default:
        throw new org.omg.CORBA.BAD_OPERATION ();
    }
    return value;
  }

  public static void write (org.omg.CORBA.portable.OutputStream ostream, com.sun.corba.se.impl.protocol.giopmsgheaders.TargetAddress value)
  {
    ostream.write_short (value.discriminator ());
    switch (value.discriminator ())
    {
      case com.sun.corba.se.impl.protocol.giopmsgheaders.KeyAddr.value:
        ostream.write_long (value.object_key ().length);
        ostream.write_octet_array (value.object_key (), 0, value.object_key ().length);
        break;
      case com.sun.corba.se.impl.protocol.giopmsgheaders.ProfileAddr.value:
        org.omg.IOP.TaggedProfileHelper.write (ostream, value.profile ());
        break;
      case com.sun.corba.se.impl.protocol.giopmsgheaders.ReferenceAddr.value:
        com.sun.corba.se.impl.protocol.giopmsgheaders.IORAddressingInfoHelper.write (ostream, value.ior ());
        break;
      default:
        throw new org.omg.CORBA.BAD_OPERATION ();
    }
  }

}
