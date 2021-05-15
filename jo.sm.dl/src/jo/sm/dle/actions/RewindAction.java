package jo.sm.dle.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class RewindAction extends AbstractAction
{
    public RewindAction()
    {
        super("|<");
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        //SongLogic.setCaretStart();
    }

}
