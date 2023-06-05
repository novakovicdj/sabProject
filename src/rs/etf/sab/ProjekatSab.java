/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package rs.etf.sab;

import java.math.BigDecimal;
import java.util.List;
//import rs.etf.sab.operations.ArticleOperations;
//import rs.etf.sab.operations.BuyerOperations;
//import rs.etf.sab.operations.CityOperations;
//import rs.etf.sab.operations.GeneralOperations;
//import rs.etf.sab.operations.OrderOperations;
//import rs.etf.sab.operations.ShopOperations;

import rs.etf.sab.operations.*;
import org.junit.Test;
import rs.etf.sab.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

import java.util.Calendar;
import org.junit.Assert;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

/**
 *
 * @author novak
 */
public class ProjekatSab {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

//        GeneralOperations general = new GeneralImpl();
//        ArticleOperations article = new ArticleImpl();
//        BuyerOperations buyer = new BuyerImpl();
//        CityOperations city = new CityImpl();
//        OrderOperations order = new OrderImpl(general);
//        ShopOperations shop = new ShopImpl();
//        
//        general.eraseAll();
//        Calendar cal = Calendar.getInstance();
//        cal.clear();
//        cal.set(2018, 0, 1);
//        general.setInitialTime(cal);
//        
//        city.createCity("Beograd");
//        city.createCity("Novi Sad");
//        city.createCity("Sabac");
//        
//        city.connectCities(1, 2, 8);
//        city.connectCities(1, 3, 6);
//        city.connectCities(3, 2, 3);
//        
//        shop.createShop("Shop 1", "Sabac");
//        shop.createShop("Shop 2", "Novi Sad");
//        
//        buyer.createBuyer("Marko", 1);
//        buyer.createBuyer("Ana", 2);
//        
//        article.createArticle(1, "Jabuka", 50);
//        article.createArticle(2, "Banana", 70);
//        article.createArticle(2, "Limun", 65);
//        
//        buyer.createOrder(1);
//        
//        shop.increaseArticleCount(1, 5);
//        shop.increaseArticleCount(3, 3);
//
//        order.addArticle(1, 1, 2);
//        order.addArticle(1, 3, 1);
//        
//        shop.setDiscount(1, 10);
//        
//        order.completeOrder(1);


        ArticleOperations articleOperations = new ArticleImpl(); // Change this for your implementation (points will be negative if interfaces are not implemented).
        BuyerOperations buyerOperations = new BuyerImpl();
        CityOperations cityOperations = new CityImpl();
        GeneralOperations generalOperations = new GeneralImpl();
        OrderOperations orderOperations = new OrderImpl(generalOperations);
        ShopOperations shopOperations = new ShopImpl();
        TransactionOperations transactionOperations = new TransactionImpl();
        
        TestHandler.createInstance(
                articleOperations,
                buyerOperations,
                cityOperations,
                generalOperations,
                orderOperations,
                shopOperations,
                transactionOperations
        );

