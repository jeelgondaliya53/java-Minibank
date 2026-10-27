# MiniBank — Practicals 1–10

Java 17+ console banking project built cumulatively for OOP practicals.

## Open and run

1. Open this folder in VS Code / IntelliJ IDEA / Eclipse.
2. Make sure JDK 17 or later is installed.
3. Run `app.MiniBank` from `src/main/java`, or build with Maven:

```bash
mvn clean package
java -jar target/oop-minibank-1.0.0.jar
```

To run demonstrations for concepts covered in Practicals 1–10:

```bash
java -jar target/oop-minibank-1.0.0.jar --demo
```

The demo includes validator/parser examples, account polymorphism, annotations, exceptions, synchronized worker threads, an executor, producer-consumer coordination, and transfers using consistent account lock ordering.

## Packages

- `app` — console entry point
- `model` — customers, accounts, enums, records and interfaces
- `model.annotation` — custom validation annotations
- `util` — validators, parser, statement formatter and annotation validator
- `service` — worker threads, transaction processor and producer-consumer buffer
- `exception` — checked banking exceptions

This is an educational mini-project, not production banking software. It does not persist data between runs and should not be used for real financial transactions.
