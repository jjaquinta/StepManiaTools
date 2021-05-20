package jo.sm.dle.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import jo.sm.dl.data.sm.SMChart;
import jo.sm.dl.logic.PlayLogic;
import jo.sm.dle.actions.AutoTrackAction;
import jo.sm.dle.actions.PlayAction;
import jo.sm.dle.actions.StopAction;
import jo.sm.dle.data.DirectoryBean;
import jo.sm.dle.data.SongBean;
import jo.sm.dle.logic.RuntimeLogic;
import jo.sm.dle.logic.SongLogic;
import jo.util.ui.swing.utils.ComponentUtils;
import jo.util.ui.swing.utils.ListenerUtils;
import jo.util.utils.obj.IntegerUtils;

public class DLEFrame extends JFrame
{
    private DLEPanel   mClient;
    private JLabel     mStatus;
    private DLEToolbar mToolbar;

    private JMenuBar   mMenu;
    private JMenu      mFile;
    private JMenu      mOpen;
    private JMenuItem  mSave;
    private JMenuItem  mExport;
    private JMenuItem  mSteps;
    private JMenuItem  mExit;
    private JMenu      mEdit;
    private JMenuItem  mAutoTrack;
    private JMenuItem  mRecalc;
    private JMenu      mView;
    private JMenu      mChart;
    private JMenuItem  mZoomIn;
    private JMenuItem  mZoomOut;
    private JMenu      mPlay;
    private JMenuItem  mPlayTrack;
    private JMenuItem  mPlayAll;
    private JMenuItem  mPlayFromCaret;
    private JMenuItem  mStop;

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
        mExport = new JMenuItem("Export");
        mSteps = new JMenuItem("Steps");
        mExit = new JMenuItem("Exit");
        mEdit = new JMenu("Edit");
        mAutoTrack = new JMenuItem(new AutoTrackAction());
        mRecalc = new JMenuItem("Recalc");
        mView = new JMenu("View");
        mChart = new JMenu("Chart");
        mZoomIn = new JMenuItem("Zoom In");
        mZoomOut = new JMenuItem("Zoom Out");
        mPlay = new JMenu("Play");
        mPlayAll = new JMenuItem(new PlayAction());
        mPlayTrack = new JMenuItem("Track");
        mPlayFromCaret = new JMenuItem("From Caret");
        mStop = new JMenuItem(new StopAction());
        mToolbar = new DLEToolbar();
        updateOpenMenu();
    }

    private void initLayout()
    {
        JPanel statusbar = new JPanel();
        statusbar.setLayout(new BorderLayout());
        statusbar.add("Center", mStatus);

        getContentPane().add("North", mToolbar);
        getContentPane().add("Center", mClient);
        getContentPane().add("South", statusbar);
        Dimension d = new Dimension(1024, 1024);
        if (RuntimeLogic.getInstance().getSettings().containsKey("screenWidth"))
            d.width = IntegerUtils.parseInt(RuntimeLogic.getInstance()
                    .getSettings().get("screenWidth"));
        if (RuntimeLogic.getInstance().getSettings()
                .containsKey("screenHeight"))
            d.height = IntegerUtils.parseInt(RuntimeLogic.getInstance()
                    .getSettings().get("screenHeight"));
        setSize(d);

        mMenu.add(mFile);
        mFile.add(mOpen);
        mFile.add(mSave);
        mFile.add(mExport);
        mFile.add(mSteps);
        mFile.add(mExit);
        mMenu.add(mEdit);
        mEdit.add(mRecalc);
        mEdit.add(mAutoTrack);
        mMenu.add(mView);
        mView.add(mChart);
        mView.add(mZoomIn);
        mView.add(mZoomOut);
        mMenu.add(mPlay);
        mPlay.add(mPlayAll);
        mPlay.add(mPlayTrack);
        mPlay.add(mPlayFromCaret);
        mPlay.add(new JSeparator());
        mPlay.add(mStop);
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
        ListenerUtils.listen(mExport, (ev) -> System.out.println("TODO"));
        ListenerUtils.listen(mSteps, (ev) -> SongLogic.recalc());
        ListenerUtils.listen(mExit, (ev) -> dispose());
        ListenerUtils.listen(mZoomIn, (ev) -> RuntimeLogic.zoomIn());
        ListenerUtils.listen(mZoomOut, (ev) -> RuntimeLogic.zoomOut());
        ListenerUtils.listen(mRecalc, (ev) -> SongLogic.recalc());
        ListenerUtils.listen(mSave, (ev) -> SongLogic.save());
        ListenerUtils.listen(mPlayAll, (ev) -> SongLogic.doPlayAll());
        ListenerUtils.listen(mPlayTrack, (ev) -> SongLogic.doPlayTrack());
        ListenerUtils.listen(mPlayFromCaret,
                (ev) -> SongLogic.doPlayFromCaret());
        ListenerUtils.listen(mStop, (ev) -> PlayLogic.stop());
    }

    private void doResized()
    {
        Dimension d = getSize();
        RuntimeLogic.getInstance().getSettings().put("screenWidth",
                String.valueOf(d.width));
        RuntimeLogic.getInstance().getSettings().put("screenHeight",
                String.valueOf(d.height));
    }

    private void doUpdateStatus()
    {
        mStatus.setText(RuntimeLogic.getInstance().getError());
    }

    private void doUpdateSong()
    {
        updateChartMenu();
    }

    private void doSong(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        int o = cmd.indexOf('$');
        String dirName = cmd.substring(0, o);
        String songName = cmd.substring(o + 1);
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
            if (dir.getSongs().size() < 24)
                addSongsToMenu(dir.getName(), dir.getSongs(), dirMenu);
            else
            {
                Map<String,List<SongBean>> songs = new HashMap<>();
                for (SongBean song : dir.getSongs())
                {
                    String index = song.getName().substring(0, 1).toUpperCase();
                    List<SongBean> ss = songs.get(index);
                    if (ss == null)
                    {
                        ss = new ArrayList<>();
                        songs.put(index, ss);
                    }
                    ss.add(song);
                }
                String[] indicies = songs.keySet().toArray(new String[0]);
                Arrays.sort(indicies);
                for (String i : indicies)
                {
                    List<SongBean> ss = songs.get(i);
                    if (ss.size() == 1)
                        addSongToMenu(dir.getName(), dirMenu, ss.get(0));
                    else
                    {
                        JMenu sMenu = new JMenu(i);
                        dirMenu.add(sMenu);
                        addSongsToMenu(dir.getName(), ss, sMenu);
                    }
                }
            }
        }
    }

    public void addSongsToMenu(String dirName, List<SongBean> songs, JMenu dirMenu)
    {
        for (SongBean song : songs)
            addSongToMenu(dirName, dirMenu, song);
    }

    public void addSongToMenu(String dirName, JMenu dirMenu, SongBean song)
    {
        JMenuItem songMenu = new JMenuItem(song.getName());
        dirMenu.add(songMenu);
        songMenu.setActionCommand(dirName + "$" + song.getName());
        ListenerUtils.listen(songMenu, (e) -> doSong(e));
    }

    private void updateChartMenu()
    {
        mChart.removeAll();
        SongBean song = RuntimeLogic.getInstance().getSelectedSong();
        if (song == null)
            return;
        for (SMChart chart : song.getProject().getTune().getCharts())
        {
            JCheckBoxMenuItem chartMenu = new JCheckBoxMenuItem(
                    chart.getNotesDifficulty(),
                    chart.getNotesDifficulty().equals(song.getSelectedChart()));
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
                menu.setSelected(menu.getActionCommand()
                        .equals(song.getSelectedChart()));
            }
        }
    }
}
