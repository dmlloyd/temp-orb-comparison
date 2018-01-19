


package com.sun.corba.ee.impl.presentation.rmi.codegen;

import org.glassfish.pfl.dynamic.codegen.spi.Primitives;
import org.glassfish.pfl.dynamic.codegen.spi.Variable;
import org.glassfish.pfl.dynamic.codegen.spi.Expression;
import org.glassfish.pfl.basic.contain.Pair;
import org.glassfish.pfl.dynamic.codegen.spi.Utility;
import org.glassfish.pfl.dynamic.codegen.spi.MethodInfo;
import org.glassfish.pfl.dynamic.codegen.spi.Type;
import java.io.PrintStream ;

import java.lang.reflect.Method ;

import java.security.ProtectionDomain ;

import java.util.Properties ;
import java.util.List ;
import java.util.ArrayList ;

import static java.lang.reflect.Modifier.* ;

import static org.glassfish.pfl.dynamic.codegen.spi.Wrapper.* ;


public class CodegenProxyCreator {
    private String className ;
    private Type superClass ;
    private List<Type> interfaces ;
    private List<MethodInfo> methods ;

    private static final Properties debugProps = new Properties() ;
    private static final Properties emptyProps = new Properties() ;

    static {
        debugProps.setProperty( DUMP_AFTER_SETUP_VISITOR, "true" ) ;
        debugProps.setProperty( TRACE_BYTE_CODE_GENERATION, "true" ) ;
        debugProps.setProperty( USE_ASM_VERIFIER, "true" ) ;
    }

    public CodegenProxyCreator( String className, Class sc,
        Class[] interfaces, Method[] methods ) {

        this.className = className ;
        this.superClass = Type.type( sc ) ;

        this.interfaces = new ArrayList<Type>() ;
        for (Class cls : interfaces) {
            this.interfaces.add( Type.type( cls ) ) ;
        }

        this.methods = new ArrayList<MethodInfo>() ;
        for (Method method : methods) {
            this.methods.add( Utility.getMethodInfo( method ) ) ;
        }
    }

    
    public Class<?> create( ProtectionDomain pd, ClassLoader cl,
        boolean debug, PrintStream ps ) {

        Pair<String,String> nm = splitClassName( className ) ;

        _clear() ;
        _setClassLoader( cl ) ;
        _package( nm.first() ) ;
        _class( PUBLIC, nm.second(), superClass, interfaces ) ;

        _constructor( PUBLIC ) ;
        _body() ;
            _expr(_super());
        _end() ;

        _method( PRIVATE, _Object(), "writeReplace" ) ;
        _body() ;
            _return(_call(_this(), "selfAsBaseClass" )) ;
        _end() ;

        int ctr=0 ;
        for (MethodInfo method : methods) 
            createMethod( ctr++, method ) ;
    
        _end() ; 

        return _generate( cl, pd, debug ? debugProps : emptyProps, ps ) ;
    }

    private static final Type objectArrayType = Type._array(_Object()) ;

    private static void createMethod( int mnum, MethodInfo method ) {
        Type rtype = method.returnType() ;
        _method( method.modifiers() & ~ABSTRACT, rtype, method.name()) ;
        
        List<Expression> args = new ArrayList<Expression>() ;
        for (Variable var : method.arguments() ) 
            args.add( _arg( var.type(), var.ident() ) ) ;

        _body() ;
            List<Expression> wrappedArgs = new ArrayList<Expression>() ;
            for (Expression arg : args) {
                wrappedArgs.add( Primitives.wrap( arg ) ) ;
            }
            
            Expression invokeArgs = _define( objectArrayType, "args",
                _new_array_init( _Object(), wrappedArgs ) ) ;

            
            Expression invokeExpression = _call(
                _this(), "invoke", _const(mnum), invokeArgs ) ;

            
            if (rtype == _void()) {
                _expr( invokeExpression ) ;
                _return() ;
            } else {
                Expression resultExpr = _define( _Object(), "result", invokeExpression ) ;

                if (rtype != _Object()) {
                    if (rtype.isPrimitive()) {
                        
                        Type ctype = Primitives.getWrapperTypeForPrimitive( rtype ) ;
                        Expression cexpr = _cast( ctype, resultExpr ) ;
                        _return( Primitives.unwrap( cexpr ) ) ;
                    } else {
                        _return(_cast(rtype, resultExpr )) ;
                    }
                } else {
                    _return( resultExpr ) ;
                }
            }
        _end() ; 
    }
}
