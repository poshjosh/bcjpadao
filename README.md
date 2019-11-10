# bcjpadao
Light weight (18 classes) JPA helper library - syntatic sugar (elegantly expressive) and more

## Define variables

```java
        int firstResult = 0;
        int maxResults = 100;
        
        EntityManager em;
        
        Class<E> entityType;
        
        Object toPersist;
```
        
## Simple use-case (Generic Dao)

```java
        try(Dao dao = new DaoImpl(em)) {
            dao.begin().persist(toPersist).commit();
        }
```

## DeleteDao 

```java
        // Call Dao#forDelete to get DeleteDao instance, forSelect to get SelectDao... etc
        //
        DeleteDao<E> forDelete = dao.forDelete(entityType);

        // finish() method calls executeUpdate(), commit(), close()
        //
        int updateCount = forDelete.begin().from(entityType).where("col_0", "val_0").finish(); 
```

## SelectDao

```java
        List<E> resultList = dao.forSelect(entityType).getCriteria()
                .from(entityType)
                .select("col_0", "col_1")
                .where()
                .getResultsAndClose(firstResult, maxResults);
```

## Using Builders makes life easier 

```java
        // (Builders implement CriteriaDao so no need to call getCriteria())

        // SELECT COUNT(*) WHERE col = 'val'
        
        Long count = dao.builderForSelect(Long.class)
                .where(entityType, "col", "val").count().getSingleResultAndClose();
        
        // SELECT col_0, col_1 FROM table WHERE ... ... ORDER BY col_2 ASC col_1 ASC

        List<String[]> resultList = new DaoImpl(em).builderForSelect(String[].class)
                .from(entityType)
                .where("col_0", "val_0")
                .or().where("col_1", Criteria.LIKE, "val_1")
                .and().where("col_2", Criteria.LESS_THAN, "val_2")
                .select(columnsToSelect)
                .ascOrder("col_2", "col_1")
                .getResultsAndClose(firstResult, maxResults);
```

## Reusing Dao / Builder(s)

```java
        // Dao / BuilderFor may be reused if #close() has not been called. Simply call #reset() before reuse.

        try(BuilderForUpdate<E> reused = new DaoImpl(em).builderForUpdate(entityType)) {
            
            reused.begin();
            
            updateCount = reused.from(entityType).where("col_0", "val_0").set("col_0", "val_1").executeUpdate();
            
            reused.reset();
            
            updateCount = reused.from(entityType).where("col_0", "val_1").set("col_0", "val_0").executeUpdate();
            
            reused.commit();
        }
```