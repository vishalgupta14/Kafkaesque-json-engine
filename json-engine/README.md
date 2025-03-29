# JSON Transformation Engine - README

## Overview
The JSON Transformation Engine is a powerful, extensible framework that allows transformation, conversion, and enrichment of JSON data using declarative schemas. It supports:

- Type conversion (primitive and complex types)
- Mathematical operations
- String operations
- Conditional logic
- Recursive object/array processing
- Variable injection and JSONPath referencing

---

## Features

### ✅ **Type Conversion (Straight-Pull)**
Supports bi-directional conversion between:
- `integer`, `double`, `long`, `boolean`, `string`
- `bigInteger`, `bigDecimal`
- `timestamp`, `ISODate`, `date`, `mongodbDate`

Supports array transformations as well:
- `integerArray <-> doubleArray`
- `timestampArray <-> ISODateArray`

### ✅ **Hard-Coded Engine**
Injects constant values into the output. Values can be:
- Primitive values: Integer, String, Double, Boolean
- Timestamps/ISO Dates
- Arrays & Objects
- Variables (e.g., `#myVariable`)

### ✅ **Math Operation Engine**
Supports arithmetic operations between two or more values:
- `add`, `subtract`, `multiply`, `divide`
- Supports optional type conversion using `convert` field (e.g., `bigDecimal`)

### ✅ **String Operation Engine**
Supports string-based transformations like:
- `toUpperCase`, `toLowerCase`, `substring`
- `replace`, `regexReplace`, `split`
- `equals`, `notEquals`, `equalsIgnoreCase`
- `removeWhitespace`, `length`, `indexOf`

### ✅ **Conditional Operation Engine**
Applies logical rules for conditional outcomes:
```json
"criteria": [
  { "condition": "$1 == 'VIP' && $2 > 100", "outcome": "#vipDiscount" },
  { "condition": "$2 > 500", "outcome": 10 },
  { "condition": "true", "outcome": "#defaultDiscount" }
]
```

### ✅ **Object & Array Processing (Recursive)**
Supports nested JSON transformation using:
- `object-array` engine (recursive parsing)
- Supports schema chaining and arbitrary nesting

---

## Schema Structure

### Root-Level Schema Format
```json
{
  "forms": [
    { "name": "...", "engine": "...", "input": ["..."], "output": "...", ... }
  ]
}
```

### Supported Engines
| Engine              | Description                                 |
|--------------------|---------------------------------------------|
| `straight-pull`     | Direct type conversion from input to output |
| `hard-coded`        | Injects fixed values                        |
| `string-operation`  | Runs string logic (e.g., toUpperCase)       |
| `math-operation`    | Runs math logic                             |
| `conditional-operation` | Applies logical conditions             |
| `object-array`      | Processes array of nested objects           |

---

## Input Schema Examples

### Example 1: Nested User Transformation
```json
{
  "name": "employee",
  "engine": "object-array",
  "input": ["$.users"],
  "output": "/employee",
  "schema": [
    { "name": "userId", "engine": "straight-pull", "input": ["$.userId"], "output": "/userId" },
    { "name": "name", "engine": "straight-pull", "input": ["$.name"], "output": "/name" },
    {
      "name": "employeeAddress",
      "engine": "object-array",
      "input": ["$.addresses"],
      "output": "/employeeAddress",
      "schema": [
        { "name": "type", "engine": "straight-pull", "input": ["$.type"], "output": "/type" },
        { "name": "street", "engine": "straight-pull", "input": ["$.street"], "output": "/street" }
      ]
    },
    {
      "name": "transactions",
      "engine": "object-array",
      "input": ["$.transactions"],
      "output": "/transactions",
      "schema": [
        { "name": "amount", "engine": "straight-pull", "input": ["$.amount"], "output": "/amount" },
        { "name": "timestamp", "engine": "straight-pull", "input": ["$.timestamp"], "output": "/timestamp", "inputDataType": "timestamp", "outputDataType": "ISODate" }
      ]
    }
  ]
}
```

### Example 2: Math Engine
```json
{
  "name": "addition",
  "engine": "math-operation",
  "input": ["$.num1", "$.num2"],
  "operation": "add",
  "output": "/sumResult",
  "convert": "bigDecimal"
}
```

### Example 3: Conditional Engine
```json
{
  "name": "applyDiscount",
  "engine": "conditional-operation",
  "output": "/finalDiscount",
  "input": ["$.customerType", "$.orderAmount"],
  "criteria": [
    { "condition": "$1 == 'VIP' && $2 > 100", "outcome": "#vipDiscount" },
    { "condition": "$2 > 500", "outcome": 10 },
    { "condition": "true", "outcome": "#defaultDiscount" }
  ]
}
```

---

## Variable Reference
- `#variableName` can be used in any `value`, `condition`, or `input`
- Variables are populated using `hard-coded` forms

---

## Output
Output is an enriched and transformed JSON object that adheres to:
- Schema structure
- Data type requirements
- Nested object handling

### Example Output
```json
{
  "transformed": {
    "integerToDouble": 42.0,
    "doubleToInteger": 42,
    "timestampToISO": "2025-03-12T16:35:30Z"
  },
  "employee": [
    {
      "userId": 1,
      "name": "John Doe",
      "employeeAddress": [
        { "type": "Home", "street": "123 Main St" },
        { "type": "Work", "street": "456 Office Blvd" }
      ],
      "transactions": [
        { "amount": "12345678901234567890", "timestamp": "2025-03-11T18:30:30Z" }
      ]
    }
  ]
}
```

---

## Usage
1. Define your schema using the JSON structure
2. Pass your `inputJson` and `schemaJson` to the engine
3. Get a transformed JSON output via:
```java
ObjectNode result = JsonTransformEngine.transformJson(schemaJson, inputJson);
```

---

## Roadmap
- [ ] Support for custom function plugins
- [ ] Validation schema for input/output
- [ ] Advanced expression engine for criteria
- [ ] Integration with Apache Arrow or Avro for binary processing

---

## Contributors
Developed and maintained by **Vishal Gupta** and team. Contributions welcome!

---

## License
MIT License. Feel free to use, modify, and extend.

