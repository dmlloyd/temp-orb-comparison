

package com.sun.corba.ee.impl.encoding;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;

import com.sun.corba.ee.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;


public class EncapsInputStreamFactory {
	public static EncapsInputStream newEncapsInputStream(final EncapsInputStream eis) {
		return AccessController.
				doPrivileged(new PrivilegedAction<EncapsInputStream>() {

					@Override
					public EncapsInputStream run() {
						return new EncapsInputStream(eis);
					}
					
				});
	}
	
	public static EncapsInputStream newEncapsInputStream(final org.omg.CORBA.ORB orb,
			final byte[] buf,
            final int size,
            final ByteOrder byteOrder,
            final GIOPVersion version) {
		return AccessController.
				doPrivileged(new PrivilegedAction<EncapsInputStream>() {

					@Override
					public EncapsInputStream run() {
						return new EncapsInputStream(orb, buf, size, byteOrder, version);
					}
					
				});
		
	}
	
	public static EncapsInputStream newEncapsInputStream(final org.omg.CORBA.ORB orb,
			final byte[] data,
			final int size,
			final GIOPVersion version) {
		return AccessController.
				doPrivileged(new PrivilegedAction<EncapsInputStream>() {

					@Override
					public EncapsInputStream run() {
						return new EncapsInputStream(orb, data, size, version);
					}
					
				});
		
	}
	
	public static EncapsInputStream newEncapsInputStream(final org.omg.CORBA.ORB orb,
			final byte[] data,
			final int size) {
		return AccessController.
				doPrivileged(new PrivilegedAction<EncapsInputStream>() {

					@Override
					public EncapsInputStream run() {
						return new EncapsInputStream(orb, data, size);
					}
					
				});
		
	}
	
	public static EncapsInputStream newEncapsInputStream(final org.omg.CORBA.ORB orb,
			final ByteBuffer byteBuffer,
            final int size,
            final ByteOrder byteOrder,
            final GIOPVersion version) {
		return AccessController.
				doPrivileged(new PrivilegedAction<EncapsInputStream>() {

					@Override
					public EncapsInputStream run() {
						return new EncapsInputStream(orb, byteBuffer, size, byteOrder, version);
					}
					
				});
		
	}
	
	public static EncapsInputStream newEncapsInputStream(final org.omg.CORBA.ORB orb, 
            final byte[] data, 
            final int size, 
            final GIOPVersion version, 
            final CodeBase codeBase) {
		return AccessController.
				doPrivileged(new PrivilegedAction<EncapsInputStream>() {

					@Override
					public EncapsInputStream run() {
						return new EncapsInputStream(orb, data, size, version, codeBase);
					}
					
				});
		
	}
	
	public static TypeCodeInputStream newTypeCodeInputStream(final org.omg.CORBA.ORB orb,
            final byte[] data,
            final int size,
            final ByteOrder byteOrder,
            final GIOPVersion version) {
		return AccessController.
				doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {

					@Override
					public TypeCodeInputStream run() {
						return new TypeCodeInputStream(orb, data, size, byteOrder, version);
					}
					
				});
		
	}
	
	public static TypeCodeInputStream newTypeCodeInputStream(final org.omg.CORBA.ORB orb,
			final byte[] data,
			final int size) {
		return AccessController.
				doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {

					@Override
					public TypeCodeInputStream run() {
						return new TypeCodeInputStream(orb, data, size);
					}
					
				});
		
	}
	
	public static TypeCodeInputStream newTypeCodeInputStream(final org.omg.CORBA.ORB orb,
            final ByteBuffer byteBuffer,
            final int size,
            final ByteOrder byteOrder,
            final GIOPVersion version) {
		return AccessController.
				doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {

					@Override
					public TypeCodeInputStream run() {
						return new TypeCodeInputStream(orb, byteBuffer, size, byteOrder, version);
					}
					
				});
		
	}
}
