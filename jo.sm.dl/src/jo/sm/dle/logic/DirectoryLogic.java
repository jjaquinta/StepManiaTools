package jo.sm.dle.logic;

import java.io.File;

import jo.sm.dl.data.JProperties;
import jo.sm.dle.data.DirectoryBean;
import jo.sm.dle.data.RuntimeBean;

public class DirectoryLogic
{
    public static void addDirectory(String inName)
    {
        DirectoryBean dir = new DirectoryBean();
        dir.setName(inName);
        RuntimeBean rt = RuntimeLogic.getInstance();
        File inDir = new File(inName);
        if (!inDir.exists())
        {
            inDir = new File(rt.getBaseDir(), inName);
        }
        dir.setInDir(inDir);
        JProperties indirProps = RuntimeLogic.newProperties(rt.getSettings());
        File inProps = new File(inDir, "dl.properties");
        if (!RuntimeLogic.readProperties(indirProps, inProps))
            return;
        dir.setInSettings(indirProps);
        File outDir = null;
        if ((indirProps.getProperty("SongDir") != null) && (indirProps.getProperty("PackageDir") != null))
        {
            File songDir = new File(indirProps.getProperty("SongDir"));
            outDir = new File(songDir, indirProps.getProperty("PackageDir"));
        }
        else
            outDir = new File(indirProps.getProperty("out"));
        dir.setOutDir(outDir);
        JProperties outdirProps = RuntimeLogic.newProperties(indirProps);
        File outProps = new File(outDir, "dl.properties");
        RuntimeLogic.readProperties(outdirProps, outProps);
        dir.setOutSettings(outdirProps);
        File[] fin = inDir.listFiles();
        for (int i = 0; i < fin.length; i++)
        {
            SongLogic.addSong(dir, fin[i]);
        }
        rt.getDirectories().add(dir);
    }
}
