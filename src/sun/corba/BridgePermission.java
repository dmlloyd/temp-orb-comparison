

package sun.corba ;

import java.security.BasicPermission ;


public final class BridgePermission extends BasicPermission
{
    
    public BridgePermission(String name)
    {
        super(name);
    }

    

    public BridgePermission(String name, String actions)
    {
        super(name, actions);
    }
}
