package jo.sm.dle.cmd;

import jo.sm.dle.cmd.ui.DLEFrame;
import jo.sm.dle.logic.RuntimeLogic;

public class DancingLlamaEditor
{
    public DancingLlamaEditor(String[] argv)
    {
        RuntimeLogic.init(argv);
    }
    
    public void run()
    {
        DLEFrame frame = new DLEFrame();
        frame.setVisible(true);
    }
    
    public static void main(String[] argv)
    {
        DancingLlamaEditor app = new DancingLlamaEditor(argv);
        app.run();
    }
}
