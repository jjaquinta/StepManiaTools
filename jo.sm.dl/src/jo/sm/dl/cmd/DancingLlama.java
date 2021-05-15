package jo.sm.dl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.StringTokenizer;

import jo.sm.dl.data.JProperties;
import jo.sm.dl.data.SMProject;
import jo.sm.dl.logic.ProjectLogic;
import jo.util.utils.obj.BooleanUtils;
import jo.util.utils.obj.DoubleUtils;

public class DancingLlama
{
    private String[] mArgs;
    private File     mBaseDir;
    private JProperties mSettings;
    
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
                String inName = st.nextToken();
                File inDir = new File(inName);
                if (!inDir.exists())
                {
                    inDir = new File(mBaseDir, inName);
                }
                JProperties indirProps = newProperties(mSettings);
                File inProps = new File(inDir, "dl.properties");
                if (inProps.exists())
                {
                    FileInputStream fis = new FileInputStream(inProps);
                    indirProps.load(fis);
                    fis.close();
                }
                File outDir = null;
                if ((indirProps.getProperty("SongDir") != null) && (indirProps.getProperty("PackageDir") != null))
                {
                    File songDir = new File(indirProps.getProperty("SongDir"));
                    outDir = new File(songDir, indirProps.getProperty("PackageDir"));
                }
                else
                    outDir = new File(indirProps.getProperty("out"));
                outDir.mkdirs();
                JProperties outdirProps = newProperties(indirProps);
                File outProps = new File(outDir, "dl.properties");
                if (outProps.exists())
                {
                    FileInputStream fis = new FileInputStream(outProps);
                    outdirProps.load(fis);
                    fis.close();
                }
                File[] fin = inDir.listFiles();
                for (int i = 0; i < fin.length; i++)
                {
                    String name = fin[i].getName();
                    if (!name.endsWith(".mid"))
                        continue;
                    if (indirProps.containsKey("filter") && (name.indexOf(indirProps.getProperty("filter")) < 0))
                        continue;
                    System.out.println("\n"+name);
                    JProperties songProps = newProperties(outdirProps);
                    songProps.setProperty("title", name.substring(0, name.length() - 4));
                    File s1Props = new File(inDir, name.substring(0, name.length() - 4)+".properties");
                    if (s1Props.exists())
                    {
                        FileInputStream fis = new FileInputStream(s1Props);
                        songProps.load(fis);
                        fis.close();
                    }
                    if (BooleanUtils.parseBoolean(songProps.get("skip")))
                        continue;
                    SMProject proj = ProjectLogic.newInstance(songProps);
                    ProjectLogic.load(proj, fin[i]);
                    double skipLongerThan = DoubleUtils.parseDouble(songProps.get("skipLongerThan"));
                    if (skipLongerThan > 0)
                        if (proj.getMIDI().getLengthInSeconds() > skipLongerThan)
                            continue;
                    File fout = new File(outDir, name.substring(0, name.length() - 4));
                    fout.mkdirs();
                    File s2Props = new File(outDir, "dl.properties");
                    if (s2Props.exists())
                    {
                        FileInputStream fis = new FileInputStream(s2Props);
                        songProps.load(fis);
                        fis.close();
                    }
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
        mSettings = new JProperties();
        for (int i = 0; i < mArgs.length; i++)
            if (mArgs[i].startsWith("--props"))
            {
                File propsFile = new File(mArgs[++i]);
                mBaseDir = propsFile.getParentFile();
                try
                {
                    FileInputStream fis = new FileInputStream(propsFile);
                    mSettings.load(fis);
                    fis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else if (mArgs[i].startsWith("--"))
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

    private JProperties newProperties(JProperties oldProperties)
    {
        JProperties newProps = new JProperties(oldProperties);
        return newProps;
    }
    
    public static void main(String[] argv)
    {
        DancingLlama app = new DancingLlama(argv);
        app.run();
    }
}
