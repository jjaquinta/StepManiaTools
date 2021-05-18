package jo.sm.dle.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.logic.MIDILogic;
import jo.util.ui.swing.TableLayout;

public class NotePanel extends JComponent
{
    private MIDINote   mNote;

    private JTextField mPitch;
    private JTextField mInstrument;
    private JTextField mLoud;
    private JTextField mTick;
    private JTextField mDuration;
    private JTextField mTrack;

    public NotePanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mPitch = new JTextField();
        mPitch.setEditable(false);
        mInstrument = new JTextField();
        mInstrument.setEditable(false);
        mLoud = new JTextField();
        mLoud.setEditable(false);
        mTick = new JTextField();
        mTick.setEditable(false);
        mDuration = new JTextField();
        mDuration.setEditable(false);
        mTrack = new JTextField();
        mTrack.setEditable(false);
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,+", new JLabel("Pitch:"));
        add("+,. fill=h", mPitch);
        add("1,+", new JLabel("Instrument:"));
        add("+,. fill=h", mInstrument);
        add("1,+", new JLabel("Loud:"));
        add("+,. fill=h", mLoud);
        add("1,+", new JLabel("Tick:"));
        add("+,. fill=h", mTick);
        add("1,+", new JLabel("Duration"));
        add("+,. fill=h", mDuration);
        add("1,+", new JLabel("Track:"));
        add("+,. fill=h", mTrack);
    }

    private void initLink()
    {
    }

    private void doNewNote()
    {
        if (mNote == null)
        {
            mPitch.setText("");
            mInstrument.setText("");
            mLoud.setText("");
            mTick.setText("");
            mDuration.setText("");
            mTrack.setText("");
        }
        else
        {
            mPitch.setText(MIDINote.NOTES[mNote.getPitch()] + " ("
                    + mNote.getPitch() + ")");
            mInstrument.setText(MIDILogic.getInstrumentName(mNote.getBank(),
                    mNote.getProgram()));
            mLoud.setText(String.valueOf(mNote.getLoud()));
            mTick.setText(String.valueOf(mNote.getTick()));
            mDuration.setText(String.valueOf(mNote.getDuration()));
            mTrack.setText(String.valueOf(mNote.getTrack() + 1));
        }
    }

    public MIDINote getNote()
    {
        return mNote;
    }

    public void setNote(MIDINote note)
    {
        mNote = note;
        doNewNote();
    }
}
