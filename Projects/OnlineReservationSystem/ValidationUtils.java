import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Small static helpers for validating user input across the forms.
 */
public final class ValidationUtils {

    // Note: uuuu (proleptic year), not yyyy (year-of-era), is required here -
    // STRICT resolver style cannot resolve yyyy without an explicit era.
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd-MM-uuuu")
            .withResolverStyle(ResolverStyle.STRICT);

    private ValidationUtils() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isNumeric(String value) {
        if (isBlank(value)) {
            return false;
        }
        return value.trim().matches("\\d+");
    }

    /** Validates strict dd-MM-yyyy format (rejects things like 31-02-2026). */
    public static boolean isValidDate(String value) {
        if (isBlank(value)) {
            return false;
        }
        try {
            LocalDate.parse(value.trim(), DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
