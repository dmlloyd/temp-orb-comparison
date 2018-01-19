

package com.sun.jndi.cosnaming;





public final class CorbanameUrl {
    private String stringName;
    private String location;

    
    public String getStringName() {
        return stringName;
    }

    public Name getCosName() throws NamingException {
        return CNCtx.parser.parse(stringName);
    }

    public String getLocation() {
        return "corbaloc:" + location;
    }

    public CorbanameUrl(String url) throws MalformedURLException {

        if (!url.startsWith("corbaname:")) {
            throw new MalformedURLException("Invalid corbaname URL: " + url);
        }

        int addrStart = 10;  

        int addrEnd = url.indexOf('#', addrStart);
        if (addrEnd < 0) {
            addrEnd = url.length();
            stringName = "";
        } else {
            stringName = CorbaUtils.decode(url.substring(addrEnd+1));
        }
        location = url.substring(addrStart, addrEnd);

        int keyStart = location.indexOf('/');
        if (keyStart >= 0) {
            
            if (keyStart == (location.length() -1)) {
                location += "NameService";
            }
        } else {
            location += "/NameService";
        }
    }


}
