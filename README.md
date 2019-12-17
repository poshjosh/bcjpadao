## chocogreen

#### A Java app to enable the fulfillment team at *chocogreen* generate clear information about the number of chocolates to serve up for wrapper-less orders

### Specifications

-  Java 1.8

-  App is bundled as **.jar** file, with dependencies.

-  However only test scope dependencies are required:

```xml
    <dependencies>
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-all</artifactId>
            <version>1.10.19</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
```
### To Run the application

-  Make sure java is on your class path.

-  Open the command prompt.

-  Change directory to the directory/folder where the jar file is.

-  Make sure the **input\orders.csv** file is in the same folder with the jar file.

   For example consider the following, depicting related file locations:

   -  **Jar file**: C:\Users\USER\Desktop\app\chocogreen-1.0-SNAPSHOT.jar

   -  **CSV file**: C:\Users\USER\Desktop\app\input\orders.csv

   And here is the structure:

   -  **app** *(C:\Users\USER\Desktop\app)*

      -  **chocogreen.jar**

      -  **input**

         -  **orders.csv**  

-  Run the following command: 

```
   java -jar [JAR FILE]  
```

   For example: 

```
   C:\Users\USER\Desktop\app>java -jar chocogreen-1.0-SNAPSHOT.jar
```

-  You could specify a different location for the source file with a '**source**' argument:

   For example, a relative path: 

```
   C:\Users\USER\Desktop\app>java -jar chocogreen-1.0-SNAPSHOT.jar input\orders.csv
```
   
   Or, an absolute path: 
```
C:\Users\USER\Desktop\app>java -jar chocogreen-1.0-SNAPSHOT.jar C:\Users\USER\Desktop\app\input\orders.csv
```

-  The app runs in silent mode by default. To display logging information add the argument '**debug=true**'

   For example: 
```            
   C:\Users\USER\Desktop\app>java -jar chocogreen-1.0-SNAPSHOT.jar debug=true
```

### List of Supported Command Line Arguments

|     Argument     |             Type               |     Default Value    |                    Implication                  | 
| ---------------- | ------------------------------ | -------------------- | ----------------------------------------------- |
| charset          | class java.nio.charset.Charset | system default value | Charset for I/O operations                      |
| debug            | boolean                        | true                 | Display logging info                            |
| limit            | int                            | Integer.MAX_VALUE    | Process at most this number of lines            |
| offset           | int                            | 0                    | Start processing from the line at this position |
| source           | text                           | input/orders.csv     | The source of CSV data (absolute or relative)   |
| sourceHasHeaders | boolean                        | true                 | true if the source has headers, otherwise false |

-  An invalid command line argument, will cause the default value to be used.

-  If unsure, use the **'debug=true'** argument to generate debugging information.

### Notes

-  I was tempted to create an Order class with a field named 'type', for simplicity.

   However, I believe the 'type' field does not belong to the Order class. 
   Rather, it belongs to Chocolate or order item data class.
   To butress this point, compare the following methods which should return any of (milk | dark | white) :

```java
   Order.getType()  	
```
   vs

```java
   Chocolate.getType()
```

   The first method is misleading, while the second is intuitive.

-  A bonus item (chocolate) has price of value zero (0). This is consistent with the concept of bonuses being 'free of charge'.
   Also, amount payable does not change after adding bonuses because each bonus has price of '0'.

-  @specs captures notes relating to given specifications.

-  serialVersionUID was not generated for any of the classes.



