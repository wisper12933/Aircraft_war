package edu.hitsz.aircraft;

import edu.hitsz.bullet.AbstractBullet;
import edu.hitsz.bullet.HeroBullet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeroAircraftTest {
    HeroAircraft heroAircraft;

    @BeforeEach
    void setUp(){
        System.out.println("**--- Create HeroAircraft ---**");
        heroAircraft = HeroAircraft.getInstance();
    }

    @AfterEach
    void tearDown(){
        System.out.println("**--- Teardown HeroAircraft ---**\n");
        heroAircraft = null;
    }

    @Test
    @DisplayName("Test decreaseHp method")
    void decreaseHp(){
        System.out.println("**--- Test decreaseHp method executed  ---**");

        float Hp = heroAircraft.getHp();
        int decrease = 60;
        heroAircraft.decreaseHp(decrease);
        assertEquals(Hp - decrease,heroAircraft.getHp());
        heroAircraft.decreaseHp(100);
        assertEquals(0,heroAircraft.getHp());
    }

    @Test
    @DisplayName("Test increaseHp method")
    void increaseHp(){
        System.out.println("**--- Test increaseHp method executed  ---**");

        float maxHp = heroAircraft.getMaxHp();
        heroAircraft.setHp(60);
        heroAircraft.increaseHp(30);
        assertEquals(90,heroAircraft.getHp());

        heroAircraft.increaseHp(1000);
        assertEquals(maxHp,heroAircraft.getHp());
    }

    @Test
    @DisplayName("Test shoot method")
    void shoot() {
        System.out.println("**--- Test shoot method executed  ---**");

        List<AbstractBullet> heroBullets = heroAircraft.shoot();
        assertNotNull(heroBullets);
        /*逐个检查类型*/
        for (AbstractBullet heroBullet : heroBullets){
            assertTrue(heroBullet instanceof HeroBullet);
        }
    }
}