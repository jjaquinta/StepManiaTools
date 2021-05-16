package jo.sm.dl.data;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import jo.util.utils.obj.IntegerUtils;

public class JProperties extends Properties
{
    private JProperties mParent;
    
    public JProperties(JProperties parent)
    {
        super();
        mParent = parent;
    }
    
    public JProperties()
    {
        super();
    }
    
    @Override
    public synchronized Object get(Object key)
    {
        Object ret = super.get(key);
        if ((ret == null) && (mParent != null))
            ret = mParent.get(key);
        return ret;
    }
    
    @Override
    public String getProperty(String key, String defaultValue)
    {
        String ret = super.getProperty(key);
        if (ret == null) 
            if (mParent != null)
                ret = mParent.getProperty(key, defaultValue);
            else
                ret = defaultValue;
        return ret;
    }
    
    @Override
    public String getProperty(String key)
    {
        String ret = super.getProperty(key);
        if ((ret == null) && (mParent != null))
            ret = mParent.getProperty(key);
        return ret;
    }
    
    public Set<Integer> getAsIntSet(String key)
    {
        Set<Integer> ret = new HashSet<>();
        String prop = getProperty(key);
        if (prop != null)
            for (StringTokenizer st = new StringTokenizer(prop, ","); st.hasMoreTokens(); )
                ret.add(IntegerUtils.parseInt(st.nextElement()));
        return ret;
    }
    
    public int getAsInt(String key, String def)
    {
        return IntegerUtils.parseInt(getProperty(key, def));
    }
}
