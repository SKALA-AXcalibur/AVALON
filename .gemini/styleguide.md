** IMPORTANT: All code reviews, comments and responses must be written in Korean. **

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

---

# TypeScript Style Guide
## Introduction
This style guide outlines the coding conventions for TypeScript code. It aims to ensure consistency, readability, and maintainability across projects. This guide can be adapted to specific project needs.

## Key Principles
* **Readability:** Code should be easy to understand for all team members, including AI assistants. Clear naming and logical structure are paramount.
* **Maintainability:** Code should be easy to modify, extend, and refactor. Well-defined types and modules contribute significantly to this.
* **Consistency:** Adhering to a consistent style across all projects improves collaboration, reduces cognitive load, and helps AI tools provide more accurate suggestions.
* **Type Safety:** Leverage TypeScript's type system to catch errors at compile-time and improve code reliability. Avoid `any` where possible.
* **Performance:** While readability and maintainability are primary, write efficient code. Be mindful of potential performance implications of certain patterns.

## Style Guidelines

### Line Length
* **Maximum line length:** 100 characters.
    * This accommodates modern screen widths and helps maintain readability, especially with complex type definitions or nested structures.
    * Consider breaking lines at logical points like operators, commas, or before arrow function bodies if they exceed the limit.

### Indentation
* **Use 2 spaces per indentation level.**
    * Spaces are preferred over tabs for consistency across different editors and environments. This is also a common default for tools like Prettier.

### Imports
* **Group imports:** Organize imports into groups, typically:
    1.  Node.js built-in modules (e.g., `fs`, `path`)
    2.  External/third-party library imports (e.g., `react`, `lodash`)
    3.  Internal/project-specific module imports (e.g., `../services/user-service`, `@/components/Button`)
* **Sort imports:** Sort imports alphabetically within each group. This can be automated by linters.
* **Named imports:** Prefer named imports over default imports for clarity, unless a module is specifically designed to be used with a default import.
    ```typescript
    // Preferred
    import { SpecificFunction, SpecificClass } from 'module-name';

    // Avoid if named exports are available
    // import ModuleName from 'module-name';
    ```
* **Type-only imports:** Use `import type` when importing only types or interfaces. This allows compilers to optimize away these imports.
    ```typescript
    import type { User, Product } from './interfaces';
    ```
* **Avoid relative paths for deeply nested files:** Use path aliases (e.g., `@/*` configured in `tsconfig.json`) for cleaner imports.

### Naming Conventions

* **Variables and Functions:** Use `camelCase`: `userName`, `calculateTotalValue()`.
* **Constants:** Use `UPPER_SNAKE_CASE` for true constants (values that never change at runtime). For `readonly` properties or configuration values that might be objects or arrays, `camelCase` or `PascalCase` (if they are class-like) is also acceptable if consistently applied.
    ```typescript
    const MAX_USERS = 100;
    const API_KEY = 'your-secret-key';
    const defaultUserRoles = ['viewer', 'editor']; // camelCase for mutable-looking constant
    ```
* **Classes, Interfaces, Type Aliases, Enums:** Use `PascalCase`: `UserService`, `UserDetails`, `ColorTheme`, `OrderStatus`.
* **Methods and Properties (within classes/objects):** Use `camelCase`: `getUserById()`, `isActive`.
* **Private Members:** Prefix private properties and methods with an underscore (`_`) if not using the `private` keyword (though `private` is preferred for compile-time checking).
    ```typescript
    class ProductService {
        private _cache: Map<string, Product>; // Preferred
        _fallbackUrl: string; // If not using private keyword strictly

        constructor() {
            this._cache = new Map();
            this._fallbackUrl = '/products/default';
        }

        public getProduct(id: string): Product | undefined {
            return this._cache.get(id);
        }
    }
    ```
