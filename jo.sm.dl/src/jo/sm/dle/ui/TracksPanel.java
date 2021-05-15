package jo.sm.dle.ui;

import java.awt.BorderLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import jo.sm.dl.data.MIDITrack;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.PCSBeanUtils;

public class TracksPanel extends JComponent
{
    private SongBean    mSong;
    
    private JComboBox<MIDITrack>    mTrackPicker;
    private TrackPanel  mTrackInfo;
    
    public TracksPanel()
    {
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mTrackPicker = new JComboBox<>();
        mTrackInfo = new TrackPanel();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("North", mTrackPicker);
        add("Center", mTrackInfo);
    }

    private void initLink()
    {
        ListenerUtils.listen(mTrackPicker, (ev) -> doSelectTrack());
    }
    
    private void doSelectTrack()
    {
        MIDITrack track = (MIDITrack)mTrackPicker.getSelectedItem();
        SongLogic.selectTrack(track);
    }
    
    private void updateControls()
    {
        if (mSong == null)
            return;
        mTrackPicker.setModel(new DefaultComboBoxModel<>(mSong.getProject().getMIDI().getTrackInfos().toArray(new MIDITrack[0])));
        mTrackPicker.setSelectedItem(mSong.getSelectedTrack());
    }

    public SongBean getSong()
    {
        return mSong;
    }

    public void setSong(SongBean song)
    {
        if (mSong != null)
            PCSBeanUtils.unlisten(mSong, "selectedTrack");
        mSong = song;
        updateControls();
        PCSBeanUtils.listen(mSong, "selectedTrack", (ov,nv)->repaint());
        mTrackInfo.setSong(song);
    }
}
