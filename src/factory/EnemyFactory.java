package factory;

import edu.hitsz.aircraft.*;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

public class EnemyFactory extends AbstractFactory{

    public static int upgradeEliteHp = 0;

    public static int upgradeMobSpeed = 0;
    @Override
    AbstractFlyingObject createObject(String type, EnemyAircraft enemy) {
        AbstractFlyingObject enemyAircraft = null;

        switch (type) {
            case "MobEnemy":
                enemyAircraft = new MobEnemy(
                        (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                        (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2),
                        0,
                        9+upgradeMobSpeed,
                        30);
                break;
            case "EliteEnemy":
                int ran = (int) (Math.random()*20+1);

                if (ran <= 10){
                    //不会左右移动
                    enemyAircraft = new EliteEnemy(
                            (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                            (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2),
                            0,
                            5,
                            90+upgradeEliteHp);
                }else {
                    //会左右移动
                    enemyAircraft = new EliteEnemy(
                            (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                            (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2),
                            5,
                            4,
                            90+upgradeEliteHp);
                }
                break;
            default:
                break;
        }

        return enemyAircraft;
    }
}
