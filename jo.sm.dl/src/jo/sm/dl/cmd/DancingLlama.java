package jo.sm.dl.cmd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jo.sm.dl.data.SMProject;
import jo.sm.dl.logic.DifficultyLogic;
import jo.sm.dl.logic.ProjectLogic;

public class DancingLlama
{
    private String[] mArgs;
    private String   mFilter;
    private File     mInDir;
    private File     mOutDir;
    
    public DancingLlama(String[] argv)
    {
        mArgs = argv;
    }
    
    public void run()
    {
        parseArgs();
        try
        {
            mOutDir.mkdirs();
            File[] fin = mInDir.listFiles();
            for (int i = 0; i < fin.length; i++)
            {
                String name = fin[i].getName();
                if (!name.endsWith(".mid"))
                    continue;
                if ((mFilter != null) && (name.indexOf(mFilter) < 0))
                    continue;
                System.out.println("\n"+name);
                File fout = new File(mOutDir, name.substring(0, name.length() - 4));
                SMProject proj = ProjectLogic.newInstance(mArgs);
                ProjectLogic.load(proj, fin[i]);
                ProjectLogic.dance(proj);
                proj.getTune().setTitle(fout.getName());
                ProjectLogic.save(proj, fout);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private void parseArgs()
    {
        List<String> args = new ArrayList<>();
        for (int i = 0; i < mArgs.length; i++)
            if ("--in".equals(mArgs[i]))
                mInDir = new File(mArgs[++i]);
            else if ("--out".equals(mArgs[i]))
                mOutDir = new File(mArgs[++i]);
            else if ("--filter".equals(mArgs[i]))
                mFilter = mArgs[++i];
            else
                args.add(mArgs[i]);
        mArgs = args.toArray(new String[0]);
        DifficultyLogic.init(mArgs);
    }
    
    public static void main(String[] argv)
    {
        DancingLlama app = new DancingLlama(argv);
        app.run();
    }
}