        TestRunner.runTests();

//        generalOperations.eraseAll();
//
//        final Calendar initialTime = Calendar.getInstance();
//        initialTime.clear();
//        initialTime.set(2018, 0, 1);
//        generalOperations.setInitialTime(initialTime);
//        final Calendar receivedTime = Calendar.getInstance();
//        receivedTime.clear();
//        receivedTime.set(2018, 0, 22);
//        final int cityB = cityOperations.createCity("B");
//        final int cityC1 = cityOperations.createCity("C1");
//        final int cityA = cityOperations.createCity("A");
//        final int cityC2 = cityOperations.createCity("C2");
//        final int cityC3 = cityOperations.createCity("C3");
//        final int cityC4 = cityOperations.createCity("C4");
//        final int cityC5 = cityOperations.createCity("C5");
//        cityOperations.connectCities(cityB, cityC1, 8);
//        cityOperations.connectCities(cityC1, cityA, 10);
//        cityOperations.connectCities(cityA, cityC2, 3);
//        cityOperations.connectCities(cityC2, cityC3, 2);
//        cityOperations.connectCities(cityC3, cityC4, 1);
//        cityOperations.connectCities(cityC4, cityA, 3);
//        cityOperations.connectCities(cityA, cityC5, 15);
//        cityOperations.connectCities(cityC5, cityB, 2);
//        final int shopA = shopOperations.createShop("shopA", "A");
//        final int shopC2 = shopOperations.createShop("shopC2", "C2");
//        final int shopC3 = shopOperations.createShop("shopC3", "C3");
//        shopOperations.setDiscount(shopA, 20);
//        shopOperations.setDiscount(shopC2, 50);
//        final int laptop = articleOperations.createArticle(shopA, "laptop", 1000);
//        final int monitor = articleOperations.createArticle(shopC2, "monitor", 200);
//        final int stolica = articleOperations.createArticle(shopC3, "stolica", 100);
//        final int sto = articleOperations.createArticle(shopC3, "sto", 200);
//        shopOperations.increaseArticleCount(laptop, 10);
//        shopOperations.increaseArticleCount(monitor, 10);
//        shopOperations.increaseArticleCount(stolica, 10);
//        shopOperations.increaseArticleCount(sto, 10);
//        final int buyer = buyerOperations.createBuyer("kupac", cityB);
//        buyerOperations.increaseCredit(buyer, new BigDecimal("20000"));
//        final int order = buyerOperations.createOrder(buyer);
//        orderOperations.addArticle(order, laptop, 5);
//        orderOperations.addArticle(order, monitor, 4);
//        orderOperations.addArticle(order, stolica, 10);
//        orderOperations.addArticle(order, sto, 4);
//        Assert.assertNull((Object)orderOperations.getSentTime(order));
//        Assert.assertTrue("created".equals(orderOperations.getState(order)));
//        orderOperations.completeOrder(order);
//        Assert.assertTrue("sent".equals(orderOperations.getState(order)));
//        final int buyerTransactionId = transactionOperations.getTransationsForBuyer(buyer).get(0);
//        Assert.assertEquals((Object)initialTime, (Object)transactionOperations.getTimeOfExecution(buyerTransactionId));
//        Assert.assertNull((Object)transactionOperations.getTransationsForShop(shopA));
//        final BigDecimal shopAAmount = new BigDecimal("5").multiply(new BigDecimal("1000")).setScale(3); // 5000
//        final BigDecimal shopAAmountWithDiscount = new BigDecimal("0.8").multiply(shopAAmount).setScale(3); // 4000
//        final BigDecimal shopC2Amount = new BigDecimal("4").multiply(new BigDecimal("200")).setScale(3); // 800
//        final BigDecimal shopC2AmountWithDiscount = new BigDecimal("0.5").multiply(shopC2Amount).setScale(3); // 400
//        final BigDecimal shopC3AmountWithDiscount;
//        final BigDecimal shopC3Amount = shopC3AmountWithDiscount = new BigDecimal("10").multiply(new BigDecimal("100")).add(new BigDecimal("4").multiply(new BigDecimal("200"))).setScale(3); //1800
//        final BigDecimal amountWithoutDiscounts = shopAAmount.add(shopC2Amount).add(shopC3Amount).setScale(3); // 7600
//        final BigDecimal amountWithDiscounts = shopAAmountWithDiscount.add(shopC2AmountWithDiscount).add(shopC3AmountWithDiscount).setScale(3); // 6200
//        final BigDecimal systemProfit = amountWithDiscounts.multiply(new BigDecimal("0.05")).setScale(3); // 310
//        final BigDecimal shopAAmountReal = shopAAmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3); // 3800
//        final BigDecimal shopC2AmountReal = shopC2AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3); // 380
//        final BigDecimal shopC3AmountReal = shopC3AmountWithDiscount.multiply(new BigDecimal("0.95")).setScale(3); // 1710
//        Assert.assertEquals((Object)amountWithDiscounts, (Object)orderOperations.getFinalPrice(order));
//        Assert.assertEquals((Object)amountWithoutDiscounts.subtract(amountWithDiscounts), (Object)orderOperations.getDiscountSum(order));
//        Assert.assertEquals((Object)amountWithDiscounts, (Object)transactionOperations.getBuyerTransactionsAmmount(buyer));
//        Assert.assertEquals((Object)transactionOperations.getShopTransactionsAmmount(shopA), (Object)new BigDecimal("0").setScale(3));
//        Assert.assertEquals((Object)transactionOperations.getShopTransactionsAmmount(shopC2), (Object)new BigDecimal("0").setScale(3));
//        Assert.assertEquals((Object)transactionOperations.getShopTransactionsAmmount(shopC3), (Object)new BigDecimal("0").setScale(3));
//        Assert.assertEquals((Object)new BigDecimal("0").setScale(3), (Object)transactionOperations.getSystemProfit());
//        generalOperations.time(2);
//        System.out.println(initialTime.get(Calendar.DAY_OF_YEAR));
//        Assert.assertEquals((Object)initialTime, (Object)orderOperations.getSentTime(order));
//        Assert.assertNull((Object)orderOperations.getRecievedTime(order));
//        Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityA);
//        generalOperations.time(9);
//        Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityA);
//        generalOperations.time(8);
//        Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityC5);
//        generalOperations.time(5);
//        Assert.assertEquals((long)orderOperations.getLocation(order), (long)cityB);
//        Assert.assertEquals((Object)receivedTime, (Object)orderOperations.getRecievedTime(order));
//        Assert.assertEquals((Object)shopAAmountReal, (Object)transactionOperations.getShopTransactionsAmmount(shopA));
//        Assert.assertEquals((Object)shopC2AmountReal, (Object)transactionOperations.getShopTransactionsAmmount(shopC2));
//        Assert.assertEquals((Object)shopC3AmountReal, (Object)transactionOperations.getShopTransactionsAmmount(shopC3));
//        Assert.assertEquals((Object)systemProfit, (Object)transactionOperations.getSystemProfit());
//        final int shopATransactionId = transactionOperations.getTransactionForShopAndOrder(order, shopA);
//        Assert.assertNotEquals(-1L, (long)shopATransactionId);
//        Assert.assertEquals((Object)receivedTime, (Object)transactionOperations.getTimeOfExecution(shopATransactionId));
        
        
    }
        
    

}
