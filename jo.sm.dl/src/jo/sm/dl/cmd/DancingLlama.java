package jo.sm.dl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import jo.sm.dl.data.SMProject;
import jo.sm.dl.logic.ProjectLogic;

public class DancingLlama
{
    private String[] mArgs;
    private Properties mSettings;
    
    public DancingLlama(String[] argv)
    {
        mArgs = argv;
    }
    
    public void run()
    {
        parseArgs();
        try
        {
            for (StringTokenizer st = new StringTokenizer(mSettings.getProperty("in"), ","); st.hasMoreTokens(); )
            {
                File inDir = new File(st.nextToken());
                Properties props = new Properties(mSettings);
                File inProps = new File(inDir, "dl.properties");
                if (inProps.exists())
                {
                    FileInputStream fis = new FileInputStream(inProps);
                    props.load(fis);
                    fis.close();
                }
                File outDir = new File(props.getProperty("out"));
                outDir.mkdirs();
                Properties oProps = new Properties(mSettings);
                File outProps = new File(outDir, "dl.properties");
                if (outProps.exists())
                {
                    FileInputStream fis = new FileInputStream(outProps);
                    oProps.load(fis);
                    fis.close();
                }
                File[] fin = inDir.listFiles();
                for (int i = 0; i < fin.length; i++)
                {
                    String name = fin[i].getName();
                    if (!name.endsWith(".mid"))
                        continue;
                    if (props.containsKey("filter") && (name.indexOf(props.getProperty("filter")) < 0))
                        continue;
                    System.out.println("\n"+name);
                    Properties sProps = new Properties(oProps);
                    sProps.setProperty("title", name.substring(0, name.length() - 4));
                    File s1Props = new File(inDir, name.substring(0, name.length() - 4)+".properties");
                    if (s1Props.exists())
                    {
                        FileInputStream fis = new FileInputStream(s1Props);
                        sProps.load(fis);
                        fis.close();
                    }
                    File fout = new File(outDir, name.substring(0, name.length() - 4));
                    fout.mkdirs();
                    File s2Props = new File(outDir, "dl.properties");
                    if (s2Props.exists())
                    {
                        FileInputStream fis = new FileInputStream(s2Props);
                        sProps.load(fis);
                        fis.close();
                    }
                    SMProject proj = ProjectLogic.newInstance(sProps);
                    ProjectLogic.load(proj, fin[i]);
                    ProjectLogic.dance(proj);
                    ProjectLogic.save(proj, fout);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void parseArgs()
    {
        mSettings = new Properties();
        for (int i = 0; i < mArgs.length; i++)
            if (mArgs[i].startsWith("--"))
            {
                String key = mArgs[i].substring(2).toLowerCase();
                String value = "true";
                if ((i + 1 < mArgs.length) && !mArgs[i+1].startsWith("--"))
                    value = mArgs[++i];
                if (mSettings.containsKey(key))
                    value = mSettings.getProperty(key)+","+value;
                mSettings.put(key, value);
            }
    }
    
    public static void main(String[] argv)
    {
        DancingLlama app = new DancingLlama(argv);
        app.run();
    }
}
