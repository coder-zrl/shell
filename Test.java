package shell脚本;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test {
    String filePath;

    public Test(String nowFilePath) {
        this.filePath = nowFilePath;
    }

    public void println(String str) {
        str = str.substring(1, str.length() - 1);
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).equals("\\")) {
                i += 1;
                if (str.substring(i, i + 1).equals("\\")) {
                    System.out.print("\\");
                } else if (str.substring(i, i + 1).equals("n")) {
                    System.out.print("\n");
                } else if (str.substring(i, i + 1).equals("t")) {
                    System.out.print("\t");
                } else {
                    System.out.print(str.substring(i, i + 1));
                }
            } else {
                System.out.print(str.substring(i, i + 1));
            }
        }
        System.out.println();
    }

    public void cd(String new_filePath) {
        //看看是不是要去别的盘
        if (new_filePath.substring(1, 2).equals(":")) {
            filePath = new_filePath;
        } else {
            //判断是不是从D:进来的,是的话去掉\
            if (filePath.endsWith(":\\")) {
                this.filePath = filePath.substring(0, filePath.length() - 1);
            }
            if (new_filePath.equals("..")) {
                int end = this.filePath.lastIndexOf("\\");
                this.filePath = this.filePath.substring(0, end);
            } else {
                this.filePath = this.filePath + File.separator + new_filePath;
            }
            //判断是不是退到D:了,是的话加上\
            if (filePath.endsWith(":")) {
                this.filePath += "\\";
            }
        }
    }

    public void mkdir(String foldername) {
        File file = new File(this.filePath + File.separator + foldername);
        boolean boo = file.mkdirs();
    }

    public void ls() {
        File file = new File(this.filePath);
        String[] files = file.list();
        for (String f : files) {
            System.out.println(f);
        }
    }

    public void grep(String pattern, String filename) {
        try {
            pattern = pattern.substring(1, pattern.length() - 1);
            File file = new File(filePath + File.separator + filename);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "utf-8");//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                Pattern p = Pattern.compile(pattern);
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    // 创建 matcher 对象
                    Matcher m = p.matcher(lineTxt);
                    if (m.find()&&lineTxt.startsWith(pattern)) {
                        System.out.println(lineTxt);
                    }
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }
    public void pwd(){
        System.out.println(filePath);
    }
    public void cat(String aim_filePath){
        try {
            String path;
            if (aim_filePath.substring(1,2).equals(":")){
                path=aim_filePath;
            }else{
                path=filePath + File.separator + aim_filePath;
            }
            File file = new File(path);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), "utf-8");//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                        System.out.println(lineTxt);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
    }
    public void cp(File file,File toFile) throws Exception {
        byte[] b = new byte[1024];
        int a;
        FileInputStream fis;
        FileOutputStream fos;
        if (file.isDirectory()) {
            String filepath = file.getAbsolutePath();
            filepath=filepath.replaceAll("\\\\", "/");
            String toFilepath = toFile.getAbsolutePath();
            toFilepath=toFilepath.replaceAll("\\\\", "/");
            int lastIndexOf = filepath.lastIndexOf("\\\\");
            toFilepath = toFilepath + filepath.substring(lastIndexOf,filepath.length());
            File copy=new File(toFilepath);
            //复制文件夹
            if (!copy.exists()) {
                copy.mkdir();
            }
            //遍历文件夹
            for (File f : file.listFiles()) {
                cp(f, copy);
            }
        } else {
            if (toFile.isDirectory()) {
                String filepath = file.getAbsolutePath();
                filepath=filepath.replaceAll("\\\\", "/");
                String toFilepath = toFile.getAbsolutePath();
                toFilepath=toFilepath.replaceAll("\\\\", "/");
                int lastIndexOf = filepath.lastIndexOf("/");
                toFilepath = toFilepath + filepath.substring(lastIndexOf ,filepath.length());

                //写文件
                File newFile = new File(toFilepath);
                fis = new FileInputStream(file);
                fos = new FileOutputStream(newFile);
                while ((a = fis.read(b)) != -1) {
                    fos.write(b, 0, a);
                }
            } else {
                //写文件
                fis = new FileInputStream(file);
                fos = new FileOutputStream(toFile);
                while ((a = fis.read(b)) != -1) {
                    fos.write(b, 0, a);
                }
            }

        }
    }
    public void rm(String filename){
        File file = new File(filePath + File.separator +filename);
        boolean result=file.delete();
        if (!result){
            System.out.println("此文件不存在");
        }
        //删除临时文件
        file.deleteOnExit();
    }
    public static void main(String[] args) {
        Test test = new Test(System.getProperty("user.dir"));
        while (true) {
            try {
                System.out.print(test.filePath + " :");
                Scanner cin = new Scanner(System.in);//输入
                String message = cin.nextLine();//输入一行命令
                String[] cmd = message.split(" ");
                if (cmd[0].equals("ls")) {
                    test.ls();
                } else if (cmd[0].equals("cd")) {
                    test.cd(cmd[1]);
                } else if (cmd[0].equals("mkdir")) {
                    test.mkdir(cmd[1]);
                } else if (cmd[0].equals("echo")) {
                    test.println(cmd[1]);
                } else if (cmd[0].equals("grep")) {
                    test.grep(cmd[1], cmd[2]);
                } else if(cmd[0].equals("quit")){
                    System.out.println("程序退出");
                    break;
                } else if(cmd[0].equals("pwd")){
                    test.pwd();
                } else if(cmd[0].equals("cat")){
                    test.cat(cmd[1]);
                }else if(cmd[0].equals("cp")){
                    //需要复制的目标文件或目标文件夹
                    File file = new File(cmd[1]);
                    //复制到的位置
                    File toFile = new File(cmd[2]);
                    test.cp(file,toFile);
                    System.out.println("复制");
                }else if(cmd[0].equals("rm")){
                    test.rm(cmd[1]);
                }else {
                    System.out.println("这个命令目前还没计划");
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }
}
