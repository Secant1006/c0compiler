package com.secant.c0compiler.errorhandling;

public enum ErrorCode {
    FILE_READ_ERROR("An error occurred when reading file."),
    INTEGER_OVERFLOW("The integer is too big."),
    INCOMPLETE_NOT_EQUAL("The 'not equal' operator is incomplete."),
    UNKNOWN_CHARACTER("The character is unknown, or currently not supported."),
    TOKEN_BUFFER_OVERFLOW("Cannot unread multiple tokens."),
    FILE_WRITE_ERROR("An error occurred when writing file."),
    NO_TYPE_SPECIFIER("No type specifier."),
    NO_IDENTIFIER("No identifier."),
    INVALID_CONSTANT_DECLARATION("Variable cannot be declared without assignment."),
    NO_SEMICOLON("No semicolon."),
    INVALID_PARAMETER("The parameter is invalid."),
    VARIABLE_DECLARATION_AFTER_FUNCTION_DEFINITION("Variable declaration can't be after function definition."),
    NO_LEFT_BRACE("No left brace."),
    NO_RIGHT_BRACE("No right brace."),
    INVALID_STATEMENT("The statement is invalid."),
    NO_LEFT_BRACKET("No left bracket."),
    NO_RIGHT_BRACKET("No right bracket."),
    INVALID_ASSIGNMENT_EXPRESSION("No assignment operator."),
    INVALID_EXPRESSION("The expression is invalid."),
    INVALID_VARIABLE_DEFINITION("Variable type cannot be 'void'."),
    VARIABLE_HAS_BEEN_DECLARED("Variable has been declared."),
    FUNCTION_HAS_BEEN_DEFINED("Function has been defined."),
    VARIABLE_OR_FUNCTION_UNDEFINED("Variable or function is not defined."),
    ASSIGN_TO_CONSTANT("Cannot assign value to constant."),
    NO_RETURN_VALUE("No return value for 'int' function."),
    ASSIGN_TO_NON_INTEGER("Don't assign value to non-integer. I didn't write extension!"),
    PARAMETER_SIZE_MISMATCH("Too many or too few arguments."),
    INVALID_CONDITION("Condition is invalid.");

    private final String description;

    ErrorCode(final String description) {
        this.description = description;
    }

    public String getErrorMessage() {
        return this.description;
    }

}
