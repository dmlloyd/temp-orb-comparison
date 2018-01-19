

package com.sun.corba.ee.impl.encoding;



class CodeSetCache
{
    private ThreadLocal<WeakHashMap<String,CharsetEncoder>> ctbMapLocal =
        new ThreadLocal<WeakHashMap<String,CharsetEncoder>>() {
            protected WeakHashMap<String,CharsetEncoder> initialValue() {
                return new WeakHashMap<String,CharsetEncoder>() ;
            }
        } ;

    private ThreadLocal<WeakHashMap<String,CharsetDecoder>> btcMapLocal =
        new ThreadLocal<WeakHashMap<String,CharsetDecoder>>() {
            protected WeakHashMap<String,CharsetDecoder> initialValue() {
                return new WeakHashMap<String,CharsetDecoder>() ;
            }
        } ;

    
    CharsetDecoder getByteToCharConverter(String key) {
        return btcMapLocal.get().get( key ) ;
    }

    
    CharsetEncoder getCharToByteConverter(String key) {
        return ctbMapLocal.get().get( key ) ;
    }

    
    CharsetDecoder setConverter(String key, CharsetDecoder converter) {
        btcMapLocal.get().put( key, converter ) ;
        return converter;
    }

    
    CharsetEncoder setConverter(String key, CharsetEncoder converter) {
        ctbMapLocal.get().put( key, converter ) ;
        return converter;
    }
}
