import java.io.*;

public class FileRecThread extends Thread {

    int id;
    File filename;
    long start;
    File tofile;

    public FileRecThread(int _id,String _filename,long _start,String _tofile)
    {
        this.id=_id;
        this.filename=new File(_filename);
        this.start=_start;
        this.tofile=new File(_tofile);
    }


    public void run()
    {
        FileSplitFrame.listText.append("文件重组线程"+id+"开始\n");
        if(!tofile.exists())
        {
            try {
                tofile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            RandomAccessFile rafWriter=new RandomAccessFile(tofile,"rw");
            BufferedInputStream bis=new BufferedInputStream(new FileInputStream(filename));
            rafWriter.seek(start);
            byte b[]=new byte[1024];
            long c=filename.length()/1024;
            int s=(int)(filename.length()%1024);
            double len=filename.length();
            for(int i=1;i<=c;i++)
            {
                double p=((i*1024)/len)*100;
                FileSplitFrame.progressBar.setValue((int)p);
                if(c!=i)
                {
                    bis.read(b);
                    rafWriter.write(b);
                }else
                {
                    byte bs[]=new byte[1024+s];
                    bis.read(bs);
                    rafWriter.write(bs);
                }
            }
            bis.close();
            rafWriter.close();




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileSplitFrame.progressBar.setValue(100);
        FileSplitFrame.listText.append("文件重组线程"+id+"结束\n");
    }



}
