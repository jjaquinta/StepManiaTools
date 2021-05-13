package jo.sm.dle.cmd.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import jo.sm.dl.data.SMChart;
import jo.sm.dle.data.DirectoryBean;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.utils.ComponentUtils;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.obj.IntegerUtils;

public class DLEFrame extends JFrame
{
    private DLEPanel mClient;
    private JLabel  mStatus;
    
    private JMenuBar    mMenu;
    private JMenu       mFile;
    private JMenu       mOpen;
    private JMenuItem   mSave;
    private JMenuItem   mExit;
    private JMenu       mEdit;
    private JMenuItem   mRecalc;
    private JMenu       mView;
    private JMenu       mTrack;
    private JMenu       mChart;
    private JMenuItem   mZoomIn;
    private JMenuItem   mZoomOut;

    public DLEFrame()
    {
        super("Dancing Llama Editor");
        initInstantiate();
        initLink();
        initLayout();
    }

    private void initInstantiate()
    {
        mStatus = new JLabel();
        mClient = new DLEPanel();
        mMenu = new JMenuBar();
        mFile = new JMenu("File");
        mOpen = new JMenu("Open");
        mSave = new JMenuItem("Save");
        mExit = new JMenuItem("Exit");
        mEdit = new JMenu("Edit");
        mRecalc = new JMenuItem("Recalc");
        mView = new JMenu("View");
        mTrack = new JMenu("Track");
        mChart = new JMenu("Chart");
        mZoomIn = new JMenuItem("Zoom In");
        mZoomOut = new JMenuItem("Zoom Out");
        updateOpenMenu();
    }

    private void initLayout()
    {
        JPanel statusbar = new JPanel();
        statusbar.setLayout(new BorderLayout());
        statusbar.add("Center", mStatus);

        getContentPane().add("Center", mClient);
        getContentPane().add("South", statusbar);
        Dimension d = new Dimension(1024, 1024);
        if (RuntimeLogic.getInstance().getSettings().containsKey("screenWidth"))
            d.width = IntegerUtils.parseInt(RuntimeLogic.getInstance().getSettings().get("screenWidth"));
        if (RuntimeLogic.getInstance().getSettings().containsKey("screenHeight"))
            d.height = IntegerUtils.parseInt(RuntimeLogic.getInstance().getSettings().get("screenHeight"));
        setSize(d);
        
        mMenu.add(mFile);
        mFile.add(mOpen);
        mFile.add(mSave);
        mFile.add(mExit);
        mMenu.add(mEdit);
        mEdit.add(mRecalc);
        mMenu.add(mView);
        mView.add(mTrack);
        mView.add(mChart);
        mView.add(mZoomIn);
        mView.add(mZoomOut);
        setJMenuBar(mMenu);
    }

    private void initLink()
    {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e)
            {
                doFrameShut();
            }
        });
        RuntimeLogic.listen("error", (ov, nv) -> doUpdateStatus());
        RuntimeLogic.listen("selectedSong", (ov, nv) -> doUpdateSong());
        ComponentUtils.componentResized(this, (ev) -> doResized());
        ListenerUtils.listen(mExit, (ev) -> dispose());
        ListenerUtils.listen(mZoomIn, (ev)->RuntimeLogic.zoomIn());
        ListenerUtils.listen(mZoomOut, (ev)->RuntimeLogic.zoomOut());
        ListenerUtils.listen(mRecalc, (ev) -> SongLogic.recalc());
        ListenerUtils.listen(mSave, (ev) -> SongLogic.save());
    }
    
    private void doResized()
    {
        Dimension d = getSize();
        RuntimeLogic.getInstance().getSettings().put("screenWidth", String.valueOf(d.width));
        RuntimeLogic.getInstance().getSettings().put("screenHeight", String.valueOf(d.height));
    }

    private void doUpdateStatus()
    {
        mStatus.setText(RuntimeLogic.getInstance().getError());
    }

    private void doUpdateSong()
    {
        updateTrackMenu();
        updateChartMenu();
    }
    
    private void doSong(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        int o = cmd.indexOf('$');
        String dirName = cmd.substring(0, o);
        String songName = cmd.substring(o +1);
        for (DirectoryBean dir : RuntimeLogic.getInstance().getDirectories())
            if (dir.getName().equals(dirName))
                for (SongBean song : dir.getSongs())
                    if (song.getName().equals(songName))
                    {
                        RuntimeLogic.select(dir);
                        RuntimeLogic.select(song);
                        return;
                    }
    }
    
    private void doTrack(ActionEvent e)
    {
        int track = IntegerUtils.parseInt(e.getActionCommand());
        SongLogic.toggleTrack(track);
    }
    
    private void doChart(ActionEvent e)
    {
        SongLogic.selectChart(e.getActionCommand());
        updateChartMenuSelection();
    }

    private void doFrameShut()
    {
        RuntimeLogic.term();
        setVisible(false);
        System.exit(0);
    }
    
    private void updateOpenMenu()
    {
        mOpen.removeAll();
        for (DirectoryBean dir : RuntimeLogic.getInstance().getDirectories())
        {
            JMenu dirMenu = new JMenu(dir.getName());
            mOpen.add(dirMenu);
            for (SongBean song : dir.getSongs())
            {
                JMenuItem songMenu = new JMenuItem(song.getName());
                dirMenu.add(songMenu);
                songMenu.setActionCommand(dir.getName()+"$"+song.getName());
                ListenerUtils.listen(songMenu, (e) -> doSong(e));
            }
        }
    }
    
    private void updateTrackMenu()
    {
        mTrack.removeAll();
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        int numTracks = song.getProject().getMIDI().getTracks();
        for (int track = 0; track < numTracks; track++)
        {
            JCheckBoxMenuItem trackMenu = new JCheckBoxMenuItem("Track "+(track+1), song.getTracks().contains(track));
            trackMenu.setIcon(ScorePanel.getTrackSwatch(track));
            mTrack.add(trackMenu);
            trackMenu.setActionCommand(String.valueOf(track));
            ListenerUtils.listen(trackMenu, (e) -> doTrack(e));
        }
    }
    
    private void updateChartMenu()
    {
        mChart.removeAll();
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (SMChart chart : song.getProject().getTune().getCharts())
        {
            JCheckBoxMenuItem chartMenu = new JCheckBoxMenuItem(chart.getNotesDifficulty(), chart.getNotesDifficulty().equals(song.getSelectedChart()));
            mChart.add(chartMenu);
            chartMenu.setActionCommand(chart.getNotesDifficulty());
            ListenerUtils.listen(chartMenu, (e) -> doChart(e));
        }
    }
    
    private void updateChartMenuSelection()
    {
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (int i = 0; i < mChart.getItemCount(); i++)
        {
            JMenuItem comp = mChart.getItem(i);
            if (comp instanceof JCheckBoxMenuItem)
            {
                JCheckBoxMenuItem menu = (JCheckBoxMenuItem)comp;
                menu.setSelected(menu.getActionCommand().equals(song.getSelectedChart()));
            }
        }
    }
}
