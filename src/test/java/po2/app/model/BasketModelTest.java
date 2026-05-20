package app.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the BasketModel class.
 * Validates the polymorphic price calculations, the proportional bulk price
 * formula, the basket total computation, and the SDG 3 vegetable goal.
 */
public class BasketModelTest {

    /**
     * Verifies that a PackagedFood returns its base price unchanged.
     */
    @Test
    public void testPackagedPrice() {
        PackagedFood food = new PackagedFood(
                "Arroz",
                FoodGroup.YELLOW,
                2.50,
                350.0,
                "Cartao",
                ""
        );
        assertEquals(2.50, food.getPrice(), 0.001);
    }

    /**
     * Verifies that a BulkFood applies the proportional formula
     * (basePrice / 1000) * selectedWeight correctly for different weights.
     */
    @Test
    public void testBulkPriceCalculation() {
        BulkFood broccoli = new BulkFood(
                "Brocolos",
                FoodGroup.GREEN,
                2.00,
                34.0,
                500.0,
                ""
        );
        assertEquals(1.00, broccoli.getPrice(), 0.001);

        BulkFood apple = new BulkFood(
                "Maca",
                FoodGroup.GREEN,
                4.00,
                52.0,
                250.0,
                ""
        );
        assertEquals(1.00, apple.getPrice(), 0.001);
    }

    /**
     * Verifies that the basket total sums prices correctly using polymorphism,
     * mixing packaged and bulk items in the same basket.
     */
    @Test
    public void testTotalBasketCalculation() {
        BasketModel basketModel = new BasketModel();

        PackagedFood rice = new PackagedFood("Arroz", FoodGroup.YELLOW, 1.89, 350.0, "Cartao", "");
        BulkFood broccoli = new BulkFood("Brocolos", FoodGroup.GREEN, 2.00, 34.0, 500.0, "");
        PackagedFood milk = new PackagedFood("Leite", FoodGroup.BLUE, 0.79, 42.0, "Plastico", "");

        basketModel.addItem(rice);
        basketModel.addItem(broccoli);
        basketModel.addItem(milk);

        assertEquals(3.68, basketModel.getTotalPrice(), 0.001);
    }

    /**
     * Verifies the SDG 3 vegetable goal: at least 400g of GREEN items must
     * be present in the basket. Tests three scenarios — goal met, goal not
     * met, and goal met despite the presence of non-vegetable items.
     */
    @Test
    public void testSDGGoalCondition() {
        BasketModel basketModelA = new BasketModel();
        BulkFood broccoli = new BulkFood("Brocolos", FoodGroup.GREEN, 2.00, 34.0, 500.0, "");
        basketModelA.addItem(broccoli);
        assertTrue(basketModelA.hasMetVegetableGoal());

        BasketModel basketModelB = new BasketModel();
        BulkFood apple = new BulkFood("Maca", FoodGroup.GREEN, 1.50, 52.0, 200.0, "");
        basketModelB.addItem(apple);
        assertFalse(basketModelB.hasMetVegetableGoal());

        BasketModel basketModelC = new BasketModel();
        BulkFood carrots = new BulkFood("Cenoura", FoodGroup.GREEN, 0.90, 41.0, 450.0, "");
        BulkFood chicken = new BulkFood("Frango", FoodGroup.RED, 5.20, 239.0, 500.0, "");
        basketModelC.addItem(carrots);
        basketModelC.addItem(chicken);
        assertTrue(basketModelC.hasMetVegetableGoal());
    }
}