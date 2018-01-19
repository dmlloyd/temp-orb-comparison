



package xxxx;




public class ExceptionListImpl extends ExceptionList {

    private static final int     INITIAL_CAPACITY       = 2;

    private List<TypeCode> _exceptions;

    public ExceptionListImpl() {
        _exceptions = new ArrayList<TypeCode>(INITIAL_CAPACITY);
    }

    public synchronized int count() 
    {
        return _exceptions.size();
    }

    public synchronized void add(TypeCode tc)
    {
        _exceptions.add(tc);
    }

    public synchronized TypeCode item(int index)
        throws Bounds
    {
        try {
            return _exceptions.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Bounds();
        }
    }

    public synchronized void remove(int index)
        throws Bounds
    {
        try {
            _exceptions.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw new Bounds();
        }
    }

}

