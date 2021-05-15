package jo.sm.dle.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jo.sm.dl.logic.PlayLogic;
import jo.util.ui.swing.logic.IconLogic;

public class StopAction extends AbstractAction
{
    public StopAction()
    {
        super("Stop", IconLogic.makeIcon("\u23f9", 16));
        putValue(SHORT_DESCRIPTION, "Stop Playing");
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        PlayLogic.stop();
    }
}
