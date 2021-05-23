package jo.sm.dle.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.logic.IconLogic;

public class PlayTrackAction extends AbstractAction
{
    public PlayTrackAction()
    {
        super("Play", IconLogic.makeIcon("\u23E9", 16));
        putValue(SHORT_DESCRIPTION, "Play Track");
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        SongLogic.doPlayTrack();
    }
}
