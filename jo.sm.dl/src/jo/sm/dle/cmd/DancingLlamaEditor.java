package jo.sm.dle.cmd;

import jo.sm.dle.data.DirectoryBean;
import jo.sm.dle.logic.RuntimeLogic;

public class DancingLlamaEditor
{
    public DancingLlamaEditor(String[] argv)
    {
        RuntimeLogic.init(argv);
    }
    
    public void run()
    {
        System.out.println(RuntimeLogic.getInstance().getDirectories().size()+" directories");
        for (DirectoryBean dir : RuntimeLogic.getInstance().getDirectories())
            System.out.println("\t"+dir.getName()+" "+dir.getSongs().size()+" songs");
    }
    
    public static void main(String[] argv)
    {
        DancingLlamaEditor app = new DancingLlamaEditor(argv);
        app.run();
    }
}
