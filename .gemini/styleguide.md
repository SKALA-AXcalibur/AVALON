** IMPORTANT: All code reviews, summaries, comments and responses must be written in Korean. **

---

# Python Style Guide

## Introduction
This style guide outlines the coding conventions for Python code.
It's based on PEP 8, but with some modifications to address specific needs and
preferences.

## Key Principles
* **Readability:** Code should be easy to understand for all team members.
* **Maintainability:** Code should be easy to modify and extend.
* **Consistency:** Adhering to a consistent style across all projects improves
  collaboration and reduces errors.
* **Performance:** While readability is paramount, code should be efficient.

## Deviations from PEP 8

### Line Length
* **Maximum line length:** 100 characters (instead of PEP 8's 79).
    * Modern screens allow for wider lines, improving code readability in many cases.
    * Many common patterns in our codebase, like long strings or URLs, often exceed 79 characters.

### Indentation
* **Use 4 spaces per indentation level.** (PEP 8 recommendation)

### Imports
* **Group imports:**
    * Standard library imports
    * Related third party imports
    * Local application/library specific imports
* **Absolute imports:** Always use absolute imports for clarity.
* **Import order within groups:**  Sort alphabetically.

### Naming Conventions

* **Variables:** Use lowercase with underscores (snake_case): `user_name`, `total_count`
* **Constants:**  Use uppercase with underscores: `MAX_VALUE`, `DATABASE_NAME`
* **Functions:** Use lowercase with underscores (snake_case): `calculate_total()`, `process_data()`
* **Classes:** Use CapWords (CamelCase): `UserManager`, `PaymentProcessor`
* **Modules:** Use lowercase with underscores (snake_case): `user_utils`, `payment_gateway`

### Docstrings
* **Use triple double quotes (`"""Docstring goes here."""`) for all docstrings.**
* **First line:** Concise summary of the object's purpose.
* **For complex functions/classes:** Include detailed descriptions of parameters, return values,
  attributes, and exceptions.
* **Use Google style docstrings:** This helps with automated documentation generation.
    ```python
    def my_function(param1, param2):
        """Single-line summary.

        More detailed description, if necessary.

        Args:
            param1 (int): The first parameter.
            param2 (str): The second parameter.

        Returns:
            bool: The return value. True for success, False otherwise.

        Raises:
            ValueError: If `param2` is invalid.
        """
        # function body here
    ```

### Type Hints
* **Use type hints:**  Type hints improve code readability and help catch errors early.
* **Follow PEP 484:**  Use the standard type hinting syntax.

### Comments
* **Write clear and concise comments:** Explain the "why" behind the code, not just the "what".
* **Comment sparingly:** Well-written code should be self-documenting where possible.
* **Use complete sentences:** Start comments with a capital letter and use proper punctuation.

### Logging
* **Use a standard logging framework:** use the built-in `logging` module.
* **Log at appropriate levels:** DEBUG, INFO, WARNING, ERROR, CRITICAL
* **Provide context:** Include relevant information in log messages to aid debugging.

### Error Handling
* **Use specific exceptions:** Avoid using broad exceptions like `Exception`.
* **Handle exceptions gracefully:** Provide informative error messages and avoid crashing the program.
* **Use `try...except` blocks:**  Isolate code that might raise exceptions.

## Tooling
* **Code formatter:**  [Specify formatter, e.g., Black] - Enforces consistent formatting automatically.
* **Linter:**  [Specify linter, e.g., Flake8, Pylint] - Identifies potential issues and style violations.

---

# Java Style Guide

## Introduction
This style guide briefly outlines the coding conventions for Java code.
This guide is based on the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) but has been partially modified to reflect specific requirements and preferences.

## Key Principles
*   **Readability:** Code should be easily understandable by all team members.
*   **Maintainability:** Code should be easy to modify and extend.
*   **Consistency:** Adhering to a consistent style across all projects improves collaboration and reduces errors.
*   **Performance:** While readability is paramount, code efficiency should also be considered.

## Differences from (or Highlights of) the Google Java Style Guide

### Line Length
*   **Maximum line length:** 100 characters (same as Google Java Style Guide recommendation).
    *   Modern screens allow for wider lines, which often improves code readability in many cases.
    *   Common patterns in our codebase (e.g., long string literals, complex generic types) often exceed 80 characters.

### Indentation
*   **Use 2 spaces per indentation level.** (Google Java Style Guide recommendation)
    *   Spaces must be used instead of tab characters.

### Imports
*   **No wildcard imports:** Wildcard imports using `*` (e.g., `java.util.*`) are not used as they can hinder readability compared to importing only explicitly needed classes.
*   **Import order:** Group imports in the following order, and sort alphabetically within each group.
    1.  All static imports (grouped together)
    2.  Imports starting with `java.`
    3.  Imports starting with `javax.`
    4.  Imports starting with `org.`
    5.  Imports starting with `com.`
    6.  All other imports (e.g., project-specific packages)
