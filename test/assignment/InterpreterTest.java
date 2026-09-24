package assignment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

/* 
 * Any comments and methods here are purely descriptions or suggestions.
 * This is your test file. Feel free to change this as much as you want.
 */
class FakeCritter implements Critter {
    private String name;
    private List<String> commands;
    private int nextCodeLine;
    private int[] registers = new int[10];
    private HungerLevel hunger = HungerLevel.SATISFIED;
    private boolean randomResult = false;

    // one index per bearing: 0, 45, 90, 135, 180, 225, 270, 315
    private int[] cellContents = new int[8];
    private int[] offAngles = new int[8];

    public boolean hopped = false;
    public boolean turnedLeft = false;
    public boolean turnedRight = false;
    public boolean ate = false;
    public int infectedWith = -1;

    public FakeCritter(String name, ArrayList<String> commands) {
        this.name = name;
        this.commands = commands;
    }

    public List<String> getCode() {
        return commands;
    }

    public int getNextCodeLine() {
        return nextCodeLine;
    }

    public void setNextCodeLine(int line) {
        nextCodeLine = line;
    }

    public int getReg(int r) {
        return registers[r];
    }

    public void setReg(int r, int v) {
        registers[r] = v;
    }

    public HungerLevel getHungerLevel() {
        return hunger;
    }

    public void hop() {
        hopped = true;
    }

    public void left() {
        turnedLeft = true;
    }

    public void right() {
        turnedRight = true;
    }

    public void eat() {
        ate = true;
    }

    public void infect() {
        infectedWith = 0;
    }

    public void infect(int n) {
        infectedWith = n;
    }

    public int getCellContent(int bearing) {
        return cellContents[bearing / 45]; // bearing / 45 produces the index number of each bearing
    }

    public int getOffAngle(int bearing) {
        return offAngles[bearing / 45];
    }

    public void setHunger(HungerLevel hungerLevel) {
        hunger = hungerLevel;
    }

    public void setCellContent(int bearing, int value) {
        cellContents[bearing / 45] = value;
    }

    public void setOffAngle(int bearing, int angle) {
        offAngles[bearing / 45] = angle;
    }

    public boolean ifRandom() {
        return randomResult;
    }

    public void setRandomResult(boolean result) {
        randomResult = result;
    }
}


public class InterpreterTest {

    // This will run ONCE before all other tests. It can be useful to setup up
    // global variables and anything needed for all of the tests.


    private Interpreter interpreter;

    @BeforeAll
    static void setupAll() {

    }

    // This will run before EACH test.
    @BeforeEach
    void setupEach() {
        interpreter = new Interpreter();
    }

    @Test
    void testHop() {
        ArrayList<String> code = new ArrayList<>();
        code.add("hop");
        FakeCritter c = new FakeCritter("Test", code);
        interpreter.executeCritter(c);
        assertTrue(c.hopped);
        assertEquals(1, c.getNextCodeLine());
    }

    @Test
    void testRight() {
        ArrayList<String> code = new ArrayList<>();
        code.add("right");
        FakeCritter c = new FakeCritter("Test", code);
        interpreter.executeCritter(c);
        assertTrue(c.turnedRight);
        assertEquals(1, c.getNextCodeLine());
    }

    @Test
    void testLeft() {
        ArrayList<String> code = new ArrayList<>();
        code.add("left");
        FakeCritter c = new FakeCritter("Test", code);
        interpreter.executeCritter(c);
        assertTrue(c.turnedLeft);
        assertEquals(1, c.getNextCodeLine());
    }

    @Test
    void testEat() {
        ArrayList<String> code = new ArrayList<>();
        code.add("eat");
        FakeCritter c = new FakeCritter("Test", code);
        interpreter.executeCritter(c);
        assertTrue(c.ate);
        assertEquals(1, c.getNextCodeLine());
    }

    @Test
    void testInfectNoArg() {
        ArrayList<String> code = new ArrayList<>();
        code.add("infect");
        FakeCritter c = new FakeCritter("Test", code);
        interpreter.executeCritter(c);
        assertEquals(0, c.infectedWith);
    }

    @Test
	void testInfectWithArg() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("infect 3", "left", "right", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		interpreter.executeCritter(c);
		assertEquals(2, c.infectedWith);
	}

	@Test
	void testGoAbsolute() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("go 4", "left", "right", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
		assertFalse(c.turnedLeft);
		assertFalse(c.turnedRight);
	}

	@Test
	void testGoRelativePlus() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("go +2", "left", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
		assertFalse(c.turnedLeft);
	}

