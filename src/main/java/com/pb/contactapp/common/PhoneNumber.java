package com.pb.contactapp.common;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank(message ="Phone number can't be empty")
@NotNull
@Pattern(regexp = "^\\+?\\d*$", message = "phone number should contains only numbers and \"+\" sign")
@Size(min = 9, max = 15)
@ReportAsSingleViolation
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = { })
public @interface PhoneNumber {
    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
