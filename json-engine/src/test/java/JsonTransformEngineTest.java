import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.json.engine.JsonTransformer.transformJson;
import static org.junit.jupiter.api.Assertions.*;

public class JsonTransformEngineTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String schemaJson;
    private String inputJson;

    @BeforeEach
    void setUp() {
        schemaJson = """
                {
                  "forms": [
                    {
                      "name": "jsonStart",
                      "engine": "hard-coded",
                      "output": "#jsonStart",
                      "value": 0
                    },
                    {
                      "name": "jsonEnd",
                      "engine": "hard-coded",
                      "output": "#jsonEnd",
                      "value": "4"
                    },
                    {
                      "name": "data",
                      "engine": "object-array",
                      "output": "/transformed",
                      "input": [""],
                      "schema": [
                        {
                          "name": "integerToDouble",
                          "engine": "straight-pull",
                          "output": "/integerToDouble",
                          "input": ["$.integerValue"],
                          "inputDataType": "integer",
                          "outputDataType": "double"
                        },
                        {
                          "name": "doubleToInteger",
                          "engine": "straight-pull",
                          "output": "/doubleToInteger",
                          "input": ["$.doubleValue"],
                          "inputDataType": "double",
                          "outputDataType": "integer"
                        },
                        {
                          "name": "bigIntegerToBigDecimal",
                          "engine": "straight-pull",
                          "output": "/bigIntegerToBigDecimal",
                          "input": ["$.bigIntegerValue"],
                          "inputDataType": "bigInteger",
                          "outputDataType": "bigDecimal"
                        },
                        {
                          "name": "bigDecimalToBigInteger",
                          "engine": "straight-pull",
                          "output": "/bigDecimalToBigInteger",
                          "input": ["$.bigDecimalValue"],
                          "inputDataType": "bigDecimal",
                          "outputDataType": "bigInteger"
                        },
                        {
                          "name": "timestampToISO",
                          "engine": "straight-pull",
                          "output": "/timestampToISO",
                          "input": ["$.timestampValue"],
                          "inputDataType": "timestamp",
                          "outputDataType": "ISODate"
                        },
                        {
                          "name": "ISOToTimestamp",
                          "engine": "straight-pull",
                          "output": "/ISOToTimestamp",
                          "input": ["$.isoDateValue"],
                          "inputDataType": "ISODate",
                          "outputDataType": "timestamp"
                        },
                        {
                          "name": "integerArrayToDoubleArray",
                          "engine": "straight-pull",
                          "output": "/integerArrayToDoubleArray",
                          "input": ["$.integerArray"],
                          "inputDataType": "integerArray",
                          "outputDataType": "doubleArray"
                        },
                        {
                          "name": "doubleArrayToIntegerArray",
                          "engine": "straight-pull",
                          "output": "/doubleArrayToIntegerArray",
                          "input": ["$.doubleArray"],
                          "inputDataType": "doubleArray",
                          "outputDataType": "integerArray"
                        }
                      ],
                      "inputDataType": "object",
                      "outputDataType": "array"
                    },
                    {
                      "name": "employee",
                      "engine": "object-array",
                      "output": "/employee",
                      "input": ["$.users"],
                      "schema": [
                        {
                          "name": "userId",
                          "engine": "straight-pull",
                          "output": "/userId",
                          "input": ["$.userId"]
                        },
                        {
                          "name": "name",
                          "engine": "straight-pull",
                          "output": "/name",
                          "input": ["$.name"]
                        },
                        {
                          "name": "employeeAddress",
                          "engine": "object-array",
                          "output": "/employeeAddress",
                          "input": ["$.addresses"],
                          "schema": [
                            {
                              "name": "type",
                              "engine": "straight-pull",
                              "output": "/type",
                              "input": ["$.type"]
                            },
                            {
                              "name": "street",
                              "engine": "straight-pull",
                              "output": "/street",
                              "input": ["$.street"]
                            }
                          ],
                          "inputDataType": "array",
                          "outputDataType": "array"
                        },
                        {
                          "name": "transactions",
                          "engine": "object-array",
                          "output": "/transactions",
                          "input": ["$.transactions"],
                          "schema": [
                            {
                              "name": "transactionId",
                              "engine": "straight-pull",
                              "output": "/transactionId",
                              "input": ["$.transactionId"]
                            },
                            {
                              "name": "amount",
                              "engine": "straight-pull",
                              "output": "/amount",
                              "input": ["$.amount"]
                            },
                            {
                              "name": "timestamp",
                              "engine": "straight-pull",
                              "output": "/timestamp",
                              "input": ["$.timestamp"],
                              "inputDataType": "timestamp",
                              "outputDataType": "ISODate"
                            }
                          ],
                          "inputDataType": "array",
                          "outputDataType": "array"
                        }
                      ],
                      "inputDataType": "array",
                      "outputDataType": "array"
                    }
                  ]
                }
                """;

        inputJson = """
                {
                                                       "integerValue": 42,
                                                       "doubleValue": 42.75,
                                                       "bigIntegerValue": 12345678901234567890,
                                                       "bigDecimalValue": 9876543210.12345,
                                                       "timestampValue": 1741797330000,
                                                       "isoDateValue": "2025-03-12T10:15:30Z",
                                                       "dateValue": "2025-03-12",
                                                       "integerArray": [1, 2, 3, 4, 5],
                                                       "doubleArray": [1.1, 2.2, 3.3, 4.4, 5.5],
                                                       "bigIntegerArray": [12345678901234567890, 98765432101234567890],
                                                       "bigDecimalArray": [12345678901234.567890, 98765432101234.567890],
                                                       "timestampArray": [1741797330000, 1741797340000, 1741797350000],
                                                       "isoDateArray": ["2025-03-12T10:15:30Z", "2025-03-12T11:15:30Z", "2025-03-12T12:15:30Z"],
                                                       "stringArray": ["hello", "world", "json"],
                                                       "users": [
                                                         {
                                                           "userId": 1,
                                                           "name": "John Doe",
                                                           "email": "john.doe@example.com",
                                                           "isActive": true,
                                                           "age": 30,
                                                           "balance": 12345.67,
                                                           "createdAt": "2025-03-12T16:35:30Z",
                                                           "lastLoginTimestamp": 1741774530000,
                                                           "addresses": [
                                                             {
                                                               "type": "Home",
                                                               "street": "123 Main St",
                                                               "city": "New York",
                                                               "zip": "10001",
                                                               "country": "USA"
                                                             },
                                                             {
                                                               "type": "Work",
                                                               "street": "456 Office Blvd",
                                                               "city": "Los Angeles",
                                                               "zip": "90001",
                                                               "country": "USA"
                                                             }
                                                           ],
                                                           "transactions": [
                                                             {
                                                               "transactionId": "TXN123456",
                                                               "amount": 12345678901234567890,
                                                               "currency": "USD",
                                                               "timestamp": 1741774530000,
                                                               "status": "Completed"
                                                             },
                                                             {
                                                               "transactionId": "TXN987654",
                                                               "amount": 987654,
                                                               "currency": "EUR",
                                                               "timestamp": 1741778130000,
                                                               "status": "Pending"
                                                             }
                                                           ]
                                                         },
                                                         {
                                                           "userId": 2,
                                                           "name": "Alice Smith",
                                                           "email": "alice.smith@example.com",
                                                           "isActive": false,
                                                           "age": 27,
                                                           "balance": 98765.43,
                                                           "createdAt": "2024-12-10T10:00:00Z",
                                                           "lastLoginTimestamp": 1733788800000,
                                                           "addresses": [
                                                             {
                                                               "type": "Home",
                                                               "street": "789 Suburb Rd",
                                                               "city": "San Francisco",
                                                               "zip": 94105,
                                                               "country": "USA"
                                                             }
                                                           ],
                                                           "transactions": [
                                                             {
                                                               "transactionId": "TXN555111",
                                                               "amount": 555,
                                                               "currency": "GBP",
                                                               "timestamp": 1733788800000,
                                                               "status": "Completed"
                                                             }
                                                           ]
                                                         }
                                                       ]
                                  }
                """;
    }

    @Test
    void testJsonTransformation() throws Exception {
        // Execute transformation
        ObjectNode result = transformJson(schemaJson, inputJson);
        System.out.println(result.toPrettyString());
        // Assertions
        assertNotNull(result);
    }
}
