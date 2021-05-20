package jo.sm.dle.ui;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dl.logic.MIDILogic;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.PCSBeanUtils;

public class TrackPanel extends JComponent
{
    private SongBean          mSong;
    private MIDITrack         mTrack;

    private JTextField        mNumber;
    private JTextField        mBank;
    private JTextField        mProgram;
    private JComboBox<String> mType;
    private JTextField        mLowPitch;
    private JTextField        mHighPitch;
    private JTextField        mNotes;

    public TrackPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mNumber = new JTextField();
        mNumber.setEditable(false);
        mBank = new JTextField();
        mBank.setEditable(false);
        mProgram = new JTextField();
        mProgram.setEditable(false);
        mLowPitch = new JTextField();
        mLowPitch.setEditable(false);
        mHighPitch = new JTextField();
        mHighPitch.setEditable(false);
        mNotes = new JTextField();
        mNotes.setEditable(false);
        mType = new JComboBox<>(new String[] { "Ignore", "Unknown", "Melody", "Rhythm", "Bass", "Harmony", "Incidental" });
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,+", new JLabel("Number:"));
        add("+,. fill=h", mNumber);
        add("1,+", new JLabel("Bank:"));
        add("+,. fill=h", mBank);
        add("1,+", new JLabel("Program:"));
        add("+,. fill=h", mProgram);
        add("1,+", new JLabel("Type"));
        add("+,. fill=h", mType);
        add("1,+", new JLabel("LowPitch:"));
        add("+,. fill=h", mLowPitch);
        add("1,+", new JLabel("HighPitch:"));
        add("+,. fill=h", mHighPitch);
        add("1,+", new JLabel("Notes:"));
        add("+,. fill=h", mNotes);
    }

    private void initLink()
    {
        ListenerUtils.listen(mType, (ev) -> doType());
    }

    private void doType()
    {
        if (mTrack == null)
            return;
        int idx = mType.getSelectedIndex();
        if (idx >= 0)
            idx--;
        SongLogic.setTrackType(mTrack.getTrack(), idx);
    }

    private void doNewTrack()
    {
        mTrack = mSong.getSelectedTrack();
        if (mTrack == null)
        {
            mNumber.setText("");
            mBank.setText("");
            mProgram.setText("");
            mLowPitch.setText("");
            mHighPitch.setText("");
            mNotes.setText("");
            mType.setSelectedIndex(-1);
        }
        else
        {
            mNumber.setText("Track #" + (mTrack.getTrack() + 1));
            mBank.setText(String.valueOf(mTrack.getBank()));
            mProgram.setText(MIDILogic.getInstrumentName(mTrack.getBank(),
                    mTrack.getProgram()));
            mLowPitch.setText(MIDINote.NOTES[mTrack.getLowPitch()]);
            mHighPitch.setText(MIDINote.NOTES[mTrack.getHighPitch()]);
            mNotes.setText("#" + mTrack.getNotes().size());
            mType.setSelectedIndex(mTrack.getType() + 1);
        }
    }

    public SongBean getSong()
    {
        return mSong;
    }

    public void setSong(SongBean song)
    {
        if (mSong != null)
            PCSBeanUtils.unlisten(mSong, "selectedTrack,tracks");
        mSong = song;
        PCSBeanUtils.listen(mSong, "selectedTrack,tracks", (ov, nv) -> doNewTrack());
        doNewTrack();
    }
}
