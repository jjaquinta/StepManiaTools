package jo.sm.dle.data;

import java.util.Properties;

public class DirectoryBean
{
    private Properties  mSettings;
    private String      mName;
    public Properties getSettings()
    {
        return mSettings;
    }
    public void setSettings(Properties settings)
    {
        mSettings = settings;
    }
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
}
