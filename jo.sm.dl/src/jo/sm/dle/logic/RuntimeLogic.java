package jo.sm.dle.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.function.BiConsumer;

import jo.sm.dl.data.JProperties;
import jo.sm.dle.data.DirectoryBean;
import jo.sm.dle.data.RuntimeBean;
import jo.sm.dle.data.SongBean;
import jo.util.utils.PCSBeanUtils;

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
        mRuntime.setSettings(new JProperties());
        parseArgs();
        parseFiles();
    }
    
    public static void term()
    {
        writeProperties(mRuntime.getSettings(), mRuntime.getPropsFile());
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
                mRuntime.setPropsFile(propsFile);
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
    
    public static JProperties newProperties(JProperties oldProperties)
    {
        JProperties newProps = new JProperties(oldProperties);
        return newProps;
    }

    public static boolean readProperties(JProperties props, File file)
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

    public static boolean writeProperties(JProperties props, File file)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            props.store(fos, "Overall JProperties");
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    public static void listen(String prop, BiConsumer<Object, Object> action)
    {
        PCSBeanUtils.listen(RuntimeLogic.getInstance(), prop, action);
    }

    public static void select(DirectoryBean dir)
    {
        mRuntime.setSelectedDirectory(dir);
    }

    public static void select(SongBean song)
    {
        SongLogic.load(song);
        mRuntime.setSelectedSong(song);
    }
    
    public static void zoomOut()
    {
        int size = mRuntime.getZoomSize();
        size -= size/4;
        mRuntime.setZoomSize(size);
    }
    
    public static void zoomIn()
    {
        int size = mRuntime.getZoomSize();
        size += size/2;
        mRuntime.setZoomSize(size);
    }
}
