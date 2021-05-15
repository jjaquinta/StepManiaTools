package jo.sm.dle.ui;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JToolBar;

import jo.sm.dle.actions.PlayAction;
import jo.sm.dle.actions.StopAction;

public class DLEToolbar extends JComponent
{
    private JToolBar    mClient;
    
    public DLEToolbar()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mClient = new JToolBar();
    }

    private void initLayout()
    {
        mClient.add(new PlayAction());
        mClient.add(new StopAction());
        
        setLayout(new BorderLayout());
        add("Center", mClient);
    }

    private void initLink()
    {
    }

}
