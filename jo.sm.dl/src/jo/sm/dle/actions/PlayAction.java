package jo.sm.dle.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.logic.IconLogic;

public class PlayAction extends AbstractAction
{
    public PlayAction()
    {
        super("Play", IconLogic.makeIcon("\u25b6", 16));
        putValue(SHORT_DESCRIPTION, "Play All");
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        SongLogic.doPlayAll();
    }
}
