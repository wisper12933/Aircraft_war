package factory;

import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.EnemyAircraft;
import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.basic.AbstractFlyingObject;

public class BossFactory extends AbstractFactory{

    public static int addHp = 0;
    public static boolean isHpUpgrade = false;
    public static int bossHp = 0;
    @Override
    AbstractFlyingObject createObject(String type, EnemyAircraft enemy) {
        AbstractFlyingObject boss = null;

        bossHp = isHpUpgrade?1200:900;

        if (type.equals("BossEnemy")){
            boss = new BossEnemy(
                    (int) (Math.random() * (Main.WINDOW_WIDTH - ImageManager.MOB_ENEMY_IMAGE.getWidth())),
                    (int) (Math.random() * Main.WINDOW_HEIGHT * 0.2),
                    3,
                    0,
                    bossHp+addHp);
        }

        return boss;
    }

}