* **Modules/Files:** Use `kebab-case` (e.g., `user-service.ts`, `validation-utils.ts`) or `camelCase` (e.g., `userService.ts`) consistently. `kebab-case` is more common.
* **Type Parameters (Generics):** Use single uppercase letters (e.g., `T`, `U`, `K`, `V`, `P`) for simple generics. For more descriptive generic type parameters, use `PascalCase` (e.g., `TResponse`, `KeyType`).
    ```typescript
    function identity<T>(arg: T): T {
        return arg;
    }

    interface ApiResponse<TData> {
        data: TData;
        error: string | null;
    }
    ```

### Documentation Comments (TSDoc/JSDoc)
* **Use TSDoc (JSDoc with TypeScript enhancements) `/** ... */` for all exported classes, interfaces, types, functions, and methods.**
* **First line:** Provide a concise summary of the element's purpose, ending with a period.
* **Detailed description:** If necessary, provide more details, including `@param`, `@returns`, `@throws`, `@template` (for generics), `@deprecated`, `@see`, etc.
* **Type information in TSDoc:** While TypeScript infers types, explicitly documenting types in TSDoc can sometimes aid readability for complex types or when generating external documentation. However, avoid redundancy if the type signature is clear.
    ```typescript
    /**
     * Retrieves a user by their unique identifier.
     *
     * This function queries the database and returns the user object.
     *
     * @param userId - The unique identifier of the user to retrieve.
     * @returns The user object if found, otherwise undefined.
     * @throws Error if the database connection fails.
     * @template TUser - A generic type extending the base User profile.
     */
    async function getUser<TUser extends UserProfile>(userId: string): Promise<TUser | undefined> {
        // ... implementation
        if (!userId) {
            throw new Error('User ID cannot be empty.');
        }
        // fetch user
        return undefined; // Placeholder
    }
    ```

### Types, Interfaces, and Enums
* **Prefer `interface` for object shapes and class contracts.** They can be extended and implemented.
* **Use `type` for primitives, unions, intersections, tuples, and more complex type manipulations (e.g., mapped types, conditional types).**
    ```typescript
    interface UserProfile {
        id: string;
        name: string;
        email?: string; // Optional property
    }

    type UserId = string;
    type Status = 'active' | 'inactive' | 'pending';
    type Point = [number, number];
    ```
* **Avoid `any`:** Use `any` as a last resort. Prefer `unknown` for values whose type is not known at compile time, and then perform type checking or use type assertions carefully.
* **Explicit return types:** Always specify return types for functions and methods, even if they can be inferred. This improves readability and helps catch errors.
* **`readonly` modifier:** Use `readonly` for properties that should not be reassigned after an object is created.
    ```typescript
    interface Configuration {
        readonly apiKey: string;
        readonly baseUrl: string;
    }
    ```
* **String Enums:** Prefer string enums over numeric enums for better readability and debugging, unless numeric values are specifically required.
    ```typescript
    enum LogLevel {
        Debug = "DEBUG",
        Info = "INFO",
        Warning = "WARNING",
        Error = "ERROR",
    }
    ```
* **`const` assertions:** Use `as const` for objects or arrays where you want to treat values as literal types and properties as `readonly`.
    ```typescript
    const httpMethods = ['GET', 'POST', 'PUT', 'DELETE'] as const;
    type HttpMethod = typeof httpMethods[number]; // 'GET' | 'POST' | 'PUT' | 'DELETE'
    ```

### Null and Undefined
* **Enable `strictNullChecks` in `tsconfig.json`.** This is crucial for catching null/undefined errors at compile time.
* Be explicit about whether a value can be `null` or `undefined` using union types (e.g., `string | undefined`).
* Use optional chaining (`?.`) and nullish coalescing (`??`) operators for safer access to potentially null/undefined properties and providing default values.

