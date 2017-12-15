package jo.sm.dl.cmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import jo.sm.dl.data.StepData;
import jo.sm.dl.data.StepData.StepNotesData;
import jo.util.utils.io.StreamUtils;

public class StepFileAnalysis
{
    private String[]    mArgs;
    
    private File        mInFile;
    private List<StepData> mStepFiles = new ArrayList<>();
    private Map<String,AllStepNotesData> mNotesByDiff = new HashMap<>();
    private Map<Integer,AllStepNotesData> mNotesByMeter = new HashMap<>();
    private Map<String,Map<Integer,AllStepNotesData>> mNotesByDiffMeter = new HashMap<>();
    
    public StepFileAnalysis(String[] argv)
    {
        mArgs = argv;
    }
    
    public void run()
    {
        parseArgs();
        try
        {
            scan(mInFile);
            analyse();
            report();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    private void report()
    {
        for (String key : mNotesByDiff.keySet())
        {
            AllStepNotesData data = mNotesByDiff.get(key);
            report(data, false);
            Map<Integer, AllStepNotesData> nmdata = mNotesByDiffMeter.get(key);
            Integer[] meters = nmdata.keySet().toArray(new Integer[0]);
            Arrays.sort(meters);
            for (Integer meter : meters)
                report(nmdata.get(meter), true);
        }
        Integer[] keys = mNotesByMeter.keySet().toArray(new Integer[0]);
        Arrays.sort(keys);
        for (Integer key : keys)
        {
            AllStepNotesData data = mNotesByMeter.get(key);
            report(data, true);
        }
    }
    
    private void report(AllStepNotesData data, boolean meter)
    {
        if (meter)
            System.out.println(data.mDifficulty+" ("+data.mMeter+")");
        else
            System.out.println(data.mDifficulty);
        System.out.println("=======================");
        System.out.println("Songs: "+data.mNotes.size());
        report("Notes", data.mNotes);
        report("Holds", data.mHolds);
        report("Rolls", data.mRolls);
        report("Mines", data.mMines);
        report("Note4ths", data.mNote4ths);
        report("Note8ths", data.mNote8ths);
        report("Note12ths", data.mNote12ths);
        report("Note16ths", data.mNote16ths);
        report("Note24ths", data.mNote24ths);
        report("Note32nds", data.mNote32nds);
        report("Note48ths", data.mNote48ths);
        report("Note64ths", data.mNote64ths);
        report("Note192nds", data.mNote192nds);
        reportFloat("BPM", data.mBPM);                
        reportFloat("NPM", data.mNPM);                
        reportFloat("Double%", data.mDoublePC);                
        reportFloat("Length", data.mSongLength);                
        System.out.println();
    }
    
    private void reportFloat(String title, List<Float> data)
    {
        if (data.size() == 0)
            return;
        float tot = 0;
        for (Float i : data)
            tot += i;
        Collections.sort(data);
        float average = tot/data.size();
        float lowq = data.get(data.size()/4);
        float midq = data.get(data.size()/2);
        float uppq = data.get(data.size()*3/4);
        System.out.println(title+": "+average+" ["+lowq+" - "+midq+" - "+uppq+"]");
        if (Float.isNaN(average))
        {
            System.out.print("  (");
            for (Float f : data)
                System.out.print(" "+f);
            System.out.println(" )");
        }
    }
    
    private void report(String title, List<Integer> data)
    {
        int tot = 0;
        for (Integer i : data)
            tot += i;
        if (tot == 0)
            return;
        Collections.sort(data);
        int average = tot/data.size();
        int lowq = data.get(data.size()/4);
        int midq = data.get(data.size()/2);
        int uppq = data.get(data.size()*3/4);
        System.out.println(title+": "+average+" ["+lowq+" - "+midq+" - "+uppq+"]");
    }
    
    private void analyse()
    {
        System.out.println(mStepFiles.size()+" songs scanned");
        int steps = 0;
        for (StepData data : mStepFiles)
        {
            for (StepNotesData nd : data.getNotes())
            {
                AllStepNotesData ndata = mNotesByDiff.get(nd.mDifficulty);
                if (ndata == null)
                {
                    ndata = new AllStepNotesData();
                    ndata.mDifficulty = nd.mDifficulty.toLowerCase();
                    mNotesByDiff.put(nd.mDifficulty, ndata);
                }
                ndata.add(nd);
                steps++;
                Map<Integer,AllStepNotesData> nmdata = mNotesByDiffMeter.get(nd.mDifficulty);
                if (nmdata == null)
                {
                    nmdata = new HashMap<>();
                    mNotesByDiffMeter.put(nd.mDifficulty, nmdata);
                }
                ndata = nmdata.get(nd.mMeter);
                if (ndata == null)
                {
                    ndata = new AllStepNotesData();
                    ndata.mDifficulty = nd.mDifficulty.toLowerCase();
                    ndata.mMeter = nd.mMeter;
                    nmdata.put(nd.mMeter, ndata);
                }
                ndata.add(nd);
                ndata = mNotesByMeter.get(nd.mMeter);
                if (ndata == null)
                {
                    ndata = new AllStepNotesData();
                    ndata.mDifficulty = "combined";
                    ndata.mMeter = nd.mMeter;
                    mNotesByMeter.put(nd.mMeter, ndata);
                }
                ndata.add(nd);
            }
        }
        System.out.println(steps+" steps scanned");
        System.out.println(mNotesByDiff.size()+" difficulty levels");
    }
    
    private void scan(File f) throws IOException
    {
        if (f.isDirectory())
        {
            File[] fs = f.listFiles();
            if (fs != null)
                for (File ffs : fs)
                    scan(ffs);
        }
        else if (f.getName().endsWith(".zip"))
            scanZip(f);
        else if (f.getName().endsWith(".sm"))
            scanStepFile(f);
        //else
        //    System.out.println(f.toString()+" - skipping");
    }
    
    private void scanZip(File f) throws IOException
    {
        System.out.println(f.toString()+" - unzipping");
        FileInputStream fis = new FileInputStream(f);
        scanZip(fis);
        fis.close();
    }
    
    private void scanZip(InputStream is) throws IOException
    {
        ZipInputStream zis = new ZipInputStream(is);
        try
        {
            for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry())
            {
                if (ze.getName().endsWith(".zip"))
                {
                    System.out.println(ze.getName()+" - unzipping");
                    scanZip(zis);
                }
                else if (ze.getName().endsWith(".sm"))
                {
                    System.out.println(ze.getName()+" - scanning");
                    scanStepFile(zis);
                }
                //else
                //    System.out.println(ze.getName()+" - skipping");            
            }
        }
        catch (Exception e)
        {
            System.err.println(e.getLocalizedMessage());
        }
    }
    
    private void scanStepFile(File f) throws IOException
    {
        System.out.println(f.getName()+" - scanning");
        FileInputStream fis = new FileInputStream(f);
        scanStepFile(fis);
        fis.close();
    }
    
    private void scanStepFile(InputStream fis) throws IOException
    {
        String sf = StreamUtils.readStreamAsString(fis, "utf-8");
        for (;;)
        {
            int o = sf.indexOf("//");
            if (o < 0)
                break;
            int e = sf.indexOf('\n', o);
            if (e < 0)
                e = sf.length();
            sf = sf.substring(0, o) + sf.substring(e);
        }
        StepData data = new StepData(); 
        for (StringTokenizer st = new StringTokenizer(sf, ";"); st.hasMoreTokens(); )
        {
            String line = st.nextToken().trim();
            if (line.length() == 0)
                continue;
            if (!line.startsWith("#"))
                continue;
            line = line.substring(1).trim();
            int o = line.indexOf(":");
            if (o < 0)
                continue;
            String key = line.substring(0, o).trim();
            line = line.substring(o + 1).trim();
            if (key.equals("NOTES"))
                data.getRawNotes().add(line);
            else
                data.getRawProps().put(key, line);
        }
        parse(data);
        mStepFiles.add(data);
    }
    
    private void parse(StepData data)
    {
        String bpm = data.getRawProps().getProperty("BPMS");
        int o = bpm.indexOf('=');
        bpm = bpm.substring(o + 1);
        o = bpm.indexOf(",");
        if (o > 0)
            bpm = bpm.substring(0, o);
        data.setBPM(Float.parseFloat(bpm));
        for (String rawNotes : data.getRawNotes())
        {
            StepData.StepNotesData nd = data.new StepNotesData();
            StringTokenizer st = new StringTokenizer(rawNotes, ":");
            nd.mType = st.nextToken().trim();
            st.nextToken(); // author
            nd.mDifficulty = st.nextToken().trim().toLowerCase();
            nd.mMeter = Integer.parseInt(st.nextToken().trim());
            st.nextToken(); // radar
            String measures = st.nextToken();
            st = new StringTokenizer(measures, ",");
            int doubles = 0;
            while (st.hasMoreTokens())
            {
                String measure = st.nextToken();
                StringTokenizer st2 = new StringTokenizer(measure, "\r\n");
                int mcount = 0;
                int notes = 0;
                while (st2.hasMoreTokens())
                {
                    String line = st2.nextToken().trim();
                    if (line.length() == 0)
                        continue;
                    mcount++;
                    int steps = 0;
                    for (char c : line.toCharArray())
                        switch (c)
                        {
                            case '0':
                                break;
                            case '1':
                                nd.mNotes++;
                                notes++;
                                steps++;
                                break;
                            case '2':
                                nd.mHolds++;
                                steps++;
                                break;
                            case '4':
                                nd.mRolls++;
                                steps++;
                                break;
                            case 'M':
                                nd.mMines++;
                                break;
                        }
                    if (line.length() > 4)
                        mcount += line.length()/4 - 1;
                    if (steps > 1)
                        doubles++;
                }
                if (mcount == 4)
                    nd.mNote4ths += notes;
                else if (mcount == 8)
                    nd.mNote8ths += notes;
                else if (mcount == 12)
                    nd.mNote12ths += notes;
                else if (mcount == 16)
                    nd.mNote16ths += notes;
                else if (mcount == 24)
                    nd.mNote24ths += notes;
                else if (mcount == 32)
                    nd.mNote32nds += notes;
                else if (mcount == 48)
                    nd.mNote48ths += notes;
                else if (mcount == 64)
                    nd.mNote64ths += notes;
                else if ((mcount == 96) || (mcount == 192))
                    nd.mNote192nds += notes;
                else
                    System.out.println("Unexpected measure count="+mcount+" for <<"+measure+">>");
                nd.mMeasures++;
            }
            nd.mBPM = data.getBPM();
            nd.mSongLength = nd.mMeasures*4/nd.mBPM;
            nd.mNPM = nd.mNotes/nd.mSongLength;
            if (doubles == 0)
                nd.mDoublePC = 0;
            else
                nd.mDoublePC = doubles/(float)(nd.mNotes - doubles);
            if (Float.isNaN(nd.mDoublePC))
                System.out.println("WTF?");
            data.getNotes().add(nd);
        }
    }

    private void parseArgs()
    {
        for (int i = 0; i < mArgs.length; i++)
            if ("-i".equals(mArgs[i]))
                mInFile = new File(mArgs[++i]);
    }
    
    public static void main(String[] argv)
    {
        StepFileAnalysis app = new StepFileAnalysis(argv);
        app.run();
    }

    public class AllStepNotesData
    {
        public String  mDifficulty;
        public int     mMeter;
        public List<Integer> mNotes = new ArrayList<>();
        public List<Integer> mHolds = new ArrayList<>();
        public List<Integer> mRolls = new ArrayList<>();
        public List<Integer> mMines = new ArrayList<>();
        public List<Integer> mNote4ths = new ArrayList<>();
        public List<Integer> mNote8ths = new ArrayList<>();
        public List<Integer> mNote12ths = new ArrayList<>();
        public List<Integer> mNote16ths = new ArrayList<>();
        public List<Integer> mNote24ths = new ArrayList<>();
        public List<Integer> mNote32nds = new ArrayList<>();
        public List<Integer> mNote48ths = new ArrayList<>();
        public List<Integer> mNote64ths = new ArrayList<>();
        public List<Integer> mNote192nds = new ArrayList<>();
        public List<Float> mBPM = new ArrayList<>();
        public List<Float> mNPM = new ArrayList<>();
        public List<Float> mSongLength = new ArrayList<>();
        public List<Float> mDoublePC = new ArrayList<>();

        public void add(StepNotesData nd)
        {
            mNotes.add(nd.mNotes);
            mHolds.add(nd.mHolds);
            mRolls.add(nd.mRolls);
            mMines.add(nd.mMines);
            mNote4ths.add(nd.mNote4ths);
            mNote8ths.add(nd.mNote8ths);
            mNote12ths.add(nd.mNote12ths);
            mNote16ths.add(nd.mNote16ths);
            mNote24ths.add(nd.mNote24ths);
            mNote32nds.add(nd.mNote32nds);
            mNote48ths.add(nd.mNote48ths);
            mNote64ths.add(nd.mNote64ths);
            mNote192nds.add(nd.mNote192nds);
            mBPM.add(nd.mBPM);
            mNPM.add(nd.mNPM);
            mSongLength.add(nd.mSongLength);
            mDoublePC.add(nd.mDoublePC);
        }
    }

}
