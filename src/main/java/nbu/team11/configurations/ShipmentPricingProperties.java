package nbu.team11.configurations;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shipment.price")
public class ShipmentPricingProperties {

    private double baseToOffice;
    private double baseToAddress;
    private double perKilo;

    public double getBaseToOffice() {
        return baseToOffice;
    }

    public void setBaseToOffice(double baseToOffice) {
        this.baseToOffice = baseToOffice;
    }

    public double getBaseToAddress() {
        return baseToAddress;
    }

    public void setBaseToAddress(double baseToAddress) {
        this.baseToAddress = baseToAddress;
    }

    public double getPerKilo() {
        return perKilo;
    }

    public void setPerKilo(double perKilo) {
        this.perKilo = perKilo;
    }
}
