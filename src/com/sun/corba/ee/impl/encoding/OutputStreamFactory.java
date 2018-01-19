

package com.sun.corba.ee.impl.encoding;

import java.security.AccessController;
import java.security.PrivilegedAction;

import com.sun.corba.ee.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.corba.ee.spi.orb.ORB;
import com.sun.corba.ee.spi.protocol.MessageMediator;
import com.sun.corba.ee.spi.transport.Connection;


public class OutputStreamFactory {
	public static TypeCodeOutputStream newTypeCodeOutputStream(final ORB orb) {
		return AccessController.doPrivileged(new PrivilegedAction<TypeCodeOutputStream>() {

			@Override
			public TypeCodeOutputStream run() {
				return new TypeCodeOutputStream(orb);
			}
			
		});
    }
	
	public static EncapsOutputStream newEncapsOutputStream(final ORB orb,
			final GIOPVersion version) {
		return AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {

			@Override
			public EncapsOutputStream run() {
				return new EncapsOutputStream(orb, version);
			}
			
		});	
	}
	
	public static EncapsOutputStream newEncapsOutputStream(final ORB orb) {
		return AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {

			@Override
			public EncapsOutputStream run() {
				return new EncapsOutputStream(orb);
			}
			
		});
		
	}
	
	public static CDROutputObject newCDROutputObject(final ORB orb,
			final MessageMediator mediator,
			final GIOPVersion giopVersion,
            final Connection connection,
            final Message header,
            final byte streamFormatVersion) {
		return AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {

			@Override
			public CDROutputObject run() {
				return new CDROutputObject(orb, mediator, giopVersion, connection, header, streamFormatVersion);
			}
		});
	}
	
	public static CDROutputObject newCDROutputObject(final ORB orb,
			final MessageMediator messageMediator,
			final Message header,
			final byte streamFormatVersion) {
		return AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {

			@Override
			public CDROutputObject run() {
				return new CDROutputObject(orb, messageMediator, header, streamFormatVersion);
			}
		});
		
	}
	
	public static CDROutputObject newCDROutputObject(final ORB orb,
			final MessageMediator messageMediator,
            final Message header,
            final byte streamFormatVersion,
            final int strategy) {
		return AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {

			@Override
			public CDROutputObject run() {
				return new CDROutputObject(orb, messageMediator, header, streamFormatVersion, strategy);
			}
		});
		
	}
}
