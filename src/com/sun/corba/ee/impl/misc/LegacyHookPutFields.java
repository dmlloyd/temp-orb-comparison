

package xxxx;



class LegacyHookPutFields extends ObjectOutputStream.PutField
{
    private Map<String, Object> fields = new HashMap<String, Object>();

    
    public void put(String name, boolean value){
        fields.put(name, Boolean.valueOf(value));
    }
                
    
    public void put(String name, char value){
        fields.put(name, Character.valueOf(value));
    }
                
    
    public void put(String name, byte value){
        fields.put(name, Byte.valueOf(value));
    }
                
    
    public void put(String name, short value){
        fields.put(name, Short.valueOf(value));
    }
                
    
    public void put(String name, int value){
        fields.put(name, Integer.valueOf(value));
    }
                
    
    public void put(String name, long value){
        fields.put(name, Long.valueOf(value));
    }
                
    
    public void put(String name, float value){
        fields.put(name, Float.valueOf(value));
    }
                
    
    public void put(String name, double value){
        fields.put(name, Double.valueOf(value));
    }
                
    
    public void put(String name, Object value){
        fields.put(name, value);
    }
                
    
    public void write(ObjectOutput out) throws IOException {
        out.writeObject(fields);
    }
}    
