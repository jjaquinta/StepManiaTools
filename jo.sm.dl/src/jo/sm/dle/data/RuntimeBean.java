package jo.sm.dle.data;

import java.io.File;
import java.util.Properties;

public class RuntimeBean
{
    private String[] mArgs;
    private File     mBaseDir;
    private Properties mSettings;
    
    public String[] getArgs()
    {
        return mArgs;
    }
    public void setArgs(String[] args)
    {
        mArgs = args;
    }
    public File getBaseDir()
    {
        return mBaseDir;
    }
    public void setBaseDir(File baseDir)
    {
        mBaseDir = baseDir;
    }
    public Properties getSettings()
    {
        return mSettings;
    }
    public void setSettings(Properties settings)
    {
        mSettings = settings;
    }

}
