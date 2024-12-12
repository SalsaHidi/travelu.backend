package travelu.travelu_backend.model;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import org.springframework.web.servlet.HandlerMapping;
import travelu.travelu_backend.service.PembayaranService;


/**
 * Validate that the noInvoice value isn't taken yet.
 */
@Target({ FIELD, METHOD, ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = PembayaranNoInvoiceUnique.PembayaranNoInvoiceUniqueValidator.class
)
public @interface PembayaranNoInvoiceUnique {

    String message() default "{Exists.pembayaran.noInvoice}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class PembayaranNoInvoiceUniqueValidator implements ConstraintValidator<PembayaranNoInvoiceUnique, String> {

        private final PembayaranService pembayaranService;
        private final HttpServletRequest request;

        public PembayaranNoInvoiceUniqueValidator(final PembayaranService pembayaranService,
                final HttpServletRequest request) {
            this.pembayaranService = pembayaranService;
            this.request = request;
        }

        @Override
        public boolean isValid(final String value, final ConstraintValidatorContext cvContext) {
            if (value == null) {
                // no value present
                return true;
            }
            @SuppressWarnings("unchecked") final Map<String, String> pathVariables =
                    ((Map<String, String>)request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE));
            final String currentId = pathVariables.get("id");
            if (currentId != null && value.equalsIgnoreCase(pembayaranService.get(Long.parseLong(currentId)).getNoInvoice())) {
                // value hasn't changed
                return true;
            }
            return !pembayaranService.noInvoiceExists(value);
        }

    }

}