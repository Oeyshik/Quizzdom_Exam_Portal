# Logging Configuration Guide

This project uses **Log4j2** for logging with a comprehensive configuration file.

## Configuration File

The logging configuration is defined in `src/main/resources/log4j2.xml`.

## Log File Locations

By default, log files are created in the `logs/` directory relative to the application root:

- **Main Log**: `logs/examserver.log`
- **Error Log**: `logs/examserver-error.log`
- **Debug Log**: `logs/examserver-debug.log`

## Configuring Log Path

To change the log file path, edit the `LOG_PATH` property in `log4j2.xml`:

```xml
<Property name="LOG_PATH">logs</Property>
```

You can use:
- Relative path: `logs` (relative to application root)
- Absolute path: `C:/logs/examserver` or `/var/log/examserver`

## Log Levels

The following log levels are configured:

- **DEBUG**: Detailed information for debugging
- **INFO**: General informational messages
- **WARN**: Warning messages
- **ERROR**: Error messages
- **FATAL**: Critical errors

### Changing Log Levels

Edit the `level` attribute in the `<Logger>` or `<Root>` sections:

```xml
<Logger name="com.exam" level="DEBUG" additivity="false">
```

Available levels (from lowest to highest):
- `TRACE`
- `DEBUG`
- `INFO`
- `WARN`
- `ERROR`
- `FATAL`
- `OFF`

## Rolling Strategy

Logs are automatically rolled (archived) based on:

1. **Time-based**: Daily rollover at midnight
2. **Size-based**: When file reaches 10MB

### Configuring Rollover

**Time-based rollover:**
```xml
<TimeBasedTriggeringPolicy interval="1" modulate="true"/>
```
- `interval="1"` means daily (1 day)
- Change to `7` for weekly, `30` for monthly

**Size-based rollover:**
```xml
<SizeBasedTriggeringPolicy size="10MB"/>
```
- Change `10MB` to your desired size (e.g., `50MB`, `100MB`)

**Retention Policy:**
```xml
<DefaultRolloverStrategy max="10">
    <Delete basePath="${LOG_PATH}" maxDepth="1">
        <IfFileName glob="${LOG_FILE_NAME}-*.log.gz"/>
        <IfLastModified age="30d"/>
    </Delete>
</DefaultRolloverStrategy>
```
- `max="10"`: Keep maximum 10 rolled files
- `age="30d"`: Delete files older than 30 days

## Log File Structure

### Main Log File (`examserver.log`)
Contains all log levels (INFO, WARN, ERROR, DEBUG, etc.)

### Error Log File (`examserver-error.log`)
Contains only ERROR and FATAL level logs

### Debug Log File (`examserver-debug.log`)
Contains only DEBUG level logs

## Log Format

Default log format:
```
yyyy-MM-dd HH:mm:ss.SSS [thread] LEVEL LoggerName - message
```

Example:
```
2026-01-26 10:30:45.123 [main] INFO  com.exam.controller.UserController - User created successfully with ID: 1
```

## Application-Specific Loggers

- **com.exam**: Application code (DEBUG level)
- **org.springframework**: Spring Framework (INFO level)
- **org.hibernate**: Hibernate/JPA (INFO level)
- **org.springframework.security**: Security (DEBUG level)

## Console Output

Logs are also output to the console with INFO level and above.

## Usage in Code

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyClass {
    private static final Logger logger = LogManager.getLogger(MyClass.class);
    
    public void myMethod() {
        logger.debug("Debug message");
        logger.info("Info message");
        logger.warn("Warning message");
        logger.error("Error message", exception);
    }
}
```

## Troubleshooting

### Logs not appearing

1. Check if the `logs/` directory exists and is writable
2. Verify log level is set appropriately
3. Check application.properties for any logging overrides

### Changing log path at runtime

You can override the log path using system properties:
```bash
java -DLOG_PATH=/custom/path -jar examserver.jar
```

### Viewing logs

- **Windows**: Use Notepad, Notepad++, or any text editor
- **Linux/Mac**: Use `tail -f logs/examserver.log` for real-time viewing

## Best Practices

1. Use appropriate log levels:
   - DEBUG: Detailed debugging information
   - INFO: Important business events
   - WARN: Potential issues
   - ERROR: Errors that need attention

2. Include context in log messages:
   ```java
   logger.info("User created with ID: {} and username: {}", userId, username);
   ```

3. Log exceptions with stack traces:
   ```java
   logger.error("Error creating user: {}", e.getMessage(), e);
   ```

4. Don't log sensitive information (passwords, tokens, etc.)

