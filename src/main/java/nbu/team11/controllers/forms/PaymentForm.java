package nbu.team11.controllers.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentForm {
    private String cardHolderName;
    private String cardNumber;
    private String expiryDate; // MM/YY
    private String cvv;
}
