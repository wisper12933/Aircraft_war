package edu.hitsz.view;

import DAO.Player;
import DAO.PlayerDao;
import DAO.PlayerDaoImpl;
import edu.hitsz.application.Main;
import edu.hitsz.application.difficulty.Easy;
import edu.hitsz.application.difficulty.Medium;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class RankDisplay {
    private JPanel mainPanel;
    private JTextField rankTextField;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JPanel middlePanel;
    private JScrollPane midScrollPanel;
    private JTable rankTable;
    private JButton deleteButton;

    private PlayerDao rdPlayerDao;
    private File rankFile;

    private Path fileInfo;
    private InputStreamReader reader;
    private BufferedReader bufferedReader;
    private int num;
    private int line = 4;
    private String[] columnName = {"排名","成绩","用户名","游戏时间"};
    private String[][] tableData;
    private String lineTxt;
    private DefaultTableModel model;

    private String Username;

    public RankDisplay(int num) throws FileNotFoundException {
        //先告知输入用户名
        Username = JOptionPane.showInputDialog(null,"请输入用户名","输入",JOptionPane.QUESTION_MESSAGE);

        this.num = num;
        rdPlayerDao = new PlayerDaoImpl();

        //选择读取与更改文件的位置
        if (Main.game instanceof Easy){
            rankFile = new File("file/easyRank.txt");
            fileInfo = Paths.get("file/easyRank.txt");
            rankTextField.setText("简单模式排行榜");
        }else if (Main.game instanceof Medium){
            rankFile = new File("file/mediumRank.txt");
            fileInfo = Paths.get("file/mediumRank.txt");
            rankTextField.setText("普通模式排行榜");
        }else {
            rankFile = new File("file/hardRank.txt");
            fileInfo = Paths.get("file/hardRank.txt");
            rankTextField.setText("英雄末路排行榜");
        }

        //创建流
        reader = new InputStreamReader(new FileInputStream(rankFile), StandardCharsets.UTF_8);
        bufferedReader = new BufferedReader(reader);

        //创建table数组
        if (num > 0){
            tableData = new String[num][line];
        }else {
            tableData = new String[1][line];
        }

        //按键事件
        deleteButton.addActionListener(e -> {
            int isDelete = JOptionPane.showConfirmDialog(null,"确定删除？","提示",JOptionPane.YES_NO_CANCEL_OPTION);
            if (isDelete == JOptionPane.YES_OPTION){
                refresh();
                JOptionPane.showMessageDialog(null,"删除成功");
            }
        });
    }

    //所有调用该类的情况都要使用该方法来构造table
    public void createTable() throws IOException {
        //创建dao和data数组
        int i = 0;//行
        while ((lineTxt = bufferedReader.readLine()) != null){
            int txtID = Integer.parseInt(lineTxt);
            tableData[i][0] = String.valueOf(i+1);

            lineTxt = bufferedReader.readLine();
            tableData[i][1] = lineTxt;
            int txtScore = Integer.parseInt(lineTxt);

            lineTxt = bufferedReader.readLine();
            tableData[i][2] = lineTxt;
            String txtName = lineTxt;

            lineTxt = bufferedReader.readLine();
            tableData[i][3] = lineTxt;
            String txtDate = lineTxt;

            rdPlayerDao.doAdd(new Player(txtID,txtScore,txtName,txtDate));
            i++;
        }

        //生成该类的model
        model = new DefaultTableModel(tableData, columnName){
            @Override
            public boolean isCellEditable(int row, int col){
                return false;
            }
        };

        //添加到界面
        rankTable.setModel(model);
        midScrollPanel.setViewportView(rankTable);
    }

    private void refresh(){
        //选择行
        int row = rankTable.getSelectedRow();
        String deleteChoice = rankTable.getValueAt(row,3).toString();
        //删掉对应行
        if(row != -1){
            model.removeRow(row);
        }
        //Dao层操作
        rdPlayerDao.doDelete(deleteChoice);
        List<Player> ranker = rdPlayerDao.getAllInformation();
        //重写文件
        try {
            //清空原文件
            FileWriter fileWriter = new FileWriter(rankFile);
            fileWriter.write("");

            //准备list
            List<String> infoList = new ArrayList<>();
            for (Player player:ranker){

                String infoID = String.valueOf(player.getPlayerID());
                infoList.add(infoID);

                String infoScore = String.valueOf(player.getScore());
                infoList.add(infoScore);

                String infoName = player.getPlayerName();
                infoList.add(infoName);

                String infoDate = player.getPlayDate();
                infoList.add(infoDate);
            }

            //重写
            Files.write(fileInfo, infoList, StandardOpenOption.WRITE);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        //刷新排名
        String[][] temp = tableData;
        resetNum();
        int number = getNum();
        if (number>0){
            tableData = new String[number][line];
            if (row >= 0) System.arraycopy(temp, 0, tableData, 0, row);
            for (int r=row; r<number; r++){
                tableData[r] = temp[r+1];
                tableData[r][0] = String.valueOf(r+1);
            }
            model = new DefaultTableModel(tableData, columnName){
                @Override
                public boolean isCellEditable(int row, int col){
                    return false;
                }
            };
            //添加到界面
            rankTable.setModel(model);
            midScrollPanel.setViewportView(rankTable);
        }
    }
    public void resetNum(){
        num--;
    }
    public int getNum() {
        return num;
    }
    public JPanel getMainPanel(){
        return mainPanel;
    }
    public String getUsername(){
        return Username;
    }

}
