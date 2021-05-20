package jo.sm.dle.ui;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import jo.sm.dl.data.sm.pat.PatDef;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.PCSBeanUtils;

public class PatternsPanel extends JComponent
{
    private SongBean    mSong;
    
    private JComboBox<PatDef>    mPatternPicker;
    private PatternPanel  mPatternInfo;
    
    public PatternsPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mPatternPicker = new JComboBox<>();
        mPatternInfo = new PatternPanel();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("North", mPatternPicker);
        add("Center", mPatternInfo);
    }

    private void initLink()
    {
        ListenerUtils.listen(mPatternPicker, (ev) -> doSelectPattern());
    }
    
    private void doSelectPattern()
    {
        PatDef pattern = (PatDef)mPatternPicker.getSelectedItem();
        SongLogic.selectPattern(pattern);
    }
    
    private void updateControls()
    {
        if (mSong == null)
            return;
        mPatternPicker.setModel(new DefaultComboBoxModel<>(mSong.getProject().getPatterns().toArray(new PatDef[0])));
        mPatternPicker.setSelectedItem(mSong.getSelectedPattern());
    }

    public SongBean getSong()
    {
        return mSong;
    }

    public void setSong(SongBean song)
    {
        if (mSong != null)
            PCSBeanUtils.unlisten(mSong, "selectedPattern");
        mSong = song;
        updateControls();
        PCSBeanUtils.listen(mSong, "selectedPattern", (ov,nv)->repaint());
        mPatternInfo.setSong(song);
    }
}
