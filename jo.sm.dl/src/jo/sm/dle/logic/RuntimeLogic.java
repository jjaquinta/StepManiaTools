package jo.sm.dle.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import jo.sm.dle.data.RuntimeBean;

public class RuntimeLogic
{
    private static RuntimeBean mRuntime = new RuntimeBean();
    
    public static RuntimeBean getInstance()
    {
        return mRuntime;
    }
    
    public static void init(String[] args)
    {
        mRuntime.setArgs(args);        
        mRuntime.setSettings(new Properties());
        parseArgs();
        parseFiles();
    }
    
    private static void parseFiles()
    {
        try
        {
            for (StringTokenizer st = new StringTokenizer(mRuntime.getSettings().getProperty("in"), ","); st.hasMoreTokens(); )
            {
                String inName = st.nextToken();
                DirectoryLogic.addDirectory(inName);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    
    private static void parseArgs()
    {
        String[] args = mRuntime.getArgs();
        for (int i = 0; i < args.length; i++)
            if (args[i].startsWith("--props"))
            {
                File propsFile = new File(args[++i]);
                mRuntime.setBaseDir(propsFile.getParentFile());
                readProperties(mRuntime.getSettings(), propsFile);
            }
            else if (args[i].startsWith("--"))
            {
                String key = args[i].substring(2).toLowerCase();
                String value = "true";
                if ((i + 1 < args.length) && !args[i+1].startsWith("--"))
                    value = args[++i];
                if (mRuntime.getSettings().containsKey(key))
                    value = mRuntime.getSettings().getProperty(key)+","+value;
                mRuntime.getSettings().put(key, value);
            }
    }   
    
    public static Properties newProperties(Properties oldProperties)
    {
        Properties newProps = new Properties();
        for (Object key : oldProperties.keySet())
            newProps.put(key, oldProperties.get(key));
        return newProps;
    }

    public static boolean readProperties(Properties props, File file)
    {
        if (file.exists())
        {
            try
            {
                FileInputStream fis = new FileInputStream(file);
                props.load(fis);
                fis.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
