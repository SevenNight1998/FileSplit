import javax.swing.*;
import java.io.*;

public class SplitFileThread extends Thread {

    int _id;
    long block,f_start;
    File f;
    public SplitFileThread(int id, long start, long block, String filePart)
    {
        this._id=id;
        this.block=block;
        this.f_start=start;
        f=new File(filePart);
    }

    public void run()
    {
        System.out.println(_id+"Thread"+_id+"started");
        System.out.println(_id+"需要截取文件："+block+"字节");
        try {
            byte[] b=new byte[1024];
            RandomAccessFile rafRead=new RandomAccessFile(f,"r");
            File SplitFiles=new File(f.getParent()+"/"+f.getName()+".part"+_id);
            rafRead.seek(f_start);
            long c=block/1024;
            int s=(int)(block%1024);

            if(!SplitFiles.exists())
            {
                SplitFiles.createNewFile();
                BufferedOutputStream bos=new BufferedOutputStream(new FileOutputStream(SplitFiles));
                System.out.println(_id+"需要截取"+c+"次");
               double p;
                for(double i=1;i<=c;i++)
                {
                    p=((i*1024)/block)*100;
                    FileSplitFrame.progressBar.setValue((int)p);
                    if(i!=c)
                    {
                        rafRead.read(b);
                        bos.write(b);
                        bos.flush();
                    }else
                    {
                        byte bs[]=new byte[1024+s];
                        System.out.println(_id+"最后剩余"+s+"字节");
                        rafRead.read(bs);
                        bos.write(bs);
                        bos.flush();
                    }

                }
                System.out.println(_id+":"+SplitFiles.length());
                FileSplitFrame.progressBar.setValue(100);
                rafRead.close();
                bos.close();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Thread"+_id+"end");
    }


}
