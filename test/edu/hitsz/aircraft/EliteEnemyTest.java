package edu.hitsz.aircraft;

import edu.hitsz.application.ImageManager;
import edu.hitsz.application.Main;
import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.bullet.EnemyBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.supply.AbstractSupply;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EliteEnemyTest {

    EliteEnemy eliteEnemy;
    //生成子弹用来测试是否发生碰撞
    //可碰撞
    HeroBullet heroBullet1 = new HeroBullet(50,
            50,
            0,
            0,
            30);
    //不可碰撞
    HeroBullet heroBullet2 = new HeroBullet(2000,
            2000,
            0,
            0,
            30);

    @BeforeEach
    void setUp(){
        System.out.println("**--- Create EliteAircraft ---**");
        eliteEnemy = new EliteEnemy( 50,
                50,
                0,
                3,
                90);
    }

    @AfterEach
    void tearDown(){
        System.out.println("**--- Teardown EliteAircraft ---**\n");
        eliteEnemy = null;
    }

    @Test
    @DisplayName("Test shoot method")
    void shoot() {
        System.out.println("**--- Test shoot method executed  ---**");

        List<AbstractBullet> eliteBullets = eliteEnemy.shoot();
        assertNotNull(eliteBullets);
        /**逐个检查类型*/
        for (AbstractBullet eliteBullet:eliteBullets){
            assertTrue(eliteBullet instanceof EnemyBullet);
        }
    }

    @Test
    @DisplayName("Test bonus method")
    void bonus() {
        //有前提：精英机被摧毁
        System.out.println("**--- Test bonus method executed  ---**");

        eliteEnemy.vanish();
        Assumptions.assumeTrue(eliteEnemy.notValid());
        AbstractSupply bonus = eliteEnemy.bonus();

        assertNotNull(bonus);
        /**检查生成的道具是否继承了精英机的速度*/
        assertEquals(bonus.getSpeedY(),eliteEnemy.getSpeedY());
    }

}