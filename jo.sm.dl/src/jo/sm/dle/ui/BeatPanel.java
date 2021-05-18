package jo.sm.dle.ui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import jo.sm.dl.data.MIDINote;
import jo.sm.dl.data.SMBeat;
import jo.sm.dl.logic.MIDILogic;
import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.TableLayout;
import jo.util.ui.swing.utils.ListenerUtils;

public class BeatPanel extends JComponent
{
    private SMBeat     mBeat;

    private JTextField mPitch;
    private JTextField mInstrument;
    private JTextField mLoud;
    private JTextField mTick;
    private JTextField mNote;
    private JTextField mAlignment;
    private JButton    mNext;
    private JButton    mPrev;

    public BeatPanel()
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
        mNote = new JTextField();
        mNote.setEditable(false);
        mAlignment = new JTextField();
        mAlignment.setEditable(false);
        mNext = new JButton("\u23E9");
        mNext.setToolTipText("Next Beat>>");
        mPrev = new JButton("\u23EA");
        mPrev.setToolTipText("<<Previous Beat");
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,+", new JLabel("Arrows"));
        add("+,. fill=h", mNote);
        add("1,+", new JLabel("Pitch:"));
        add("+,. fill=h", mPitch);
        add("1,+", new JLabel("Instrument:"));
        add("+,. fill=h", mInstrument);
        add("1,+", new JLabel("Loud:"));
        add("+,. fill=h", mLoud);
        add("1,+", new JLabel("Tick:"));
        add("+,. fill=h", mTick);
        add("1,+", new JLabel("Alignment:"));
        add("+,. fill=h", mAlignment);
        add("1,+", mPrev);
        add("+,.", mNext);
    }

    private void initLink()
    {
        ListenerUtils.listen(mNext, (ev) -> SongLogic.nextBeat());
        ListenerUtils.listen(mPrev, (ev) -> SongLogic.previousBeat());
    }
    
    private static final String NORMAL = "\u2190\u2192\u2191\u2193";
    private static final String HOLD = "\u21D0\u21D2\u21D1\u21D3";
    
    private String getArrows()
    {
        StringBuffer arrows = new StringBuffer();
        for (int i = 0; i < mBeat.getNotes().length; i++)
            switch (mBeat.getNotes()[i])
            {
                case SMBeat.NOTE_NORMAL:
                    arrows.append(NORMAL.substring(i, i+1));
                    break;
                case SMBeat.NOTE_HOLD_HEAD:
                    arrows.append(HOLD.substring(i, i+1));
                    break;
                case SMBeat.NOTE_HOLD_RELEASE:
                    arrows.append(HOLD.substring(i, i+1));
                    break;
                case SMBeat.NOTE_ROLL_HEAD:
                    arrows.append(HOLD.substring(i, i+1));
                    break;
                case SMBeat.NOTE_MINE:
                    arrows.append("*");
                    break;
            }
        return arrows.toString();
    }

    private void doNewNote()
    {
        if (mBeat == null)
        {
            mPitch.setText("");
            mInstrument.setText("");
            mLoud.setText("");
            mTick.setText("");
            mNote.setText("");
            mAlignment.setText("");
        }
        else
        {
            MIDINote note = mBeat.getNote();
            if (note != null)
            {
                mPitch.setText(MIDINote.NOTES[note.getPitch()] + " ("
                        + note.getPitch() + ")");
                mInstrument.setText(note.getTrack()+" "+MIDILogic.getInstrumentName(note.getBank(),
                        note.getProgram()));
                mLoud.setText(String.valueOf(note.getLoud()));
            }
            else
            {
                mPitch.setText("");
                mInstrument.setText("");
                mLoud.setText("");
            }
            mTick.setText(String.valueOf(mBeat.getTick()));
            mNote.setText(getArrows());
            mAlignment.setText(String.valueOf(mBeat.getAlignment()));
        }
    }

    public SMBeat getBeat()
    {
        return mBeat;
    }

    public void setBeat(SMBeat note)
    {
        mBeat = note;
        doNewNote();
    }
}
