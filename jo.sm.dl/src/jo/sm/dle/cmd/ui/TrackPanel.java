package jo.sm.dle.cmd.ui;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.MIDITrack;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.PCSBeanUtils;

public class TrackPanel extends JComponent
{
    private SongBean    mSong;
    private MIDITrack   mTrack;
    
    private JTextField     mNumber;
    private JTextField     mBank;
    private JTextField     mProgram;
    private JCheckBox      mMelody;
    private JTextField     mLowPitch;
    private JTextField     mHighPitch;
    private JTextField     mNotes;
    private JLabel         mSwatch;
    private JCheckBox      mDisplay;
    private JButton        mDisplayOnly;
    
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
        mSwatch = new JLabel();
        mDisplay = new JCheckBox("Display");
        mMelody = new JCheckBox("Melody");
        mDisplayOnly = new JButton("Show Only This");
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,+", new JLabel("Number:"));add("+,. fill=h", mNumber);
        add("1,+", new JLabel("Bank:"));add("+,. fill=h", mBank);
        add("1,+", new JLabel("Program:"));add("+,. fill=h", mProgram);
        add("1,+", new JLabel(""));add("+,. fill=h", mMelody);
        add("1,+", new JLabel("LowPitch:"));add("+,. fill=h", mLowPitch);
        add("1,+", new JLabel("HighPitch:"));add("+,. fill=h", mHighPitch);
        add("1,+", new JLabel("Notes:"));add("+,. fill=h", mNotes);
        add("1,+", new JLabel("Swatch:"));add("+,. fill=h", mSwatch);
        add("1,+", new JLabel(""));add("+,. fill=h", mDisplay);
        add("1,+ 2x1 fill=h", mDisplayOnly);
    }

    private void initLink()
    {
        ListenerUtils.listen(mDisplayOnly, (ev) -> doDisplayOnly());
        ListenerUtils.listen(mDisplay, (ev) -> doDisplay());
        ListenerUtils.listen(mMelody, (ev) -> doMelody());
    }
    
    private void doMelody()
    {
        if (mMelody.isSelected())
            SongLogic.setMelody(mTrack.getTrack());
        else
            SongLogic.setMelody(-1);
    }
    
    private void doDisplayOnly()
    {
    }
    
    private void doDisplay()
    {
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
            mSwatch.setIcon(null);
            mDisplay.setSelected(false);
            mMelody.setSelected(false);
        }
        else
        {
            mNumber.setText("Track #"+(mTrack.getTrack()+1));
            mBank.setText(String.valueOf(mTrack.getBank()));
            mProgram.setText(MIDINote.getInstrument(mTrack.getProgram()));
            mLowPitch.setText(MIDINote.NOTES[mTrack.getLowPitch()]);
            mHighPitch.setText(MIDINote.NOTES[mTrack.getHighPitch()]);
            mNotes.setText("#"+mTrack.getNotes().size());
            mSwatch.setIcon(ScorePanel.getTrackSwatch(mTrack.getTrack()));
            mDisplay.setSelected(mSong.getTracks().contains(mTrack.getTrack()));
            mMelody.setSelected(mSong.getMelodyTrack() == mTrack.getTrack());
        }
    }
    
    private void doNewTrackDisplay()
    {
        mTrack = mSong.getSelectedTrack();
        if (mTrack == null)
        {
            mDisplay.setSelected(false);
        }
        else
        {
            mDisplay.setSelected(mSong.getTracks().contains(mTrack.getTrack()));
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
        PCSBeanUtils.listen(mSong, "selectedTrack", (ov,nv)->doNewTrack());
        PCSBeanUtils.listen(mSong, "tracks", (ov,nv)->doNewTrackDisplay());
        doNewTrack();
    }
}
