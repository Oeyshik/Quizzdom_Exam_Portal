# Role Management System

## Overview

The application uses a **Role-Based Access Control (RBAC)** system with three entities:
- **Role**: Defines different user roles (NORMAL, ADMIN, INSTRUCTOR, etc.)
- **UserRole**: Join table connecting Users and Roles (many-to-many relationship)
- **User**: Users can have multiple roles

## Entity Design

### Role Entity
- `roleId` (Long): Primary key - manually assigned
- `roleName` (String): Name of the role (e.g., "NORMAL", "ADMIN")

### UserRole Entity
- `userRoleId` (Long): Primary key - auto-generated
- `user` (User): Many-to-one relationship with User
- `role` (Role): Many-to-one relationship with Role

### Relationship
- **User** ↔ **UserRole** ↔ **Role**
- One User can have multiple UserRoles
- One Role can be assigned to multiple Users
- UserRole is the join entity

## Default Roles

The system automatically initializes these default roles on startup:

1. **NORMAL** (ID: 1) - Regular users
2. **ADMIN** (ID: 2) - Administrators
3. **INSTRUCTOR** (ID: 3) - Instructors/Teachers

## How Roles Are Created

### Option 1: Automatic Initialization (Recommended)
Roles are automatically created when the application starts via `DataInitializer` component.

**No action needed** - just start the application!

### Option 2: Via API
Use the RoleController API endpoints:

```bash
# Initialize default roles manually
POST http://localhost:8080/role/init

# Create a custom role
POST http://localhost:8080/role/
Body: {
  "roleId": 4,
  "roleName": "STUDENT"
}
```

### Option 3: Direct Database Insert
You can insert roles directly into the database:

```sql
INSERT INTO roles (role_id, role_name) VALUES (1, 'NORMAL');
INSERT INTO roles (role_id, role_name) VALUES (2, 'ADMIN');
INSERT INTO roles (role_id, role_name) VALUES (3, 'INSTRUCTOR');
```

## API Endpoints

### Role Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/role/` | Create a new role |
| GET | `/role/` | Get all roles |
| GET | `/role/{roleId}` | Get role by ID |
| GET | `/role/name/{roleName}` | Get role by name |
| POST | `/role/init` | Initialize default roles |

### Examples

**Get all roles:**
```bash
GET http://localhost:8080/role/
```

**Get role by name:**
```bash
GET http://localhost:8080/role/name/NORMAL
```

**Create a new role:**
```bash
POST http://localhost:8080/role/
Content-Type: application/json

{
  "roleId": 4,
  "roleName": "STUDENT"
}
```

## User Registration with Roles

When a user registers, they are automatically assigned the **NORMAL** role by default.

The `UserController.createUser()` method:
1. Fetches the NORMAL role from the database
2. Creates a UserRole linking the user to the NORMAL role
3. Saves the user with the role assignment

## Adding Custom Roles

### Step 1: Create the Role
```bash
POST http://localhost:8080/role/
{
  "roleId": 4,
  "roleName": "STUDENT"
}
```

### Step 2: Assign Role to User
Modify the `UserController.createUser()` method to accept a role parameter, or create a separate endpoint to assign roles to existing users.

## Database Schema

### roles table
```sql
CREATE TABLE roles (
    role_id BIGINT PRIMARY KEY,
    role_name VARCHAR(255)
);
```

### user_role table
```sql
CREATE TABLE user_role (
    user_role_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(role_id)
);
```

## Best Practices

1. **Initialize roles on startup** - Use DataInitializer (already implemented)
2. **Use existing roles** - Don't create roles on-the-fly in controllers
3. **Check role existence** - Always verify role exists before assigning
4. **Use meaningful role names** - Use uppercase, descriptive names (NORMAL, ADMIN, etc.)
5. **Assign role IDs sequentially** - Start from 1 and increment

## Troubleshooting

### Roles not created on startup
- Check application logs for initialization errors
- Manually call `POST /role/init` to initialize roles
- Verify database connection is working

### Role not found errors
- Ensure roles are initialized (check database or call `/role/init`)
- Verify role name is correct (case-insensitive search)
- Check if role ID exists in database

### User registration fails
- Ensure NORMAL role exists (ID: 1)
- Check application logs for detailed error messages
- Verify database tables are created correctly

## Code Structure

```
com.exam/
├── model/
│   ├── Role.java          # Role entity
│   └── UserRole.java      # UserRole join entity
├── repo/
│   └── RoleRepository.java # Role data access
├── service/
│   ├── RoleService.java   # Role service interface
│   └── impl/
│       └── RoleServiceImpl.java # Role service implementation
├── controller/
│   └── RoleController.java # Role REST API
└── config/
    └── DataInitializer.java # Auto-initialize roles on startup
```

