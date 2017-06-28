import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Properties;

public class FileSplitFrame extends JFrame implements ActionListener{
    JLabel title;
    String filetemp="";
    static JTextArea listText;
    JScrollPane jScrollPane;
    JTextField count_file,filebox;
    JButton file_entry,split_file,im_file;
    JList list;
    static JProgressBar progressBar;
    public FileSplitFrame(String title)
    {
        super(title);
        init();
        com_init();
        com_listener_init();
    }
    private void init()
    {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(100,100,600,700);
        this.setLayout(null);
        this.setResizable(false);
        this.setVisible(true);
    }

    private void com_init()
    {
        im_file=new JButton("文件重组");
        im_file.addActionListener(this);
        im_file.setBounds(50,540,400,50);
        JLabel ImFile=new JLabel("文件重组：");
        ImFile.setBounds(50,500,400,20);
        progressBar=new JProgressBar();
        progressBar.setBounds(50,170,400,50);
        JLabel count_f=new JLabel("文件数：");
        filebox=new JTextField();
        file_entry=new JButton("选择文件");
        title=new JLabel("FileSplit");
        listText=new JTextArea();
        split_file=new JButton("分割");
        count_file=new JTextField();
        jScrollPane=new JScrollPane(listText);
        title.setFont(new Font("arial",Font.PLAIN,30));
        title.setHorizontalAlignment(JLabel.CENTER);
        title.setVerticalAlignment(JLabel.CENTER);
        count_f.setBounds(460,170,100,20);
        title.setBounds(100,10,400,100);
        filebox.setBounds(50,130,400,20);
        filebox.setEditable(false);
        file_entry.setBounds(450,130,100,20);
        split_file.setBounds(460,280,80,20);
        count_file.setBounds(460,200,80,20);
        jScrollPane.setBounds(50,240,400,250);
        jScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(split_file);
        this.add(jScrollPane);
        this.add(progressBar);
        this.add(count_f);
        this.add(im_file);
        this.add(count_file);
        this.add(ImFile);
        this.add(filebox);
        this.add(title);
        this.add(file_entry);
    }
    private void com_listener_init()
    {
        file_entry.addActionListener(this);
        split_file.addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand().toString())
        {
            case "选择文件":
                file_selection();
                break;
            case "分割":
                file_Split();
                break;
            case "文件重组":
                ImFile();
                break;
        }
    }

    private void file_selection()
    {

        JFileChooser fileChooser=new JFileChooser();
        fileChooser.setCurrentDirectory(new File(filetemp));
        fileChooser.showOpenDialog(this);
        if(fileChooser.getSelectedFile()==null )
        {
            filebox.setText("没有选择文件");
        }else
        {
            filetemp=fileChooser.getSelectedFile().getParent();
            filebox.setText(fileChooser.getSelectedFile().toString());


        }
    }
    private void file_Split()
    {
        FileSplitFrame.listText.append("文件分割开始：\n");
        File f=new File(filebox.getText());

        try {
            PrintWriter pw=new PrintWriter(new FileWriter(f.getParent()+"/"+f.getName()+".properties"),true);
            Properties properties=new Properties();
            if(f.exists())
            {
                System.out.println("文件大小："+f.length());
                int count = Integer.valueOf(count_file.getText());
                properties.put("count",count+"");
                properties.put("filename",f.getName());
                long block=f.length()/count;
                long blocks=f.length()%count;
                FileSplitFrame.listText.append("创建文件分割线程：\n");
                for(int i=0;i<count;i++)
                {
                    FileSplitFrame.listText.append("创建分割线程"+(i+1)+":\n");
                    properties.put("files"+(i+1),f.getName()+"."+"part"+(i+1));
                    if(i!=count-1)
                    {

                        new SplitFileThread(i+1,i*block,block,f.getAbsolutePath()).start();
                        System.out.println(i+"号文件大小"+block);

                    }else
                    {
                        new SplitFileThread(i+1,i*block,block+blocks,f.getAbsolutePath()).start();
                        System.out.println(i+"号文件大小"+(block+blocks));
                    }
                }
                FileSplitFrame.listText.append("创建分割线程结束\n");
                FileSplitFrame.listText.append("创建分割配置文件:"+f.getParent()+"\\"+f.getName()+".properties\n");
                properties.list(pw);
                pw.println();
            }else
            {
                JOptionPane.showMessageDialog(this,"文件不存在");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void ImFile()
    {
        FileSplitFrame.listText.append("创建文件重组线程\n");
        File f=new File(filebox.getText());
        Properties im_pro=new Properties();
        try {
            im_pro.load(new FileReader(f));
            int count=Integer.valueOf(im_pro.get("count").toString());

            for(int i=0;i<count;i++)
            {
                File file=new File(f.getParent()+"\\"+im_pro.getProperty("files"+(i+1)));
               new FileRecThread(i,f.getParent()+"\\"+file.getName(),i*file.length(),f.getParent()+"\\"+"Filerec-"+im_pro.getProperty("filename")).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileSplitFrame.listText.append("创建文件重组线程结束\n");
    }


}
