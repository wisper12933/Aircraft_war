package edu.hitsz.application;

import edu.hitsz.aircraft.*;
import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.EnemyBullet;
import factory.AbstractFactory;
import factory.BossFactory;
import factory.EnemyFactory;
import edu.hitsz.supply.AbstractSupply;
import edu.hitsz.supply.BombSupply;
import edu.hitsz.supply.FireSupply;
import edu.hitsz.supply.HpSupply;
import thread.BgmThread;
import thread.BossBgmThread;
import thread.MusicThread;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import thread.stopFlag.BossAppearFlag;
import thread.stopFlag.GameStopFlag;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 *
 * @author hitsz
 */
public abstract class Game extends JPanel {

    private int backGroundTop = 0;

    protected BufferedImage backGround = null;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    private final ScheduledExecutorService bgmServer;

    private final ScheduledExecutorService bossBgmServer;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 40;

    private final HeroAircraft heroAircraft;
    private final List<EnemyAircraft> enemyAircrafts;
    private final List<AbstractBullet> heroBullets;
    private final List<AbstractBullet> enemyBullets;
    private final List<AbstractSupply> supply;

    protected int enemyMaxNumber = 5;

    /**
     * boss生成条件
     * */
    protected int bossAppear;
    private int bossTime = 0;
    public static int bombScore = 0;
    protected int eliteRate;
    protected boolean needBoss;
    private int bossInterval;

    protected int upgradeBossHp = 0;


    private boolean gameOverFlag = false;

    protected boolean changeBgm = false;
    private boolean isSoundOpen;
    private int score = 0;
    protected int time = 0;


    /**
     * 周期（ms)
     * 指示子弹的发射、敌机的产生频率
     */
    private int cycleDuration = 600;
    private int cycleTime = 0;


    public Game() {
        heroAircraft = HeroAircraft.getInstance();
        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        supply = new LinkedList<>();

        /**
         * Scheduled 线程池，用于定时任务调度
         * 关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
         * apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-action-%d").daemon(true).build());

        this.bgmServer = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-bgm-%d").daemon(true).build());

        this.bossBgmServer = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("game-bossBgm-%d").daemon(true).build());

        //启动英雄机鼠标监听
        new HeroController(this, heroAircraft);

    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public final void action() {

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;

            // 周期性执行（控制频率）
            if (timeCountAndNewCycleJudge()) {
                //System.out.println(time);//需要打印时间时再解除
                /**生成随机数来判定敌机类型
                 *  频率 普通 > 精英
                 * */
                int ran = (int) (Math.random()*100+1);

                /**工厂模式
                 * */
                AbstractFactory enemyFactory = new EnemyFactory();
                AbstractFactory bossFactory = new BossFactory();
                /**新敌机产生
                 * */
                //生成boss机
                bossInterval = score - bossTime*bossAppear;

                if (bossInterval >= bossAppear//每过400分生成一次
                        && !BossAppearFlag.bossAppearFlag && needBoss){
                    //达到阈值且没有boss则生成boss,无视最大敌机数量，只要满足条件就一定生成
                    enemyAircrafts.add((EnemyAircraft) bossFactory.produceObject("BossEnemy",null));

                    BossAppearFlag.bossAppearFlag = true;
                    bossTime ++;
                    System.out.println("当前boss血量："+ (BossFactory.bossHp+BossFactory.addHp));
                    BossFactory.addHp += upgradeBossHp;

                    if (upgradeBossHp != 0){
                        System.out.println("下一个boss血量增加"+BossFactory.addHp+",即需要多射击"+(BossFactory.addHp/30)+"下");
                    }

                    bossInterval = 0;
                }

                if (bossInterval >= bossAppear && BossAppearFlag.bossAppearFlag && needBoss){
                    //到达下一个阈值时，boss还没打完，则bossTime也加一
                    bossTime ++;
                }

                if (enemyAircrafts.size() < enemyMaxNumber) {
                    //生成非boss机
                    if(ran <= eliteRate){
                        //需要转一下类(AbstractFlyingObject -> EnemyAircraft)
                        enemyAircrafts.add((EnemyAircraft) enemyFactory.produceObject("MobEnemy",null));
                    }
                    else{
                        enemyAircrafts.add((EnemyAircraft) enemyFactory.produceObject("EliteEnemy",null));
                    }
                }
                // 敌机射出子弹
                enemyShootAction();
            }
            //英雄机射出子弹
            if (heroShootRate()){
                heroBullets.addAll(heroAircraft.shoot());
            }

            // 子弹移动
            bulletsMoveAction();

            // 道具移动
            supplyMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //每个时刻重绘界面
            repaint();

            //刷新难度
            difficultUpgrade();

            // 游戏结束检查
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                gameOverFlag = true;
                GameStopFlag.gameOverFlag = true;
                System.out.println("Game Over!");
                executorService.shutdown();