### Functions and Methods
* **Arrow Functions:** Use arrow functions for callbacks and when `this` lexical scoping is desired. For methods in classes or standalone functions, regular function declarations are also fine and sometimes preferred for hoisting or explicit `this` binding.
* **Parameter Destructuring:** Use parameter destructuring for functions with multiple options or parameters, especially if they are objects.
    ```typescript
    interface CreateUserOptions {
        username: string;
        email: string;
        isActive?: boolean;
    }

    function createUser({ username, email, isActive = false }: CreateUserOptions): UserProfile {
        // ...
        return { id: 'new-id', name: username, email, isActive };
    }
    ```

### Classes
* **Access Modifiers:** Explicitly use `public`, `private`, and `protected` for class members. Default is `public`.
* **Constructor:** Keep constructors concise. If there's complex initialization logic, move it to private methods.
* **Readonly Properties:** Use the `readonly` modifier for properties that should only be set in the constructor.

### Comments (Implementation Comments)
* **Write clear and concise comments:** Explain the "why" behind the code, not just the "what." Complex logic or workarounds are good candidates for comments.
* **Use `//` for single-line comments and `/* ... */` for multi-line comments.** Avoid using block comments for TSDoc.
* **TODO comments:** Use `// TODO:` or `// FIXME:` to mark areas that need future attention, optionally including a reference or your name.
    ```typescript
    // TODO (yourname_date): Refactor this to use the new validation service.
    ```

### Logging
* **Use a dedicated logging library:** For applications, use a library like Winston, Pino, or Bunyan instead of `console.log()` for production code. `console.log()` is acceptable for quick debugging or simple scripts.
* **Log at appropriate levels:** Use standard levels like `debug`, `info`, `warn`, `error`.
* **Provide context in logs:** Include relevant information such as request IDs, user IDs, or operation names to aid in debugging.
* **Structured logging:** Consider structured logging (e.g., JSON format) for easier parsing and analysis by log management systems.

### Error Handling
* **Use specific custom error classes:** Extend the built-in `Error` class to create custom error types for better error identification and handling.
    ```typescript
    class NetworkError extends Error {
        constructor(message: string, public statusCode?: number) {
            super(message);
            this.name = 'NetworkError';
        }
    }
    ```
* **Handle Promises correctly:** Always handle rejected Promises using `.catch()` or `try...catch` with `async/await`.
* **Avoid throwing non-Error objects:** Only throw instances of `Error` or its subclasses.
* **`try...catch...finally`:** Use `finally` blocks to ensure cleanup code (e.g., releasing resources) executes regardless of whether an error occurred.

### Async/Await
* **Prefer `async/await` for asynchronous code:** It generally leads to more readable and maintainable asynchronous logic compared to chaining `.then()` and `.catch()` directly, especially for complex sequences.
* Ensure `async` functions always return a `Promise`.

## Tooling
* **Code Formatter: Prettier**
    * Use Prettier to automatically enforce consistent code formatting. Integrate it into your editor and CI/CD pipeline.
    * Share a `.prettierrc.json` or similar configuration file in the project.
* **Linter: ESLint with TypeScript Support**
    * Use ESLint with `@typescript-eslint/parser` and `@typescript-eslint/eslint-plugin` to identify potential bugs, style violations, and enforce coding standards.
    * Configure ESLint with a `.eslintrc.js` or `.eslintrc.json` file, extending recommended rule sets (e.g., `eslint:recommended`, `plugin:@typescript-eslint/recommended`).
    * Consider rules for import sorting, naming conventions, and best practices.
* **Compiler: TypeScript Compiler (`tsc`)**
    * Configure `tsconfig.json` with appropriate compiler options, including:
        * `"target"`: (e.g., `ES2020` or newer)
        * `"module"`: (e.g., `ESNext` or `CommonJS` depending on the environment)
        * `"strict"`: `true` (enables all strict type-checking options)
        * `"esModuleInterop"`: `true`
        * `"skipLibCheck"`: `true` (can speed up compilation for projects with many dependencies)
        * `"forceConsistentCasingInFileNames"`: `true`
        * `"noImplicitAny"`: `true` (part of `strict`)
        * `"strictNullChecks"`: `true` (part of `strict`)