*   **Remove unused imports:** Always remove unused imports using IDE features.

### Naming Conventions

*   **Packages:** All lowercase, with words concatenated without underscores (`_`) or other separators. Using reverse domain names is recommended (e.g., `com.company.project.module`).
*   **Classes and Interfaces:** Use PascalCase (or UpperCamelCase). Start each word with an uppercase letter (e.g., `UserManager`, `PaymentProcessor`, `UserService`).
*   **Methods:** Use lowerCamelCase. The first word starts with a lowercase letter, and subsequent words start with an uppercase letter (e.g., `calculateTotal()`, `processData()`).
*   **Constants:** All uppercase, with words separated by underscores (`_`) (Snake Case, `UPPER_SNAKE_CASE`). Must be declared `static final` (e.g., `MAX_VALUE`, `DATABASE_URL`).
*   **Variables:** Use lowerCamelCase (e.g., `userName`, `totalCount`). This applies to local variables, instance variables, and class variables (non-static).
*   **Type Parameters:** Typically use a single uppercase letter (e.g., `T`, `E`, `K`, `V`). Alternatively, a class name followed by `T` can be used (e.g., `FooT`).

### Javadoc Comments
*   **Use Javadoc comments (`/** ... */`) for all public classes, interfaces, and methods.** Also recommended for protected members.
*   **First line:** Briefly summarize the purpose of the class or method. Must end with a period.
*   **Detailed description:** If necessary, describe parameters (`@param`), return values (`@return`), exceptions (`@throws`), related classes/methods (`@see`), author (`@author`), version (`@version`), etc., in detail.
*   **Javadoc format:**
    ```java
    /**
     * Single-line summary. (Ends with a period)
     *
     * Provide more detailed explanations here if necessary.
     * Can be written in multiple paragraphs.
     *
     * @param param1 Description of the first parameter.
     * @param param2 Description of the second parameter.
     * @return Description of the return value. true on success, false otherwise.
     * @throws IllegalArgumentException if {@code param2} is invalid.
     * @see AnotherClass#anotherMethod(String)
     */
    public boolean myFunction(int param1, String param2) {
        // Method body
        if (param2 == null || param2.isEmpty()) {
            throw new IllegalArgumentException("param2 cannot be null or empty.");
        }
        return true;
    }
    ```

### Explicit Type Usage and Generics
*   **Explicit Type Usage:** Java is a statically-typed language. Specify clear types when declaring variables. The `var` keyword can be used for local variable type inference, but only when it does not harm readability.
*   **Proper Use of Generics:** Use generics to enhance type safety and reduce code duplication. Avoid using raw types.

### Comments
*   **Write clear and concise comments:** Explain the "why" or "how" rather than the "what" of the code.
*   **Use comments only when necessary:** Well-written code should be self-explanatory.
*   **Use complete sentences:** Comments should start with an uppercase letter and use proper punctuation.
*   **Use single-line comments (`//`) and block comments (`/* ... */`):** Used for implementation comments, not Javadoc.

### Logging
*   **Use a standard logging framework:** Standardize on **SLF4J** as the interface and **Logback** (or **Log4j 2**) as the implementation.
*   **Use appropriate logging levels:** DEBUG, INFO, WARN, ERROR. (Also TRACE, FATAL as needed).
*   **Provide context:** Include relevant information in log messages to aid debugging (e.g., user ID, request ID).
*   **Avoid using System.out.println:** Use a logging framework instead of `System.out.println` for purposes other than debugging.

### Error Handling
*   **Use specific exceptions:** Avoid catching broad exceptions like `RuntimeException` or `Exception` directly. Use specific exceptions whenever possible.
*   **Handle exceptions appropriately:** Avoid ignoring exceptions (empty `catch` blocks) or simply handling them with `e.printStackTrace()`. Recover in a meaningful way, convert to an exception of an appropriate abstraction level and rethrow, or inform the user of the error.
*   **Checked vs. Unchecked Exceptions:**
    *   Use `Checked Exceptions` for situations where the caller can recover.
    *   Use `Unchecked Exceptions` (mainly subclasses of `RuntimeException`) for programming errors (e.g., NPE, `IllegalArgumentException`).
*   **Utilize `try-catch-finally` and `try-with-resources`:** When resource deallocation is necessary, use `finally` blocks or `try-with-resources` statements to ensure safe release.

## Tooling
*   **Code Formatter:** [e.g., Google Java Format, IntelliJ IDEA/Eclipse built-in formatter (share settings)] - Automatically applies a consistent code style. Use [specify specific formatter and configuration file].
*   **Static Analysis Tools / Linter:** [e.g., Checkstyle, SpotBugs, PMD, SonarLint] - Identifies potential bugs, code smells, and style violations. Use [specify specific tools and ruleset].
