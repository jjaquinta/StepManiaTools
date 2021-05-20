package jo.sm.dle.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jo.sm.dle.logic.SongLogic;

public class AutoTrackAction extends AbstractAction
{
    public AutoTrackAction()
    {
        super("Auto Track");
        putValue(SHORT_DESCRIPTION, "Work out track types");
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        SongLogic.autoTrack();
    }
}