                // 释放锁
                synchronized (Main.object){
                    Main.object.notify();
                }
                new MusicThread("src/videos/game_over.wav").start();
            }
        };

        /**
         * 循环播放
         * boss机未生成，播放游戏bgm
         * boss机生成，暂停游戏bgm，播放boss bgm
         * boss机坠毁，暂停boss bgm，播放游戏bgm
         * */
        if (isSoundOpen){//两种bgm
            Runnable bgmSound;
            Runnable bossBgmSound;
            if (changeBgm){
                bgmSound = new BgmThread("src/videos/common.wav");
                bossBgmSound = new BossBgmThread("src/videos/real_boss.wav");
            }else {
                bgmSound = new BgmThread("src/videos/bgm.wav");
                bossBgmSound = new BossBgmThread("src/videos/bgm_boss.wav");
            }

            //游戏bgm
            bgmServer.scheduleWithFixedDelay(bgmSound, 10, 10, TimeUnit.MILLISECONDS);
            //boss bgm
            bossBgmServer.scheduleWithFixedDelay(bossBgmSound, 10, 10, TimeUnit.MILLISECONDS);
        }
        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */

        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    //***********************
    //      Action 各部分
    //***********************
    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }
    private void enemyShootAction() {
        // 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            //敌机被摧毁后不再射击
            if (!enemyAircraft.notValid()){
                //所有敌机都有shoot函数，但是Mob的shoot为空，无操作
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }
    }

    private void bulletsMoveAction() {
        for (AbstractBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (AbstractBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void supplyMoveAction(){
        for (AbstractSupply supply : supply){
            supply.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (EnemyAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // 敌机子弹攻击英雄
        for (AbstractBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                //消失的子弹不检测
                continue;
            }
            //因为敌机子弹只会撞击英雄机，且英雄机只有一个，所以无需过多的步骤
            if (heroAircraft.crash(bullet)) {
                // 英雄机撞击到敌机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (AbstractBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (EnemyAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    //音效
                    if (isSoundOpen){
                        new MusicThread("src/videos/bullet_hit.wav").start();
                    }

                    if (enemyAircraft.notValid()) {
                        //判断是否为精英敌机
                        if(enemyAircraft instanceof EliteEnemy){
                            // 获得分数，产生道具补给
                            // 限定只有80%的概率生成道具
                            score += 30;
                            int ran = (int)(Math.random()*100 +1);
                            if (ran <= 90){
                                supply.add(enemyAircraft.bonus());
                            }
                        }else if (enemyAircraft instanceof BossEnemy){
                            // 先标记boss消失
                            // 再生成道具
                            BossAppearFlag.bossAppearFlag = false;
                            score += 90;
                            int propNum = ((BossEnemy) enemyAircraft).getPropNum();
                            for (int i=0; i<propNum; i++){
                                //因为boss会生成两个道具，所以位置应该调整，并且boss没有y方向速度，所以要给道具添加上
                                AbstractSupply bossSupply = enemyAircraft.bonus();
                                bossSupply.setSpeedY(3);
                                bossSupply.setLocation(enemyAircraft.getLocationX() +(i*2 - propNum + 1)*40,enemyAircraft.getLocationY());
                                supply.add(bossSupply);
                            }
                        } else{
                            score += 10;
                        }
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // 我方获得道具，道具生效
        for (AbstractSupply supply : supply){
            if (supply.notValid()){
                //道具不存在则跳过该道具
                continue;
            }
            //英雄机碰到道具
            if (heroAircraft.crash(supply)){
                if (supply instanceof HpSupply){
                    //加血道具
                    ((HpSupply) supply).executeHp(heroAircraft);

                    //音效
                    if (isSoundOpen){
                        new MusicThread("src/videos/get_supply.wav").start();
                    }
                }
                else if (supply instanceof FireSupply){
                    //火力道具生效15s
                    ((FireSupply) supply).executeFire(heroAircraft);

                    //音效
                    if (isSoundOpen){
                        new MusicThread("src/videos/get_supply.wav").start();
                    }
                }
                else if (supply instanceof BombSupply){
                    //炸弹道具
                        //添加订阅者
                    for (AbstractBullet bullet:enemyBullets){
                        ((BombSupply) supply).addObject(bullet);
                    }
                    for (AbstractAircraft enemy:enemyAircrafts){
                        ((BombSupply) supply).addObject(enemy);
                    }
                    ((BombSupply) supply).executeBomb(this);

                    //音效
                    if (isSoundOpen){
                        new MusicThread("src/videos/bomb_explosion.wav").start();
                    }
                }
                else{
                    //显示是否出现未知道具，便于更改
                    System.out.println("ERROR!");
                }
                //道具生效后消失
                supply.vanish();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 删除无效道具
     * 4. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supply.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写paint方法
     * 通过重复调用paint方法，实现游戏动画
     *
     * @param  g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);

        // 绘制背景,图片滚动
        g.drawImage(backGround, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(backGround, 0, this.backGroundTop, null);
        this.backGroundTop += 1;
        if (this.backGroundTop == Main.WINDOW_HEIGHT) {
            this.backGroundTop = 0;
        }

        // 先绘制子弹，再绘制道具，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, supply);
        paintImageWithPositionRevised(g, heroBullets);

        paintImageWithPositionRevised(g, enemyAircrafts);

        g.drawImage(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2, null);

        //绘制得分和生命值
        paintScoreAndLife(g);

    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            g.drawImage(image, object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2, null);
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 10;
        int y = 25;
        g.setColor(new Color(16711680));
        g.setFont(new Font("SansSerif", Font.BOLD, 22));
        g.drawString("SCORE:" + this.score, x, y);
        y = y + 20;
        g.drawString("LIFE:" + this.heroAircraft.getHp(), x, y);
    }


    public abstract void difficultUpgrade();//提升难度
    public abstract boolean heroShootRate();//控制英雄机设计频率
    public int getScore(){
        return score;
    }
    public void setScore(int score){
        this.score = score;
    }
    public boolean getGameOverFlag(){
        return gameOverFlag;
    }
    public void setIsSoundOpen(boolean isOpen){
        isSoundOpen = isOpen;
    }
}
