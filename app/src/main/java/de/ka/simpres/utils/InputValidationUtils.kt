package de.ka.simpres.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import de.ka.simpres.R

/**
 * Lists different validation rules.
 */
enum class ValidationRules(
    val variable: Int? = null, val predicate: String.() -> Boolean, @StringRes val errorTextResId: Int
) {
    NOT_EMPTY(predicate = { isBlank() }, errorTextResId = R.string.error_input_empty),
    MIN_4(variable = 4, predicate = { length < 4 }, errorTextResId = R.string.error_input_too_small),
    MIN_8(variable = 8, predicate = { length < 8 }, errorTextResId = R.string.error_input_too_small),
    MAX_8(variable = 8, predicate = { length > 8 }, errorTextResId = R.string.error_input_too_long),
}

/**
 * A validator utility class for string inputs. This validator can be used by injecting and creating instances of
 * validators with [Validator].
 *
 * **Features:** Adding a pre-defined error mutable live data to auto set and reset a error message after
 * validating with [Validator.isValid].
 *
 * Each validator can have several error outputs and each output can have several validation rules.
 *
 * Prioritize rules by adding the validation rule to the end of the rule list, which you want to show when at least
 * two rules apply for the same validation fail.
 *
 * Example usage:
 * ```
 * private val validator = inputValidator.Validator(InputValidator.ValidatorInput(titleError, listOf(ValidationRules.NOT_EMPTY, ValidationRules.MIN_4)))
 * // where titleError is a MutableLiveData String representation and the list contains validation rules
 * titleValidator.isValid(input)
 * // where input is a string. This auto sets error messages in titleError if one of the two rulses are not valid for
 * // the given input
 * ```
 */
class InputValidator(val context: Context) {

    /**
     * A input class for validating with a error output. Should at least have one [rules] to have any effect.
     */
    data class ValidatorInput(val errorOutPut: MutableLiveData<String?>, val rules: List<ValidationRules>)

    /**
     * A simple input validator, offering a [isValid] method for quick error setting and retrieving the validation state
     * of inputs.
     */
    inner class Validator(private vararg val validationPairs: ValidatorInput) {

        /**
         * Checks whether all validations are true or at least one is failing.
         *
         * @return true if all fields defined in the [validationPairs] are valid, false if at least one is failing
         */
        fun isValid(input: String?): Boolean {
            if (input == null) return false

            var isValid = true

            validationPairs.forEach pairs@{ pair ->
                pair.rules.forEach { rule ->
                    if (rule.predicate(input)) {
                        isValid = false
                        if (rule.variable != null) {
                            pair.errorOutPut.postValue(
                                String.format(
                                    context.getString(rule.errorTextResId),
                                    rule.variable
                                )
                            )
                        } else {
                            pair.errorOutPut.postValue(context.getString(rule.errorTextResId))
                        }
                        return@pairs
                    }
                }
            }
            return isValid
        }

    }

}