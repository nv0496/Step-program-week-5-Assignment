import java.time.LocalDateTime;
import java.util.*;

// ==============================
// a) Immutable Product class
// ==============================
final class Product {
    private final String productId;
    private final String name;
    private final String category;
    private final String manufacturer;
    private final double basePrice;
    private final double weight;
    private final String[] features;
    private final Map<String, String> specifications;

    private Product(String productId, String name, String category, String manufacturer,
                    double basePrice, double weight, String[] features,
                    Map<String, String> specifications) {
        if (productId == null || name == null || category == null || manufacturer == null)
            throw new IllegalArgumentException("Product fields cannot be null");
        if (basePrice < 0 || weight < 0) throw new IllegalArgumentException("Invalid price/weight");

        this.productId = productId;
        this.name = name;
        this.category = category;
        this.manufacturer = manufacturer;
        this.basePrice = basePrice;
        this.weight = weight;
        this.features = features != null ? features.clone() : new String[0];
        this.specifications = specifications != null ? new HashMap<>(specifications) : new HashMap<>();
    }

    // Factory methods
    public static Product createElectronics(String id, String name, double price, double weight) {
        return new Product(id, name, "Electronics", "Generic Electronics", price, weight,
                new String[]{"Warranty", "User Manual"}, Map.of("Voltage", "220V"));
    }

    public static Product createClothing(String id, String name, double price, double weight) {
        return new Product(id, name, "Clothing", "Generic Clothing", price, weight,
                new String[]{"Washable", "Comfort Fit"}, Map.of("Material", "Cotton"));
    }

    public static Product createBooks(String id, String name, double price, double weight) {
        return new Product(id, name, "Books", "Generic Publisher", price, weight,
                new String[]{"Paperback", "English"}, Map.of("Pages", "300"));
    }

    // Getters with defensive copying
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getManufacturer() { return manufacturer; }
    public double getBasePrice() { return basePrice; }
    public double getWeight() { return weight; }
    public String[] getFeatures() { return features.clone(); }
    public Map<String, String> getSpecifications() { return new HashMap<>(specifications); }

    // Final method for consistency
    public final double calculateTax(String region) {
        switch (region.toUpperCase()) {
            case "US": return basePrice * 0.07;
            case "EU": return basePrice * 0.20;
            case "IN": return basePrice * 0.18;
            default: return basePrice * 0.10;
        }
    }

    @Override
    public String toString() {
        return "Product{" + name + ", category=" + category + ", price=" + basePrice + "}";
    }
    @Override
    public int hashCode() { return Objects.hash(productId); }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        return productId.equals(((Product)o).productId);
    }
}

// ==============================
// b) Customer class
// ==============================
class Customer {
    private final String customerId;
    private final String email;
    private String name;
    private String phoneNumber;
    private String preferredLanguage;
    private final String accountCreationDate;

    public Customer(String customerId, String email, String name) {
        if (customerId == null || email == null) throw new IllegalArgumentException("Required fields missing");
        this.customerId = customerId;
        this.email = email;
        this.name = name;
        this.accountCreationDate = LocalDateTime.now().toString();
    }

    // Getters and setters
    public String getCustomerId() { return customerId; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public void setName(String name) { if (name != null) this.name = name; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    public String getAccountCreationDate() { return accountCreationDate; }

    // Package-private method
    String getCreditRating() {
        return "Good"; // Mock rating
    }

    // Public profile
    public String getPublicProfile() {
        return "Customer: " + name + ", Language: " + preferredLanguage;
    }

    @Override
    public String toString() { return "Customer{" + customerId + ", name=" + name + "}"; }
}

// ==============================
// c) ShoppingCart class
// ==============================
class ShoppingCart {
    private final String cartId;
    private final String customerId;
    private final List<Object> items = new ArrayList<>();
    private double totalAmount;
    private int itemCount;

    public ShoppingCart(String cartId, String customerId) {
        this.cartId = cartId;
        this.customerId = customerId;
    }

    public boolean addItem(Object product, int quantity) {
        if (!(product instanceof Product) || quantity <= 0) return false;
        Product p = (Product) product;
        for (int i = 0; i < quantity; i++) items.add(p);
        itemCount += quantity;
        totalAmount += p.getBasePrice() * quantity - calculateDiscount();
        return true;
    }

    private double calculateDiscount() {
        if (itemCount >= 5) return 20.0;
        return 0.0;
    }

    // Package-private
    String getCartSummary() {
        return "Cart{" + cartId + ", items=" + itemCount + ", total=" + totalAmount + "}";
    }
}

// ==============================
// d) Order Processing Classes
// ==============================
class Order {
    private final String orderId;
    private final LocalDateTime orderTime;

    public Order(String orderId) {
        this.orderId = orderId;
        this.orderTime = LocalDateTime.now();
    }

    public String getOrderId() { return orderId; }
    public LocalDateTime getOrderTime() { return orderTime; }
}

class PaymentProcessor {
    private final String processorId;
    private final String securityKey;

    public PaymentProcessor(String processorId, String securityKey) {
        this.processorId = processorId;
        this.securityKey = securityKey;
    }

    public boolean processPayment(double amount) {
        return amount > 0; // Mock payment success
    }
}

class ShippingCalculator {
    private final Map<String, Double> shippingRates;

    public ShippingCalculator(Map<String, Double> rates) {
        this.shippingRates = new HashMap<>(rates);
    }

    public double calculateShipping(String region, double weight) {
        return shippingRates.getOrDefault(region, 10.0) + weight * 0.5;
    }
}

// ==============================
// e) Final ECommerceSystem
// ==============================
final class ECommerceSystem {
    private static final Map<String, Object> productCatalog = new HashMap<>();

    public static boolean processOrder(Object order, Object customer) {
        return order instanceof Order && customer instanceof Customer;
    }

    public static void addProductToCatalog(String id, Object product) {
        productCatalog.put(id, product);
    }

    public static Object getProductFromCatalog(String id) {
        return productCatalog.get(id);
    }
}

// ==============================
// Main Test
// ==============================
public class ECommerceDemo {
    public static void main(String[] args) {
        // Create products
        Product laptop = Product.createElectronics("P1", "Laptop", 800, 2.5);
        Product tshirt = Product.createClothing("P2", "T-Shirt", 20, 0.3);

        // Customer
        Customer c1 = new Customer("C1", "user@email.com", "Alice");
        c1.setPreferredLanguage("English");

        // Shopping cart
        ShoppingCart cart = new ShoppingCart("Cart1", c1.getCustomerId());
        cart.addItem(laptop, 1);
        cart.addItem(tshirt, 3);
        System.out.println(cart.getCartSummary());

        // Order
        Order order = new Order("O1");
        PaymentProcessor pp = new PaymentProcessor("Pay1", "SEC123");
        System.out.println("Payment success: " + pp.processPayment(860));

        // Shipping
        ShippingCalculator sc = new ShippingCalculator(Map.of("US", 15.0, "IN", 10.0));
        System.out.println("Shipping cost: " + sc.calculateShipping("IN", laptop.getWeight()));

        // ECommerceSystem
        ECommerceSystem.addProductToCatalog("P1", laptop);
        System.out.println("Catalog Product: " + ECommerceSystem.getProductFromCatalog("P1"));
    }
}
