# bcjpadao
Light weight (18 classes) JPA helper library - syntatic sugar (elegantly expressive) and more

# Define variables

        int firstResult = 0;
        int maxResults = 100;
        
        EntityManager em;
        
        Class<E> entityType;
        
        Object toPersist;
        
# Simple use-case

        try(Dao dao = new DaoImpl(em)) {
            dao.begin().persist(toPersist).commit();
        }

# SELECT COUNT(*) WHERE col = 'val'
        
        Long count = dao.builderForSelect(Long.class)
                .where(entityType, "col", "val").count().getSingleResultAndClose();
        
# SELECT col_0, col_1 FROM table WHERE ... ... ORDER BY col_2 ASC col_1 ASC

        String [] columnsToSelect = {"col_0", "col_1"};
        
        dao = new DaoImpl(em);
        
        BuilderForSelect<String[]> forSelect = dao.builderForSelect(String[].class);
        
        List<String[]> resultList = forSelect.from(entityType)
                .where("col_0", "val_0")
                .or().where("col_1", Criteria.LIKE, "val_1")
                .and().where("col_2", Criteria.LESS_THAN, "val_2")
                .select(columnsToSelect)
                .ascOrder("col_2", "col_1")
                .getResultsAndClose(firstResult, maxResults);

# finish() method calls executeUpdate(), commit(), close()
        
        int updateCount = dao.forDelete(entityType)
                    .begin().from(entityType).where("col_0", "val_0").finish(); 

# Dao / BuilderFor may be reused if #close() has not been called. Simply call #reset() before reuse.

        try(BuilderForUpdate<E> reused = new DaoImpl(em).builderForUpdate(entityType)) {
            
            reused.begin();
            
            updateCount = reused.from(entityType).where("col_0", "val_0").set("col_0", "val_1").executeUpdate();
            
            reused.reset();
            
            updateCount = reused.from(entityType).where("col_0", "val_1").set("col_0", "val_0").executeUpdate();
            
            reused.commit();
        }