	@Test
	void testGoRelativeMinus() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("right", "left", "go -2"));
		FakeCritter c = new FakeCritter("Test", code);
		c.setNextCodeLine(2);
		interpreter.executeCritter(c);
		assertTrue(c.turnedRight);
        assertFalse(c.turnedLeft);
	}

	@Test
	void testIfRandomTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifrandom 1", "left", "right"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setRandomResult(true);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfRandomFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifrandom 1", "left", "right"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setRandomResult(false);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testIfHungryTrueWhenHungry() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifhungry 1", "left", "right"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setHunger(Critter.HungerLevel.HUNGRY);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfHungryTrueWhenStarving() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifhungry 1", "left", "right"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setHunger(Critter.HungerLevel.STARVING);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfHungryFalseWhenSatisfied() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifhungry 1", "left", "right"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setHunger(Critter.HungerLevel.SATISFIED);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testIfStarvingTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifstarving 1", "left", "right"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setHunger(Critter.HungerLevel.STARVING);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfStarvingFalseWhenOnlyHungry() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifstarving 1", "left", "right"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setHunger(Critter.HungerLevel.HUNGRY);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testIfEmptyTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifempty 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
		c.setCellContent(0, Critter.EMPTY);
        c.setNextCodeLine(1);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfEmptyFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifempty 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
		c.setCellContent(0, Critter.WALL);
        c.setNextCodeLine(1);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}


	@Test
	void testIfAllyTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifally 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setCellContent(0, Critter.ALLY);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfAllyFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifally 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setCellContent(0, Critter.ENEMY);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testIfEnemyTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifenemy 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setCellContent(0, Critter.ENEMY);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfEnemyFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifenemy 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setCellContent(0, Critter.ALLY);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testIfWallTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifwall 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setCellContent(0, Critter.WALL);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfWallFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifwall 0 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setCellContent(0, Critter.EMPTY);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	// ---------- ifangle ----------

	@Test
	void testIfAngleTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifangle 0 90 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setOffAngle(0, 90);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfAngleFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifangle 0 90 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setOffAngle(0, 45);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testWrite() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("write 3 42", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		interpreter.executeCritter(c);
		assertEquals(42, c.getReg(3));
		assertTrue(c.hopped);
	}

	@Test
	void testAdd() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("add 1 2", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		c.setReg(1, 5);
		c.setReg(2, 7);
		interpreter.executeCritter(c);
		assertEquals(12, c.getReg(1));
        assertTrue(c.hopped);
	}

	@Test
	void testSub() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("sub 1 2", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		c.setReg(1, 10);
		c.setReg(2, 4);
		interpreter.executeCritter(c);
		assertEquals(6, c.getReg(1));
        assertTrue(c.hopped);
	}

	@Test
	void testInc() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("inc 1", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		c.setReg(1, 5);
		interpreter.executeCritter(c);
		assertEquals(6, c.getReg(1));
        assertTrue(c.hopped);
	}

	@Test
	void testDec() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("dec 1", "hop"));
		FakeCritter c = new FakeCritter("Test", code);
		c.setReg(1, 5);
		interpreter.executeCritter(c);
		assertEquals(4, c.getReg(1));
        assertTrue(c.hopped);
	}

	@Test
	void testIfLtTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "iflt 1 2 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setReg(1, 3);
		c.setReg(2, 5);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfLtFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "iflt 1 2 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setReg(1, 5);
		c.setReg(2, 3);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testIfGtTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifgt 1 2 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setReg(1, 5);
		c.setReg(2, 3);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfGtFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifgt 1 2 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setReg(1, 3);
		c.setReg(2, 5);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

	@Test
	void testIfEqTrue() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifeq 1 2 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setReg(1, 5);
		c.setReg(2, 5);
		interpreter.executeCritter(c);
		assertTrue(c.hopped);
	}

	@Test
	void testIfEqFalse() {
		ArrayList<String> code = new ArrayList<>(Arrays.asList("hop", "ifeq 1 2 1"));
		FakeCritter c = new FakeCritter("Test", code);
        c.setNextCodeLine(1);
		c.setReg(1, 5);
		c.setReg(2, 3);
		interpreter.executeCritter(c);
		assertFalse(c.hopped);
	}

    // You can test execute critter here. You may want to make additional tests and
    // your own testing harness. See spec section 2.5 for more details.
    @Test
    void testExecuteCritter() throws IOException {
        CritterSpecies test = interpreter.loadSpecies("species/test.cri");
        ArrayList<String> code = (ArrayList<String>) test.getCode();
        FakeCritter c = new FakeCritter(test.getName(), code);
    
        interpreter.executeCritter(c);
        assertTrue(c.hopped);
        assertEquals(1, c.getNextCodeLine());
    
        c.hopped = false;
        interpreter.executeCritter(c);
        assertTrue(c.turnedLeft);
        assertFalse(c.hopped);
        assertEquals(2, c.getNextCodeLine());

        c.turnedLeft = false;
        interpreter.executeCritter(c);
        assertTrue(c.hopped);
        assertEquals(3, c.getNextCodeLine());
    
        c.hopped = false;
        interpreter.executeCritter(c);
        assertTrue(c.hopped);
        assertEquals(1, c.getNextCodeLine());
    }

    // Test load species. You may want to make more tests for different cases here.
    @Test
    void testLoadSpecies() throws IOException {
        CritterSpecies test = interpreter.loadSpecies("species/test.cri");
        ArrayList<String> actual_code = (ArrayList<String>) test.getCode();
        ArrayList<String> expected_code = new ArrayList<>();
        expected_code.add("hop");
        expected_code.add("left");
        expected_code.add("hop");
        expected_code.add("go 1");
        assertEquals(expected_code, actual_code);
        assertEquals("Test_Creature", test.getName());
    }

}
