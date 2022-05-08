package edu.hitsz.application;

import DAO.Player;
import DAO.PlayerDaoImpl;
import edu.hitsz.application.difficulty.Easy;
import edu.hitsz.application.difficulty.Medium;
import edu.hitsz.view.DifficultySelect;
import edu.hitsz.view.RankDisplay;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 程序入口
 * @author hitsz
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static Path fileInfo;

    public static File myFile;
    public static Game game;
    public static Object object = new Object();
    public static DifficultySelect ds = new DifficultySelect();
    public static JPanel selectPanel = ds.getMainPanel();
    public static RankDisplay rd;
    public static JPanel rankPanel;
    public static PlayerDaoImpl playerDao;
    public static int num;
    public static int ID = 1;
    public static JFrame frame = new JFrame("Aircraft War");

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Hello Aircraft War");

        // 获得屏幕的分辨率，初始化 Frame
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Runnable mainThread = () -> {

            //添加选择窗口
            frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setResizable(false);
                //设置窗口的大小和位置,居中放置
            frame.setBounds(((int) screenSize.getWidth() - WINDOW_WIDTH) / 2, 0,
                    WINDOW_WIDTH, WINDOW_HEIGHT);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(selectPanel);
            frame.setVisible(true);

            //等待选择
            synchronized (object){
                try {
                    while (ds.getIsVisible()){
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //添加游戏窗口
            game = ds.getGame();
            game.setIsSoundOpen(ds.getIsSoundOpen());
            frame.remove(selectPanel);
            frame.setContentPane(game);
            frame.setVisible(true);
            game.action();

            //等待游戏结束
            synchronized (object){
                try {
                    while (!game.getGameOverFlag()){
                        object.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //读取排名文件
            try {
                read();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //新建排名窗口对象，同时获取用户名
            num = playerDao.getNum()+1;
            try {
                rd = new RankDisplay(num);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //根据获取的用户名新建玩家信息，同时保存至文件
            try {
                refreshAndSave();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //创建并添加table,显示排名
            try {
                rd.createTable();
            } catch (IOException e) {
                e.printStackTrace();
            }
            rankPanel = rd.getMainPanel();
            frame.remove(selectPanel);
            frame.setContentPane(rankPanel);
            frame.setVisible(true);

        };

        new Thread(mainThread,"主线程").start();
    }

    //读取文件
    private static void read() throws IOException {
        playerDao = new PlayerDaoImpl();
        if (game instanceof Easy){
            fileInfo = Paths.get("file/easyRank.txt");
            myFile = new File("file/easyRank.txt");
        } else if (game instanceof Medium){
            fileInfo = Paths.get("file/mediumRank.txt");
            myFile = new File("file/mediumRank.txt");
        }else {
            fileInfo = Paths.get("file/hardRank.txt");
            myFile = new File("file/hardRank.txt");
        }
        /**
         * 读取之前的游戏记录
         * */

        InputStreamReader reader = new InputStreamReader(new FileInputStream(myFile), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        String lineTxt;
        while ((lineTxt = bufferedReader.readLine()) != null){
            int txtID = Integer.parseInt(lineTxt);
            lineTxt = bufferedReader.readLine();
            int txtScore = Integer.parseInt(lineTxt);
            lineTxt = bufferedReader.readLine();
            String txtName = lineTxt;
            lineTxt = bufferedReader.readLine();
            String txtDate = lineTxt;
            playerDao.doAdd(new Player(txtID,txtScore,txtName,txtDate));
            ID = ID+1;
        }
    }
    private static void refreshAndSave() throws IOException {
         /**
         * 构造本次游戏的记录
         * 然后进行排名
         * */
        java.util.List<Player> ranker = playerDao.getAllInformation();

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(currentDate);

        //获取输入的用户名
        String playerName = rd.getUsername();

        Player newInfo = new Player(ID,game.getScore(),playerName,dateString);
        playerDao.doAdd(newInfo);
        playerDao.doSort();

        /**
         * * 将排名表写道rank文件，排名信息写进information文件
         */
        List<String> infoList = new ArrayList<>();
        for (Player player:ranker) {
            //用集合准备两个文件要写入的信息
            String infoID = String.valueOf(player.getPlayerID());
            infoList.add(infoID);
            String infoScore = String.valueOf(player.getScore());
            infoList.add(infoScore);
            String infoName = player.getPlayerName();
            infoList.add(infoName);
            String infoDate = player.getPlayDate();
            infoList.add(infoDate);
        }
        Files.write(fileInfo, infoList, StandardOpenOption.WRITE);
    }
}
