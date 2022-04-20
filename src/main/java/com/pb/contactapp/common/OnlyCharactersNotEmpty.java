package com.pb.contactapp.common;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@NotBlank(message = "Please provide value")
@NotNull
@Pattern(regexp = "^[a-zA-ZżŻźŹćĆńŃąĄśŚłŁęĘóÓ]*$", message = "Field should contains only characters")
@ReportAsSingleViolation
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = { })
public @interface OnlyCharactersNotEmpty {
    String message() default "";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
