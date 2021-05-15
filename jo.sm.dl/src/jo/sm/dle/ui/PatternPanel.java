package jo.sm.dle.ui;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jo.sm.dl.data.PatDef;
import jo.sm.dle.data.SongBean;
import jo.util.ui.swing.TableLayout;
import jo.util.utils.PCSBeanUtils;

public class PatternPanel extends JComponent
{
    private SongBean    mSong;
    private PatDef      mPattern;
    
    private JTextField     mUsed;
    private JTextField     mBeat;
    private JCheckBox      mMelody;
    private JList<String>  mNotes;
    
    public PatternPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mUsed = new JTextField();
        mUsed.setEditable(false);
        mBeat = new JTextField();
        mBeat.setEditable(false);
        mMelody = new JCheckBox("Melody");
        mMelody.setEnabled(false);
        mNotes = new JList<>();
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,+", new JLabel("Length:"));add("+,. fill=h", mUsed);
        add("1,+", new JLabel("Beat:"));add("+,. fill=h", mBeat);
        add("1,+", new JLabel(""));add("+,. fill=h", mMelody);
        add("1,+ 2x3 fill=hv", new JScrollPane(mNotes));
    }

    private void initLink()
    {
    }
    
    private void doNewPattern()
    {
        mPattern = mSong.getSelectedPattern();
        if (mPattern == null)
        {
            mUsed.setText("");
            mBeat.setText("");
            mMelody.setSelected(false);
        }
        else
        {
            mUsed.setText("Track #"+(mPattern.getNotes().size()));
            mBeat.setText(String.valueOf(mPattern.getBeat()));
            mMelody.setSelected(mPattern.isMelody());
        }
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
        PCSBeanUtils.listen(mSong, "selectedPattern", (ov,nv)->doNewPattern());
        doNewPattern();
    }
}
