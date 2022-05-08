package edu.hitsz.supply;

import edu.hitsz.aircraft.MobEnemy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HpSupplyTest {

    HpSupply hpSupply;
    //生成飞机来测试碰撞
    //可碰撞
    MobEnemy mobEnemy1 = new MobEnemy(51,
            51,
            0,
            0,
            10);
    //不可碰撞
    MobEnemy mobEnemy2 = new MobEnemy(500,
            500,
            0,
            0,
            10);

    @BeforeEach
    void setUp(){
        System.out.println("**--- Create HpSupply ---**");
        hpSupply = new HpSupply( 50,
                50,
                0,
                3);
    }

    @AfterEach
    void tearDown(){
        System.out.println("**--- Teardown HpSupply ---**\n");
        hpSupply = null;
    }

    @Test
    @DisplayName("Test getHealth method")
    void getHealth() {
        System.out.println("**--- Test getHealth method executed  ---**");

        assertEquals(20,hpSupply.getHealth());
    }

    @Test
    @DisplayName("Test crash method")
    void crash() {
        System.out.println("**--- Test crash method executed  ---**");

        assertTrue(hpSupply.crash(mobEnemy1));
        assertFalse(hpSupply.crash(mobEnemy2));
    }
}