

package com.sun.corba.ee.spi.orb ;

import java.lang.reflect.Constructor ;

import com.sun.corba.ee.spi.logging.ORBUtilSystemException ;


public class OperationFactoryExt {
    private static final ORBUtilSystemException wrapper =
        ORBUtilSystemException.self ;

    private OperationFactoryExt() {} 

    private static class ConvertAction implements Operation {
        private Class<?> cls ;
        private Constructor<?> cons ;

        public ConvertAction( Class<?> cls ) {
            this.cls = cls ;
            try {
                cons = cls.getConstructor( String.class ) ;
            } catch (Exception exc) {
                throw wrapper.exceptionInConvertActionConstructor( exc,
                    cls.getName() ) ;
            }
        }

        public Object operate( Object value )
        {
            try {
                return cons.newInstance( value ) ;
            } catch (Exception exc) {
                throw wrapper.exceptionInConvertAction( exc ) ;
            }
        }

        @Override
        public String toString() {
            return "ConvertAction[" + cls.getName() + "]" ;
        }

        @Override
        public boolean equals( Object obj ) 
        {
            if (this==obj) {
                return true;
            }

            if (!(obj instanceof ConvertAction)) {
                return false;
            }

            ConvertAction other = (ConvertAction)obj ;

            return toString().equals( other.toString() ) ;
        }

        @Override
        public int hashCode()
        {
            return toString().hashCode() ;
        }
    }

    public static Operation convertAction( Class<?> cls ) {
        return new ConvertAction( cls ) ;
    }
}
